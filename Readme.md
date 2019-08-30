#web 安全常见漏洞

1. sql 注入 </br>

解决方案:参数检测,拦截非法入参,后端使用druid 连接池的sql防火墙
参见:com.taoyuanx.securitydemo.web

2.xss攻击 </br>

解决方案:参数检测,拦截非法入参,转义html字符,参见com.taoyuanx.securitydemo.web

</br>

3.跨站请求
解决方案:设置允许跨站的header信息

4.文件,图片等非站内请求

解决方案:防盗链处理(Http Rerfer头部,nginx可设置)

5.接口暴力访问

解决方案:系统限流,全局限流,单个ip限流,黑名单等,参见:com.taoyuanx.securitydemo.security.RateLimitAspect,com.taoyuanx.securitydemo.web.BlackListFilter

7.文件未授权访问

解决方案:上传的文件,展示时后端返回签名的文件,访问时,走一次后端,方便做权限验证


8.文件不安全类型上传

解决方案:校验文件类型,校验流信息,校验文件真实类型,参见/api/upload

9密码泄露风险

密码加密传输,参见login.html

