package com.itant.androidtool;

import android.content.Context;

import com.itant.androidtool.app.AppTool;
import com.itant.androidtool.app.SystemTool;
import com.itant.androidtool.cmd.CmdTool;
import com.itant.androidtool.network.NetworkTool;
import com.itant.androidtool.storage.FileTool;
import com.itant.androidtool.storage.PreferencesTool;
import com.itant.androidtool.string.DateTool;
import com.itant.androidtool.string.HanziTool;
import com.itant.androidtool.string.JsonTool;
import com.itant.androidtool.string.RegexTool;
import com.itant.androidtool.ui.BitmapTool;
import com.itant.androidtool.ui.ScreenTool;

/**
 * 各种工具的工厂类，可以获取各种工具类的实例
 * @author iTant
 * 
 */
public class ToolFactory {

	private ToolFactory() {}
	
	private static class ToolProvider {
		private static ToolFactory instance = new ToolFactory();
	}
	
	public static ToolFactory getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}
	
	/**
	 * 
	 * @return 与应用相关的工具
	 */
	public AppTool produceAppTool() {
		return AppTool.getInstance();
	}
	
	/**
	 * @return 系统相关工具
	 */
	public SystemTool produceSystemTool() {
		return SystemTool.getInstance();
	}
	
	/**
	 * 
	 * @return 与命令行相关的工具
	 */
	public CmdTool produceCmdTool() {
		return CmdTool.getInstance();
	}
	
	/**
	 * 
	 * @return 网络相关工具
	 */
	public NetworkTool produceNetworkTool() {
		return NetworkTool.getInstance();
	}
	
	/**
	 * 
	 * @return 文件相关工具
	 */
	public FileTool produceFileTool() {
		return FileTool.getInstance();
	}
	
	/**
	 * 
	 * @param context 上下文
	 * @return SharedPreferences相关工具
	 */
	public PreferencesTool producePreferencesTool(Context context) {
		return PreferencesTool.getInstance(context);
	}
	
	/**
	 * 
	 * @return 日期相关工具
	 */
	public DateTool produceDateTool() {
		return DateTool.getInstance();
	}
	
	/**
	 * 
	 * @return 拼音相关工具
	 */
	public HanziTool produceHanzi2Pinyin() {
		return HanziTool.getInstance();
	}
	
	/**
	 * 
	 * @return Json相关工具
	 */
	public JsonTool produceJsonHelper() {
		return JsonTool.getInstance();
	}
	
	/**
	 * 
	 * @return 正则相关工具
	 */
	public RegexTool produceRegexTool() {
		return RegexTool.getInstance();
	}
	
	/**
	 * 
	 * @return 位图相关工具
	 */
	public BitmapTool produceBitmapTool() {
		return BitmapTool.getInstance();
	}
	
	/**
	 * 
	 * @return 屏幕相关工具
	 */
	public ScreenTool produceScreenTool() {
		return ScreenTool.getInstance();
	}
}
