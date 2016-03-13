package com.itant.androidtool.app;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 与系统相关的工具方法
 * @author Jason
 *
 */
public class SystemTool {
	
	private SystemTool() {}
	
	private static class ToolProvider {
		private static SystemTool instance = new SystemTool();
	}
	
	public static SystemTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}

	/**
	 * 判断当前手机是否有ROOT权限
	 *
	 * @return true:表示当前手机已经root false:表示当前手机尚未root
	 */
	public boolean isRoot() {
		boolean bool = false;

		try {
			if ((!new File("/system/bin/su").exists())
					&& (!new File("/system/xbin/su").exists())) {
				bool = false;
			} else {
				bool = true;
			}
		} catch (Exception e) {

		}
		return bool;
	}
	
	/**
	 * 判断是否是平板
	 * 
	 * @param context 上下文
	 * @return true:表示当前设备是平板 false:表示当前设备不是平板
	 */
	public boolean isTablet(Context context) {
		if (null == context) {
			return false;
		}
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	/**
	 * 判断SD卡是否可用
	 * 
	 * @return true:表示SD卡可用 false:表示SD卡不可用
	 */
	public boolean isSDCardEnable() {
		if (Environment.getExternalStorageDirectory().exists() &&
				TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return SD卡剩余容量
	 */
	public long getAvailableSDSize(){
	    //取得SD卡文件路径
	    File path = Environment.getExternalStorageDirectory(); 
	    StatFs sf = new StatFs(path.getPath()); 
	    //获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize(); 
	    //空闲的数据块的数量
	    long freeBlocks = sf.getAvailableBlocks();
	    //返回SD卡空闲大小(MB)
	    return (freeBlocks * blockSize) >> 20;
    }
	
	/**
	 * @return SD卡总容量(单位：Bit)
	 */
	public long getSDSize(){
		File path = Environment.getExternalStorageDirectory(); 
		StatFs stat = new StatFs(path.getPath()); 
		long blockSize = stat.getBlockSize(); 
		long availableBlocks = stat.getBlockCount();
		return availableBlocks * blockSize; 
	}
	
	/**
	 * @return 剩余磁盘空间（单位：MB）
	 */
	public long getAvailableDiskSize() {
		File root = Environment.getRootDirectory();  
		StatFs sf = new StatFs(root.getPath());  
		long blockSize = sf.getBlockSize();  
		long availCount = sf.getAvailableBlocks();  
		return (availCount * blockSize) >> 20;
	}

	/**
	 * 获取电源锁
	 * 
	 * @param context 上下文
	 * @return 电源锁
	 */
	public WakeLock acquireWakeLock(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getClass().getCanonicalName());
		if (null != wakeLock) {
			wakeLock.acquire();
		}
		return wakeLock;
	}

	/**
	 * 释放设备电源锁
	 * 
	 * @param wakeLock 电源锁
	 */
	public void releaseWakeLock(WakeLock wakeLock) {
		if (null != wakeLock) {  
			wakeLock.release();  
			wakeLock = null;  
	    }  
	}
	
	/**
	 * 清理后台程序(需要root权限)
	 * 
	 * @author iTant
	 */
	public static class CleanAsRootTask extends AsyncTask<Void, Void, Void> {
		private Context context;
		
		public CleanAsRootTask(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int thisPid = android.os.Process.myPid();
			Process process = null;
			try {
				process = Runtime.getRuntime().exec("su");
		        DataOutputStream os = new DataOutputStream(process.getOutputStream()); 
		        
		        // 获取后台进程
		        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				List<ActivityManager.RunningAppProcessInfo> mRunningProcess = mActivityManager.getRunningAppProcesses();
				
				// 获取已安装的非系统应用的UID
				List<Integer> apkUIDs = new ArrayList<Integer>();
				List<ApplicationInfo> applicationInfos = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
				for (ApplicationInfo applicationInfo : applicationInfos) {
					if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
						// 非系统应用
						apkUIDs.add(applicationInfo.uid);
					}
				}
				
				// 后台进程的进程名包含有非系统应用的包名则可以杀掉
				for (ActivityManager.RunningAppProcessInfo processInfo : mRunningProcess) {
					if (processInfo.pid != thisPid) {
						// 不是本应用
						for (Integer uid : apkUIDs) {
							// 属于非系统应用
							if (processInfo.uid == uid) {
								os.writeBytes("kill -9 " + processInfo.pid + "\n");
								os.flush();
								break;
							}
						}
					}
				}
				
				os.writeBytes( "exit\n");
	            os.flush();
				os.close();
		    } catch (Exception e) {  
		            e.printStackTrace();  
		    } finally {
		    	if (process != null) {
		    		try {
		    			process.getInputStream().close();
		    			process.getOutputStream().close();
		    			process.destroy();
		    			process = null;
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
		    	}
		    }
			
			return null;
		}
	}
	
	/**
	 * 获取手机的IMEI
	 * 
	 * @param context 上下文
	 * @return 手机的IMEI
	 */
	public String getIMEI(Context context) {
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}
}
