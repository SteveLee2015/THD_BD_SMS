package thd.bd.sms.service;

import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;


import java.util.Timer;
import java.util.TimerTask;

import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Logger;

/**
 * RD循环报告服务
 *
 * @author lerry
 */
public class CycleReportRDLocService extends CycleReportService {

    private final static String TAG = "CycleReportRDLocService";
    private int mCountNum = 10;//倒计时 取 卡频和设定位置报告周期的较大值
    protected int mReportSetFequency = 60;//设定的位置报告周期
    private String reportType;//报告类型
    private ReportSet mReportSet = null;
    private RDTimeTask timeTask;

    @Override
    public void onCreate() {
        super.onCreate();
        initSound();
        timeTask = new RDTimeTask();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            sendNumStr = intent.getStringExtra("sendNumStr");
            reportStatus = intent.getStringExtra("reportStatus");
        }
        //reportType = intent.getStringExtra("reportType");
        reportType = ReportSet.REPORTSET_RD;
        Logger.d(TAG, "reportType" + reportType + "\r\n" + "reportStatus" + reportStatus);
        if (reportStatus == null || reportStatus.equals("")) {
            reportStatus = "未设置状态!";
        }
        if (sendNumStr == null || sendNumStr.equals("")) {
            sendNumStr = "";
        }

        mReportSet = oper.getByType(reportType);
        mReportSetFequency = Integer.valueOf(mReportSet.getReportHz());
        mCountNum = 1;
        if (timeTask != null) {
            timeTask.getInstance().start(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public void onDestroy() {
        timeTask.getInstance().destroyed();
        Logger.d(TAG, "onDestroy");
//        SharedPreferencesHelper.put(Constant.SP_RD_REPORT_STATE,false);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 定时器
     */
    class RDTimeTask extends TimerTask {

        private boolean flag = false;
        private RDTimeTask mybTimeTask = null;
        private Timer timer = null;

        private RDTimeTask() {
        }

        //单例模式，保持这个对象
        public RDTimeTask getInstance() {
            if (mybTimeTask == null || flag) {
                //当flag == true时，为了解决，timer.cancel()后，重新创建一个timer
                mybTimeTask = new RDTimeTask();
                if (flag) {
                    flag = false;
                }
            }
            return mybTimeTask;
        }

        public void start(boolean flg) {
            //毫秒
            long time = getStartTime();

            if (timer == null) {
                timer = new Timer();

            } else {
                //从此计时器的任务队列中移除所有已取消的任务。
                timer.purge();
            }

            timer.scheduleAtFixedRate(this, 1, time);
            Logger.d(TAG, "定时任务开始...............");
        }

        public void run() {
            Logger.d(TAG, "定时任务执行：\"+System.currentTimeMillis()");

            //写自己的逻辑，略
            if (mCountNum > 0) {
                //有超频的时候,加上超频时间
                mCountNum--;
                Logger.d(TAG, mCountNum + "");
            } else if (mCountNum == 0) {
                Logger.d(TAG, mCountNum + "");
                mCountNum = cardFreq > mReportSetFequency ? cardFreq : mReportSetFequency;
                try {
                    // 循环逻辑  rd rn Status?
                    locReport(sendNumStr, reportType, reportStatus);
                    Logger.d(TAG, "Timer()-->locReport()");
                    playSoundAndVibrate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void destroyed() {
            Logger.d(TAG, "定时任务销毁............................");
            //终止此计时器，丢弃所有当前已安排的任务。(不但结束当前schedule，连整个Timer的线程(即当前的定时任务)都会结束掉)
            timer.cancel();
            timer = null;
            mybTimeTask = null;
            flag = true;
        }

        private long getStartTime() {
            //毫秒
            long i = 1 * 1000;

            return i;
        }
    }
}
