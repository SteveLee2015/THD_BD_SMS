package thd.bd.sms.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.BDCache;
import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.database.RDCacheOperation;
import thd.bd.sms.service.CoreService;
import thd.bd.sms.service.CycleLocService;
import thd.bd.sms.service.CycleReportRDLocService;
import thd.bd.sms.service.CycleReportRNLocService;
import thd.bd.sms.service.CycleReportSOSService;
import thd.bd.sms.service.report.SOSReport;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.utils.WinUtils;

public class MainCenterActivity extends BaseActivity {
    @BindView(R.id.main_center_boshu_layout)
    LinearLayout mainCenterBoshuLayout;
    @BindView(R.id.main_center_close_img)
    ImageView mainCenterCloseImg;
    @BindView(R.id.main_center_xingtu_layout)
    LinearLayout mainCenterXingtuLayout;
    @BindView(R.id.RN_location_info_layout)
    LinearLayout RNLocationInfoLayout;
    @BindView(R.id.RD_location_info_layout)
    LinearLayout RDLocationInfoLayout;
    @BindView(R.id.main_center_daohang_layout)
    LinearLayout mainCenterDaohangLayout;
    @BindView(R.id.main_center_state_layout)
    LinearLayout mainCenterStateLayout;
    @BindView(R.id.main_center_RNReport_layout)
    LinearLayout mainCenterRNReportLayout;
    @BindView(R.id.main_center_RDReport_layout)
    LinearLayout mainCenterRDReportLayout;
    @BindView(R.id.main_center_SOS_layout)
    LinearLayout mainCenterSOSLayout;
    @BindView(R.id.main_center_RN_report_txt)
    TextView mainCenterRNReportTxt;
    @BindView(R.id.main_center_RD_report_txt)
    TextView mainCenterRDReportTxt;
    @BindView(R.id.tishi_total_txt)
    TextView tishiTotalTxt;

    private Intent intent;
    private final String TAG = "MainCenterActivity";
//    private MyConn myConn;
    private RDCacheOperation cacheOperation;
    //    private List<BDCache> lists = new ArrayList<BDCache>();
//    private boolean isBind = false;

    private CoreService coreService;


    /**
     * 数据库变化广播
     *
     * @author llg052
     */

    BroadcastReceiver dbChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD.equals(action)) {
                // 数据库变化了,重新查询数据库
                int count = cacheOperation.getAll().size();
                SharedPreferencesHelper.put(Constant.SP_RECORDED_KEY_COUNT, count);
                writeAbout();
            }
        }

    };


    @Override
    protected int getContentView() {
        return R.layout.activity_main_center;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);
