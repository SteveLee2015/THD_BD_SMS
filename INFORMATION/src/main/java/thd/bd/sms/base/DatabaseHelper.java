package thd.bd.sms.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static thd.bd.sms.base.DatabaseHelper.ReportSetColumns.STATUS_CODE;

/**
 * SQLite操作类
 * @author llg
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="bd_one_tool.sql";

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	
	/**
	 * 短报文表
	 * @author llg052
	 *
	 */
	public static abstract class CustomColumns implements BaseColumns {
		public static final String TABLE_NAME="DB_MSG_TB";
		public static final String COLUMNS_USER_ADDRESS="USER_ADDRESS"; //用户地址
		public static final String COLUMNS_MSG_TYPE="MSG_TYPE";//消息类别
		public static final String COLUMNS_SEND_ADDRESS="SEND_ADDRESS";//发送地址
		public static final String COLUMNS_SEND_TIME="SEND_TIME"; //发送时间
		public static final String COLUMNS_MSG_LEN="MSG_LEN";//电文长度
		public static final String COLUMNS_MSG_CONTENT="MSG_CONTENT";//电文长度
		public static final String COLUMNS_CRC="CRC";
		public static final String COLUMNS_FLAG="MSG_FLAG"; //短信标识  0-收件箱  1-发件箱  2-草稿箱  3-表示未读
		
	}
	
	public static abstract class  AdvancedSetColumns implements BaseColumns {
		public static final String TABLE_NAME="DB_ADVANCED_SET";
		public static final String COLUMNS_PWD="ADVANCED_PWD"; //密码
	}
	
	/*
	 * 定位设置*/
	public static abstract class LocationSetColumns implements BaseColumns {
		public static final String TABLE_NAME="BD_LOCATION_SET";//表名
		public static final String LOCATION_STATUS="LOCATION_STATUS"; //状态 使用中 or 未使用
		public static final String LOCATION_TYPE="LOCATION_TYPE"; //定位类型 单次 or 连续
		public static final String LOCATION_FEQ="LOCATION_FEQ"; //定位频度
		public static final String HEIGHT_TYPE="HEIGHT_TYPE";//高类型
		public static final String HEIGHT_VALUE="HEIGHT_VALUE"; //高程
		public static final String TIANXI_VALUE="TIANXIAN_VALUE";//天线高

		public static final String LOCATIONSET_STATUS_USING = "LOCATIONSET_STATUS_USING";//使用中ing
		public static final String LOCATIONSET_STATUS_NOT_USING = "LOCATIONSET_STATUS_NOT_USING";//未使用中
	}
	/*
	 * 报告设置*/
	public static abstract class ReportSetColumns implements BaseColumns {
		public static final String TABLE_NAME="BD_REPORT_SET";//表名
		public static String REPORT_TYPE="REPORT_TYPE";//报告类型
		public static String REPORT_NUM="REPORT_NUM"; //报告号码
		public static final String REPORT_FEQ="REPORT_FEQ"; //报告频度
		public static final String TIANXI_VALUE="TIANXIAN_VALUE";//天线高
		public static final String STATUS_CODE="STATUS_CODE";//状态码
	}
	/*
	 * 路标
	 */
	public static abstract class RoadFlagColumns implements BaseColumns {
		public static final String TABLE_NAME="BD_ROAD_FLAG";
		public static final String ROAD_NAME="ROAD_NAME";//路标名称
		public static final String ROAD_LON="ROAD_LON"; //路标经度
		public static final String ROAD_LAT="ROAD_LAT"; //路标纬度
		public static final String ROAD_HEIGH="ROAD_HEIGH";//路标高程
		public static final String ROAD_NOTE="ROAD_NOTE"; //路标注释
	}

	/**
	 * 友邻位置
	 */
	public static abstract class FriendsLocationColumns implements BaseColumns {
		public static final String TABLE_NAME="BD_FRIEND_LOCATION";
		public static final String FRIENDS_ID="FRIENDS_ID";//友邻位置
		public static final String REPORT_TIME="REPORT_TIME";//报告时间
		public static final String FRIENDS_LON="FRIENDS_LON";//经度
		public static final String FRIENDS_LAT="FRIENDS_LAT";//纬度
		public static final String FRIENDS_HEIGHT="FRIENDS_HEIGHT";//高程
	}
	/**
	 * 缓存rd数据列表
	 * @author llg052
	 *
	 */
	public static abstract class RDCacheColumns implements BaseColumns {
		public static final String TABLE_NAME = "BD_RD_CACHE";
		public static final String COLUMNS_SEND_ADDRESS="SEND_ADDRESS";//发送地址
		public static final String COLUMNS_MSG_TYPE="MSG_TYPE";//消息类别   短报文/位置报告/定位申请
		public static final String COLUMNS_PRIORITY="PRIORITY";//发送优先级
		//public static final String COLUMNS_TAG="TAG";//是否已经发送标示
		public static final String COLUMNS_MSG_CONTENT="MSG_CONTENT";//消息内容 短报文-是为短报文 /位置报告-位置报告/定位申请-定位申请
		public static final String COLUMNS_WORD = "RD_CACHE"; //缓存rd数据  经过build之后的byte[]
	}
	/**
	 * 状态列表
	 * @author llg052
	 *
	 */
	public static abstract class StateCodeColumns implements BaseColumns {
		public static final String TABLE_NAME = "DB_STATE_CODE";
		public static final String COLUMNS_WORD_ORDER = "STATE_CODE_ORDER"; //状态代码_序号
		public static final String COLUMNS_STATUS_WORD = "STATE_CODE"; //状态代码
	}
	/**
	 * 常用短语
	 * @author llg052
	 *
	 */
	public static abstract class MsgWordColumns implements BaseColumns {
		public static final String TABLE_NAME = "DB_MESSAGE_WORD";
		public static final String COLUMNS_WORD = "MESSAGE_WORD"; //常用短语
	}
	/*
	 * 指令导航*/
	public static abstract class InstructionNavColumns implements BaseColumns {
		public static final String TABLE_NAME="BD_INSTRUCTION_NAV";//表名
		public static final String NAV_LINE_ID="NAV_LINE_ID"; //导航路线ID
		public static final String KEY_CREATED_TIME="KEY_CREATED_TIME"; //创建时间
		public static final String TARGET_POINT="TARGET_POINT";//目的地
		public static final String PASS_POINT="PASS_POINT"; //途径点
		public static final String EVADE_POINT="EVADE_POINT";//规避点
		public static final String USERADDRESS = "USER_ADDRESS";//指令发送者北斗卡号
//		public static final String  MESSAGE_STATE="MESSAGE_STATE";//规避点
	}

	/*
	 * 路线导航*/
	public static abstract class LineNavColumns implements BaseColumns {
		public static final String TABLE_NAME="BD_LINE_NAV";//表名
		public static final String NAV_LINE_ID="NAV_LINE_ID"; //导航路线ID
		public static final String CREATED_TIME="CREATED_TIME"; //创建时间
		public static final String CURRENT_INDEX="CURRENT_INDEX"; //当前条数
		public static final String TOTAL_NUMBER="TOTAL_NUMBER"; //总条数
		public static final String PASS_POINT="PASS_POINT"; //途径点
	}
	
	
	/*
	 * 友邻位置*/
	public static abstract class FriendLocationNavColumns implements BaseColumns {
		public static final String TABLE_NAME="BD_FRIEND_LOCATION_NAV";//表名
		public static final String RECEIVE_TIME="RECEIVE_TIME"; //接收时间
		public static final String FRIEND_COUNT="FRIEND_COUNT"; //友邻总数
		public static final String FRIEND_CURRENT_ID="FRIEND_CURRENT_ID"; //当前序号
		public static final String FRIEND_ID="FRIEND_ID"; //友邻id
		public static final String FRIEND_LON="FRIEND_LON";//友邻位置经度
		public static final String FRIEND_LON_DIR="FRIEND_LON_DIR";//友邻位置经度方向
		public static final String FRIEND_LAT="FRIEND_LAT";//友邻位置纬度
		public static final String FRIEND_LAT_DIR="FRIEND_LAT_DIR";//友邻位置纬度方向
	}

	/**
	 * 用户授权表
	 * @author steve
	 */
	public static abstract class AuthenticationColumns implements BaseColumns {
		public static final String TABLE_NAME = "BD_USER_AUTHENTICATION_TABLE";
		public static final String USER_DEVICE_NUMBER= "USER_DEVICE_NUMBER";// 用户设备编号
	}
	
	private static final String SQL_CREATE_USER_AUTHENTICATION_TABLE="CREATE TABLE "
	        + AuthenticationColumns.TABLE_NAME
	        +" ("+ AuthenticationColumns._ID
	        + "  INTEGER PRIMARY KEY,"
	        + AuthenticationColumns.USER_DEVICE_NUMBER
	        +TEXT_TYPE+" )";
	        
	private static final String SQL_DEL_USER_AUTHENTICATION_TABLE = "DROP TABLE IF EXISTS "
			+ AuthenticationColumns.TABLE_NAME;
	
	
	/**
	 * 作战时间
	 * @author steve
	 */
	public static abstract class ZuoZhanTimeColumns implements BaseColumns {
		
		public static final String TABLE_NAME="BD_ZUOZHAN_TIME";//表名
		
		public static final String ZUO_ZHAN_TIME="ZUO_ZHAN_TIME"; //作战时间
		
		public static final String BJ_TIME="BJ_TIME"; //北京时间
		
		public static final String ZUO_ZHAN_STEP="ZUO_ZHAN_STEP"; //作战时间步长
		
	}
	
	
	/**
	 * 创建作战时间数据表
	 */
	private static final String SQL_CREATE_ZUO_ZHAN_TIME_TABLE="CREATE TABLE "+ ZuoZhanTimeColumns.TABLE_NAME+" ("+ ZuoZhanTimeColumns._ID+"  INTEGER PRIMARY KEY,"+
			ZuoZhanTimeColumns.ZUO_ZHAN_TIME+TEXT_TYPE+COMMA_SEP+
			ZuoZhanTimeColumns.ZUO_ZHAN_STEP+TEXT_TYPE+COMMA_SEP+
			ZuoZhanTimeColumns.BJ_TIME+TEXT_TYPE+")";

	/**
	 * 删除作战时间数据表
	 */
	private static final String SQL_DEL_ZUO_ZHAN_TABLE="DROP TABLE IF EXISTS "+ ZuoZhanTimeColumns.TABLE_NAME;
	
	/**
	 * 创建路线导航数据表
	 */
	private static final String SQL_CREATE_LINE_NAV_TABLE="CREATE TABLE "+ LineNavColumns.TABLE_NAME+" ("+ LineNavColumns._ID+"  INTEGER PRIMARY KEY,"+
			LineNavColumns.NAV_LINE_ID+TEXT_TYPE+" UNIQUE "+COMMA_SEP+
			LineNavColumns.CREATED_TIME+TEXT_TYPE+COMMA_SEP+
			LineNavColumns.CURRENT_INDEX+TEXT_TYPE+COMMA_SEP+
			LineNavColumns.TOTAL_NUMBER+TEXT_TYPE+COMMA_SEP+
			LineNavColumns.PASS_POINT+TEXT_TYPE+")";

	/**
	 * 删除路线导航数据表
	 */
	private static final String SQL_DEL_LINE_NAV_TABLE="DROP TABLE IF EXISTS "+ LineNavColumns.TABLE_NAME;

	/**
	 * 创建指令导航数据表
	 */
	private static final String SQL_CREATE_ISTRUCTION_NAV_TABLE="CREATE TABLE "+ InstructionNavColumns.TABLE_NAME+" ("+ InstructionNavColumns._ID+"  INTEGER PRIMARY KEY,"+
			InstructionNavColumns.NAV_LINE_ID+TEXT_TYPE+" UNIQUE "+COMMA_SEP+
			InstructionNavColumns.KEY_CREATED_TIME+TEXT_TYPE+COMMA_SEP+
			InstructionNavColumns.TARGET_POINT+TEXT_TYPE+COMMA_SEP+
			InstructionNavColumns.PASS_POINT+TEXT_TYPE+COMMA_SEP+
			InstructionNavColumns.EVADE_POINT+TEXT_TYPE+COMMA_SEP+
	        InstructionNavColumns.USERADDRESS + TEXT_TYPE
