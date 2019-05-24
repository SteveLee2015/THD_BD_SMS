package thd.bd.sms.fragment;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import thd.bd.sms.R;
import thd.bd.sms.activity.BDContactActivity;
import thd.bd.sms.activity.ReplyMessageActivity;
import thd.bd.sms.adapter.SendMsgAdapter;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.bean.Item;
import thd.bd.sms.database.BDMessageDatabaseOperation;
import thd.bd.sms.database.DataBaseHelper.BDMessageColumns;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.Utils;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CommunicationFragment extends Fragment {

    private static final String TAG = "CommunicationFragment";

    public static final int TRANSMIT_MSG = 10086;
    public static final int STATE_CODE_MSG = 10087;

    Context context;
    @BindView(R.id.message_listview)
    ListView messageListview;
    @BindView(R.id.communication_add_msg_layout)
    LinearLayout communicationAddMsgLayout;
    @BindView(R.id.communication_contant_layout)
    LinearLayout communicationContantLayout;

    private SendMsgAdapter mSendMsgAdapter = null;
    private List<Item> items = null;// 聊天列表数据
    List<Item> toRemoveItems = new ArrayList<Item>();
    private BDMessageDatabaseOperation operation = null;
    private List<Map<String, Object>> listMsg = null;//?
    private View view;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_communication, container, false);

//        EventBus.getDefault().register(this);

        unbinder = ButterKnife.bind(this, view);

        context = getActivity();

        operation = new BDMessageDatabaseOperation(context);
        if (items == null) {
            items = new ArrayList<Item>();
        }
        Cursor cursor = operation.getHomeMessages();
        showListView(cursor);
        cursor.close();

        addReceiver();

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (items == null) {
            items = new ArrayList<Item>();
        }
        Cursor cursor = operation.getHomeMessages();
        showListView(cursor);
        cursor.close();

        //取消notification
        if (getActivity() != null) {
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(Config.NOTIFICATION_SMS);
        }
    }

    //短信列表List展示
    private void showListView(Cursor cursor) {

        //获取收/发件列表
        final List<Map<String, Object>> mapList = operation.build(cursor);
        // 把数据转为 item对象 放到items集合中
        items.clear();
        for (Map<String, Object> map : mapList) {

            Item item = operation.object2Item(map);
            items.add(item);

        }
        /* 2.把数据转换成List */
        mSendMsgAdapter = new SendMsgAdapter(context, items);

        messageListview.setChoiceMode(messageListview.CHOICE_MODE_SINGLE);
        messageListview.setAdapter(mSendMsgAdapter);

        Cursor mCursor = operation.getHomeMessages();
        listMsg = new ArrayList<Map<String, Object>>();

        //填充数据
        listMsg = operation.iniMsgData(mCursor);

        messageListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id2) {
                String phoneNumber = String.valueOf(listMsg.get(position).get(BDMessageColumns.COLUMNS_USER_ADDRESS));
                String userName = String.valueOf(listMsg.get(position).get(BDMessageColumns.COLUMNS_USER_NAME));
                String flag = String.valueOf(listMsg.get(position).get(BDMessageColumns.COLUMNS_FLAG));
                long id = Long.valueOf(String.valueOf(listMsg.get(position).get(BDMessageColumns._ID)));
                Intent intent = new Intent();
                if (userName != null && !"".equals(userName)) {
                    intent.putExtra("PHONE_NUMBER", userName + "(" + phoneNumber + ")");
                } else {
                    intent.putExtra("PHONE_NUMBER", phoneNumber);
                }
                intent.putExtra("MESSAGE_FLAG", flag);
                intent.putExtra("MESSAGE_ID", id);
                intent.putExtra(Config.INTENT_TYPE, Config.REPLY_DIALOG);
                intent.setClass(context, ReplyMessageActivity.class);
                startActivity(intent);

            }
        });

        messageListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("删除短信");
                alert.setMessage("是否删除该条短信?");
                alert.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int index1) {

                        Map<String, Object> selectObj = listMsg.get(position);
                        String phoneNumber = String.valueOf(selectObj.get(BDMessageColumns.COLUMNS_USER_ADDRESS));
                        String userName = String.valueOf(selectObj.get(BDMessageColumns.COLUMNS_USER_NAME));
                        String flag = String.valueOf(selectObj.get(BDMessageColumns.COLUMNS_FLAG));
                        long id = Long.valueOf(String.valueOf(selectObj.get(BDMessageColumns._ID)));

                        boolean istrue = operation.delete(phoneNumber);
                        if (istrue) {
                            //list.remove(index);

                            Cursor cursor = operation.getHomeMessages();
                            showListView(cursor);

                            Toast.makeText(getActivity(), "聊天记录删除成功!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "聊天记录删除失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNeutralButton("全部删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean istrue = operation.delete();
                        if (istrue) {
                            items.clear();
                            mSendMsgAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "清除全部聊天记录成功!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "清除全部聊天记录失败!", Toast.LENGTH_SHORT).show();
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

        if (mSendMsgAdapter != null) {
            mSendMsgAdapter.notifyDataSetChanged();
            return;
        }

    }


    private void deletePhoneNumberMsg() {
        // 遍历 checked 为true 删除
        int k = items.size();

        for (int i = 0; i < k; i++) {
            if (items.get(i).checked) {// 被选中
                // 删除 数据库数据
                String phonenumber = items.get(i).send_id;
                operation.delete(phonenumber);
                toRemoveItems.add(items.get(i));
            }
        }

        items.removeAll(toRemoveItems);
        toRemoveItems.clear();

        // 更新界面
        mSendMsgAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPause() {
        super.onPause();
        Utils.checkBoxSelect = false;
        mSendMsgAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(smsDataChangReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 与onCreateView相对应,当该Fragment的视图被移除时调用
//        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    private void addReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.setPriority(ReceiverAction.BD_PRIORITY_C);
        filter.addAction(ReceiverAction.APP_ACTION_SMS_REFRESH);
        getActivity().registerReceiver(smsDataChangReceiver, filter);

        IntentFilter filterSendSmS = new IntentFilter();
        filterSendSmS.addAction(ReceiverAction.APP_ACTION_SMS_NEW_DIALOG);
        filterSendSmS.setPriority(ReceiverAction.BD_PRIORITY_E);
        getActivity().registerReceiver(smsDataChangReceiver, filterSendSmS);
    }


    /**
     * 短报文数据变化广播
     */
    BroadcastReceiver smsDataChangReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action) {
                case ReceiverAction.APP_ACTION_SMS_NEW_DIALOG: {

                    onResume();
                    break;
                }
                case ReceiverAction.APP_ACTION_SMS_REFRESH: {
                    //数据库更新了 更新ui
                    Cursor cursor = operation.getHomeMessages();
                    showListView(cursor);
                    cursor.close();
                    //获取发件号码
                    break;
                }
            }
        }
    };

    @OnClick({R.id.communication_add_msg_layout, R.id.communication_contant_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.communication_contant_layout:
                Intent intent = new Intent();
                intent.setClass(getActivity(), BDContactActivity.class);
                intent.setData(BDContactColumn.CONTENT_URI);
                intent.putExtra(Config.NEED_BACK, false);
                getActivity().startActivity(intent);
                break;

            case R.id.communication_add_msg_layout:
                Intent intent1 = new Intent(context, ReplyMessageActivity.class);
                intent1.putExtra(Config.INTENT_TYPE, Config.NEW_DIALOG);
                startActivity(intent1);
                break;
        }

    }
}
