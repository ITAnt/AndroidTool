package com.itant.androidtool.cmd;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 与命令行相关的工具方法
 * 
 * @author iTant
 *
 */
public class CmdTool {
	
	private CmdTool() {}
	
	private static class ToolProvider {
		private static CmdTool instance = new CmdTool();
	}
	
	public static CmdTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}
	
	/**
	 * 以root权限执行shell命令
	 * 
	 * @param cmd 命令行
	 */
	public void execRootShellCmd(String cmd) {

		try {
			// 申请获取root权限，这一步很重要，不然会没有作用
			Process process = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.writeBytes(cmd + "\n");
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * 执行命令行并返回结果
	 * 
	 * @param cmd 命令行
	 * @return 命令行的执行结果
	 */
	public String execShellCmd(String cmd) {
		Process process = null;
	    InputStream is = null;
	    BufferedReader reader = null;
	    String result = null;
	    try {
	           process = Runtime.getRuntime().exec(cmd);
	           process.waitFor();

	           is = process.getInputStream();
	           reader = new BufferedReader(new InputStreamReader(is));
	           String line;
	           StringBuilder lineBuilder = new StringBuilder();
	           while ((line = reader.readLine()) != null) {
	               lineBuilder.append(line);
	           }
	           result = lineBuilder.toString();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	try {
	    		is.close();
		        reader.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
	    	is = null;
	        reader = null;
	    }
	    return result;
	}
}
