package thd.bd.sms.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.BDParameterException;
import android.location.BDUnknownException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thd.cmd.manager.BDCmdManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thd.bd.sms.R;
import thd.bd.sms.adapter.MsgUsalWordAdapter;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.BDCache;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.bean.BDMessageInfo;
import thd.bd.sms.bean.UsalMsg;
import thd.bd.sms.database.BDMessageDatabaseOperation;
import thd.bd.sms.database.DataBaseHelper.BDMessageColumns;
import thd.bd.sms.database.MessgeUsualOperation;
import thd.bd.sms.database.RDCacheOperation;
import thd.bd.sms.service.CycleLocService;
import thd.bd.sms.service.CycleReportRDLocService;
import thd.bd.sms.service.CycleReportRNLocService;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.DBhelper;
import thd.bd.sms.utils.ReceiverAction;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.utils.WinUtils;
import thd.bd.sms.view.CommomDialogList;

/**
 * 北斗短信详情页面
 *
 * @author llg
 */
public class ReplyMessageActivity extends BaseActivity implements OnClickListener, OnLayoutChangeListener {


    protected static final String TAG = "ReplyMessageActivity";
    private RDCacheOperation cacheOperation;
    private EditText messageContent;//发送内容
    private ListView mBDDetailsMessageList = null;
    private LinearLayout returnLayout = null;
    private Cursor mCursor = null;
    private int visibleItemCount = 10;
    private int visibleStartIndex = 0;
    private int visibleLastIndex = visibleItemCount;
    private int currentSelectNum = 0;
    private TextView titleName = null;
    private TextView title = null;
    private int screenWidth = 0;
    private String phoneNumber = "";
    private String mUserName = "";
    private String phoneName = "";
    private List<Map<String, Object>> listData = null;
    private DetailsMessageAdapter detailsAdapter = null;
    private ClipboardManager clipboardManager = null;
    private long draftID = 0;
    private boolean isSendMessage = false;
    private String messageFlag = "";
    private int totalNum = 0;
    private BDMessageDatabaseOperation messageOperation = null;
    private final int REQUEST_CONTACT = 1, BD_CURR_DEVICE_MODE = 0x10002;
    private static final String BD_MESSAAGE_COUNT_PREFERENCE_NAME = "BD_MSG_COUNT_PREF";
    private String cardNum = "";

    private Context mContext = this;
    private SimpleDateFormat sdf = null;

    private String sendNumStr;

    private View activityRootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    private LinearLayout receiver;
    private ImageView addMore;//更多
    private LinearLayout ll_sendButton;//发送
    //    private ImageView queryContractImageview;
    private EditText mEtSimNumTXT;//卡号
    private String simNumStr;//收件人号
    private LinearLayout ll_more_info;//详细信息
    private TextView tvCacheInfo;//倒计时
    private TextView tv_bit;//剩余bit数
    private int type;//类型 是新建还是回复
    private CheckBox checkSendPhoneSMS;//发送到手机 选择框

    private static final int WARN_NO_RECEIVER = 10086;//缺少收件人警告
    private static final int WARN_NO_CONTENT = 10087;//缺少发送内容警告

    private BDCmdManager cmdManager;

    public String getSendNumStr() {
        return sendNumStr;
    }

    public void setSendNumStr(String sendNumStr) {
        this.sendNumStr = sendNumStr;
    }

