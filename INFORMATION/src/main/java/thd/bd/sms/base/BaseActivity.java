package thd.bd.sms.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.BDParameterException;
import android.location.BDUnknownException;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.thd.cmd.manager.BDCmdManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import thd.bd.sms.application.SMSApplication;
import thd.bd.sms.bean.DBLocation;
import thd.bd.sms.bean.GpsLocationEvent;
import thd.bd.sms.bean.SatelliteInfo;
import thd.bd.sms.service.CoreService;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.service.LocationService;
import thd.bd.sms.view.GspStatesManager;

/**
 * @author lerry
 * @time 2019.05.09
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;

    private static final String TAG = "BaseActivity";

    /*//当Android6.0系统以上时，动态获取权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET};
    //权限的标志
    private static final int PERMISSION_CODES = 1001;
    private boolean permissionGranted = true;

    public static LocationManager locationManager;
    private boolean isNetConn = false;
    private boolean isFirstLocation = true;
    //百度地图定位
    private LocationService locationService;
    private static final String TAG = "BaseActivity";

    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;*/

    private final int BD_BSI_MESSAGE = 5;
    android.os.PowerManager.WakeLock wakeLock;
    private PowerManager pm = null;

    private MyConn myConn = null;
    private CoreService coreService;
    private boolean isBind = false;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(getContentView());


        WinUtils.hiddenKeyBoard(this);

        WindowManager m = this.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(metrics);
        Utils.setmDisplayMetrics(metrics);

        coreService = CoreService.getInstance();
        myConn = new MyConn();

        // 绑定服务 后台运行短报文等服务
        boolean isCoreServiceStart = SysUtils.isServiceRunning(this,coreService.getClass().getName());

        if(!isCoreServiceStart){
            Intent intent2 = new Intent(this, coreService.getClass());
            startService(intent2);
            bindService(intent2, myConn, 0);
            Log.e(TAG, "LERRY_SERVICE: =================BaseActivity123=======开启CoreService服务====");
        }



        //保持cpu一直运行，不管屏幕是否黑屏
        pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();

        unbinder = ButterKnife.bind(this);

    }


    abstract protected int getContentView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();


        /*boolean isCoreServiceStart = SysUtils.isServiceRunning(this,coreService.getClass().getName());
        if(isCoreServiceStart){
            Intent mIntent = new Intent();
            mIntent.setClass(this, coreService.getClass());
            Log.e(TAG, "LERRY_SERVICE: =================BaseActivity162=======关闭CoreService服务====");

            if(isBind){
                this.unbindService(myConn);
            }
            this.stopService(mIntent);
        }*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable(){
            public void run() {

                BDCmdManager bdCmdManager = BDCmdManager.getInstance(BaseActivity.this);

                try {
                    bdCmdManager.sendRMOCmdBDV21("BSI", 2, 1);
                    Log.e(TAG, "LERRY_BSI==================发送波束语句=================");
                } catch (BDUnknownException e) {
                    e.printStackTrace();
                } catch (BDParameterException e) {
                    e.printStackTrace();
                }
            }
        }, 7000);   //5秒
    }

    @Override
    protected void onStart() {
        super.onStart();

        Message message = new Message();
        message.what = BD_BSI_MESSAGE;

    }

    @Override
    protected void onStop() {

        super.onStop();
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
}
