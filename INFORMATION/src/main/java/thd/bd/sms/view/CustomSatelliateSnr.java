package thd.bd.sms.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.GpsSatellite;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import java.util.List;

import thd.bd.sms.bean.SatelliteInfo;
import thd.bd.sms.utils.Utils;

/**
 * 自定义卫星星图
 * @author llg
 */
public class CustomSatelliateSnr extends View {

	private Context mContext=null;

	//private static int MAX_VISUAL_SATELLIATE_NUM = 16;
	private static int MAX_VISUAL_SATELLIATE_NUM = 36;

	private String[] mSatelliatePRN = new String[MAX_VISUAL_SATELLIATE_NUM];//卫星号数

	private float[][] snrRect=new float[MAX_VISUAL_SATELLIATE_NUM][4];

	private float[][] prnPixelArr=new float[MAX_VISUAL_SATELLIATE_NUM][2];//

	private float[][] snrPixelArr=new float[MAX_VISUAL_SATELLIATE_NUM][3];//载噪比值

	//private int gapValue=10;
	private int gapValue=10;//柱状图宽度

	//private Bitmap   bitmap2=null;

	private int
			bitMapWidth=0,bitMapHeight=0,
			width=0, height=0;

	private float left=0,top=0;

	Paint mPaint=new Paint();

	public CustomSatelliateSnr(Context context) {
		super(context);
		init();
	}


	public CustomSatelliateSnr(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}


	public CustomSatelliateSnr(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext=context;
		init();
	}

	private void init(){
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(1);
		// 初始化数据
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		for (int i = 0; i < MAX_VISUAL_SATELLIATE_NUM; i++) {
			mSatelliatePRN[i] = "";
			snrRect[i][0]=-100;
			snrRect[i][1]=-100;
			snrRect[i][2]=-100;
			snrRect[i][3]=-100;
			prnPixelArr[i][0]=-100;
			prnPixelArr[i][1]=-100;
			snrPixelArr[i][0]=-100;
			snrPixelArr[i][1]=-100;
			snrPixelArr[i][2]=-100;
		}
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/*绘制可见卫星图*/
		//canvas.drawBitmap(bitmap2,left, top, paint);
		for(int i=0;i<MAX_VISUAL_SATELLIATE_NUM;i++){

			String prn = (!"".equals(mSatelliatePRN[i]))?mSatelliatePRN[i]:"0";
			if(prn.equals("0")){
				continue;
			}
			Integer prnInt = Integer.valueOf(prn);

			if (prnInt>=160){
				mPaint.setColor(Color.BLUE);
			}else {
				mPaint.setColor(Color.RED);
			}
			//paint.setColor(mContext.getResources().getColor(R.color.statellite_snr_bg));
			canvas.drawRect(snrRect[i][0],snrRect[i][1],snrRect[i][2], snrRect[i][3], mPaint);
			//paint.setTextSize(Utils.dp2pixel(15));
			mPaint.setTextSize(Utils.dp2pixel(10));//柱状图文字
			int mSatelliatePRN_No = prnInt>=160?(prnInt-160):prnInt;
			//canvas.drawText(mSatelliatePRN[i],prnPixelArr[i][0],prnPixelArr[i][1], mPaint);
			canvas.drawText(mSatelliatePRN_No+"",prnPixelArr[i][0],prnPixelArr[i][1], mPaint);
			canvas.drawText(String.valueOf((int)snrPixelArr[i][0]),snrPixelArr[i][1],snrPixelArr[i][2], mPaint);
		}
//		canvas.drawText(120+"",300,300, mPaint);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		resize();
	}

