package thd.bd.sms.service.report;


import android.location.BDLocationReport;

import thd.bd.sms.bean.ReportSet;

/**
 * 位置报告接口
 * @author lerry
 *
 */
public interface Reportable {
	
	/**
	 * 位置报告
	 * 报告参数:报告策略  报告数据
	 */
//	public abstract void reportLoc(ReportSet mReportSet, BDData mBDDat);
	public abstract void reportLoc(ReportSet mReportSet,BDLocationReport report);

}
