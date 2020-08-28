var Tool = function () { };

/**
 * 是否是苹果设备
 */
Tool.prototype.isPlatformIOS = function () {
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
Tool.prototype.isPlatformHuawei = function () {
    return device.manufacturer.toLowerCase().indexOf("huawei") >= 0;
}

/**
 * 是否是vivo设备
 */
Tool.prototype.isPlatformVivo = function () {
    return device.manufacturer.toLowerCase().indexOf("vivo") >= 0;
}

/**
 * 是否是oppo设备
 */
Tool.prototype.isPlatformOppo = function () {
    return device.manufacturer.toLowerCase().indexOf("oppo") >= 0;
}

/**
 * 是否是xiaomi设备
 */
Tool.prototype.isPlatformXiaomi = function () {
    return device.manufacturer.toLowerCase().indexOf("xiaomi") >= 0;
}

module.exports = new Tool();
