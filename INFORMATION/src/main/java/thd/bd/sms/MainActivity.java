package thd.bd.sms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import thd.bd.sms.Application.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
}
