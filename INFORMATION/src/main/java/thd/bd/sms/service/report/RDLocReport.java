package thd.bd.sms.service.report;

import android.content.Context;
import android.content.Intent;
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


/**
 * rn位置报告
 * @author llg052
 *
 */
public class RDLocReport extends BaseReport implements Reportable{

	private BDCmdManager cmdManager;
	private Context mContext;

	RDLocReport(Context mContext) {
		super(mContext);
		this.mContext = mContext;

	}
	private static final String TAG = "RDLocReport";
	@Override
	public void reportLoc(ReportSet mReportSet) {
		Logger.e(TAG, mReportSet.toString());

		//保存到数据库中
		//封装数据
		BDCache mBdCache = new BDCache();
		mBdCache.setMsgType(BDCache.RD_REPORT_FLAG);
		mBdCache.setSendAddress(mReportSet.getReportNnm());

		mBdCache.setMsgContent("RD定位的位置报告");
		mBdCache.setPriority(BDCache.PRIORITY_3);
		mBdCache.setCacheContent(mReportSet.getReportNnm()+","+mReportSet.getReportType()+","+mReportSet.getTianxianValue()+","+mReportSet.getReportHz());
		//把数据保存到数据库中
		dispatchData(mBdCache);

		try {
			cmdManager = BDCmdManager.getInstance(mContext);
			cmdManager.sendLocationReport2CmdBDV21(mReportSet.getReportNnm(), BDRDSSManager.HeightFlag.COMMON_USER, 10, 0);
		} catch (BDUnknownException e) {
			e.printStackTrace();
		} catch (BDParameterException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 数据处理
	 *
	 * @param mBdCache
	 */
	private void dispatchData(BDCache mBdCache) {

		RDCacheOperation operation = new RDCacheOperation(mContext);

		int conutBefore = operation.getCount();
		if (conutBefore >= Config.CACHE_COUNT) {

			Toast.makeText(mContext, "抱歉,缓存溢出!", Toast.LENGTH_SHORT).show();

		} else {

			operation.insert(mBdCache);//插入数据
			int count = operation.getCount();//获取数据
			SharedPreferencesHelper.put(Constant.SP_RECORDED_KEY_COUNT,count);//记录数据
			notifyData();
			// 唤醒线程
		}

	}

	/**
	 * 通知数据变化
	 */
	private void notifyData() {
//		Intent intent = new Intent();
//		intent.setAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD);
//		mContext.sendBroadcast(intent);
	}

}
