package thd.bd.sms.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.BDBeam;
import android.location.BDEventListener;
import android.location.BDLocation;
import android.location.BDLocationReport;
import android.location.BDMessageInfo;
import android.location.BDUnknownException;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.thd.cmd.manager.BDCmdManager;
import com.thd.cmd.manager.entity.BDNACCmd;
import com.thd.cmd.manager.entity.BDNALCmd;
import com.thd.cmd.manager.entity.BDSendCommand;
import com.thd.cmd.manager.helper.BDConstants;
import com.thd.cmd.manager.listener.BDCmdTimeOutListener;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import thd.bd.sms.R;
import thd.bd.sms.activity.MainCenterActivity;
import thd.bd.sms.activity.MsgdbActivity;
import thd.bd.sms.bean.BDCache;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.bean.BDInstructionNav;
import thd.bd.sms.bean.BDLineNav;
import thd.bd.sms.bean.FriendsLocation;
import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.database.BDInstructionNavOperation;
import thd.bd.sms.database.BDLineNavOperation;
import thd.bd.sms.database.BDMessageDatabaseOperation;
import thd.bd.sms.database.FriendsLocationDatabaseOperation;
import thd.bd.sms.database.RDCacheOperation;
import thd.bd.sms.fragment.CommunicationFragment;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.DBhelper;
import thd.bd.sms.utils.Logger;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.view.GspStatesManager;


/**
 * @author lerry
 */
public class CoreService extends Service {

    private static String objectLock = new String("CoreService");

    private static final String TAG = "CoreService";

    public static Context mContext;

    public BDCmdManager bdCmdManager;
    private RDCacheOperation cacheOperation;

    public static CoreService instance;


    private long lastTime = 0;//上传访问时间

    private int sleepTime = 60;//线程睡眠等待时间

    private Thread sendThread;


    private TakeCacheable mTakeCacheable;

    private int overTime;//超频时间

//    private int num;//信号值

    private RDCacheOperation operation;//rd缓存数据操作类

    private boolean isWait = false;//无信号,无数据等待

    private boolean isSleep = false;//超频睡眠

    private Handler mHanlder = new Handler();

    public CoreService() {
        super();
    }

    private MyConn myConn;
    private boolean isBind = false;

    public static CoreService getInstance() {

        if (instance == null) {
            instance = new CoreService();
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "LERRY_TXA: =======CoreService102短报文后台服务开启================");


        mContext = this.getApplication().getApplicationContext();
        bdCmdManager = BDCmdManager.getInstance(mContext);
        operation = new RDCacheOperation(mContext);
        SharedPreferencesHelper.getSericeFeq();
        if (SharedPreferencesHelper.getSericeFeq() != 0) {
            sleepTime = SharedPreferencesHelper.getSericeFeq();
        }


        initBDService();

        myConn = new MyConn();

        //通知
        notification();
//        showUI();


    }

    /**
     * 注册北斗接收监听器
     */
    private void initBDService() {
        registerReceiver();
        try {
            bdCmdManager.addBDEventListener(localInfoListener, bdBeamStatusListener, bdCmdTimeOutListener, bdLocationListener, bdLocReportListener, mBDFKIListener);
        } catch (BDUnknownException e) {
            e.printStackTrace();
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BDConstants.BD_MESSAGE_BROAD_ACTION);
        registerReceiver(receiver, filter);
        Log.e(TAG, "registerReceiver: ===========已注册registerReceiver=========");
    }

