package thd.bd.sms.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.location.BDLocation;
import android.location.BDLocationReport;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import thd.bd.sms.R;
import thd.bd.sms.application.SMSApplication;
import thd.bd.sms.bean.BDMessageInfo;
import thd.bd.sms.bean.DBLocation;
import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.database.BDMessageDatabaseOperation;
import thd.bd.sms.database.ReportSetDatabaseOperation;
import thd.bd.sms.service.report.LocReportManager;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.CharUtil;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.DBhelper;
import thd.bd.sms.utils.DateUtils;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.view.GspStatesManager;

/**
 * 循环报告服务
 *
 * @author llg
 */
public class CycleReportService extends Service {

    private final static String TAG = "CycleReportService";
    private final static int WARN_NO_LOCATION = 1000;
    protected static WakeLock wakeLock = null;
    protected int cardFreq = 60;//卡频
//    protected ReportSet mReportSet = null;
    protected ReportSetDatabaseOperation oper = null;
    protected MediaPlayer mediaPlayer;

    protected DBLocation rnLocation;

    protected Context mContext = this;

    protected SMSApplication app;
    private String reportType;//报告类型
    protected String reportStatus;//报告状态
    protected String sendNumStr;//收件人
    private BDLocationReport report;

    Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case WARN_NO_LOCATION:
                    Toast.makeText(mContext, "没有位置信息,发送失败!", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onCreate() {
        super.onCreate();
        app = (SMSApplication) getApplication();
//        addReceiver();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CycleReportService");
        if (null != wakeLock) {
            wakeLock.acquire();
        }


        oper = new ReportSetDatabaseOperation(this);
        if (SharedPreferencesHelper.getSericeFeq()!=0) {
            cardFreq = SharedPreferencesHelper.getSericeFeq();
        }
        initSound();
    }


    /**
     * 添加广播
     */
//    private void addReceiver() {
//
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ReceiverAction.APP_ACTION_RN_LOCATION);
//        registerReceiver(rnLocReceiver, filter);
//    }



