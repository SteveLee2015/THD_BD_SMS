package thd.bd.sms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thd.bd.sms.base.DatabaseHelper;
import thd.bd.sms.base.DatabaseHelper.FriendsLocationColumns;
import thd.bd.sms.bean.FriendBDPoint;
import thd.bd.sms.bean.FriendLocation;
import thd.bd.sms.bean.FriendsLocation;

/**
 * 友邻位置数据接口
 * @author llg052
 *
 */
public class FriendsLocationDatabaseOperation {
     
	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CREATED = "created";
	
	private DatabaseHelper databaseHelper;
	private Context context;
	private SQLiteDatabase sqliteDatabase;

	public FriendsLocationDatabaseOperation(Context mContext){
		this.context=mContext;
	}
	
	/**
	 * 增加友邻位置
	 * @param
	 * @return
	 */
	public boolean insert(FriendsLocation flag){
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase = databaseHelper.getWritableDatabase();
		
		String rawLon = flag.getLon();
		String[] splitLon = rawLon.split("\\.");

		/////////////有问题//////////////
		if (splitLon[0].length()>=5) {
			double rawlon = Double.parseDouble(rawLon);
			flag.setLon(rawlon/100+"");
		}
		String rawLat = flag.getLat();
		String[] splitLat = rawLat.split("\\.");
		if (splitLat[0].length()>=4) {
			double rawlat = Double.parseDouble(rawLat);
			flag.setLat(rawlat/100+"");
		}
		/////////////有问题//////////////

		//判断
		String latiFormat = String.format("%.6f", Double.parseDouble(flag.getLat()));
		String longiFormat = String.format("%.6f",Double.parseDouble(flag.getLon()));

		flag.setLat(latiFormat);
		flag.setLon(longiFormat);

		ContentValues contentValues=new ContentValues();
		contentValues.put(FriendsLocationColumns.FRIENDS_ID,flag.getUserId());
		contentValues.put(FriendsLocationColumns.REPORT_TIME, flag.getReportTime());
		contentValues.put(FriendsLocationColumns.FRIENDS_LON,flag.getLon());
		contentValues.put(FriendsLocationColumns.FRIENDS_LAT,flag.getLat());
		contentValues.put(FriendsLocationColumns.FRIENDS_HEIGHT,flag.getHeight());
		long id=sqliteDatabase.insert(FriendsLocationColumns.TABLE_NAME, null, contentValues) ;
		return id>0;
	}

	/**
	 * 删除
	 * @param address
	 * @return
	 */
	public boolean deleteAllGroupByAddress(String address){
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase = databaseHelper.getWritableDatabase();
		boolean istrue=sqliteDatabase.delete(FriendsLocationColumns.TABLE_NAME,FriendsLocationColumns.FRIENDS_ID +"="+address,null)>0;
		return istrue;
	}

	/**
	 * 删除
	 * @param rowId
	 * @return
	 */
	public boolean delete(long rowId){
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase = databaseHelper.getWritableDatabase();
		boolean istrue=sqliteDatabase.delete(FriendsLocationColumns.TABLE_NAME,KEY_ROWID +"="+rowId,null)>0;
		return istrue;
	}
	/**
	 * 删除
	 * @param
	 * @return
	 */
	public boolean delete(){
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase = databaseHelper.getWritableDatabase();
		boolean istrue=sqliteDatabase.delete(FriendsLocationColumns.TABLE_NAME,null,null)>0;
		return istrue;
	}
	
