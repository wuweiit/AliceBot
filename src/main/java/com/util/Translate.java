package com.util;

import java.util.regex.Matcher;

/**
 * 当遇到非字母数字的时候，就在该字符之间加上空格。
 * 
 * @author xiaolong
 * 
 */
public class Translate {


	static java.util.regex.Pattern p = java.util.regex.Pattern
			.compile("[A-Za-z0-9\\s]");


	public static String translateString(String input) {
		StringBuffer newStr = new StringBuffer("");
		String strTemp = "";
		char[] chars = new char[1];
		Matcher m;
		for (int i = 0; i < input.length(); i++) {
			chars[0] = input.charAt(i);
			m = p.matcher(new String(chars));
			if (!m.matches())// 中文
				newStr.append(" ").append(input.charAt(i)).append(" ");
			else// 英文数字
				newStr.append(input.charAt(i));
		}
		// java.lang.System.out.println("#" + newStr.toString());
		strTemp = newStr.toString().replaceAll("\\s{2,}", " ");// 把2个连续的空格替换成一个空格。
		strTemp = strTemp.replaceAll("^\\s*|\\s*$", ""); // 把头和尾的空格去掉。
		return strTemp;
	}
}
