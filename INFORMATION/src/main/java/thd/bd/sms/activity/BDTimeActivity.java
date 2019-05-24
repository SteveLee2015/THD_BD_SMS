package thd.bd.sms.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.MyLocationBean;
import thd.bd.sms.utils.DateUtils;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.utils.WinUtils;

public class BDTimeActivity extends BaseActivity {

    private static final String TAG = "BDTimeActivity";

    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.time_sfm)
    TextView timeSfm;
    @BindView(R.id.time_text)
    TextView timeText;
    @BindView(R.id.time_ymd)
    TextView timeYmd;
    @BindView(R.id.time_jiaoshi_btn)
    Button timeJiaoshiBtn;
    /**
     * 定位时间
     */
    private long locationTime = 0;
    /**
     * 完成校验北斗时间
     */
    private final int COMPLETE_CHECK_BD_TIME = 1;

    private final int COMPLETE_GO_BD_TIME = 3;//当前界面要一直随秒走时间
    private final int PROMPT_CHECK_BD_TIME = 2;

    @Override
    protected int getContentView() {
        return R.layout.activity_time;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMPLETE_CHECK_BD_TIME: {
                    Toast.makeText(BDTimeActivity.this, "卫星校时完成!", Toast.LENGTH_SHORT).show();
                    break;
                }
                case PROMPT_CHECK_BD_TIME: {
                    Toast.makeText(BDTimeActivity.this, "当前卫星信号差,校时失败!", Toast.LENGTH_SHORT).show();
                    break;
                }

                case COMPLETE_GO_BD_TIME: {
                    updateTime((Calendar) msg.obj);
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getLatlng(MyLocationBean myLocationBean) {

        if (myLocationBean != null && myLocationBean.getTime() != 0) {
            locationTime = myLocationBean.getTime();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(locationTime));

        Message message = new Message();
        message.what = COMPLETE_GO_BD_TIME;
        message.obj = calendar;
        mHandler.sendMessage(message);
        Log.e("LERRYTEST_MAP", "=========BDTimeActivity81=======location==" + myLocationBean.getTime());
    }

    @OnClick({R.id.return_home_layout, R.id.time_jiaoshi_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.return_home_layout:
                break;
            case R.id.time_jiaoshi_btn:

                /*// 判断 服务是否在运行
                // 连续 rd报告服务  rn报告服务 状态服务 定位服务
                // 弹出对话框
                String clssName1 = CycleReportRDLocService.class.getName();
                boolean isStart1 = AppUtils.isServiceRunning(mContext, clssName1);

                String clssName2 = CycleReportRNLocService.class.getName();
                boolean isStart2 = AppUtils.isServiceRunning(mContext, clssName2);

                String clssName3 = CycleReportStatuService.class.getName();
                boolean isStart3 = AppUtils.isServiceRunning(mContext, clssName3);

                String clssName4 = CycleLocService.class.getName();
                boolean isStart4 = AppUtils.isServiceRunning(mContext, clssName4);

                String clssName5 = CycleReportSOSService.class.getName();
                boolean isStart5 = AppUtils.isServiceRunning(mContext, clssName5);

//                DBLocation location = GspStatesManager.getInstance().mLocation;
//                if (location != null)
//                    locationTime = GspStatesManager.getInstance().mLocation.getTime();

                if (isStart1) {
                    //弹出提示对话框  请关闭连续rd报告
                    String msg = "校时前,请关闭RD连续位置报告!";
                    aletDialog(msg);
                    return;
                }

                if (isStart2) {
                    //弹出提示对话框  请关闭连续rd报告
                    String msg = "校时前,请关闭RN连续位置报告!";
                    aletDialog(msg);
                    return;
                }

                if (isStart3) {
                    //弹出提示对话框  请关闭连续rd报告
                    String msg = "校时前,请关闭连续状态报告!";
                    aletDialog(msg);
                    return;
                }

                if (isStart4) {
                    //弹出提示对话框  请关闭连续rd报告
                    String msg = "校时前,请关闭RD连续定位!";
                    aletDialog(msg);
                    return;
                }

                if (isStart5) {
                    //弹出提示对话框  请关闭连续rd报告
                    String msg = "校时前,请关闭SOS救援服务!";
                    aletDialog(msg);
                    return;
                }*/

                if (locationTime != 0) {
//                    if (app.isBlueToothModel()) {
//                        if (app.getConnectedDevice() == null) {
//                            Toast.makeText(mContext, "设备未连接!", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }
                    //handleTimeChange(locationTime+8*3600*1000);
                    handleTimeChange(locationTime);
                } else {
                    Toast.makeText(BDTimeActivity.this, "未能获取定位信息,校时失败!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * 修改时间
     *
     * @param time
     */
    private void handleTimeChange(long time) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = time;
        calendar.setTimeInMillis(currentTime);
        int year = calendar.get(Calendar.YEAR);
        // 由于模块在启动后，未与卫星通信时，默认的时间是2000-1-1 00:00:00,
        // 所以判断如果年份小于等于2000，则提示用户"请到空旷的地方，方便卫星校时"
        if (year <= 2000) {
            mHandler.sendEmptyMessage(PROMPT_CHECK_BD_TIME);
        } else {
            //SystemDateTime.setDateTime(currentTime);
            //该方案需要系统权限
            //S510 发送系统广播修改系统时间

            String dateTimeStr = DateUtils.getDateTimeStr(time);
            Intent intent = new Intent();
            intent.setAction(ReceiverAction.SPEEDATA_ACTION_SETDATETIME);
            intent.putExtra(ReceiverAction.SPEEDATA_KEY_DATETIME, dateTimeStr);
            sendBroadcast(intent);

            mHandler.sendEmptyMessage(COMPLETE_CHECK_BD_TIME);
        }
    }

    private void updateTime(Calendar calendar) {

        String s,f,m,n,y,r,w;//时，分，秒，年，月，日，周
        s = Utils.showTwoBitNum(calendar.get(Calendar.HOUR_OF_DAY));
        f = Utils.showTwoBitNum(calendar.get(Calendar.MINUTE));
        m = Utils.showTwoBitNum(calendar.get(Calendar.SECOND));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        n = year.substring(year.length() - 2);
        y = Utils.showTwoBitNum(calendar.get(Calendar.MONTH) + 1);
        r = Utils.showTwoBitNum(calendar.get(Calendar.DAY_OF_MONTH));
        w = Utils.getCurrentWeekDay(calendar.get(calendar.DAY_OF_WEEK) - 1);
        timeSfm.setText(s+":"+f+":"+m);
        timeYmd.setText(n+"年"+y+"月"+r+"日，星期"+w);
    }

    @Override
    public void onComLocation(Location location) {
        super.onComLocation(location);

        Log.e("LERRYTEST_MAP", "=========BDTimeActivity123=======location==" + location.getTime());
    }
}
