package thd.bd.sms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import thd.bd.sms.base.DatabaseHelper;
import thd.bd.sms.base.DatabaseHelper.InstructionNavColumns;
import thd.bd.sms.bean.BDInstructionNav;
import thd.bd.sms.bean.BDPoint;
import thd.bd.sms.utils.Utils;


/**
 * 指令导航表操作类
 * @author steve
 */
public class BDInstructionNavOperation {
     
	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CREATED = "created";
	public static final String KEY_CREATED_TIME = "created_time";

	private DatabaseHelper databaseHelper;
	private Context context;
	private SQLiteDatabase sqliteDatabase;

	public BDInstructionNavOperation(Context mContext){
		this.context=mContext;
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
	}
	
	/**
	 * 增加北斗指令导航
	 * @return
	 */
	public long insert(String useraddress, String lineId, String targetPointStr, String passPointStr, String evadePointStr, String createTime){
		ContentValues contentValues=new ContentValues();
		contentValues.put(InstructionNavColumns.NAV_LINE_ID,lineId);
		contentValues.put(InstructionNavColumns.KEY_CREATED_TIME,createTime);
		contentValues.put(InstructionNavColumns.TARGET_POINT, targetPointStr);
		contentValues.put(InstructionNavColumns.PASS_POINT,passPointStr);
		contentValues.put(InstructionNavColumns.EVADE_POINT,evadePointStr);
		contentValues.put(InstructionNavColumns.USERADDRESS,useraddress);
//		contentValues.put(InstructionNavColumns.MESSAGE_STATE,"new");
		long id=sqliteDatabase.insert(InstructionNavColumns.TABLE_NAME, null, contentValues) ;
		return id;
	}

	public int  update(String useraddress, String lineId, String targetPointStr, String passPointStr, String evadePointStr, String createTime){
		ContentValues contentValues=new ContentValues();
		contentValues.put(InstructionNavColumns.NAV_LINE_ID,lineId);
		contentValues.put(InstructionNavColumns.KEY_CREATED_TIME,createTime);
		contentValues.put(InstructionNavColumns.TARGET_POINT, targetPointStr);
		contentValues.put(InstructionNavColumns.PASS_POINT,passPointStr);
		contentValues.put(InstructionNavColumns.EVADE_POINT,evadePointStr);
		contentValues.put(InstructionNavColumns.USERADDRESS,useraddress);
//		contentValues.put(InstructionNavColumns.MESSAGE_STATE,"new");
//		long id=sqliteDatabase.insert(InstructionNavColumns.TABLE_NAME, null, contentValues) ;
		return sqliteDatabase.update(InstructionNavColumns.TABLE_NAME,contentValues,InstructionNavColumns.NAV_LINE_ID+"=?",new String[]{lineId});
	}

//	public void setReaded(){
//		ContentValues contentValues=new ContentValues();
//		contentValues.put(InstructionNavColumns.MESSAGE_STATE,"old");
//		sqliteDatabase.update(InstructionNavColumns.TABLE_NAME,contentValues,InstructionNavColumns.MESSAGE_STATE+"=?",new String[]{"new"});
//	}

//	public int getNewMessageNumber(){
//		Cursor cursor = sqliteDatabase.query(InstructionNavColumns.MESSAGE_STATE,new String[]{InstructionNavColumns.NAV_LINE_ID},
//				InstructionNavColumns.MESSAGE_STATE+"=?",new String[]{"new"},null,null,null);
//		if(cursor != null){
//			return cursor.getCount();
//		}
//		return 0;
//	}

	/**
	 * 删除
	 * @param rowId
	 * @return
	 */
	public boolean delete(long rowId){
		boolean istrue=sqliteDatabase.delete(InstructionNavColumns.TABLE_NAME,KEY_ROWID +"="+rowId,null)>0;
		return istrue;
	}
	
	public boolean delete(String lineId){
		boolean istrue=sqliteDatabase.delete(InstructionNavColumns.TABLE_NAME,InstructionNavColumns.NAV_LINE_ID +"='"+lineId+"'",null)>0;
		return istrue;
	}
	
