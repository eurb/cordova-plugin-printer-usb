package com.github.eurb.usb.printer;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.github.eurb.usb.printer.base.BasePrinter;
import com.github.eurb.usb.printer.exceptions.GenericPrinterException;
import com.github.eurb.usb.printer.impl.Printer;
import com.github.eurb.usb.printer.interfaces.IGenericPrinter;
import com.github.eurb.usb.printer.interfaces.IVariables;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by E.U.R.B. on 30/10/17.
 * This is what the plugin really does
 */

public class GenericPrinterUSB extends CordovaPlugin {

    private final String TAG = GenericPrinterUSB.class.getSimpleName();


    private UsbManager usbManager; //USB device manager
    private UsbDevice usbDevice;//Represents the current printer
    private PendingIntent pendingIntent;
    private String printMessageRequest, messageResponse, printerName;
    private IGenericPrinter iGenericPrinter; //Represents the implementation of each printer
    private BasePrinter basePrinter; //Represents the implementation of each printer

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext)
            throws JSONException {
        usbManager = (UsbManager) cordova.getActivity().getSystemService(Context.USB_SERVICE);
        basePrinter = new Printer(usbManager);
        if(IVariables.SCAN_ACTION.equals(action)){
            findPrinters(callbackContext);
            return true;
        }
        else if (IVariables.PRINT_ACTION.equals(action)) {
            Log.i(TAG, "The printing process starts...");
            printerName=data.getString(0);
            printMessageRequest = data.getString(1);
            try {
                if(printMessageRequest==null || IVariables.EMPTY.equals(printMessageRequest))
                    throw new Exception("Wrong message.");
                print(callbackContext);
                messageResponse = "It was printed correctly...";
                Log.i(TAG, messageResponse);
                callbackContext.success(messageResponse);
                return true;
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
                e.printStackTrace();
            }
        }

        else if (IVariables.PRINT_TEST_ACTION.equals(action)) {
            Log.i(TAG, "The printing process starts...");
            printerName=data.getString(0);
            try {
                printMessageRequest=IVariables.TEST_MESSAGE;
                if(printMessageRequest==null || IVariables.EMPTY.equals(printMessageRequest))
                    throw new Exception("Wrong message.");
                print(callbackContext);
                messageResponse = "It was printed correctly...";
                Log.i(TAG, messageResponse);
                callbackContext.success(messageResponse);
                return true;
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void print(final CallbackContext callbackContext)  {
        try {
            Log.i(TAG, "It tries to detect the printer connected to some USB port.");
            iGenericPrinter = (IGenericPrinter) basePrinter;
            usbDevice=iGenericPrinter.findPrinterByName(printerName);
            if(usbDevice==null)
                throw new Exception("There are no printers connected");
            pendingIntent = PendingIntent.getBroadcast(cordova.getActivity().getBaseContext(), 0,
                    new Intent(IVariables.USB_PERMISSION), 0);
            requestPermission(callbackContext);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }


    @SuppressLint("NewApi")
    public void findPrinters(final CallbackContext callbackContext){
        Log.i(TAG, "It tries to detect the printer connected to some usb port");
        iGenericPrinter = (IGenericPrinter) basePrinter;
        JSONArray printers = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            List<UsbDevice> lstPrinters= iGenericPrinter.findPrinters();
            for(UsbDevice usbDevice : lstPrinters){
                if(UsbConstants.USB_CLASS_PRINTER == usbDevice.getInterface(0).getInterfaceClass()) {
                    JSONObject printerObj = new JSONObject();
                    printerObj.put("productName", usbDevice.getProductName());
                    printerObj.put("manufacturerName", usbDevice.getManufacturerName());
                    printerObj.put("deviceId", usbDevice.getDeviceId());
                    printerObj.put("serialNumber", usbDevice.getSerialNumber());
                    printerObj.put("vendorId", usbDevice.getVendorId());
                    printers.put(printerObj);
                }
            }
            if(printers.length() <=0)
                throw new GenericPrinterException("No printers connected.");
            jsonObj.put("printers", printers);
            callbackContext.success(printers);

        }catch (JSONException e) {
            e.printStackTrace();
        } catch (GenericPrinterException e) {
            Log.e(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }
    }

    /**
     * It is necessary to grant permits to the usb port, so permits are requested
     * @param callbackContext
     * @throws Exception
     */
    @SuppressLint("NewApi")
    void requestPermission(final CallbackContext callbackContext)   {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IntentFilter filter = new IntentFilter(
                            IVariables.USB_PERMISSION);
                    cordova.getActivity().registerReceiver(broadcastReceiver, filter);
                    if (usbDevice != null) {
                        usbManager.requestPermission(usbDevice,
                                pendingIntent);
                    } else {
                        Log.e(TAG, "The printer was not found");
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    callbackContext.error(e.getMessage());
                }
            }
        }).start();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                Log.i(TAG, "The Broadcast object starts");
                if (IVariables.USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        final UsbDevice printerDevice = (UsbDevice) intent
                                .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(
                                UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (printerDevice != null) {
                                Log.i("Info", "Permits are granted.");
                                print(printerDevice);
                            }
                        } else {
                            Log.i(TAG, "Permission denied to the printing object.");
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

    };

    /**
     * The process of sending bytes to the selected printer starts.
     * @param printerDevice
     * @throws GenericPrinterException
     */
    void print(final UsbDevice printerDevice) throws GenericPrinterException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "We try to open the connection to the USB port.");
                    basePrinter.open(printerDevice);
                    Log.i(TAG, "The connection to the USB port was opened successfully.");

                    Log.i(TAG, "We try to send the message to the printer.");
                    basePrinter.print(printMessageRequest);
                    Log.i(TAG, "It was printed with Success.");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                } finally {
                    try {
                        Log.i(TAG, "close connections");
                        basePrinter.close();
                        cordova.getActivity().unregisterReceiver(broadcastReceiver);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
