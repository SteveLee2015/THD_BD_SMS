package thd.bd.sms.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.service.CycleReportSOSService;
import thd.bd.sms.sharedpreference.Constant;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.SysUtils;
import thd.bd.sms.utils.Utils;


/**
 * 连续报告策略设置
 *
 * @author llg
 */
public class SoSsetActivity extends BaseActivity {

    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    @BindView(R.id.title_name)
    TextView titleName;
    private Button cancelBtn;
    private Button submitBtn;
    private Button immediatelyBtn;
    private EditText etcontent;
    private EditText etinputNum;
    private ImageView ivcontract;
    private Context mContext = this;
    public final int REQUEST_CONTACT = 1;
    private static final String TAG = "SoSsetActivity";
    private TextView vtHint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListeners();
        initDatas();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_sos_set;
    }

    private void initDatas() {

        String sendNumStr = SharedPreferencesHelper.getSosNum();
        String reportStatus = SharedPreferencesHelper.getSosContent();

        if (!TextUtils.isEmpty(sendNumStr)) {
            etinputNum.setText(sendNumStr);

        }

        if (!TextUtils.isEmpty(reportStatus)) {
            etcontent.setText(reportStatus);
        }

        notifyButton();

        String trim = etcontent.getText().toString().trim();
        notificationButton(trim);
    }

    private void notificationButton(String toSendContent) {

        Toast mToast = null;
        boolean isOver = false;
        /*判断写入的汉字的个数和数字字母个数,汉字个数*14 数字字母个数×4*/
        int num = Utils.checkStrBits(toSendContent);
        int flag = Utils.checkMsg(toSendContent);
        //f20   3
        //11622714  8
        //4006693   7
        //000038    6
        //1         1
        //193907    6
        // ********  救援内容转16进制
        //21        2

        int temp = (Utils.getMessageMaxLength() - 264) / 2;

        vtHint.setText("当前输入：" + num + "/" + temp + "bit");

        if (num > temp) {
            if (!isOver) {
                mToast = Toast.makeText(mContext, "输入超过最长字符，将不能发送短信!", Toast.LENGTH_SHORT);
                mToast.show();
                isOver = true;
            }

            submitBtn.setVisibility(View.INVISIBLE);
            immediatelyBtn.setVisibility(View.INVISIBLE);


        } else {

            if (isOver && mToast != null) {
                mToast.cancel();
                isOver = false;
            }

            submitBtn.setVisibility(View.VISIBLE);
            immediatelyBtn.setVisibility(View.VISIBLE);

        }
    }

    private void notifyButton() {
        String temp1 = etinputNum.getText().toString().trim();
        String temp2 = etcontent.getText().toString().trim();


        //判断 当前文字长度 和 卡屏长度


        if (!TextUtils.isEmpty(temp1) && !TextUtils.isEmpty(temp2)) {
            immediatelyBtn.setVisibility(View.VISIBLE);
        } else {
            immediatelyBtn.setVisibility(View.GONE);
        }
    }

    private void initListeners() {

        etinputNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                notifyButton();

            }
        });
        etcontent.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.e(TAG, "所输入即所得,用户输入的同时可以根据输入的内容进行实时搜索显示!!");

                String toSendContent = s.toString();
                notificationButton(toSendContent);


                //notifyButton();

            }


        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();

            }
        });

        immediatelyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();

                String sendNumStr = SharedPreferencesHelper.getSosNum();
                String reportStatus = SharedPreferencesHelper.getSosContent();

                // 判断一个服务是否在运行
                String clssName = CycleReportSOSService.class.getName();
                boolean isStart = SysUtils.isServiceRunning(mContext, clssName);

                if (!isStart) {
                    Intent sosIntent = new Intent(mContext, CycleReportSOSService.class);
                    sosIntent.putExtra("sendNumStr", sendNumStr);
                    sosIntent.putExtra("reportStatus", reportStatus);
                    startService(sosIntent);

                    openUIsos();

                    finish();

                } else {
                    //救援服务正在运行 ,不要重复开启

                    Toast.makeText(mContext, "救援服务正在运行", Toast.LENGTH_SHORT).show();

                    openUIsos();

                    finish();

                    return;
                }

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        ivcontract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(mContext, BDContactActivity.class);
                intent.setData(BDContactColumn.CONTENT_URI);
                intent.putExtra(Config.NEED_BACK, true);
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });


    }

    private void openUIsos() {
        Intent sosIntent = new Intent(mContext, SOSActivity.class);
        startActivity(sosIntent);
    }

    private void saveData() {
        String temp1 = etinputNum.getText().toString().trim();
        String temp2 = etcontent.getText().toString().trim();

        if (TextUtils.isEmpty(temp1)) {
            Toast.makeText(mContext, "报告平台号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(temp2)) {
            Toast.makeText(mContext, "救援信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //判断不能超长
        // TODO: 2017/1/10
        SharedPreferencesHelper.put(Constant.SP_KEY_SOS_NUM, temp1);
        SharedPreferencesHelper.put(Constant.SP_KEY_SOS_CONTENT, temp2);

        Toast.makeText(mContext, "数据保存成功", Toast.LENGTH_SHORT).show();
        immediatelyBtn.setVisibility(View.VISIBLE);
    }

    private void initView() {

        titleName.setText("SOS救援");

        cancelBtn = (Button) findViewById(R.id.cancel);
        immediatelyBtn = (Button) findViewById(R.id.immediately);
        submitBtn = (Button) findViewById(R.id.submit);
        vtHint = (TextView) findViewById(R.id.tv_hint);
        etinputNum = (EditText) findViewById(R.id.et_inputNum);
        etcontent = (EditText) findViewById(R.id.et_content);
        ivcontract = (ImageView) findViewById(R.id.iv_contract);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: 11111");
        if (requestCode == REQUEST_CONTACT) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                Uri result = data.getData();
                Cursor cursor = mContext.getContentResolver().query(result, null, null, null, null);
                String mUserAddress = "";
                if (cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    //使用ContentResolver查找联系人的电话号码
                    long contactId = ContentUris.parseId(result);
                    Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    String phoneNumber = "";
                    if (phones.moveToNext()) {
                        //获取查询结果中电话号码列中数据
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    mUserAddress = name + "(" + phoneNumber + ")";
                    if (etinputNum != null) {
                        etinputNum.setText(phoneNumber);
                    }
                }
                cursor.close();
            }
        }
    }

    @OnClick(R.id.return_home_layout)
    public void onViewClicked() {
        SoSsetActivity.this.finish();
    }
}
