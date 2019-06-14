package thd.bd.sms.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.BDCache;
import thd.bd.sms.database.RDCacheOperation;
import thd.bd.sms.service.CycleReportSOSService;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.SysUtils;

public class MsgdbActivity extends BaseActivity {

    private static final String TAG = "MsgdbActivity";
    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.tv_chcheInfo)
    TextView tvChcheInfo;
    @BindView(R.id.lv_msg_db_cache)
    ListView msgDbCacheListview;
    private RDCacheOperation cacheOperation;
    private CachebdAdapter cachebdAdapter;
    private List<BDCache> lists = new ArrayList<BDCache>();

    @Override
    protected int getContentView() {
        return R.layout.activity_msg_db;
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //测试
        //String crashNullException = null;
        //crashNullException.charAt(1);

        addReceiver();
        getUI();
        getData();
        initListeners();

    }

    /**
     * 注册广播
     */
    private void addReceiver() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD);
        registerReceiver(dbChangeReceiver, filter);
    }

    private void initListeners() {
        // 全部删除
        msgDbCacheListview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MsgdbActivity.this);
                builder.setTitle("全部删除");
                builder.setMessage("确定要全部取消报文发送吗?");
                builder.setCancelable(false);
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                cacheOperation.deleteAll();
                                cachebdAdapter.deleteAllMessageData();
                                cachebdAdapter.notifyDataSetChanged();

                                int count = cacheOperation.getCount();
                                SharedPreferencesHelper.put(Constant.SP_RECORDED_KEY_COUNT, count);

                                //通知数据库变化广播
                                notifyData();

                                dialog.dismiss();

                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return false;
            }
        });

    }

    private void getData() {
        try {
            cacheOperation = new RDCacheOperation(this);
            lists = cacheOperation.getAll();
            cachebdAdapter = new CachebdAdapter();
            msgDbCacheListview.setAdapter(cachebdAdapter);
            int size = SharedPreferencesHelper.getRecordedCount();
            String info = "还有" + size + "条等待发送";
            if (tvChcheInfo != null) {
                tvChcheInfo.setText(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUI() {
        titleName.setText("北斗短报文服务");
    }

    /**
     * 通知数据变化
     */
    private void notifyData() {
        Intent intent = new Intent();
//		intent.setAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_DEL);
        intent.setAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD);
        /*if(Build.VERSION.SDK_INT >= 26) {
            ComponentName componentName=new ComponentName(getApplicationContext(),"thd.bd.sms.activity.dbChangeReceiver");//参数1-包名 参数2-广播接收者所在的路径名
            intent.setComponent(componentName);
//            intent.addFlags(0x01000000);//加上这句话，可以解决在android8.0系统以上2个module之间发送广播接收不到的问题}
        }*/
        sendBroadcast(intent);
    }

    private void writeAbout() {
        int size = SharedPreferencesHelper.getRecordedCount();
        String info = "还有" + size + "条等待发送";
        if (tvChcheInfo != null) {
            tvChcheInfo.setText(info);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dbChangeReceiver);
        String clssName = CycleReportSOSService.class.getName();
        boolean isStart = SysUtils.isServiceRunning(MsgdbActivity.this, clssName);

        if (isStart) {
            Intent sosIntent = new Intent(MsgdbActivity.this, SOSActivity.class);
            startActivity(sosIntent);
        }
    }

    ;
    /**
     * 数据库变化广播
     *
     * @author llg052
     */

    BroadcastReceiver dbChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD.equals(action)) {
                // 数据库变化了,重新查询数据库
                lists = cacheOperation.getAll();
                cachebdAdapter.notifyDataSetChanged();
                writeAbout();
            }
        }

    };

    @OnClick(R.id.return_home_layout)
    public void onViewClicked() {
        MsgdbActivity.this.finish();
    }
    // /////////adapter开始////////

    public class CachebdAdapter extends BaseAdapter implements OnClickListener {

        private ViewHolder viewHolder = null;
        public List<BDCache> toRemoveItems;
        public View ll_checked_title;


        /**
         * 获得总长度
         */
        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /**
         * 每个Item的View
         */
        @Override
        public View getView(final int position, View contentView,
                            ViewGroup parent) {

            if (contentView == null) {
                viewHolder = new ViewHolder();
                contentView = LayoutInflater.from(MsgdbActivity.this)
                        .inflate(R.layout.item_bdcache_msg, null);
                viewHolder.sendId = (TextView) contentView
                        .findViewById(R.id.msg_send_id);
                viewHolder.content = (TextView) contentView
                        .findViewById(R.id.msg_send_content);
                viewHolder.date = (TextView) contentView
                        .findViewById(R.id.msg_send_date);
//				viewHolder.flag = (ImageView) contentView
//						.findViewById(R.id.msg_flag_icon);
                viewHolder.tvCancel = (TextView) contentView
                        .findViewById(R.id.tv_cancel);
                viewHolder.llChecked = (LinearLayout) contentView
                        .findViewById(R.id.ll_checked);
                contentView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) contentView.getTag();
            }
            viewHolder.sendId.setText(lists.get(position).getSendAddress());
            viewHolder.content.setText(lists.get(position).getMsgContent());
            viewHolder.date.setText("");
            viewHolder.llChecked.setTag(position);

            viewHolder.llChecked.setOnClickListener(this);
            return contentView;
        }

        /**
         * 删除List中所有的短信内容
         */
        public void deleteAllMessageData() {
            if (lists != null) {
                lists.clear();
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_checked:
                    // 弹出对话框
                    final Integer position = (Integer) v.getTag();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MsgdbActivity.this);
                    builder.setTitle("取消发送");
                    builder.setMessage("确定要取消该报文吗?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    BDCache bdCache = lists.get(position);
                                    if (bdCache != null) {
                                        cacheOperation.delete(bdCache);
                                        int count = cacheOperation.getCount();
                                        SharedPreferencesHelper.put(Constant.SP_RECORDED_KEY_COUNT, count);
                                        lists = cacheOperation.getAll();
                                        CachebdAdapter.this.notifyDataSetChanged();
                                        notifyData();
                                    }
                                    dialog.dismiss();

                                }
                            });
                    builder.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();

                    break;

                default:
                    break;
            }
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

//		/**
//		 * 向下的图标
//		 * */
//		ImageView downIcon;
//		/**
//		 * 转发短信
//		 */
//		LinearLayout zhuanFaMsg;
//		/**
//		 * 删除短信
//		 */
//		LinearLayout delMsg;
//		/**
//		 * 是否可见
//		 */
//		LinearLayout visibleOper;
        /**
         * 取消发送父布局
         */
        LinearLayout llChecked;
        /**
         * 取消发送
         */
        TextView tvCancel;

    }

}