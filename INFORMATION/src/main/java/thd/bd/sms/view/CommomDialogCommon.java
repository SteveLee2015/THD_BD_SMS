package thd.bd.sms.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import thd.bd.sms.R;
import thd.bd.sms.utils.SysUtils;


/**
 * Created by lerry on 2018/9/5 0005.
 */

public class CommomDialogCommon extends Dialog implements View.OnClickListener {

    private TextView contentTxt;
    private TextView dialog_commom_list_title;
    private LinearLayout tv_tishi_txt_layout;

    private Context mContext;
    private OnCloseListener listener;
    private OnDaoHangListener daoHangListener;
//    private String positiveName;
    private String negativeName;
    private ImageView iv_close;
    private TextView tv_message;
    private Button cancelTxt;
    private LinearLayout tishi_latlng_layout;
    private EditText tishi_lat,tishi_lon;

    private double lat,lon;
    private String title;
    private String mainMessage;
    private String centerContent;
    private boolean isLatlon=false;

    public CommomDialogCommon(Context context) {
        super(context);
        this.mContext = context;
    }
    public CommomDialogCommon(Context context, String content) {
        super(context, R.style.dialog_aa);
        this.mContext = context;
    }

    public CommomDialogCommon(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public CommomDialogCommon(Context context, int themeResId, String centerContent,OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.centerContent = centerContent;
        this.listener = listener;
    }

    public CommomDialogCommon(Context context, int themeResId, boolean isLatlon,OnDaoHangListener daoHangListener) {
        super(context, themeResId);
        this.mContext = context;
        this.isLatlon = isLatlon;
        this.daoHangListener = daoHangListener;
    }

    protected CommomDialogCommon(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }


    /*public CommomDialogCommon setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }
*/
    public CommomDialogCommon setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    public CommomDialogCommon setTitle(String title){
        this.title = title;
        return this;
    }

    public CommomDialogCommon setMessage(String msg){
        this.mainMessage = msg;
        return this;
    }

    public double getLat(){
        return lat;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_commom_tishi);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView(){
        contentTxt = (TextView) findViewById(R.id.tv_tishi_txt);
        tv_tishi_txt_layout = (LinearLayout)findViewById(R.id.tv_tishi_txt_layout);


        tishi_latlng_layout = (LinearLayout)findViewById(R.id.tishi_latlng_layout);
        tv_message = (TextView)findViewById(R.id.tv_message);
        dialog_commom_list_title = (TextView)findViewById(R.id.dialog_commom_tishi_title);

        iv_close = (ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        tishi_lat = (EditText)findViewById(R.id.tishi_lat);
        tishi_lon = (EditText)findViewById(R.id.tishi_lon);

        cancelTxt = (Button)findViewById(R.id.dialog_commom_tishi_cancel);
        cancelTxt.setOnClickListener(this);

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            dialog_commom_list_title.setText(title);
        }

        if(!TextUtils.isEmpty(mainMessage)){
            tv_message.setText(mainMessage);
        }

        if(!TextUtils.isEmpty(centerContent)){
            contentTxt.setText(centerContent);
        }

        if(isLatlon){
            tishi_latlng_layout.setVisibility(View.VISIBLE);
            tv_tishi_txt_layout.setVisibility(View.GONE);
            Log.e("isLatlon", "initView: ++++++++++++++++++++" );
        }

        if(!TextUtils.isEmpty(tishi_lat.getText().toString())){
            lat = Double.parseDouble(tishi_lat.getText().toString());
        }

        if(!TextUtils.isEmpty(tishi_lon.getText().toString())){
            lon = Double.parseDouble(tishi_lon.getText().toString());
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                this.dismiss();
                break;

            case R.id.dialog_commom_tishi_cancel:


                if(isLatlon){

                    if (daoHangListener != null) {
                        daoHangListener.onClick(this,lat,lon);
                    }

                }else {
                    if (listener != null) {
                        listener.onClick(this);
                    }
                    this.dismiss();
                }

                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog);
    }

    public interface OnDaoHangListener{
        void onClick(Dialog dialog,double lat,double lon);
    }

}
