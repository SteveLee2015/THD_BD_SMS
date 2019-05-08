package thd.bd.sms.Application;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import thd.bd.sms.crashUtils.Constant;
import thd.bd.sms.crashUtils.SharedPreferencesHelper;
import thd.bd.sms.crashUtils.SysUtils;
import thd.bd.sms.crashUtils.WinUtils;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;

    //当Android6.0系统以上时，动态获取权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET};
    //权限的标志
    private static final int PERMISSION_CODES = 1001;
    private boolean permissionGranted = true;

    private LocationManager locationManager;
    private boolean isNetConn = false;
    private boolean isFirstLocation = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        WinUtils.hiddenKeyBoard(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        unbinder = ButterKnife.bind(this);

        initGPS();
        //判断网络是否连接
        isNetConn = SysUtils.isConn(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermission();
        }


    }

    abstract protected int getContentView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }

    /**
     * 定位监听，定位都要用这个
     */
    LocationListener locationListener = new LocationListener() {

        // 位置改变时被调用
        @Override
        public void onLocationChanged(Location location) {

            Log.e("LERRYTEST_MAP", "=========BaseActivity332=======location==" + location.getLatitude() + "," + location.getLongitude());

            if(isFirstLocation){
                if(location.getLatitude()!=0.0 && location.getLatitude()!=0.0){
                    SharedPreferencesHelper sp = new SharedPreferencesHelper(BaseActivity.this);
                    sp.put(Constant.SP_KEY_RN_LOCATION_LAT,(float) location.getLatitude());
                    sp.put(Constant.SP_KEY_RN_LOCATION_LAT,(float) location.getLatitude());
                    isFirstLocation = false;
                }
            }

            onComLocation(location);

        }

        // 用户禁用具有定位功能的硬件时被调用
        @Override
        public void onProviderDisabled(String provider) {
            //updateView(null);

        }

        // 用户启用具有定位功能的硬件时被调用
        @Override
        public void onProviderEnabled(String provider) {
//            Location location = locationManager.getLastKnownLocation(provider);
//            Intent intent = new Intent();
//            intent.setAction(ReceiverAction.APP_ACTION_RN_LOCATION);
//            intent.putExtra(ReceiverAction.APP_KEY_RN_LOCATION, location);
//            sendBroadcast(intent);

        }

        // 定位功能硬件状态改变时被调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
//				Toast.makeText(getActivity(), "当前GPS状态为可见状态", Toast.LENGTH_SHORT)
//						.show();
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
//				Toast.makeText(getActivity(), "当前GPS状态为服务区外状态", Toast.LENGTH_SHORT)
//						.show();
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//				Toast.makeText(getActivity(), "当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT)
//						.show();
                    break;
            }
        }
    };


    public void onComLocation(Location location) {

    }

    /**
     * 动态的进行权限请求
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void requestPermission(){
        List<String> p = new ArrayList<>();
        for(String permission :PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                p.add(permission);
            }
        }
        if(p.size() > 0){
            requestPermissions(p.toArray(new String[p.size()]),PERMISSION_CODES);
        }else {
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODES:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    permissionGranted = false;
                }else {
                    permissionGranted = true;
                }
//                checkGPSPermission();
                break;
        }
    }

    private void checkGPSPermission() {
        if (permissionGranted) {//开了定位服务
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，申请权限。
//                        Toast.makeText(getActivity(), "没有权限", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "请先打开GPS服务！", Toast.LENGTH_SHORT).show();
            } else {
                // 有权限了，去放肆吧。
//                        Toast.makeText(getActivity(), "有权限", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            }
        } else {
            Toast.makeText(this, "系统检测到未开启GPS定位服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }

    private void initGPS() {
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT).show();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("请先打开GPS,以方便定位，若不设置，下次将再次出现！");
            dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {

            // 弹出Toast
//          Toast.makeText(this, "GPS is ready",Toast.LENGTH_LONG).show();
//          // 弹出对话框
//          new AlertDialog.Builder(this).setMessage("GPS is ready").setPositiveButton("OK", null).show();
        }
    }

    @Override
    protected void onResume() {
        if(isFirstLocation){
            if (isNetConn) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            }
        }else {
            SharedPreferencesHelper sp = new SharedPreferencesHelper(BaseActivity.this);
            double lat = (double) sp.getSharedPreference(Constant.SP_KEY_RN_LOCATION_LAT, 0.0);
            double lon = (double) sp.getSharedPreference(Constant.SP_KEY_RN_LOCATION_LON, 0.0);
            Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lon);

            onComLocation(location);

            checkGPSPermission();
        }


        super.onResume();
    }
}
