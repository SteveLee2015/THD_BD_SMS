package thd.bd.sms.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;

import thd.bd.sms.R;
import thd.bd.sms.base.BaseFragment;
import thd.bd.sms.utils.ReceiverAction;

public class ContainerFragment extends BaseFragment {

	private FrameLayout container;
	private LinearLayout friend1;
	private LinearLayout friend2;
	private Context mContext;
	private FragmentManager supportFragmentManager;
	private TextView tvGroup;
	private TextView tvSingle;
	private BadgeView singleBadgeView;
	private BadgeView groupBadgeView;
	
	int tagFlag = 1;

	@Override
	public View initView() {
		mContext = getActivity();
		addReceiver();
		supportFragmentManager = ((FragmentActivity) mActivity)
				.getSupportFragmentManager();
		View view = View.inflate(getActivity(), R.layout.fragment_container, null);
		container = (FrameLayout) view.findViewById(R.id.fl_container);
		friend1 = (LinearLayout) view.findViewById(R.id.ll_friend1_btn);
		friend2 = (LinearLayout) view.findViewById(R.id.ll_friend2_btn);
		tvGroup = (TextView) view.findViewById(R.id.tv_group);
		tvSingle = (TextView) view.findViewById(R.id.tv_single);
		//加载默认的友邻位置
		openFriendFragment1();
		initListener();
		
		singleBadgeView = new BadgeView(mContext,friend1);
		singleBadgeView.setTextColor(Color.BLUE);
		singleBadgeView.setBackgroundColor(Color.YELLOW);
		singleBadgeView.setTextSize(12);
		singleBadgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
		singleBadgeView.setText("new!");
		
		groupBadgeView = new BadgeView(mContext,friend2);
		groupBadgeView.setTextColor(Color.BLUE);
		groupBadgeView.setBackgroundColor(Color.YELLOW);
		groupBadgeView.setTextSize(12);
		groupBadgeView.setGravity(Gravity.TOP | Gravity.RIGHT);
		groupBadgeView.setText("new!");
		
		return view;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(newInfoReceiver);
	}

	private void initListener() {

		friend1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tagFlag = 1;
				openFriendFragment1();
				singleBadgeView.hide();
			}
		});
		
		/*friend2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tagFlag = 2;
				Fragment targetFragment = supportFragmentManager
						.findFragmentByTag("FriendsLcationFragment");
				if (targetFragment == null) {
					targetFragment = new FriendsLcationFragment();
					startFragment(targetFragment, "FriendsLcationFragment");
				}
				groupBadgeView.hide();
			}
		});*/
	}

	private void openFriendFragment1() {
		Fragment targetFragment = supportFragmentManager
				.findFragmentByTag("FriendLcationFragment");
		if (targetFragment == null) {
			targetFragment = new FriendLcationFragment();
		}
		startFragment(targetFragment, "FriendLcationFragment");
	}
	
	private void startFragment(Fragment routeFragment, String fragmentName) {
		FragmentTransaction transaction = supportFragmentManager.beginTransaction();
				   //transaction.replace(R.id.fl_container, routeFragment, fragmentName)
				   transaction.replace(R.id.fl_container, routeFragment, fragmentName)
				   //.addToBackStack("RouteFragment")// 点击回退菜单键的时候
		// 直接退出
		.commit();
	}
	/**
	 * 添加广播
	 */
	private void addReceiver() {

		IntentFilter filter=new IntentFilter();
		filter.addAction(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);
		filter.addAction(ReceiverAction.APP_ACTION_FRIEND_LOCATION_HLT);
		mContext.registerReceiver(newInfoReceiver, filter);
	}
	
	
	/**
	 * 数据更新广播
	 */
	BroadcastReceiver newInfoReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (ReceiverAction.APP_ACTION_FRIEND_LOCATION_21.equals(action)) {
				
				if (singleBadgeView!=null && 2==tagFlag) {
					singleBadgeView.show();
				}
				
			}else if (ReceiverAction.APP_ACTION_FRIEND_LOCATION_HLT.equals(action)) {
				if (groupBadgeView!=null && 1==tagFlag) {
					groupBadgeView.show();
				}
			}
		}
	};
}
