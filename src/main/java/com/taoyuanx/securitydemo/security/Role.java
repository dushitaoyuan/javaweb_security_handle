package com.taoyuanx.securitydemo.security;

/**
 * 简单权限控制,将账户账户状态和角色关联
 */
public enum Role {
    /**
     * 数字越小权限越大
     */
    ADMIN(0, "超级管理员"),
    COMMONUSER(1, "普通用户"),
    USER(2, "任何已登录用户"),
    PUBLIC(3, "未登录");
    private int accountStatus;
    private String desc;

    private Role(int accountStatus, String desc) {
        this.accountStatus = accountStatus;
        this.desc = desc;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public static boolean isMatch(Role[] roles, Integer accountStatus) {
        for (Role role : roles) {
            if (accountStatus <= role.getAccountStatus()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断账户是否具有某个角色
     *
     * @param requireRole
     * @param accountStatus
     * @return
     */
    public static boolean hasRole(RequireRole requireRole, Integer accountStatus) {
        if (requireRole == null) {
            return true;
        }
        Role[] roles = requireRole.role();

        if (roles != null) {
            if (accountStatus == null) {
                accountStatus = Role.PUBLIC.getAccountStatus();
            }
            return isMatch(roles, accountStatus);
        }
        return true;
    }

    public static void main(String[] args) {

        System.out.println(isMatch(new Role[]{Role.ADMIN}, Role.COMMONUSER.accountStatus));

        System.out.println(isMatch(new Role[]{Role.COMMONUSER}, Role.ADMIN.accountStatus));
        System.out.println(isMatch(new Role[]{Role.PUBLIC}, Role.PUBLIC.accountStatus));
    }
}
