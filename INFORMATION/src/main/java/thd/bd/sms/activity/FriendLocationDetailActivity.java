package thd.bd.sms.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.database.FriendsLocationDatabaseOperation;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.view.CommomDialogList;

public class FriendLocationDetailActivity extends BaseActivity {

    private final String TAG = "FriendLocationDetailActivity";
    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;

    private ListView listView;

    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private FriendsLocationDatabaseOperation oper;
    private FriendsAdapter adapter;
    private TextView tvInfo;
    private String address;
    private RelativeLayout message_title;

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
                    list = oper.getAllByAddress(address);
                    adapter.notifyDataSetChanged();
                }

            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_friend_loaction;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);

        initView();
    }

    @SuppressLint("LongLogTag")
    public void initView() {
        address = getIntent().getStringExtra("friend_address");

        addReceiver();
        oper = new FriendsLocationDatabaseOperation(this);
        list = oper.getAllByAddress(address);

        message_title = (RelativeLayout) findViewById(R.id.message_title);
        message_title.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.lv_friends);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        if (list.size() == 0) {
            tvInfo.setVisibility(View.VISIBLE);
        } else {
            tvInfo.setVisibility(View.GONE);
        }
        titleName.setText("友邻位置详情");
        adapter = new FriendsAdapter();
        listView.setAdapter(adapter);
        initListener();

    }


    @Override
    public void onResume() {
        super.onResume();
        //取消notification
        if (FriendLocationDetailActivity.this != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(Config.NOTIFICATION_LOC_REPORT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(newInfoReceiver);
    }

    /**
     * 添加广播
     */
    private void addReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);
        registerReceiver(newInfoReceiver, filter);
    }

    private void initListener() {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                final int index = position;
                final String[] arrayFruit = getResources().getStringArray(R.array.friend_loc_oper_detail);

                new CommomDialogList(FriendLocationDetailActivity.this, R.style.dialog_aa, arrayFruit, new CommomDialogList.OnCloseListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onClick(Dialog dialog, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0://导航
                                String lonStr = String.valueOf(list.get(index).get("FRIEND_LON"));
                                String latStr = String.valueOf(list.get(index).get("FRIEND_LAT"));
                                SysUtils.goToBaiduMap(FriendLocationDetailActivity.this, Double.parseDouble(latStr), Double.parseDouble(lonStr), "友邻位置");
                                break;

                            case 1://地图显示
                                //显示地图

                                Intent intent = new Intent(FriendLocationDetailActivity.this, FriendLocationMapActivity.class);
                                intent.putExtra("latitude", String.valueOf(list.get(index).get(
                                        "FRIEND_LAT")));
                                intent.putExtra("longitude", String.valueOf(list.get(index).get(
                                        "FRIEND_LON")));

                                startActivity(intent);

                                break;

                            case 2://回复短报文
                                String friendID = String.valueOf(list.get(index).get("FRIEND_ID"));
                                while (friendID.startsWith("0")) {
                                    friendID = friendID.substring(1);
                                }
                                Intent intent1 = new Intent(FriendLocationDetailActivity.this, ReplyMessageActivity.class);
                                intent1.putExtra(ReceiverAction.KEY_BD_FRIEND_ID, friendID);
                                Log.e(TAG, "onClick: ============FriendLocationDetailActivity185===========" + friendID);
                                intent1.putExtra("PHONE_NUMBER", friendID);
                                intent1.putExtra("MESSAGE_FLAG", "0");
                                //intent.putExtra("MESSAGE_ID", id);
                                intent1.putExtra(Config.INTENT_TYPE, Config.REPLY_DIALOG);
                                startActivity(intent1);
                                break;

                            case 3://删除
                                //从数据库中删除数据
                                //从数据库中删除数据
                                Map<String, Object> map = list.get(index);
                                String id = String.valueOf(map.get("F_ID"));
                                boolean istrue = oper.delete(Long.valueOf(id));
                                oper.close();
                                if (istrue) {
                                    Toast.makeText(FriendLocationDetailActivity.this, getResources().getString(R.string.friend_loc_del_success), Toast.LENGTH_SHORT).show();
                                    list.remove(index);
                                    adapter.notifyDataSetChanged();
                                    notifcation(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);
                                } else {
                                    Toast.makeText(FriendLocationDetailActivity.this, getResources().getString(R.string.friend_loc_del_fail), Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                                break;

                            case 4://全部删除
                                //全部删除
                                boolean istrue1 = oper.delete();
                                oper.close();
                                if (istrue1) {
                                    Toast.makeText(FriendLocationDetailActivity.this, getResources().getString(R.string.friend_loc_del_success), Toast.LENGTH_SHORT).show();
                                    list.clear();
                                    adapter.notifyDataSetChanged();
                                    notifcation(ReceiverAction.APP_ACTION_FRIEND_LOCATION_21);

                                } else {
                                    Toast.makeText(FriendLocationDetailActivity.this, getResources().getString(R.string.friend_loc_del_fail), Toast.LENGTH_SHORT).show();
                                }

                                adapter.notifyDataSetChanged();
                                FriendLocationDetailActivity.this.finish();
                                dialog.dismiss();
                                break;
                        }
                    }
                }).show();


                return false;
            }
        });
    }

    /**
     * 通知数据有更新
     */
    private void notifcation(String action) {

        Intent intent = new Intent();
        intent.setAction(action);
        intent.setAction(ReceiverAction.APP_ACTION_SMS_REFRESH);
        if (Build.VERSION.SDK_INT >= 26) {
            ComponentName componentName = new ComponentName(getApplicationContext(), "");//参数1-包名 参数2-广播接收者所在的路径名
            intent.setComponent(componentName);
//            intent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
        }
        sendBroadcast(intent);

    }

    @OnClick(R.id.return_home_layout)
    public void onViewClicked() {
        FriendLocationDetailActivity.this.finish();
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
                mInflater = LayoutInflater.from(FriendLocationDetailActivity.this);
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

                contentView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) contentView.getTag();
            }
            if (list != null) {
                viewHolder.userId.setText(String.valueOf(list.get(position)
                        .get("FRIEND_ID")));
                viewHolder.time.setText(String.valueOf(list.get(position).get(
                        "FRIEND_REPORT_TIME")));
                viewHolder.lon.setText(String.valueOf(list.get(position).get(
                        "FRIEND_LON")));
                viewHolder.lat.setText(String.valueOf(list.get(position).get(
                        "FRIEND_LAT")));
                viewHolder.height.setText(String.valueOf(list.get(position)
                        .get("FRIEND_HEIGHT")));
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

    }
}