    //获得设置的中继站的号码
    SharedPreferences station_pref;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BD_CURR_DEVICE_MODE:
                    // 获取当前的短信内容以及短信号码
                    String deviceMode = String.valueOf(msg.obj);
                    if (!"SOS".equals(deviceMode)) {
                        String cardNum = phoneNumber;
                        String message = messageContent.getText().toString();
                        long rowId = 0;
                        if (message != null && !"".equals(message)) {
                            // 判断当前号码是电话号码还是北斗卡号
                            boolean isSendPhone = Utils.isPhoneNumber(cardNum);
                            BDMessageInfo mBDMessageInfo = new BDMessageInfo();
                            mBDMessageInfo.setmUserAddress(cardNum);
                            mBDMessageInfo.setMsgType(1);

                            mBDMessageInfo.setMsgCharset(Utils.checkMsg(message));
                            mBDMessageInfo.setMessage(message);
                            mBDMessageInfo.setmSendTime(date);
                            if (draftID > 0) {
                                update(draftID, 1);
                                rowId = draftID;
                            } else {
                                rowId = save(mBDMessageInfo, 1);
                            }
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put(BDMessageColumns.COLUMNS_SEND_TIME, date);
                            map.put(BDMessageColumns.COLUMNS_FLAG, "1");
                            map.put(BDMessageColumns._ID, rowId);
                            map.put(BDMessageColumns.COLUMNS_MSG_CONTENT, message);
                            listData.add(map);
                            detailsAdapter.notifyDataSetChanged();
                            mBDDetailsMessageList.setSelection(listData.size());
                            messageContent.setText("");
                        } else {
                            Toast.makeText(ReplyMessageActivity.this, "短信内容不能为空!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        isSendMessage = true;
                    } else {
                        Toast.makeText(ReplyMessageActivity.this, "紧急救援过程中,无法发送短报文!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case WARN_NO_RECEIVER:
                    Utils.createAlertDialog(mContext, "发送错误", "缺少收件人!", false,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {
                                    dialog.dismiss();
                                }
                            }, "确定").show();

                    break;
                case WARN_NO_CONTENT:
                    Utils.createAlertDialog(mContext, "发送错误", "请输入发送内容!", false,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int arg1) {
                                    dialog.dismiss();
                                }
                            }, "确定").show();

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.activity_reply_bd_msg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WinUtils.setWinTitleColor(this);
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        addReceiver();
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;
        WinUtils.hiddenKeyBoard(this);
        /* 界面分辨率 */
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager m = getWindowManager();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = sdf.format(new Date());
        m.getDefaultDisplay().getMetrics(metrics);
        screenWidth = (int) (metrics.widthPixels);
        screenWidth = (screenWidth * 2) / 3;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        messageOperation = new BDMessageDatabaseOperation(this);
        clipboardManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);

        cmdManager = BDCmdManager.getInstance(this);

        initUI();

        getData();

        initData();

