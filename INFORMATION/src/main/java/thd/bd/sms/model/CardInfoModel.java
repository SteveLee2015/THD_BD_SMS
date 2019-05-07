package thd.bd.sms.model;

import android.content.Context;
import android.location.BDEventListener;
import android.location.BDUnknownException;
import android.location.CardInfo;

import com.thd.cmd.manager.BDCmdManager;

import thd.bd.sms.contract.CardInfoContract;

public class CardInfoModel implements CardInfoContract.Model {

    StringBuffer cardInfoStr = new StringBuffer();

    private BDEventListener.LocalInfoListener localInfoListener = new BDEventListener.LocalInfoListener() {
        @Override
        public void onCardInfo(CardInfo cardInfo) {

            cardInfoStr.append("mCardAddress : " + cardInfo.getCardAddress());
            cardInfoStr.append("\n");

            cardInfoStr.append("mSerialNum : " + cardInfo.getSerialNum());
            cardInfoStr.append("\n");

            cardInfoStr.append("mBroadCastAddress : " + cardInfo.getBroadCastAddress());
            cardInfoStr.append("\n");

            cardInfoStr.append("mCardType : " + cardInfo.getCardType());
            cardInfoStr.append("\n");

            cardInfoStr.append("mSericeFeq : " + cardInfo.getSericeFeq());
            cardInfoStr.append("\n");

            cardInfoStr.append("mCommLevel : " + cardInfo.getCommLevel());
            cardInfoStr.append("\n");

            cardInfoStr.append("checkEncryption : " + cardInfo.getCheckEncryption());
            cardInfoStr.append("\n");

            cardInfoStr.append("mSubordinatesNum : " + cardInfo.getSubordinatesNum());
            cardInfoStr.append("\n");

        }
    };

    @Override
    public void sendCardCmd(Context context) {
        BDCmdManager bdCmdManager = BDCmdManager.getInstance(context);
        try {
            bdCmdManager.addBDEventListener(localInfoListener);
        } catch (BDUnknownException e) {
            e.printStackTrace();
        }

        bdCmdManager.sendAccessCardInfoCmdBDV21(0,0);
    }

    @Override
    public String getCardInfo() {
        return cardInfoStr.toString();
    }
}
