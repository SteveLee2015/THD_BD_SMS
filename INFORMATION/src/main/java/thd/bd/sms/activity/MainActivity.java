package thd.bd.sms.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.thd.cmd.manager.BDCmdManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.application.SMSApplication;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.DBLocation;
import thd.bd.sms.bean.GpsLocationEvent;
import thd.bd.sms.bean.MyLocationBean;
import thd.bd.sms.bean.SatelliteInfo;
import thd.bd.sms.fragment.CommunicationFragment;
import thd.bd.sms.fragment.FriendsFragment;
import thd.bd.sms.fragment.MapFragment;
import thd.bd.sms.fragment.SettingFragment;
import thd.bd.sms.service.LocationService;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.view.GspStatesManager;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.main_map_layout)
    LinearLayout mainMapLayout;
    @BindView(R.id.main_communication_layout)
    LinearLayout mainCommunicationLayout;
    @BindView(R.id.main_friends_layout)
    LinearLayout mainFriendsLayout;
    @BindView(R.id.main_setting_layout)
    LinearLayout mainSettingLayout;
    @BindView(R.id.main_map_img)
    ImageView mainMapImg;
    @BindView(R.id.main_communication_img)
    ImageView mainCommunicationImg;
    @BindView(R.id.main_friends_img)
    ImageView mainFriendsImg;
    @BindView(R.id.main_setting_img)
    ImageView mainSettingImg;
    @BindView(R.id.main_center_btn)
    ImageView mainCenterBtn;

    private MapFragment mapFragment;
    private CommunicationFragment communicationFragment;
    private FriendsFragment friendsFragment;
    private SettingFragment settingFragment;

    /**
     * 记录当前Activity显示的fragment
     */
    private Fragment mContent;

    /**
     * FragmentActivity向Fragment传递数据：创建个Bundle数据包，FragmentActivity中Fragment对象调用setArguments(Bundle bundle)方法即可；
     * Fragment接收从FragmentActivity传来的数据：调用getArguments()接收数据包，返回Bundle对象；
     */
    private Bundle bundle;
    private List<Fragment> fragmentList = new ArrayList<>();

    //当Android6.0系统以上时，动态获取权限
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

    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        initGPS();
        //判断网络是否连接
        isNetConn = SysUtils.isConn(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }

        initContent();

    }


    private void initContent() {

        mainMapImg.setImageResource(R.mipmap.map_clicked);
        mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
        mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
        mainSettingImg.setImageResource(R.mipmap.setting_unclick);

        mapFragment = new MapFragment();
        communicationFragment = new CommunicationFragment();
        friendsFragment = new FriendsFragment();
        settingFragment = new SettingFragment();

        //加载第一个fragment界面
        mContent = mapFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.main_bottom_fragmentLayout, mapFragment).commit();

        bundle = new Bundle();
//        bundle.putDouble("lat",);
//        bundle.putDouble("lng",);
        mContent.setArguments(bundle);

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

        /*if(isFirstLocation){//如果是第一次登陆
//            if (isNetConn) {//如果有网络，就用网络定位，如果没有网络也没有GPS，就只有听天由命了
                locationService.start();
//            }
        }else {//如果不是第一次登陆

            if(SharedPreferencesHelper.contain(Constant.SP_KEY_RN_LOCATION_LON)){//如果有最后一次登陆经纬度

                Log.e(TAG, "onStart: =============mCurrentLat========"+ SharedPreferencesHelper.getLastLat()
                        +"============"+SharedPreferencesHelper.getLastLng());


                double lat = (double)SharedPreferencesHelper.getLastLat();
                double lon = (double)SharedPreferencesHelper.getLastLng();
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
        }*/

        checkGPSPermission();
        locationService.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.unregisterListener(mListener); //注销掉监听
        if(locationService.isStart()){
            locationService.stop(); //停止定位服务
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        locationManager.removeGpsStatusListener(mGpslistener);
        GspStatesManager.getInstance().removeStatellitesListener(mSatellitesListener);
        GspStatesManager.getInstance().removeLocationListener(mRnLocationlistener);

        if (mCurrentLat != 0.0 && mCurrentLon != 0.0) {
            SharedPreferencesHelper.put(Constant.SP_KEY_RN_LOCATION_LAT, mCurrentLat);
            SharedPreferencesHelper.put(Constant.SP_KEY_RN_LOCATION_LON, mCurrentLon);
        }

    }

    public void onComLocation(Location location) {

        MyLocationBean myLocationBean = new MyLocationBean();
        myLocationBean.setLatitude(location.getLatitude());
        myLocationBean.setLongitude(location.getLongitude());
        myLocationBean.setTime(location.getTime());
        myLocationBean.setAltitude(location.getAltitude());

//        Log.e("LERRYTEST_MAP", "=========MainActivity115=======location==" + location.getLatitude() + "," + location.getLongitude());

//        Toast.makeText(MainActivity.this,"onComLocation....lat="+location.getLatitude()+"===lng="+location.getLongitude(),Toast.LENGTH_SHORT).show();


        EventBus.getDefault().post(myLocationBean);

//        EventBus.getDefault().post(new GpsLocationEvent(location));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick({R.id.main_map_layout, R.id.main_communication_layout, R.id.main_friends_layout,
            R.id.main_setting_layout, R.id.main_center_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_map_layout:
                mainMapImg.setImageResource(R.mipmap.map_clicked);
                mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
                mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
                mainSettingImg.setImageResource(R.mipmap.setting_unclick);

                switchContent_keep(mContent, mapFragment, 0);
                break;

            case R.id.main_communication_layout:
                mainMapImg.setImageResource(R.mipmap.map_unclick);
                mainCommunicationImg.setImageResource(R.mipmap.communication_clicked);
                mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
                mainSettingImg.setImageResource(R.mipmap.setting_unclick);

                switchContent_keep(mContent, communicationFragment, 1);
                break;

            case R.id.main_friends_layout:
                mainMapImg.setImageResource(R.mipmap.map_unclick);
                mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
                mainFriendsImg.setImageResource(R.mipmap.friends_clicked);
                mainSettingImg.setImageResource(R.mipmap.setting_unclick);

                switchContent_keep(mContent, friendsFragment, 2);
                break;

            case R.id.main_setting_layout:
                mainMapImg.setImageResource(R.mipmap.map_unclick);
                mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
                mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
                mainSettingImg.setImageResource(R.mipmap.setting_clicked);

                switchContent_keep(mContent, settingFragment, 3);
                break;

            case R.id.main_center_btn:
//                Intent intent = new Intent(MainActivity.this, MainCenterActivity.class);
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, mainCenterBtn, "shareNames").toBundle());

