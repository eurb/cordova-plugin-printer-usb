package com.github.eurb.usb.printer.interfaces;

import android.hardware.usb.UsbDevice;
import com.github.eurb.usb.printer.exceptions.GenericPrinterException;
import java.util.List;

/**
 * Created by E.U.R.B. on 27/10/17.
 */

public interface IGenericPrinter {

    /**
     * Scan all printers connected to a USB port
     * @return
     * @throws GenericPrinterException
     */
    List<UsbDevice> findPrinters() throws GenericPrinterException;

    /**
     * Returns the current printer to use
     * @param name
     * @return
     * @throws GenericPrinterException
     */
    UsbDevice findPrinterByName(String name) throws GenericPrinterException;


}
