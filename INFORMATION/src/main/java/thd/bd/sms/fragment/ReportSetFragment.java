package thd.bd.sms.fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import thd.bd.sms.R;
import thd.bd.sms.activity.BDContactActivity;
import thd.bd.sms.base.BaseFragment;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.bean.ReportSet;
import thd.bd.sms.database.ReportSetDatabaseOperation;
import thd.bd.sms.database.StateCodeOperation;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.view.CustomListView;
import thd.bd.sms.view.OnCustomListListener;

import static android.app.Activity.RESULT_OK;


/**
 * 连续报告策略设置
 * 
 * @author llg
 */
public class ReportSetFragment extends BaseFragment implements OnClickListener {
	/**
	 * 日志标识
	 */
	private static final String TAG = "ReportSetFragment";

	
	protected Context mContext;

	/**
	 * 报告频度
	 */
	private EditText reportHz = null;


	/**
	 * 天线数据
	 */
	private EditText antenna = null;
	private Spinner spinner = null;


	/**
	 * 设置按钮
	 */
	private Button setBtn = null;
	private Button contactBtn = null;

	/**
	 * 报告类型
	 */
	protected CustomListView reportType = null;


	/**
	 * RDSS定位设置数据操作
	 */
	protected ReportSetDatabaseOperation settingDatabaseOper = null;

	protected EditText reportNum;//报告平台号


	private String[] datas;//报告类型数据


	protected LinearLayout ll_tianxian;//t天线布局
	protected LinearLayout ll_zhuangtai;//状态布局

	public final int REQUEST_CONTACT = 1,BD_CURR_DEVICE_MODE=0x10002;
	private String statuCode; //状态内容
	private int statuCodeInt;//状态序号
	protected LinearLayout ll_change;
	protected LinearLayout ll_desc;
	protected TextView tv_desc;


	@Override
	public View initView() {
		mContext = getActivity();
		View view = View.inflate(getActivity(), R.layout.fragment_report_set, null);

		initUI(view);
		initDatas();
		getLastData();

		return view;
	}

	public void getLastData() {

		ReportSet set =settingDatabaseOper.getFirst();
		/* 如果数据库中有数据,则在界面上显示数据库存储数据 */
		if (set != null) {
			setText(set);
		}
	}


