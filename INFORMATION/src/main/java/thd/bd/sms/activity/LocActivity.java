package thd.bd.sms.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.BDLocation;
import android.location.BDRDSSManager;
import android.location.BDUnknownException;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thd.cmd.manager.BDCmdManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.BDCache;
import thd.bd.sms.bean.DBLocation;
import thd.bd.sms.bean.LocationParam;
import thd.bd.sms.bean.LocationSet;
import thd.bd.sms.database.LocSetDatabaseOperation;
import thd.bd.sms.service.CycleLocService;
import thd.bd.sms.service.CycleReportRDLocService;
import thd.bd.sms.service.CycleReportRNLocService;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.view.CustomListView;
import thd.bd.sms.view.OnCustomListListener;

/**
 * 北斗二代 RNSS 定位
 * 北斗一代 RDSS 定位
 *
 * @author llg
 */
public class LocActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "LocActivity";
    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;
    private Context mContext = LocActivity.this;
    private CustomListView customListView = null;
    private TextView mLongitude = null;
    private TextView mLatitude = null;
    private TextView mHeight = null;
    private TextView mTime = null;
    private TextView mLongitudeLable = null;
    private TextView mLatitudeLable = null;
    private TextView mHeightLable = null;
    private int COOD_FLAG = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LocationParam param = null;
    private PowerManager pm = null;

    private MyConn myConn;

    private int flag;//定位标识 rd定位 还是rn定位

    private DBLocation dwr;

    private LocationSet mLocationSet;

    private LinearLayout ll_loc_submit;//rd按钮
    private Button loc_btn;
    private ImageView settingRdIv;
    private Button loc_now_btn;

    private int frequcnce;//报告频度

    private MyCount myCount;//定位倒计时

    private ImmediateCount mImmediateCount;//紧急定位倒计时

    private LocSetDatabaseOperation oper;
    private boolean ImmediateCountRunning = false;
    private boolean MyCountRunning = false;


    @Override
    protected int getContentView() {
        return R.layout.activity_loc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        WinUtils.setWinTitleColor(this);

        EventBus.getDefault().register(this);

        oper = new LocSetDatabaseOperation(mContext);
        //mLocationSet = oper.getFirst();
        mLocationSet = oper.getByStatus(LocationSet.LOCATIONSET_STATUS_USING);
//        String locationFeq = mLocationSet.getLocationFeq();
//        frequcnce = Integer.parseInt(locationFeq);

        IntentFilter filter = new IntentFilter();

        Intent intent = getIntent();
        flag = intent.getIntExtra(Config.FLAG_TAG, -1);

        myConn = new MyConn();

        //保持cpu一直运行，不管屏幕是否黑屏
        pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
//        wakeLock.acquire();
        initUI();
        initListener();


        if (Config.FLAG_RD == flag) {

            //上次数据回显
            dwr = new DBLocation();
            dwr.setLongitude(SharedPreferencesHelper.getRDLon());
            dwr.setLatitude(SharedPreferencesHelper.getRDLat());
            dwr.setAltitude(SharedPreferencesHelper.getRDHeight());
            dwr.setTimeStr(SharedPreferencesHelper.getRDTime());

            if (SharedPreferencesHelper.getRDLat() != 0.0) {
                updateView(dwr);
            }


        } else {

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getRDLocation(BDLocation bdLocation) {
        dwr = new DBLocation();
        dwr.setLatitude(bdLocation.getLatitude());
        dwr.setLongitude(bdLocation.getLongitude());
        dwr.setAltitude(bdLocation.getEarthHeight());
        dwr.setTimeStr(bdLocation.mLocationTime);
        updateView(dwr);
        Log.w("LERRYTEST_RD定位", "========LocActivity167================bdLocation.getLatitude()==" + bdLocation.getLatitude() + ",bdLocation.getLongitude()========" + bdLocation.getLongitude());
    }

    private void initListener() {

        settingRdIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RDLocationSetActivity.class);
                startActivity(intent);
            }
        });

        customListView.setOnCustomListener(new OnCustomListListener() {
            @Override
            public void onListIndex(int num) {
                COOD_FLAG = num;
                if (Config.FLAG_RD == flag) {
                    if (dwr != null) {
                        updateView(dwr);
                    }
                } else if (Config.FLAG_RN == flag) {

                }
                switch (num) {
                    case 0:
                        mSwitchCGCS2000HeightCoodriate(param);
                        break;
                    case 1:
                        mSwitchGaoSiCoodriate(param);
                        break;
                    case 2:
                        mSwitchMaiKaTuoCoodriate(param);
                        break;
                    case 3:
                        mSwitchKongJianZhiJiaoCoodriate(param);
                        break;
                    case 4:
                        mSwitchBeiJing54Coodriate(param);
                        break;
                    default:

                        break;
                }
            }
        });
    }

    /**
     * 经纬度转换成beijing54坐标
     */
    protected void mSwitchBeiJing54Coodriate(LocationParam param) {

        mLongitude.setText((param != null && param.getmLon() != null) ? param
                .getmLon() : mContext.getResources().getString(
                R.string.common_x_value));
        mLongitudeLable.setText(mContext.getResources().getString(
                R.string.common_lon_str));
        mLatitude.setText((param != null && param.getmLat() != null) ? param
                .getmLat() : mContext.getResources().getString(
                R.string.common_y_value));
        mLatitudeLable.setText(mContext.getResources().getString(
                R.string.common_lat_str));
        mHeight.setText((param != null && param.getmHeight() != null) ? param
                .getmHeight() : mContext.getResources().getString(
                R.string.common_z_value));
        mHeightLable.setText(mContext.getResources().getString(
                R.string.common_height_str));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mLocationSet = oper.getByStatus(LocationSet.LOCATIONSET_STATUS_USING);
        String locationFeq = mLocationSet.getLocationFeq();
        frequcnce = Integer.parseInt(locationFeq);
        Log.w("LERRYTEST_RD定位", "=========LocActivity314=========frequcnce==" + frequcnce);

        if (Config.FLAG_RD == flag) {
            setTitle("RD定位信息");
            ll_loc_submit.setVisibility(View.VISIBLE);
            if ("".equals(SharedPreferencesHelper.getCardAddress())) {
                String hint = "请检查是否已经打开RD开关!" + "\r\n" + "请检查是否已经安装北斗卡!";
                Toast.makeText(mContext, hint, Toast.LENGTH_LONG).show();
                return;
            }

            //开启连续定位

            int type = mLocationSet.getType();
            String classNameLoc = CycleLocService.class.getName();
            boolean isStartLoc = SysUtils.isServiceRunning(mContext, classNameLoc);

            if (type == 1) {
                Log.w("LERRYTEST_RD定位", "=========LocActivity255=========isStartLoc==" + isStartLoc);
                if (isStartLoc) {
                    loc_btn.setText("停止连续定位");
                } else {
                    loc_btn.setText("开始连续定位");
                }
            } else {
                loc_btn.setText("开始定位");
            }

            if (frequcnce > 0) {

                // TODO: 2017/3/6 增加判断
                String locFlag = loc_btn.getText().toString().trim();

                if (locFlag.equals("停止连续定位")) {
                    //连续报位
                    //startContinueCycleLoc();
                    if (!isStartLoc) {
                        Intent intentLoc = new Intent(this, CycleLocService.class);
                        startService(intentLoc);
                        loc_btn.setText("停止连续定位");
                    } else {
                        //loc_btn.setText("开始连续定位");
                        //Intent intentLoc = new Intent(this, CycleLocService.class);
                        //stopService(intentLoc);
                    }
                } else if (locFlag.equals("开始连续定位")) {
                    Intent intentLoc = new Intent(this, CycleLocService.class);
                    stopService(intentLoc);
                }


            } else {
                //单次报位
//				int sericeFeq = cardInfo.getSericeFeq();
//				myCount = new MyCount(sericeFeq*1000, 1000);
                loc_btn.setText("开始定位");
            }

            int sericeFeq = SharedPreferencesHelper.getSericeFeq();
            myCount = new MyCount(sericeFeq * 1000, 1000);
            mImmediateCount = new ImmediateCount(sericeFeq * 2 * 1000, 1000);

//			//上次数据回显
//			dwr = new DBLocation();
//			dwr.setLongitude(sphelper.getFloat(SpHelper.SP_KEY_RD_LOCATION_LON));
//			dwr.setLatitude(sphelper.getFloat(sphelper.SP_KEY_RD_LOCATION_LAT));
//			dwr.setEarthHeight(sphelper.getFloat(SpHelper.SP_KEY_RD_LOCATION_EARTHHEIGHT));
//			dwr.setmLocationTime(sphelper.getString(SpHelper.SP_KEY_RD_LOCATION_TIME));
//
//			updateView(dwr);

        } else if (Config.FLAG_RN == flag) {

            setTitle("RN定位信息");
            ll_loc_submit.setVisibility(View.GONE);
//            if (!app.isBlueToothModel()) {
//            /* 查找到服务信息 */
//                Criteria criteria = new Criteria();
//
//                criteria.setAccuracy(Criteria.ACCURACY_FINE);
//                // 设置是否要求速度
//                criteria.setSpeedRequired(false);
//                // 设置是否允许运营商收费
//                criteria.setCostAllowed(false);
//                // 设置是否需要方位信息
//                criteria.setBearingRequired(false);
//                // 设置是否需要海拔信息
//                criteria.setAltitudeRequired(false);
//                // 设置对电源的需求
//                criteria.setPowerRequirement(Criteria.POWER_LOW);
//                // // 获取GPS信息
//                String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
//                // locationManager.requestLocationUpdates(provider, 1000,
//                // 0,locationListener);
//                locationManager.requestLocationUpdates(provider,
//                        1000, 0, locationListener);
//            }
        }
    }

    /**
     * 经纬度转换成CGCS2000-正常高
     */
    public void mSwitchCGCS2000HeightCoodriate(LocationParam param) {
        mLongitude.setText((param != null && param.getmLon() != null) ? param
                .getmLon() : mContext.getResources().getString(
                R.string.common_lon_value));
        mLongitudeLable.setText(mContext.getResources().getString(
                R.string.common_lon_str));
        mLatitude.setText((param != null && param.getmLat() != null) ? param
                .getmLat() : mContext.getResources().getString(
                R.string.common_lat_value));
        mLatitudeLable.setText(mContext.getResources().getString(R.string.common_lat_str));
        mHeight.setText((param != null && param.getmHeight() != null) ? param
                .getmHeight() : mContext.getResources().getString(R.string.common_dadi_heigh_value));
        mHeightLable.setText(mContext.getResources().getString(R.string.common_height_str));
    }

    /**
     * 经纬度转换成高斯平面坐标
     */
    public void mSwitchGaoSiCoodriate(LocationParam param) {
        mLongitude.setText((param != null && param.getmLon() != null) ? param
                .getmLon() : mContext.getResources().getString(
                R.string.common_x_value));
        mLongitudeLable.setText(mContext.getResources().getString(
                R.string.common_x_cood));
        mLatitude.setText((param != null && param.getmLat() != null) ? param
                .getmLat() : mContext.getResources().getString(
                R.string.common_y_value));
        mLatitudeLable.setText(mContext.getResources().getString(
                R.string.common_y_cood));
        mHeight.setText((param != null && param.getmHeight() != null) ? param
                .getmHeight() : mContext.getResources().getString(
                R.string.common_height_value));
        mHeightLable.setText(mContext.getResources().getString(
                R.string.common_height_str));
    }

    /**
     * 经纬度转换成麦卡托平面坐标
     */
    public void mSwitchMaiKaTuoCoodriate(LocationParam param) {
        mLongitude.setText((param != null && param.getmLon() != null) ? param
                .getmLon() : mContext.getResources().getString(
                R.string.common_x_value));
        mLongitudeLable.setText(mContext.getResources().getString(
                R.string.common_x_cood));
        mLatitude.setText((param != null && param.getmLat() != null) ? param
                .getmLat() : mContext.getResources().getString(
                R.string.common_y_value));
        mLatitudeLable.setText(mContext.getResources().getString(
                R.string.common_y_cood));
        mHeight.setText((param != null && param.getmHeight() != null) ? param
                .getmHeight() : mContext.getResources().getString(
                R.string.common_height_value));
        mHeightLable.setText(mContext.getResources().getString(R.string.common_height_str));
    }

    /**
     * 经纬度转换成空间直角坐标
     */
    public void mSwitchKongJianZhiJiaoCoodriate(LocationParam param) {
        mLongitude.setText((param != null && param.getmLon() != null) ? param
                .getmLon() : mContext.getResources().getString(
                R.string.common_x_value));
        mLongitudeLable.setText(mContext.getResources().getString(
                R.string.common_x_cood));
        mLatitude.setText((param != null && param.getmLat() != null) ? param
                .getmLat() : mContext.getResources().getString(
                R.string.common_y_value));
        mLatitudeLable.setText(mContext.getResources().getString(
                R.string.common_y_cood));
        mHeight.setText((param != null && param.getmHeight() != null) ? param
                .getmHeight() : mContext.getResources().getString(
                R.string.common_z_value));
        mHeightLable.setText(mContext.getResources().getString(
                R.string.common_z_cood));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (wakeLock != null) {
//            Log.i(TAG, "---------------------------->removeLocation()");
//            wakeLock.release();
//            wakeLock = null;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        /* 取消定位注册 */
//        if (locationManager != null && locationListener != null) {
//            locationManager.removeUpdates(locationListener);
//        }
//        unregisterReceiver(locReceiver);
        //是否需要关闭连续定位
        if (Config.FLAG_RD == flag && frequcnce > 0) {
            String classNameLoc = CycleLocService.class.getName();
            boolean isStartLoc = SysUtils.isServiceRunning(mContext, classNameLoc);
            if (isStartLoc) {
                Intent intentLoc = new Intent(this, CycleLocService.class);
                stopService(intentLoc);
                loc_btn.setText("开始连续定位");
            }
        }

    }

    /**
     * 初始化UI
     */
    public void initUI() {
        titleName.setText("RD定位信息");

        ll_loc_submit = (LinearLayout) this.findViewById(R.id.ll_loc_submit);
        loc_btn = (Button) this.findViewById(R.id.loc_btn);
        settingRdIv = (ImageView) this.findViewById(R.id.setting_rd);
        loc_now_btn = (Button) this.findViewById(R.id.loc_now_btn);
        loc_btn.setOnClickListener(this);
        loc_now_btn.setOnClickListener(this);
        mLongitude = (TextView) this.findViewById(R.id.bdloc_lon);
        mLatitude = (TextView) this.findViewById(R.id.bdloc_lat);
        mHeight = (TextView) this.findViewById(R.id.bdloc_height);
        mTime = (TextView) this.findViewById(R.id.bdloc_time);
        mLongitudeLable = (TextView) this.findViewById(R.id.bdloc_lon_lable);
        mLatitudeLable = (TextView) this.findViewById(R.id.bdloc_lat_lable);
        mHeightLable = (TextView) this.findViewById(R.id.bdloc_height_lable);
        /* 坐标增加数据 */
        customListView = (CustomListView) this.findViewById(R.id.bd_coodr_type);
        customListView.setData(getResources().getStringArray(
                R.array.bdloc_zuobiao_array));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.loc_btn:
                if (mLocationSet != null) {

                    if (ImmediateCountRunning) {
                        Toast.makeText(mContext, "待倒计时结束后,再进行相关操作!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(SysUtils.isServiceRunning(this,CycleReportRNLocService.class.getName())){
                        Toast.makeText(mContext, getResources().getString(R.string.stop_RN_report), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(SysUtils.isServiceRunning(this,CycleReportRDLocService.class.getName())){
                        Toast.makeText(mContext, getResources().getString(R.string.stop_RD_report), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // frequcnce >0 为连续定位
                    if (frequcnce > 0) {
                        startContinueCycleLoc();
                    } else {
                        rdLocation(false);
                        // 修改界面  倒计时
                        if (myCount != null) {
                            myCount.start();
                        }
                        loc_btn.setClickable(false);
                    }

                } else {
                    Toast.makeText(mContext, "请设置定位策略!", Toast.LENGTH_SHORT).show();
                    //弹出对话框提示
                }
                break;
            case R.id.loc_now_btn:
                if (mImmediateCount != null) {
                    mImmediateCount.start();

                }
                rdLocation(true);
                loc_now_btn.setClickable(false);
                break;
            default:
                break;
        }

    }

    /**
     * 开始/关闭 连续定位
     */
    private void startContinueCycleLoc() {
        String classNameLoc = CycleLocService.class.getName();
        boolean isStartLoc = SysUtils.isServiceRunning(mContext, classNameLoc);
        Intent intentLoc = new Intent(this, CycleLocService.class);
        Log.w("LERRYTEST_RD定位", "========LocActivity523================startContinueCycleLoc()=========");
        if (!isStartLoc) {
            startService(intentLoc);
            bindService(intentLoc, myConn, 0);
            Log.w("LERRYTEST_RD定位", "========LocActivity523================startContinueCycleLoc().startService=========");
            //修改 界面
            loc_btn.setText("停止连续定位");
        } else {
            stopService(intentLoc);
            unbindService(myConn);
            Log.w("LERRYTEST_RD定位", "========LocActivity523================startContinueCycleLoc().stopService=========");
            //修改 界面
            loc_btn.setText("开始连续定位");
        }
    }

    /**
     * rd定位
     */
    private void rdLocation(boolean isImmediateLocation) {


        BDCmdManager cmdManager = BDCmdManager.getInstance(this);
        try {

            if(isImmediateLocation){
                cmdManager.sendLocationInfoReqCmdBDV21(BDRDSSManager.ImmediateLocState.LOC_IMMEDIATE_FLAG, Integer.valueOf(mLocationSet.getHeightType()),
                        "L", Integer.valueOf(mLocationSet.getHeightValue()), Integer.valueOf(mLocationSet.getTianxianValue()), 0);


            }else {
                cmdManager.sendLocationInfoReqCmdBDV21(BDRDSSManager.ImmediateLocState.LOC_NORMAL_FLAG, Integer.valueOf(mLocationSet.getHeightType()),
                        "L", Integer.valueOf(mLocationSet.getHeightValue()), Integer.valueOf(mLocationSet.getTianxianValue()), 0);
                //封装数据
                BDCache mBdCache = new BDCache();
                mBdCache.setMsgType(BDCache.RD_LOCATION_FLAG);
                mBdCache.setSendAddress(SharedPreferencesHelper.getCardAddress());
                mBdCache.setMsgContent("定位申请");
                mBdCache.setPriority(BDCache.PRIORITY_1);
                mBdCache.setCacheContent("单次定位，普通定位，测高方式："+mLocationSet.getHeightType()+",高程数据："+mLocationSet.getHeightValue()+",天线高："+mLocationSet.getTianxianValue());
                //把数据保存到数据库中
                SysUtils.dispatchData(LocActivity.this,mBdCache);
            }
        } catch (BDUnknownException e) {
            e.printStackTrace();
        }
        //本机信息

//        BD_RD_ICI cardInfo = BDCardInfoManager.getInstance().getCardInfo();
//        if (cardInfo == null) {
//            Toast.makeText(mContext, mContext.getResources().getString(R.string.have_not_bd_sim), Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        //if (!BDHelper.checkBDSimCard(mContext))return;
//
//        //从数据库中获取定位参数
//
//        if (mLocationSet == null) {
//            Toast.makeText(mContext, "请设置定位参数!", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        BD_RD_DWA dwa = new BD_RD_DWA();
//        //dwa.mUserID = cardInfo.getCardAddress();
//        dwa.setmUserID(cardInfo.getCardAddress());
//        if (isImmediateLocation) {
//            dwa.setmUrgency("A");
//        } else {
//            dwa.setmUrgency("V");
//        }
//        dwa.setmMeter_Height_Way(Integer.parseInt(mLocationSet.getHeightType()));
//        dwa.setmHeight_type("L");
//        dwa.setmHeightValue(Double.parseDouble(mLocationSet.getHeightValue()));
//        dwa.setmAntenna_Height(Double.parseDouble(mLocationSet.getTianxianValue()));
//        dwa.setmPressureValue(0);
//        dwa.setmTemptertureValue(0);
//        dwa.setmApply_Freq(0);
//        //频率
//        int frequence = Integer.parseInt(mLocationSet.getLocationFeq());
//
//        //单次定位
//        BDData data = new BDData(ProtocolType.PROTOCOL_TYPE_BD21, BD21DataType.BD_21_RD_DWA, dwa);
//
//        if (isImmediateLocation) {
//            // 紧急定位 直接发送  有超频的危险
//            PackageObj buildPakege = protocol.buildPakege(data);
//            if (buildPakege == null) {
//                Toast.makeText(this, "buildPakege组包失败!!!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Intent intent = new Intent();
//            intent.setAction(BroadcastReceiverConst.COMM_RAW_DATA_ADD);
//            intent.putExtra(BroadcastReceiverConst.COMM_RAW_DATA, buildPakege.rawData);
//            sendBroadcast(intent);
//        } else {
//            // 非紧急定位
//            sendData(data);
//        }

    }


    /**
     * 定位接收广播
     */
    /*private BroadcastReceiver locReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ReceiverAction.BD_ACTION_DWR.equals(action)) {
                dwr = intent.getParcelableExtra(ReceiverAction.BD_KEY_DWR);
                Toast.makeText(mContext, "接收到定位信息!", Toast.LENGTH_SHORT).show();
                if (dwr == null) {
                    return;
                }
                updateView(dwr);
                //保存到sp中

                //界面显示的时候 把最近一次数据显示

            }
//            else if (ReceiverAction.BD_ACTION_RN_GGA.equals(action)) {
//               if (Config.FLAG_RN == flag) {
//                   RN_GGA gga = (RN_GGA) intent.getParcelableExtra("GGA");
//                   updateRNView(gga);
//               }
//            }else if (ReceiverAction.BD_ACTION_RN_RMC.equals(action)){
//                RN_RMC rmc = (RN_RMC)intent.getParcelableExtra("RMC");
//                String time  = rmc.getmDate()+" "+rmc.getmUTCTime();
//                SimpleDateFormat sdf2 = new SimpleDateFormat(
//                        "dd-MM-yy HH:mm:ss");
//                Date d = null;
//                try {
//                    d = sdf2.parse(time);
//                    String timenow = sdf.format(new Date(d.getTime()+8*3600*1000));
//                    //系统时间
//                    mTime.setText(timenow);
//                } catch (ParseException e2) {
//                    e2.printStackTrace();
//                }
//            }
        }
    };*/
//    private SpHelper sphelper;
    protected void updateView(DBLocation location) {

        // 经度  纬度  高度 时间
        double lon = Utils.changeLonLatMinuteToDegree(location.getLongitude());
        double lat = Utils.changeLonLatMinuteToDegree(location.getLatitude());

        String latiFormat = String.format("%.6f", lat);
        String longiFormat = String.format("%.6f", lon);

        lon = Double.parseDouble(longiFormat);
        lat = Double.parseDouble(latiFormat);


        if (location.getLongitude() != 0.0 && location.getLatitude() != 0.0) {
            param = Utils.translate(lon, lat, location.getAltitude(),
                    COOD_FLAG);
        }
        mLongitude.setText((param != null && param.getmLon() != null) ? param
                .getmLon() : mContext.getResources().getString(
                R.string.common_lon_value));
        mLatitude.setText((param != null && param.getmLat() != null) ? param
                .getmLat() : mContext.getResources().getString(
                R.string.common_lat_value));
        mHeight.setText((param != null && param.getmHeight() != null) ? param
                .getmHeight() : mContext.getResources().getString(
                R.string.common_dadi_heigh_value));

        if (location.getTime() != 0) {
            //本地时间
            String locationTime = sdf.format(new Date(location.getTime()));
            String time = "00:00:00.00";
            if (locationTime.length() >= 6) {
                String hh = locationTime.substring(0, 2);
                int anInt = Integer.parseInt(hh);
                int beijingTime = (anInt + 8) % 24;
                String mm = locationTime.substring(2, 4);
                String ss = locationTime.substring(4, locationTime.length());
                time = beijingTime + ":" + mm + ":" + ss;
            }
            mTime.setText(time);

        } else if ("".equals(location.getTimeStr())) {
            mTime.setText(location.getTimeStr());
        }

        Log.e(TAG, "LERRYTEST_MAP: =========LocActivity694==========lon==" + lon + "========lat==" + lat + "========time==" + mTime.getText().toString());

        SharedPreferencesHelper.put(Constant.SP_KEY_RD_LOCATION_LON, (float) location.getLongitude());
        SharedPreferencesHelper.put(Constant.SP_KEY_RD_LOCATION_LAT, (float) location.getLatitude());
        SharedPreferencesHelper.put(Constant.SP_KEY_RD_LOCATION_EARTHHEIGHT, (float) location.getAltitude());
        SharedPreferencesHelper.put(Constant.SP_KEY_RD_LOCATION_TIME, location.getTime());

    }

    @OnClick(R.id.return_home_layout)
    public void onViewClicked() {
        LocActivity.this.finish();
    }


    class MyCount extends CountDownTimer {

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            MyCountRunning = true;
            loc_btn.setText("剩余:" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            int type = mLocationSet.getType();
            MyCountRunning = false;
            if (1 == type) {
                loc_btn.setText("开始连续定位");
            } else {
                loc_btn.setText("开始定位");

            }
            loc_btn.setClickable(true);
        }

    }

    class ImmediateCount extends CountDownTimer {

        public ImmediateCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            loc_now_btn.setText("剩余:" + millisUntilFinished / 1000);
            ImmediateCountRunning = true;
        }

        @Override
        public void onFinish() {
            ImmediateCountRunning = false;
            loc_now_btn.setText("紧急定位");
            loc_now_btn.setClickable(true);
        }

    }

    private void updateRNView(Location gga) {
        if (gga == null) return;
        double lon = gga.getLongitude();
        double lat = gga.getLatitude();
        double height = gga.getAltitude();
        if (lon > 0 && lat > 0) {
            this.param = Utils.translate(lon, lat, height, COOD_FLAG);
        }
        mLongitude.setText((param != null && param.getmLon() != null) ? param
                .getmLon() : mContext.getResources().getString(
                R.string.common_lon_value));
        mLatitude.setText((param != null && param.getmLat() != null) ? param
                .getmLat() : mContext.getResources().getString(
                R.string.common_lat_value));

        mHeight.setText((param != null && param.getmHeight() != null) ? height + "" : mContext.getResources().getString(
                R.string.common_dadi_heigh_value));
    }

    @Override
    public void onComLocation(Location location) {
        super.onComLocation(location);

        double lon = location.getLongitude();
        double lat = location.getLatitude();

        String latiFormat = String.format("%.6f", lat);
        String longiFormat = String.format("%.6f", lon);

        lon = Double.parseDouble(longiFormat);
        lat = Double.parseDouble(latiFormat);

        location.setLatitude(lat);
        location.setLongitude(lon);

        Log.e("LERRYTEST_MAP", "=========LocActivity=======location813==" + location.getLatitude() + "," + location.getLongitude());

//        updateView(location, lon, lat);

        if (Config.FLAG_RN == flag) {

            updateRNView(location);
            String timenow = sdf.format(new Date(location.getTime()));
            //系统时间
            mTime.setText(timenow);
        }
    }

    private class MyConn implements ServiceConnection {

        /**
         * 当服务被成功绑定时候调用
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d(TAG, "onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }

    }
}
