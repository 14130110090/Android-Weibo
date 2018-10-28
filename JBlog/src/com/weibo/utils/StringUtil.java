package com.weibo.utils;

public class StringUtil {
	/**
	 * ��������Ƿ�Ϊ�գ����Ƿ�Ϊnull��ֻ����"",' ','\t','\r','\n'
	 * @param input
	 * @return
	 */
	public static boolean isBlank(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}
}
