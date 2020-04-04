# web 安全常见漏洞

## 常见漏洞及解决方案

1. sql 注入   

解决方案:参数检测,拦截非法入参,后端使用druid 连接池的sql防火墙
参见:com.taoyuanx.securitydemo.web

2. xss攻击  

解决方案:参数检测,拦截非法入参,转义html字符,参见com.taoyuanx.securitydemo.web



3.  跨站请求  

解决方案:设置允许跨站的header信息

4. 文件,图片等非站内请求  

解决方案:防盗链处理(Http Rerfer头部,nginx可设置)

5. 接口暴力访问  

解决方案:系统限流,全局限流,单个ip限流,黑名单等,参见:com.taoyuanx.securitydemo.security.RateLimitAspect,com.taoyuanx.securitydemo.web.BlackListFilter

7. 文件未授权访问  

解决方案:上传的文件,展示时后端返回签名的文件,访问时,走一次后端,方便做权限验证,参见,/api/upload,/api/file
文件存储时,去除可执行权限,尽量和应用服务器进行物理隔离

8. 文件不安全类型上传  

解决方案:校验文件类型,校验流信息,校验文件真实类型,参见/api/upload

9. 密码泄露风险  

密码加密传输,参见login.html

10. 越权访问

解决方案:权限控制,参见SimpleAuthHandlerIntercepter
如果通过tomcat发布静态文件,可通过过滤器禁止非授权访问,如采用其他静态资源服务器,可严格控制后台权限,保证数据不被越权访问
,静态资源越权访问,可通过client端js控制location

11. 中间人攻击

解决方案:采用https协议,参数签名,返回值签名,防止参数或返回值被篡改

12. slowhttp 攻击

解决方案:限制恶意访问,有钱的买防护工具,(阿里云盾,知道创宇等),没钱的多部署几台机器,修改连接超时时间,事后分析ip,封禁
协议漏洞,没啥好防护的策略,没钱的事后防范吧



## esapi 介绍

此jar包为一个比较全面的安全库,控制较为全面,业务较为复杂的可自行扩展  
基本使用例子:com.taoyuanx.securitydemo.EsapiTest  