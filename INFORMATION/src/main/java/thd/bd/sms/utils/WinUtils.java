package thd.bd.sms.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import thd.bd.sms.R;

/**
 * Window操作帮助类
 * @author llg052
 *
 */
public class WinUtils {
	
	/**
	 * 设置对话框的宽度 和高度
	 * @param mActivity
	 * @param widthPer
	 * @param heightPer
	 */
	/*public static void setDialogPosition(Activity mActivity , float widthPer, float heightPer) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager m = mActivity.getWindowManager();
		m.getDefaultDisplay().getMetrics(metrics);
		LayoutParams p = mActivity.getWindow().getAttributes();
		p.height = (int) ((metrics.heightPixels)*heightPer);// 高度设置为屏幕的
		p.width = (int) ((metrics.widthPixels)*widthPer);//宽度
		mActivity.getWindow().setAttributes(p);
	}*/
	
	/**
	 * 隐藏输入键盘
	 * @param mActivity
	 */
	public static void hiddenKeyBoard(Activity mActivity) {
		mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	
	
	public static int dip2px(Context context, double dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, double pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		//Toast.makeText(context, "px->dp系数:"+scale+"", 0).show();
		return (int) (pxValue / scale + 0.5f);
	}
	
	@SuppressWarnings("deprecation")
	public static int getDisplayWidth(Activity mActivity) {
		WindowManager windowManager = mActivity.getWindowManager();
		Display defaultDisplay = windowManager.getDefaultDisplay();
		int width = defaultDisplay.getWidth();
		return width;
	}
	@SuppressWarnings("deprecation")
	public static int getDisplayHeight(Activity mActivity) {
		WindowManager windowManager = mActivity.getWindowManager();
		Display defaultDisplay = windowManager.getDefaultDisplay();
		int height = defaultDisplay.getHeight();
		return height;
	}

	public static void setWinTitleColor(Activity context){
		Window window = context.getWindow();
		//After LOLLIPOP not translucent status bar
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//Then call setStatusBarColor.
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(context.getResources().getColor(R.color.colorMainTitle));
	}

}
