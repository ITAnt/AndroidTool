package com.itant.androidtool.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式相关的工具方法
 * @author iTant
 *
 */
public class RegexTool {
	
	private RegexTool() {}
	
	private static class ToolProvider {
		private static RegexTool instance = new RegexTool();
	}
	
	public static RegexTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}

	/**
	 * 查看某字符串content里是否能找到符合regex条件的字符串
	 * @return true:表示找得到 false:表示找不到
	 */
	public static boolean find(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
	}
}
