package thd.bd.sms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import thd.bd.sms.base.DatabaseHelper.StateCodeColumns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thd.bd.sms.base.DatabaseHelper;
import thd.bd.sms.bean.StateCode;

public class StateCodeOperation {

	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CREATED = "created";

	private DatabaseHelper databaseHelper;
	private Context context;
	private SQLiteDatabase sqliteDatabase;

	public StateCodeOperation(Context mContext) {
		this.context = mContext;
		databaseHelper=new DatabaseHelper(context);
		sqliteDatabase = databaseHelper.getWritableDatabase();
	}

	/**
	 * 增加友邻位置
	 * @param
	 * @return
	 */
	public long insert(String wordText){
		ContentValues contentValues=new ContentValues();
		contentValues.put(StateCodeColumns.COLUMNS_STATUS_WORD,wordText);
		long id=sqliteDatabase.insert(StateCodeColumns.TABLE_NAME, null, contentValues) ;
		return id;
	}

	public long insert(StateCode mStateCode){
		ContentValues contentValues=new ContentValues();
		contentValues.put(StateCodeColumns.COLUMNS_WORD_ORDER,mStateCode.getMsgCongentOrder());
		contentValues.put(StateCodeColumns.COLUMNS_STATUS_WORD,mStateCode.getMsgContent());
		long id=sqliteDatabase.insert(StateCodeColumns.TABLE_NAME, null, contentValues) ;
		return id;
	}
	
	public boolean update(long rowId,String wordText){
		ContentValues contentValues=new ContentValues();
		contentValues.put(StateCodeColumns.COLUMNS_STATUS_WORD,wordText);
		long id=sqliteDatabase.update(StateCodeColumns.TABLE_NAME, contentValues, StateCodeColumns._ID+"="+rowId,null);
		return id>0;
	}

	public boolean update(StateCode mStateCode){
		ContentValues contentValues=new ContentValues();
		contentValues.put(StateCodeColumns.COLUMNS_WORD_ORDER ,mStateCode.getMsgCongentOrder());
		contentValues.put(StateCodeColumns.COLUMNS_STATUS_WORD,mStateCode.getMsgContent());
		long id=sqliteDatabase.update(StateCodeColumns.TABLE_NAME, contentValues, StateCodeColumns._ID+"="+mStateCode.getRowId(),null);
		return id>0;
	}

	public List<Map<String, Object>> getAll() throws SQLException {
		String orderBy = StateCodeColumns._ID + " desc";
		// 查询
		Cursor mCursor = sqliteDatabase
				.query(true, StateCodeColumns.TABLE_NAME, new String[] {
						StateCodeColumns._ID, StateCodeColumns.COLUMNS_WORD_ORDER ,StateCodeColumns.COLUMNS_STATUS_WORD},
						null, null, null, null, null, null);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int index=0;
		while (mCursor.moveToNext()) {
			    index++;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("MESSAGE_WORD_ID", mCursor.getString(mCursor.getColumnIndex(StateCodeColumns._ID)));
				map.put("MESSAGE_WORD_TEXT_ORDER", mCursor.getString(mCursor.getColumnIndex(StateCodeColumns.COLUMNS_WORD_ORDER)));
				map.put("MESSAGE_WORD_TEXT", mCursor.getString(mCursor.getColumnIndex(StateCodeColumns.COLUMNS_STATUS_WORD)));
				map.put("MESSAGE_WORD_CHECKED",false);
				map.put("MESSAGE_NUM", index);
				list.add(map);
		}
		mCursor.close();
		return list;
	}
	
	public String[] getAllMessagesArray() throws SQLException {
		String orderBy = StateCodeColumns._ID + " desc";
		// 查询
		Cursor mCursor = sqliteDatabase
				.query(true, StateCodeColumns.TABLE_NAME, new String[] {
						StateCodeColumns._ID, StateCodeColumns.COLUMNS_WORD_ORDER ,StateCodeColumns.COLUMNS_STATUS_WORD},
						null, null, null, null, orderBy, null);
		String[] array=new String[mCursor.getCount()];
		int index=0;
		while (mCursor.moveToNext()) {
				array[index]=mCursor.getString(mCursor.getColumnIndex(StateCodeColumns.COLUMNS_STATUS_WORD));
				index++;
		}
		mCursor.close();
		return array;
	}

	public Cursor getMessageWordById(long rowId) throws SQLException {
		// 查询
		Cursor mCursor = sqliteDatabase
				.query(true, StateCodeColumns.TABLE_NAME, new String[] {
						StateCodeColumns._ID, StateCodeColumns.COLUMNS_WORD_ORDER ,StateCodeColumns.COLUMNS_STATUS_WORD},
						StateCodeColumns._ID + "=" + rowId, null, null, null,
						null, null);
		return mCursor;
	}

	/**
	 * 删除
	 * @param rowId
	 * @return
	 */
	public boolean delete(long rowId) {
		return sqliteDatabase.delete(StateCodeColumns.TABLE_NAME, KEY_ROWID + "="
				+ rowId, null) > 0;
	}
	/**
	 * 删除所有的
	 * @return
	 */
	public boolean deleteAll() {
		return sqliteDatabase.delete(StateCodeColumns.TABLE_NAME,null, null) > 0;
	}

	public void close() {
		if (databaseHelper != null) {
			databaseHelper.close();
		}
		if (sqliteDatabase != null) {
			sqliteDatabase.close();
		}
	}
}
