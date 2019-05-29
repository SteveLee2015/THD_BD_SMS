package thd.bd.sms.fragment;

import android.view.View;

import thd.bd.sms.bean.ReportSet;


/**
 * Created by asdfg on 2017/1/9.
 */

public class ReportSetFragmentRD extends ReportSetFragment {



    @Override
    public void getLastData(){

        ReportSet reportSet = settingDatabaseOper.getByType(2+"");
        //修改
        if (reportSet!=null) {
            setText(reportSet);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reportType.setIndex(2);

        ll_change.setVisibility(View.GONE);
        ll_desc.setVisibility(View.VISIBLE);
        tv_desc.setText("RD位置报告");

    }
}