//			+InstructionNavColumns.MESSAGE_STATE+TEXT_TYPE+COMMA_SEP
			+" )";

	/**
	 * 删除指令导航数据表
	 */
	private static final String SQL_DEL_ISTRUCTION_NAV_TABLE="DROP TABLE IF EXISTS "+ InstructionNavColumns.TABLE_NAME;

	
//	private static final String  SQL_CREATE_CONTACT_TABLE="CREATE TABLE "+BDContactColumn.TABLE_NAME+" ("+BDContactColumn._ID+"  INTEGER PRIMARY KEY,"+
//			BDContactColumn.USER_NAME+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.CARD_NUM+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.CARD_LEVEL+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.CARD_SERIAL_NUM+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.CARD_FREQUENCY+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.USER_ADDRESS+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.PHONE_NUMBER+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.USER_EMAIL+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.CHECK_CURRENT_NUM+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.FIRST_LETTER_INDEX+TEXT_TYPE+COMMA_SEP+
//			BDContactColumn.REMARK+TEXT_TYPE+" )";
	
	
	/**
	 * 创建友邻位置数据表
	 */
	private static final String SQL_CREATE_FRIEND_LOCATION_NAV_TABLE="CREATE TABLE "+ FriendLocationNavColumns.TABLE_NAME+" ("+ FriendLocationNavColumns._ID+"  INTEGER PRIMARY KEY,"+
			FriendLocationNavColumns.RECEIVE_TIME+TEXT_TYPE+COMMA_SEP+
			FriendLocationNavColumns.FRIEND_COUNT+TEXT_TYPE+COMMA_SEP+
			FriendLocationNavColumns.FRIEND_CURRENT_ID+TEXT_TYPE+COMMA_SEP+
			FriendLocationNavColumns.FRIEND_ID+TEXT_TYPE+COMMA_SEP+
			FriendLocationNavColumns.FRIEND_LON+TEXT_TYPE+COMMA_SEP+
			FriendLocationNavColumns.FRIEND_LON_DIR+TEXT_TYPE+COMMA_SEP+
			FriendLocationNavColumns.FRIEND_LAT+TEXT_TYPE+COMMA_SEP+
			FriendLocationNavColumns.FRIEND_LAT_DIR+TEXT_TYPE+" )";
	
	/**
	 * 删除友邻位置数据表
	 */
	private static final String SQL_DEL_FRIEND_LOCATION_NAV_TABLE="DROP TABLE IF EXISTS "+ FriendLocationNavColumns.TABLE_NAME;

	
	
