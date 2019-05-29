package thd.bd.sms.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import thd.bd.sms.bean.ReportSet;


/**
 * Created by lerry .
 */

public class ReportSetFragmentStatus extends ReportSetFragment {


    private static final String TAG = "ReportSetFragmentStatus";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getLastData(){

        ReportSet reportSet = settingDatabaseOper.getByType(0+"");
        //修改
        if (reportSet!=null) {
            setText(reportSet);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        reportType.setIndex(0);
        ll_change.setVisibility(View.GONE);
        ll_desc.setVisibility(View.VISIBLE);
        tv_desc.setText("状态报告");

    }
}
