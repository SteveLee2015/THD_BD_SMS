package thd.bd.sms.activity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import thd.bd.sms.R;
import thd.bd.sms.adapter.FragmentAdapter;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.fragment.ReportSetFragmentRD;
import thd.bd.sms.fragment.ReportSetFragmentRN;
import thd.bd.sms.fragment.ReportSetFragmentStatus;
import thd.bd.sms.utils.WinUtils;


/**
 * 连续报告策略设置
 * 
 * @author lerry
 */
public class ReportSetActivity extends BaseActivity {

	private ViewPager mTabPager;
	private TextView mTab1;
	private TextView mTab2;
	private TextView mTab3;
	private LinearLayout mView1;
	private LinearLayout mView2;
	private LinearLayout mView3;
	private ImageView mTabImg;

	private TextView tv_tb1;
	private TextView tv_tb2;
	private TextView tv_tb3;


	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;//单个水平动画位移
	private int two;
	private int three;

	FragmentAdapter adapter;
	ArrayList<Fragment> fragmentsList;

	@Override
	protected int getContentView() {
		return R.layout.activity_report_set;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WinUtils.setWinTitleColor(this);
		super.onCreate(savedInstanceState);

		Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息

		mTabPager = (ViewPager)findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		//下标线view
		mTab1 = (TextView) findViewById(R.id.tab_message_list);
		mTab2 = (TextView) findViewById(R.id.tab_instruct_navi);
		mTab3 = (TextView) findViewById(R.id.tab_line_navi);

		//头标view
		mView1 = (LinearLayout) findViewById(R.id.message_tab_view);
		mView2 = (LinearLayout) findViewById(R.id.instruct_tab_view);
		mView3 = (LinearLayout) findViewById(R.id.line_tab_view);

		tv_tb1 = (TextView) findViewById(R.id.tv_tb1);
		tv_tb2 = (TextView) findViewById(R.id.tv_tb2);
		tv_tb3 = (TextView) findViewById(R.id.tv_tb3);

		//
		tv_tb1.setTextColor(Color.BLUE);



		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		mView1.setOnClickListener(new MyOnClickListener(0));
		mView2.setOnClickListener(new MyOnClickListener(1));
		mView3.setOnClickListener(new MyOnClickListener(2));
		Display currDisplay = getWindowManager().getDefaultDisplay();//获取屏幕当前分辨率
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = (int)(displayWidth*0.9)/3; //设置水平动画平移大小
		two = one*2;
		three = one*3;
		initViewPage();
	}


	//初始化 viewpage
	public void initViewPage(){
		fragmentsList = new ArrayList<Fragment>();
		fragmentsList.add(new ReportSetFragmentStatus());
		fragmentsList.add(new ReportSetFragmentRN());
		fragmentsList.add(new ReportSetFragmentRD());
		adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentsList);
		mTabPager.setOffscreenPageLimit(2);
		mTabPager.setAdapter(adapter);
		mTabPager.setCurrentItem(0);
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
			mTabPager.setCurrentItem(index);
		}
	};
	//选项卡切换
	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
				case 0:

					mTab1.setBackgroundColor(getResources().getColor(R.color.colorLine));
					tv_tb1.setTextColor(getResources().getColor(R.color.colorLine));
					if (currIndex == 1) {
						animation = new TranslateAnimation(one, 0, 0, 0);
						mTab2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
						tv_tb2.setTextColor(getResources().getColor(R.color.colorBlack));
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, 0, 0, 0);
						mTab3.setBackgroundColor(getResources().getColor(R.color.colorWhite));
						tv_tb3.setTextColor(getResources().getColor(R.color.colorBlack));
					}
					break;
				case 1:

					mTab2.setBackgroundColor(getResources().getColor(R.color.colorLine));
					tv_tb2.setTextColor(getResources().getColor(R.color.colorLine));
					if (currIndex == 0) {
						animation = new TranslateAnimation(zero, one, 0, 0);
						mTab1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
						tv_tb1.setTextColor(getResources().getColor(R.color.colorBlack));
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, one, 0, 0);
						mTab3.setBackgroundColor(getResources().getColor(R.color.colorWhite));
						tv_tb3.setTextColor(getResources().getColor(R.color.colorBlack));
					}
					break;
				case 2:

					mTab3.setBackgroundColor(getResources().getColor(R.color.colorLine));
					tv_tb3.setTextColor(getResources().getColor(R.color.colorLine));
					if (currIndex == 0) {
						animation = new TranslateAnimation(zero, two, 0, 0);
						mTab1.setBackgroundColor(getResources().getColor(R.color.colorWhite));
						tv_tb1.setTextColor(getResources().getColor(R.color.colorBlack));
					} else if (currIndex == 1) {
						animation = new TranslateAnimation(one, two, 0, 0);
						mTab2.setBackgroundColor(getResources().getColor(R.color.colorWhite));
						tv_tb2.setTextColor(getResources().getColor(R.color.colorBlack));
					}
					break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(350);
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

}
