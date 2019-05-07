package thd.bd.sms.activity;

import android.location.BDUnknownException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private CardInfoPresenter cardInfoPresenter;
    private String cardInfoStr;

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
        Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardInfoPresenter = new CardInfoPresenter(this);
        cardInfoPresenter.attachView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardInfoExit.setText(cardInfoStr);
    }

    @OnClick(R.id.card_info_btn)
    public void onViewClicked() {
        cardInfoPresenter.sendCardCmd(this);
        cardInfoStr = cardInfoPresenter.getCardInfo();

        Log.e(TAG, "onViewClicked: =========="+ cardInfoStr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cardInfoPresenter.detachView();
//        try {
//            cmdManager.removeBDEventListener(localInfoListener, bdBeamStatusListener ,bdCmdTimeOutListener , bdLocationListener , bdLocReportListener);
//        } catch (BDUnknownException e) {
//            e.printStackTrace();
//        }
//        cmdManager.onDestroy();
    }
}
