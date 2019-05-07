package thd.bd.sms.presenter;

import android.content.Context;

import thd.bd.sms.Application.BasePresenter;
import thd.bd.sms.contract.CardInfoContract;
import thd.bd.sms.model.CardInfoModel;

public class CardInfoPresenter extends BasePresenter implements CardInfoContract.Presenter {

    private CardInfoContract.Model model;
    private CardInfoContract.View view;

    public CardInfoPresenter(CardInfoContract.View view) {
        model = new CardInfoModel();
        this.view = view;
    }

    @Override
    public void sendCardCmd(Context context) {

        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }

        mView.showLoading();
        model.sendCardCmd(context);
    }

    /*@Override
    public String getCardInfo() {
        String ss = model.getCardInfo();
        view.hideLoading();

        if(ss==null &&"".equals(ss)){
            view.onError(new Throwable("没有卡信息！"));
        }else {
            view.onSuccess();
        }
        return model.getCardInfo();
    }*/
}
