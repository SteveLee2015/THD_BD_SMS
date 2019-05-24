package thd.bd.sms.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.DBLocation;
import thd.bd.sms.bean.GpsLocationEvent;
import thd.bd.sms.bean.MyLocationBean;
import thd.bd.sms.bean.SatelliteInfo;
import thd.bd.sms.utils.CollectionUtils;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.view.CustomSatelliateMap;
import thd.bd.sms.view.CustomSatelliateSnr;
import thd.bd.sms.view.GspStatesManager;

public class StatelliteStatusActivity extends BaseActivity {
    private final static int LOCATION_RESULT = 0x1000,
            GP_SATELLIATE_STATUS = 0x1001, BD_SATELLIATE_STATUS = 0x1002;
    @BindView(R.id.tv_Statellite)
    TextView mStatellite;
    @BindView(R.id.tv_change)
    TextView mTvChange;
    @BindView(R.id.gps_location_status)
    TextView mGPSLocationStatus;
    @BindView(R.id.rl_change_statellite)
    RelativeLayout mRLchangeStatellite;
    @BindView(R.id.gps_map_view)
    CustomSatelliateMap mCustomBDMap;
    @BindView(R.id.gps_location_result)
    TextView mGPSLocationResult;
    @BindView(R.id.gps_location_height_value)
    TextView mGPSLocationHeight;
    @BindView(R.id.gps_snr_view)
    CustomSatelliateSnr mCustomBDSnr;

    protected LocationManager locationManager = null;

    private LinearLayout ll_gps_bd2;

    private int intFlag;

//    SharedPreferences mSharePref;
    List<GpsSatellite> gplist = new ArrayList<>();

    private Unbinder unbinder;
    @Override
    protected int getContentView() {
        return R.layout.activity_statellite_status;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCATION_RESULT:
                    Location mBDRNSSLocation = (Location) msg.obj;
                    if (true) {//???
                        mGPSLocationStatus.setText("状态:已定位");
                        int lon = (int) (mBDRNSSLocation.getLongitude() * 100000);
                        int lat = (int) (mBDRNSSLocation.getLatitude() * 100000);
                        int height = (int) (mBDRNSSLocation.getAltitude() * 100);
                        //mGPSLocationResult.setText("("+lon/100000.0+","+lat/100000.0+","+height/100.0+")");
                        mGPSLocationResult.setText((lon / 100000.0) + " , " + (lat / 100000.0));
                        mGPSLocationHeight.setText((height / 100.0) + "m");
                    } else {
                        mGPSLocationStatus.setText("状态:未定位");
                        mGPSLocationResult.setText("0,0");
                        mGPSLocationHeight.setText(0 + "m");
                    }
                    break;
                case GP_SATELLIATE_STATUS:

                    gplist = (List<GpsSatellite>) msg.obj;

                    Log.e("LERRYTEST_RN", "=========StatelliteStatusActivity102=========gplist=="+gplist.size());
                    //排序
                    //Collections.sort(gplist);
                    showMap(gplist);
                    break;
//                case BD_SATELLIATE_STATUS:
//                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statellite_status);
        unbinder = ButterKnife.bind(this);

        EventBus.getDefault().register(this);
//        mSharePref = getSharedPreferences("BD_BLUETOOTH_PREF", 0);

        locationManager = BaseActivity.locationManager;

//        Intent intent = getIntent();
//        if (intent != null) {
//            intFlag = intent.getIntExtra(Config.FLAG_TAG, -1);
//        }

        intFlag = Config.FLAG_ALL;

        ll_gps_bd2 = (LinearLayout) findViewById(R.id.ll_gps_bd2);

        ll_gps_bd2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch (intFlag) {
            case Config.FLAG_GPS:
                mStatellite.setText("GPS星图");
                break;
            case Config.FLAG_BD:
                mStatellite.setText("北斗2星图");
                break;
            case Config.FLAG_ALL:
                mStatellite.setText("RNSS星图");
                break;

            default:
                break;
        }

    }

    /**
     * 展示数据
     *
     * @param gplist
     */
    private void showMap(List<GpsSatellite> gplist) {
        List newList = CollectionUtils.removeDuplicate(gplist);
        if (mCustomBDMap == null || mCustomBDMap == null) {
            return;
        }
        Log.e("LERRYTEST_RN", "=========StatelliteStatusActivity171=========newList=="+newList.size());
        mCustomBDSnr.showMap(newList);//载噪比
        mCustomBDMap.showMap(newList);//星图
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getGpsSatelliteList(List<GpsSatellite> list){
        gplist = list;

        Log.e("LERRYTEST_RN", "=========StatelliteStatusActivity180=========gplist=="+gplist.size());
        //排序
        //Collections.sort(gplist);
        showMap(gplist);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        locationManager.addGpsStatusListener(mGpslistener);
        gplist.clear();
//        changeTitle();
        //通知更新
        showMap(gplist);
        updateView(null);
//        GspStatesManager.getInstance().addSatellitesListener(mSatellitesListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeGpsStatusListener(mGpslistener);
//        GspStatesManager.getInstance().removeStatellitesListener(mSatellitesListener);
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
                if (ActivityCompat.checkSelfPermission(StatelliteStatusActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Log.e("LERRYTEST_RN", "=========StatelliteStatusActivity228=========event=="+event);
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

                        Message msg = Message.obtain();
                        msg.what = GP_SATELLIATE_STATUS;
                        msg.obj = list;
                        mHandler.sendMessage(msg);

                        Log.e("LERRYTEST_RN", "=========StatelliteStatusActivity264=========list=="+list.size());
                        break;
                    default:
                        break;
                }
            }
        }
    };

