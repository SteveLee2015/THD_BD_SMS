package thd.bd.sms.service.report;

import android.content.Context;

import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.utils.Logger;


/**
 * 自定义位置报告
 * @author llg052
 *
 */
public class StateLocReport extends BaseReport implements Reportable{

	private static final String TAG = "StateLocReport";
	private Context mContext;

	StateLocReport(Context mContext) {
		super(mContext);
		this.mContext = mContext;
	}
	@Override
	public void reportLoc(ReportSet mReportSet) {

		Logger.e(TAG, mReportSet.toString());

//		sendData(mBDDat);
	}

}
