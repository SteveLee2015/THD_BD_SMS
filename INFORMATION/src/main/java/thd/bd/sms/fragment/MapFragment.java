package thd.bd.sms.fragment;

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
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import thd.bd.sms.R;
import thd.bd.sms.activity.BSIActivity;
import thd.bd.sms.bean.MyLocationBean;

public class MapFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "MapFragment";

    @BindView(R.id.bmapView)
    MapView bmapView;
    @BindView(R.id.main_my_loction)
    ImageView mainMyLoction;
    @BindView(R.id.main_bsi_btn)
    Button mainBsiBtn;
    Unbinder unbinder;
    private View view;

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
        mBaiduMap = bmapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);

        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_geo);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, false, mCurrentMarker,
                accuracyCircleFillColor, accuracyCircleStrokeColor));

        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);//获取传感器管理服务
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLatlng(MyLocationBean myLocationBean){
        myLocation(myLocationBean.getLatitude(),myLocationBean.getLongitude());
        mCurrentLat = myLocationBean.getLatitude();
        mCurrentLon = myLocationBean.getLongitude();

//        Log.e(TAG, "getLatlng: ================"+ mCurrentLat+","+mCurrentLon);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 与onCreateView相对应,当该Fragment的视图被移除时调用
        unbinder.unbind();
        bmapView.onDestroy();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        bmapView.onDestroy();
        bmapView = null;
        EventBus.getDefault().unregister(this);
    }

    private void myLocation(double latitude,double longitude) {

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


    @OnClick({R.id.main_my_loction,R.id.main_bsi_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_my_loction:
                myLocation(mCurrentLat, mCurrentLon);
                break;
            case R.id.main_bsi_btn:
                Intent intent = new Intent(getActivity(),BSIActivity.class);
                startActivity(intent);
                break;

        }
    }
}