//	private static final String SQL_DEL_CONTACT_TABLE="DROP TABLE IF EXISTS "+BDContactColumn.TABLE_NAME;

	
	private static final String SQL_CREATE_FRIEND_LOCATION_TABLE="CREATE TABLE "+ FriendsLocationColumns.TABLE_NAME+" ("+ FriendsLocationColumns._ID+"  INTEGER PRIMARY KEY,"+
			FriendsLocationColumns.FRIENDS_ID+TEXT_TYPE+COMMA_SEP+
			FriendsLocationColumns.REPORT_TIME+TEXT_TYPE+COMMA_SEP+
			FriendsLocationColumns.FRIENDS_LON+TEXT_TYPE+COMMA_SEP+
			FriendsLocationColumns.FRIENDS_LAT+TEXT_TYPE+COMMA_SEP+
			FriendsLocationColumns.FRIENDS_HEIGHT+TEXT_TYPE+" )";
	
	private static final String SQL_DEL_FRIEND_LOCATION_TABLE="DROP TABLE IF EXISTS "+ FriendsLocationColumns.TABLE_NAME;
	
	/**
	 * 创建状态代码表
	 */
	private static final String SQL_CREATE_STATE_CODE_TABLE="CREATE TABLE "
	        + StateCodeColumns.TABLE_NAME
	        +" ("+ StateCodeColumns._ID
	        + "  INTEGER PRIMARY KEY,"
			+ StateCodeColumns.COLUMNS_WORD_ORDER+TEXT_TYPE+COMMA_SEP
	        + StateCodeColumns.COLUMNS_STATUS_WORD
	        +TEXT_TYPE+" )";
	/**
	 * 删除状态代码表
	 */
	private static final String SQL_DEL_STATE_CODE_TABLE = "DROP TABLE IF EXISTS "
			+ StateCodeColumns.TABLE_NAME;
	
	/**
	 * 创建rd缓存表表
	 */
	private static final String SQL_CREATE_RD_CACHE_TABLE="CREATE TABLE "
			+ RDCacheColumns.TABLE_NAME
			+" ("+ RDCacheColumns._ID
			+ "  INTEGER PRIMARY KEY,"
			+ RDCacheColumns.COLUMNS_SEND_ADDRESS+TEXT_TYPE+COMMA_SEP
			+ RDCacheColumns.COLUMNS_MSG_TYPE+TEXT_TYPE+COMMA_SEP
			+ RDCacheColumns.COLUMNS_PRIORITY+TEXT_TYPE+COMMA_SEP
			+ RDCacheColumns.COLUMNS_MSG_CONTENT+TEXT_TYPE+COMMA_SEP
			+ RDCacheColumns.COLUMNS_WORD
			+TEXT_TYPE+" )";
	/**
	 * 删除rd缓存表
	 */
	private static final String SQL_DEL_RD_CACHE_TABLE = "DROP TABLE IF EXISTS "
			+ RDCacheColumns.TABLE_NAME;
	
	/**
	 * 创建常用短语表
	 */
	private static final String SQL_CREATE_MESSAGE_WORD_TABLE="CREATE TABLE "
			+ MsgWordColumns.TABLE_NAME
			+" ("+ MsgWordColumns._ID
			+ "  INTEGER PRIMARY KEY,"
			+ MsgWordColumns.COLUMNS_WORD
			+TEXT_TYPE+" )";
	/**
	 * 删除常用短语表
	 */
	private static final String SQL_DEL_MESSAGE_WORD_TABLE = "DROP TABLE IF EXISTS "
			+ MsgWordColumns.TABLE_NAME;
	
	private static final String SQL_INSERT_FRIEND_LOCATION="INSERT INTO "+ FriendsLocationColumns.TABLE_NAME+" ("+
			FriendsLocationColumns.FRIENDS_ID+COMMA_SEP+
			FriendsLocationColumns.REPORT_TIME+COMMA_SEP+
			FriendsLocationColumns.FRIENDS_LON+COMMA_SEP+
			FriendsLocationColumns.FRIENDS_LAT+COMMA_SEP+
			FriendsLocationColumns.FRIENDS_HEIGHT+" )  values(?,?,?,?,?)";
	
	/**
	 * 创建通讯消息SQL
	 */
	private static final String SQL_CREATE_MSG_TABLE="CREATE TABLE "+ CustomColumns.TABLE_NAME+" ("+ CustomColumns._ID+"  INTEGER PRIMARY KEY,"
			+ CustomColumns.COLUMNS_USER_ADDRESS+TEXT_TYPE+COMMA_SEP
			+ CustomColumns.COLUMNS_MSG_TYPE+TEXT_TYPE+COMMA_SEP
			+ CustomColumns.COLUMNS_SEND_ADDRESS+TEXT_TYPE+COMMA_SEP
			+ CustomColumns.COLUMNS_SEND_TIME+TEXT_TYPE+COMMA_SEP
			+ CustomColumns.COLUMNS_MSG_LEN+TEXT_TYPE+COMMA_SEP
			+ CustomColumns.COLUMNS_MSG_CONTENT+TEXT_TYPE+COMMA_SEP
			+ CustomColumns.COLUMNS_CRC+TEXT_TYPE+COMMA_SEP
			+ CustomColumns.COLUMNS_FLAG+TEXT_TYPE+" );";
	
	private static final String SQL_CREATE_ROAD_FLAG_TABLE=" CREATE TABLE "+ RoadFlagColumns.TABLE_NAME +" (" + RoadFlagColumns._ID+"  INTEGER PRIMARY KEY,"
			+ RoadFlagColumns.ROAD_NAME +TEXT_TYPE+COMMA_SEP
			+ RoadFlagColumns.ROAD_LON+TEXT_TYPE+COMMA_SEP
			+ RoadFlagColumns.ROAD_LAT+TEXT_TYPE+COMMA_SEP
			+ RoadFlagColumns.ROAD_HEIGH+TEXT_TYPE+COMMA_SEP
			+ RoadFlagColumns.ROAD_NOTE+TEXT_TYPE+" )";
	
	/**
	 * 删除表
	 */
	private static final String SQL_DEL_MSG_TABLE="DROP TABLE IF EXISTS "+ CustomColumns.TABLE_NAME;
	
	
	/**
	 * 创建高级设置密码表SQL
	 * */
	private static  final String SQL_CREATE_ADVANCED_SET_TABLE="CREATE TABLE "+ AdvancedSetColumns.TABLE_NAME+" ("+ AdvancedSetColumns._ID+" INTEGER PRIMARY KEY,"
			+ AdvancedSetColumns.COLUMNS_PWD+TEXT_TYPE+");";
	
	/**
	 * 创建定位设置表
	 */
	private static final String SQL_CREATE_LOCATION_SET_TABLE="CREATE TABLE "+ LocationSetColumns.TABLE_NAME +" ("+
			LocationSetColumns._ID+" INTEGER PRIMARY KEY " +COMMA_SEP+
			LocationSetColumns.LOCATION_STATUS+TEXT_TYPE+COMMA_SEP+
			LocationSetColumns.LOCATION_TYPE+TEXT_TYPE+COMMA_SEP+
			LocationSetColumns.LOCATION_FEQ+TEXT_TYPE+COMMA_SEP+
			LocationSetColumns.HEIGHT_TYPE +TEXT_TYPE+COMMA_SEP+
			LocationSetColumns.HEIGHT_VALUE+TEXT_TYPE+COMMA_SEP+
			LocationSetColumns.TIANXI_VALUE +TEXT_TYPE+" )";
	
	/**
	 * 创建报告设置表
	 */
	private static final String SQL_CREATE_REPORT_SET_TABLE="CREATE TABLE "+ ReportSetColumns.TABLE_NAME +" ("+
			ReportSetColumns._ID+" INTEGER PRIMARY KEY " +COMMA_SEP+
			ReportSetColumns.REPORT_TYPE+TEXT_TYPE+COMMA_SEP+
			ReportSetColumns.REPORT_NUM +TEXT_TYPE+COMMA_SEP+
			ReportSetColumns.REPORT_FEQ+TEXT_TYPE+COMMA_SEP+
			ReportSetColumns.TIANXI_VALUE+TEXT_TYPE+COMMA_SEP+
			STATUS_CODE +TEXT_TYPE+" )";


	/**
	 * 创建短信
	 */
	private static  final String SQL_INSERT_MSG_TABLE="INSERT INTO "+ CustomColumns.TABLE_NAME+" ("+
	    CustomColumns.COLUMNS_USER_ADDRESS+COMMA_SEP+
	    CustomColumns.COLUMNS_MSG_TYPE +COMMA_SEP+
	    CustomColumns.COLUMNS_SEND_ADDRESS +COMMA_SEP+
	    CustomColumns.COLUMNS_SEND_TIME +COMMA_SEP+
	    CustomColumns.COLUMNS_MSG_LEN +COMMA_SEP+
	    CustomColumns.COLUMNS_MSG_CONTENT +COMMA_SEP+
	    CustomColumns.COLUMNS_CRC +COMMA_SEP+
	    CustomColumns.COLUMNS_FLAG +
	   ") values(?,?,?,?,?,?,?,?) ";
	
	
	/**
	 * 常用短语插入SQL语句
	 */
	private static  final String SQL_INSERT_USUAL_WORD="INSERT INTO "+ MsgWordColumns.TABLE_NAME+" ("+
			MsgWordColumns.COLUMNS_WORD+" )"+
			"values(?)";

	/**
	 * 默认状态插入SQL语句
	 */
	private static  final String SQL_INSERT_STATUS="INSERT INTO "+ StateCodeColumns.TABLE_NAME+" ("+
			StateCodeColumns.COLUMNS_WORD_ORDER+COMMA_SEP+
			StateCodeColumns.COLUMNS_STATUS_WORD +" )"+
			"values(?,?)";


	/**
	 * 定位设置插入SQL语句
	 */
	private static  final String SQL_INSERT_LOCATION_SET="INSERT INTO "+ LocationSetColumns.TABLE_NAME+" ("+
			LocationSetColumns.LOCATION_STATUS+COMMA_SEP+
			LocationSetColumns.LOCATION_TYPE+COMMA_SEP+
			LocationSetColumns.LOCATION_FEQ+COMMA_SEP+
			LocationSetColumns.HEIGHT_TYPE +COMMA_SEP+
			LocationSetColumns.HEIGHT_VALUE+COMMA_SEP+
			LocationSetColumns.TIANXI_VALUE+" )"+
			"values(?,?,?,?,?,?)";
	/**
	 * 报告设置插入SQL语句
	 */
	private static  final String SQL_INSERT_REPORT_SET="INSERT INTO "+ ReportSetColumns.TABLE_NAME+" ("+
			ReportSetColumns.REPORT_TYPE+COMMA_SEP+
			ReportSetColumns.REPORT_NUM +COMMA_SEP+
			ReportSetColumns.REPORT_FEQ+COMMA_SEP+
			ReportSetColumns.TIANXI_VALUE+COMMA_SEP+
			STATUS_CODE+" )"+
			"values(?,?,?,?,?)";


		
	
	/**
	 * 删除高级设置密码表
	 */
	private static final String SQL_DEL_ADVANCED_SET_TABLE="DROP TABLE IF EXISTS "+ AdvancedSetColumns.TABLE_NAME;
	/**
	 * 删除定位设置
	 */
	private static final String SQL_DEL_LOC_SET_TABLE="DROP TABLE IF EXISTS "+ LocationSetColumns.TABLE_NAME;
	/**
	 * 删除报告设置
	 */
	private static final String SQL_DEL_REP_SET_TABLE="DROP TABLE IF EXISTS "+ ReportSetColumns.TABLE_NAME;
	
	/**
	 * 删除定位设置
	 */
	private static final String SQL_DEL_ROAD_FLAG_TABLE="DROP TABLE IF EXISTS "+ RoadFlagColumns.TABLE_NAME;
	
	/**
	 * 默认密码
	 * @param context
	 */
	private static final String SQL_INSERT_ADVANCED_SET="INSERT INTO "+ AdvancedSetColumns.TABLE_NAME+" ("+ AdvancedSetColumns.COLUMNS_PWD+") values(?) ";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(SQL_CREATE_MSG_TABLE);
		 db.execSQL(SQL_CREATE_ADVANCED_SET_TABLE);
		 db.execSQL(SQL_CREATE_RD_CACHE_TABLE);//rd缓存
		 db.execSQL(SQL_CREATE_STATE_CODE_TABLE);//常用状态
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{1,"系统默认状态1"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{2,"系统默认状态2"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{3,"系统默认状态3"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{4,"系统默认状态4"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{5,"系统默认状态5"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{6,"系统默认状态6"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{7,"系统默认状态7"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{8,"系统默认状态8"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{9,"系统默认状态9"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{10,"系统默认状态10"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{11,"系统默认状态11"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{12,"系统默认状态12"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{13,"系统默认状态13"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{14,"系统默认状态14"});
		 db.execSQL(SQL_INSERT_STATUS,new Object[]{15,"系统默认状态15"});
		 db.execSQL(SQL_CREATE_MESSAGE_WORD_TABLE);//常用短语
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语1"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语2"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语3"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语4"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语5"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语6"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语7"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语8"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语9"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语10"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语11"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语12"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语13"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语14"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语15"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语16"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语17"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语18"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语19"});
		 db.execSQL(SQL_INSERT_USUAL_WORD,new Object[]{"系统默认常用短语20"});
		 db.execSQL(SQL_CREATE_LOCATION_SET_TABLE);//定位
		 db.execSQL(SQL_INSERT_LOCATION_SET,new Object[]{LocationSetColumns.LOCATIONSET_STATUS_USING,0,"0","0","80","0"});
		 db.execSQL(SQL_INSERT_LOCATION_SET,new Object[]{LocationSetColumns.LOCATIONSET_STATUS_NOT_USING,1,"62","0","80","0"});
		 db.execSQL(SQL_CREATE_REPORT_SET_TABLE);//报告
		 db.execSQL(SQL_INSERT_REPORT_SET,new Object[]{"0","160768","61","45",2});
		 db.execSQL(SQL_INSERT_REPORT_SET,new Object[]{"1","160768","61","45",0});
		 db.execSQL(SQL_INSERT_REPORT_SET,new Object[]{"2","160768","61","45",0});
		 db.execSQL(SQL_INSERT_REPORT_SET,new Object[]{"3","160768","61","45",0});//sos
		 db.execSQL(SQL_CREATE_ROAD_FLAG_TABLE);
         db.execSQL(SQL_CREATE_FRIEND_LOCATION_TABLE);
//         db.execSQL(SQL_CREATE_CONTACT_TABLE);
         db.execSQL(SQL_CREATE_ISTRUCTION_NAV_TABLE);
         db.execSQL(SQL_CREATE_LINE_NAV_TABLE);
         db.execSQL(SQL_CREATE_ZUO_ZHAN_TIME_TABLE);
         db.execSQL(SQL_CREATE_USER_AUTHENTICATION_TABLE);
         db.execSQL(SQL_CREATE_FRIEND_LOCATION_NAV_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL(SQL_DEL_MSG_TABLE);
		 db.execSQL(SQL_DEL_ADVANCED_SET_TABLE);
		 db.execSQL(SQL_DEL_LOC_SET_TABLE);//定位
		 db.execSQL(SQL_DEL_REP_SET_TABLE);//报告
		 db.execSQL(SQL_DEL_ROAD_FLAG_TABLE);
		 db.execSQL(SQL_DEL_FRIEND_LOCATION_TABLE);
		 db.execSQL(SQL_DEL_RD_CACHE_TABLE);//rd缓存
		 db.execSQL(SQL_DEL_STATE_CODE_TABLE);//常用状态
		 db.execSQL(SQL_DEL_MESSAGE_WORD_TABLE);//常用短语
//		 db.execSQL(SQL_DEL_CONTACT_TABLE);
		 db.execSQL(SQL_DEL_ISTRUCTION_NAV_TABLE);
		 db.execSQL(SQL_DEL_LINE_NAV_TABLE);
		 db.execSQL(SQL_DEL_ZUO_ZHAN_TABLE);
		 db.execSQL(SQL_DEL_USER_AUTHENTICATION_TABLE);
		 onCreate(db);
	}
	
}
