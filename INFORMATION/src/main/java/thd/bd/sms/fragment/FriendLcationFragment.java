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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import thd.bd.sms.R;
import thd.bd.sms.activity.BDContactActivity;
import thd.bd.sms.activity.FriendLocationDetailActivity;
import thd.bd.sms.activity.FriendLocationMapActivity;
import thd.bd.sms.activity.ReplyMessageActivity;
import thd.bd.sms.base.BaseFragment;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.bean.FriendBDPoint;
import thd.bd.sms.database.FriendsLocationDatabaseOperation;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.view.CommomDialogList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 友邻位置
 *
 * @author llg052
 */
public class FriendLcationFragment extends BaseFragment {

    private final String TAG = "FriendLcationFragment";

    private Context mContext;
    private ListView listView;

    private List<FriendBDPoint> list = new ArrayList();
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
                if (oper != null && adapter != null) {
                    list = oper.getAllGroupById();
                    adapter.notifyDataSetChanged();

                    if (list.size()==0) {
                        tvInfo.setVisibility(View.VISIBLE);
                    }else {
                        tvInfo.setVisibility(View.GONE);
                    }
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
        Log.e(TAG, "LERRY_YOULIN=======================FriendLcationFragment86=============list==" + list.size());


        View view = View.inflate(getActivity(), R.layout.activity_friend_loaction, null);
        listView = (ListView) view.findViewById(R.id.lv_friends);
        tvInfo = (TextView) view.findViewById(R.id.tv_info);
        if (list.size() == 0) {
            tvInfo.setVisibility(View.VISIBLE);
        } else {
            tvInfo.setVisibility(View.GONE);
        }
        adapter = new FriendsAdapter();
        listView.setAdapter(adapter);
        initListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //取消notification
        if (mActivity != null) {
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

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);
        mContext.registerReceiver(newInfoReceiver, filter);
    }

    private void initListener() {

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                final int index = position;
                final String[] arrayFruit = mContext.getResources().getStringArray(R.array.friend_loc_oper);

                new CommomDialogList(getActivity(), R.style.dialog_aa, arrayFruit, new CommomDialogList.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, int position) {
                        dialog.dismiss();
                        switch (position){
                            case 0://回复短报文
                                FriendBDPoint bdPoint1 = list.get(index);
                                String friendID = bdPoint1.getFriendID();
                                while (friendID.startsWith("0")){
                                    friendID = friendID.substring(1);
                                }
                                Intent intent1 = new Intent(getActivity(),ReplyMessageActivity.class);
                                intent1.putExtra(ReceiverAction.KEY_BD_FRIEND_ID, friendID);
                                Log.e(TAG, "onClick: ============FriendLocationDetailActivity185==========="+friendID );
                                intent1.putExtra("PHONE_NUMBER",friendID);
                                intent1.putExtra("MESSAGE_FLAG", "0");
                                //intent.putExtra("MESSAGE_ID", id);
                                intent1.putExtra(Config.INTENT_TYPE, Config.REPLY_DIALOG);
                                startActivity(intent1);
                                break;

                            case 1://删除
                                //从数据库中删除数据
                                FriendBDPoint bdPoint = list.get(index);
                                String address = bdPoint.getFriendID();
                                boolean istrue = oper.deleteAllGroupByAddress(address);
                                oper.close();
                                if (istrue) {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_success), Toast.LENGTH_SHORT).show();
                                    list.remove(index);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_fail), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                break;

                            case 2://全部删除
                                //全部删除
                                boolean istrue1 = oper.delete();
                                oper.close();
                                if (istrue1) {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_success), Toast.LENGTH_SHORT).show();
                                    list.clear();
                                    adapter.notifyDataSetChanged();
                                    if (list.size() == 0) {
                                        tvInfo.setVisibility(View.VISIBLE);
                                    } else {
                                        tvInfo.setVisibility(View.GONE);
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.friend_loc_del_fail), Toast.LENGTH_SHORT).show();
                                }

                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                break;
                        }
                    }
                }).show();

                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FriendLocationDetailActivity.class);
                intent.putExtra("friend_address", list.get(position).getFriendID());
                startActivity(intent);
            }
        });
    }


    /**
     * 数据适配器
     *
     * @author llg052
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

                viewHolder.item_bdloc_friends_center_layout = (LinearLayout)contentView.findViewById(R.id.item_bdloc_friends_center_layout);
                viewHolder.item_bdloc_friends_center_layout.setVisibility(View.GONE);

                viewHolder.company = (TextView) contentView
                        .findViewById(R.id.item_friends_company_txt);
                viewHolder.company.setText("友邻ID  ");
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
                int count = oper.getCountByAddress(list.get(position)
                        .getFriendID());

                viewHolder.height.setText(count+"");
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
        LinearLayout item_bdloc_friends_center_layout;

        TextView company, idTxt, count;
    }
}