//        //进入退出效果 注意这里 创建的效果对象是 Explode()
        getWindow().setEnterTransition(new Explode().setDuration(2000));
        getWindow().setExitTransition(new Explode().setDuration(2000));

        addReceiver();
        initView();

    }

    private void initView() {
//        myConn = new MyConn();

        coreService = CoreService.getInstance();

        cacheOperation = new RDCacheOperation(this);

        showUI();
    }

    /**
     * 注册广播
     */
    private void addReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD);
        registerReceiver(dbChangeReceiver, filter);
    }


    /**
     * 数据回写
     *
     * @param
     */
    private void showUI() {

        Boolean floatRD = SharedPreferencesHelper.getRDReportState();
        Boolean floatRN = SharedPreferencesHelper.getRNReportState();
//        Boolean floatStatus = SpTools.getFloatStatus(context, SpTools.SP_FLOAT_STATUS_KEY_STATUS);

        if (floatRD && SysUtils.isServiceRunning(this,CycleReportRDLocService.class.getName())) {
            mainCenterRDReportTxt.setText(Config.RD_RUNNING);
            SharedPreferencesHelper.put(Constant.SP_RD_REPORT_STATE,true);
            SharedPreferencesHelper.put(Constant.SP_RN_REPORT_STATE,false);
            mainCenterRNReportTxt.setText(Config.RN_WAITING);
        } else {
            mainCenterRDReportTxt.setText(Config.RD_WAITING);
            SharedPreferencesHelper.put(Constant.SP_RD_REPORT_STATE,false);
        }
        if (floatRN && SysUtils.isServiceRunning(this,CycleReportRNLocService.class.getName())) {

            mainCenterRNReportTxt.setText(Config.RN_RUNNING);
            SharedPreferencesHelper.put(Constant.SP_RN_REPORT_STATE,true);
            mainCenterRDReportTxt.setText(Config.RD_WAITING);
            SharedPreferencesHelper.put(Constant.SP_RD_REPORT_STATE,false);

        } else {
            mainCenterRNReportTxt.setText(Config.RN_WAITING);
            SharedPreferencesHelper.put(Constant.SP_RN_REPORT_STATE,false);
        }
//        if (floatStatus) {
//            openStatucontinueService(context);
//            tv_status.setText(Config.SMS_RUNNING);
//        } else {
//            tv_status.setText(Config.SMS_WAITING);
//        }
        writeAbout();
    }

    private void writeAbout() {
        int size = SharedPreferencesHelper.getRecordedCount();
        String info = "还有" + size + "条等待发送";
        if (tishiTotalTxt != null) {
            tishiTotalTxt.setText(info);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dbChangeReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @OnClick({R.id.main_center_boshu_layout, R.id.main_center_close_img,
            R.id.main_center_xingtu_layout, R.id.RN_location_info_layout,
            R.id.RD_location_info_layout, R.id.main_center_daohang_layout,
            R.id.main_center_state_layout, R.id.main_center_RNReport_layout,
            R.id.main_center_RDReport_layout, R.id.main_center_SOS_layout,
            R.id.tishi_total_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_center_boshu_layout:
                intent = new Intent(this, BSIActivity.class);
                startActivity(intent);
                break;
            case R.id.main_center_close_img:
                MainCenterActivity.this.finish();
                break;

            case R.id.main_center_xingtu_layout:
                intent = new Intent(this, StatelliteStatusActivity.class);
                startActivity(intent);
                break;
            case R.id.RN_location_info_layout:
                intent = new Intent();
                intent.putExtra(Config.FLAG_TAG, Config.FLAG_RN);
                intent.setClass(MainCenterActivity.this, LocActivity.class);
                startActivity(intent);
                break;
            case R.id.RD_location_info_layout:
                intent = new Intent();
                intent.putExtra(Config.FLAG_TAG, Config.FLAG_RD);
                intent.setClass(MainCenterActivity.this, LocActivity.class);
                startActivity(intent);
                break;
            case R.id.main_center_daohang_layout:
                Utils.goToBaiduMap(this, 0, 0, "请设置导航终点");
                break;
            case R.id.main_center_state_layout:
                Toast.makeText(this, "功能尚未开发完成，敬请期待！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_center_RNReport_layout:
                if (SysUtils.isServiceRunning(this, CycleReportRDLocService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_RD_report), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SysUtils.isServiceRunning(this, CycleReportSOSService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_SOS_service), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SysUtils.isServiceRunning(this, CycleLocService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_RD_Location), Toast.LENGTH_SHORT).show();
                    return;
                }

                isServiceStart(ReportSet.REPORTSET_RN);

                break;
            case R.id.main_center_RDReport_layout:
//                String rnStr = tv_rd.getText().toString().trim();
                if (SysUtils.isServiceRunning(this, CycleReportRNLocService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_RN_report), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SysUtils.isServiceRunning(this, CycleLocService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_RD_Location), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SysUtils.isServiceRunning(this, CycleReportSOSService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_SOS_service), Toast.LENGTH_SHORT).show();
                    return;
                }

                isServiceStart(ReportSet.REPORTSET_RD);

                break;
            case R.id.main_center_SOS_layout:
                Intent intent1 = new Intent(this, SoSsetActivity.class);
                startActivity(intent1);
                break;

            case R.id.tishi_total_txt:
                Intent intent = new Intent(this, MsgdbActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 判断服务是否开启
     */

    private void isServiceStart(String tag){
        switch (tag){
            case ReportSet.REPORTSET_RD:
                String className = CycleReportRDLocService.class.getName();
                boolean isStart = SysUtils.isServiceRunning(this, className);

                if(isStart){//如果开启就需要关闭
                    mainCenterRDReportTxt.setText(Config.RD_WAITING);
                    SharedPreferencesHelper.put(Constant.SP_RD_REPORT_STATE, false);

                    coreService.isServiceStart(ReportSet.REPORTSET_RD);
                    Log.e(TAG, "LERRY_RDREPORT: =================MainCenterActivity311=======关闭RD连续位置报告服务====");

                }else {//开启
                    coreService.isServiceStart(ReportSet.REPORTSET_RD);
                    mainCenterRDReportTxt.setText(Config.RD_RUNNING);
                    SharedPreferencesHelper.put(Constant.SP_RD_REPORT_STATE, true);
                    Log.e(TAG, "LERRY_RDREPORT: =================MainCenterActivity326=======开启RD连续位置报告服务====");
                }

                break;

            case ReportSet.REPORTSET_RN:

                String className1 = CycleReportRNLocService.class.getName();
                boolean isStart1 = SysUtils.isServiceRunning(this, className1);

                if(isStart1){//如果开启就需要关闭
                    mainCenterRNReportTxt.setText(Config.RN_WAITING);
                    SharedPreferencesHelper.put(Constant.SP_RN_REPORT_STATE, false);

                    coreService.isServiceStart(ReportSet.REPORTSET_RN);
                    Log.e(TAG, "LERRY_RDREPORT: =================MainCenterActivity342=======关闭RN连续位置报告服务====");

                }else {//开启
                    coreService.isServiceStart(ReportSet.REPORTSET_RN);
                    mainCenterRNReportTxt.setText(Config.RN_RUNNING);
                    SharedPreferencesHelper.put(Constant.SP_RN_REPORT_STATE, true);
                    Log.e(TAG, "LERRY_RDREPORT: =================MainCenterActivity357=======开启RN连续位置报告服务====");
                }

                break;
        }
    }


    /*private class MyConn implements ServiceConnection {

        *//**
         * 当服务被成功绑定时候调用
         *//*
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.w(TAG, "LERRY_RDREPORT====================onServiceConnected");
            isBind = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "LERRY_RDREPORT====================onServiceDisconnected");
            isBind = false;
        }

    }*/

}