	public Cursor get(long rowId) throws SQLException {
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
		//查询
		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
				             FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
				             FriendsLocationColumns.FRIENDS_HEIGHT},KEY_ROWID + "=" + rowId, null, null,null,null,null);
		return mCursor;
	}
	/**
	 * 得到数据   获取同一个友邻发送多个数据时候只显示最新数据
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String ,Object>> getAllLastLocationList() throws SQLException {
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
		//查询
		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
	             FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
	             FriendsLocationColumns.FRIENDS_HEIGHT},null, null, null,null,FriendsLocationColumns._ID+" desc",null);
		List<Map<String ,Object>> list=new ArrayList<Map<String,Object>>();
		
		Map<String,String> userAddressMap=new HashMap<String,String>();
		while(mCursor.moveToNext()){
			String userAddress=mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_ID));
			if(!userAddressMap.containsKey(userAddress)){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("F_ID",mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns._ID)) );
				map.put("FRIEND_ID",userAddress);
				map.put("FRIEND_REPORT_TIME", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.REPORT_TIME)));
				map.put("FRIEND_LON", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LON)));
				map.put("FRIEND_LAT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LAT)));
				map.put("FRIEND_HEIGHT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_HEIGHT)));
				list.add(map); 
				userAddressMap.put(userAddress, "exists");
			}
	    }
		mCursor.close();
		return list;
	}


	public List<FriendBDPoint> getAllGroupById() throws SQLException {
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
		//查询
		Cursor mCursor = sqliteDatabase.rawQuery("select *,count(*) from BD_FRIEND_LOCATION group by FRIENDS_ID order by REPORT_TIME",null);

//		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
//				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
//						FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
//						FriendsLocationColumns.FRIENDS_HEIGHT},null, null, FriendsLocationColumns.FRIENDS_ID,null,FriendsLocationColumns.REPORT_TIME+" desc",null);

		List<FriendBDPoint> list = new ArrayList<FriendBDPoint>();
		int count = mCursor.getCount();
		for (int i = 0; i < count; i++) {

			if (mCursor.moveToNext()) {
				FriendBDPoint nav = new FriendBDPoint();
				nav.setFriendID(mCursor.getString(mCursor
						.getColumnIndex(FriendsLocationColumns.FRIENDS_ID)));

				nav.setReceiveTime(mCursor.getString(mCursor
						.getColumnIndex(FriendsLocationColumns.REPORT_TIME)));
				nav.setFriendCount(mCursor.getString(mCursor
						.getColumnIndex("count(*)")));

				list.add(nav);
			}
		}

		mCursor.close();
		return list;
	}

	/**
	 * 根据用户address获取所有数据
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String ,Object>> getAllByAddress(String address) throws SQLException {
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
		//查询
//		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
//				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
//						FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
//						FriendsLocationColumns.FRIENDS_HEIGHT},FriendsLocationColumns.FRIENDS_ID + "=" +address, null, null,null,FriendsLocationColumns._ID+" desc",null);

		Cursor mCursor = sqliteDatabase.rawQuery("select * from BD_FRIEND_LOCATION where FRIENDS_ID = "+"'"+address+"'",null);
		int count = mCursor.getCount();
		Log.e("FriendsOperation", "LERRY_YOULIN==================FriendsLocationDatabaseOperation213=======address=="+address+"====count=="+count);
		List<Map<String ,Object>> list=new ArrayList<Map<String,Object>>();

		Map<String,String> userAddressMap=new HashMap<String,String>();
		while(mCursor.moveToNext()){
			String userAddress=mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_ID));


			if(true){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("F_ID",mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns._ID)) );
				map.put("FRIEND_ID",userAddress);
				map.put("FRIEND_REPORT_TIME", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.REPORT_TIME)));
				map.put("FRIEND_LON", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LON)));
				map.put("FRIEND_LAT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LAT)));
				map.put("FRIEND_HEIGHT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_HEIGHT)));
				list.add(map);
				userAddressMap.put(userAddress, "exists");
			}
		}
		mCursor.close();
		return list;
	}

	/**
	 * 得到数据   所有的数据
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String ,Object>> getAllLocationList() throws SQLException {
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
		//查询
		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
				FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
				FriendsLocationColumns.FRIENDS_HEIGHT},null, null, null,null,FriendsLocationColumns._ID+" desc",null);
		List<Map<String ,Object>> list=new ArrayList<Map<String,Object>>();
		
		Map<String,String> userAddressMap=new HashMap<String,String>();
		while(mCursor.moveToNext()){
			String userAddress=mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_ID));
			if(true){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("F_ID",mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns._ID)) );
				map.put("FRIEND_ID",userAddress);
				map.put("FRIEND_REPORT_TIME", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.REPORT_TIME)));
				map.put("FRIEND_LON", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LON)));
				map.put("FRIEND_LAT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LAT)));
				map.put("FRIEND_HEIGHT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_HEIGHT)));
				list.add(map); 
				userAddressMap.put(userAddress, "exists");
			}
		}
		mCursor.close();
		return list;
	}
//	/**
//	 * 得到所有用户最新的一条数据
//	 * @return
//	 * @throws SQLException
//	 */
//	public List<Map<String ,Object>> getAllUserLastNewData() throws SQLException{
//		databaseHelper=new DatabaseHelper(context);
//		sqliteDatabase=databaseHelper.getReadableDatabase();
//		//查询
//		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
//				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
//	             FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
//	             FriendsLocationColumns.FRIENDS_HEIGHT}, null, null, null,null,null,null);
//		List<Map<String ,Object>> list=new ArrayList<Map<String,Object>>();
//		while(mCursor.moveToNext()){
//			Map<String,Object> map=new HashMap<String,Object>();
//			map.put("F_ID",mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns._ID)) );
//			map.put("FRIEND_ID", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_ID)));
//			map.put("FRIEND_REPORT_TIME", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.REPORT_TIME)));
//			map.put("FRIEND_LON", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LON)));
//			map.put("FRIEND_LAT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LAT)));
//			map.put("FRIEND_HEIGHT", mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_HEIGHT)));
//			list.add(map); 	
//		}
//		mCursor.close();
//		return list;
//	}
	public int getSize() throws SQLException {
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
		//查询
		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
	             FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
	             FriendsLocationColumns.FRIENDS_HEIGHT},null, null, null,null,null,null);
		
		return mCursor.getCount();
	}
	
	public FriendLocation getById(long rowId) throws SQLException {
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase=databaseHelper.getReadableDatabase();
		FriendLocation mFriendLocation=new FriendLocation();
		//查询
		Cursor mCursor=sqliteDatabase.query(true,FriendsLocationColumns.TABLE_NAME,
				new String[]{FriendsLocationColumns._ID,FriendsLocationColumns.FRIENDS_ID ,FriendsLocationColumns.REPORT_TIME,
				             FriendsLocationColumns.FRIENDS_LON,FriendsLocationColumns.FRIENDS_LAT,
				             FriendsLocationColumns.FRIENDS_HEIGHT},KEY_ROWID + "=" + rowId, null, null,null,null,null);
		if(mCursor.moveToNext()){
			mFriendLocation.setId(rowId);
			mFriendLocation.setAddress(mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_ID)));
			mFriendLocation.setFriendsLon(mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LON)) );
			mFriendLocation.setFriendsLat(mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_LAT)) );
			mFriendLocation.setFriendsHeight(mCursor.getString(mCursor.getColumnIndex(FriendsLocationColumns.FRIENDS_HEIGHT)) );
		}
		return mFriendLocation;
	}
	
	public void close(){
		databaseHelper.close();
		sqliteDatabase.close();
	}
}
