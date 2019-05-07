package thd.bd.sms.Application;

import android.app.Application;
import android.content.Context;
import com.forlong401.log.transaction.log.manager.LogManager;
import com.thd.cmd.manager.BDCmdManager;

import thd.bd.sms.crashUtils.CrashHandler;

public class SMSApplication extends Application {

    public Context appContext;
    private static SMSApplication smsApplication;
    public boolean openCrash = true; // 关闭或打开 crah重启

    public static SMSApplication getInstance(){
        if(smsApplication!=null){
            return smsApplication;
        }else {
            smsApplication = new SMSApplication();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;
        //上线的时候打开该代码
        if (openCrash) {
            CrashHandler.newInstance().init(appContext);
        }
        //第三方日志收集器
        LogManager.getManager(getApplicationContext()).registerCrashHandler();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //第三方日志反注册
        LogManager.getManager(getApplicationContext()).unregisterCrashHandler();
        BDCmdManager.getInstance(this).onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
