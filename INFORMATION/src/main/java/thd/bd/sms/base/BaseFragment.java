package thd.bd.sms.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * fragment 基类
 * @author llg052
 *
 */
public abstract class BaseFragment extends Fragment {

	public Activity mActivity;
	public ProgressDialog mProgressDialog;
	protected SharedPreferences sp;
	public ProgressDialog mDialog;
	Unbinder mUnbinder;
	protected ProgressDialog showProgressDialog(String message) {
		return showProgressDialog(message, false);
	}
	protected ProgressDialog showProgressDialog(String message, boolean isHorizontal) {
		return showProgressDialog(mActivity, message, isHorizontal);
	}
	protected ProgressDialog showProgressDialog(Activity activity, String message, boolean isHorizontal) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(activity);
//			mProgressDialog.setProgressStyle(isHorizontal?mProgressDialog.STYLE_HORIZONTAL:mProgressDialog.STYLE_SPINNER);
			mProgressDialog.setProgressStyle(mProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(!isHorizontal);
			mProgressDialog.setCancelable(false);
			
		}
		
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
		return mProgressDialog;
	}
	
	protected void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
		}
	}
	
	public void killeditText(EditText mEditext) {
		mEditext.setInputType(InputType.TYPE_NULL);
		mEditext.clearFocus();
	}
	
	protected void changeFragmentNeedBack(Fragment famFragment) {}
	
	protected void changeFragmentNeedBack(int id ,Fragment famFragment) {
		getFragmentManager()
		.beginTransaction()
		.addToBackStack(getClass()
				.getSimpleName())
				//.add(R.id.fl_layout, famFragment)
				.replace(id, famFragment)
				.commitAllowingStateLoss();
	}
	protected void changeFragment(Fragment famFragment) {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		EventBus.getDefault().register(BaseFragment.this);
	}

	@Override
	public void onResume() {
		super.onResume();

	}


	/**
	 * Fragment布局
	 * 
	 * return 返回Fragment应该填充的View对象
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = initView();
//		mUnbinder = ButterKnife.bind(this.getClass(),view);
		return view;
	}

	/**
	 * activity 粘附的activity
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		sp =activity.getSharedPreferences("default", Context.MODE_PRIVATE);
	}

	/**
	 * Activity创建完成
	 * savedInstanceState 保存的参数
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initData();
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		if(mUnbinder != null)
//		mUnbinder.unbind();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		EventBus.getDefault().unregister(BaseFragment.this);
	}

	/**
	 * 初始化界面 该方法必须实现!
	 */
	public abstract View initView();

	/**
	 * 初始化数据 从网络中获取数据
	 * 
	 */
	public void initData() {

	}

	/**
	 * 加载等待
	 */
	private void progressDialog(String msg) {
		if (mDialog == null) {
			mDialog = new ProgressDialog(getActivity());
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDialog.setMessage(msg);
			mDialog.setIndeterminate(false);
			mDialog.setCancelable(true);
			mDialog.show();
		}
	}
}
