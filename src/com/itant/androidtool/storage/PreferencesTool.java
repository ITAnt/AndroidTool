package com.itant.androidtool.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Preferences相关工具方法
 * @author iTant
 *
 */
public class PreferencesTool {
	private SharedPreferences mPreferences;
	private Editor mEditor;
	private static PreferencesTool mInstance;

	private PreferencesTool(Context context) {
		mPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	private static synchronized void initInstance(Context context) {
		if (mInstance == null) {
			mInstance = new PreferencesTool(context);
		}
	}

	public static PreferencesTool getInstance(Context context) {
		if (mInstance == null) {
			initInstance(context);
		}
		return mInstance;
	}

	/**
	 * putString 存放String类型键值对
	 * 
	 * @param key 键
	 * @param value 值
	 */
	public void putString(String key, String value) {
		mEditor.putString(key, value);
		commit();
	}

	/**
	 * 获取String类型的值
	 * 
	 * @param key 键
	 */
	public String getString(String key) {
		return mPreferences.getString(key, null);
	}

	private void commit() {
		mEditor.commit();
	}
}
