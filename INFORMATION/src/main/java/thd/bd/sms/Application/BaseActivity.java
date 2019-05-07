package thd.bd.sms.Application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import thd.bd.sms.crashUtils.WinUtils;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        WinUtils.hiddenKeyBoard(this);

        unbinder = ButterKnife.bind(this);
    }

    abstract protected int getContentView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }
}
