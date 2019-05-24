package thd.bd.sms.utils;


import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * 日志功能
 * @author lerry
 */
public class Logger {
	
	/**
	 * 是否打印日志
	 */
	private static boolean IS_PRINT=true;
	
	
	public static void  i(String tag, String msg){
		if(IS_PRINT){
		    Log.i(tag, msg);
		}
	}
	
	public static void e(String tag, String msg){
		if(IS_PRINT){
			Log.e(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if(IS_PRINT){
			Log.d(tag, msg);
		}
	}
	
	
	public static void v(String tag, String msg){
		if(IS_PRINT){
			Log.v(tag, msg);
		}
	}
 
	public static void w(String tag, String msg){
		if(IS_PRINT){
			Log.w(tag, msg);
		}
	}
	public static void WriteToFile(String fileName, String info){
		if(IS_PRINT) {
			String systime = DateUtils.getDateTime(System.currentTimeMillis());
			String data = systime + "  " + info + "\r\n";
			String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/log510/";
			try {
				File file = new File(filePath);
				if (file != null && !file.exists()) {
					file.mkdirs();
					file = null;
				}
				FileUtils.wirteDateTofile(filePath + fileName, data);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