	/**
	 * 全部删除
	 * @return
	 */
	public boolean delete(){
		boolean istrue=sqliteDatabase.delete(InstructionNavColumns.TABLE_NAME,null,null)>0;
		return istrue;
	}
	
	
	public BDInstructionNav get(long rowId) throws SQLException {
		//查询
		Cursor mCursor=sqliteDatabase.query(true,InstructionNavColumns.TABLE_NAME,
				new String[]{InstructionNavColumns._ID,InstructionNavColumns.NAV_LINE_ID ,InstructionNavColumns.KEY_CREATED_TIME ,InstructionNavColumns.TARGET_POINT,
				InstructionNavColumns.PASS_POINT,InstructionNavColumns.EVADE_POINT,InstructionNavColumns.USERADDRESS},KEY_ROWID + "=" + rowId, null, null,null,null,null,null);
		BDInstructionNav nav=new BDInstructionNav();
		if(mCursor.moveToNext()){
			  nav.setRowId(mCursor.getLong(mCursor.getColumnIndex(InstructionNavColumns._ID)));
			  nav.setLineId(mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.NAV_LINE_ID)));
			  nav.setCreateTime(mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.KEY_CREATED_TIME)));
			  BDPoint targetPoint=new BDPoint();
			  String targetPointStr=mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.TARGET_POINT));
              nav.userAddress = mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.USERADDRESS));
			if(!TextUtils.isEmpty(nav.userAddress)) {
				try {
					String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?";
					Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID}, selection, new String[]{nav.userAddress}, null);
					if (cursor != null) {
						if (cursor.getCount() > 0) {
							Cursor nameCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME},
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))}, null);

							if (nameCursor != null) {
								if (nameCursor.getCount() > 0) {
									nav.userName = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

								}
								nameCursor.close();
							}

						}
						cursor.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			  if(targetPointStr!=null&&!"".equals(targetPointStr)){
				  String temp[] =targetPointStr.split(",");
				  targetPoint.setLon(Utils.lonStr2Double(temp[0]));
				  targetPoint.setLonDirection(Utils.getLonDirection(temp[0]));
				  targetPoint.setLat(Utils.latStr2Double(temp[1]));
				  targetPoint.setLatDirection(Utils.getLatDirection(temp[1]));
			  }
			  nav.setTargetPoint(targetPoint);
			  String passPointStr=mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.PASS_POINT));
			  if(passPointStr!=null&&!"".equals(passPointStr)){
				  String temp[]=passPointStr.split(",");
				  List<BDPoint> passPoints=new ArrayList<BDPoint>();
				  for(int i=0;i<temp.length/2;i++){
					  BDPoint mBDPoint=new BDPoint();
					  mBDPoint.setLon(Utils.lonStr2Double(temp[i*2]));
					  mBDPoint.setLonDirection(Utils.getLonDirection(temp[i*2]));
					  mBDPoint.setLat(Utils.latStr2Double(temp[i*2+1]));
					  mBDPoint.setLatDirection(Utils.getLatDirection(temp[i*2+1]));
					  passPoints.add(mBDPoint);
				  }
				  nav.setPassPoints(passPoints);
			  }
			  String evadePointStr=mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.EVADE_POINT));
			  if(evadePointStr!=null&&!"".equals(evadePointStr)){
				  String temp[]=evadePointStr.split(",");
				  List<BDPoint> evadePoints=new ArrayList<BDPoint>();
				  for(int i=0;i<temp.length/2;i++){
					  BDPoint mBDPoint=new BDPoint();
					  mBDPoint.setLon(Utils.lonStr2Double(temp[i*2]));
					  mBDPoint.setLonDirection(Utils.getLonDirection(temp[i*2]));
					  mBDPoint.setLat(Utils.latStr2Double(temp[i*2+1]));
					  mBDPoint.setLatDirection(Utils.getLatDirection(temp[i*2+1]));
					  evadePoints.add(mBDPoint);
				  }
				  nav.setEvadePoints(evadePoints);
			  }
		}
		mCursor.close();
		return nav;
	}
	
	public List<BDInstructionNav> getAll() throws SQLException {
		Cursor mCursor=sqliteDatabase.query(true,
				InstructionNavColumns.TABLE_NAME,
				new String[]{
						InstructionNavColumns._ID,
						InstructionNavColumns.NAV_LINE_ID ,
						InstructionNavColumns.KEY_CREATED_TIME ,
						InstructionNavColumns.TARGET_POINT,
						InstructionNavColumns.PASS_POINT,
						InstructionNavColumns.EVADE_POINT},
						null, null, null, null, null, null);
				//null, null,null, null,null,InstructionNavColumns._ID +" desc",null);
		
		List<BDInstructionNav> mInstructionNavs=new ArrayList<BDInstructionNav>();
		
		while(mCursor.moveToNext()){
			  BDInstructionNav nav=new BDInstructionNav(); 
			  nav.setRowId(mCursor.getLong(mCursor.getColumnIndex(InstructionNavColumns._ID)));
			  nav.setLineId(mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.NAV_LINE_ID)));
			  nav.setCreateTime(mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.KEY_CREATED_TIME)));
			  BDPoint targetPoint=new BDPoint();
			  String targetPointStr=mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.TARGET_POINT));
			  if(targetPointStr!=null&&!"".equals(targetPointStr)){
				  String temp[] =targetPointStr.split(",");
				  targetPoint.setLon(Utils.lonStr2Double(temp[0]));
				  targetPoint.setLonDirection(Utils.getLonDirection(temp[0]));
				  targetPoint.setLat(Utils.latStr2Double(temp[1]));
				  targetPoint.setLatDirection(Utils.getLatDirection(temp[1]));
			  }
			  nav.setTargetPoint(targetPoint);
			  String passPointStr=mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.PASS_POINT));
			  if(passPointStr!=null&&!"".equals(passPointStr)){
				  String temp[]=passPointStr.split(",");
				  List<BDPoint> passPoints=new ArrayList<BDPoint>();
				  for(int i=0;i<temp.length/2;i++){
					  BDPoint mBDPoint=new BDPoint();
					  mBDPoint.setLon(Utils.lonStr2Double(temp[i*2]));
					  mBDPoint.setLonDirection(Utils.getLonDirection(temp[i*2]));
					  mBDPoint.setLat(Utils.latStr2Double(temp[i*2+1]));
					  mBDPoint.setLatDirection(Utils.getLatDirection(temp[i*2+1]));
					  passPoints.add(mBDPoint);
				  }
				  nav.setPassPoints(passPoints);
			  }
			  String evadePointStr=mCursor.getString(mCursor.getColumnIndex(InstructionNavColumns.EVADE_POINT));
			  if(evadePointStr!=null&&!"".equals(evadePointStr)){
				  String temp[]=evadePointStr.split(",");
				  List<BDPoint> evadePoints=new ArrayList<BDPoint>();
				  for(int i=0;i<temp.length/2;i++){
					  BDPoint mBDPoint=new BDPoint();
					  mBDPoint.setLon(Utils.lonStr2Double(temp[i*2]));
					  mBDPoint.setLonDirection(Utils.getLonDirection(temp[i*2]));
					  mBDPoint.setLat(Utils.latStr2Double(temp[i*2+1]));
					  mBDPoint.setLatDirection(Utils.getLatDirection(temp[i*2+1]));
					  evadePoints.add(mBDPoint);
				  }
				  nav.setEvadePoints(evadePoints);
			  }
			  mInstructionNavs.add(nav);
		}
		mCursor.close();
		return mInstructionNavs;
	}
	
	
	public int getSize() throws SQLException {
		//查询
		Cursor mCursor=sqliteDatabase.query(true,InstructionNavColumns.TABLE_NAME,
				new String[]{InstructionNavColumns._ID,InstructionNavColumns.NAV_LINE_ID ,InstructionNavColumns.KEY_CREATED_TIME ,InstructionNavColumns.TARGET_POINT,
				InstructionNavColumns.PASS_POINT,InstructionNavColumns.EVADE_POINT},null, null, null,null,null,null,null);
		return mCursor.getCount();
	}

	
	public void close(){
		databaseHelper.close();
		sqliteDatabase.close();
	}
}