    private void unRegisterReceiver() {
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.e(TAG, "unRegisterReceiver: ==========IllegalArgumentException===========");
            }

        }
    }


   /* *//**
     * 开启或关闭RD/RN位置报告
     *//*

    public void openLocReportService(String tag){
        switch (tag) {
            case ReportSet.REPORTSET_RD:
                Intent service = new Intent(this, CycleReportRDLocService.class);
                //service.putExtra("reportType", reportType);
//                    service.putExtra("sendNumStr", sendNumStr);
//                    service.putExtra("reportStatus", reportStatus);
                this.startService(service);
                bindService(service, myConn, 0);
                Log.e(TAG, "LERRY_RDREPORT: =================CoreService217=======开启RD连续位置报告服务====");
                break;

            case ReportSet.REPORTSET_RN:

                break;
        }
    }*/

    public void isServiceStart(String tag){
        switch (tag){
            case ReportSet.REPORTSET_RD:
                String className = CycleReportRDLocService.class.getName();
                boolean isStart = SysUtils.isServiceRunning(mContext, className);

                if(isStart){//如果开启就需要关闭

                    Intent mIntent = new Intent();
                    mIntent.setClass(mContext, CycleReportRDLocService.class);
                    Log.e(TAG, "LERRY_RDREPORT: =================CoreService204=======关闭RD连续位置报告服务====");

//                    if (isBind) {
//                        mContext.unbindService(myConn);
//                    }
                    mContext.stopService(mIntent);
                }else {//开启
                    Intent service = new Intent(mContext, CycleReportRDLocService.class);
                    //service.putExtra("reportType", reportType);
//                    service.putExtra("sendNumStr", sendNumStr);
//                    service.putExtra("reportStatus", reportStatus);
                    mContext.startService(service);
//                    mContext.bindService(service, myConn, 0);
                    Log.e(TAG, "LERRY_RDREPORT: =================CoreService217=======开启RD连续位置报告服务====");
                }

                break;

            case ReportSet.REPORTSET_RN:

                String className1 = CycleReportRNLocService.class.getName();
                boolean isStart1 = SysUtils.isServiceRunning(mContext, className1);

                if(isStart1){//如果开启就需要关闭

                    Intent mIntent = new Intent();
                    mIntent.setClass(mContext, CycleReportRNLocService.class);
                    Log.e(TAG, "LERRY_RDREPORT: =================CoreService231=======关闭RN连续位置报告服务====");

//                    if (isBind) {
//                        mContext.unbindService(myConn);
//                    }
                    mContext.stopService(mIntent);
                }else {//开启
                    Intent service = new Intent(mContext, CycleReportRNLocService.class);
                    //service.putExtra("reportType", reportType);
//                    service.putExtra("sendNumStr", sendNumStr);
//                    service.putExtra("reportStatus", reportStatus);
                    mContext.startService(service);
//                    mContext.bindService(service, myConn, 0);
                    Log.e(TAG, "LERRY_RDREPORT: =================CoreService244=======开启RN连续位置报告服务====");
                }

                break;
        }
    }

    @SuppressWarnings("ResourceType")
    public void notification() {
//        Toast.makeText(mContext, "北斗服务启动", 1).show();


        InputStream is = getResources().openRawResource(R.mipmap.notification_new_sms);
        Bitmap mBitmap = BitmapFactory.decodeStream(is);
        mContext = mContext.getApplicationContext();
//        Intent notificationIntent = new Intent(mContext, MsgdbActivity.class);
//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

        String CHANNEL_ONE_ID = "thd.bd.sms";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);


//        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.notification_new_sms);
            Notification.Builder builder = new Notification.Builder(getApplicationContext())
                    .setChannelId(CHANNEL_ONE_ID)
                    .setContentTitle("北斗服务")
                    .setContentText("北斗后台服务正在运行")
//                .setContentIntent(contentIntent)//点击意图
                    .setTicker("@core")
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(false)//用户点击就自动消失
                    .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setLargeIcon(mBitmap)//加载大图标
                    //.setColor(Color.parseColor("#3191e8"))
                    .setColor(getResources().getColor(R.color.colorMainBlue))
                    .setSmallIcon(R.mipmap.notification_new_sms);//设置通知小ICON
            Notification notification = builder.build();

//        Intent notificationIntent = new Intent(mContext, MsgdbActivity.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        notification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

            startForeground(1, notification);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.d(TAG, "onStartCommand");

        mTakeCacheable = new TakeCacheable();
        sendThread = new Thread(mTakeCacheable);
        sendThread.setPriority(Thread.MIN_PRIORITY);
        sendThread.start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        Intent mIntent = new Intent();
//        mIntent.setClass(this, clazz);
//        Log.e(TAG, "LERRY_RDREPORT: =================MainCenterActivity183=======关闭RD/RN连续位置报告服务====");

        unRegisterReceiver();

        if (isBind) {
            this.unbindService(myConn);
        }
