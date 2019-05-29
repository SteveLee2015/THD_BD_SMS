package thd.bd.sms.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.BDBeam;
import android.location.BDEventListener;
import android.location.BDLocation;
import android.location.BDLocationReport;
import android.location.BDMessageInfo;
import android.location.BDUnknownException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.forlong401.log.transaction.log.manager.LogManager;
import com.thd.cmd.manager.BDCmdManager;
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
import thd.bd.sms.activity.MainActivity;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.bean.FriendsLocation;
import thd.bd.sms.database.BDMessageDatabaseOperation;
import thd.bd.sms.database.FriendsLocationDatabaseOperation;
import thd.bd.sms.fragment.CommunicationFragment;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.CrashHandler;
import thd.bd.sms.service.LocationService;
import thd.bd.sms.utils.DBhelper;
import thd.bd.sms.utils.DateUtils;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.Utils;


public class SMSApplication extends Application {

    public Context appContext;
    private static SMSApplication smsApplication;
    public boolean openCrash = true; // 关闭或打开 crah重启
    public BDCmdManager bdCmdManager;
    public static LocationService locationService;

    private static final String TAG = "SMSApplication";

    public static SMSApplication getInstance() {
        if (smsApplication != null) {
            return smsApplication;
        } else {
            smsApplication = new SMSApplication();
        }
        return null;
    }

    //接收短报文广播接收器
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
                        Log.i(TAG, "LERRY_TXR: =====================接收到短报文==" + message);

                        storeMsg(appContext, mBDMessage.getmUserAddress(), message);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
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
        public void onCardInfo(android.location.CardInfo cardInfo) {
            Log.e("TEST", "======================>cardInfo " + cardInfo.getCardAddress());

            EventBus.getDefault().postSticky(cardInfo);
//            EventBus.getDefault().post(cardInfo);

            //卡等级
            if(cardInfo.getCommLevel()==0){
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_COMMLEVEL, 0);
            }else {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_COMMLEVEL, cardInfo.getCommLevel());
            }
            //是否加密
            if(cardInfo.getCheckEncryption()==null){
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_CHECKENCRYPITION, "");
            }else {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_CHECKENCRYPITION, cardInfo.getCheckEncryption());
            }

            //频度
            if(cardInfo.getSericeFeq()==0){
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_SERICEFEQ, 0);
            }else {
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_SERICEFEQ, cardInfo.getSericeFeq());
            }
            //是否有卡+卡号
            if(cardInfo.getCardAddress()==null){
                SharedPreferencesHelper.put(Constant.SP_CARD_INFO_ADDRESS, "");
            }else {
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
//            Log.e(TAG, "SMSApplication96: ==========波束=========="+beams );
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

            Log.e("TEST", "=================> bdLocationListener lat =" + bdLocation.getLatitude() + ",lon =" + bdLocation.getLongitude());
        }
    };

    /**
     * 读取位置报告信息监听类
     */
    private BDEventListener.BDLocReportListener bdLocReportListener = new BDEventListener.BDLocReportListener() {
        @Override
        public void onLocReport(BDLocationReport bdLocationReport) {
            Toast.makeText(appContext, "bdLocReportListener lat =" + bdLocationReport.getLatitude() + ",lon =" + bdLocationReport.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.e("TEST", "=================> bdLocReportListener lat =" + bdLocationReport.getLatitude() + ",lon =" + bdLocationReport.getLongitude());
            boolean isAdd = mAddLocationReportToDatabase(bdLocationReport, Config.RD_DWR);
            notifcation(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);
            notificationSMS(bdLocationReport.mUserAddress,bdLocationReport.mLatitude+","+bdLocationReport.mLongitude);
        }
    };

    /**
     * 通知数据有更新
     */
    private void notifcation(String action) {

        Intent intent = new Intent();
        intent.setAction(action);
        appContext.sendBroadcast(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;
        bdCmdManager = BDCmdManager.getInstance(appContext);

        Log.e(TAG, "onCreate: ==========开启后台服务啦。。。。");

        //上线的时候打开该代码
        if (openCrash) {
            CrashHandler.newInstance().init(appContext);
        }
        //第三方日志收集器
        LogManager.getManager(getApplicationContext()).registerCrashHandler();

        SharedPreferencesHelper.init(this);

        initMap();

        initBDService();

//        initGreenDao();
    }

    private void initMap() {
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
    }

    /**
     * 注册北斗接收监听器
     */
    private void initBDService() {
        registerReceiver();
        try {
            bdCmdManager.addBDEventListener(localInfoListener, bdBeamStatusListener, bdCmdTimeOutListener, bdLocationListener, bdLocReportListener);
        } catch (BDUnknownException e) {
            e.printStackTrace();
        }
    }



    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BDConstants.BD_MESSAGE_BROAD_ACTION);
        registerReceiver(receiver, filter);
    }

    private void unRegisterReceiver() {
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
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
        Log.e(TAG, "onTrimMemory: ==========关闭后台服务啦。。。。");
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
        Log.e(TAG, "LERRY_TXA: =======SMSApplication短报文显示通知方法================");

        Intent notificationIntent = new Intent(appContext, CommunicationFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(appContext, 0,
                notificationIntent, 0);

        @SuppressLint("ResourceType") InputStream is = appContext.getResources().openRawResource(R.mipmap.notification_new_sms);
        //InputStream is = mContext.getResources().openRawResource(R.drawable.qqqqqq);
//		InputStream is = mContext.getResources().openRawResource(R.drawable.xxoo);
        Bitmap mBitmap = BitmapFactory.decodeStream(is);


        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(NOTIFICATION_SERVICE);

        String channelId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelId = "0516";
            NotificationChannel channel = new NotificationChannel(channelId, "lerry", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(false); //是否在桌面icon右上角展示小红点   
//            channel.setLightColor(Color.RED); //小红点颜色   
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知   
            notificationManager.createNotificationChannel(channel);

        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, "0516");
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

        FriendsLocationDatabaseOperation oper = new FriendsLocationDatabaseOperation(appContext);


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

        Log.e(TAG, "mAddLocationReportToDatabase: =============reportTime=="+reportTime );

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

            time = ymd +"\t"+ beijingTime + ":" + mm + ":" + ss;
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
        Log.e(TAG, "mAddLocationReportToDatabase: =======是否保存到数据库========"+isTrue +",========time=="+time);
        oper.close();
        return isTrue;
    }



    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
//    private void initGreenDao() {
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "BD_SMS.db");
//        SQLiteDatabase db = helper.getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(db);
//        daoSession = daoMaster.newSession();
//    }
//
//    private DaoSession daoSession;
//    public DaoSession getDaoSession() {
//        return daoSession;
//    }

}
