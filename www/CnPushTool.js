var CnPushTool = function () { };

/**
 * 是否是苹果设备
 */
CnPushTool.prototype.isPlatformIOS = function () {
    return (
        device.platform === "iPhone" ||
        device.platform === "iPad" ||
        device.platform === "iPod touch" ||
        device.platform === "iOS"
    );
};

/**
 * 是否是华为设备
 */
CnPushTool.prototype.isPlatformHuawei = function () {
    return device.manufacturer.toLowerCase().indexOf("huawei") >= 0;
}

/**
 * 是否是vivo设备
 */
CnPushTool.prototype.isPlatformVivo = function () {
    return device.manufacturer.toLowerCase().indexOf("vivo") >= 0;
}

/**
 * 是否是oppo设备
 */
CnPushTool.prototype.isPlatformOppo = function () {
    return device.manufacturer.toLowerCase().indexOf("oppo") >= 0;
}

/**
 * 是否是xiaomi设备
 */
CnPushTool.prototype.isPlatformXiaomi = function () {
    return device.manufacturer.toLowerCase().indexOf("xiaomi") >= 0;
}

module.exports =new CnPushTool();
