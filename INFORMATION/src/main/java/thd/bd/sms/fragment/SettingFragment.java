package thd.bd.sms.fragment;

import android.content.Intent;
import android.location.CardInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thd.cmd.manager.BDCmdManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import thd.bd.sms.R;
import thd.bd.sms.activity.BDTimeActivity;
import thd.bd.sms.activity.MsgUsalWordActivity;
import thd.bd.sms.activity.RDLocationSetActivity;
import thd.bd.sms.activity.ReportSetActivity;
import thd.bd.sms.activity.SoSsetActivity;
import thd.bd.sms.activity.SoftwareActivity;
import thd.bd.sms.activity.StateCodeActivity;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Utils;

public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";

    @BindView(R.id.setting_bd_card)
    TextView settingBdCard;
    @BindView(R.id.setting_sim_level)
    TextView settingSimLevel;
    @BindView(R.id.setting_serice_feq)
    TextView settingSericeFeq;
    @BindView(R.id.setting_serial_number)
    TextView settingSerialNumber;
    @BindView(R.id.setting_jiami)
    TextView settingJiami;
    Unbinder unbinder;
    @BindView(R.id.setting_time_layout)
    LinearLayout settingTimeLayout;
    @BindView(R.id.setting_report_layout)
    LinearLayout settingReportLayout;
    @BindView(R.id.setting_RDLocation_layout)
    LinearLayout settingRDLocationLayout;
    @BindView(R.id.setting_duanyu_layout)
    LinearLayout settingDuanyuLayout;
    @BindView(R.id.setting_custom_layout)
    LinearLayout settingCustomLayout;
    @BindView(R.id.setting_SOS_layout)
    LinearLayout settingSOSLayout;
    @BindView(R.id.setting_about_layout)
    LinearLayout settingAboutLayout;
    @BindView(R.id.setting_sim_max_length)
    TextView settingSimMaxLength;
    private View view;

    private CardInfo cardInfo;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_setting, container, false);

        EventBus.getDefault().register(this);

        unbinder = ButterKnife.bind(this, view);

        if (cardInfo != null && !"2031958".equals(cardInfo.getCardAddress())) {
            settingBdCard.setText("北斗卡号：" + cardInfo.getCardAddress());
            settingSerialNumber.setText("序列号：" + cardInfo.getSerialNum());
            if("N".equals(cardInfo.getCheckEncryption())){
                settingJiami.setText("是否密卡：否");
            }else if("E".equals(cardInfo.getCheckEncryption())){
                settingJiami.setText("是否密卡：是");
            }
            settingSimLevel.setText("通信等级：" + cardInfo.getCommLevel() + "级");
            settingSimMaxLength.setText("最大通信长度：" + Utils.getMessageMaxLength() + " bit");
            settingSericeFeq.setText("服务频度："+cardInfo.getSericeFeq()+"秒");
        } else {
            BDCmdManager bdCmdManager = BDCmdManager.getInstance(getContext());
            bdCmdManager.sendAccessCardInfoCmdBDV21(0, 0);
        }

        return view;

    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getCardInfo(CardInfo cardInfo) {

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

        this.cardInfo = cardInfo;

        setCardInfoToView();
    }

    private void setCardInfoToView() {
        //这里需要把卡数据存到本地
        //卡等级
        if (cardInfo.getCommLevel() == 0) {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_COMMLEVEL, 0);
        } else {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_COMMLEVEL, cardInfo.getCommLevel());
        }
        //是否加密
        if (cardInfo.getCheckEncryption() == null) {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_CHECKENCRYPITION, "");
        } else {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_CHECKENCRYPITION, cardInfo.getCheckEncryption());
        }

        //频度
        if (cardInfo.getSericeFeq() == 0) {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_SERICEFEQ, 0);
        } else {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_SERICEFEQ, cardInfo.getSericeFeq());
        }
        //是否有卡+卡号
        if (cardInfo.getCardAddress() == null) {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_ADDRESS, "");
        } else {
            SharedPreferencesHelper.put(Constant.SP_CARD_INFO_ADDRESS, cardInfo.getCardAddress());
        }

        if (cardInfo != null && !"2031958".equals(cardInfo.getCardAddress())) {
            settingBdCard.setText("北斗卡号：" + cardInfo.getCardAddress());
            settingSerialNumber.setText("序列号：" + cardInfo.getSerialNum());
            if("N".equals(cardInfo.getCheckEncryption())){
                settingJiami.setText("是否密卡：否");
            }else if("E".equals(cardInfo.getCheckEncryption())){
                settingJiami.setText("是否密卡：是");
            }
            settingSimLevel.setText("通信等级：" + cardInfo.getCommLevel() + "级");
            settingSimMaxLength.setText("最大通信长度：" + Utils.getMessageMaxLength() + " bit");
            settingSericeFeq.setText("服务频度："+cardInfo.getSericeFeq()+"秒");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 与onCreateView相对应,当该Fragment的视图被移除时调用
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @OnClick({R.id.setting_time_layout, R.id.setting_report_layout,
            R.id.setting_RDLocation_layout, R.id.setting_duanyu_layout,
            R.id.setting_custom_layout, R.id.setting_SOS_layout,
            R.id.setting_about_layout})

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_time_layout:
                intent = new Intent(getActivity(), BDTimeActivity.class);
                startActivity(intent);
                break;

            case R.id.setting_report_layout:
                intent = new Intent(getActivity(), ReportSetActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_RDLocation_layout:
                intent = new Intent(getActivity(), RDLocationSetActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_duanyu_layout:
                intent = new Intent(getActivity(), MsgUsalWordActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_custom_layout:
                intent = new Intent(getActivity(), StateCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_SOS_layout:
                intent = new Intent(getActivity(), SoSsetActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_about_layout:
                intent = new Intent(getActivity(), SoftwareActivity.class);
                startActivity(intent);
                break;
        }
    }

}
