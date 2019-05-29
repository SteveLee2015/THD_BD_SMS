package thd.bd.sms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import thd.bd.sms.base.DatabaseHelper.ReportSetColumns;

import thd.bd.sms.base.DatabaseHelper;
import thd.bd.sms.bean.ReportSet;

/**
 * 报告设置数据库管理类
 * @author llg
 */
public class ReportSetDatabaseOperation {
     
	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CREATED = "created";
	
	private DatabaseHelper databaseHelper;
	private Context context;
	private SQLiteDatabase sqliteWriteDatabase;
	private SQLiteDatabase sqliteReadDatabase;

	public ReportSetDatabaseOperation(Context mContext){
		this.context=mContext;
		databaseHelper=new DatabaseHelper(context);
		sqliteWriteDatabase = databaseHelper.getWritableDatabase();
		sqliteReadDatabase=databaseHelper.getReadableDatabase();
	}
	
	/**
	 * 增加定位设置
	 * @param
	 * @return
	 */
	public boolean insert(ReportSet reportSet){
		ContentValues contentValues=new ContentValues();
		contentValues.put(ReportSetColumns.REPORT_TYPE,reportSet.getReportType());
		contentValues.put(ReportSetColumns.REPORT_NUM, reportSet.getReportNnm());
		contentValues.put(ReportSetColumns.REPORT_FEQ,reportSet.getReportHz());
		contentValues.put(ReportSetColumns.TIANXI_VALUE,reportSet.getTianxianValue());
		contentValues.put(ReportSetColumns.STATUS_CODE,reportSet.getStatusCode());
		long id=sqliteWriteDatabase.insert(ReportSetColumns.TABLE_NAME, null, contentValues) ;
		return id>0;
	}

	/**
	 * 删除
	 * @param rowId
	 * @return
	 */
	public boolean delete(long rowId){
		boolean istrue=sqliteWriteDatabase.delete(ReportSetColumns.TABLE_NAME,KEY_ROWID +"="+rowId,null)>0;
		return istrue;
	}
	/**
	 * 按类别删除
	 * @param type
	 * @return
	 */
	public boolean deleteByType(String type){
		boolean istrue=sqliteWriteDatabase.delete(ReportSetColumns.TABLE_NAME,ReportSetColumns.REPORT_TYPE +"="+type,null)>0;
		return istrue;
	}
	/**
	 * 删除
	 * @param
	 * @return
	 */
	public boolean delete(){
		boolean istrue=sqliteWriteDatabase.delete(ReportSetColumns.TABLE_NAME,null,null)>0;
		return istrue;
	}
	
	public Cursor get(long rowId) throws SQLException {
		
		Cursor mCursor=sqliteReadDatabase.query(true,ReportSetColumns.TABLE_NAME,
				new String[]{ReportSetColumns.REPORT_NUM},KEY_ROWID + "=" + rowId, null, null,null,null,null);
		return mCursor;
	}
	/**
	 * 按类别查询
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public Cursor getWithType(String type) throws SQLException {
		Cursor mCursor=sqliteReadDatabase.query(
				true,
				ReportSetColumns.TABLE_NAME,
				new String[]{ReportSetColumns.REPORT_NUM},"REPORT_TYPE = " + type,
				null, null,null,null,null);
		return mCursor;
	}
	/**
	 * 得到数据
	 * @return
	 * @throws SQLException
	 */
	public ReportSet getFirst() throws SQLException {
		//查询
		Cursor mCursor=sqliteReadDatabase.query(true,ReportSetColumns.TABLE_NAME,
				new String[]{
				 ReportSetColumns._ID,
				 ReportSetColumns.REPORT_TYPE,
				 ReportSetColumns.REPORT_NUM,
				 ReportSetColumns.REPORT_FEQ,
				 ReportSetColumns.TIANXI_VALUE,
				 ReportSetColumns.STATUS_CODE
				},null, null, null,null,null,null);
		boolean istrue=mCursor.moveToFirst();
		if(istrue){
			ReportSet set=new ReportSet();
			set.setId(mCursor.getInt(mCursor.getColumnIndex(ReportSetColumns._ID)));
			set.setReportType(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.REPORT_TYPE)));
			set.setReportNnm(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.REPORT_NUM)));
			set.setReportHz(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.REPORT_FEQ)));
			set.setTianxianValue(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.TIANXI_VALUE)));
			set.setStatusCode(mCursor.getInt(mCursor.getColumnIndex(ReportSetColumns.STATUS_CODE)));
			mCursor.close();
			return set;
		}else{
			return null;
		}
	}
	
	/**
	 * 得到数据
	 * @return
	 * @throws SQLException
	 */
	public ReportSet getByType(String type) throws SQLException {
		//查询
		Cursor mCursor=sqliteReadDatabase.query(
				true,
				ReportSetColumns.TABLE_NAME,
				new String[]{ReportSetColumns._ID,ReportSetColumns.REPORT_TYPE,ReportSetColumns.REPORT_NUM,
				ReportSetColumns.REPORT_FEQ,ReportSetColumns.TIANXI_VALUE,ReportSetColumns.STATUS_CODE},
				"REPORT_TYPE =?", new String[]{type}, null,null,null,null);
		boolean istrue=mCursor.moveToFirst();
		if(istrue){
			ReportSet set=new ReportSet();
			set.setId(mCursor.getInt(mCursor.getColumnIndex(ReportSetColumns._ID)));
			set.setReportType(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.REPORT_TYPE)));
			set.setReportNnm(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.REPORT_NUM)));
			set.setReportHz(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.REPORT_FEQ)));
			set.setTianxianValue(mCursor.getString(mCursor.getColumnIndex(ReportSetColumns.TIANXI_VALUE)));
			set.setStatusCode(mCursor.getInt(mCursor.getColumnIndex(ReportSetColumns.STATUS_CODE)));
			mCursor.close();
			return set;
		}else{
			return null;
		}
		
	}
	public int getSize() throws SQLException {
		//查询
		Cursor mCursor=sqliteReadDatabase.query(true,ReportSetColumns.TABLE_NAME,
				new String[]{ReportSetColumns.REPORT_TYPE,ReportSetColumns.REPORT_NUM,
				 ReportSetColumns.REPORT_TYPE
				,ReportSetColumns.TIANXI_VALUE},null, null, null,null,null,null);
		
		return mCursor.getCount();
	}
	/**
	 * 更新
	 * @param set
	 * @return
	 */
	public boolean update(ReportSet set){
		ContentValues contentValues=new ContentValues();
		contentValues.put(ReportSetColumns.REPORT_TYPE,set.getReportType());
		contentValues.put(ReportSetColumns.REPORT_NUM, set.getReportNnm());
		contentValues.put(ReportSetColumns.REPORT_FEQ,set.getReportHz());
		contentValues.put(ReportSetColumns.TIANXI_VALUE,set.getTianxianValue());
		contentValues.put(ReportSetColumns.STATUS_CODE,set.getStatusCode());
		int index=sqliteWriteDatabase.update(ReportSetColumns.TABLE_NAME, contentValues, "REPORT_TYPE=?", new String[]{String.valueOf(set.getReportType())});
		return index>0;
	}
	
	public void close(){
		databaseHelper.close();
		if(sqliteWriteDatabase!=null){
		   sqliteWriteDatabase.close();
		}
		if(sqliteReadDatabase!=null){
			sqliteReadDatabase.close();
		}
	}
}
