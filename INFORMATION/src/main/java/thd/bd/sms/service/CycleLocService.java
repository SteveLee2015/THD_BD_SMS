package thd.bd.sms.service;

import android.content.ComponentName;
import android.content.Intent;
import android.location.BDRDSSManager;
import android.location.BDUnknownException;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.thd.cmd.manager.BDCmdManager;

import java.util.Timer;
import java.util.TimerTask;

import thd.bd.sms.application.SMSApplication;
import thd.bd.sms.bean.BDCache;
import thd.bd.sms.bean.LocationSet;
import thd.bd.sms.database.LocSetDatabaseOperation;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.SysUtils;

/**
 * 连续定位服务
 * 
 * @author llg
 */
public class CycleLocService extends CycleReportService {

	private final static String TAG = "CycleLocService";
	private int mCountNum = 0;//倒计时 取 卡频和设定位置报告周期的较大值
	protected int mReportSetFequency = 60;//设定的位置报告周期
	private String reportType;//报告类型
	private LocationSet mLocationSet;
	private SMSApplication app;

	private LocTimeTask timeTask;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initSound();
		app = (SMSApplication) getApplication();
		timeTask = new LocTimeTask();
		Log.w("LERRYTEST_RD定位" ,"========CycleLocService44================new LocTimeTask()...............====");
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		LocSetDatabaseOperation oper = new LocSetDatabaseOperation(mContext);
		//mLocationSet = oper.getFirst();
		mLocationSet = oper.getByStatus(LocationSet.LOCATIONSET_STATUS_USING);
		mReportSetFequency = Integer.parseInt(mLocationSet.getLocationFeq());

		Log.w("LERRYTEST_RD定位" ,"========CycleLocService57================onStartCommand...............====");

		if("".equals(SharedPreferencesHelper.getCardAddress())){
			Toast.makeText(mContext, "请安装北斗卡!", Toast.LENGTH_LONG).show();
		}else {
			if (timeTask!=null){
				timeTask.getInstance().start(true);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public ComponentName startService(Intent service) {
		return super.startService(service);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timeTask.getInstance().destroyed();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class LocTimeTask extends TimerTask {

		private boolean flag = false;
		private LocTimeTask mybTimeTask = null;
		private Timer timer = null;
		private LocTimeTask(){}
		//单例模式，保持这个对象
		public LocTimeTask getInstance(){
			if (mybTimeTask == null || flag ) {
				//当flag == true时，为了解决，timer.cancel()后，重新创建一个timer
				mybTimeTask = new LocTimeTask();
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
			Log.w("LERRYTEST_RD定位" ,"========CycleLocService109================定时任务开始...............====");
		}

		public void run() {
			if (mCountNum > 0) {
				//有超频的时候,加上超频时间
				mCountNum--;
				Log.w("LERRYTEST_RD定位" ,"========CycleLocService116================mCountNum=="+mCountNum);
			} else if (mCountNum == 0) {
				Log.w("LERRYTEST_RD定位" ,"========CycleLocService119================mCountNum=="+mCountNum);
				mCountNum = cardFreq>mReportSetFequency?cardFreq:mReportSetFequency;

				// 循环 定位
				BDCmdManager cmdManager = BDCmdManager.getInstance(app);
				try {
					cmdManager.sendLocationInfoReqCmdBDV21(BDRDSSManager.ImmediateLocState.LOC_NORMAL_FLAG, Integer.valueOf(mLocationSet.getHeightType()), "L",
							Integer.valueOf(mLocationSet.getHeightValue()), Integer.valueOf(mLocationSet.getTianxianValue()), 0);
					Log.w("LERRYTEST_RD定位" ,"========CycleLocService132============发送RD定位语句======");
				} catch (BDUnknownException e) {
					e.printStackTrace();
				}

				//封装数据
				//BD_RD_DWA dwa = (BD_RD_DWA)data.m_Data;
				BDCache mBdCache = new BDCache();
				mBdCache.setMsgType(BDCache.RD_LOCATION_FLAG);
				mBdCache.setSendAddress(SharedPreferencesHelper.getCardAddress());
				mBdCache.setMsgContent("定位申请");
				mBdCache.setPriority(BDCache.PRIORITY_1);
				mBdCache.setCacheContent("连续定位，普通定位，测高方式："+mLocationSet.getHeightType()+",高程数据："+mLocationSet.getHeightValue()+",天线高："+mLocationSet.getTianxianValue());
				//把数据保存到数据库中
				SysUtils.dispatchData(CycleLocService.this,mBdCache);

				playSoundAndVibrate();

			}
		}

		public void destroyed(){
			Log.w("LERRYTEST_RD定位" ,"========CycleLocService141================定时任务销毁=========");
			//终止此计时器，丢弃所有当前已安排的任务。(不但结束当前schedule，连整个Timer的线程(即当前的定时任务)都会结束掉)
			timer.cancel();
			flag = true;
		}

		private long getStartTime() {
			//毫秒
			long i = 1*1000;

			return i;
		}
	}
}
