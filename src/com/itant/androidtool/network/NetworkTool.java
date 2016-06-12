package com.itant.androidtool.network;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * 网络相关工具方法
 * @author iTant
 *
 */
public class NetworkTool {
	
	private NetworkTool() {}
	
	private static class ToolProvider {
		private static NetworkTool instance = new NetworkTool();
	}
	
	public static NetworkTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}

	/**
	 * 判断是否有网络
	 * 
	 * @param context 上下文
	 * @return true:表示有网络 false:表示没有网络
	 */
	public boolean isNetworkConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {
	            return mNetworkInfo.isAvailable();  
	        }  
	    }  
	    return false;
	}
	
	/**
	 * 判断是否已经连接上WiFi
	 * 
	 * @param context 上下文
	 * @return true:表示已连接WiFi false:表示没有连接WiFi
	 */
	public boolean isWiFiConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {
	            return (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI);  
	        }  
	    }  
	    return false;  
	}
	
	/**
	 * 判断是否连接了移动网络
	 * 
	 * @param context 上下文
	 * @return true:表示连接了移动网络 false:表示没有连接移动网络
	 */
	public boolean isMobileConnected(Context context) {  
	    if (context != null) {  
	        ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
	        if (mNetworkInfo != null) {
	            return (mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE);  
	        }  
	    }  
	    return false;  
	}
	
	/**
	 * 获取IPv4形式的IP地址
	 * @return (发送端)服务器IP地址，转换IP输出格式
	 */
	public String getSenderIP(WifiManager wifiManager) {
		int ipAddress = wifiManager.getDhcpInfo().serverAddress;
		return Formatter.formatIpAddress(ipAddress);   
	}
	
	/**
	 * 获取手机的IP地址
	 * 
	 * @return 有IP的话返回IP，否则返回"IP not found"
	 */
	public String getLocalIpAddress() {

		try {
			Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaceEnumeration.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
				Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
				while (inetAddressEnumeration.hasMoreElements()) {
					InetAddress inetAddress = inetAddressEnumeration.nextElement();
					if (!inetAddress.isLoopbackAddress() && isIPv4Address(inetAddress.getHostAddress())) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			
		}

		return "IP not found";
	}
	
	/**
	 * 判断一个字符串是否IPv4形式的IP地址
	 * 
	 * @param unknownAddress 待判定的字符串
	 * @return true:表示该字符串是IP地址 false:表示该字符串不是IP地址
	 */
	public boolean isIPv4Address(String unknownAddress) {
		String regex = "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(unknownAddress);
		while (matcher.find()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 开启热点
	 * 
	 * @param wifiManager WiFi管理者
	 * @param wifiConfiguration 热点相关配置
	 * @param enable true:表示打开热点
	 * @return 热点是否开启成功 true:表示开启成功 false:表示开启失败
	 */
	public boolean setWifiApEnabled(WifiManager wifiManager, WifiConfiguration wifiConfiguration, boolean enable) {
		boolean invokeStatus = true;
		try {
			Method setupHotSpot = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
			setupHotSpot.invoke(wifiManager, wifiConfiguration, enable);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			invokeStatus = false;
		}
		
		return invokeStatus;
	}
	
	/**
	 * 获取所连接的WiFi名称
	 * 
	 * @param wifiManager WiFi管理者
	 * @return 所连接的WiFi名称
	 */
	public String getConnectedWifiSsid(WifiManager wifiManager) {  
        return wifiManager.getConnectionInfo().getSSID();  
	}  
	
	/*****************************供接收者使用***************************************/
	// 加密类型，分为三种情况：1.没有密码	2.用WEP加密	3.用WPA加密，我们这里只用到了第3种
	public static final int TYPE_NO_PASSWD = 1;
	public static final int TYPE_WEP = 2;
	public static final int TYPE_WPA = 3;
	/**
	 * 连接信息生成配置对象
	 */
	public static WifiConfiguration createWifiInfo(String SSID, String password, int type) {
		WifiConfiguration config = new WifiConfiguration();
		config.SSID = SSID;
		// 清除热点记录clearAll(SSID);
		if (type == TYPE_NO_PASSWD) {
			config.hiddenSSID = false;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.preSharedKey = null;
		} else if (type == TYPE_WEP) {
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == TYPE_WPA) {
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = false;
			config.priority = 10000;
			config.status = WifiConfiguration.Status.ENABLED;
			
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		}

		return config;
	}
	/*****************************供接收者使用***************************************/
	
	/**
	 *
	 * @return 运营商名称
	 */
	public String getCommunicationCorporation(Context context) {
		// 移动设备网络代码（英语：Mobile Network Code，MNC）是与移动设备国家代码（Mobile Country Code，MCC）
		// （也称为“MCC / MNC”）相结合, 例如46000，前三位是MCC，后两位是MNC 获取手机服务商信息
		String corporation = "未知";
		String IMSI =  ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 运营商代码
		System.out.println(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
			corporation = "中国移动";
		} else if (IMSI.startsWith("46001") || IMSI.startsWith("46006")) {
			corporation = "中国联通";
		} else if (IMSI.startsWith("46003") || IMSI.startsWith("46005")) {
			corporation = "中国电信";
		}
		return corporation;
	}
	
	/**
	 * 获取外网IP
	 * @return
	 */
	public String getOutIP() {
		String IP = "";
		try {
			String address = "http://ip.taobao.com/service/getIpInfo2.php?ip=myip";
	
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				// 将流转化为字符串
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String tmpString = "";
				StringBuilder retJSON = new StringBuilder();
				while ((tmpString = reader.readLine()) != null) {
					retJSON.append(tmpString + "\n");
				}
	
				JSONObject jsonObject = new JSONObject(retJSON.toString());
				String code = jsonObject.getString("code");
				if (code.equals("0")) {
					JSONObject data = jsonObject.getJSONObject("data");
					IP = data.getString("ip");
				} else {
					IP = "";
					Log.e("提示", "IP接口异常，无法获取IP地址！");
				}
			} else {
				IP = "";
				Log.e("提示", "网络连接异常，无法获取IP地址！");
			}
		} catch (Exception e) {
			IP = "";
			Log.e("提示", "获取IP地址时出现异常，异常信息是：" + e.toString());
		}
		return IP;
	}
/**
     * 发送GET请求
     * @param urlAddress 请求的网址
     * @param requestParams 请求参数
     * @return
     */
    public static String submitGetRequest(String urlAddress, RequestParams requestParams) {
        String result = null;
        HttpURLConnection connection = null;
        URL url = null;

        try {
            if (requestParams != null) {
                // 请求参数
                Map<String, String> params = requestParams.getParams();
                int paramSize = params.size();
                int index = 1;
                StringBuilder paramsBuilder = new StringBuilder();
                paramsBuilder.append("?");
                for (String key : params.keySet()) {
                    paramsBuilder.append(key).append("=").append(URLEncoder.encode(params.get(key), "UTF-8"));
                    if (index < paramSize) {
                        paramsBuilder.append("&");
                        index++;
                    }
                }
                url = new URL(urlAddress + paramsBuilder.toString());
            } else {
                url = new URL(urlAddress);
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-type", "text/html");
            // 不要用cache，用了也没有什么用，因为我们不会经常对一个链接频繁访问。（针对程序）
            connection.setUseCaches(false);
            connection.setConnectTimeout(6 * 1000);
            connection.setReadTimeout(6 * 1000);
            connection.setRequestProperty("Charset", "utf-8");

            connection.connect();

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                InputStream inputStream  = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                result = builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * 发送POST请求
     * @return
     */
    public static String submitPostRequest(String urlAddress, RequestParams requestParams) {
        String result = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlAddress);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            //connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)");
            connection.setRequestProperty("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*");
            connection.setRequestProperty("Accept-Language", "zh-cn");
            //connection.setRequestProperty("UA-CPU", "x86");
            //connection.setRequestProperty("Accept-Encoding", "gzip");
            // 很重要
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setConnectTimeout(6 * 1000);
            connection.setReadTimeout(6 * 1000);
            // 发送POST请求必须设置这两项
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Charset", "utf-8");

            connection.connect();

            if (requestParams != null) {
                // 请求参数
                Map<String, String> params = requestParams.getParams();
                int paramSize = params.size();
                int index = 1;
                StringBuilder paramsBuilder = new StringBuilder();
                for (String key : params.keySet()) {
                    paramsBuilder.append(key).append("=").append(params.get(key));
                    if (index < paramSize) {
                        paramsBuilder.append("&");
                        index++;
                    }
                }

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(paramsBuilder.toString());
                writer.flush();
                writer.close();
            }

            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                InputStream inputStream = null;
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                result = builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

}
