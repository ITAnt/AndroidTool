package com.itant.androidtool.app;

import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 与应用相关的工具方法
 * @author iTant
 *
 */
public class AppTool {
	
	private AppTool() {}
	
	private static class ToolProvider {
		private static AppTool instance = new AppTool();
	}
	
	public static AppTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}

	/**
	 * 判断当前应用是否获取了root权限
	 * 
	 * @return true:表示当前应用已经获取了root权限	false:表示当前应用没有被授予root权限
	 */
	public boolean isCurrentAppRootAuth() {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获得属于桌面的应用的应用包名称
	 * 
	 * @param context 上下文环境
	 * @return 返回包含所有包名的字符串列表
	 */
	public List<String> getHomePackages(Context context) {
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = context.getPackageManager();
		//属性
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for(ResolveInfo ri : resolveInfo){
			names.add(ri.activityInfo.packageName);
			System.out.println(ri.activityInfo.packageName);
		}
		
		return names;
	}
	
	/**
	 * 当前栈顶的Activity名称
	 * 
	 * @param context 上下文环境
	 */
	public String getCurrentActivity(Context context) {
		ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        String currentRunningActivityName = taskInfo.get(0).topActivity.getClassName();
        return currentRunningActivityName;
	}
	
	/**
	 * 获取当前正在运行的应用的包名
	 * 
	 * @param context 上下文环境
	 */
	public String getCurrentPackageName(Context context) {
		ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        String currentRunningActivityName = taskInfo.get(0).topActivity.getPackageName();
        return currentRunningActivityName;
	}
	
	/**
	 * 判断某一个Service是否在运行
	 * 
	 * @param context 上下文环境
	 * @param serviceName Service的全类名（包括包名，如"com.itant.service.DemoService"）
	 * @return true:表示该Service正在运行	false:表示该Service没有在运行
	 */
	public boolean isServiceRunning(Context context, String serviceName) {
		if (TextUtils.isEmpty(serviceName)) {
			return false;
		} else {
			ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
	        for (int i = 0; i < runningService.size(); i++) {
	            if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
	                return true;
	            }
	        }
	        return false;
		}
    }
	
	/**
	 * 获取当前系统已安装的某个APP的版本名称
	 * 
	 * @param mContext 上下文环境
	 * @param packageName 应用的包名
	 */
	public String getVersionName(Context mContext, String packageName) {
		PackageManager packageManager = mContext.getPackageManager();
		PackageInfo packInfo;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(packageName, 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	
	/**
	 * 判断Intent是否有效
	 * 
	 * @param context 上下文
	 * @return true:表示该Intent有效 false:表示该Intent无效
	 */
	public boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list != null && list.size() > 0;
	}
	
	/**
	 * 检查是否支持拨号功能
	 * 
	 * @param context 上下文
	 * @return true:表示支持拨号 false:表示不支持拨号
	 */
	public boolean isSupportCallPhone(Context context) {
		TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
			return true;
		}
		return false;
	}
	
	/**
	 * 在你的activity界面启动一个系统已经安装了的应用
	 * 
	 * @param Activity 你当前所在的Activity
	 * @param packageName 要启动的Activity的包名
	 */
	public void startAppFromActivity(Activity activity, String packageName) {
		Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
		activity.startActivity(intent);
	}
	
	/**
	 * 在你的Service启动一个系统已经安装了的应用
	 * 
	 * @param context 上下文
	 * @param packageName 要启动的Activity的包名
	 */
	public void startAppFromService(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	/**
	 * 拨打电话
	 * 
	 * @param activity 当前所在的Activity
	 * @param phoneNumber 要拨打的手机号（11位数字）
	 */
	public void callPhone(Activity activity, String phoneNumber) {
		Intent intent=new Intent(Intent.ACTION_DIAL);  
        intent.setData(Uri.parse("tel:" + phoneNumber));  
        activity.startActivity(intent);  
	}
	
	/**
	 * 判断APK是否已经安装
	 * 
	 * @param context 上下文
	 * @param packageName 目标应用包名
	 * @return true:表示当前系统已经安装该应用 false:表示当前系统尚未安装该应用
	 */
	public boolean isApkInstalled(Context context, String packageName) {
		try {
			context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			return false;
		}
	}
	

	/**
	 * 分享文件
	 * 
	 * @param activity 当前Activity
	 * @param file 要分享的文件
	 */
	public void shareFile(Activity activity, File file) {
		if (file != null && file.exists()) {
			Intent share = new Intent(Intent.ACTION_SEND);   
			share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			share.setType("*/*");//此处可发送多种文件
			activity.startActivity(Intent.createChooser(share, "文件分享"));
		}
	}
	
	/**
	 * 根据图片名称获取图片的资源id
	 * 
	 * @param imageName 图片名(不包括后缀名)
	 * @return 图片资源ID
	 */
	public int getResourceId(Activity activity, String imageName) {
		Context context = activity.getBaseContext();
		int resId = activity.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
		return resId;
	}
	
	/**
	 * 用反射方式根据图片名称获取资源id
	 * 
	 * @param field 获取方法：Field field = R.drawable.class.getField(name);
	 * @param name 图片名称(不包括后缀名)
	 * @return 图片资源ID
	 */
	public int getResourceId(Field field, String name) {
	    try {
	        return Integer.parseInt(field.get(null).toString());
	    } catch (Exception e) {}
	    return 0;
	}
	
	
/**
     * 安装一个app
     *
     * @param context
     * @param filePath 需要安装的文件路径
     * @return
     */
    public boolean install(Context context, String filePath) {
        boolean installSuccess = true;
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            context.startActivity(i);
        } catch (Exception e) {
            installSuccess = false;
        }
        return installSuccess;
    }

    /**
     * 根据包名判断是否安装
     * @param context
     * @param packageName
     * @return
     */
    public boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据包名启动apk
     *
     * @param packagename
     */
    public void openAppByPackageName(Context context, String packagename) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }
}
