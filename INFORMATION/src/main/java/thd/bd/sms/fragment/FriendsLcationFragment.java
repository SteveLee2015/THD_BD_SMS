package thd.bd.sms.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import thd.bd.sms.R;
import thd.bd.sms.adapter.DialogAdapter;
import thd.bd.sms.base.BaseFragment;
import thd.bd.sms.bean.FriendBDPoint;
import thd.bd.sms.database.BDFriendLocationOperation;
import thd.bd.sms.utils.ReceiverAction;

/**
 * 友邻位置 深圳海力特 fuck
 *
 * @author llg
 */
public class FriendsLcationFragment extends BaseFragment {
    /**
     * 日志标识
     */
    private static final String TAG = "FriendsLcationFragment";
    /**
     * 导航任务的ListView对象
     */
    private ExpandableListView expandistView = null;
    /**
     * 绑定在ListView组件的所有短信数据适配器对象
     */
    private FriendLocationTaskAdapter mFriendLocationTaskAdapter = null;
    private TextView noLinePrompt = null;

    private Context mContext;

    private BDFriendLocationOperation operation = null;

    private List<FriendBDPoint> lists = null;

    private List<String> receiveTimeList = new ArrayList<String>();


    @Override
    public View initView() {
        mContext = getActivity();
        addReceiver();
        View view = View.inflate(getActivity(), R.layout.fragment_friends_loaction, null);
        operation = new BDFriendLocationOperation(mContext);
        doUi(view);

        return view;
    }

    public void doUi(View view) {
        expandistView = (ExpandableListView) view.findViewById(R.id.nav_line_expand_listview);
        expandistView.setCacheColorHint(0);
        expandistView.setGroupIndicator(null);
        noLinePrompt = (TextView) view.findViewById(R.id.no_nav_line_prompt);

    }


    @Override
    public void onResume() {
        super.onResume();
        /* 1.从数据库中查询所有的短信数据,如果数据库没有数据则发送指令请求最新插入的数据 */
        lists = operation.getAll();
        if (mFriendLocationTaskAdapter == null) {
            mFriendLocationTaskAdapter = new FriendLocationTaskAdapter(mContext);
        }
        if (lists.size() > 100) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示");
            builder.setMessage("友邻位置数量超过100条,请删除不必要的友邻位置信息!");
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
        if (lists.size() > 0) {
            noLinePrompt.setVisibility(View.GONE);
            /* 2.把数据转换成List */
            expandistView.setAdapter(mFriendLocationTaskAdapter);
            /* 增加选项 */
            // 父条目的点击事件 长按时间 子条目的点击事件 长按事件

            // 子条目点击事件
            expandistView.setOnChildClickListener(new OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    return false;
                }
            });

