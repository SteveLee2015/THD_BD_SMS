package thd.bd.sms.contract;

import android.content.Context;

import thd.bd.sms.Application.BaseView;

public interface CardInfoContract {

    interface Model {
        void sendCardCmd(Context context);
//        String getCardInfo();
    }

    interface View extends BaseView {
        @Override
        void showLoading();

        @Override
        void hideLoading();

        @Override
        void onError(Throwable throwable);

        void onSuccess();
    }

    interface Presenter {
        void sendCardCmd(Context context);
//        String getCardInfo();
    }
}