	private void initDatas() {

		StateCodeOperation oper = new StateCodeOperation(mContext);
		final String[] listMsg = oper.getAllMessagesArray();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item, listMsg);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
				statuCode = listMsg[position];
				statuCodeInt = position;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//上次记录回写

			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		reportType.setOnCustomListener(new OnCustomListListener() {
			@Override
			public void onListIndex(int num) {
				//reportHz.setEnabled(num==0?false:true);
				reportType.setIndex(num);
				// TODO 查询数据库
				String data = datas[num];
				ReportSet reportSet = settingDatabaseOper.getByType(num+"");
				//修改
				if (reportSet!=null) {
					setText(reportSet);
				}else {
					reportSet = new ReportSet();
					int currentIndex = reportType.getCurrentIndex();
					reportSet.setReportType(currentIndex+"");
					reportSet.setReportHz("60");
					reportSet.setReportNnm("192311");
					reportSet.setTianxianValue("40");
					reportSet.setStatusCode(0);
					setText(reportSet);
				}
			}
		});

	}

	/**
	 * 初始化UI
	 */
	public void initUI(View view) {
		datas = this.getResources().getStringArray(R.array.report_type_array);
		ll_change = (LinearLayout) view.findViewById(R.id.ll_to_change);
		ll_desc = (LinearLayout) view.findViewById(R.id.ll_desc);
		tv_desc = (TextView) view.findViewById(R.id.tv_dest);
		settingDatabaseOper = new ReportSetDatabaseOperation(mContext);
		reportType = (CustomListView) view.findViewById(R.id.report_type);
		ll_tianxian = (LinearLayout) view.findViewById(R.id.ll_tianxian);
		ll_zhuangtai = (LinearLayout) view.findViewById(R.id.ll_zhuangtai);
		reportHz = (EditText) view.findViewById(R.id.report_frequency);
		reportNum = (EditText) view.findViewById(R.id.report_num);
		antenna = (EditText) view.findViewById(R.id.tianxian_height_value);
		spinner = (Spinner) view.findViewById(R.id.spinner);
		setBtn = (Button) view.findViewById(R.id.bdset_submit_btn);
		contactBtn = (Button) view.findViewById(R.id.contact);
		reportType.setData(datas);
		setBtn.setOnClickListener(this);
		contactBtn.setOnClickListener(this);
	}
	
	
	protected void setText(ReportSet set) {
		reportHz.setText(set.getReportHz()+"");
		String type = set.getReportType();
		int intType = Integer.parseInt(type);
		reportType.setIndex(intType);
		reportNum.setText(set.getReportNnm()+"");
		antenna.setText(set.getTianxianValue()+"");
		
		String reportType2 = set.getReportType();
		
		if (!"2".equals(reportType2)) {
			antenna.setEnabled(false);
			ll_tianxian.setVisibility(View.GONE);
		}else {
			antenna.setEnabled(true);
			ll_tianxian.setVisibility(View.VISIBLE);
		}
		if (!"0".equals(reportType2)) {
			spinner.setEnabled(false);
			ll_zhuangtai.setVisibility(View.GONE);
		}else {
			spinner.setEnabled(true);
			statuCodeInt = set.getStatusCode();
			spinner.setSelection(statuCodeInt);
			ll_zhuangtai.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){

			case R.id.contact:{

				Intent intent = new Intent();
				intent.setClass(mContext, BDContactActivity.class);
				intent.setData(BDContactColumn.CONTENT_URI);
				intent.putExtra(Config.NEED_BACK, true);
				startActivityForResult(intent, REQUEST_CONTACT);
				break;
			}
			case R.id.bdset_submit_btn:{
				//暂时 关闭
				if (!Utils.checkBDSimCard(getActivity()))return;
				
				/*校验定位频率数据*/
				if (reportHz == null|| "".equals(reportHz.getText().toString())) {
					Toast.makeText(mContext,"频度不能为空!",Toast.LENGTH_SHORT).show();
					return;
				}
				/*校验检查平台号码*/
				if (reportNum == null|| "".equals(reportNum.getText().toString())) {
					Toast.makeText(mContext,"平台号码不能为空!",Toast.LENGTH_SHORT).show();
					return;
				}
                /*校验天线数据*/ 
				if (antenna == null || "".equals(antenna.getText().toString())) {
					Toast.makeText(mContext,"天线高不能为空!",Toast.LENGTH_SHORT).show();
					return;
				}

				int frequency = Integer.valueOf(reportHz.getText().toString());
                /*校验定位频度数据是否大于卡频度*/
				if (frequency <= SharedPreferencesHelper.getSericeFeq()) {
					Toast.makeText(mContext,"报告频度必须大于" + SharedPreferencesHelper.getSericeFeq() + "秒",Toast.LENGTH_SHORT).show();
					return;
				}
				/* 保存至数据库 */
				ReportSet set = new ReportSet();
				set.setReportNnm(reportNum.getText().toString());
			    set.setReportHz(reportHz.getText().toString());
				set.setTianxianValue(antenna.getText().toString());
				int currentIndex = reportType.getCurrentIndex();
				//String strType = datas[currentIndex];
				set.setReportType(currentIndex+"");
				set.setStatusCode(statuCodeInt);
				boolean toDataBase = saveToDataBase(set);
				if (toDataBase){
					//ReportSetActivity.this.finish();
				}
				break;
			}
			default:
				break;
		}
	}

	
	public boolean saveToDataBase(ReportSet set) {
		boolean istrue = false;
		/*如果数据库中没有定位设置的数据*/
		Cursor cursor = settingDatabaseOper.getWithType(set.getReportType());
		if (cursor!=null) {
			//settingDatabaseOper.deleteByType(set.getReportType());
			istrue = settingDatabaseOper.update(set);
		}else {
			istrue = settingDatabaseOper.insert(set);
		}
		if (istrue) {
			Toast.makeText(mContext,"参数保存成功",Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext,"参数保存失败",Toast.LENGTH_SHORT).show();
		}
		return istrue;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(settingDatabaseOper!=null){
			settingDatabaseOper.close();
		}
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
				Cursor cursor=mContext.getContentResolver().query(result,null, null, null, null);
				String mUserAddress="";
				if(cursor.moveToFirst()){
					String name=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
					//使用ContentResolver查找联系人的电话号码
					long contactId= ContentUris.parseId(result);
					Cursor phones=getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId, null, null);
					String phoneNumber="";
					if(phones.moveToNext()){
						//获取查询结果中电话号码列中数据
						phoneNumber=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					phones.close();
					mUserAddress=name+"("+phoneNumber+")";
					if (reportNum!=null){
						reportNum.setText(phoneNumber);
					}
				}
				cursor.close();
			}
		}
	}
}
