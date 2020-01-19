//全局定义变量和全局函数

var apiBaseUrl = "http://localhost:9999/"
var staticBaseUrl = "http://localhost:9999/"

function getApiUrl(path) {
    return apiBaseUrl + path;
}

function getStaticUrl(path) {
    return staticBaseUrl + path;
}

$(document).ready(function () {

    hookAjax({
        //拦截回调
        onreadystatechange: function (xhr) {
            try {
                //权限异常,跳转到登录
                if (xhr.status == 401) {
                    window.location.href = "/";
                }
                //限流异常
                if (xhr.status == 429) {
                    var result = JSON.parse(xhr.responseText);
                    alert(result.msg);
                }
            } catch (e) {
                console.log("error")
            }
        }
    });


});

/**
 * 加密密码
 */
function encodePassword(password) {
    return CryptoJS.SHA256(password).toString(CryptoJS.enc.Base64);
}