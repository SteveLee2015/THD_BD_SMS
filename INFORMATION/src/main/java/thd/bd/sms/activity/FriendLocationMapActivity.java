package thd.bd.sms.activity;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.MyLocationBean;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.view.CommomDialogCommon;

public class FriendLocationMapActivity extends BaseActivity {
    private static final String TAG = "MapActivity";

    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;

    private MapView mapView;
    private LatLng center = new LatLng(0, 0);
    private LatLng myLocation;
    private BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private MapStatus.Builder builder;
    private float zoom = 16.5f; // 地图缩放级别
    BitmapDescriptor mCurrentMarker;
    private final int accuracyCircleFillColor = 0xAA99FF99;//范围内充颜色
    private final int accuracyCircleStrokeColor = 0xAA33CCFF;//范围边框
    private MyLocationData locData;

    private Unbinder unbinder;//全局变量


    @Override
    protected int getContentView() {
        return R.layout.activity_map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        WinUtils.setWinTitleColor(this);

        EventBus.getDefault().register(this);

        //绑定初始化ButterKnife
        unbinder = (Unbinder) ButterKnife.bind(this);
//        setContentView(getContentView());

        titleName.setText("地图显示友邻位置");

        mapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        builder = new MapStatus.Builder();

//        float zoom = 16.5f; // 地图缩放级别
//        builder.target(myLocation).zoom(zoom);
//        mapView.getMap().setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_geo);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, false, mCurrentMarker,
                accuracyCircleFillColor, accuracyCircleStrokeColor));


        Log.e("LERRYTEST_MAP", "=========MapActivity117=======location==" + getIntent().getStringExtra("latitude") + "," + getIntent().getStringExtra("longitude"));
        if (getIntent() != null && (getIntent().getStringExtra("latitude") != null) && getIntent().getStringExtra("longitude") != null) {
            setMarker();
        }


    }

    private void setMarker() {
//        /**
//         * 初始化全局 bitmap 信息，不用时及时 recycle
//         */

        double lat = Double.parseDouble(getIntent().getStringExtra("latitude"));
        double lng = Double.parseDouble(getIntent().getStringExtra("longitude"));

        Log.e("LERRYTEST_MAP", "=========MapActivity62=======location==" + lat + "," + lng);

        BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        LatLng llA = new LatLng(lat, lng);

        MarkerOptions ooA = new MarkerOptions().position(Utils.baiduMapJP(llA)).icon(bdA).zIndex(9).draggable(true);
//        MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true);
        // 掉下动画
        ooA.animateType(MarkerOptions.MarkerAnimateType.drop);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

        //以当前友邻位置为中心点
        builder.target(Utils.baiduMapJP(llA)).zoom(zoom);
//        builder.target(llA).zoom(zoom);
        mapView.getMap().setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        Log.e("LERRYTEST_MAP", "=========MapActivity158=======友邻location中心点==");


//        if (myLocation.latitude != 0.0 && myLocation.longitude != 0.0 && center.longitude != 0.0 && center.latitude != 0.0) {
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//            builder.include(center);
//            builder.include(myLocation);
//
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(builder.build());
//            mBaiduMap.setMapStatus(u);
//        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //marker被点击时回调的方法
            //若响应点击事件，返回true，否则返回false
            //默认返回false
            @Override
            public boolean onMarkerClick(final Marker marker) {
                new CommomDialogCommon(FriendLocationMapActivity.this, R.style.dialog_aa, "地图选点坐标为：\n经度：" + marker.getPosition().longitude + "\n纬度：" + marker.getPosition().latitude + "\n\n\n是否导航到此？", new CommomDialogCommon.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        SysUtils.goToBaiduMap(FriendLocationMapActivity.this, marker.getPosition().latitude, marker.getPosition().longitude, "地图选点");
                    }
                }).show();
                return false;
            }
        });
    }

    private void myLocation(LatLng myLocation) {
//        /**
//         * 初始化全局 bitmap 信息，不用时及时 recycle
//         */

        /*BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

        MarkerOptions ooA = new MarkerOptions().position(myLocation).icon(bdA).zIndex(9).draggable(true);
        mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));*/

//        if(mMarkerA!=null){
//            mMarkerA.remove();
//        }


        locData = new MyLocationData.Builder()
                .accuracy(40.0f)
                // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(mCurrentDirection)
                .latitude(myLocation.latitude)
                .longitude(myLocation.longitude).build();
        mBaiduMap.setMyLocationData(locData);

        Log.e("LERRYTEST_MAP", "=========MapActivity113=======location==" + myLocation.latitude + "," + myLocation.longitude);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mapView.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // activity 销毁时同时销毁地图控件
        mapView.onDestroy();
        mapView = null;
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLatlng(MyLocationBean myLocationBean) {

        if (myLocationBean != null && myLocationBean.getLatitude() != 0.0 && myLocationBean.getLongitude() != 0.0) {
            myLocation = new LatLng(myLocationBean.getLatitude(), myLocationBean.getLongitude());
            myLocation(Utils.baiduMapJP(myLocation));
//            myLocation(myLocation);

        }
    }

    @OnClick(R.id.return_home_layout)
    public void onViewClicked() {
        this.finish();
    }
}
