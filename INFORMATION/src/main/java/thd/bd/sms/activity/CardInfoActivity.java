package thd.bd.sms.activity;

import android.location.CardInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.Application.BaseActivity;
import thd.bd.sms.R;
import thd.bd.sms.contract.CardInfoContract;
import thd.bd.sms.crashUtils.ProgressDialog;
import thd.bd.sms.presenter.CardInfoPresenter;

public class CardInfoActivity extends BaseActivity implements CardInfoContract.View {
    @BindView(R.id.card_info_exit)
    EditText cardInfoExit;
    @BindView(R.id.card_info_btn)
    Button cardInfoBtn;


    private static final String TAG = "CardInfoActivity";
    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;
    private CardInfoPresenter cardInfoPresenter;

    @Override
    protected int getContentView() {
        return R.layout.activity_card_info;
    }

    @Override
    public void showLoading() {
        ProgressDialog.getInstance().show(this);
    }

    @Override
    public void hideLoading() {
        ProgressDialog.getInstance().dismiss();
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(this, "发送失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        titleName.setText("读卡界面");

        cardInfoPresenter = new CardInfoPresenter(this);
        cardInfoPresenter.attachView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick({R.id.card_info_btn,R.id.return_home_layout})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.bsi_btn:
                cardInfoPresenter.sendCardCmd(this);
                break;

            case R.id.return_home_layout:
                CardInfoActivity.this.finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cardInfoPresenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCardInfo(CardInfo cardInfo) {
        hideLoading();

        if (cardInfo.getCardAddress() == null && "".equals(cardInfo.getCardAddress())) {
            return;
        }
        StringBuffer cardInfoStr = new StringBuffer();

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

        Log.e(TAG, "BDEventListener.LocalInfoListener: ==========" + cardInfoStr);

        cardInfoExit.setText(cardInfoStr.toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_DOWN) {

            this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
