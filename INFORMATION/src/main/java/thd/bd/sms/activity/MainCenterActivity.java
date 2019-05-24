package thd.bd.sms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.utils.WinUtils;

public class MainCenterActivity extends BaseActivity {
    @BindView(R.id.main_center_boshu_layout)
    LinearLayout mainCenterBoshuLayout;
    @BindView(R.id.main_center_close_img)
    ImageView mainCenterCloseImg;
    @BindView(R.id.main_center_xingtu_layout)
    LinearLayout mainCenterXingtuLayout;

    private Intent intent;

    @Override
    protected int getContentView() {
        return R.layout.activity_main_center;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);
//        //进入退出效果 注意这里 创建的效果对象是 Explode()
        getWindow().setEnterTransition(new Explode().setDuration(2000));
        getWindow().setExitTransition(new Explode().setDuration(2000));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @OnClick({R.id.main_center_boshu_layout, R.id.main_center_close_img,
            R.id.main_center_xingtu_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_center_boshu_layout:
                intent = new Intent(this, BSIActivity.class);
                startActivity(intent);
                break;
            case R.id.main_center_close_img:
                MainCenterActivity.this.finish();
                break;

            case R.id.main_center_xingtu_layout:
                intent = new Intent(this, StatelliteStatusActivity.class);
                startActivity(intent);
                break;
        }
    }

}
