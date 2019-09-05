package com.taoyuanx.securitydemo.security;

/**
 * @author dushitaoyuan
 * @desc 限流类型
 * @date 2019/8/27
 */
public enum RateLimitType {
    IP(0, "IP限流"), METHOD(1, "方法名"),
    SERVICE_KEY(3, "业务自定义key"),
    GLOBAL(4,"系统全局"),
    TOTAL_COUNT(5,"总次数限制");
    private int code;
    private String desc;

    private RateLimitType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static  RateLimitType type(Integer code) {
        if (code == null) {
            return null;
        }
        switch (code) {
            case 0:
                return IP;
            case 1:
                return METHOD;
            case 3:
                return SERVICE_KEY;

            case 4:
                return GLOBAL;
            case 5:
                return TOTAL_COUNT;
        }
        return null;
    }
}