            // 父条目点击事件 展开 与 不展开
            expandistView.setOnGroupClickListener(new OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {

                    Toast.makeText(mContext, groupPosition + "", Toast.LENGTH_SHORT).show();
                    return true;
                }

            });

        } else {
            noLinePrompt.setText("当前没有友邻位置相关信息！");
            noLinePrompt.setVisibility(View.VISIBLE);
        }
        refreshData();
    }

    private void refreshData() {
        List<String> receiveTime = operation.getReceiveTime();
        Set<String> hasSet = new HashSet<String>(receiveTime);
        receiveTimeList.clear();
        receiveTimeList.addAll(hasSet);
        Collections.sort(receiveTimeList);
        Collections.reverse(receiveTimeList);
    }

    /**
     * 添加广播
     */
    private void addReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.APP_ACTION_FRIEND_LOCATION_HLT);
        mContext.registerReceiver(newInfoReceiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (operation != null) {
            operation.close();
            operation = null;
        }
        mContext.unregisterReceiver(newInfoReceiver);
    }

    /**
     * 数据更新广播
     */
    BroadcastReceiver newInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ReceiverAction.APP_ACTION_FRIEND_LOCATION_HLT.equals(action)) {

                //更新数据
                if (operation != null && mFriendLocationTaskAdapter != null) {
                    lists = operation.getAll();
                    refreshData();
                    mFriendLocationTaskAdapter.notifyDataSetChanged();
                    if (noLinePrompt != null) {
                        noLinePrompt.setVisibility(View.GONE);
                    }
                }

            }
        }
    };

    /**
     * 数据适配器
     *
     * @author llg052
     */
    public class FriendLocationTaskAdapter extends BaseExpandableListAdapter {

        private ViewHolder viewHolder = null;
        private Context mContext = null;
        private LayoutInflater mInflater = null;
        private String newNavId = "";


        /**
         * 构造方法
         *
         * @param mContext
         */
        public FriendLocationTaskAdapter(Context mContext) {
            this.mContext = mContext;
            this.mInflater = LayoutInflater.from(mContext);
        }

        /**
         * 父标签总数 时间
         */
        @Override
        public int getGroupCount() {

            return receiveTimeList.size();
        }

        /**
         * 对应时间节点下的子条目
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            String receiveTime = receiveTimeList.get(groupPosition);
            List<FriendBDPoint> byReceiveTime = operation
                    .getByReceiveTime(receiveTime);
            return byReceiveTime.size();
        }

        /**
         * 父标签
         */
        @Override
        public Object getGroup(int groupPosition) {
            String receiveTime = receiveTimeList.get(groupPosition);
            return receiveTime;
        }

        /**
         * 子标签
         */
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String receiveTime = receiveTimeList.get(groupPosition);
            List<FriendBDPoint> byReceiveTime = operation
                    .getByReceiveTime(receiveTime);
            FriendBDPoint friendBDPoint = byReceiveTime.get(childPosition);
            return friendBDPoint;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /**
         * 子标签条目id
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        /**
         *
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * 父布局 设置tag
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(mContext,
                        R.layout.item_parent_friends_location, null);
            }
            TextView parentView = (TextView) convertView
                    .findViewById(R.id.parent);
            TextView show_all_btn = (TextView) convertView
                    .findViewById(R.id.btn_show_all);
            TextView btn_parent_del = (TextView) convertView
                    .findViewById(R.id.btn_parent_del);
            TextView btn_parent_show_info = (TextView) convertView
                    .findViewById(R.id.btn_parent_show_info);
            final String receiveTime = receiveTimeList.get(groupPosition);
            parentView.setText(receiveTime);
            // 地图展示逻辑
            show_all_btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 从数据库中获取 友邻位置集合
                    ArrayList<FriendBDPoint> friendLocationList = (ArrayList<FriendBDPoint>) operation
                            .getByReceiveTime(receiveTime);
                    // 跳转到地图界面 地图界面添加友邻位置

//                    String endName = String.valueOf(list.get(index).get("FRIEND_ID"));
//                    String lonStr = String.valueOf(list.get(index).get("FRIEND_LON"));
//                    String latStr = String.valueOf(list.get(index).get("FRIEND_LAT"));
//                    double lon = Double.parseDouble(lonStr);
//                    double lat = Double.parseDouble(latStr);

//                    // 方案I 发送地图显示广播
//                    Intent intent = new Intent();
//                    intent.setAction(ReceiverAction.ACTION_BD_SHOW_IN_MAP_TEAM);
//                    intent.putExtra(ReceiverAction.KEY_BD_FRIEND_MEMBERS, friendLocationList);
//                    mActivity.sendBroadcast(intent);


                }
            });

            // 删除该条友邻
            btn_parent_del.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            mContext);
                    alert.setTitle("删除友邻位置");
                    alert.setMessage("是否删除该条友邻位置?");
                    alert.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int index) {
                                    boolean delete = operation
                                            .delete(receiveTime);
                                    if (delete) {
                                        receiveTimeList.remove(receiveTime);
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext, "删除友邻位置成功!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "删除友邻位置失败!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    alert.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                }
                            });
                    alert.setNeutralButton("全部删除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    boolean istrue = operation.delete();
                                    if (istrue) {
                                        receiveTimeList.clear();
                                        notifyDataSetChanged();
                                        Toast.makeText(mContext, "删除路线导航成功!",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, "删除路线导航失败!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    alert.create().show();

                }
            });

            // 详情展示
            btn_parent_show_info.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final List<FriendBDPoint> detalInfoList = operation
                            .getByReceiveTime(receiveTime);

                    View layout = View.inflate(mContext,
                            R.layout.item_detail_friends_location, null);

                    final AlertDialog.Builder alert = new AlertDialog.Builder(
                            mContext);
                    alert.setTitle("友邻位置");
                    // alert.setMessage("该条友邻位置详情");
                    alert.setView(layout);
                    alert.setCancelable(true);
                    final AlertDialog alertDialog = alert.create();

                    ListView lv_detal = (ListView) layout
                            .findViewById(R.id.lv_detal);

                    // 设置数据适配器
                    lv_detal.setAdapter(new DialogAdapter(mContext,
                            detalInfoList));

                    lv_detal.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            // 条目点击事件

                            FriendBDPoint friendBDPoint = detalInfoList
                                    .get(position);

                            if (friendBDPoint == null) return;

                            //显示地图
                            String endName = friendBDPoint.getFriendID();
                            String lonStr = friendBDPoint.getLon();
                            String latStr = friendBDPoint.getLat();
                            double lon = Double.parseDouble(lonStr);
                            double lat = Double.parseDouble(latStr);

                            //显示地图

                            // 方案I 发送地图显示广播
//                            Intent intent = new Intent();
//                            intent.setAction(ReceiverAction.ACTION_BD_SHOW_IN_MAP);
//                            intent.putExtra(ReceiverAction.KEY_BD_FRIEND_POINT_LAT, lat);
//                            intent.putExtra(ReceiverAction.KEY_BD_FRIEND_POINT_LON, lon);
//                            intent.putExtra(ReceiverAction.KEY_BD_FRIEND_ID, endName);
//                            mActivity.sendBroadcast(intent);
//
//
//                            //方案II 调用远程服务
//                            SMSapp app = (SMSapp) mActivity.getApplication();
//
//                            //IMapService mapService = app.mapService;
//                            try {
//                                app.showInMap_(lat, lon, endName);
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }
            });

            return convertView;
        }

        /**
         * 子布局
         */
        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.item_friend_location_task_msg, null);
                viewHolder.sendId = (TextView) convertView
                        .findViewById(R.id.item_nav_line_id);
                viewHolder.content = (TextView) convertView
                        .findViewById(R.id.item_nav_line_content);
                viewHolder.date = (TextView) convertView
                        .findViewById(R.id.item_nav_line_date);
                viewHolder.naviBtn = (Button) convertView
                        .findViewById(R.id.item_navi_line_btn);
                viewHolder.navLineImage = (ImageView) convertView
                        .findViewById(R.id.nav_line_imageview);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            List<String> receiveTime = operation.getReceiveTime();
            String receiveTime2 = receiveTime.get(groupPosition);
            final List<FriendBDPoint> listChild = operation
                    .getByReceiveTime(receiveTime2);
            final FriendBDPoint nav = listChild.get(childPosition);
            viewHolder.sendId.setText("友邻ID:"
                    + String.valueOf(nav.getFriendID()));
            viewHolder.content.setText("位置信息:" + nav.getLon() + " "
                    + nav.getLat());
            if (newNavId.equals(nav.getFriendID())) {
                Bitmap bm = BitmapFactory.decodeResource(
                        mContext.getResources(), R.drawable.bak_for_other);
                viewHolder.navLineImage.setImageBitmap(bm);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(
                        mContext.getResources(),
                        R.drawable.bak_for_other);
                viewHolder.navLineImage.setImageBitmap(bm);
            }
            viewHolder.naviBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    // 地图显示 显示的是单个友邻
                    // Intent notificationIntent = new Intent(mContext,
                    // NaviStudioActivity.class);
                    // String lineId = nav.getFriendID();
                    // notificationIntent.putExtra("LINE_ID",
                    // (!"".equals(lineId)) ? Integer.valueOf(lineId) : 0);
                    // mContext.startActivity(notificationIntent);

//                    Intent notificationIntent = new Intent(mContext,
//                            TestMapActivity.class);
//                    FriendBDPoint friendBDPoint = listChild.get(childPosition);
//                    long rowId = friendBDPoint.getRowId();
//                    notificationIntent.putExtra("RERPORT_ROW_ID", rowId);
//                    mContext.startActivity(notificationIntent);
                }
            });
            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    public static class ViewHolder {
        /**
         * 发送ID
         */
        TextView sendId;
        /**
         * 信息大小
         */
        TextView size;
        /**
         * 内容
         */
        TextView content;
        /**
         * 日期
         */
        TextView date;
        /**
         * 导航按钮
         */
        Button naviBtn;
        /**
         * 图片
         */
        ImageView navLineImage;
    }

}
