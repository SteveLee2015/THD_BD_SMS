package thd.bd.sms.Application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.BDBeam;
import android.location.BDEventListener;
import android.location.BDLocation;
import android.location.BDLocationReport;
import android.location.BDMessageInfo;
import android.location.BDUnknownException;
import android.location.CardInfo;
import android.util.Log;
import android.widget.Toast;

import com.forlong401.log.transaction.log.manager.LogManager;
import com.thd.cmd.manager.BDCmdManager;
import com.thd.cmd.manager.helper.BDConstants;
import com.thd.cmd.manager.listener.BDCmdTimeOutListener;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import thd.bd.sms.crashUtils.CrashHandler;

public class SMSApplication extends Application {

    public Context appContext;
    private static SMSApplication smsApplication;
    public boolean openCrash = true; // 关闭或打开 crah重启
    public BDCmdManager bdCmdManager;
//    private static CardInfo cardInfoBean = new CardInfo();

    private static final String TAG = "SMSApplication";
    private boolean isFirst = true;

    public static SMSApplication getInstance(){
        if(smsApplication!=null){
            return smsApplication;
        }else {
            smsApplication = new SMSApplication();
        }
        return null;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BDConstants.BD_MESSAGE_BROAD_ACTION:
                    BDMessageInfo mBDMessage = intent.getParcelableExtra(BDConstants.BDRDSS_MESSAGE);
                    String message = "";
                    try {
                        message = new String(mBDMessage.getMessage(), "GBK");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(appContext, message ,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 读取北斗卡信息监听类
     */
    private BDEventListener.LocalInfoListener localInfoListener = new BDEventListener.LocalInfoListener() {

        @Override
        public void onCardInfo(CardInfo cardInfo) {
            Log.i("TEST", "======================>cardInfo " + cardInfo.getCardAddress());

//            setCardInfoBean(cardInfo);
//            if(!isFirst){
                EventBus.getDefault().post(cardInfo);
//            }
//            isFirst = false;
        }
    };

    private String beams = "";
    /**
     * 读取波束信息监听类
     */
    private BDEventListener.BDBeamStatusListener bdBeamStatusListener = new BDEventListener.BDBeamStatusListener() {
        @Override
        public void onBeamStatus(BDBeam bdBeam) {
            for (Integer beam : bdBeam.getBeamWaves()) {
                beams += (beam +",");
            }
            Log.e(TAG, "SMSApplication89: ==========波束=========="+beams );
            beams ="";
        }
    };

    /**
     * 监听队列中发送数据的位置和等待时间
     */
    private BDCmdTimeOutListener bdCmdTimeOutListener = new BDCmdTimeOutListener() {
        @Override
        public void onTimeOut(Map<Integer, Long> map) {
            for (Integer key : map.keySet()) {
                Log.i("TEST1" ,"==========key =" +key +", value =" +map.get(key));
            }
        }
    };

    /**
     * 读取RDSS位置信息监听类
     */
    private BDEventListener.BDLocationListener bdLocationListener = new BDEventListener.BDLocationListener() {
        @Override
        public void onLocationChange(BDLocation bdLocation) {
            Toast.makeText(appContext, "bdLocationListener lat =" + bdLocation.getLatitude() +",lon =" + bdLocation.getLongitude() , Toast.LENGTH_SHORT).show();
            Log.i("TEST" , "=================> bdLocationListener lat =" + bdLocation.getLatitude() +",lon =" + bdLocation.getLongitude());
        }
    };

    /**
     * 读取位置报告信息监听类
     */
    private BDEventListener.BDLocReportListener bdLocReportListener = new BDEventListener.BDLocReportListener() {
        @Override
        public void onLocReport(BDLocationReport bdLocationReport) {
            Toast.makeText(appContext, "bdLocReportListener lat =" + bdLocationReport.getLatitude() +",lon =" + bdLocationReport.getLongitude() , Toast.LENGTH_SHORT).show();
            Log.i("TEST" , "=================> bdLocReportListener lat =" + bdLocationReport.getLatitude() +",lon =" + bdLocationReport.getLongitude());
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;
        bdCmdManager = BDCmdManager.getInstance(appContext);

        Log.e(TAG, "onCreate: ==========开启后台服务啦。。。。" );

        //上线的时候打开该代码
        if (openCrash) {
            CrashHandler.newInstance().init(appContext);
        }
        //第三方日志收集器
        LogManager.getManager(getApplicationContext()).registerCrashHandler();

        initBDService();
    }

    private void initBDService() {
        registerReceiver();
        try {
            bdCmdManager.addBDEventListener(localInfoListener, bdBeamStatusListener ,bdCmdTimeOutListener ,bdLocationListener , bdLocReportListener);
        } catch (BDUnknownException e) {
            e.printStackTrace();
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BDConstants.BD_MESSAGE_BROAD_ACTION);
        registerReceiver(receiver , filter);
    }

    private void unRegisterReceiver() {
        unregisterReceiver(receiver);
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

        unRegisterReceiver();
        bdCmdManager.onDestroy();
        Log.e(TAG, "onTrimMemory: ==========关闭后台服务啦。。。。" );
    }

//    public CardInfo getCardInfoBean() {
//        return cardInfoBean;
//    }
//
//    public void setCardInfoBean(CardInfo cardInfoBean) {
//        SMSApplication.cardInfoBean = cardInfoBean;
//    }
}
