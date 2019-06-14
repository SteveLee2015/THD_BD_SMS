package thd.bd.sms.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.BDBeam;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.service.CycleReportSOSService;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.WinUtils;

/**
 * 采用RDSS中间件实现访问北斗信息
 * 广播Action::::com.s510.home
 * intent.putExtra("data", "enable");这个是不屏蔽
 * intent.putExtra("data", "disenable"); 这个是屏蔽
 *
 * @author llg
 */
public class SOSActivity extends BaseActivity {

    private static final String TAG = "SOSActivity";
    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;

    private Context mContext = this;
    private TextView desc1;
    private TextView desc2;
    private Button submitCancel;

    private boolean signal = false;
    private Button submitSingle;
    private TextView noSingle;
    private LinearLayout hasSingle;

    //public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志

    @Override
    protected int getContentView() {
        return R.layout.activity_sos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
        IntentFilter filter = new IntentFilter();
//        filter.addAction(ReceiverAction.BD_ACTION_BSI);
        filter.addAction(ReceiverAction.BD_ACTION_SOS_UI);
        filter.addAction(ReceiverAction.BD_ACTION_SOS_UI_SOS_SIZE);
        registerReceiver(infoReceiver, filter);

        setFinishOnTouchOutside(false);//

        titleName.setText("正在进行SOS救援");
        returnHomeLayout.setVisibility(View.INVISIBLE);

        //WinUtils.setDialogPosition(this, 0.9f,0.7f);

        hasSingle = (LinearLayout) findViewById(R.id.ll_has_single);
        noSingle = (TextView) findViewById(R.id.iv_no_single);
        desc1 = (TextView) findViewById(R.id.sos_desc1);
        desc2 = (TextView) findViewById(R.id.sos_desc2);
        submitCancel = (Button) findViewById(R.id.sos_submit);
        submitSingle = (Button) findViewById(R.id.sos_signal);

        submitCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mContext, CycleReportSOSService.class);
                stopService(intent);

                finish();
            }
        });

        submitSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(mContext, BSIActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        closeHome();

    }

    private void closeHome() {
        Intent homeIntent = new Intent();
        homeIntent.setAction(ReceiverAction.BD_ACTION_HOME);
        homeIntent.putExtra(ReceiverAction.BD_KEY_HOME, ReceiverAction.BD_VALUE_N_HOME);
        /*if(Build.VERSION.SDK_INT >= 26) {
            ComponentName componentName=new ComponentName(getApplicationContext(),"");//参数1-包名 参数2-广播接收者所在的路径名
            homeIntent.setComponent(componentName);
//            intent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
        }*/
        sendBroadcast(homeIntent);
    }

    @Override
    protected void onDestroy() {

        openHome();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(infoReceiver);
        super.onDestroy();
    }

    private void openHome() {
        Intent homeIntent = new Intent();
        homeIntent.setAction(ReceiverAction.BD_ACTION_HOME);
        homeIntent.putExtra(ReceiverAction.BD_KEY_HOME, ReceiverAction.BD_VALUE_Y_HOME);
        /*if(Build.VERSION.SDK_INT >= 26) {
            ComponentName componentName=new ComponentName(getApplicationContext(),"");//参数1-包名 参数2-广播接收者所在的路径名
            homeIntent.setComponent(componentName);
//            homeIntent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
        }*/
        sendBroadcast(homeIntent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBSIBeam(BDBeam bdBeam) {
        if (bdBeam == null) {
            Log.d(TAG, "====LERRYTEST_bs================getBSIBeam===161===========bdBeam == null");
            return;
        }


        int[] beamArray = bdBeam.getBeamWaves();
        if (beamArray == null) {
            Log.d(TAG, "====LERRYTEST_bs================getBSIBeam===168===========beamArray == null");
            return;
        }

        Log.d(TAG, "====LERRYTEST_bs================getBSIBeam===172===========beamArray==" + beamArray);

        // 判断是否有信号
        int count = beamArray.length;
        int num = 0;
        for (int index = 0; index < bdBeam.getBeamWaves().length; index++) {
            if (bdBeam.getBeamWaves()[index] > 0) {
                num++;
            }
        }

        if (num > 0) {
            signal = true;
            hasSingle.setVisibility(View.VISIBLE);
            noSingle.setVisibility(View.GONE);
        } else {
            signal = true;
            hasSingle.setVisibility(View.GONE);
            noSingle.setVisibility(View.VISIBLE);
        }

    }


    BroadcastReceiver infoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action) {

                /*case ReceiverAction.BD_ACTION_BSI:// 波束信息
                    // 判断是否有信号 有信号则 sendThread 查询数据 有数据则 发送 没有数据则 线程等待

                    BD_RD_BSI bsi = intent
                            .getParcelableExtra(ReceiverAction.BD_KEY_BSI);
                    if (bsi == null) {
                        return;
                    }
                    int[] beamArray = bsi.getBeamWaves();
                    if (beamArray == null) {
                        return;
                    }

                    // 判断是否有信号
                    int count = beamArray.length;
                    int num = 0;
                    for (int index = 0; index < bsi.getBeamWaves().length; index++) {
                        if (bsi.getBeamWaves()[index] > 0) {
                            num++;
                        }
                    }

                    if (num > 0) {
                        signal = true;
                        hasSingle.setVisibility(View.VISIBLE);
                        noSingle.setVisibility(View.GONE);
                    } else {
                        signal = true;
                        hasSingle.setVisibility(View.GONE);
                        noSingle.setVisibility(View.VISIBLE);
                    }

                    break;*/


                case ReceiverAction.BD_ACTION_SOS_UI:

                    //更新倒计时
                    int mCountDown = intent.getIntExtra(ReceiverAction.BD_KEY_SOS_UI_COUNT_NUM, -1);
                    String desc2Str = "距离下次发送还有" + mCountDown + "秒";
                    desc2.setText(desc2Str);
                    break;

                case ReceiverAction.BD_ACTION_SOS_UI_SOS_SIZE:

                    // 如何获取已经发送信息
                    int mSosSize = intent.getIntExtra(ReceiverAction.BD_KEY_SOS_UI_SOS_SIZE, -1);
                    String desc1Str = "信号正常,已经发送" + mSosSize + "条救援信息";
                    desc1.setText(desc1Str);
                    break;
            }

        }
    };

}

