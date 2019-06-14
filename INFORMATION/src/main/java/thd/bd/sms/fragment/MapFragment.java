package thd.bd.sms.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import thd.bd.sms.R;
import thd.bd.sms.activity.MapOfflineActivity;
import thd.bd.sms.bean.MyLocationBean;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.view.CommomDialogCommon;

public class MapFragment extends Fragment implements SensorEventListener, MKOfflineMapListener, BaiduMap.OnMapLongClickListener {

    private static final String TAG = "MapFragment";

    @BindView(R.id.bmapView)
    MapView bmapView;
    @BindView(R.id.main_my_loction)
    ImageButton mainMyLoction;
    Unbinder unbinder;
    @BindView(R.id.main_offline_img)
    ImageButton mainOfflineImg;
    @BindView(R.id.main_input_latlng_img)
    ImageButton mainInputLatlngImg;
    private View view;

    private MKOfflineMap mOffline = null;

//    private MKOfflineMap mOffline = null;

    private BaiduMap mBaiduMap;
    private BitmapDescriptor mCurrentMarker;
    private final int accuracyCircleFillColor = 0xAA99FF99;//范围内充颜色
    private final int accuracyCircleStrokeColor = 0xAA33CCFF;//范围边框
    private MyLocationData locData;
    //传感器
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        initMap();


        return view;

    }

    private void initMap() {
        mOffline = new MKOfflineMap();
        mOffline.init(this);

        mBaiduMap = bmapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //实例化UiSettings类对象
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        //通过设置enable为true或false 选择是否显示指南针
        mUiSettings.setCompassEnabled(true);

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);

        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_geo);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, false, mCurrentMarker,
                accuracyCircleFillColor, accuracyCircleStrokeColor));

        mBaiduMap.setOnMapLongClickListener(this);

        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);//获取传感器管理服务
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLatlng(MyLocationBean myLocationBean) {
        myLocation(myLocationBean.getLatitude(), myLocationBean.getLongitude());
        mCurrentLat = myLocationBean.getLatitude();
        mCurrentLon = myLocationBean.getLongitude();

//        Log.e(TAG, "getLatlng: ================"+ mCurrentLat+","+mCurrentLon);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 与onCreateView相对应,当该Fragment的视图被移除时调用
        unbinder.unbind();
        if (bmapView != null) {
            bmapView.onDestroy();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        bmapView = null;
        EventBus.getDefault().unregister(this);
        /**
         * 退出时，销毁离线地图模块
         */
//        mOffline.destroy();
    }

    private void myLocation(double latitude, double longitude) {

        mBaiduMap.setMyLocationData(locData);

        locData = new MyLocationData.Builder()
                .accuracy(40.0f)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection)
                .latitude(latitude)
                .longitude(longitude).build();
        mBaiduMap.setMyLocationData(locData);


//        Log.e("LERRYTEST_MAP", "=========MapFragment=======location==" + latitude + "," + longitude);
//        Log.e("LERRYTEST_MAP1", "============location=======mCurrentDirection==" +mCurrentDirection);
    }


    @Override
    public void onResume() {
        bmapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    @Override
    public void onStop() {
        super.onStop();
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
//            locData = new MyLocationData.Builder()
//                    .accuracy(40.0f)
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(mCurrentDirection).latitude(mCurrentLat)
//                    .longitude(mCurrentLon).build();
//            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
//        Log.e("LERRYTEST_MAP", "============location=======mCurrentDirection==" +mCurrentDirection);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPause() {
        bmapView.onPause();
        super.onPause();
    }


    @OnClick({R.id.main_my_loction, R.id.main_offline_img,R.id.main_input_latlng_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_my_loction:
                myLocation(mCurrentLat, mCurrentLon);
                break;
            case R.id.main_offline_img:
                Intent intent = new Intent(getActivity(), MapOfflineActivity.class);
                startActivity(intent);
                break;

            case R.id.main_input_latlng_img:
                openLatlonDialog();
                break;

        }
    }

    private void openLatlonDialog() {
        new CommomDialogCommon(getActivity(), R.style.dialog_aa, true, new CommomDialogCommon.OnDaoHangListener() {
            @Override
            public void onClick(Dialog dialog,double lat,double lon) {

                if(lat<0 || lat>90.0){
                    Toast.makeText(getContext(),"请输入正确的纬度",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(lon<0 || lon>180.0){
                    Toast.makeText(getContext(),"请输入正确的经度",Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();
                SysUtils.goToBaiduMap(getActivity(),lat,lon,"手动输入经纬度");
            }
        }).setMessage("请输入要跳转的坐标").setTitle("手动输入坐标导航").setNegativeButton("导航").show();
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
//        type - 事件类型: MKOfflineMap.TYPE_NEW_OFFLINE, MKOfflineMap.TYPE_DOWNLOAD_UPDATE, MKOfflineMap.TYPE_VER_UPDATE.
//        state - 事件状态: 当type为TYPE_NEW_OFFLINE时，表示新安装的离线地图数目. 当type为TYPE_DOWNLOAD_UPDATE时，表示更新的城市ID.


    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        new CommomDialogCommon(getActivity(), R.style.dialog_aa, "地图选点坐标为：\n经度：" + latLng.longitude + "\n纬度：" + latLng.latitude + "\n\n\n是否导航到此？", new CommomDialogCommon.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog) {
                SysUtils.goToBaiduMap(getActivity(), latLng.latitude, latLng.longitude, "地图选点");
            }
        }).show();
    }

}
