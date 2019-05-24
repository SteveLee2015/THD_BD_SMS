package thd.bd.sms.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import thd.bd.sms.R;
import thd.bd.sms.adapter.FragmentAdapter;

public class FriendsFragment extends Fragment {

    private ViewPager tabpager;
    private ImageView mTabImg;// 动画图片
    private View mTab1View, mTab2View, mTab3View;
    private LinearLayout mView1, mView2, mView3;
    private int zero = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int one;//单个水平动画位移
    private int two;

    FragmentAdapter adapter;
    ArrayList<Fragment> fragmentsList;
    private TextView tv_tb1;
    private TextView tv_tb2;
    private TextView tv_tb3;
//    private TextView tv_tb4;

    private BadgeView friendBadgeView;//友邻位置
    private BadgeView instrucNaviBadgeView;//指令导航
    private BadgeView lineNaviBadgeView;//线路导航
    private int item=0;
    private View view;

    private final String TAG = "FriendsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这里不能这么写
        if (getActivity().getIntent() != null) {
            item = getActivity().getIntent().getIntExtra("friend_location", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends, container, false);
//        unbinder = ButterKnife.bind(this, view);
//        EventBus.getDefault().register(this);

        initView();

        return view;

    }

    private void initView() {
        tabpager = (ViewPager) view.findViewById(R.id.tabpager);
        tabpager.setOnPageChangeListener(new MyOnPageChangeListener());

        //下标线view
        mTab1View = (View) view.findViewById(R.id.tab_friends_loaction);
        mTab2View = (View) view.findViewById(R.id.tab_instruct_navi);
        mTab3View = (View) view.findViewById(R.id.tab_line_navi);

        //头标view
        mView1 = (LinearLayout) view.findViewById(R.id.friends_tab_view);
        mView2 = (LinearLayout) view.findViewById(R.id.instruct_tab_view);
        mView3 = (LinearLayout) view.findViewById(R.id.line_tab_view);

        tv_tb1 = (TextView) view.findViewById(R.id.tv_tb1);
        tv_tb2 = (TextView) view.findViewById(R.id.tv_tb2);
        tv_tb3 = (TextView) view.findViewById(R.id.tv_tb3);

        //
        tv_tb1.setTextColor(Color.BLUE);

        friendBadgeView = new BadgeView(getActivity(), tv_tb1);
        friendBadgeView.setTextColor(Color.BLUE);
        friendBadgeView.setBackgroundColor(Color.YELLOW);
        friendBadgeView.setTextSize(12);
        friendBadgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
        friendBadgeView.setText("new!");

        instrucNaviBadgeView = new BadgeView(getActivity(), tv_tb2);
        instrucNaviBadgeView.setTextColor(Color.BLUE);
        instrucNaviBadgeView.setBackgroundColor(Color.YELLOW);
        instrucNaviBadgeView.setTextSize(12);
        instrucNaviBadgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
        instrucNaviBadgeView.setText("new!");

        lineNaviBadgeView = new BadgeView(getActivity(), tv_tb3);
        lineNaviBadgeView.setTextColor(Color.BLUE);
        lineNaviBadgeView.setBackgroundColor(Color.YELLOW);
        lineNaviBadgeView.setTextSize(12);
        lineNaviBadgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
        lineNaviBadgeView.setText("new!");

        mTabImg = (ImageView) view.findViewById(R.id.img_tab_now);
        mView1.setOnClickListener(new MyOnClickListener(0));
        mView2.setOnClickListener(new MyOnClickListener(1));
        mView3.setOnClickListener(new MyOnClickListener(2));

//        mTitleName = (TextView) findViewById(R.id.sub_title_name);
        Display currDisplay = getActivity().getWindowManager().getDefaultDisplay();//获取屏幕当前分辨率
        int displayWidth = currDisplay.getWidth();
        int displayHeight = currDisplay.getHeight();
        one = displayWidth / 4; //设置水平动画平移大小
        two = one * 2;
        initViewPage(item);
    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            tabpager.setCurrentItem(index);
        }
    }

    //选项卡切换
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            Log.e(TAG, "onPageSelected: =====================arg0=="+arg0+"=========currIndex=="+currIndex );
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (friendBadgeView != null) {
                        friendBadgeView.hide();
                    }
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                        mTab2View.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        tv_tb2.setTextColor(getResources().getColor(R.color.colorBlack));
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                        mTab3View.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        tv_tb3.setTextColor(getResources().getColor(R.color.colorBlack));
                    }
                    Log.e(TAG, "onPageSelected: ========当前是第一页========" );
                    break;
                case 1:
                    if (instrucNaviBadgeView != null) {
                        instrucNaviBadgeView.hide();
                    }
                    mTab2View.setBackgroundColor(getResources().getColor(R.color.colorLine));
                    tv_tb2.setTextColor(getResources().getColor(R.color.colorLine));
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(zero, one, 0, 0);
                        mTab1View.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        tv_tb1.setTextColor(getResources().getColor(R.color.colorBlack));
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                        mTab3View.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        tv_tb3.setTextColor(getResources().getColor(R.color.colorBlack));
                    }
                    Log.e(TAG, "onPageSelected: ========当前是第2页========" );
                    break;
                case 2:
                    if (lineNaviBadgeView != null) {
                        lineNaviBadgeView.hide();
                    }
                    mTab3View.setBackgroundColor(getResources().getColor(R.color.colorLine));
                    tv_tb3.setTextColor(getResources().getColor(R.color.colorLine));
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(zero, two, 0, 0);
                        mTab1View.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        tv_tb1.setTextColor(getResources().getColor(R.color.colorBlack));
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                        mTab2View.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        tv_tb2.setTextColor(getResources().getColor(R.color.colorBlack));
                    }
                    Log.e(TAG, "onPageSelected: ========当前是第3页========" );
                    break;
            }
            currIndex = arg0;

            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(150);
            mTabImg.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }
    }


    //初始化 viewpage
    public void initViewPage(int item) {
        fragmentsList = new ArrayList<Fragment>();
        fragmentsList.add(new ContainerFragment());
        fragmentsList.add(new InstructNaviFragment());
        fragmentsList.add(new LineFragment());
        adapter = new FragmentAdapter(getChildFragmentManager(), fragmentsList);
        tabpager.setOffscreenPageLimit(2);
        tabpager.setAdapter(adapter);

        tabpager.setCurrentItem(item);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        unbinder.unbind();
    }

}
