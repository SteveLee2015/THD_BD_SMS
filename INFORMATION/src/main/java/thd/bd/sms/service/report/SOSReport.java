package thd.bd.sms.service.report;

import android.content.Context;
import android.location.BDLocationReport;
import android.location.BDParameterException;
import android.location.BDUnknownException;
import android.util.Log;

import com.thd.cmd.manager.BDCmdManager;
import com.thd.cmd.manager.entity.QueueMode;

import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.utils.Logger;
import thd.bd.sms.utils.Utils;


/**
 * SOS紧急求救
 * @author lerry
 *
 */
public class SOSReport extends BaseReport implements Reportable{

	private static final String TAG = "SOSReport";
	private Context mContext;

	SOSReport(Context mContext) {
		super(mContext);
		this.mContext = mContext;
	}

	@Override
	public void reportLoc(ReportSet mReportSet, BDLocationReport report) {
		Logger.e(TAG, mReportSet.toString());

		BDCmdManager cmdManager = BDCmdManager.getInstance(mContext);
//		sendData(mBDDat);
		try {
//            String msg ="你好呀为什么短报文不行呢";
			cmdManager.sendSMSCmdBDV21( mReportSet.getReportNnm(), 1 , QueueMode.HEAD,Utils.checkMsgEncodeMode(mReportSet.getReportSOSContent()) ,
					"N" , mReportSet.getReportSOSContent());
			Log.e(TAG, "reportLoc: =================发送SOS紧急求救=====================");
		} catch (BDUnknownException e) {
			e.printStackTrace();
		} catch (BDParameterException e) {
			e.printStackTrace();
		}
	}
}
