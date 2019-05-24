package thd.bd.sms.presenter;

import android.content.Context;

import java.util.List;

import thd.bd.sms.base.BasePresenter;
import thd.bd.sms.bean.BDMsgBean;
import thd.bd.sms.contract.CommunicationContract;
import thd.bd.sms.model.CommunicationModel;

public class CommunicationPresenter extends BasePresenter implements CommunicationContract.Presenter {

    private CommunicationContract.Model model;
    private CommunicationContract.View view;
    private List<BDMsgBean> list;

    public CommunicationPresenter(CommunicationContract.View view) {
        model = new CommunicationModel();
        this.view = view;
    }


    @Override
    public void loadAllMessages(Context context,int page) {
        //View是否绑定 如果没有绑定，就不执行网络请求
        if (!isViewAttached()) {
            return;
        }

        mView.showLoading();
        list = model.loadAllMessages(context,page);

        if(list!=null && list.size()!=0){
            view.onSuccess(list);
            view.hideLoading();
        }
    }


}