    //更新显示内容的方法
    public void updateView(Location location) {
        if (location == null) {
            if (mGPSLocationStatus != null) {
                mGPSLocationStatus.setText("未定位");
            }
            if (mGPSLocationResult != null) {
                mGPSLocationResult.setText("0,0");
            }
            if (mGPSLocationHeight != null) {
                mGPSLocationHeight.setText(0 + "m");
            }

            //清除  最后数据
            gplist.clear();
            //通知更新
            showMap(gplist);

            return;
        }
        Message msg = Message.obtain();
        msg.what = LOCATION_RESULT;
        msg.obj = location;
        mHandler.sendMessage(msg);
    }

    /*private void showGpsModelDialog() {
        final CharSequence[] items = {"北斗GPS混合定位", "单北斗定位", "单GPS定位"};
        int position = mSharePref.getInt("LOCATION_MODEL", 0);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("设置定位模式")
                .setSingleChoiceItems(items, position,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface,
                                    int position) {
                                if (app.isBlueToothModel()) {

                                    mSharePref.edit()
                                            .putInt("LOCATION_MODEL",
                                                    position).commit();
                                    int strategy = 0;
                                    if (position == 0) {// 混合
//									strategy = LocationStrategy.HYBRID_STRATEGY;
                                        app.write("$CFGSYS,H11\r\n".getBytes());
                                    } else if (position == 1) {// 单北斗定位
                                        app.write("$CFGSYS,H10\r\n".getBytes());
                                    } else if (position == 2) {// 单GPS
                                        app.write("$CFGSYS,H01\r\n".getBytes());
//									strategy = LocationStrategy.GPS_ONLY_STRATEGY;
                                    }
                                    app.write("$CFGSAVE,\r\n".getBytes());
                                    GspStatesManager.getInstance().clearData();
                                    changeTitle();
//								manager.setLocationStrategy(strategy);
                                    dialogInterface.dismiss();
                                } else {

                                }
                            }
                        }).create();
        dialog.show();
    }*/

//    private void changeTitle() {
//        int position = mSharePref.getInt("LOCATION_MODEL", 0);
//        switch (position) {
//            case 2:
//                mStatellite.setText("GPS星图");
//                break;
//            case 1:
//                mStatellite.setText("北斗星图");
//                break;
//            case 0:
//                mStatellite.setText("RNSS星图");
//                break;
//
//            default:
//                break;
//        }
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSatlitesStatus(List<SatelliteInfo> list){
//        Log.w("LERRYTEST_RN", "StatelliteStatusActivity361 = " + list.size());
//        mCustomBDSnr.showMapBlue(list);//载噪比
//        mCustomBDMap.showMapBlue(list);//星图
//    }

//    protected void onSatlitesStatus(ArrayList<SatelliteInfo> list) {
//        Log.d(StatelliteStatusActivity.class.getSimpleName(), "listsize = " + list.size());
//        mCustomBDSnr.showMapBlue(list);//载噪比
//        mCustomBDMap.showMapBlue(list);//星图
//    }

//    protected void updateRnss(DBLocation gga) {
////        if (gga.getmState_Indicate() > 0) {
//        mGPSLocationStatus.setText("状态:已定位");
////        } else {
////            mGPSLocationStatus.setText("状态:未定位");
////        }
//        mGPSLocationResult.setText(String.format("%.6f", gga.getLongitude()) + " , " + String.format("%.6f", gga.getLatitude()));
//        if (gga.getAltitude() > 0) {
//            mGPSLocationHeight.setText(gga.getAltitude() + gga.mHeightUnit);
//        } else {
//            mGPSLocationHeight.setText("");
//        }
//    }

//    GspStatesManager.ISatellitesListener mSatellitesListener = new GspStatesManager.ISatellitesListener() {
//        @Override
//        public void onSatelliteInfo(ArrayList<SatelliteInfo> list) {
//            onSatlitesStatus(list);
//        }
//
//        @Override
//        public void onUpdateRnss(DBLocation gga) {
//            updateRnss(gga);
//        }
//    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLatlng(MyLocationBean myLocationBean){
        if (myLocationBean != null) {

            Location location = new Location("");
            location.setLatitude(myLocationBean.getLatitude());
            location.setLongitude(myLocationBean.getLongitude());
            updateView(location);
        }
        Log.e("LERRYTEST_MAP", "=========StatelliteStatusActivity400=======location==" + myLocationBean.getLatitude() + "," + myLocationBean.getLongitude());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(GpsLocationEvent event){
        if(event != null && event.mLocation != null){
            updateView(event.mLocation);
            Log.e("LERRYTEST_MAP", "=========StatelliteStatusActivity407=======event.mLocation==" + event.mLocation.getLatitude() + "," + event.mLocation.getLongitude());
        }

    }

    @OnClick({R.id.tv_change, R.id.rl_change_statellite})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_change_statellite:
            case R.id.tv_change:
//                if (app.isBlueToothModel()) {
//                    showGpsModelDialog();
//                } else {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
//                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onComLocation(Location location) {
        super.onComLocation(location);

        Log.e("LERRYTEST_MAP", "=========StatelliteStatusActivity440=======location==" + location.getLatitude() + "," + location.getLongitude());
    }
}
