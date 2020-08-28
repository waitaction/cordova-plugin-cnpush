var JPush = function () {
    var self = this;
    this.registerCallback = null;
    this.registerNewCallback = null;
    window.JPush.init();
    window.JPush.setDebugMode(true);
    window.JPush.setStatisticsOpen(true);

    document.addEventListener("jpush.receiveRegistrationId", function (event) {
        console.log("receiveRegistrationId" + JSON.stringify(event));
        console.log(event.registrationId)
        self.registerCallback(registrationId);
    }, false);

    document.addEventListener("jpush.openNotification", function (event) {
        try {
            var alertContent;
            if (device.platform == "Android") {
                alertContent = event.alert;
            } else {
                alertContent = event.aps.alert;
            }
            badgeNumb = badgeNumb - 1;
            badgeNumb = badgeNumb <= 0 ? 0 : badgeNumb;
            window.JPush.setBadgeNumber(badgeNumb);
            console.log("open Notification:" + alertContent);
            cordova.fireDocumentEvent('messageReceived', alertContent);
        } catch (exception) {
            console.log("JPushPlugin:onOpenNotification" + exception);
        }
    }, false);

    document.addEventListener("jpush.receiveNotification", function (event) {
        try {
            var alertContent;
            if (device.platform == "Android") {
                alertContent = event.alert;
            } else {
                alertContent = event.aps.alert;
            }
            console.log(alertContent);
            cordova.fireDocumentEvent('messageReceived', alertContent);
            badgeNumb = badgeNumb + 1;
            window.JPush.setBadgeNumber(badgeNumb);
        } catch (exception) {
            console.log(exception)
        }
    }, false);

    document.addEventListener("jpush.receiveMessage", function (event) {
        try {
            var message;
            if (device.platform == "Android") {
                message = event.message;
            } else {
                message = event.content;
            }
            console.log(message);
            cordova.fireDocumentEvent('messageReceived', message);
            badgeNumb = badgeNumb + 1;
            window.JPush.setBadgeNumber(badgeNumb);
        } catch (exception) {
            console.log("JPushPlugin:onReceiveMessage-->" + exception);
        }
    }, false);

    document.addEventListener("resume", function (event) {
        try {
            badgeNumb = 0
            window.JPush.setBadgeNumber(0);
        } catch (exception) {
            console.log("onResume-->" + exception);
        }
    }, false);

};

JPush.prototype.register = function (callback) {
    this.registerCallback = callback;
}

JPush.prototype.onNewToken = function (callback) {
    this.registerNewCallback = callback;
}

JPush.prototype.getRegistrationID = function () {
    let self = this;
    window.JPush.getRegistrationID(function (token) {
        try {
            console.log(token);
            if (token.length == 0) {
                window.setTimeout(self.getRegistrationID, 120000);
            } else {
                self.registerNewCallback(token);
            }
        } catch (exception) {
            console.log(exception);
        }
    });
}
module.exports = new JPush();
