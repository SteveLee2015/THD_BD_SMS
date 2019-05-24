package thd.bd.sms.activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import thd.bd.sms.R;
import thd.bd.sms.bean.BDContactColumn;
import thd.bd.sms.database.DataBaseHelper.BDMessageColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thd.bd.sms.base.BaseActivity;
import thd.bd.sms.utils.Config;
import thd.bd.sms.utils.EditTextHelper;
import thd.bd.sms.utils.Utils;
import thd.bd.sms.utils.WinUtils;

/**
 * 北斗通讯录页面
 * @author lerry
 */
public class BDContactActivity extends BaseActivity {
	/**
	 * 日志标识
	 */
    private  static final String TAG="BDContactActivity";

    /**
     * 显示E200和手机号码的列表对象
     */
    private ExpandableListView contactListView=null;
    
    /**
     * 通讯录查询内容条件输入框
     */
    private EditText mSearchContact=null;
    
    /**
     * 增加联系人图标
     */
    private ImageView addContact=null;
    private Context mContext=this;
    private BaseExpandableListAdapter adapter=null;
    private  long rowId=0;
    private Uri mUri=null;
    private Cursor mCursor=null;
    private LinearLayout returnLayout=null;
    private List<Map<String,Object>> mE200List=null,mPhoneList=null;
    private Map<Integer,String> phoneMap=null;
    //private String[] bdContactType={"北斗号码","手机号码"};
    private String[] bdContactType={"北斗号码"};//隐藏手机号码
    private Map<String,Object> longClickMap=null;

	private boolean needBack;//是否需要返回

	private TextView titleName;//标题

	@Override
	protected int getContentView() {
		return R.layout.activity_bdcontact;
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		WinUtils.setWinTitleColor(this);
		super.onCreate(savedInstanceState);
		WinUtils.hiddenKeyBoard(this);
		Intent intent = getIntent();
		if (intent!=null) {
			needBack = intent.getBooleanExtra(Config.NEED_BACK, false);
		}
		initUI();	
		
		initData();
		
	}
	