        initListener();


    }


    /**
     * 添加广播监听
     */
    private void addReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ReceiverAction.APP_ACTION_SMS_REFRESH);
        filter.addAction(ReceiverAction.APP_ACTION_SMS_NEW_DIALOG);
        filter.addAction(ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD);
        filter.setPriority(ReceiverAction.BD_PRIORITY_D);
        registerReceiver(smsDataChangReceiver, filter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(this);
        //取消所有的通知
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * 获取数据
     */
    private void getData() {
        station_pref = mContext.getSharedPreferences("BD_RELAY_STATION_PREF", mContext.MODE_PRIVATE);
        Intent intent = this.getIntent();


        if (intent != null) {

            //区分是新建对话 还是回复对话
            type = intent.getIntExtra(Config.INTENT_TYPE, -1);

//			if (mEtSimNumTXT !=null) {
//				mEtSimNumTXT.setText(friendId);
//				type = Config.REPLY_DIALOG;
//			}

            if (Config.NEW_DIALOG == type) {
                // 新建对话
                // title 先为 新建消息 选择联系人后 修改为联系人

            } else if (Config.REPLY_DIALOG == type) {//回复对话
                //隐藏
                receiver.setVisibility(View.GONE);
                // 回复对话
                mUserName = intent.getStringExtra("PHONE_NUMBER");
                if (mUserName.contains("(")) {//如果是通讯录已有联系人，phoneNumber需要拆分，如果不是，不用拆分
                    phoneNumber = mUserName.substring(mUserName.lastIndexOf("(") + 1, mUserName.lastIndexOf(")"));
                    phoneName = mUserName.substring(0, mUserName.lastIndexOf("("));
                } else {
                    phoneNumber = mUserName;
                    phoneName = "";//根据PhoneNumber查询名称
                }
                //设置号码
                mEtSimNumTXT.setText(phoneNumber);
                messageFlag = intent.getStringExtra("MESSAGE_FLAG");
                long id = intent.getLongExtra("MESSAGE_ID", 0);
                if (!"".equals(messageFlag) && "3".equals(messageFlag)) {
                    update(id, 0);
                }
                SharedPreferences countPreference = this.getSharedPreferences(BD_MESSAAGE_COUNT_PREFERENCE_NAME, 0);
                countPreference.edit().putInt("BD_MESSAGE_NUM", 1).commit();


                //其他界面跳转过来  比如:地图回复短报文  友邻位置跳转等
                //从 intent 中获取 收件人号码
//                String friendId = intent.getStringExtra(ReceiverAction.KEY_BD_FRIEND_ID);
            }

            Cursor draftCursor = messageOperation.getDraftMessages(phoneNumber);
            if (draftCursor.moveToNext()) {
                draftID = draftCursor.getLong(draftCursor
                        .getColumnIndex(BDMessageColumns._ID));
                String msg = draftCursor.getString(draftCursor
                        .getColumnIndex(BDMessageColumns.COLUMNS_MSG_CONTENT));
                messageContent.setText(msg);
                messageContent.setSelection(msg.length());
            }
            draftCursor.close();
        }
    }

    /**
     * 响应各种点击事件
     */
    private void initListener() {

        //发送到手机 选择框
        checkSendPhoneSMS.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 保存数据到sp中

                SharedPreferencesHelper.put(simNumStr, isChecked);
                if (isChecked) {
                    mEtSimNumTXT.setHint("请输入手机号");
                } else {
                    mEtSimNumTXT.setHint("请输入北斗卡号");
                }
            }
        });

        mBDDetailsMessageList
                .setOnItemLongClickListener(new OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0,
                                                   View arg1, final int p, long arg3) {
                        final long id = Long.valueOf(String.valueOf(listData.get(p)
                                .get(BDMessageColumns._ID)));
                        final String msg = String.valueOf(listData.get(p).get(
                                BDMessageColumns.COLUMNS_MSG_CONTENT));
                        String[] items;
                        Map<String, Object> map = listData.get(p);
                        String msgFlag = (String) map.get(BDMessageColumns.COLUMNS_FLAG);

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                ReplyMessageActivity.this);
                        builder.setTitle("信息选项");
                        if (5 == Integer.valueOf(msgFlag)) {

                            items = new String[]{"删除", "复制", "地图显示"};

                        } else {

                            items = new String[]{"删除", "复制"};

                        }
                        builder.setItems(items,
                                new DialogInterface.OnClickListener() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int position) {
                                        if (position == 0) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                                    ReplyMessageActivity.this);
                                            builder.setTitle("删除提示");
                                            builder.setMessage("是否删除该短信?");
                                            builder.setPositiveButton(
                                                    "确定",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface arg0,
                                                                int arg1) {
                                                            boolean istrue = messageOperation
                                                                    .delete(id);
                                                            listData.remove(p);
                                                            if (istrue) {
                                                                Toast.makeText(
                                                                        ReplyMessageActivity.this,
                                                                        "删除该条短信!",
                                                                        Toast.LENGTH_LONG)
                                                                        .show();
                                                            } else {
                                                                Toast.makeText(
                                                                        ReplyMessageActivity.this,
                                                                        "删除短信失败!",
                                                                        Toast.LENGTH_SHORT)
                                                                        .show();
                                                            }
                                                            detailsAdapter.notifyDataSetChanged();
                                                        }
                                                    });
                                            builder.setNegativeButton(
                                                    "取消",
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                DialogInterface arg0,
                                                                int arg1) {
                                                            arg0.dismiss();
                                                        }
                                                    });
                                            builder.create().show();
                                        } else if (position == 1) {
                                            //复制粘贴
                                            ClipData data = ClipData
                                                    .newPlainText("短信内容", msg);
                                            clipboardManager
                                                    .setPrimaryClip(data);
                                            Toast.makeText(
                                                    ReplyMessageActivity.this,
                                                    "已经复制到剪贴板",
                                                    Toast.LENGTH_SHORT).show();
                                        } else if (position == 2) {
                                            //地图显示

                                            //获取当前的经纬度信息 发件人
                                            String[] split = msg.split(":");
                                            String stringEW = split[2];
                                            String ew = stringEW.substring(0, stringEW.length() - 5);
                                            String stringNS = split[3];
                                            String ns = stringNS.substring(0, stringNS.length() - 5);

                                        }
                                        arg0.dismiss();
                                    }
                                });
                        builder.create().show();
                        return false;
                    }
                });


        /**
         * 输入内容监听
         */
        messageContent.addTextChangedListener(new TextWatcher() {


            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;
            private Toast mToast = null;
            private boolean isOver = false;


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "监听输入内容的变化,监听输入内容的长度!!");
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "所输入即所得,用户输入的同时可以根据输入的内容进行实时搜索显示!!");

                String toSendContent = s.toString();
                /*判断写入的汉字的个数和数字字母个数,汉字个数*14 数字字母个数×4*/
                int num = Utils.checkStrBits(toSendContent);
