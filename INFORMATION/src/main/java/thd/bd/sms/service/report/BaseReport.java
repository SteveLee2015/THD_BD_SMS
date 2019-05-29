package thd.bd.sms.service.report;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;

import thd.bd.sms.application.SMSApplication;

public class BaseReport {
	
	//private BDProtocol protocol;
	private Context mContext;
	private SMSApplication app;


	BaseReport(Context mContext){
		this.mContext = mContext;
		//protocol = BDProtocol.getInstance(mContext);
		if (mContext instanceof Activity) {
			Activity mActivity = (Activity) mContext;
			app = (SMSApplication) mActivity.getApplication();
		}else if (mContext instanceof Service) {
			Service mService = (Service) mContext;
			app = (SMSApplication) mService.getApplication();
		}else if (mContext instanceof Application) {
			app = (SMSApplication) mContext;
		}
	}
	
	/**
	 * 发送数据
	 * @param 
	 */
	protected void sendData() {
		
		//插入到数据库
//		if (app!=null) {
//			app.appSendData(data);
//		}
		
//		PackageObj buildPakege = protocol.buildPakege(data);
//		Intent intent = new Intent();
//		intent.setAction(BroadcastReceiverConst.COMM_RAW_DATA_ADD);
//		intent.putExtra(BroadcastReceiverConst.COMM_RAW_DATA, buildPakege.rawData);
//		mContext.sendBroadcast(intent);
	}

}
