package thd.bd.sms.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.lang.Thread.UncaughtExceptionHandler;

import thd.bd.sms.activity.MainActivity;

/**
 * 处理 未捕获的异常  开发中关闭  上线后打开
 * @author lerry
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private static final String TAG = "CrashHandler";

	private Context context;
	private UncaughtExceptionHandler defUncaughtExcep;
	private static CrashHandler crashHandler;
	private CrashHandler(){}
	
	public static CrashHandler newInstance(){
		if(crashHandler == null){
			synchronized (CrashHandler.class) {
				if(crashHandler == null){
					crashHandler = new CrashHandler();
				}
			}
		}
		return crashHandler;
	}
	
	public void init(Context context){
		this.context = context;
		defUncaughtExcep = Thread.currentThread().getDefaultUncaughtExceptionHandler();
		Thread.currentThread().setDefaultUncaughtExceptionHandler(this);
		
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		handleException(ex);

		Log.d(TAG, "uncaughtException: 6666666666666"+ex.toString());
		Log.d(TAG, ex.getStackTrace().toString());

		/**
		 *

		if(defUncaughtExcep != null){
			defUncaughtExcep.uncaughtException(thread, ex);
		}
		 */

		//android.os.Process.killProcess(android.os.Process.myPid());
		//System.exit(10);
		//restartApp();
		reStartApp();


	}
	
	private boolean handleException(Throwable ex){
		boolean blHanlder = false;
		if(ex == null){
			return blHanlder;
		}
		StackTraceElement[] stackTraceElements = ex.getStackTrace();
		int size = stackTraceElements.length;
//		Logger.e("Exception", ex.getMessage());
		Logger.WriteToFile("crashlog.txt","Exception");
		Logger.WriteToFile("crashlog.txt",ex.getMessage());
		for(int s = 0; s<size;s++){
			StackTraceElement element = stackTraceElements[s];
			Log.e("Exception", element.toString());
			Logger.WriteToFile("crashlog.txt",element.toString());
		}
		
		return blHanlder;
	}

	public void restartApp(){
		Intent intent = new Intent(context,MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		//结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void reStartApp(){

		Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
		PendingIntent restartIntent = PendingIntent.getActivity(
				context.getApplicationContext(), 0, intent,PendingIntent.FLAG_CANCEL_CURRENT);
		//退出程序
		AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000*2,
				restartIntent); // 1秒钟后重启应用
		android.os.Process.killProcess(android.os.Process.myPid());

	}

}
