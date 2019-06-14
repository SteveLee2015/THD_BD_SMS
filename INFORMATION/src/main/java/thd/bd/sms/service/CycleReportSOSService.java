package thd.bd.sms.service;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import java.util.Timer;
import java.util.TimerTask;

import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.database.StateCodeOperation;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.Logger;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.view.GspStatesManager;


/**
 * 循环报告服务
 * 
 * @author llg
 */
public class CycleReportSOSService extends CycleReportService {

	private final static String TAG = "CycleReportSOSService";
	private int mCountNum = 10;//倒计时 取 卡频和设定位置报告周期的较大值
	protected int mReportSetFequency = 60;//设定的位置报告周期
	private String reportType;//报告类型
	private ReportSet mReportSet = null;
	private SOSTimeTask timeTask;
	private Intent uiIntent;
	android.os.PowerManager.WakeLock wakeLock;
	@SuppressLint("InvalidWakeLockTag")
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate: ");
		super.onCreate();
		initSound();
		//清理 sos发送记录
		Config.SOS_COUNT = 0;
		timeTask = new SOSTimeTask();
		uiIntent = new Intent();
		uiIntent.setAction(ReceiverAction.BD_ACTION_SOS_UI);
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

		 wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"SOS");
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent!=null) {
			sendNumStr = intent.getStringExtra("sendNumStr");
			reportStatus = intent.getStringExtra("reportStatus");
		}
		reportType = ReportSet.REPORTSET_STATE;
		Logger.d(TAG, "reportType"+reportType+"\r\n"+"reportStatus"+reportStatus);
		if (reportStatus==null || reportStatus.equals("")) {
			reportStatus="未设置状态!";
		}
		if (sendNumStr==null || sendNumStr.equals("")) {
			sendNumStr="";
		}

		//设置 报告状态  根据状态码 查询出  状态汉字
		StateCodeOperation codeOperation = new StateCodeOperation(mContext);
		String[] listMsg = codeOperation.getAllMessagesArray();

		mReportSet = oper.getByType(reportType);
		//mReportSetFequency = Integer.valueOf(mReportSet.getReportHz());

		if (SharedPreferencesHelper.getSericeFeq()!=0) {
			cardFreq = SharedPreferencesHelper.getSericeFeq();
			mReportSetFequency = cardFreq;
		}
		mCountNum = 1;

		int statusCode = mReportSet.getStatusCode();
		String statusCodeWord = listMsg[statusCode];

		reportStatus = statusCodeWord;

		timeTask.getInstance().start(true);

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
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	/**
	 * 定时器
	 */
	class SOSTimeTask extends TimerTask {

		private boolean flag = false;
		private SOSTimeTask mybTimeTask = null;
		private Timer timer = null;
		private SOSTimeTask(){}
		//单例模式，保持这个对象
		public SOSTimeTask getInstance(){
			if (mybTimeTask == null || flag ) {
				//当flag == true时，为了解决，timer.cancel()后，重新创建一个timer
				mybTimeTask = new SOSTimeTask();
				if (flag){
					flag = false;
				}
			}
			return mybTimeTask;
		}

		public void start(boolean flg) {
			//毫秒
			long time = getStartTime();

			if (timer == null){
				timer = new Timer();

			} else {
				//从此计时器的任务队列中移除所有已取消的任务。
				timer.purge();
			}

			timer.scheduleAtFixedRate(this, 1, time);
			wakeLock.acquire();
			Logger.d(TAG, "定时任务开始...............");
		}

		public void run() {
			long currentTimeMillis = System.currentTimeMillis();
			Logger.d(TAG, "定时任务执行："+currentTimeMillis);

			//写自己的逻辑，略
			if (mCountNum > 0) {
				//有超频的时候,加上超频时间
				// 设置为本机卡频
				mCountNum--;
				uiIntent.putExtra(ReceiverAction.BD_KEY_SOS_UI_COUNT_NUM,mCountNum);
				sendBroadcast(uiIntent);
				Logger.d(TAG, mCountNum+"");
			} else if (mCountNum == 0) {
				Logger.d(TAG, mCountNum+"");
				rnLocation = GspStatesManager.getInstance().mLocation;
				//mCountNum = cardFreq>mReportSetFequency?cardFreq:mReportSetFequency;
				uiIntent.putExtra(ReceiverAction.BD_KEY_SOS_UI_COUNT_NUM,mCountNum);
				mCountNum = cardFreq;
				Log.d(TAG, "run: cardFreq="+cardFreq);
				sendBroadcast(uiIntent);
				try {
					// 循环逻辑  rd rn Status?
					reportType = ReportSet.REPORTSET_SOS;
					sendNumStr = SharedPreferencesHelper.getSosNum();
					reportStatus = SharedPreferencesHelper.getSosContent();

					locReport(sendNumStr,reportType,reportStatus);
					Logger.d(TAG, "Timer()-->locReport()");
					playSoundAndVibrate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		public void destroyed(){
			Logger.d(TAG, "定时任务销毁............................");
			//终止此计时器，丢弃所有当前已安排的任务。(不但结束当前schedule，连整个Timer的线程(即当前的定时任务)都会结束掉)
			timer.cancel();
			flag = true;
			wakeLock.release();
		}

		private long getStartTime() {
			//毫秒
			long i = 1*1000;

			return i;
		}
	}
}
