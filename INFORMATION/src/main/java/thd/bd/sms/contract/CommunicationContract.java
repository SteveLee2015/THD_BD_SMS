package thd.bd.sms.contract;

import android.content.Context;

import java.util.List;

import thd.bd.sms.base.BaseView;
import thd.bd.sms.bean.BDMsgBean;

public interface CommunicationContract {
    interface Model {
        List<BDMsgBean> loadAllMessages(Context context,int page);
    }

    interface View extends BaseView {
        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

        void onSuccess(List<BDMsgBean> list);
    }

    interface Presenter {
        void loadAllMessages(Context context,int page);

    }
}
