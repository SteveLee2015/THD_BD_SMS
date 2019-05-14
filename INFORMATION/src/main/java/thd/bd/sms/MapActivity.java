package thd.bd.sms;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.baidu.mapapi.model.LatLng;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.Application.BaseActivity;
import thd.bd.sms.activity.BSIActivity;
import thd.bd.sms.activity.CardInfoActivity;

public class MapActivity extends BaseActivity implements SensorEventListener {


    @BindView(R.id.bmapView)
    MapView bmapView;
    @BindView(R.id.main_my_loction)
    ImageView mainMyLoction;
    @BindView(R.id.main_bsi_btn)
    Button mainBsiBtn;
    @BindView(R.id.main_cardinfo_btn)
    Button mainCardinfoBtn;

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
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        initMap();
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

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void onComLocation(Location location) {
        super.onComLocation(location);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation(latLng);
        mCurrentLat = location.getLatitude();
        mCurrentLon = location.getLongitude();

    }

    private void myLocation(LatLng myLocation) {

        mBaiduMap.setMyLocationData(locData);

        locData = new MyLocationData.Builder()
                .accuracy(40.0f)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection)
                .latitude(myLocation.latitude)
                .longitude(myLocation.longitude).build();
        mBaiduMap.setMyLocationData(locData);


        Log.e("LERRYTEST_MAP", "=========MapActivity113=======location==" + myLocation.latitude + "," + myLocation.longitude);
//        Log.e("LERRYTEST_MAP1", "============location=======mCurrentDirection==" +mCurrentDirection);
    }

    @Override
    protected void onResume() {
        bmapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
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
    protected void onDestroy() {
        bmapView.onDestroy();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        bmapView.onDestroy();
        bmapView = null;
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        bmapView.onPause();
        super.onPause();
    }


    @OnClick({R.id.main_my_loction,R.id.main_bsi_btn, R.id.main_cardinfo_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_my_loction:
                myLocation(new LatLng(mCurrentLat, mCurrentLon));
                break;
            case R.id.main_bsi_btn:
                Intent intent = new Intent(this,BSIActivity.class);
                startActivity(intent);
                break;
            case R.id.main_cardinfo_btn:
                Intent intent1 = new Intent(this,CardInfoActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
