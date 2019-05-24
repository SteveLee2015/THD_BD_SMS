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
import java.util.List;

import thd.bd.sms.R;
import thd.bd.sms.base.BaseFragment;
import thd.bd.sms.bean.BDInstructionNav;
import thd.bd.sms.database.BDInstructionNavOperation;
import thd.bd.sms.utils.ReceiverAction;

/**
 * 指令导航
 *
 * @author llg052
 */
public class InstructNaviFragment extends BaseFragment {

    private ListView listView;
    private TextView tvInfo;
    private Context mContext;
    private BDInstructionNavOperation operation;
    private List<BDInstructionNav> list = new ArrayList<BDInstructionNav>();
    private NaviTaskAdapter adapter;
    private long newNavId = 0l;

    @Override
    public View initView() {
        mContext = getActivity();
        addReceiver();
        View view = View.inflate(getActivity(), R.layout.fragment_instruct_navi, null);
        listView = (ListView) view.findViewById(R.id.lv_instruction);
        tvInfo = (TextView) view.findViewById(R.id.tv_instruction);
        operation = new BDInstructionNavOperation(mContext);
        list = operation.getAll();
        if (list.size() > 100) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("提示");
            builder.setMessage("指令导航数量超过100条,请删除不必要的指令导航信息!");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        if (list.size() == 0) {
            tvInfo.setVisibility(View.VISIBLE);
        } else {
            tvInfo.setVisibility(View.GONE);
        }
        adapter = new NaviTaskAdapter();
        listView.setAdapter(adapter);

        initListener();
        return view;
    }

    /**
     * 添加广播
     */
    private void addReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.APP_ACTION_INSTRUCT_NAVI);
        mContext.registerReceiver(newInfoReceiver, filter);
    }

    /**
     * 一些事件处理
     */
    private void initListener() {


        //String formatLon = String.format("%.6f", bdInstructionNav.getTargetPoint().getLat());

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BDInstructionNav bdInstructionNav = list.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("查看指令导航信息");
                builder.setMessage(
                        "指令ID:" + bdInstructionNav.getLineId() + "\r\n" +
                                "目标点:\r\n" + String.format("%.6f", bdInstructionNav.getTargetPoint().getLon()) + "," + bdInstructionNav.getTargetPoint().getLonDirection() + "," + String.format("%.6f", bdInstructionNav.getTargetPoint().getLat()) + "," + bdInstructionNav.getTargetPoint().getLatDirection() + ",\r\n" +
                                "途经点:\r\n" + bdInstructionNav.getPassPointsString() + ",\r\n" +
                                "规避点:\r\n" + bdInstructionNav.getEvadePointsString());
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("导航", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        navigation(bdInstructionNav);
                    }
                });
                builder.create().show();
            }
        });

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int index, long arg3) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("删除短信");
                alert.setMessage("是否删除该条短信?");
                alert.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int index1) {
                        boolean istrue = operation.delete(list.get(index).getRowId());
                        if (istrue) {
                            list.remove(index);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(mContext, "删除指令导航成功!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "删除指令导航失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNeutralButton("全部删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean istrue = operation.delete();
                        if (istrue) {
                            list.clear();
                            adapter.notifyDataSetChanged();
                            Toast.makeText(mContext, "全部删除指令导航成功!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "全部删除指令导航失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alert.show();
                return true;
            }
        });


    }

    @Override
    public void initData() {
        super.initData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (operation != null) {
            operation.close();
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
            if (ReceiverAction.APP_ACTION_INSTRUCT_NAVI.equals(action)) {
                //更新数据
                if (operation != null && adapter != null) {
                    list = operation.getAll();
                    adapter.notifyDataSetChanged();
                    if (tvInfo != null) {
                        tvInfo.setVisibility(View.GONE);
                    }
                }

            }
        }
    };

    public class NaviTaskAdapter extends BaseAdapter {

        private ViewHolder viewHolder = null;
        private LayoutInflater mInflater = null;

        /**
         * 构造方法
         */
        public NaviTaskAdapter() {
            this.mInflater = LayoutInflater.from(mContext);
        }


        public int getCount() {
            return list.size();
        }


        public Object getItem(int arg0) {
            return list.get(arg0);
        }


        public long getItemId(int arg0) {
            return 0;
        }

        public View getView(final int position, View contentView, ViewGroup parent) {
            if (contentView == null) {
                viewHolder = new ViewHolder();
                contentView = mInflater.inflate(R.layout.item_navi_task_msg, null);
                viewHolder.sendId = (TextView) contentView.findViewById(R.id.msg_send_id);
                viewHolder.content = (TextView) contentView.findViewById(R.id.msg_send_content);
                viewHolder.date = (TextView) contentView.findViewById(R.id.msg_send_date);
                viewHolder.naviBtn = (Button) contentView.findViewById(R.id.start_navi_btn);
                viewHolder.imageView = (ImageView) contentView.findViewById(R.id.navi_task_imageview);
                contentView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) contentView.getTag();
            }
            final BDInstructionNav nav = list.get(position);
            if (newNavId == nav.getRowId()) {
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bak_for_other);
                viewHolder.imageView.setImageBitmap(bm);
            } else {
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bak_for_other);
                viewHolder.imageView.setImageBitmap(bm);
            }
            String createTime = nav.getCreateTime();
            if (!TextUtils.isEmpty(createTime)) {
                viewHolder.date.setText(createTime);
            }

            viewHolder.sendId.setText("指令ID:" + String.valueOf(nav.getLineId()));
            //String.format("%.6f", bdInstructionNav.getTargetPoint().getLon())
            viewHolder.content.setText("目标点:" + String.format("%.6f", nav.getTargetPoint().getLat()) + "," + String.format("%.6f", nav.getTargetPoint().getLon()) + ",途经点:" + nav.getPassPointsString() + ",规避点:" + nav.getEvadePointsString());
            viewHolder.naviBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

//                    navigation(nav);

//					Intent notificationIntent = new Intent(mContext,TestMapActivity.class);
//					long rowId = nav.getRowId();
//					notificationIntent.putExtra("NAVI_ID",rowId);
//					startActivity(notificationIntent);
                }
            });
            return contentView;
        }
    }

    // 发送导航广播
//    private void navigation(BDInstructionNav nav) {
//        Intent intent = new Intent();
//        intent.setAction(ReceiverAction.ACTION_BD_NAVIGATION_INSTRUCTIONS);
//        intent.putExtra(ReceiverAction.KEY_BD_NAVIGATION_INSTRUCTIONS, nav);
////        intent.setPackage("navigation.ns.com.navigation");
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

        ImageView imageView;
    }
}
