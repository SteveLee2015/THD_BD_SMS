package thd.bd.sms.service.report;

import android.content.Context;
import android.location.BDLocationReport;

import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.utils.Logger;


/**
 * 自定义位置报告
 * @author llg052
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

//		sendData(mBDDat);
	}
}
