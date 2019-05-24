package thd.bd.sms.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DataBaseHelper extends SQLiteOpenHelper {

	/**
	 * 数据库版本号
	 */
	private static final int DATABASE_VERSION=1;
	/**
	 * 数据库名称
	 */
	private static final String DATABASE_NAME="novsky_bd_one_tool.sql";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	
	/**
	 * 删除表
	 */
	private static final String SQL_DEL_MSG_TABLE="DROP TABLE IF EXISTS "+ BDMessageColumns.TABLE_NAME;
	/**
	 * 北斗短信字段类
	 * @author steve
	 */
	public static abstract class BDMessageColumns implements BaseColumns {
		public static final String TABLE_NAME="DB_MSG_TB";
		public static final String COLUMNS_USER_ADDRESS="USER_ADDRESS"; //用户地址
		public static final String COLUMNS_MSG_TYPE="MSG_TYPE";//消息类别
		public static final String COLUMNS_USER_NAME="USER_NAME";//用户名称
		public static final String COLUMNS_SEND_TIME="SEND_TIME"; //发送时间
		public static final String COLUMNS_MSG_LEN="MSG_LEN";//电文长度
		public static final String COLUMNS_MSG_CONTENT="MSG_CONTENT";//电文内容
		public static final String COLUMNS_CRC="CRC";
		public static final String COLUMNS_FLAG="MSG_FLAG"; //短信标识  0-收件箱  1-发件箱  2-草稿箱  3-表示未读  4-表示SOS  5-位置报告
	}
	
	/**
	 * 创建短信表SQL
	 */
	private static final String SQL_CREATE_MSG_TABLE="CREATE TABLE "+ BDMessageColumns.TABLE_NAME+" ("+ BDMessageColumns._ID+"  INTEGER PRIMARY KEY,"
			+ BDMessageColumns.COLUMNS_USER_ADDRESS+TEXT_TYPE+COMMA_SEP
			+ BDMessageColumns.COLUMNS_MSG_TYPE+TEXT_TYPE+COMMA_SEP
			+ BDMessageColumns.COLUMNS_USER_NAME+TEXT_TYPE+COMMA_SEP
			+ BDMessageColumns.COLUMNS_SEND_TIME+TEXT_TYPE+COMMA_SEP
			+ BDMessageColumns.COLUMNS_MSG_LEN+TEXT_TYPE+COMMA_SEP
			+ BDMessageColumns.COLUMNS_MSG_CONTENT+TEXT_TYPE+COMMA_SEP
			+ BDMessageColumns.COLUMNS_CRC+TEXT_TYPE+COMMA_SEP
			+ BDMessageColumns.COLUMNS_FLAG+TEXT_TYPE+" );";

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		 db.execSQL(SQL_CREATE_MSG_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 db.execSQL(SQL_DEL_MSG_TABLE);
	}

}
