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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import thd.bd.sms.crashUtils.Constant;
import thd.bd.sms.crashUtils.SharedPreferencesHelper;
import thd.bd.sms.crashUtils.SysUtils;
import thd.bd.sms.crashUtils.WinUtils;
import thd.bd.sms.service.LocationService;

/**
 * @author lerry
 * @time 2019.05.09
 */
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
    //百度地图定位
    private LocationService locationService;
    private static final String TAG = "BaseActivity";

    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;

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

        SharedPreferencesHelper sp = new SharedPreferencesHelper(BaseActivity.this);
        sp.put(Constant.SP_KEY_RN_LOCATION_LAT,mCurrentLat);
        sp.put(Constant.SP_KEY_RN_LOCATION_LON,mCurrentLon);

        Log.e(TAG, "onDestroy: =============mCurrentLat========"+ mCurrentLat+"============"+mCurrentLon);
    }

    /**
     * 定位监听，定位都要用这个
     */
    LocationListener locationListener = new LocationListener() {

        // 位置改变时被调用
        @Override
        public void onLocationChanged(Location location) {

            Log.e("LERRYTEST_MAP", "=========BaseActivity332=======location==" + location.getLatitude() + "," + location.getLongitude());

            if(location!=null){
                if(location.getLatitude()!=0.0 && location.getLongitude()!=0.0){
                    locationService.stop();
                }
                mCurrentLat = location.getLatitude();
                mCurrentLon = location.getLongitude();

                onComLocation(location);
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

        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // -----------location config ------------
        locationService = SMSApplication.getInstance().locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());

        if(isFirstLocation){//如果是第一次登陆
            if (isNetConn) {//如果有网络，就用网络定位，如果没有网络也没有GPS，就只有听天由命了
                locationService.start();
            }
        }else {//如果不是第一次登陆

            SharedPreferencesHelper sp = new SharedPreferencesHelper(BaseActivity.this);

            if(sp.contain(Constant.SP_KEY_RN_LOCATION_LON)){//如果有最后一次登陆经纬度

                Log.e(TAG, "onDestroy: =============mCurrentLat========"+ sp.getSharedPreference(Constant.SP_KEY_RN_LOCATION_LAT, 0.0)
                        +"============"+sp.getSharedPreference(Constant.SP_KEY_RN_LOCATION_LON, 0.0)
                        +"=================="+sp.getSharedPreference(Constant.SP_KEY_RN_LOCATION_LON, 0.0).getClass());

                double lat = (double) sp.getSharedPreference(Constant.SP_KEY_RN_LOCATION_LAT, 0.0);
                double lon = (double) sp.getSharedPreference(Constant.SP_KEY_RN_LOCATION_LON, 0.0);
                Location location = new Location("");
                location.setLatitude(lat);
                location.setLongitude(lon);
                onComLocation(location);
            }else {//如果没有最后一次经纬度信息
                if(isNetConn){//如果有网络
                    locationService.start();
                }
            }

            checkGPSPermission();
        }
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); //注销掉监听
        if(locationService.isStart()){
            locationService.stop(); //停止定位服务
        }
        super.onStop();
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            checkGPSPermission();

            StringBuffer sb = new StringBuffer(256);
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                    sendFirstLocation(location.getLatitude(),location.getLongitude());

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");

                    sendFirstLocation(location.getLatitude(),location.getLongitude());

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");

                    sendFirstLocation(location.getLatitude(),location.getLongitude());

                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
            }

//            Toast.makeText(BaseActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onReceiveLocation: ======388================"+sb.toString() );
        }

    };

    /*
     * 第一次定位用百度网络定位
     */
    private void sendFirstLocation(double lat,double lon){
        isFirstLocation = false;
        Location myLocation = new Location("");
        myLocation.setLatitude(lat);
        myLocation.setLongitude(lon);

        onComLocation(myLocation);
    }
}
