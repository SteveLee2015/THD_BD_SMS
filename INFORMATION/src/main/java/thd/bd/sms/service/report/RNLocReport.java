package thd.bd.sms.service.report;

import android.content.Context;
import android.content.Intent;
import android.location.BDLocationReport;
import android.location.BDParameterException;
import android.location.BDRDSSManager;
import android.location.BDUnknownException;
import android.widget.Toast;

import com.thd.cmd.manager.BDCmdManager;

import thd.bd.sms.bean.BDCache;
import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.database.RDCacheOperation;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.Logger;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.SysUtils;

/**
 * rd位置报告
 * @author lerry
 *
 */
public class RNLocReport extends BaseReport implements Reportable{

	RNLocReport(Context mContext) {
		super(mContext);
		this.mContext = mContext;
	}

	private static final String TAG = "RNLocReport";
	private BDCmdManager cmdManager;
	private Context mContext;


	@Override
	public void reportLoc(ReportSet mReportSet,BDLocationReport report) {
		Logger.e(TAG, mReportSet.toString());
		Logger.e(TAG, "==========");

		//rn位置报告逻辑
		//保存到数据库中
		//封装数据
		BDCache mBdCache = new BDCache();
		mBdCache.setMsgType(BDCache.RN_REPORT_FLAG);
		mBdCache.setSendAddress(mReportSet.getReportNnm());

		mBdCache.setMsgContent("RN定位的位置报告");
		mBdCache.setPriority(BDCache.PRIORITY_3);
		mBdCache.setCacheContent(mReportSet.getReportNnm()+","+mReportSet.getReportType()+","+mReportSet.getTianxianValue()+","+mReportSet.getReportHz());
		//把数据保存到数据库中
		SysUtils.dispatchData(mContext,mBdCache);

		try {
			cmdManager = BDCmdManager.getInstance(mContext);
			cmdManager.sendLocationReport1CmdBDV21(report);
		} catch (BDUnknownException e) {
			e.printStackTrace();
		} catch (BDParameterException e) {
			e.printStackTrace();
		}

	}


}
