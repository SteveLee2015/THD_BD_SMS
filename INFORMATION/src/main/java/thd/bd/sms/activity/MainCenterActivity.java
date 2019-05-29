package thd.bd.sms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.service.CycleReportRDLocService;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.SysUtils;
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

    private Intent intent;

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @OnClick({R.id.main_center_boshu_layout, R.id.main_center_close_img,
            R.id.main_center_xingtu_layout, R.id.RN_location_info_layout,
            R.id.RD_location_info_layout,R.id.main_center_daohang_layout,
            R.id.main_center_state_layout, R.id.main_center_RNReport_layout,
            R.id.main_center_RDReport_layout, R.id.main_center_SOS_layout})
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
                break;
            case R.id.main_center_state_layout:
                break;
            case R.id.main_center_RNReport_layout:
                break;
            case R.id.main_center_RDReport_layout:
//                String rnStr = tv_rd.getText().toString().trim();
                if (Config.RD_RUNNING.equals("正在位置报告")) {
//                    tv_rd.setText(Config.RD_WAITING);
//                    SpTools.setFloatStatus(context, SpTools.SP_FLOAT_STATUS_KEY_RD, false);
                    closeLorReportService(CycleReportRDLocService.class);
                } else {
                    openRDcontinueService(this);
                }
                break;
            case R.id.main_center_SOS_layout:
                break;
        }
    }

    /**
     * 关闭服务
     */
    private void closeLorReportService(Class clazz) {
        String className = clazz.getName();
        boolean isStart = SysUtils.isServiceRunning(this, className);

//		Intent mIntent=new Intent();
//		mIntent.setClass(mContext,clazz);
//		mContext.stopService(mIntent);

        /**
         * 判断方法为什么失效
         */

        if (isStart) {
            Intent mIntent = new Intent();
            mIntent.setClass(this, clazz);
            this.stopService(mIntent);
        }
    }


    private void openRDcontinueService(Context context) {
        boolean result = openLorReportService("", ReportSet.REPORTSET_RD, "", CycleReportRDLocService.class);

//        if (result) {
//            tv_rd.setText(Config.RD_RUNNING);
//            SpTools.setFloatStatus(context, SpTools.SP_FLOAT_STATUS_KEY_RD, true);
//        } else {
//            tv_rd.setText(Config.RD_WAITING);
//            SpTools.setFloatStatus(context, SpTools.SP_FLOAT_STATUS_KEY_RD, false);
//        }
    }

    /**
     * 开启服务
     */
    private boolean openLorReportService(String sendNumStr, String reportType, String reportStatus, Class clazz) {
        String clssName = clazz.getName();
        boolean isStart = SysUtils.isServiceRunning(this, clssName);
        boolean result = false;

        try {
            //开启 连续报位服务
            if (!isStart) {
                //不在运行 就开启
                if (sendNumStr.isEmpty()) {
                    sendNumStr = "";
                }
                Intent service = new Intent(this, clazz);
                //service.putExtra("reportType", reportType);
                service.putExtra("sendNumStr", sendNumStr);
                service.putExtra("reportStatus", reportStatus);
                this.startService(service);
                //closeViewPager();
                result = true;
            } else {
                // 在运行 什么都不做
                //Toast.makeText(mContext, "服务正在运行,再次开启失败!", 1).show();

                result = true;

            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }



}
