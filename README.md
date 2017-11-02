# Generic-Printer-Plugin-USB
A Cordova/Phonegap plugin which one can print a message on the selected usb printer.

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

