package thd.bd.sms.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import thd.bd.sms.R;


/**
 * Created by lerry on 2018/9/5 0005.
 */

public class CommomDialogList extends Dialog implements AdapterView.OnItemClickListener,View.OnClickListener {

    private ListView contentListview;
    private TextView dialog_commom_list_title;

    private Context mContext;
    private CommomDialogList.OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String[] list;
    private ImageView iv_close;
    private TextView tv_message;

    private String title;
    private String mainMessage;

    public CommomDialogList(Context context) {
        super(context);
        this.mContext = context;
    }
    public CommomDialogList(Context context, String content) {
        super(context, R.style.dialog_aa);
        this.mContext = context;
    }

    public CommomDialogList(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public CommomDialogList(Context context, int themeResId, String[] list, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.list = list;
        this.listener = listener;
    }

    protected CommomDialogList(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }


    public CommomDialogList setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public CommomDialogList setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    public CommomDialogList setTitle(String title){
        this.title = title;
        return this;
    }

    public CommomDialogList setMessage(String msg){
        this.mainMessage = msg;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_commom_list);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
    }

    private void initView(){
        contentListview = (ListView)findViewById(R.id.dialog_commom_listview);
        contentListview.setOnItemClickListener(this);


        tv_message = (TextView)findViewById(R.id.tv_message);
        dialog_commom_list_title = (TextView)findViewById(R.id.dialog_commom_list_title);

        iv_close = (ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.item,R.id.text_view,list);
        contentListview.setAdapter(adapter);


//        cancelTxt = (TextView)findViewById(R.id.dialog_commom_list_cancel);
//        cancelTxt.setOnClickListener(this);

        /*if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }*/

        if(!TextUtils.isEmpty(title)){
            dialog_commom_list_title.setText(title);
        }

        if(!TextUtils.isEmpty(mainMessage)){
            tv_message.setText(mainMessage);
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onClick(this, position);
        }
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                this.dismiss();
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, int position);
    }

}
