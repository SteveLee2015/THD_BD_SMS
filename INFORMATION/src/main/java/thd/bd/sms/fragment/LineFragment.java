package thd.bd.sms.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import thd.bd.sms.R;
import thd.bd.sms.base.BaseFragment;
import thd.bd.sms.bean.BDLineNav;
import thd.bd.sms.bean.BDPoint;
import thd.bd.sms.database.BDLineNavOperation;
import thd.bd.sms.utils.CollectionUtils;
import thd.bd.sms.utils.ReceiverAction;

/**
 * 线路导航
 *
 * @author llg052
 */
public class LineFragment extends BaseFragment {


    private Context mContext;
    private BDLineNavOperation operation;
    private TextView tvInfo;
    private ListView listView;
    private List<BDLineNav> lineNavLists = new ArrayList<>();
    private LineTaskAdapter mLineTaskAdapter;
    private String newNavId = "";

    @Override
    public View initView() {
        mContext = getActivity();
        addReceiver();
        operation = new BDLineNavOperation(mContext);
        View view = View.inflate(getActivity(), R.layout.fragment_line_navi, null);
        listView = (ListView) view.findViewById(R.id.lv_lineNavi);
        tvInfo = (TextView) view.findViewById(R.id.tv_lineNavi);


        initListener();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //lineNavLists = operation.getNavLineList();
        getData();

        int size = operation.getSize();
        mLineTaskAdapter = new LineTaskAdapter();
        listView.setAdapter(mLineTaskAdapter);

        if (lineNavLists.size() == 0) {
            tvInfo.setVisibility(View.VISIBLE);
        } else {
            tvInfo.setVisibility(View.GONE);
        }

        if (lineNavLists.size() > 100) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示");
            builder.setMessage("线路导航数量超过100条,请删除不必要的指令导航信息!");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void initListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final BDLineNav bdLineNav = lineNavLists.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("查看路线导航信息");
                builder.setMessage("路线ID:" + bdLineNav.getLineId() + "\n" + "路线点:\n" + bdLineNav.getPassPointsString());
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("导航", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        navigation(bdLineNav);

                    }
                });
                builder.create().show();
            }
        });

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int position, long arg3) {
                final BDLineNav bdLineNav = lineNavLists.get(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("删除路线导航");
                alert.setMessage("是否删除该条路线导航?");
                alert.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int index) {
                        boolean istrue = operation.delete(bdLineNav.getLineId());
                        if (istrue) {
                            lineNavLists.remove(position);
                            mLineTaskAdapter.notifyDataSetChanged();
                            Toast.makeText(mContext, "删除路线导航成功!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "删除路线导航失败!", Toast.LENGTH_SHORT).show();
                        }
                        notificationListview();
                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
                alert.setNeutralButton("全部删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean istrue = operation.delete();
                        if (istrue) {
                            lineNavLists.clear();
                            mLineTaskAdapter.notifyDataSetChanged();
                            Toast.makeText(mContext, "删除路线导航成功!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "删除路线导航失败!", Toast.LENGTH_SHORT).show();
                        }
                        notificationListview();
                    }
                });
                alert.create().show();
                return true;
            }
        });
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
     * 添加广播
     */
    private void addReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.APP_ACTION_LINE_NAVI);
        mContext.registerReceiver(newInfoReceiver, filter);
    }

    /**
     * 数据更新广播
     */
    BroadcastReceiver newInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ReceiverAction.APP_ACTION_LINE_NAVI.equals(action)) {
                notificationListview();
            }
        }
    };

    private void notificationListview() {
        //更新数据

        getData();

        if (mLineTaskAdapter != null) {
            mLineTaskAdapter.notifyDataSetChanged();
        }
        if (tvInfo != null) {
            tvInfo.setVisibility(View.GONE);
        }
    }

    private void getData() {

        if (operation != null) {

            //先获取对应 线路id
            List<String> navLineIds = operation.getNavLineIds();

            //去除重复元素
            List list = CollectionUtils.removeDuplicate(navLineIds);
            navLineIds = list;


            //根据线路id 得到完整  线路
            if (navLineIds == null) return;
            lineNavLists.clear();

            for (String navLineId : navLineIds) {
                boolean complete = operation.checkLineNavComplete(navLineId);

                if (complete) {

                    //得到所有的条序号
                    List<Integer> currentIndexs = operation.getNavLineCurrentIndexsByLineId(navLineId);
                    if (currentIndexs == null) return;

                    Collections.sort(currentIndexs);

                    ArrayList<BDPoint> allPassPoints = new ArrayList<>();

                    //遍历 条序号
                    for (int currentIndex : currentIndexs) {
                        //根据 线路id  和  条序号 找到对应的 途经点(途经点是有顺序的)
                        //得到 条序号对应的点
                        List<BDPoint> passPoints = operation.getNavLinePassPointsByLineId(navLineId, currentIndex + "");
                        //List list2 = CollectionUtils.removeDuplicate(passPoints);
                        //passPoints = list2;

                        allPassPoints.addAll(passPoints);
                    }
                    BDLineNav mBDLineNav = new BDLineNav();



                    String createTime = operation.getCreateTimeByLineId(navLineId);
                    if (!TextUtils.isEmpty(createTime)) {
                        mBDLineNav.setCreateTime(createTime);
                    }


                    mBDLineNav.setLineId(navLineId);
                    mBDLineNav.setPassPoints(allPassPoints);
                    lineNavLists.add(mBDLineNav);
                }
            }
        }
    }


    public class LineTaskAdapter extends BaseAdapter {


        private ViewHolder viewHolder = null;
        private LayoutInflater mInflater = null;

        /**
         * 构造方法
         */
        public LineTaskAdapter() {
            this.mInflater = LayoutInflater.from(mContext);
        }


        public int getCount() {
            return lineNavLists.size();
        }


        public Object getItem(int arg0) {
            return lineNavLists.get(arg0);
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(final int position, View contentView, ViewGroup parent) {
            if (contentView == null) {
                viewHolder = new ViewHolder();
                contentView = mInflater.inflate(R.layout.item_line_task_msg, null);
                viewHolder.sendId = (TextView) contentView.findViewById(R.id.item_nav_line_id);
                viewHolder.content = (TextView) contentView.findViewById(R.id.item_nav_line_content);
                viewHolder.date = (TextView) contentView.findViewById(R.id.item_nav_line_date);
                viewHolder.naviBtn = (Button) contentView.findViewById(R.id.item_navi_line_btn);
                viewHolder.navLineImage = (ImageView) contentView.findViewById(R.id.nav_line_imageview);
                contentView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) contentView.getTag();
            }
            final BDLineNav nav = lineNavLists.get(position);
            String createTime = nav.getCreateTime();
            if (!TextUtils.isEmpty(createTime)) {
                viewHolder.date.setText(createTime);
            }
            viewHolder.sendId.setText("路线ID:" + String.valueOf(nav.getLineId()));
            viewHolder.content.setText("路线点:" + nav.getPassPointsString());
            if (newNavId.equals(nav.getLineId())) {
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bak_for_other);
                viewHolder.navLineImage.setImageBitmap(bm);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bak_for_other);
                viewHolder.navLineImage.setImageBitmap(bm);
            }
            viewHolder.naviBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {


//                    navigation(nav);

                    //Intent notificationIntent = new Intent(mContext, TestMapActivity.class);
                    //String lineId = nav.getLineId();
                    //notificationIntent.putExtra("LINE_ID", (!"".equals(lineId)) ? Integer.valueOf(lineId) : 0);
                    //startActivity(notificationIntent);
                }
            });
            return contentView;
        }

    }

//    //发送导航广播
//    private void navigation(BDLineNav nav) {
//        Intent intent = new Intent();
//        intent.setAction(ReceiverAction.ACTION_BD_NAVIGATION_LINE);
//        intent.putExtra(ReceiverAction.KEY_BD_NAVIGATION_LINE, nav);
//        mActivity.sendBroadcast(intent);
//    }


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
