package thd.bd.sms.application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.BDBeam;
import android.location.BDEventListener;
import android.location.BDLocation;
import android.location.BDLocationReport;
import android.location.BDMessageInfo;
import android.location.BDUnknownException;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import thd.bd.sms.R;
import thd.bd.sms.activity.MainActivity;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.BDCache;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.bean.DBLocation;
import thd.bd.sms.bean.FriendsLocation;
import thd.bd.sms.bean.GpsLocationEvent;
import thd.bd.sms.bean.SatelliteInfo;
import thd.bd.sms.database.BDMessageDatabaseOperation;
import thd.bd.sms.database.FriendsLocationDatabaseOperation;
import thd.bd.sms.database.RDCacheOperation;
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
import thd.bd.sms.view.GspStatesManager;


public class SMSApplication extends Application {

    public Context appContext;
    private static SMSApplication smsApplication;
    public boolean openCrash = true; // 关闭或打开 crah重启
    public BDCmdManager bdCmdManager;
    public static LocationService locationService;
    public static LocationManager locationManager;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private RDCacheOperation cacheOperation;

    private static final String TAG = "SMSApplication";

    public static SMSApplication getInstance() {
        if (smsApplication != null) {
            return smsApplication;
        } else {
            smsApplication = new SMSApplication();
        }

        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        appContext = this;
//        bdCmdManager = BDCmdManager.getInstance(appContext);

        Log.e(TAG, "onCreate: ==========开启后台服务啦。。。。");

        //上线的时候打开该代码
        if (openCrash) {
            CrashHandler.newInstance().init(appContext);
        }
        //第三方日志收集器
        LogManager.getManager(getApplicationContext()).registerCrashHandler();

        SharedPreferencesHelper.init(this);

        initMap();

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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
//        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        locationManager.addGpsStatusListener(mGpslistener);
        GspStatesManager.getInstance().addSatellitesListener(mSatellitesListener);
        GspStatesManager.getInstance().addLocationListener(mRnLocationlistener);


        locationService.start();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        //第三方日志反注册
        LogManager.getManager(getApplicationContext()).unregisterCrashHandler();
        BDCmdManager.getInstance(this).onDestroy();

        locationManager.removeUpdates(locationListener);
        locationManager.removeGpsStatusListener(mGpslistener);
        GspStatesManager.getInstance().removeStatellitesListener(mSatellitesListener);
        GspStatesManager.getInstance().removeLocationListener(mRnLocationlistener);

        if (mCurrentLat != 0.0 && mCurrentLon != 0.0) {
            SharedPreferencesHelper.put(Constant.SP_KEY_RN_LOCATION_LAT, mCurrentLat);
            SharedPreferencesHelper.put(Constant.SP_KEY_RN_LOCATION_LON, mCurrentLon);
        }

//        SharedPreferencesHelper.put(Constant.SP_RD_REPORT_STATE, false);
//        SharedPreferencesHelper.put(Constant.SP_RN_REPORT_STATE, false);
        /*unRegisterReceiver();*/
//        bdCmdManager.onDestroy();
        Log.e(TAG, "onTrimMemory: ==========关闭后台服务啦。。。。");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    /**
     * 在7寸屏中 没有改功能
     * 获得RNSS卫星数据的监听器 1.如果当前单北斗模式，则每次获得的数据都是北斗卫星的数据。
     * 对北斗卫星数据进行遍历，获得当前北斗参与定位的卫星数目并显示出来。 2.如果当前单GPS模式，则每次获得的数据都是GPS卫星的数据。
     * 对GPS卫星数据进行遍历，获得当前GPS参与定位的卫星数目并显示出来。 3.如果当前是混合模式，则一次获得GPS卫星数据，一次获得北斗卫星数据。
     * 先解析GPS卫星数据，获得GPS参与定位的卫星数目，然后解析北斗卫星 数据获得北斗参与定位的卫星数目,把两次解析出来的卫星数目增加并显示出来。
     */
    protected GpsStatus.Listener mGpslistener = new GpsStatus.Listener() {
        GpsStatus mGpsStatus;

        @Override
        public void onGpsStatusChanged(int event) {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
//                Log.e("LERRYTEST_RN", "=========BaseActivity145=========event=="+event);
                mGpsStatus = locationManager.getGpsStatus(null);
                switch (event) {
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                        if (app.isBlueToothModel()) return;
                        Iterable<GpsSatellite> satellites = mGpsStatus.getSatellites();
                        Iterator<GpsSatellite> it = satellites.iterator();
                        List<GpsSatellite> list = new ArrayList<GpsSatellite>();
                        while (it.hasNext()) {
                            GpsSatellite satellite = it.next();
                            // add by llg
                            if (satellite.usedInFix()) {
                                //已经定位 卫星

                            }
                            list.add(satellite);
                        }

                        EventBus.getDefault().post(list);
//                        Log.e("LERRYTEST_RN", "=========BaseActivity164=========list=="+list.size());
//                        Message msg = Message.obtain();
//                        msg.what = GP_SATELLIATE_STATUS;
//                        msg.obj = list;
//                        mHandler.sendMessage(msg);

                        break;
                    default:
                        break;
                }
            }
        }
    };