//        this.stopService(mIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    /*private void openUIsos() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent sosIntent = new Intent(mContext, SOSActivity.class);
                sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sosIntent);
                Toast.makeText(mContext, "=============", Toast.LENGTH_SHORT).show();

            }
        }, 3000);
    }*/

    /**
     * 发送业务 从缓存数据库中获取数据
     *
     * @author llg052
     */
    public class TakeCacheable implements Runnable {

        long currentTime;
        long interval;
        long needSleep;
        long pastSleep;
        int totalCount;

        @Override
        public void run() {

            /*while (true) {

                totalCount = SharedPreferencesHelper.getRecordedCount();
                //int totalCount2 = operation.getCount();

//                Log.e(TAG, "LERRY_TXA: =======CoreService480============wait前:发送线程取出的缓存数据=="+totalCount );
                if (totalCount < 1) {
                    synchronized (objectLock) {
                        try {
                            isWait = true;
                            Log.e(TAG, "LERRY_TXA: =======CoreService486============wait前:totalCount=="+totalCount+"========发送线程进入wait()状态!" );
                            //totalCount = operation.getCount();
                            objectLock.wait();
                            Log.e(TAG, "LERRY_TXA: =======CoreService487============发送线程wait()状态结束!==" );
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (isSleep) {
                    try {
                        Logger.d(TAG, "发送线程超频睡眠开始!" + (overTime + sleepTime) * 1000);
                        sendThread.sleep((overTime + sleepTime) * 1000);
                        overTime = 0;
                        isSleep = false;
                        Logger.d(TAG, "发送线程超频睡眠结束!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                threadRunning();
            }*/
        }


        private void threadRunning() {

            /*Logger.d(TAG, "notify后:发送线程取出的缓存数据" + totalCount);
            Log.e(TAG, "LERRY_TXA: =======CoreService515============notify后:发送线程取出的缓存数据=="+totalCount );
            if (GspStatesManager.getInstance().beamnumber > 0 && totalCount > 0) {// 有信号 有数据

                currentTime = System.currentTimeMillis();
                interval = currentTime - lastTime;
                if (interval <= 0) {
                    lastTime = currentTime;
                    return;
                }
                if (interval - sleepTime * 1000 > 1000) {
                    lastTime = currentTime;

                    Logger.d(TAG, "有信号,有数据执行run");
                    Log.e(TAG, "LERRY_TXA: =======CoreService515============notify后:发送线程取出的缓存数据=="+totalCount );
                    BDCache firstCache = null;
                    try {
                        firstCache = operation.getFirst();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (firstCache != null) {
                        // 发送数据
                        Intent intent = new Intent();
                        intent.setAction(BroadcastReceiverConst.COMM_RAW_DATA_ADD);

                        try {
                            intent.putExtra(BroadcastReceiverConst.COMM_RAW_DATA,
                                    firstCache.getCacheContent().getBytes("GBK"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mContext.sendBroadcast(intent);
                        Log.e(TAG, "LERRY_TXA: =======CoreService539============发送广播到COMM_RAW_DATA_ADD==" );
                        // 等待FKI信息 根据FKI信息删除缓存数据库内容
                        Logger.d(TAG, "发送数据完成!等待FKI反馈!");

                        synchronized (this) {
                            try {
                                wait(sleepTime * 1000);
                                Logger.d(TAG, "等待中断!");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        Log.e(TAG, "LERRY_TXA: =======CoreService562============没有数据记录!==" );
                        Logger.d(TAG, "没有数据记录!");
                    }

                } else {

                    try {
                        Thread.sleep(1000);
                        //sendThread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e(TAG, "LERRY_TXA: =======CoreService572===========e.getMessage=="+e.getMessage() );
                    }
                }
            } else {
                try {
                    //睡眠卡频时间
                    sendThread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, "LERRY_TXA: =======CoreService581===========e.getMessage=="+e.getMessage());
                }
                Logger.d(TAG, "线程睡眠1s");

            }*/
        }
    }

    private class MyConn implements ServiceConnection {

        /**
         * 当服务被成功绑定时候调用
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d(TAG, "onServiceConnected");
            isBind = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            isBind = false;
        }

    }

    //接收短报文广播接收器
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BDConstants.BD_MESSAGE_BROAD_ACTION://要区分开短报文和路线导航
                    BDMessageInfo mBDMessage = intent.getParcelableExtra(BDConstants.BDRDSS_MESSAGE);
                    String message = "";

                    try {
                        message = new String(mBDMessage.getMessage(), "GBK");
                        Log.e(TAG, "LERRY_SERVICE: ======================message=="+message );
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    /*//接收处理BDNAC命令
                    if (message.startsWith(BDSendCommand.BDNAC.get$Cmd())) {//指令导航
                        BDNACCmd cmd = new BDNACCmd();
                        cmd.setValueByCmd(message);
                        //然后就可以获取cmd中的数据

                        StringBuilder sb = new StringBuilder();
                        if (cmd.getMustGoLonLatArr() != null && cmd.getMustGoLonLatArr().length > 0) {
                            for(int i = 0; i < cmd.getMustGoLonLatArr().length; ++i) {
                                sb.append(cmd.getMustGoLonLatArr()[i]).append(",");
                            }
                        }

                        StringBuilder sb1 = new StringBuilder();
                        if (cmd.getVoidGoLonLatArr() != null && cmd.getVoidGoLonLatArr().length > 0) {
                            for(int i = 0; i < cmd.getVoidGoLonLatArr().length; ++i) {
//                                sb1.append(cmd.getVoidGoLonLatArr()[i]).append(";");
//                                sb1.append(cmd.getVoidGoLonLatArr()[i].replace("*4C", "")).append(";");//这里有待修改
                                String voidLonLat = cmd.getVoidGoLonLatArr()[i];
                                int index = voidLonLat.indexOf("*");
                                if (index != -1) {
                                    voidLonLat = voidLonLat.substring(0 , index);
                                }
                                sb1.append(voidLonLat).append(";");
                            }
                        }

                        saveNACToDb(mBDMessage.getmUserAddress(),cmd.getLineId(),cmd.getTargeLonLatArr()[0],cmd.getTargeLonLatArr()[1],sb.toString(),sb1.toString());


                    } else if (message.startsWith(BDSendCommand.BDNAL.get$Cmd())) {//线路导航
                        BDNALCmd bdnalCmd = new BDNALCmd();
                        bdnalCmd.setValueByCmd(message);
                        //可以处理数据

                        StringBuilder sb = new StringBuilder();
                        if (bdnalCmd.getLonlatArr() != null && bdnalCmd.getLonlatArr().length > 0) {
                            for(int i = 0; i < bdnalCmd.getLonlatArr().length; ++i) {
                                sb.append(bdnalCmd.getLonlatArr()[i]).append(",");
                            }
                        }
                        saveNALToDb(bdnalCmd.getNavId(),bdnalCmd.getCmdNum(),bdnalCmd.getCmdTotal(),sb.toString());

                    } else {*/
                        try {
                            message = new String(mBDMessage.getMessage(), "GBK");
                            Log.i(TAG, "LERRY_TXR: =====================接收到短报文==" + message);

                            storeMsg(mContext, mBDMessage.getmUserAddress(), message);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
//                    }
//                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private BDEventListener.BDFKIListener mBDFKIListener = new BDEventListener.BDFKIListener() {

        @Override
        public void onCmd(String s, boolean b) {
            Log.e("LERRY_FKI", "mBDFKIListener: ======================onCmd=======s==" + s + "===========b==" + b);

            cacheOperation = new RDCacheOperation(mContext);

            if (cacheOperation.getFirst() == null) {
                return;
            }

            BDCache firstCache = cacheOperation.getFirst();

            switch (s) {
                case "WBA":
                    if (b) {
                        //删除最上面的一条信息
                        String msgType = firstCache.getMsgType();
                        if (BDCache.RD_REPORT_FLAG.equals(msgType)) {
                            dispatchData(firstCache);
                            notifyData();
                            Toast.makeText(mContext, "位置报告2发送成功!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "位置报告2发送失败!", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case "DWA":
                    if (b) {
                        //删除最上面的一条信息
                        String msgType = firstCache.getMsgType();
                        if (BDCache.RD_LOCATION_FLAG.equals(msgType)) {
                            dispatchData(firstCache);
                            notifyData();
                            Toast.makeText(mContext, "定位命令发送成功!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "定位命令发送成功!", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case "WAA":
                    if (b) {
                        //删除最上面的一条信息
                        String msgType = firstCache.getMsgType();
                        if (BDCache.RN_REPORT_FLAG.equals(msgType)) {
                            dispatchData(firstCache);
                            notifyData();
                            Toast.makeText(mContext, "位置报告1发送成功!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "位置报告1发送失败!", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case "TXA":
                    if (b) {
                        //删除最上面的一条信息
                        String msgType = firstCache.getMsgType();
                        if (BDCache.SMS_FLAG.equals(msgType)) {
                            dispatchData(firstCache);
                            notifyData();
                            Toast.makeText(mContext, "北斗短报文发送成功!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "北斗短报文发送失败!", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }

        @Override
        public void onTime(int i) {
            Log.e("LERRY_FKI", "mBDFKIListener: ======================onTime=======i==" + i);
        }

        @Override
        public void onSystemLauncher() {
            Log.e("LERRY_FKI", "mBDFKIListener: ======================onSystemLauncher=======");
        }

        @Override
        public void onPower() {
            Log.e("LERRY_FKI", "mBDFKIListener: ======================onPower=======");
        }

        @Override
        public void onSilence() {
            Log.e("LERRY_FKI", "mBDFKIListener: ======================onSilence=======");
        }
    };

    /**
     * 读取北斗卡信息监听类
     */
    private BDEventListener.LocalInfoListener localInfoListener = new BDEventListener.LocalInfoListener() {

        @Override
        public void onCardInfo(android.location.CardInfo cardInfo) {
            Log.e("TEST", "======================>cardInfo " + cardInfo.getCardAddress());

            EventBus.getDefault().postSticky(cardInfo);
//            EventBus.getDefault().post(cardInfo);

            //卡等级
            if (cardInfo.getCommLevel() == 0) {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_COMMLEVEL, 0);
            } else {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_COMMLEVEL, cardInfo.getCommLevel());
            }
            //是否加密
            if (cardInfo.getCheckEncryption() == null) {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_CHECKENCRYPITION, "");
            } else {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_CHECKENCRYPITION, cardInfo.getCheckEncryption());
            }

            //频度
            if (cardInfo.getSericeFeq() == 0) {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_SERICEFEQ, 0);
            } else {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_SERICEFEQ, cardInfo.getSericeFeq());
            }
            //是否有卡+卡号
            if (cardInfo.getCardAddress() == null) {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_ADDRESS, "");
            } else {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_ADDRESS, cardInfo.getCardAddress());
            }
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
                beams += (beam + ",");
            }
//            Log.e(TAG, "LERRYTEST_bs: =======CoreService697============波束=========="+beams );
            beams = "";
            EventBus.getDefault().post(bdBeam);
        }
    };

    /**
     * 监听队列中发送数据的位置和等待时间
     */
    private BDCmdTimeOutListener bdCmdTimeOutListener = new BDCmdTimeOutListener() {
        @Override
        public void onTimeOut(Map<Integer, Long> map) {
            for (Integer key : map.keySet()) {
                Log.w("TEST1", "==========key =" + key + ", value =" + map.get(key));
            }
        }
    };

    /**
     * 读取RDSS位置信息监听类
     */
    private BDEventListener.BDLocationListener bdLocationListener = new BDEventListener.BDLocationListener() {
        @Override
        public void onLocationChange(BDLocation bdLocation) {
            EventBus.getDefault().post(bdLocation);
            bdLocation.dumpInfo();

            Log.e("TEST", "=================> bdLocationListener lat =" + bdLocation.getLatitude() + ",lon =" + bdLocation.getLongitude()+",time = "+bdLocation.mLocationTime);
        }
    };

    /**
     * 读取位置报告信息监听类
     */
    private BDEventListener.BDLocReportListener bdLocReportListener = new BDEventListener.BDLocReportListener() {
        @Override
        public void onLocReport(BDLocationReport bdLocationReport) {
//            Toast.makeText(mContext, "bdLocReportListener lat =" + bdLocationReport.getLatitude() + ",lon =" + bdLocationReport.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.e("TEST", "=================> bdLocReportListener lat =" + bdLocationReport.getLatitude() + ",lon =" + bdLocationReport.getLongitude());
            boolean isAdd = mAddLocationReportToDatabase(bdLocationReport, Config.RD_DWR);
            notifcation(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);
            notificationSMS(bdLocationReport.mUserAddress, bdLocationReport.mLatitude + "," + bdLocationReport.mLongitude);
        }
    };

    /**
     * 通知数据有更新
     */
    private void notifcation(String action) {

        Intent intent = new Intent();
        intent.setAction(action);
        mContext.sendBroadcast(intent);

    }

    /**
     * 保存短报文
     *
     * @param context
     * @param bdAddress
     * @param msg
     */
    private void storeMsg(Context context, String bdAddress, String msg) {

        //此处要根据 bdAddress 收件号码 查询出收件人再保存到数据库
        String userAddres = bdAddress;
        if (userAddres.startsWith("0")) {
            userAddres = userAddres.substring(1, userAddres.length());
        }
        List<Map<String, Object>> contactList = DBhelper.queryContactDB(context, userAddres);
        if (contactList != null && contactList.size() > 0) {
            for (Map<String, Object> map : contactList) {
                String userName = (String) map.get(BDContactColumn.USER_NAME);
                BDMessageDatabaseOperation messageOperation = new BDMessageDatabaseOperation(context);
                thd.bd.sms.bean.BDMessageInfo info = new thd.bd.sms.bean.BDMessageInfo();
                info.setmUserAddress(bdAddress);
                info.setUserName(userName);
                info.setMessage(msg);
                info.setMsgType(Integer.parseInt(BDMessageDatabaseOperation.MSG_TYPE_NOT_READ));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                info.setmSendTime(date);
                long rawId = messageOperation.insert(info, BDMessageDatabaseOperation.MSG_TYPE_NOT_READ);
                break;
            }
        } else {
            BDMessageDatabaseOperation messageOperation = new BDMessageDatabaseOperation(context);
            thd.bd.sms.bean.BDMessageInfo info = new thd.bd.sms.bean.BDMessageInfo();
            info.setmUserAddress(bdAddress);
            info.setMessage(msg);
            info.setMsgType(Integer.parseInt(BDMessageDatabaseOperation.MSG_TYPE_NOT_READ));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            info.setmSendTime(date);
            long rawId = messageOperation.insert(info, BDMessageDatabaseOperation.MSG_TYPE_NOT_READ);
        }


        notificationSMS(userAddres, msg);

        //通知  聊天界面更新数据广播
        Intent refreshIntent = new Intent();
        refreshIntent.putExtra(ReceiverAction.APP_KEY_SMS_RECEIVER, bdAddress);
        refreshIntent.setAction(ReceiverAction.APP_ACTION_SMS_REFRESH);
        /*if(Build.VERSION.SDK_INT >= 26) {
            ComponentName componentName=new ComponentName(getApplicationContext(),"");//参数1-包名 参数2-广播接收者所在的路径名
            refreshIntent.setComponent(componentName);
//            refreshIntent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
        }*/

//        Log.e(TAG, "LERRY_MSG: ==========CoreService808======已执行APP_ACTION_SMS_REFRESH===================" );

        context.sendOrderedBroadcast(refreshIntent, null);

//        int flag = Integer.valueOf(BDMessageDatabaseOperation.MSG_TYPE_RECEIVER).intValue();
//        if (flag == 3) {
//            Intent receiverIntent1 = new Intent();
//            receiverIntent1.setAction("com.bd.action.BD_SMS_ICON_ACTION");
//            context.sendBroadcast(receiverIntent1);
//        }
    }


    /**
     * notification 短报文通知栏提醒
     */
    private void notificationSMS(String address, String msg) {
        Log.e(TAG, "LERRY_TXA: =======CoreService短报文显示通知方法================");

        Intent notificationIntent = new Intent(mContext, CommunicationFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, 0);

        @SuppressLint("ResourceType") InputStream is = mContext.getResources().openRawResource(R.mipmap.notification_new_sms);
        //InputStream is = mContext.getResources().openRawResource(R.drawable.qqqqqq);
//		InputStream is = mContext.getResources().openRawResource(R.drawable.xxoo);
        Bitmap mBitmap = BitmapFactory.decodeStream(is);


        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        String channelId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelId = "0516";
            NotificationChannel channel = new NotificationChannel(channelId, "lerry", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false); //是否在桌面icon右上角展示小红点   
//            channel.setLightColor(Color.RED); //小红点颜色   
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知   
            notificationManager.createNotificationChannel(channel);

        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "0516");
        builder.setContentTitle("来自" + address + "的消息")
                .setContentText("消息内容:" + msg)
                .setContentIntent(contentIntent)//点击意图
                .setTicker("您有新消息!")
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)//用户点击就自动消失
                //.setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setDefaults(Notification.DEFAULT_SOUND)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setLargeIcon(mBitmap)
                //.setColor(Color.parseColor("#EAA935"))
                .setColor(Color.parseColor("#3191e8"))
                //.setSmallIcon(R.drawable.notification_new_sms);//设置通知小ICON
                .setSmallIcon(R.mipmap.notification_new_sms_small);//设置通知小ICON
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Config.NOTIFICATION_SMS, notification);
        //notificationManager.cancel(100);//通知以后自动消失了
    }


    /**
     * 保存友邻位置到数据库
     *
     * @param report
     * @return
     */
    private boolean mAddLocationReportToDatabase(BDLocationReport report, int flag) {

        FriendsLocationDatabaseOperation oper = new FriendsLocationDatabaseOperation(mContext);


        double latitude = report.getLatitude();
        double longitude = report.getLongitude();

        //2.1 转换为 °的问题
        double latitudeNew = Utils.changeLonLatMinuteToDegree(Double.valueOf(latitude));
        double longitudeNew = Utils.changeLonLatMinuteToDegree(Double.valueOf(longitude));


        //latitude = Double.parseDouble(latiFormat);
        //longitude = Double.parseDouble(longiFormat);

        report.setLatitude(latitudeNew);
        report.setLongitude(longitudeNew);

        String reportTime = report.getReportTime();

        Log.e(TAG, "mAddLocationReportToDatabase: =============reportTime==" + reportTime);

        //本地时间
        String time = "00:00:00.00";
        if (reportTime.length() >= 6) {
            String hh = reportTime.substring(0, 2);
            int anInt = Integer.parseInt(hh);
            int beijingTime = (anInt + 8) % 24;
            String mm = reportTime.substring(2, 4);
            String ss = reportTime.substring(4, 6);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            String ymd = simpleDateFormat.format(new Date());

            time = ymd + "\t" + beijingTime + ":" + mm + ":" + ss;
        }

        /*String[] split = cbTime[0].split(":");
        if (split != null) {
            String hh = split[0];
            if (hh.length() > 2) {
                //去除 年月日  2016-12-08 15:27:3 崩溃bug
                String[] arr = hh.split("\\s+");
                for (String ss : arr) {
                    System.out.println(ss);
                }
                if (arr.length > 1) {
                    hh = arr[arr.length - 1];
                }
            }
            int anInt = Integer.parseInt(hh);
            int reportTimeInt = 0;
            //怎么区分 rd 时间  和  rn时间
            switch (flag) {
                case Config.RN_RD_WAA:
                    reportTimeInt = (anInt + 8) % 24;
                    break;
                case Config.RD_DWR:
                    reportTimeInt = anInt;
                    break;
            }

            //int reportTimeInt = (anInt+8)%24;

            int i = reportTime.indexOf(":");
            String otherStr = reportTime.substring(i, reportTime.length());
            reportTime = reportTimeInt + otherStr;
        }*/

        FriendsLocation fl = new FriendsLocation();
        fl.setUserId(report.getUserAddress());
        fl.setLat(String.valueOf(report.getLatitude()));
        fl.setLon(String.valueOf(report.getLongitude()));
        fl.setHeight(String.valueOf(report.getHeight()));
        fl.setReportTime(time);
        boolean isTrue = oper.insert(fl);
        Log.e(TAG, "mAddLocationReportToDatabase: =======是否保存到数据库========" + isTrue + ",========time==" + time);
        oper.close();
        return isTrue;
    }


    /**
     * 删除数据记录
     *
     * @param firstCache
     */
    private void dispatchData(BDCache firstCache) {
        cacheOperation.delete(firstCache);
        if (BDCache.PRIORITY_MAX == firstCache.getPriority()) {
            //删除的紧急救援条数
            //记录在app中
            Config.SOS_COUNT++;
            //  sos服务启动的时候清理一次
            // 发送广播 更新数据
            Intent sosCountIntent = new Intent();
            sosCountIntent.setAction(ReceiverAction.BD_ACTION_SOS_UI_SOS_SIZE);
            sosCountIntent.putExtra(ReceiverAction.BD_KEY_SOS_UI_SOS_SIZE, Config.SOS_COUNT);
            mContext.sendBroadcast(sosCountIntent);
        }
        int count = cacheOperation.getCount();
        SharedPreferencesHelper.put(Constant.SP_RECORDED_KEY_COUNT, count);
    }

    /**
     * 通知数据变化
     */
    private void notifyData() {
        Intent intent = new Intent();
        intent.setAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD);
        mContext.sendBroadcast(intent);
    }

    /**
     * 保存指令导航到数据库
     */
    private void saveNALToDb(String lineId,int lineNum,int lineTotalNum,String passStr){
        //保存到数据库,是否需要记录该条数据

        BDLineNavOperation navOper = new BDLineNavOperation(mContext);
        BDLineNav mBDLineNav = navOper.get(lineId);
        if (mBDLineNav == null) {
            //第一次 插入
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            String createTime = date;

            Log.e(TAG, "指令导航: =============lineId="+ lineId+"=========lineNum=="
                    +lineNum+"=========lineTotalNum=="+lineTotalNum+"============passStr=="+passStr
                    +"==========createTime=="+createTime);

            final long id = navOper.insert(lineId, lineNum + "", lineTotalNum + "", passStr, createTime);

            boolean isCompletion = navOper.checkLineNavComplete(lineId);
            if (isCompletion) {
                // 通知可以导航
                //notificationNaviLine(info, navOper, lineId);
                //notificationLineNav();
                BDLineNav line = navOper.get(lineId);

//                notificationLineNav(info, line);
                Intent receiverIntent1 = new Intent();
                receiverIntent1.putExtra("NAVILINEID", line.getLineId());
                receiverIntent1.setAction("com.bd.action.NAVI_LINE_ACTION");
                receiverIntent1.setAction(ReceiverAction.APP_ACTION_LINE_NAVI);
                mContext.sendBroadcast(receiverIntent1);

                // String content = "$BDNAR," + lineId + "," + lineNum + "," + lineTotalNum + "*41";//有问题??
                // sendBDNAR(info, content);
            } else {
                //发送回执命令 补充数据 暂时关闭
                //String content = "$BDNAR," + lineId + "," + lineNum + "," + lineTotalNum + "*41";//有问题??
                //sendBDNAR(info, content);
            }

        } else {

            //数据库中有数据  更新
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            String createTime = date;
            final long id = navOper.update(lineId, lineNum + "", lineTotalNum + "", passStr, createTime);

            //第一次 插入
            //final long id = navOper.insert(lineId, lineNum + "", lineTotalNum + "", passStr);
            boolean isCompletion = navOper.checkLineNavComplete(lineId);
            if (isCompletion) {
                // 通知可以导航
                //public static final String APP_ACTION_LINE_NAVI = "com.bd.action.LINE_NAVI_ACTION";
                //notificationNaviLine(info, navOper, lineId);
//                notificationLineNav(info, mBDLineNav);
                Intent receiverIntent1 = new Intent();
                receiverIntent1.putExtra("NAVILINEID", mBDLineNav.getLineId());
                receiverIntent1.setAction("com.bd.action.NAVI_LINE_ACTION");
                receiverIntent1.setAction(ReceiverAction.APP_ACTION_LINE_NAVI);
                mContext.sendBroadcast(receiverIntent1);
                //sendBDNAR(info, lineId, lineNum, lineTotalNum);
            } else {
                //发送回执命令 补充数据  暂时关闭
                //sendBDNAR(info, lineId, lineNum, lineTotalNum);
            }
        }
        navOper.close();
    }

    //保存路线导航到数据库
    private void saveNACToDb(String address,String lineId,String targetLonStr,String targetLatStr,String passStr,String avaidStr){
        //保存到数据库,是否需要记录该条数据
        BDInstructionNavOperation navOper = new BDInstructionNavOperation(mContext);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
//                            info.setmSendTime(date);
        String createTime = date;
        long id = -1;
        try {

            Log.e(TAG, "路线导航: =============lineId="+ lineId+"=========targetLonStr=="
                    +targetLonStr+"=========targetLatStr=="+targetLatStr+"============passStr=="+passStr
                    +"============avaidStr=="+avaidStr+"==========createTime=="+createTime);
            id = navOper.insert(address, lineId, targetLonStr + "," + targetLatStr, passStr, avaidStr, createTime);
            if (id < 0) {
                id = (long) navOper.update(address, lineId, targetLonStr + "," + targetLatStr, passStr, avaidStr, createTime);
            }
        }catch (SQLiteConstraintException ex){
            id = (long) navOper.update(address, lineId, targetLonStr + "," + targetLatStr, passStr, avaidStr, createTime);
        }
        navOper.close();

        //通知数据有更新
        notifcation(ReceiverAction.APP_ACTION_INSTRUCT_NAVI);

        if (id > 0) {
            /* 发送Notification*/
            BDInstructionNavOperation navOper1 = new BDInstructionNavOperation(mContext);
            BDInstructionNav nav = navOper1.get(id);
            navOper1.close();

            Intent receiverIntent1 = new Intent();
            receiverIntent1.putExtra("BDINSTRLINEID", nav.getRowId());
            receiverIntent1.setAction("com.bd.action.BD_INSTR_LINE_ACTION");
            mContext.sendBroadcast(receiverIntent1);

//            notificationCommandNav(info);
            return;
        }
    }

}

