# cordova-plugin-printer-usb
A Cordova/Phonegap plugin which one can print a message on the selected usb printer for Android.

# Usage

You can find a printer using:
```
window.cordova.plugins.generic.printer.usb.scan(
    function(result) {
        if (typeof result == 'string') {
            printer = JSON.stringify(result);
        } else {
            printer = JSON.stringify(result);
        }
        alert('Printer: connect success: ' + printer);
    },

    function(error) {
        alert('Printer: connect fail: ' + error);
    }
);
```

You can send data in the selected printer:

```
window.cordova.plugins.generic.printer.usb.print(printer, data,
    function(success) {
        alert("print success: " + success);
    },
    function(fail) {
        alert("print fail:" + fail);
        deferred.reject(fail);
    }
);
```

You can send a Test in the selected printer:

```
window.cordova.plugins.generic.printer.usb.test(printer,
    function(success) {
        alert("print success: " + success);
    },
    function(fail) {
        alert("print fail:" + fail);
        deferred.reject(fail);
    }
);

```


## Install Cordova

```
cordova plugin add cordova-plugin-printer-usbr
```
