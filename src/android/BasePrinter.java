package com.github.eurb.usb.printer.base;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.github.eurb.usb.printer.exceptions.GenericPrinterException;

/**
 * Created by E.U.R.B. on 30/10/17.
 */

public abstract class BasePrinter {

    protected UsbManager usbManager;
    protected UsbDeviceConnection usbDeviceConnection;
    protected UsbInterface usbInterface;
    protected UsbEndpoint usbEndpoint;

    /**
     *
     * @param usbManager
     */
    public BasePrinter(UsbManager usbManager) {
        this.usbManager = usbManager;
    }

    /**
     * Open the USB ports where the printer is connected
     * @throws GenericPrinterException
     */
    public abstract void open(UsbDevice printerDevice) throws GenericPrinterException;

    /**
     * Prints the message sent as a parameter
     * @param message
     * @throws GenericPrinterException
     */
    public abstract  void print(String message) throws GenericPrinterException;

    /**
     * Close used resources and open connections
     * @throws GenericPrinterException
     */
    public abstract void close() throws GenericPrinterException;



}
