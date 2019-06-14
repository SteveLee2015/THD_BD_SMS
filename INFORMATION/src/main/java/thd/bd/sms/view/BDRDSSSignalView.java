package thd.bd.sms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import thd.bd.sms.R;
import thd.bd.sms.utils.Utils;


/**
 * 北斗RDSS的信号量柱
 * @author llg
 */
@SuppressLint("AppCompatCustomView")
public class BDRDSSSignalView extends View {
	
	private final String TAG = "BDRDSSSignalView";
	private Context mContext=null;
	
	private int color;
	
	
	public BDRDSSSignalView(Context context) {
		super(context);
		this.mContext=context;
		color = mContext.getResources().getColor(R.color.bd_rdss_no_signal);
		Log.e(TAG, "====LERRYTEST_bs================BDRDSSSignalView(Context context)===========" );
	}

	public BDRDSSSignalView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext=context;
		color = mContext.getResources().getColor(R.color.bd_rdss_no_signal);
	}

	public BDRDSSSignalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext=context;
		color = mContext.getResources().getColor(R.color.bd_rdss_no_signal);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
//		paint.setColor(Color.GRAY);
		//canvas.drawCircle(Utils.dp2pixel(13), Utils.dp2pixel(17), Utils.dp2pixel(12), paint);
		canvas.drawRect(Utils.dp2pixel(5), Utils.dp2pixel(5), Utils.dp2pixel(120), Utils.dp2pixel(120), paint);
//		canvas.drawRect(Utils.dp2pixel(13), Utils.dp2pixel(13), Utils.dp2pixel(13), Utils.dp2pixel(13),paint);
	}
	
	public void updateCircleBgColor(int mColor){
		color=mColor;
		Log.e(TAG, "====LERRYTEST_bs==62==============updateCircleBgColor==========="+color );
	}	
}
