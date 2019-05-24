package thd.bd.sms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import thd.bd.sms.R;


public class CustomProgressView extends LinearLayout {

	private Context mContext=null;
	private BDRDSSSignalView custom_signal_1=null;
	private BDRDSSSignalView custom_signal_2=null;
	private BDRDSSSignalView custom_signal_3=null;
	private BDRDSSSignalView custom_signal_4=null;
	private BDRDSSSignalView[] array=new BDRDSSSignalView[4];
	private String TAG="CustomProgressView";

	public CustomProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext=context;
		init();
		Log.w("CustomProgressView", "CustomProgressView(Context context, AttributeSet attrs)");
	}
	
	/*初始化方法*/
	public void init(){
		View view=LayoutInflater.from(mContext).inflate(R.layout.custom_progress_view ,this,true);
		custom_signal_1=(BDRDSSSignalView)this.findViewById(R.id.progress_signal_1);
		custom_signal_2=(BDRDSSSignalView)this.findViewById(R.id.progress_signal_2);
		custom_signal_3=(BDRDSSSignalView)this.findViewById(R.id.progress_signal_3);
		custom_signal_4=(BDRDSSSignalView)this.findViewById(R.id.progress_signal_4);
		array[0]=custom_signal_1;
		array[1]=custom_signal_2;
		array[2]=custom_signal_3;
		array[3]=custom_signal_4;
	}
	
	
	public void setProgress(int num){
		if(num<=array.length){
			 for(int i=0;i<array.length;i++){
				 if(i<num){
					 
				     array[i].updateCircleBgColor(mContext.getResources().getColor(R.color.bd_rdss_have_signal));
//					 Log.w("CustomProgressView", "i<num");

				 }else{
					 array[i].updateCircleBgColor(mContext.getResources().getColor(R.color.bd_rdss_no_signal));

//					 Log.w("CustomProgressView", "i>=num");
				 }
				 array[i].invalidate();
			 }
		}else{
			//DswLog.log(TAG,"num is morth than the max!", DswLog.LOG_INFO);
		}
	}
}
