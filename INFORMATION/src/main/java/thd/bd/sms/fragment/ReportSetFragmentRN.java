package thd.bd.sms.fragment;

import android.view.View;

import thd.bd.sms.bean.ReportSet;


/**
 * Created by asdfg on 2017/1/9.
 */

public class ReportSetFragmentRN extends ReportSetFragment {


    @Override
    public void getLastData(){

        ReportSet reportSet = settingDatabaseOper.getByType(1+"");
        //修改
        if (reportSet!=null) {
            setText(reportSet);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reportType.setIndex(1);
        ll_change.setVisibility(View.GONE);
        ll_desc.setVisibility(View.VISIBLE);
        tv_desc.setText("RN位置报告");

    }



}