	List<GpsSatellite> listbd;
	public void showMap(List<GpsSatellite> list) {

		//清除上次数据
		initData();

		int index=0;
		for (int i = 0; i < MAX_VISUAL_SATELLIATE_NUM; i++) {
			int ii = list.size();
			if (i < list.size()) {
				GpsSatellite mGPSatellite = list.get(i);
				Log.i("LERRYTEST_RN" ,"=============================>mGPSatellite.getSnr() =" + mGPSatellite.getSnr());
				if(mGPSatellite.getSnr()>0){
					if(mGPSatellite.getPrn()>=160){
						mSatelliatePRN[index] = (mGPSatellite.getPrn()-160+160)+ "";
						//mSatelliatePRN[index] = (mGPSatellite.getPrn()-160)+ "";
					}else{
						mSatelliatePRN[index] =mGPSatellite.getPrn()+"";
					}
					Log.i("LERRYTEST_RN","============> prn["+index+"] ="+ mSatelliatePRN[index] +", snr="+mGPSatellite.getSnr());
					int startValue=(2*index+1)*gapValue;
					int stopValue=(2*index+2)*gapValue;
					float mStartRectValue=left + Utils.dp2pixel(startValue);
					float mStopRectValue=left + Utils.dp2pixel(stopValue);
					float mShowValue=(bitMapHeight)*(1-mGPSatellite.getSnr()/100.0f);
					Log.i("LERRYTEST_RN","================================>mStartRectValue =" +mStartRectValue +",mStopRectValue=" +mStopRectValue +",mShowValue="+mShowValue +",bitMapHeight ="+bitMapHeight);
					snrRect[index][0]=mStartRectValue;
					snrRect[index][1]=mShowValue;
					snrRect[index][2]=mStopRectValue;
					snrRect[index][3]=bitMapHeight;

					prnPixelArr[index][0]=mStartRectValue;
					prnPixelArr[index][1]=height - Utils.dp2pixel(2);

					snrPixelArr[index][0]=mGPSatellite.getSnr();
					snrPixelArr[index][1]=mStartRectValue;
					snrPixelArr[index][2]=mShowValue - Utils.dp2pixel(3);
					index++;
				}
			} else {
				mSatelliatePRN[i] = "";
				snrRect[i][0]=-100;
				snrRect[i][1]=-100;
				snrRect[i][2]=-100;
				snrRect[i][3]=-100;
				prnPixelArr[i][0]=-100;
				prnPixelArr[i][1]=-100;
				snrPixelArr[i][0]=-100;
				snrPixelArr[i][1]=-100;
				snrPixelArr[i][2]=-100;
			}
		}
		//postInvalidate();
		invalidate();
	}
	public void showMapBlue(List<SatelliteInfo> gsv) {

		//清除上次数据
		initData();
		int index = 0;
		index = addSatelliteInfo(index, gsv);
		Log.d(CustomSatelliateSnr.class.getSimpleName(),"index = "+index);
		while (index < MAX_VISUAL_SATELLIATE_NUM) {
			mSatelliatePRN[index] = "";
			snrRect[index][0]=-100;
			snrRect[index][1]=-100;
			snrRect[index][2]=-100;
			snrRect[index][3]=-100;
			prnPixelArr[index][0]=-100;
			prnPixelArr[index][1]=-100;
			snrPixelArr[index][0]=-100;
			snrPixelArr[index][1]=-100;
			snrPixelArr[index][2]=-100;
			index++;
		}
		postInvalidate();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		resize();
	}

	private void resize(){
		bitMapWidth=(MAX_VISUAL_SATELLIATE_NUM *2-1)*gapValue;
		bitMapHeight=height;
		left=(width-bitMapWidth)/2.0f;//第一个柱状图距离左边界距离
		top=-Utils.dp2pixel(13);
		bitMapHeight = height-Utils.dp2pixel(13);
	}
	private int addSatelliteInfo(int index ,List<SatelliteInfo> Infos){
		double valuel = 0;
		if(Infos != null )
			for(SatelliteInfo info : Infos) {
				if(info != null) {
					if (info.getSNR() > 0) {
						if (info.getNumber() >= 160) {
							mSatelliatePRN[index] = (info.getNumber() - 160 + 160) + "";
							//mSatelliatePRN[index] = (mGPSatellite.getPrn()-160)+ "";
						} else {
							mSatelliatePRN[index] = info.getNumber() + "";
						}
						int startValue = (2 * index + 1) * gapValue;
						int stopValue = (2 * index + 2) * gapValue;
						float mStartRectValue = left + Utils.dp2pixel(startValue);
						float mStopRectValue = left + Utils.dp2pixel(stopValue);

						valuel = info.getSNR();
						if(valuel > 100)
							valuel = 90;
						float mShowValue = (bitMapHeight) * (1 - (float)  valuel/ 100.0f);
						snrRect[index][0] = mStartRectValue;
						snrRect[index][1] = mShowValue;
						snrRect[index][2] = mStopRectValue;
						snrRect[index][3] = bitMapHeight;

						prnPixelArr[index][0] = mStartRectValue;
						prnPixelArr[index][1] = height - Utils.dp2pixel(2);

						snrPixelArr[index][0] = (float) info.getSNR();
						snrPixelArr[index][1] = mStartRectValue;
						snrPixelArr[index][2] = mShowValue - Utils.dp2pixel(3);
						index++;
					}
				}
			}
		return index;
	}
}