    /**
     * 加载数据
     */
	private void initData() {

		mCursor=this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		Cursor phones=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, null, null, null);
		while(phones.moveToNext()){
			String phoneNumber=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			int  row_id =phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			phoneMap.put(row_id, phoneNumber);
		}
		phones.close();
		while(mCursor.moveToNext()){
			Map<String,Object> map=new HashMap<String,Object>();
			//获取联系人ID
			String contactId=mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts._ID));
		    //获取联系人的名称
			String name=mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String phoneNumber=phoneMap.get(Integer.valueOf(contactId));
			map.put(BDContactColumn._ID, contactId);
			map.put(BDContactColumn.USER_NAME, name);
			map.put(BDContactColumn.CARD_NUM, phoneNumber);
			if(phoneNumber!=null&&(!"".equals(phoneNumber))){
				if(phoneNumber.length()>=6 && phoneNumber.length()<=7){
					mE200List.add(map);
				}else{
					mPhoneList.add(map);
				}
			}
		}
		mCursor.close();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (adapter==null) {
			adapter=new BDContactAdapter();
		}
        contactListView.setAdapter(adapter);
        /*默认展开ExpandableListView的项目*/
        for(int i=0;i<bdContactType.length;i++){
        	 contactListView.expandGroup(i); 	
        }
        /*ExpandableListView增加点击事件*/
        contactListView.setOnChildClickListener(new OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
                                        int groupPosition, int childPosition, long arg4) {


				if (needBack==false) {
					return true;
				}
				Intent intent=new Intent();
				Map<String,Object> map=null;
				long mid=0;
				if(groupPosition==0){
					map=mE200List.get(childPosition);
				}else if(groupPosition==1){
					map=mPhoneList.get(childPosition);
				}
				if(map!=null){
					mid=Long.valueOf(String.valueOf(map.get(BDContactColumn._ID)));
				}
				if(mUri!=null){
					 Uri data=ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, mid);
				     intent.setData(data);
				     setResult(RESULT_OK,intent);
				     BDContactActivity.this.finish();
			    }
				return true;
			}
        	
        });
		
        contactListView.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           final int index, long arg3) {
				    final int groupId=Integer.valueOf(String.valueOf(view.getTag(R.id.bd_label_1)));
				    final int childId=Integer.valueOf(String.valueOf(view.getTag(R.id.bd_label_2)));
//				    Toast.makeText(mContext, groupId+","+childId, Toast.LENGTH_SHORT).show();
				    String name="";
				    if(childId!=-1){
					    if(groupId==0){
					    	longClickMap=mE200List.get(childId);
					    }else if(groupId==1){
					    	longClickMap=mPhoneList.get(childId);
					    }
						if(longClickMap!=null){
						    name=String.valueOf(longClickMap.get(BDContactColumn.USER_NAME));
							rowId=Long.valueOf(String.valueOf(longClickMap.get(BDContactColumn._ID)));
							final String[] items={"修改","删除"};
							AlertDialog.Builder builder=new AlertDialog.Builder(mContext).setTitle(name)
							.setItems(items, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface arg0, final int position) {
								    switch(position){
								    	case 0: //修改
								    		   AlertDialog.Builder alertDialog=new AlertDialog.Builder(BDContactActivity.this);
											   alertDialog.setTitle("修改联系人");
											   LayoutInflater inflater=LayoutInflater.from(BDContactActivity.this);
											   final View view=inflater.inflate(R.layout.activity_add_contact_dialog, null);
											   final EditText mUserNameEditText=(EditText)view.findViewById(R.id.user_name);
												//排除特殊字符
												EditTextHelper.setEditTextInhibitInputSpeChat(mUserNameEditText);
											   final EditText mUserPhoneEditText=(EditText)view.findViewById(R.id.user_phone);
											   alertDialog.setView(view);
											   Cursor displayNameCursor=mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID+"="+rowId,null, null);
											   String name="";
											   String phone="";
											   if(displayNameCursor.moveToNext()){
												   name=displayNameCursor.getString(displayNameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
											   }
											   Cursor phoneCursor=mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+rowId,null, null);
											   if(phoneCursor.moveToNext()){
												    phone=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
											   }
												phoneCursor.close();
											   mUserNameEditText.setText(name);
											   mUserPhoneEditText.setText(phone);
											   alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
													@Override
													public void onClick(DialogInterface arg0, int arg1) {
														String userName=mUserNameEditText.getEditableText().toString();
														String phoneNumber=mUserPhoneEditText.getEditableText().toString();
														if(userName==null||"".equals(userName)){
															Toast.makeText(BDContactActivity.this,"用户名称不能为空!", Toast.LENGTH_SHORT).show();
															return;
														}
														if(phoneNumber==null||"".equals(phoneNumber)){
															Toast.makeText(BDContactActivity.this,"用户号码不能为空!", Toast.LENGTH_SHORT).show();
															return;
														}
														if (!(phoneNumber.length()>=6&&phoneNumber.length()<=7)) {
															Toast.makeText(BDContactActivity.this,"请输入6位或7位北斗号码!", Toast.LENGTH_SHORT).show();
															return;
														}
														//增加联系人
														try {
															Utils.updatePhoneNumber(BDContactActivity.this,rowId,userName, phoneNumber);
														    Toast.makeText(BDContactActivity.this, "修改联系人成功!",Toast.LENGTH_SHORT).show();
														} catch (Exception e) {
															Toast.makeText(BDContactActivity.this, "修改联系人失败!",Toast.LENGTH_SHORT).show();
															e.printStackTrace();
														}
														longClickMap.put(BDContactColumn.USER_NAME, userName);
														longClickMap.put(BDContactColumn.CARD_NUM, phoneNumber);
														if(groupId==0){
										    				mE200List.remove(childId);
										    			}else if(groupId==1){
										    				mPhoneList.remove(childId);
										    			}
														if(phoneNumber.length()>=6&&phoneNumber.length()<=7){
															mE200List.add(longClickMap);
														}else{
															mPhoneList.add(longClickMap);
														}
														adapter.notifyDataSetChanged();
													}
											   });
											   alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int arg1) {
														dialog.dismiss();
													}
											   });
											   alertDialog.create().show();
								    		break;
								    	case 1://删除
								    		AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
								    		builder.setTitle("删除联系人");
								    		builder.setMessage("是否删除该联系人?");
								    		builder.setCancelable(false);
								    		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
												@Override
												public void onClick(DialogInterface arg0, int arg1) {
													Uri deleteUri=ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, rowId);
													int num=mContext.getContentResolver().delete(deleteUri,null, null);
										    		if(num>0){
										    			Toast.makeText(mContext, "联系人删除成功!", Toast.LENGTH_SHORT).show();
										    			if(groupId==0){
										    				mE200List.remove(childId);
										    			}else if(groupId==1){
										    				mPhoneList.remove(childId);
										    			}
											    		adapter.notifyDataSetChanged();
										    		}else{
										    			Toast.makeText(mContext, "联系人删除失败!", Toast.LENGTH_SHORT).show();
										    		}
												}
								    		});
								    		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								    			@Override
												public void onClick(DialogInterface arg0, int arg1) {
								    				
								    			} 
											});
								    		final AlertDialog dialog=builder.create();
								    		dialog.show();
								    		break;
								    	default:
								    		break;
								    }
								}
							});
							builder.create().show();
					  }
				}
				return true;
			}
		});
		
		mSearchContact.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable editable) {
					mE200List.clear();
					mPhoneList.clear();
					String content=editable.toString().trim();
					Cursor phoneCursor=null;
					if(!"".equals(content)){
						if(Utils.isNumber(content)){
							Cursor phones=mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.NUMBER+" like '%"+content+"%'", null, null);
							if (phones==null)return;
							while(phones.moveToNext()){
								Map<String,Object> map=new HashMap<String,Object>();
								//获取联系人ID
								String contactId=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
								String phoneNumber=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								Cursor displayNameCursor=mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,ContactsContract.Contacts._ID+"="+contactId,null, null);
								String name="";
								if (displayNameCursor ==null)return;
								if(displayNameCursor.moveToNext()){
									//获取联系人的名称
									name=displayNameCursor.getString(displayNameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
								}
								map.put(BDContactColumn._ID, contactId);
								map.put(BDContactColumn.USER_NAME, name);
								map.put(BDContactColumn.CARD_NUM, phoneNumber);
								if(phoneNumber!=null&&(!"".equals(phoneNumber))){
									if(phoneNumber.length()>=6&&phoneNumber.length()<=7){
										mE200List.add(map);
									}else{
										mPhoneList.add(map);
									}
								}
							}
							phones.close();
						}else{
							phoneCursor=mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.DISPLAY_NAME+" like '%"+content+"%'",null, null);
							while(phoneCursor.moveToNext()){
								Map<String,Object> map=new HashMap<String,Object>();
								//获取联系人ID
								String contactId=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts._ID));
							    //获取联系人的名称
								String name=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
								String phoneNumber=phoneMap.get(Integer.valueOf(contactId));
								map.put(BDContactColumn._ID, contactId);
								map.put(BDContactColumn.USER_NAME, name);
								map.put(BDContactColumn.CARD_NUM, phoneNumber);
								if(phoneNumber!=null&&(!"".equals(phoneNumber))){
									if(phoneNumber.length()>=6&&phoneNumber.length()<=7){
										mE200List.add(map);
									}else{
										mPhoneList.add(map);
									}
								}
							}
							phoneCursor.close();
						}
					}else{
						phoneCursor=mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);
						while(phoneCursor.moveToNext()){
							Map<String,Object> map=new HashMap<String,Object>();
							//获取联系人ID
							String contactId=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts._ID));
						    //获取联系人的名称
							String name=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
							String phoneNumber=phoneMap.get(Integer.valueOf(contactId));
							map.put(BDContactColumn._ID, contactId);
							map.put(BDContactColumn.USER_NAME, name);
							map.put(BDContactColumn.CARD_NUM, phoneNumber);
							if(phoneNumber!=null&&(!"".equals(phoneNumber))){
								if(phoneNumber.length()>=6&&phoneNumber.length()<=7){
									mE200List.add(map);
								}else{
									mPhoneList.add(map);
								}
							}
						}
						phoneCursor.close();
					}
					adapter.notifyDataSetChanged();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3){}
			
		});
		
		addContact.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
			   AlertDialog.Builder alertDialog=new AlertDialog.Builder(BDContactActivity.this);
			   alertDialog.setTitle("增加联系人");
			   LayoutInflater inflater=LayoutInflater.from(BDContactActivity.this);
			   final View view=inflater.inflate(R.layout.activity_add_contact_dialog, null);
			   final EditText mUserNameEditText=(EditText)view.findViewById(R.id.user_name);
				EditTextHelper.setEditTextInhibitInputSpeChat(mUserNameEditText);
			   final EditText mUserPhoneEditText=(EditText)view.findViewById(R.id.user_phone);
			   alertDialog.setView(view);
			   alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String userName=mUserNameEditText.getEditableText().toString();
						String phoneNumber=mUserPhoneEditText.getEditableText().toString();
						if(userName==null||"".equals(userName)){
							Toast.makeText(BDContactActivity.this,"用户名称不能为空!", Toast.LENGTH_SHORT).show();
							return;
						}
						if(phoneNumber==null||"".equals(phoneNumber)){
							Toast.makeText(BDContactActivity.this,"用户号码不能为空!", Toast.LENGTH_SHORT).show();
							return;
						}

						if (!(phoneNumber.length()>=6&&phoneNumber.length()<=7)) {
							Toast.makeText(BDContactActivity.this,"请输入6位或7位北斗号码!", Toast.LENGTH_SHORT).show();
							return;
						}
						long rowId=0;
						//增加联系人
						try {
							rowId=Utils.insertPhoneNumber(BDContactActivity.this, userName, phoneNumber);
						    Toast.makeText(BDContactActivity.this, "增加联系人成功!",Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							Toast.makeText(BDContactActivity.this, "增加联系人失败!",Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
						Map<String,Object> map=new HashMap<String,Object>();
						map.put(BDContactColumn._ID, rowId);
						map.put(BDContactColumn.USER_NAME, userName);
						map.put(BDContactColumn.CARD_NUM, phoneNumber);
						
						if(phoneNumber.length()>=6&&phoneNumber.length()<=7){
							mE200List.add(map);
						}else{
							mPhoneList.add(map);
						}
						adapter.notifyDataSetChanged();
						arg0.dismiss();
					}
			   });
			   alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
			   });
			alertDialog.create().show();
		   }
		});
		
		returnLayout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
		         BDContactActivity.this.finish();		
			}
		});
	}



	@Override
	protected void onResume() {
		super.onResume();
	}
    
	
	class BDContactAdapter extends BaseExpandableListAdapter {

		
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if(groupPosition==0){
				return mE200List.get(childPosition);
			}else if(groupPosition==1){
				return mPhoneList.get(childPosition);
			}else{
				return null;
			}
		}

		@Override
		public long getChildId(int arg0, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View contentView,
                                 ViewGroup parent) {
			LinearLayout mlayout=(LinearLayout)LinearLayout.inflate(mContext, R.layout.bd_contact_list_item, null);
			TextView userName=(TextView)mlayout.findViewById(R.id.item_user_name);
			TextView phoneNumber=(TextView)mlayout.findViewById(R.id.item_card_num);
			Map<String,Object> map=(Map<String,Object>)getChild(groupPosition,childPosition);
			userName.setText(String.valueOf(map.get(BDContactColumn.USER_NAME)));
			phoneNumber.setText(String.valueOf(map.get(BDContactColumn.CARD_NUM)));
			mlayout.setTag(R.id.bd_label_1, groupPosition+"");
			mlayout.setTag(R.id.bd_label_2, childPosition+"");
			return mlayout;
		}

		
		@Override
		public int getChildrenCount(int groupPosition) {
			if(groupPosition==0){
				return mE200List.size();
			}else if(groupPosition==1){
				return mPhoneList.size();
			}
			return 0;
		}

		
		@Override
		public Object getGroup(int groupPosition) {
			return bdContactType[groupPosition];
		}

		@Override
		public int getGroupCount() {
			return bdContactType.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean arg1, View arg2,
                                 ViewGroup arg3) {
			LinearLayout mlayout=(LinearLayout)LinearLayout.inflate(mContext, R.layout.bd_contact_label_item, null);
			TextView lableName=(TextView)mlayout.findViewById(R.id.bd_contact_label_name);
			lableName.setText(String.valueOf(getGroup(groupPosition)));
			mlayout.setTag(R.id.bd_label_1, groupPosition+"");
			mlayout.setTag(R.id.bd_label_2,"-1");
			return mlayout;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}
	}

	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bdcontact, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_del_all:
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}*/
		
	
	private void initUI(){
		titleName=(TextView)this.findViewById(R.id.sub_title_name);
		titleName.setText("通讯录");
		addContact=(ImageView)this.findViewById(R.id.addContact);
		mSearchContact=(EditText)this.findViewById(R.id.search_contact);
		contactListView=(ExpandableListView)this.findViewById(R.id.bd_contact_listview);
		returnLayout=(LinearLayout)this.findViewById(R.id.return_home_layout);
		mUri=getIntent().getData();
		phoneMap=new HashMap<Integer,String>();
	    mE200List=new ArrayList<Map<String,Object>>();
		mPhoneList=new ArrayList<Map<String,Object>>();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mCursor!=null){
			mCursor.close();
			mCursor=null;
		}
		if(adapter!=null){
			adapter=null;
		}
	}

}