    GspStatesManager.ISatellitesListener mSatellitesListener = new GspStatesManager.ISatellitesListener() {
        @Override
        public void onSatelliteInfo(ArrayList<SatelliteInfo> list) {
//            onSatlitesStatus(list);
//            EventBus.getDefault().post(list);
        }

        @Override
        public void onUpdateRnss(DBLocation gga) {
            Location location = new Location("");
            location.setLatitude(gga.getLatitude());
            location.setLongitude(gga.getLongitude());
            location.setAltitude(gga.getAltitude());
            location.setTime(gga.getTime());
//            onComLocation(location);

//            Toast.makeText(appContext,"onUpdateRnss.....lat="+location.getLatitude()+"===lng="+location.getLongitude(),Toast.LENGTH_SHORT).show();

//            Log.e(TAG, "LERRYTEST_MAP: ===============有权限了，去放肆吧=GspStatesManager.ISatellitesListener========");

        }
    };

    GspStatesManager.ILocationListener mRnLocationlistener = new GspStatesManager.ILocationListener() {
        @Override
        public void onLocationChanged(DBLocation location) {

            Location location1 = new Location("");
            location1.setLatitude(location.getLatitude());
            location1.setLongitude(location.getLongitude());
            location1.setAltitude(location.getAltitude());
            location1.setTime(location.getTime());
//            onComLocation(location1);
//            Log.e(TAG, "LERRYTEST_MAP: ===============有权限了，去放肆吧=GspStatesManager.ILocationListener========");
//            Log.e("LERRYTEST_MAP", "=========SMSApplication292=======location==" + location.getLatitude() + "," + location.getLongitude());
//            Toast.makeText(appContext,"ILocationListener.....lat="+location.getLatitude()+"===lng="+location.getLongitude(),Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 定位监听，定位都要用这个
     */
    LocationListener locationListener = new LocationListener() {

        // 位置改变时被调用
        @Override
        public void onLocationChanged(Location location) {


            if (location != null) {
                if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                    locationService.stop();
                }
                mCurrentLat = location.getLatitude();
                mCurrentLon = location.getLongitude();

//                onComLocation(location);
                EventBus.getDefault().post(new GpsLocationEvent(location));
//                Log.e("LERRYTEST_MAP", "=========SMSApplication318=======location==" + location.getLatitude() + "," + location.getLongitude());
//                Toast.makeText(appContext,"onLocationChanged.....lat="+location.getLatitude()+"===lng="+location.getLongitude(),Toast.LENGTH_SHORT).show();
            }

        }

        // 用户禁用具有定位功能的硬件时被调用
        @Override
        public void onProviderDisabled(String provider) {
            //updateView(null);

        }

        // 用户启用具有定位功能的硬件时被调用
        @Override
        public void onProviderEnabled(String provider) {

        }

        // 定位功能硬件状态改变时被调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
//                    Toast.makeText(appContext, "当前GPS状态为可见状态", Toast.LENGTH_SHORT).show();
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
//                    Toast.makeText(appContext, "当前GPS状态为服务区外状态", Toast.LENGTH_SHORT).show();
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    Toast.makeText(appContext, "当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
