package com.taoyuanx.securitydemo.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
	//判断cookie是否存在
	public static String getCookie(Cookie[] cookies, String cookieName){
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
	//删除cookie
	public static void removeCookie(HttpServletResponse response,String path, String cookieName){
		Cookie cookie=new Cookie(cookieName,null);
		cookie.setPath(path);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}



}
