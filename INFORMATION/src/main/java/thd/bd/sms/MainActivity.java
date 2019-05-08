package thd.bd.sms;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

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
import thd.bd.sms.Application.BaseActivity;

public class MainActivity extends BaseActivity implements SensorEventListener {


    @BindView(R.id.bmapView)
    MapView bmapView;

    private BaiduMap mBaiduMap;
    private BitmapDescriptor mCurrentMarker;
    private final int accuracyCircleFillColor = 0xAA99FF99;//范围内充颜色
    private final int accuracyCircleStrokeColor = 0xAA33CCFF;//范围边框
    private MyLocationData locData;
    //传感器
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
//    private double mCurrentLat = 0.0;
//    private double mCurrentLon = 0.0;


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

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        myLocation(latLng);

//        mCurrentLat = location.getLatitude();
//        mCurrentLon = location.getLongitude();

//        Toast.makeText(this,"location="+mCurrentLat+","+mCurrentLon,Toast.LENGTH_SHORT).show();

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
}
