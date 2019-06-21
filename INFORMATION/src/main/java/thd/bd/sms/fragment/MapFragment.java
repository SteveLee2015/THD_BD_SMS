package thd.bd.sms.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import thd.bd.sms.R;
import thd.bd.sms.activity.MapOfflineActivity;
import thd.bd.sms.bean.MyLocationBean;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.view.CommomDialogCommon;
import thd.bd.sms.view.map.PoiOverlay;

public class MapFragment extends Fragment implements SensorEventListener, MKOfflineMapListener, 
        BaiduMap.OnMapLongClickListener,OnGetSuggestionResultListener,
        OnGetPoiSearchResultListener {

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
    @BindView(R.id.main_input_address)
    AutoCompleteTextView mainInputAddress;
    @BindView(R.id.main_map_search_img)
    ImageView mainMapSearchImg;
    private View view;
    private boolean isLocaiton;

    private MKOfflineMap mOffline = null;


//    private MKOfflineMap mOffline = null;

    private SuggestionSearch mSuggestionSearch = null;
    private ArrayAdapter<String> sugAdapter = null;
    private PoiSearch mPoiSearch = null;
    private int loadIndex = 0;

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

        Log.e(TAG, "onCreateView: =============SharedPreferencesHelper.getCity()=="+SharedPreferencesHelper.getCity() );

        initMap();

        initView();

        return view;

    }

    private void initView() {
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        /* 当输入关键字变化时，动态更新建议列表 */
        mainInputAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /* 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新 */
                mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(cs.toString())
                        .city(SharedPreferencesHelper.getCity()));
            }
        });
    }

    private void initMap() {
        isLocaiton = false;

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
        if (!isLocaiton) {
            myLocation(myLocationBean.getLatitude(), myLocationBean.getLongitude());
        }

        isLocaiton = true;
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
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
    }

    private void myLocation(double latitude, double longitude) {
        mainInputAddress.setText("");
        mBaiduMap.clear();

        mBaiduMap.setMyLocationData(locData);

        locData = new MyLocationData.Builder()
                .accuracy(40.0f)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection)
                .latitude(latitude)
                .longitude(longitude).build();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
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


    @OnClick({R.id.main_my_loction, R.id.main_offline_img, R.id.main_input_latlng_img,R.id.main_map_search_img})
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

            case R.id.main_map_search_img:
                String citystr = SharedPreferencesHelper.getCity();
                String keystr = mainInputAddress.getText().toString();

                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(citystr)
                        .keyword(keystr)
                        .pageNum(loadIndex)
                        .cityLimit(false)
                        .scope(1));
                break;

                default:
                    break;
        }
    }

    private void openLatlonDialog() {
        new CommomDialogCommon(getActivity(), R.style.dialog_aa, true, new CommomDialogCommon.OnDaoHangListener() {
            @Override
            public void onClick(Dialog dialog, double lat, double lon) {

                if (lat < 0 || lat > 90.0) {
                    Toast.makeText(getContext(), "请输入正确的纬度", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lon < 0 || lon > 180.0) {
                    Toast.makeText(getContext(), "请输入正确的经度", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();
                SysUtils.goToBaiduMap(getActivity(), lat, lon, "手动输入经纬度");
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

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }

        List<String> suggest = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }

        sugAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                suggest);
        mainInputAddress.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param result    Poi检索结果，包括城市检索，周边检索，区域检索
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();

            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";

            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }

            strInfo += "找到结果";
            Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结果
     * V5.2.0版本之后，该方法废弃，使用{@link #onGetPoiDetailResult(PoiDetailSearchResult)}代替
     * @param// result    POI详情检索结果
     */
    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        /*Log.e(TAG, "onGetPoiDetailResult: ===============2222==============="+ result);

        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(),
                    result.getName() + ": " + result.getAddress(),
                    Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        /*Log.e(TAG, "onGetPoiDetailResult: ===============1111==============="+ poiDetailSearchResult);

        if (poiDetailSearchResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            List<PoiDetailInfo> poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (null == poiDetailInfoList || poiDetailInfoList.isEmpty()) {
                Toast.makeText(getActivity(), "抱歉，检索结果为空", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < poiDetailInfoList.size(); i++) {
                PoiDetailInfo poiDetailInfo = poiDetailInfoList.get(i);
                if (null != poiDetailInfo) {
                    Toast.makeText(getActivity(),
                            poiDetailInfo.getName() + ": " + poiDetailInfo.getAddress(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }*/
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    private class MyPoiOverlay extends PoiOverlay {
        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            final PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
//            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            // }
//            Toast.makeText(getActivity(),
//                    poi.getName() + ": " + poi.getAddress()+",getLocation="+poi.getLocation(),
//                    Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "onPoiClick: =========================poi.getLocation()=="+poi.getLocation().latitude );

            final double lat = poi.getLocation().latitude;
            final double lon = poi.getLocation().longitude;

            new CommomDialogCommon(getActivity(), R.style.dialog_aa, "地图选点坐标为：\n经度：" + lon + "\n纬度：" + lat + "\n\n\n是否导航到此？", new CommomDialogCommon.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog) {
                    mainInputAddress.setText("");
                    mBaiduMap.clear();
                    SysUtils.goToBaiduMap(getActivity(), lat, lon, "地图搜索");
                }
            }).show();
            return true;
        }
    }
}
