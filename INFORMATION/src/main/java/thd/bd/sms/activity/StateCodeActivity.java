package thd.bd.sms.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import thd.bd.sms.R;
import thd.bd.sms.adapter.MsgUsalWordAdapter;
import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.bean.StateCode;
import thd.bd.sms.database.StateCodeOperation;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.WinUtils;

public class StateCodeActivity extends BaseActivity {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.return_home_layout)
    LinearLayout returnHomeLayout;
    private Context mContext = this;
    private ListView listView = null;
    private LinearLayout mLinerLayout = null;
    private RelativeLayout mRLoperation = null;
    private StateCodeOperation oper = null;
    private MsgUsalWordAdapter adapter = null;
    private TextView noMsgWordPrompt = null;

    @Override
    protected int getContentView() {
        return R.layout.activity_msg_usal_word;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        WinUtils.setWinTitleColor(this);

        titleName.setText("自定义状态");

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            WinUtils.setDialogPosition(this, 0.9f, 0.6f);
        } else {
            WinUtils.setDialogPosition(this, 0.9f, 0.9f);
        }
        listView = (ListView) this.findViewById(R.id.msg_word_listview);
        mLinerLayout = (LinearLayout) this.findViewById(R.id.msg_word_layout);
        mRLoperation = (RelativeLayout) this.findViewById(R.id.ll_usalMsg_operation);
        noMsgWordPrompt = (TextView) this.findViewById(R.id.no_msg_word_prompt);
        /* 增加背景 */
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.gray_bg);
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        bd.setDither(true);
        mLinerLayout.setBackgroundDrawable(bd);
        mRLoperation.setVisibility(View.GONE);
        oper = new StateCodeOperation(this);
        final List<Map<String, Object>> list = oper.getAll();
        if (list.size() == 0) {
            noMsgWordPrompt.setVisibility(View.VISIBLE);
        }
        adapter = new MsgUsalWordAdapter(this, list, Config.FLAG_STATUS_CODE);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int position, long arg3) {
                final StateCode mStateCode = new StateCode();

                Dialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("修改状态语句")
                        //.setItems(new String[]{"修改状态语句","删除状态语句"},new DialogInterface.OnClickListener() {
                        .setItems(new String[]{"修改状态语句"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        final long rowId = Long.parseLong(String.valueOf(list.get(position).get("MESSAGE_WORD_ID")));
                                        String messageOrder = String.valueOf(list.get(position).get("MESSAGE_WORD_TEXT_ORDER"));
                                        String messageContent = String.valueOf(list.get(position).get("MESSAGE_WORD_TEXT"));
                                        mStateCode.setMsgCongentOrder(Integer.parseInt(messageOrder));
                                        mStateCode.setMsgContent(messageContent);
                                        mStateCode.setRowId(rowId);

                                        View inflate = View.inflate(mContext, R.layout.dialog_edit_text_statu_usual, null);
                                        final EditText editText = (EditText) inflate.findViewById(R.id.et_usual_status);
                                        editText.setText(messageContent);
                                        Dialog dialog1 = new AlertDialog.Builder(mContext)
                                                .setTitle("修改状态语句")
                                                .setView(inflate)
                                                .setPositiveButton(
                                                        "保存",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog,
                                                                                int which) {

                                                                String shortSmS = editText.getText().toString().trim();
                                                                if (TextUtils.isEmpty(shortSmS)) {
                                                                    return;
                                                                }

                                                                if (shortSmS.length() > 10) {
                                                                    String info = "该卡只能保存长度为" + 10 + "的短语";
                                                                    Toast.makeText(StateCodeActivity.this, info, Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }

                                                                mStateCode.setMsgContent(shortSmS);
                                                                //boolean isTrue=oper.update(rowId, shortSmS);
                                                                boolean isTrue = oper.update(mStateCode);
                                                                if (isTrue) {
                                                                    list.get(position).put("MESSAGE_WORD_TEXT", shortSmS);
                                                                    Toast.makeText(mContext, "更新成功!", Toast.LENGTH_LONG).show();
                                                                    adapter.notifyDataSetChanged();
                                                                } else {
                                                                    Toast.makeText(mContext, "更新失败!", Toast.LENGTH_LONG).show();
                                                                }
                                                                dialog.dismiss();
                                                            }
                                                        }).setNegativeButton(mContext.getResources().getString(R.string.common_cancle_btn),
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface arg0, int arg1) {
                                                                arg0.dismiss();
                                                            }
                                                        }).create();
                                        dialog1.setCancelable(false);
                                        dialog1.show();
                                        break;
                                    case 1:

                                    {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle("提示");
                                        builder.setMessage("是否删除该条常用短语?");
                                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                Toast.makeText(mContext, "状态语句只支持修改,不能删除!", Toast.LENGTH_LONG).show();

                                                /**
                                                 *

                                                 Map<String,Object> map=list.get(position);
                                                 boolean istrue=oper.delete(Long.valueOf(String.valueOf(map.get("MESSAGE_WORD_ID"))));
                                                 if(istrue){
                                                 list.remove(map);
                                                 adapter.notifyDataSetChanged();
                                                 }else{
                                                 Toast.makeText(mContext, "删除失败!", Toast.LENGTH_LONG).show();
                                                 }
                                                 */
                                            }
                                        });
                                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        builder.create().show();
                                        break;
                                    }

                                    default:
                                        break;
                                }
                            }
                        }).create();
                dialog.show();
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(mContext, "长按修改状态短语,最大长度10个字符", Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "长按修改状态短语,最大长度10个字符", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            WinUtils.setDialogPosition(this, 0.9f, 0.6f);
        } else {
            WinUtils.setDialogPosition(this, 0.9f, 0.9f);
        }
    }


    @OnClick(R.id.return_home_layout)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.return_home_layout:
                this.finish();
                break;
        }
    }
}
