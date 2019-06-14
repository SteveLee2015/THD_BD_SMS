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
//	private BDRDSSSignalView custom_signal_1=null;
//	private BDRDSSSignalView custom_signal_2=null;
//	private BDRDSSSignalView custom_signal_3=null;
//	private BDRDSSSignalView custom_signal_4=null;

	private View custom_signal_1=null;
	private View custom_signal_2=null;
	private View custom_signal_3=null;
	private View custom_signal_4=null;

	private View[] array=new View[4];
	private String TAG="CustomProgressView";

	public CustomProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext=context;
		setOrientation(VERTICAL);
		init();
//		Log.w("CustomProgressView", "LERRYTEST_bs===================CustomProgressView(Context context, AttributeSet attrs)");
	}
	
	/*初始化方法*/
	public void init(){
		View view=LayoutInflater.from(mContext).inflate(R.layout.custom_progress_view ,this,true);
		custom_signal_1=(View)view.findViewById(R.id.progress_signal_1);
		custom_signal_2=(View)view.findViewById(R.id.progress_signal_2);
		custom_signal_3=(View)view.findViewById(R.id.progress_signal_3);
		custom_signal_4=(View)view.findViewById(R.id.progress_signal_4);
		array[0]=custom_signal_1;
		array[1]=custom_signal_2;
		array[2]=custom_signal_3;
		array[3]=custom_signal_4;
//		Log.i("CustomProgressView", "LERRYTEST_bs================array[0]=custom_signal_1");
	}
	
	
	public void setProgress(int num){
		if(num<=array.length){//array.length=4
			 for(int i=0;i<array.length;i++){
				 if(i<num){
				 	if(num==1){
						array[i].setBackgroundColor(mContext.getResources().getColor(R.color.colorLine));
					}else if(num==2){
						array[i].setBackgroundColor(mContext.getResources().getColor(R.color.colorMainTitle));
					}else {
						array[i].setBackgroundColor(mContext.getResources().getColor(R.color.green));
					}
//				     array[i].updateCircleBgColor(mContext.getResources().getColor(R.color.bd_rdss_have_signal));
//					 Log.w("CustomProgressView", "LERRYTEST_bs============i<num=============i="+i+"===========array.length=="+array.length);

				 }else{
//					 array[i].updateCircleBgColor(mContext.getResources().getColor(R.color.bd_rdss_no_signal));
					 array[i].setBackgroundColor(mContext.getResources().getColor(R.color.bd_rdss_no_signal));

//					 Log.w("CustomProgressView", "LERRYTEST_bs==============i>=num=============i="+i+"===========array.length=="+array.length);
				 }
				 array[i].invalidate();
//				 Log.w("CustomProgressView", "LERRYTEST_bs===============array[i].invalidate();");
			 }
		}else{
			//DswLog.log(TAG,"num is morth than the max!", DswLog.LOG_INFO);
//			Log.w("CustomProgressView", "LERRYTEST_bs=====================num is more than the max!");
		}
	}
}
