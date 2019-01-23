package com.faye.util;

public class FayeUtils {

	public static String getUserName(String name) {
		if(name.length() >= 6) {
			return name.substring(4, name.length());
		}
		return name;
	}
	
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}
	
	public static String getUserName(String gonghao,String name) {
		return gonghao.replace("@tfkj", "")+name;
	}
	
	public static void main(String[] args) {
	}
	
}
