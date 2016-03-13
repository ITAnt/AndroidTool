package com.itant.androidtool.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 屏幕相关的工具方法
 * @author iTant
 *
 */
public class ScreenTool {
	
	private ScreenTool() {}
	
	private static class ToolProvider {
		private static ScreenTool instance = new ScreenTool();
	}
	
	public static ScreenTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}

	/**
	 * 返回屏幕分辨率
	 * 
	 * @param activity 当前Activity
	 * @return 屏幕分辨率
	 */
	public DisplayMetrics getScreenMetrix(Activity activity) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics;
	}
	
	/**
	 * dp转px
	 * @param context 上下文
	 * @param dp dp值
	 * @return px值
	 */
	public int dpToPx(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	
	/**
	 * dip转换成px
	 * 
	 * @param context 上下文
	 * @param dipValue dp值
	 * @return px值
	 */
	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转换成dip
	 * 
	 * @param context 上下文
	 * @param pxValue px值
	 * @return dp值
	 */
	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
     * 
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
         return Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }
    
    /**
     * 获取xml文件里定义的大小的像素值
     * 
     * @param context 上下文
     * @param dimenId xml文件里定义的高度的id
     * @return 对应的像素值
     */
    public int getLen(Context context, int dimenId) {
    	return context.getApplicationContext().getResources().getDimensionPixelSize(dimenId);
    }
}
