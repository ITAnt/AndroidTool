package com.itant.androidtool.string;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;

/**
 * 日期相关工具方法
 * @author iTant
 *
 */
public class DateTool {

	private static final String FORMAT_CURRENT_DAY = "yyyyMMdd";
	
	private DateTool() {}
	
	private static class ToolProvider {
		private static DateTool instance = new DateTool();
	}
	
	public static DateTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}
	
	/**
	 * @return 精确到天的日期(如20140506，表示2014年5月6日)
	 */
	public String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_CURRENT_DAY, Locale.getDefault());
		return sdf.format(new Date());
	}
	
	/**
	 * 返回特定格式的时间
	 * 
	 * @param timeFormat 表示时间的格式，如"yyyy-MM-dd HH:mm:ss"
	 * @return 精确到天的日期(如20140506，表示2014年5月6日)
	 */
	public String getFormattedTime(String timeFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
		return sdf.format(new Date());
	}
	
	/**
	 * 判断当前系统时间是否是24小时制
	 * 
	 * @param context 上下文
	 * @return true:表示当前系统为24小时制 false:表示当前系统不是24小时制
	 */
	public boolean is24Hours(Context context) {
		try {
			ContentResolver cv = context.getContentResolver();
			String strTimeFormat = android.provider.Settings.System.getString(
					cv, android.provider.Settings.System.TIME_12_24);
			if (strTimeFormat.equals("24")) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * 生成一个 uuid
	 * 
	 */
	public String generateUUID() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return dateFormat.format(new Date());
	}
	
	/**
	* 时间戳转日期
	* @param timeMillis System.currentTimeMillis()
	* @return 时间
	*/
	public static String getDateFromTimeMillis(long timeMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String time = sdf.format(new Date(timeMillis));
		return time;
	}	
	
	/**
     * 自定义格式时间戳转换
     * 
     * @param beginDate
     * @return
     */
    public static String timestampToDate(String beginDate,String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String sd = sdf.format(new Date(Long.parseLong(beginDate)));
        return sd;
    }

    /**
     * 将字符串转为时间戳
     * 
     * @param user_time
     * @return
     */
    public static String dateToTimestamp(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }
}
