package thd.bd.sms.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import thd.bd.sms.R;
import thd.bd.sms.activity.FriendLocationDetailActivity;
import thd.bd.sms.activity.FriendLocationMapActivity;
import thd.bd.sms.base.BaseFragment;
import thd.bd.sms.bean.FriendBDPoint;
import thd.bd.sms.database.FriendsLocationDatabaseOperation;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.ReceiverAction;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 友邻位置
 * @author llg052
 * 
 */
public class FriendLcationFragment extends BaseFragment {

	private final String TAG = "FriendLcationFragment";
	
	private Context mContext;
	private ListView listView;

	private List<FriendBDPoint> list= new ArrayList();
	private FriendsLocationDatabaseOperation oper;
	private FriendsAdapter adapter;
	private TextView tvInfo;



	/**
	 * 数据更新广播
	 */
	BroadcastReceiver newInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (ReceiverAction.APP_ACTION_FRIEND_LOCATION_21.equals(action)) {
				//更新数据
				if (oper!=null && adapter !=null) {
					list=oper.getAllGroupById();
					adapter.notifyDataSetChanged();
				}

			}
		}
	};

	@Override
	public View initView() {
		mContext = getActivity();
		addReceiver();
		oper = new FriendsLocationDatabaseOperation(mContext);
		list = oper.getAllGroupById();
		Log.e(TAG, "LERRY_YOULIN=======================FriendLcationFragment86=============list=="+list.size() );


		View view = View.inflate(getActivity(),R.layout.activity_friend_loaction, null);
		listView = (ListView) view.findViewById(R.id.lv_friends);
		tvInfo = (TextView) view.findViewById(R.id.tv_info);
		if (list.size()==0) {
			tvInfo.setVisibility(View.VISIBLE);
		}else {
			tvInfo.setVisibility(View.GONE);
		}
		adapter=new FriendsAdapter();
		listView.setAdapter(adapter);
		initListener();

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		//取消notification
		if (mActivity!=null){
			NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(Config.NOTIFICATION_LOC_REPORT);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(newInfoReceiver);
	}

	/**
	 * 添加广播
	 */
	private void addReceiver() {

		IntentFilter filter=new IntentFilter();
		filter.addAction(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);
		mContext.registerReceiver(newInfoReceiver, filter);
	}

	private void initListener() {

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
				final int index=position;
				final String[] arrayFruit = mContext.getResources().getStringArray(R.array.friend_loc_oper);
				Dialog dialog = new AlertDialog.Builder(mContext).setTitle("位置报告")
                .setItems(arrayFruit, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int which) {
                		Toast.makeText(mContext, arrayFruit[which], Toast.LENGTH_SHORT).show();
                		if(which==1){

							Log.e("LERRYTEST_MAP" ,"=========FriendLcationFragment115====点击了地图显示=====");

                			//显示地图
                			String endName = String.valueOf(list.get(index).getFriendID());
//            				String lonStr = String.valueOf(list.get(index).get);
//            				String latStr = String.valueOf(list.get(index).get("FRIEND_LAT"));
//            				double lon = Double.parseDouble(lonStr);
//            				double lat = Double.parseDouble(latStr);
                			
							// 方案I 发送地图显示广播
                			/*Intent intent = new Intent();
                			intent.setAction(ReceiverAction.ACTION_BD_SHOW_IN_MAP);
                			intent.putExtra(ReceiverAction.KEY_BD_FRIEND_POINT_LAT, lat);
                			intent.putExtra(ReceiverAction.KEY_BD_FRIEND_POINT_LON, lon);
                			intent.putExtra(ReceiverAction.KEY_BD_FRIEND_ID, endName);
                			mActivity.sendBroadcast(intent);*/

//							Intent intent = new Intent(getActivity(),FriendLocationMapActivity.class);
//							intent.putExtra("latitude",String.valueOf(list.get(position).get(
//									"FRIEND_LAT")));
//							intent.putExtra("longitude",String.valueOf(list.get(position).get(
//									"FRIEND_LON")));
//							startActivity(intent);
//
//
//							Log.e("LERRYTEST_MAP", "=========FriendLcationFragment141================"+
//									list.get(position).get("FRIEND_LAT")+","+list.get(position).get("FRIEND_LON"));

							/*//方案II 调用远程服务
							SMSapp app = (SMSapp) mActivity.getApplication();

							//IMapService mapService = app.mapService;
							try {
								app.showInMap_(lat,lon,endName);
							} catch (RemoteException e) {
								e.printStackTrace();
							}*/


						}else if (which==0) {
                			//导航 路径规划
                			/*String endName = String.valueOf(list.get(index).get("FRIEND_ID"));
            				String lonStr = String.valueOf(list.get(index).get("FRIEND_LON"));
            				String latStr = String.valueOf(list.get(index).get("FRIEND_LAT"));
            				double lon = Double.parseDouble(lonStr);
            				double lat = Double.parseDouble(latStr);*/
                			
							/*//发送导航广播
                			Intent intent = new Intent();
                			intent.setAction(ReceiverAction.ACTION_BD_NAVIGATION);
                			intent.putExtra(ReceiverAction.KEY_BD_NAVIGATION_END_POINT_LAT, lat);
                			intent.putExtra(ReceiverAction.KEY_BD_NAVIGATION_END_POINT_LON, lon);
                			intent.putExtra(ReceiverAction.KEY_BD_NAVIGATION_END_NAME, endName);
                			mActivity.sendBroadcast(intent);*/

//							goToBaiduMap(39.919625,116.403969,"");
							//goToBaiduMap(Double.parseDouble(latStr),Double.parseDouble(lonStr),"");
                			
						}else if (which==4) {
                			//全部删除
                			boolean istrue=oper.delete();
                			oper.close();
                			if(istrue){
                				Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_success), Toast.LENGTH_SHORT).show();
                				list.clear();
                				adapter.notifyDataSetChanged();
                			}else{
                				Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_fail), Toast.LENGTH_SHORT).show();
                			}
							
						}else if (which ==2) {
							//回复短报文
							//Toast.makeText(mContext, "跳转到短报文界面", 0).show();
//							String friendID = String.valueOf(list.get(index).get("FRIEND_ID"));
//							while (friendID.startsWith("0")){
//								friendID = friendID.substring(1);
//							}
//							Intent intent = new Intent(mActivity,ReplyMessageActivity.class);
//							intent.putExtra(ReceiverAction.KEY_BD_FRIEND_ID, friendID);
//							intent.putExtra("PHONE_NUMBER",friendID);
//							intent.putExtra("MESSAGE_FLAG", "0");
//							//intent.putExtra("MESSAGE_ID", id);
//							intent.putExtra(Config.INTENT_TYPE, Config.REPLY_DIALOG);
//							startActivity(intent);

						}else if (which ==3) {
                			//从数据库中删除数据
							FriendBDPoint bdPoint = list.get(index);
                			String address = bdPoint.getFriendID();
                			boolean istrue=oper.deleteAllGroupByAddress(address);
                			oper.close();
                			if(istrue){
                				Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_success), Toast.LENGTH_SHORT).show();
                				list.remove(index);
                				adapter.notifyDataSetChanged();
                			}else{
                				Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_fail), Toast.LENGTH_SHORT).show();
                			}
                		}
                    }      
                 })        
                .setNegativeButton(mContext.getResources().getString(R.string.common_cancle_btn), 
                		new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {}
                 }).create();  dialog.show();
             	return false;
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(),FriendLocationDetailActivity.class);
				intent.putExtra("friend_address",list.get(position).getFriendID());
				startActivity(intent);
			}
		});
	}



	/**
	 * 跳转百度地图
	 */
	/*private void goToBaiduMap(double mLat,double mLng,String mAddressStr) {
		if (!isInstalled("com.baidu.BaiduMap")) {
			Toast.makeText(getActivity(),"请先安装百度地图客户端",Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent();
		intent.setData(Uri.parse("baidumap://map/direction?destination=latlng:"
				+ mLat + ","
				+ mLng + "|name:" + mAddressStr + // 终点
				"&mode=driving" + // 导航路线方式
				"&src=andr.baidu.openAPIdemo"));
		startActivity(intent); // 启动调用
	}*/


	/**
	 * 检测程序是否安装
	 *
	 * @param packageName
	 * @return
	 */
	/*private boolean isInstalled(String packageName) {
		PackageManager manager = mContext.getPackageManager();
		//获取所有已安装程序的包信息
		List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
		if (installedPackages != null) {
			for (PackageInfo info : installedPackages) {
				if (info.packageName.equals(packageName))
					return true;
			}
		}
		return false;
	}*/


	/**
	 * 数据适配器
	 * @author llg052
	 * 
	 */
	public class FriendsAdapter extends BaseAdapter {

		private ViewHolder viewHolder = null;
		private LayoutInflater mInflater = null;

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View contentView, ViewGroup parent) {
			if (contentView == null) {
				viewHolder = new ViewHolder();
				mInflater = LayoutInflater.from(mContext);
				contentView = mInflater.inflate(R.layout.item_bdloc_friends,
						null);
				viewHolder.userId = (TextView) contentView
						.findViewById(R.id.friends_loc_user_address_tx);
				viewHolder.time = (TextView) contentView
						.findViewById(R.id.friends_loc_time);
				viewHolder.lon = (TextView) contentView
						.findViewById(R.id.friends_loc_lon);
				viewHolder.lat = (TextView) contentView
						.findViewById(R.id.friends_loc_lat);
				viewHolder.height = (TextView) contentView
						.findViewById(R.id.friends_loc_height);


				viewHolder.company = (TextView) contentView
						.findViewById(R.id.item_friends_company_txt);
				viewHolder.company.setText("单位");
				viewHolder.idTxt = (TextView) contentView
						.findViewById(R.id.item_friends_id_txt);
				viewHolder.idTxt.setText("友邻ID");
				viewHolder.count = (TextView) contentView
						.findViewById(R.id.item_friends_count_txt);
				viewHolder.count.setText("数量");


				contentView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) contentView.getTag();
			}
			if (list != null) {
				viewHolder.userId.setText(String.valueOf(list.get(position)
						.getFriendID()));
				viewHolder.time.setText(String.valueOf(list.get(position).getReceiveTime()));
//				viewHolder.lon.setText(String.valueOf(list.get(position).get(
//						"FRIEND_LON")));
//				viewHolder.lat.setText(String.valueOf(list.get(position).get(
//						"FRIEND_LAT")));
//				viewHolder.height.setText(String.valueOf(list.get(position)
//						.get("FRIEND_HEIGHT")));
			}
			return contentView;
		}

		public void deleteAllData() {
			if (list != null) {
				list.clear();
			}
		}

	}

	public static class ViewHolder {
		TextView userId;
		TextView time;
		TextView lon;
		TextView lat;
		TextView height;

		TextView company,idTxt,count;
	}
}
