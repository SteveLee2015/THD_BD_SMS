package thd.bd.sms.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.location.BDParameterException;
import android.location.BDUnknownException;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thd.cmd.manager.BDCmdManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.MyLocationBean;
import thd.bd.sms.fragment.CommunicationFragment;
import thd.bd.sms.fragment.FriendsFragment;
import thd.bd.sms.fragment.MapFragment;
import thd.bd.sms.fragment.SettingFragment;
import thd.bd.sms.utils.WinUtils;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.main_map_layout)
    LinearLayout mainMapLayout;
    @BindView(R.id.main_communication_layout)
    LinearLayout mainCommunicationLayout;
    @BindView(R.id.main_friends_layout)
    LinearLayout mainFriendsLayout;
    @BindView(R.id.main_setting_layout)
    LinearLayout mainSettingLayout;
    @BindView(R.id.main_map_img)
    ImageView mainMapImg;
    @BindView(R.id.main_communication_img)
    ImageView mainCommunicationImg;
    @BindView(R.id.main_friends_img)
    ImageView mainFriendsImg;
    @BindView(R.id.main_setting_img)
    ImageView mainSettingImg;
    @BindView(R.id.main_center_btn)
    ImageView mainCenterBtn;

    private MapFragment mapFragment;
    private CommunicationFragment communicationFragment;
    private FriendsFragment friendsFragment;
    private SettingFragment settingFragment;

    /**
     * 记录当前Activity显示的fragment
     */
    private Fragment mContent;

    /**
     * FragmentActivity向Fragment传递数据：创建个Bundle数据包，FragmentActivity中Fragment对象调用setArguments(Bundle bundle)方法即可；
     * Fragment接收从FragmentActivity传来的数据：调用getArguments()接收数据包，返回Bundle对象；
     */
    private Bundle bundle;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);

        initContent();

    }


    private void initContent() {

        mainMapImg.setImageResource(R.mipmap.map_clicked);
        mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
        mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
        mainSettingImg.setImageResource(R.mipmap.setting_unclick);

        mapFragment = new MapFragment();
        communicationFragment = new CommunicationFragment();
        friendsFragment = new FriendsFragment();
        settingFragment = new SettingFragment();

        //加载第一个fragment界面
        mContent = mapFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.main_bottom_fragmentLayout, mapFragment).commit();

        bundle = new Bundle();
//        bundle.putDouble("lat",);
//        bundle.putDouble("lng",);
        mContent.setArguments(bundle);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onComLocation(Location location) {
        super.onComLocation(location);

        MyLocationBean myLocationBean = new MyLocationBean();
        myLocationBean.setLatitude(location.getLatitude());
        myLocationBean.setLongitude(location.getLongitude());
        myLocationBean.setTime(location.getTime());

//        Log.e("LERRYTEST_MAP", "=========MainActivity115=======location==" + location.getLatitude() + "," + location.getLongitude());

        EventBus.getDefault().post(myLocationBean);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @OnClick({R.id.main_map_layout, R.id.main_communication_layout, R.id.main_friends_layout,
            R.id.main_setting_layout,R.id.main_center_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_map_layout:
                mainMapImg.setImageResource(R.mipmap.map_clicked);
                mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
                mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
                mainSettingImg.setImageResource(R.mipmap.setting_unclick);

                switchContent_keep(mContent, mapFragment, 0);
                break;

            case R.id.main_communication_layout:
                mainMapImg.setImageResource(R.mipmap.map_unclick);
                mainCommunicationImg.setImageResource(R.mipmap.communication_clicked);
                mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
                mainSettingImg.setImageResource(R.mipmap.setting_unclick);

                switchContent_keep(mContent, communicationFragment, 1);
                break;

            case R.id.main_friends_layout:
                mainMapImg.setImageResource(R.mipmap.map_unclick);
                mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
                mainFriendsImg.setImageResource(R.mipmap.friends_clicked);
                mainSettingImg.setImageResource(R.mipmap.setting_unclick);

                switchContent_keep(mContent, friendsFragment, 2);
                break;

            case R.id.main_setting_layout:
                mainMapImg.setImageResource(R.mipmap.map_unclick);
                mainCommunicationImg.setImageResource(R.mipmap.communication_unclick);
                mainFriendsImg.setImageResource(R.mipmap.friends_unclick);
                mainSettingImg.setImageResource(R.mipmap.setting_clicked);

                switchContent_keep(mContent, settingFragment, 3);
                break;

            case R.id.main_center_btn:
//                Intent intent = new Intent(MainActivity.this, MainCenterActivity.class);
//                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, mainCenterBtn, "shareNames").toBundle());

//                Intent intent = new Intent(MainActivity.this, MainCenterActivity.class);
//                ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,mainCenterBtn, "shareNames");//与xml文件对应
//                ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());

                /**
                 * 在调用了startActivity方法之后立即调用overridePendingTransition方法
                 */
                Intent intent = new Intent(MainActivity.this, MainCenterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pickerview_slide_in_bottom, R.anim.pickerview_slide_out_bottom);

//                startActivity(new Intent(this, MainCenterActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

                break;
        }
    }

    /**
     * fragment切换，不保留之前的状态，每次打开都重新加载
     */
    private void switchContent(Fragment to, int i) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_bottom_fragmentLayout, to);
//        bundle.putString("page",pages[i]);
//        bundle.putString("data",data);
        to.setArguments(bundle);
    }

    /**
     * fragment切换，保留之前的状态，每次打开都不会重新加载
     */
    private void switchContent_keep(Fragment from, Fragment to, int i) {

        if (from != to) {
            mContent = to;

            //先判断是否被add过
            if (!to.isAdded()) {
//                bundle.putString("page",pages[i]);
//                bundle.putString("data",data);
//                Log.i(TAG,"switchContent_keep data= "+data);
                to.setArguments(bundle);

                fragmentList.add(to);

                Log.i(TAG, "MainActivity255: =======未添加过该fragment，添加并隐藏上一个========" );
                // 隐藏当前的fragment，add下一个fragment到Activity中
                getSupportFragmentManager().beginTransaction().hide(from).add(R.id.main_bottom_fragmentLayout, to).commit();
            } else {
                Log.i(TAG, "MainActivity258: =======添加过该fragment要隐藏了========" );
                // 隐藏当前的fragment，显示下一个fragment
                getSupportFragmentManager().beginTransaction().hide(from).show(to).commit();
            }
        }
    }
}
