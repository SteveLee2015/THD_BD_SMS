package thd.bd.sms.model;

import android.content.Context;

import com.thd.cmd.manager.BDCmdManager;

import thd.bd.sms.contract.CardInfoContract;

public class CardInfoModel implements CardInfoContract.Model {

    private static final String TAG = "CardInfoModel";


    @Override
    public void sendCardCmd(Context context) {
        BDCmdManager bdCmdManager = BDCmdManager.getInstance(context);

        bdCmdManager.sendAccessCardInfoCmdBDV21(0,0);
    }

//    @Override
//    public String getCardInfo() {
//        return SMSApplication.getInstance().getCardInfoBean().getCardAddress();
//    }
}
