var exec = require('cordova/exec');

exports.print = function (printerName, textMessage, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GenericPrinterUSB", "print", [printerName, textMessage]);
};

exports.scan = function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GenericPrinterUSB", "scan", []);
};

exports.test = function (printerName, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GenericPrinterUSB", "test", [printerName]);
};
