var fs = require('fs');
var buildGradleFilePath = "platforms/android/build.gradle";
var huaweiConfigPath = "config-push/agconnect-services.json";
function updateToBuildGradle() {
    var data = fs.readFileSync(buildGradleFilePath, 'utf8');
    data = data.replace(/jcenter\(\)/ig, `jcenter()\n        //#CORDOVA-PLUGIN-CNPUSH#START \n        maven { url 'https://developer.huawei.com/repo/' } \n        //#CORDOVA-PLUGIN-CNPUSH#END\n`);
    data = data.replace(/dependencies \{/ig, `dependencies { \n        //#CORDOVA-PLUGIN-CNPUSH#START \n        classpath 'com.huawei.agconnect:agcp:1.4.0.300' \n        //#CORDOVA-PLUGIN-CNPUSH#END\n`);
    fs.writeFileSync(buildGradleFilePath, data);
}

function copyConfigFile() {
    var data = fs.readFileSync(huaweiConfigPath, 'utf8');
    fs.writeFileSync("platforms/android/app/agconnect-services.json", data);
}

console.log("*** 拷贝至 platforms/android/app/agconnect-services.json 配置文件 ***");
copyConfigFile();
console.log("*** 注入 platforms/android/build.gradle 相关的设置 ***");
updateToBuildGradle();