//                Intent intent = new Intent(MainActivity.this, MainCenterActivity.class);
//                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,mainCenterBtn, "shareNames");//与xml文件对应
//                ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());

                /**
                 * 在调用了startActivity方法之后立即调用overridePendingTransition方法
                 */
                Intent intent = new Intent(MainActivity.this, MainCenterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pickerview_slide_in_bottom, R.anim.pickerview_slide_out_bottom);

//                startActivity(new Intent(this, MainCenterActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

                break;
        }
    }

    /**
     * fragment切换，不保留之前的状态，每次打开都重新加载
     */
    private void switchContent(Fragment to, int i) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_bottom_fragmentLayout, to);
//        bundle.putString("page",pages[i]);
//        bundle.putString("data",data);
        to.setArguments(bundle);
    }

    /**
     * fragment切换，保留之前的状态，每次打开都不会重新加载
     */
    private void switchContent_keep(Fragment from, Fragment to, int i) {

        if (from != to) {
            mContent = to;

            //先判断是否被add过
            if (!to.isAdded()) {
//                bundle.putString("page",pages[i]);
//                bundle.putString("data",data);
//                Log.i(TAG,"switchContent_keep data= "+data);
                to.setArguments(bundle);

                fragmentList.add(to);

                Log.i(TAG, "MainActivity255: =======未添加过该fragment，添加并隐藏上一个========");
                // 隐藏当前的fragment，add下一个fragment到Activity中
                getSupportFragmentManager().beginTransaction().hide(from).add(R.id.main_bottom_fragmentLayout, to).commit();
            } else {
                Log.i(TAG, "MainActivity258: =======添加过该fragment要隐藏了========");
                // 隐藏当前的fragment，显示下一个fragment
                getSupportFragmentManager().beginTransaction().hide(from).show(to).commit();
            }
        }
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次跳转到后台运行", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if (isTaskRoot()) {
                    moveTaskToBack(false);
                }
            }
            return true;

        }


        return super.onKeyDown(keyCode, event);
    }

    /**
     * 动态的进行权限请求
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void requestPermission() {
        List<String> p = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                p.add(permission);
            }
        }
        if (p.size() > 0) {
            requestPermissions(p.toArray(new String[p.size()]), PERMISSION_CODES);
        } else {
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODES:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    permissionGranted = false;
                } else {
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
//                Log.e(TAG, "LERRYTEST_MAP: ===============有权限了，去放肆吧=========");
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
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            onComLocation(location);
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
            onComLocation(location1);
//            Log.e(TAG, "LERRYTEST_MAP: ===============有权限了，去放肆吧=GspStatesManager.ILocationListener========");
//            Log.e("LERRYTEST_MAP", "=========MainActivity583=======location==" + location.getLatitude() + "," + location.getLongitude());
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

                onComLocation(location);
                EventBus.getDefault().post(new GpsLocationEvent(location));
//                Log.e("LERRYTEST_MAP", "=========MainActivity606=======location==" + location.getLatitude() + "," + location.getLongitude());
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
//                    Toast.makeText(MainActivity.this, "当前GPS状态为可见状态", Toast.LENGTH_SHORT).show();
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
//                    Toast.makeText(MainActivity.this, "当前GPS状态为服务区外状态", Toast.LENGTH_SHORT)
//                            .show();
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    Toast.makeText(MainActivity.this, "当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT)
//                            .show();
                    break;
            }
        }
    };


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

                if(location.getCity()!=null && !"".equals(location.getCity())){
                    SharedPreferencesHelper.put(Constant.SP_KEY_CITY,location.getCity());
                }else {
                    SharedPreferencesHelper.put(Constant.SP_KEY_CITY,"");
                }

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

        if(isFirstLocation){
            Location myLocation = new Location("");
            myLocation.setLatitude(lat);
            myLocation.setLongitude(lon);

            onComLocation(myLocation);
        }

        isFirstLocation = false;
    }


}
