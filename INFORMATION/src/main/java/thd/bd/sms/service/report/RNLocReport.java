package thd.bd.sms.service.report;

import android.content.Context;

import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.utils.Logger;

/**
 * rd位置报告
 * @author llg052
 *
 */
public class RNLocReport extends BaseReport implements Reportable{

	RNLocReport(Context mContext) {
		super(mContext);
	}

	private static final String TAG = "RNLocReport";

	@Override
	public void reportLoc(ReportSet mReportSet) {
		Logger.e(TAG, mReportSet.toString());
		Logger.e(TAG, "==========");

		//rn位置报告逻辑
//		sendData(mBDDat);
	}

}
