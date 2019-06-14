package thd.bd.sms.service.report;

import android.content.Context;
import android.location.BDLocationReport;
import android.util.Log;

import thd.bd.sms.bean.ReportSet;


/**
 * 位置报告管理类
 *
 * @author llg052
 */
public class LocReportManager {

    private static final String TAG = "LocReportManager";

    public static LocReportManager locReportManager;

    public static LocReportManager getInstance(Context mContext) {
        if (locReportManager == null) {
            locReportManager = new LocReportManager(mContext);
        }
        return locReportManager;
    }

    private Context mContext;

    private LocReportManager(Context mContext) {
        this.mContext = mContext;
    }

    Reportable reportor;

    /**
     * 位置报告
     * 报告类型  0状态报告  1rn位置报告  2rd位置报告
     */
    public void locationReport(ReportSet mReportSet,BDLocationReport report) {
        String reportType = mReportSet.getReportType();
        if (reportType == null) {
            Log.w("LERRYTEST_RD定位" ,"========LocReportManager42================位置报告类型不能为空!!==");
            return;
        }
        switch (reportType) {
            case "0":
                reportor = new StateLocReport(mContext);
                break;
            case "1":
                reportor = new RNLocReport(mContext);
                break;
            case "2":
                reportor = new RDLocReport(mContext);
                break;
            case "3":
                reportor = new SOSReport(mContext);
                break;

            default://默认rd位置报告
                reportor = new RDLocReport(mContext);
                break;
        }
        if (reportor == null) {
            Log.w("LERRYTEST_RD定位" ,"========LocReportManager65================位置报告类型不能为空!!==");
            return;
        }
        reportor.reportLoc(mReportSet,report);
    }


}
