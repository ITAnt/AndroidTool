package com.itant.androidtool.string;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 汉字转拼音的工具方法
 * @author iTant
 *
 */
public class HanziTool {
	
	private HanziTool() {
		format = new HanyuPinyinOutputFormat();
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		pinyin = null;
	}
	
	private static class ToolProvider {
		private static HanziTool instance = new HanziTool();
	}
	
	public static HanziTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}
	
	private HanyuPinyinOutputFormat format = null;

	private String[] pinyin;

	// 转换单个字符
	private String getCharacterPinYin(char c) {
		try {

			pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);

		}
		catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}

		// 如果c不是汉字，toHanyuPinyinStringArray会返回null
		if (pinyin == null) {
			if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
				return String.valueOf(c);
			}
			
			return null;
		}
		// 只取一个发音，如果是多音字，仅取第一个发音
		return pinyin[0];
	}

	/**
	 * 将汉字转换为拼音
	 * 
	 * @param hanzi 要转换为拼音的汉字字符串
	 * @return 汉字字符串的拼音
	 */
	public String getPinYin(String hanzi) {
		StringBuilder sb = new StringBuilder();
		String tempPinyin = null;
		for (int i = 0; i < hanzi.length(); ++i) {
			tempPinyin = getCharacterPinYin(hanzi.charAt(i));
			if (tempPinyin == null) {
				// 如果str.charAt(i)非汉字，且不是数字，则不予理会
				char character = hanzi.charAt(i);
				if (character >= '0' && character <= '9') {
					sb.append(character);
				}
			} else {
				sb.append(tempPinyin);
			}
		}
		return sb.toString();
	}
}
