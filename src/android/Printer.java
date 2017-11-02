package com.github.eurb.usb.printer.impl;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Message;
import android.util.Log;

import com.github.eurb.usb.printer.base.BasePrinter;
import com.github.eurb.usb.printer.exceptions.GenericPrinterException;
import com.github.eurb.usb.printer.interfaces.IGenericPrinter;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by E.U.R.B on 30/10/17.
 * This is the main core of the application
 */

public class Printer extends BasePrinter implements IGenericPrinter {
    public Printer(UsbManager usbManager) {
        super(usbManager);
    }

    private final String TAG = Printer.class.getSimpleName();

    @SuppressLint("NewApi")
    @Override
    public void open(final UsbDevice printerDevice) throws GenericPrinterException {

        try {
            //We get the first Device Interface
            usbInterface = printerDevice.getInterface(0);
            //The endPoint will be the second one as that is where the communication really is
            usbEndpoint = usbInterface.getEndpoint(1);
            //Open the USB port of the printer
            usbDeviceConnection = usbManager.openDevice(printerDevice);
            //We enforce the interface for claiming
            usbDeviceConnection.claimInterface(usbInterface, true);
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void print(final String message) throws GenericPrinterException {
        try {
            byte[] byteArray = message.getBytes();
            ByteBuffer outputBuffer = ByteBuffer.allocate(byteArray.length);
            UsbRequest usbRequest = new UsbRequest();
            usbRequest.initialize(usbDeviceConnection, usbEndpoint);
            usbRequest.queue(outputBuffer, byteArray.length);
            if (usbDeviceConnection.requestWait() == usbRequest) {
                Log.i(TAG, outputBuffer.getChar(0) + "");
                Message msg = new Message();
                msg.obj = outputBuffer.array();
                outputBuffer.clear();
            } else {
                Log.i(TAG, "We have no messages received.");
            }
            int transfered = usbDeviceConnection.bulkTransfer(usbEndpoint,
                    message.getBytes(Charset.forName("UTF-8")),
                    message.getBytes().length, 5000);
            Log.i(TAG, "message sent with transfer : " +
                    transfered);
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void close() throws GenericPrinterException {
        try {
            usbDeviceConnection.releaseInterface(usbInterface);
            Log.i("Info", "Interface released");
            usbDeviceConnection.close();
            Log.i("Info", "Usb connection closed");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public List<UsbDevice> findPrinters() throws GenericPrinterException {
        Log.i(TAG, String.format("Found: %s Devices ",
                usbManager.getDeviceList().size()));
        try{
            if(usbManager.getDeviceList().size()<=0)
                throw new Exception("No connected printers.");

            return new ArrayList(usbManager.getDeviceList().values());
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    @Override
    public UsbDevice findPrinterByName(final String name) throws GenericPrinterException {
        UsbDevice printerDevice=null;
        try {
            Log.i(TAG, String.format("Found: %s Devices ",
                    usbManager.getDeviceList().size()));
            for (UsbDevice device : usbManager.getDeviceList().values()) {
                Log.i(TAG, String.format("Device Name: %s", device.getProductName()));
                if (name.equals(device.getProductName())) {
                    printerDevice = device;
                    break;
                }
            }
            return printerDevice;
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
            throw  new GenericPrinterException(e.getMessage());
        }
    }
}