//                int flag = Utils.checkMsg(toSendContent);
                int temp = Utils.getMessageMaxLength();
                //if(flag==2){
                //	temp=(Utils.getMessageMaxLength()*2)/3;
                //}

                tv_bit.setText("当前输入：" + num + "/" + temp + "bit");

                selectionStart = messageContent.getSelectionStart();
                selectionEnd = messageContent.getSelectionEnd();
                if (num > temp) {
                    if (!isOver) {
                        mToast = Toast.makeText(mContext, "输入超过最长字符，将不能发送短信!", Toast.LENGTH_SHORT);
                        mToast.show();
                        isOver = true;
                        ll_sendButton.setVisibility(View.GONE);
                    }
                } else {
                    if (isOver) {
                        mToast.cancel();
                        isOver = false;
                        ll_sendButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mEtSimNumTXT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //// TODO: 2016/11/22
                registSendNO();


            }
        });
    }

    /**
     * 填充数据
     */
    private void initData() {

        String toSendContent = messageContent.getText().toString().trim();
        int contentLength = Utils.checkStrBits(toSendContent);
        int maxLength = Utils.getMessageMaxLength();

        if (!TextUtils.isEmpty(toSendContent) && (contentLength >= maxLength)) {
            Toast.makeText(mContext, "发送内容太长将不能发送", Toast.LENGTH_SHORT).show();
            ll_sendButton.setVisibility(View.INVISIBLE);
        }

        title.setVisibility(View.INVISIBLE);
        simNumStr = mEtSimNumTXT.getText().toString().trim();
        if (simNumStr != null) {
            if (simNumStr.contains("(")) {
                String name = simNumStr.substring(simNumStr.lastIndexOf("(") + 1, simNumStr.lastIndexOf(")"));
                phoneNumber = name;
            } else {
                phoneNumber = simNumStr;
            }
        }

        //根据sp回显checkbox数据
        if (!TextUtils.isEmpty(simNumStr)) {
//            checkSendPhoneSMS.setChecked(SharedPreferencesHelper.getSMSStrategy(mContext, simNumStr));
            checkSendPhoneSMS.setChecked(SharedPreferencesHelper.getIsCheckedPhone(simNumStr));
        } else {
            checkSendPhoneSMS.setChecked(false);
        }

        if ("".equals(mUserName)) {
            titleName.setText("新建报文");
        } else {
            titleName.setText(mUserName);
        }
        // 实现分页查询
        totalNum = messageOperation.getTotalNumByPhone(phoneNumber);
        visibleStartIndex = (totalNum - visibleItemCount) > 0 ? (totalNum - visibleItemCount)
                : 0;
        visibleLastIndex = totalNum;
        mCursor = messageOperation.loadPage(phoneNumber, visibleStartIndex, visibleLastIndex);
        // 把mCursor转换成List

        listData = messageOperation.getDataFromCursor(mCursor);

        detailsAdapter = new DetailsMessageAdapter();
        mBDDetailsMessageList.setAdapter(detailsAdapter);
        mBDDetailsMessageList.setSelection(mBDDetailsMessageList.getCount() - 1);
    }


    /**
     * 初始化 UI
     */
    private void initUI() {
        cacheOperation = new RDCacheOperation(this);

        activityRootView = findViewById(R.id.root_layout);
        checkSendPhoneSMS = (CheckBox) this.findViewById(R.id.checkSendPhoneSMS);
        addMore = (ImageView) this.findViewById(R.id.addMore);
        ll_sendButton = (LinearLayout) this.findViewById(R.id.ll_sendButton);
        messageContent = (EditText) this.findViewById(R.id.bd_message_content);
        mEtSimNumTXT = (EditText) this.findViewById(R.id.bd_sim_num);
        titleName = (TextView) this.findViewById(R.id.sub_title_name);
        title = (TextView) this.findViewById(R.id.title_name);
        tvCacheInfo = (TextView) this.findViewById(R.id.tv_chcheInfo);
        tv_bit = (TextView) this.findViewById(R.id.tv_bit);
        returnLayout = (LinearLayout) this.findViewById(R.id.return_home_layout);
//        queryContractImageview = (ImageView) this.findViewById(R.id.select_contract_imageview);
        receiver = (LinearLayout) this.findViewById(R.id.receiver);
        ll_more_info = (LinearLayout) this.findViewById(R.id.ll_more_info);
        returnLayout.setOnClickListener(this);
        mBDDetailsMessageList = (ListView) this.findViewById(R.id.bd_message_details_list);
        addMore.setOnClickListener(this);
        ll_sendButton.setOnClickListener(this);
//        queryContractImageview.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_home_layout:
                onBackPressed();
                break;
//            case R.id.select_contract_imageview:
//
//                Intent intent = new Intent();
//                intent.setClass(ReplyMessageActivity.this, BDContactActivity.class);
//                intent.setData(BDContactColumn.CONTENT_URI);
//                intent.putExtra(Config.NEED_BACK, true);
//                startActivityForResult(intent, REQUEST_CONTACT);
//                break;
            case R.id.addMore:
                // 更多
                openPopwindow();

                break;

            case R.id.ll_sendButton: {
                if (SysUtils.isServiceRunning(this, CycleReportRDLocService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_RD_report), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SysUtils.isServiceRunning(this, CycleLocService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_RD_Location), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SysUtils.isServiceRunning(this, CycleReportRNLocService.class.getName())) {
                    Toast.makeText(this, getResources().getString(R.string.stop_RN_report), Toast.LENGTH_SHORT).show();
                    return;
                }
                sendFunctionSMS();
                break;
            }

            default:
                break;
        }
    }

    private void openPopwindow() {
        String[] strings = new String[]{"联系人", "短语模板"};

        new CommomDialogList(ReplyMessageActivity.this, R.style.dialog_aa, strings, new CommomDialogList.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, int position) {
                dialog.dismiss();
                switch (position) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setClass(ReplyMessageActivity.this, BDContactActivity.class);
                        intent.setData(BDContactColumn.CONTENT_URI);
                        intent.putExtra(Config.NEED_BACK, true);
                        startActivityForResult(intent, REQUEST_CONTACT);
                        break;

                    case 1:
                        openMsgModel();
                        break;
                }
            }
        }).show();
    }


    /**
     * 记录 发送号码
     */
    private void registSendNO() {

        if (mEtSimNumTXT != null) {
            String simNumStr2 = mEtSimNumTXT.getText().toString().trim();
            setSendNumStr(simNumStr2);
        }
    }


    /**
     * 发送短报文
     */
    private void sendFunctionSMS() {

        simNumStr = mEtSimNumTXT.getText().toString().trim();
        String content = messageContent.getText().toString().trim();
        String phonecontent = "";
        if (simNumStr.isEmpty()) {
            Toast.makeText(mContext, "请输入发件号码!", Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(WARN_NO_RECEIVER);
            return;
        }
        if (!simNumStr.contains("(") && simNumStr.length() > 7 && !checkSendPhoneSMS.isChecked()) {
            Toast.makeText(mContext, "请输入6位或7位北斗号码!", Toast.LENGTH_LONG).show();
            return;
        }
        if (checkSendPhoneSMS.isChecked() && simNumStr.length() < 11) {
            Toast.makeText(mContext, "请输入11位手机号码!", Toast.LENGTH_LONG).show();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(mContext, "请输入发送内容!", Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(WARN_NO_CONTENT);
            return;
        }

        if (simNumStr.contains("(")) {
            phonecontent = simNumStr.substring(0, simNumStr.lastIndexOf("("));
        }

        //判断是否勾选 checkbox 发送到手机
        /*if (checkSendPhoneSMS.isChecked()) {
            //被选中发送到手机


            String address = station_pref.getString("BD_RELAY_STATION_NUM", "");
            //手机地址
            String phoneNum = simNumStr;
            phonecontent = Utils.buildSendPhoneSMS(phoneNum, content);
            if (address.isEmpty()) {
                Utils.createAlertDialog(ReplyMessageActivity.this, "提示", "请先设置中继站号:" + "\r\n" + "      '菜单'-->'中继站管理'!", false,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                dialog.dismiss();
                            }
                        }, "取消").show();
                return;
            }
        } else {
            //未选中 rd发送
            Log.e(TAG, "LERRY_TXA: =======ReplyMessageActivity764===========未选中 rd发送===");
        }*/

        //判断长度 超出长度的时候  切割成  多条短语 循环发送
        //8bit ---字母 和 数字
        //14bit ---汉字

        int maxLength = Utils.getMessageMaxLength();
        int splitLength = maxLength / 14;
        if (content.length() >= splitLength) {
            if (checkSendPhoneSMS.isChecked()) {
                sendPhoneSMS(phonecontent, content);

            } else {
                Log.e(TAG, "LERRY_TXA: =======ReplyMessageActivity795============超长短报文发送==");
                sendSMS(content);
            }

            //保存数据到数据库
            save2db(phonecontent, content, null);


        } else {
            //没有超长
            if (checkSendPhoneSMS.isChecked()) {
                sendPhoneSMS(phonecontent, content);

            } else {
                Log.e(TAG, "LERRY_TXA: =======ReplyMessageActivity810=============正常短报文发送==" + content);
                sendSMS(content);
            }
            //保存数据到数据库
            save2db(phonecontent, content, null);
            Log.e(TAG, "LERRY_TXA: =======ReplyMessageActivity815=============保存到数据库==");

        }


        notifyDataChange();

        updateCache();

        // 清楚 textview中的内容
        messageContent.setText("");
        //Toast.makeText(mContext, "app请求发送!!", 1).show();
    }

    /**
     * 发送短报文
     *
     * @param content
     * @return
     */
    @NonNull
    public void sendSMS(String content) {

        // 判断如何含有 联系人 去除联系人 取出联系电话
        while (simNumStr.contains("(")) {
            String name = simNumStr.substring(simNumStr.lastIndexOf("(") + 1, simNumStr.lastIndexOf(")"));
            simNumStr = name;
            phoneNumber = name;
        }
        if (!simNumStr.contains("(")) {
            phoneNumber = simNumStr;
        }

        try {
//            String msg ="你好呀为什么短报文不行呢";
            cmdManager.sendSMSCmdBDV21(phoneNumber, 1, Utils.checkMsgEncodeMode(content), "N", content);
        } catch (BDUnknownException e) {
            e.printStackTrace();
        } catch (BDParameterException e) {
            e.printStackTrace();
        }

//        int priority = txa.getmPriority();
        BDCache mBdCache = new BDCache();
        /*if (priority < 0) {//短信内容优先级
         *//*public static final int PRIORITY_MAX = 0;//紧急救援
                public static final int PRIORITY_1 = 100;//最高优先级 定位申请
                public static final int PRIORITY_3 = 300;//次之优先级 短报文 位置报告
                public static final int PRIORITY_5 = 500;//最弱优先级*//*
            //封装数据
            mBdCache.setPriority(BDCache.PRIORITY_MAX);
            Log.e(TAG, "LERRY_TXA: =======SMSapp320=========发送短报文啦！！！priority < 0========="+ ((BD_RD_TXA) data.m_Data).getmMessageContent());
        } else {

            mBdCache.setPriority(BDCache.PRIORITY_3);
        }*/
        //封装数据
        mBdCache.setMsgType(BDCache.SMS_FLAG);
        mBdCache.setPriority(BDCache.PRIORITY_3);
        mBdCache.setSendAddress(phoneNumber);
        mBdCache.setMsgContent(content);
        mBdCache.setCacheContent(content);

        //保存数据到缓存数据库
        SysUtils.dispatchData(ReplyMessageActivity.this, mBdCache);
    }

    public void sendPhoneSMS(String phoneContent, String content) {
        /*String address = station_pref.getString("BD_RELAY_STATION_NUM", "");
        BD_RD_TXA txa = new BD_RD_TXA() {
            @Override
            protected byte GetSendMode(String strData) {
                return 1;//super.GetSendMode(strData);
            }
        };
        // 判断如何含有 联系人 去除联系人 取出联系电话
//        while (address.contains("(")) {
//            String name = address.substring(address.lastIndexOf("(") + 1, address.lastIndexOf(")"));
//            address = name;
//            txa.setmUserID(name);
//            address = name;
//        }
        if (!address.contains("(")) {
            txa.setmUserID(address);
            phoneNumber = simNumStr;
        }


        txa.setmMessageContent(phoneContent);
        txa.setmMessageType(Config.CommType.COMMON_MODE);
        //txa.setmTransferType(ClientReceiver.EncodingMode.COMPLEX_MODE);
        BDData data = new BDData(ProtocolType.PROTOCOL_TYPE_BD21, BD21DataType.BD_21_RD_TXA, txa);
        sendData(data);
        txa.setmMessageContent(content);
        txa.setmUserID(simNumStr);*/
    }

    /**
     * 切割 超长报文
     *
     * @param content
     * @return
     */
    @NonNull
    public ArrayList<String> getSubContent(String content) {

        ArrayList<String> subContents = new ArrayList<>();

        //int contentLength = Utils.checkStrBits(content);
        int maxLength = Utils.getMessageMaxLength();
        int splitLength = maxLength / 14;

        while (content.length() > splitLength) {
            //切割
            String subContent = content.substring(0, splitLength);
            content = content.substring(splitLength, content.length());
            subContents.add(subContent);

        }

        subContents.add(content);

        return subContents;
    }

    /**
     * 保存到数据库
     */
    public void save2db(String cardNum, String messageContent, String otherMsg) {

        BDMessageInfo info = new BDMessageInfo();
        info.setUserName(cardNum);
        info.setmUserAddress(phoneNumber);

        if (!TextUtils.isEmpty(otherMsg)) {
            info.setMessage(otherMsg);
        } else {
            info.setMessage(messageContent);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        info.setmSendTime(date);
        BDMessageDatabaseOperation messageOperation = new BDMessageDatabaseOperation(
                mContext);
        messageOperation.insert(info, "1");//发件

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (messageContent != null
                && !"".equals(messageContent.getText().toString().trim())) {
            String content = messageContent.getText().toString().trim();
            if (draftID == 0) {

                String toWho = mEtSimNumTXT.getText().toString().trim();

                if (TextUtils.isEmpty(toWho)) {
                    return;
                }
                if (toWho.contains("(")) {
                    //去括号 有联系人  有 联系号码
                    phoneName = toWho.substring(0, toWho.lastIndexOf("("));
                    while (toWho.contains("(")) {
                        String name = toWho.substring(toWho.lastIndexOf("(") + 1, toWho.lastIndexOf(")"));
                        toWho = name;
                        phoneNumber = name;
                    }
                } else {
                    //只有联系号码
                    if (toWho.length() > 7) {
                        return;
                    }
                    phoneNumber = toWho;
                    //通过号码去查询联系人
                    String queryResult = DBhelper.getContactNameFromPhoneBook(mContext, toWho);
                    if (queryResult != null) {
                        phoneName = queryResult;
                    } else {
                        phoneName = toWho;
                    }
                }


                BDMessageInfo mBDMessageInfo = new BDMessageInfo();
                mBDMessageInfo.setmUserAddress(phoneNumber);
                mBDMessageInfo.setUserName(phoneName);
                mBDMessageInfo.setMsgType(1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(new Date());
                mBDMessageInfo.setMsgCharset(Utils.checkMsg(content));
                mBDMessageInfo.setMessage(content);
                mBDMessageInfo.setmSendTime(date);
                //不保存草稿
                save(mBDMessageInfo, 2);
            } else {
                //更新内容
                updateContent(draftID, content);
            }
        } else {
            if (draftID != 0 && !isSendMessage) {
                // 删除该条信息
                messageOperation.delete(draftID);
            } else {
                // 什么不用做
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONTACT) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                Uri result = data.getData();
                Cursor cursor = ReplyMessageActivity.this.getContentResolver().query(result, null, null, null, null);
                String mUserAddress = "";
                if (cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    //使用ContentResolver查找联系人的电话号码
                    long contactId = ContentUris.parseId(result);
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    String phoneNumber = "";
                    if (phones.moveToNext()) {
                        //获取查询结果中电话号码列中数据
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    mUserAddress = name + "(" + phoneNumber + ")";
                }
                cursor.close();
                mEtSimNumTXT.setText(mUserAddress);
                titleName.setText(mUserAddress);

                //同时设置数据到
                //// TODO: 2016/11/22
                registSendNO();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(smsDataChangReceiver);
        if (messageOperation != null) {
// .close();
        }
    }


    public long save(BDMessageInfo info, int flag) {
        return messageOperation.insert(info, "" + flag);
    }

    public boolean update(long uid, int flag) {
        boolean istrue = messageOperation.updateMessageStatus(uid, flag);
        return istrue;
    }

    public boolean updateContent(long uid, String content) {
        boolean istrue = messageOperation.updateMessageContent(uid, content);
        return istrue;
    }


    private void openMsgModel() {
        MessgeUsualOperation oper = new MessgeUsualOperation(this);
        List<String> listMsg = oper.getAll1();
        if (listMsg.size() == 0) {
            Toast.makeText(this, "请先添加短语!!", Toast.LENGTH_LONG).show();
        } else {
            addModel(listMsg);
        }
    }

    private void addModel(final List<String> listMsg) {

        String[] strings = listMsg.toArray(new String[listMsg.size()]);

        new CommomDialogList(ReplyMessageActivity.this, R.style.dialog_aa, strings, new CommomDialogList.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, int position) {
                dialog.dismiss();
                String usalMsg = String.valueOf(listMsg.get(position));
                UsalMsg usalMst = new UsalMsg(usalMsg);
                EventBus.getDefault().post(usalMst);
            }
        }).setMessage("请选择要使用的模板短语").setTitle("模板短语").show();

    }

    @Subscribe(threadMode = ThreadMode.MAIN) //第2步:注册一个在后台线程执行的方法,用于接收事件
    public void onUserEvent(UsalMsg usalMsg) {//参数必须是ClassEvent类型, 否则不会调用此方法
        messageContent.setText(usalMsg.getUsalMsg());
    }

    /**
     * 新建短信气泡列表
     *
     * @author steve
     */
    private class DetailsMessageAdapter extends BaseAdapter {

        private ViewHolder viewHolder = null;
        private LayoutInflater mInflater = null;

        public DetailsMessageAdapter() {
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            String flag = String.valueOf(listData.get(position).get(
                    BDMessageColumns.COLUMNS_FLAG));
            viewHolder = new ViewHolder();
            if ("1".equals(flag)) {
                contentView = mInflater.inflate(R.layout.item_reply_my_message, null);
                viewHolder.myMessageBody = (TextView) contentView
                        .findViewById(R.id.my_message_body_textview);
                viewHolder.myMessageBody.setMaxWidth(screenWidth);
                viewHolder.myMessageDate = (TextView) contentView
                        .findViewById(R.id.my_message_time_textview);
                viewHolder.myMessageBody.setText(String.valueOf(listData.get(
                        position).get(BDMessageColumns.COLUMNS_MSG_CONTENT)));
                viewHolder.myMessageDate.setText(String.valueOf(listData.get(
                        position).get(BDMessageColumns.COLUMNS_SEND_TIME)));
            } else {
                contentView = mInflater.inflate(
                        R.layout.item_reply_other_message, null);
                viewHolder.otherMessageBody = (TextView) contentView
                        .findViewById(R.id.other_message_body_textview);
                viewHolder.otherMessageDate = (TextView) contentView
                        .findViewById(R.id.other_message_time_textview);
                viewHolder.otherMessageBody.setMaxWidth(screenWidth);
                viewHolder.otherMessageBody.setText(String.valueOf(listData.get(
                        position).get(BDMessageColumns.COLUMNS_MSG_CONTENT)));
                viewHolder.otherMessageDate.setText(String.valueOf(listData.get(
                        position).get(BDMessageColumns.COLUMNS_SEND_TIME)));
            }
            return contentView;
        }

        class ViewHolder {
            /**
             * 对方发送的内容
             */
            TextView otherMessageBody;

            /**
             * 对方发送的日期
             */
            TextView otherMessageDate;

            /**
             * 自己发送的内容
             */
            TextView myMessageBody;

            /**
             * 自己发送的日期
             */
            TextView myMessageDate;
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        // 现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {

            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    ll_more_info.setVisibility(View.VISIBLE);
                    updateCache();
                }


            });


        } else if (oldBottom != 0 && bottom != 0
                && (bottom - oldBottom > keyHeight)) {

//			Toast.makeText(ReplyMessageActivity.this, "监听到软件盘关闭...", Toast.LENGTH_SHORT)
//					.show();
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    ll_more_info.setVisibility(View.GONE);
                }

            });

        }
    }

    public void notifyDataChange() {

        //获取 phoneNumber
        String phoneNumber2 = getSendNumStr();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            if (phoneNumber2 != null) {
                //去除 冗余
                String simNumStr = phoneNumber2;
                while (simNumStr.contains("(")) {
                    String name = simNumStr.substring(simNumStr.lastIndexOf("(") + 1, simNumStr.lastIndexOf(")"));
                    simNumStr = name;
                    phoneNumber2 = name;
                }
                phoneNumber = phoneNumber2;
            }
        }
        mCursor = messageOperation.loadPage(phoneNumber, visibleStartIndex, visibleLastIndex);
        listData = messageOperation.getDataFromCursor(mCursor);
        detailsAdapter.notifyDataSetChanged();
        if (mBDDetailsMessageList != null) {
            mBDDetailsMessageList.setSelection(mBDDetailsMessageList.getBottom());
        }
    }

    public void updateCache() {
        int size = SharedPreferencesHelper.getRecordedCount();
        String info = "还有" + size + "条等待发送";
        if (tvCacheInfo != null) {
            tvCacheInfo.setText(info);
        }
    }

    /**
     * 短报文数据变化广播
     */
    BroadcastReceiver smsDataChangReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 查询数据库  更新数据
            String action = intent.getAction();
            switch (action) {
                case ReceiverAction.APP_ACTION_SMS_NEW_DIALOG: {

                    notifyDataChange();
                    abortBroadcast();

                    break;
                }
                case ReceiverAction.APP_ACTION_SMS_REFRESH: {

                    // 更新数据库  receiverStr 为已读
                    String receiverStr = intent.getStringExtra(ReceiverAction.APP_KEY_SMS_RECEIVER);
                    long rawId = intent.getLongExtra(ReceiverAction.APP_KEY_SMS_RAWID, -1);
                    if (receiverStr.equals(phoneNumber) || receiverStr.contains(phoneNumber)) {//收件人是当前聊天对象
                        //更新 listData
                        if (rawId > 0) {
                            update(rawId, Integer.parseInt(BDMessageDatabaseOperation.MSG_TYPE_RECEIVER));
                        }
                        notifyDataChange();

                    } else {
                        //未读短信

                    }

                    break;
                }
                case ReceiverAction.DB_ACTION_ON_DATA_CHANGE_ADD:
                    // 数据库变化了,重新查询数据库
                    int count = cacheOperation.getAll().size();
                    SharedPreferencesHelper.put(Constant.SP_RECORDED_KEY_COUNT, count);
                    updateCache();
                    break;
            }


        }
    };
    private String date;


}