    /**
     * 位置报告
     *
     * @param sendNumStr   收件人
     * @param reportType   报告类型
     * @param reportStatus 状态
     */
    protected void locReport(String sendNumStr, String reportType, String reportStatus) {

        rnLocation =  GspStatesManager.getInstance().mLocation;
        //重新赋值
        if (SharedPreferencesHelper.getSericeFeq()!=0) {
            cardFreq = SharedPreferencesHelper.getSericeFeq();
        }

        ReportSetDatabaseOperation settingDatabaseOper = new ReportSetDatabaseOperation(mContext);
        ReportSet reportSet = settingDatabaseOper.getByType(reportType);

        if (reportSet != null && "3".equals(reportSet.getReportType())) {//紧急救援

            reportSet.setReportNnm(sendNumStr);
            reportSet.setReportHz(cardFreq + "");

        }

        if (!sendNumStr.isEmpty()) {
            if (reportSet != null) {
                reportSet.setReportNnm(sendNumStr);
            }
        }

        LocReportManager locReportManager = LocReportManager.getInstance(mContext);

        switch (reportType) {
            case ReportSet.REPORTSET_RN:

                if (rnLocation != null) {
                    report = new BDLocationReport();

                    DecimalFormat df = new DecimalFormat("#.0000000");
                    report.setMsgType(1);// 发为1 接收为0
                    report.setUserAddress(reportSet.getReportNnm());
                    //waa.setReportTime(rnLocation.getTime()+"");//时间有问题
//                    String dateTimeStr1 = DateUtils.getRNDateTimeStr(rnLocation.getTime());
                    String dateTimeStr = DateUtils.getRNDateTimeStr(rnLocation.getTime() - 1000 * 60 * 60 * 8);

//                    report.setReportTime(dateTimeStr);//时间有问题
                    // 转换为 2.1协议
                    double latitudeNew = Utils.changeLonLatMinuteToDegreeReverse(rnLocation.getLatitude());
                    double longitudeNew = Utils.changeLonLatMinuteToDegreeReverse(rnLocation.getLongitude());

                    //2.1 转换为 °的问题
//                    double latitudeNew = Utils.changeLonLatMinuteToDegree(Double.valueOf(rnLocation.getLatitude()));
//                    double longitudeNew = Utils.changeLonLatMinuteToDegree(Double.valueOf(rnLocation.getLongitude()));

                    //处理
                    String strLatitude = df.format(latitudeNew * 100);
                    String strLongitude = df.format(longitudeNew * 100);


                    Log.e("LERRYTEST_RN" ,"=========CycleReportService165=========rnLocation.getTime()=="+rnLocation.getTime()
                            +"=========dateTimeStr=="+dateTimeStr+"============"+"rnLocation.getLatitude()=="+rnLocation.getLatitude()+
                            "=================rnLocation.getLongitude()=="+rnLocation.getLongitude()+"============latitudeNew=="+latitudeNew
                            +"==================longitudeNew=="+longitudeNew+"================strLatitude=="+strLatitude+"====================strLongitude=="+strLongitude);


                    report.setLatitude(Double.parseDouble(strLatitude));
                    report.setLatitudeDir("N");
                    report.setLongitude(Double.parseDouble(strLongitude));
                    report.setLongitudeDir("E");
                    report.setHeightUnit("M");
                    report.setReportFeq(0);// 单次
                    report.setHeight(rnLocation.getAltitude());
                    report.setReportTime(dateTimeStr);

                } else {
                    mHandler.sendEmptyMessage(WARN_NO_LOCATION);
                    return;
                }
                break;
            case ReportSet.REPORTSET_RD:// RD 位置报告
//                BD_RD_WBA wba = new BD_RD_WBA();
//                wba.setmUserID(reportSet.getReportNnm());
//                wba.setmHeightType(Config.HeightFlag.COMMON_USER);
//                wba.setmAntennaHeight(Double.parseDouble(reportSet.getTianxianValue()));
//                wba.setmReportFec(0);//单次
//                mBdData = new BDData(ProtocolType.PROTOCOL_TYPE_BD21, BD21DataType.BD_21_RD_WBA, wba);
                break;
            case ReportSet.REPORTSET_STATE:// 状态 报告  短报文+RN位置信息

                if (rnLocation != null) {
                    double lon = rnLocation.getLongitude();
                    double lat = rnLocation.getLatitude();
                    double height = rnLocation.getAltitude();

                    String latiFormat = String.format("%.5f", lat);
                    String longiFormat = String.format("%.5f", lon);
                    String heightFormat = String.format("%.1f", height);

                    lat = Double.parseDouble(latiFormat);
                    lon = Double.parseDouble(longiFormat);
                    height = Double.parseDouble(heightFormat);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                    String time = sdf.format(new Date(rnLocation.getTime()));
                    if (reportStatus.isEmpty()) {
                        reportStatus = "未设置状态!";
                    }

                    int lat2 = (int) (lat * 100000);//8
                    int lon2 = (int) (lon * 100000);//7
                    int height2 = (int) (height * 10);//6

                    String relon = "";
                    String relat = "";
                    int nlon = (int)lon;
                    int nlat = (int)lat;
                    if(nlon < 10)
                        relon = String.format("00%d", lon2);
                    else if(nlon < 100)
                        relon = String.format("0%d", lon2);
                    else
                        relon = String.format("%d", lon2);
                    if(nlat <10)
                        relat = String.format("0%d", lat2);
                    else
                        relat = String.format("%d", lat2);


                    String str_height2 = "" + height2;
                    int length = str_height2.length();
                    int toaddHeight = 6 - length;
                    String heightStr = "";
                    switch (toaddHeight) {

                        case 0:
                            heightStr = "" + height2;
                            break;
                        case 1:
                            heightStr = "0" + height2;
                            break;
                        case 2:
                            heightStr = "00" + height2;
                            break;
                        case 3:
                            heightStr = "000" + height2;
                            break;
                        case 4:
                            heightStr = "0000" + height2;
                            break;
                        case 5:
                            heightStr = "00000" + height2;
                            break;
                        case 6:
                            heightStr = "000000" + height2;
                            break;
                    }


                    String title = "ns0";
                    String loc = relon + "" + relat + heightStr;
                    String locType = 1 + "";
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
                    String time2 = sdf2.format(new Date(rnLocation.getTime()));

                    String sosInfo = reportStatus;

                    String content = title + loc + locType + time2 + sosInfo;

                    //判断发送的文字是否超长,如果超长了

                    String str = "状态报告:\r\n"+
                            "位置:(" + lon + "°," + lat + "°," + height + "m)\r\n" +
                            "定位时间:"+time+"\r\n"+
                            "状态:"+ reportStatus ;

                    // 保存到发件箱
//                    sava2db(txa,str);
                    sava2db(reportSet.getReportNnm(),0,content,str);

                    //通知界面更新  发广播
                    Intent mIntent = new Intent();
                    mIntent.putExtra(ReceiverAction.APP_KEY_SMS_RECEIVER, reportSet.getReportNnm());
                    mIntent.setAction(ReceiverAction.APP_ACTION_SMS_NEW_DIALOG);
                    sendOrderedBroadcast(mIntent, null);


                } else {
                    mHandler.sendEmptyMessage(WARN_NO_LOCATION);
                    return;
                }
                break;

            case ReportSet.REPORTSET_SOS:// 状态 报告  短报文+RN位置信息


                if (rnLocation != null) {
                    double lon = rnLocation.getLongitude();
                    double lat = rnLocation.getLatitude();
                    double height = rnLocation.getAltitude();


                    String latiFormat = String.format("%.5f", lat);
                    String longiFormat = String.format("%.5f", lon);
                    String heightFormat = String.format("%.1f", height);

                    lat = Double.parseDouble(latiFormat);
                    lon = Double.parseDouble(longiFormat);
                    height = Double.parseDouble(heightFormat);

//				int lon=(int)(rnLocation.getLongitude()*100000);
//				int lat=(int)(rnLocation.getLatitude()*100000);
//				int height=(int)(rnLocation.getAltitude()*100);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = sdf.format(new Date(rnLocation.getTime()));
                    if (reportStatus.isEmpty()) {
                        reportStatus = "未设置救援信息!";
                    }

                    String str = "救援信息:\r\n"+
                            "位置:(" + lon + "°," + lat + "°," + height + "m)\r\n" +
                            "定位时间:"+time+"\r\n"+
                            "救援内容:"+ reportStatus ;

                    //String str = "经度:"+lon+","+"纬度:"+lat+","+"高度:"+height+","+"时间:"+time+"状态:"+reportStatus;
                    String content = "";
                    //判断发送的文字是否超长,如果超长了

                    int mode = 2;
                    if (mode == 1) {
                        content = str;
                    } else {
                        //对发送内容进行协议封装
                        // TODO: 2017/1/10

//                        char[] chars = new char[]{"F2".getBytes()};
                        //char char1 = 'F2';

                        //SOS格式:F20yyyyyyyllllllhhhhtHHmmsscccccccccc….
                        //SOS格式:F2BxxxxxxxxxxxyyyyyyyllllllhhhhtHHmmssccccccc….
                        //标注：x表示手机号码  y表示经度l表示纬度  h表示高程
                        //t表示位置类型 c表示救援信息(或短信内容)
                        //讨论决定 加一个校验位


                        int lat2 = (int) (lat * 100000);//8
                        int lon2 = (int) (lon * 100000);//7
                        int height2 = (int) (height * 10);//6


                        String relon = "";
                        String relat = "";
                        int nlon = (int)lon;
                        int nlat = (int)lat;
                        if(nlon < 10)
                            relon = String.format("00%d", lon2);
                        else if(nlon < 100)
                            relon = String.format("0%d", lon2);
                        else
                            relon = String.format("%d", lon2);
                        if(nlat <10)
                            relat = String.format("0%d", lat2);
                        else
                            relat = String.format("%d", lat2);



                        String str_height2 = "" + height2;
                        int length = str_height2.length();
                        int toadd = 6 - length;
                        String heightStr = "";
                        switch (toadd) {

                            case 0:
                                heightStr = "" + height2;
                                break;
                            case 1:
                                heightStr = "0" + height2;
                                break;
                            case 2:
                                heightStr = "00" + height2;
                                break;
                            case 3:
                                heightStr = "000" + height2;
                                break;
                            case 4:
                                heightStr = "0000" + height2;
                                break;
                            case 5:
                                heightStr = "00000" + height2;
                                break;
                            case 6:
                                heightStr = "000000" + height2;
                                break;
                        }


                        String title = "F20";
                        String loc = relon + "" + relat + heightStr;
                        String locType = 1 + "";
                        //String locType = "";
                        SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss");
                        String time2 = sdf2.format(new Date(rnLocation.getTime()));

                        String sosInfo = reportStatus;
                        String sosInfoHex = null;
                        try {
                            sosInfoHex = CharUtil.bytesToHexString(sosInfo.getBytes("GBK"));
                            //sosInfoHex = CharUtil.bytesToHexString(sosInfo.getBytes());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        //校验位 骗人的
                        String crc = 31 + "";

                        //1把救援信息转换为16进制值
                        //2然后转换成ASCII码表示。
                        String toSend = title + loc + locType + time2 + sosInfoHex + crc;

                        content = toSend;

                        //除救援信息外，将头特征字到尾特征字ASCII码表示。
                    }


                    //F201162271840066870440.01171322E58D81E4B887E781ABE680A5


                    // 保存到发件箱
                    // 修改 发送内容 不需要 编码处理
                    sava2db(reportSet.getReportNnm(),-1,content,str);

                    reportSet.setReportSOSContent(content);

                    //通知界面更新  发广播
                    Intent mIntent = new Intent();
                    mIntent.putExtra(ReceiverAction.APP_KEY_SMS_RECEIVER, reportSet.getReportNnm());
                    mIntent.setAction(ReceiverAction.APP_ACTION_SMS_NEW_DIALOG);
                    sendOrderedBroadcast(mIntent, null);


                } else {
                    mHandler.sendEmptyMessage(WARN_NO_LOCATION);
                    return;
                }

                break;
            default:
                break;
        }
        if (reportSet.getReportNnm() == null) {
            Toast.makeText(mContext, "请设置发送内容!!", Toast.LENGTH_SHORT).show();
        } else {
            //保存到发件箱

            //发送
            locReportManager.locationReport(reportSet,report);
        }
    }

    /**
     * 保存到发件箱
     *
     */
    private void sava2db(String userid,int priority,String messageContent,String otherMsg) {
        BDMessageInfo info = new BDMessageInfo();
        //通过 号码查询到联系人
        String userName = "";
        String dbUserName = DBhelper.getContactNameFromPhoneBook(mContext, userid);
        if (dbUserName != null) {
            userName = dbUserName;
        } else {
            userName = userid;
        }
        info.setUserName(userName);
        info.setmUserAddress(userid);

        if (-1 == priority && !TextUtils.isEmpty(otherMsg)) {
            info.setMessage(messageContent);
            info.setMessage(otherMsg);
        } else {
            info.setMessage(messageContent);
            info.setMessage(otherMsg);

        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        info.setmSendTime(date);
        BDMessageDatabaseOperation messageOperation = new BDMessageDatabaseOperation(
                mContext);
        messageOperation.insert(info, "1");//发件
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        rnLocation =  GspStatesManager.getInstance().mLocation;
        cardFreq = SharedPreferencesHelper.getSericeFeq();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (oper != null) {
            oper.close();
            oper = null;
        }


//        if (wakeLock != null) {
//            wakeLock.release();
//            wakeLock = null;
//        }
//        unregisterReceiver(rnLocReceiver);
    }

    protected void initSound() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.location);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(0.1f, 0.1f);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    protected void playSoundAndVibrate() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 接收到rn定位广播
     */
//    BroadcastReceiver rnLocReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (ReceiverAction.APP_ACTION_RN_LOCATION.equals(action)) {
                //采用  app中监听 位置信息 不在广播接受其他人提供的位置信息
//                rnLocation = intent.getParcelableExtra(ReceiverAction.APP_KEY_RN_LOCATION);
//                Log.e("LERRYTEST_RN" ,"=========CycleReportService592=========location.getTime()=="+rnLocation.getTime()+"==========="+rnLocation.getLatitude()+"，"+rnLocation.getLongitude());
//            }
//        }
//    };

}
