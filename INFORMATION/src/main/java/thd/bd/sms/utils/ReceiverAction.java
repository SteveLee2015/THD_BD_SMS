package thd.bd.sms.utils;

/**
 * 记录所有的广播
 * @author llg052
 *
 */
public class ReceiverAction {
	
	
	/**
	 * 广播接收者优先级
	 */
	public static final int BD_PRIORITY_A = 900;
	public static final int BD_PRIORITY_B = 800;
	public static final int BD_PRIORITY_C = 700;
	public static final int BD_PRIORITY_D = 600;
	public static final int BD_PRIORITY_E = 500;

	/**
	 * 线路导航 反馈广播
	 */
	public static final String BD_ACTION_LINE_NAV = "com.ns.protocol_LINE_NAV";
	public static final String BD_KEY_LINE_NAV = "com.ns.protocol_LINE_NAV";


	/**
	 * app启动
	 */
	public static final String APP_BOOT = "com.ns.sms_BOOT";
	public static final String BD_ACTION_HOME = "com.s510.home";
	public static final String BD_KEY_HOME = "data";
	public static final String BD_VALUE_Y_HOME = "enable";//启用home键
	public static final String BD_VALUE_N_HOME = "disenable";//屏蔽home键

	public static final String BD_ACTION_SOS = "com.s510.sos";
	public static final String BD_ACTION_SHORTCUT = "com.s510.shortcut";
	public static final String BD_ACTION_SP = "com.s510.sp";
	// 救援信息界面 救援信息发送倒计时
	public static final String BD_ACTION_SOS_UI = "com.s510.sos_ACTION_SOS_UI";
	public static final String BD_KEY_SOS_UI_COUNT_NUM = "mCountNum";
	// 救援信息界面 救援信息已经发送条数
	public static final String BD_ACTION_SOS_UI_SOS_SIZE = "com.s510.sos_ACTION_SOS_UI_SIZE";
	public static final String BD_KEY_SOS_UI_SOS_SIZE = "mCountSize";


	/**
	 * 短报文更新广播
	 */
	public static final String APP_ACTION_SMS_REFRESH = "com.ns.protocol_APP_ACTION_SMS_REFRESH";
	/**
	 * 新的对话开始了
	 */
	public static final String APP_ACTION_SMS_NEW_DIALOG = "com.ns.protocol_APP_ACTION_SMS_NEW_DIALOG";
	/**
	 * 收件人
	 */
	public static final String APP_KEY_SMS_RECEIVER = "SMS_RECEIVER";
	/**插入的id号**/
	public static final String APP_KEY_SMS_RAWID = "SMS_RAWID";
	/**
	 * 添加图标小红点
	 */
	public static final String APP_ACTION_SMS_ICON = "com.bd.action.BD_SMS_ICON_ACTION";
	/**
	 * 删除图标小红点
	 */
	public static final String APP_ACTION_CLEAR_SMS_ICON = "com.bd.action.BD_SMS_CLEAR_ICON_ACTION";
	
	/**
	 * 友邻位置
	 */
	public static final String APP_ACTION_FRIEND_LOCATION_21 = "com.bd.action.FRIEND_LOCATIONMESSAGE_ACTION";
	public static final String APP_ACTION_FRIEND_LOCATION_HLT = "com.bd.action.FRIEND_LOCATIONMESSAGE_HLT_ACTION";
	/**
	 * 指令导航
	 */
	public static final String APP_ACTION_INSTRUCT_NAVI = "com.bd.action.INSTRUCT_NAVI_ACTION";
	/**
	 * 线路导航
	 */
	public static final String APP_ACTION_LINE_NAVI = "com.bd.action.LINE_NAVI_ACTION";
	
	/**
	 * 思必拓 修改系统时间
	 */
	public static final String SPEEDATA_ACTION_SETDATETIME = "com.speedata.setDateTime";
	/**
	 * 修改系统时间的携带的时间值
	 */
	public static final String SPEEDATA_KEY_DATETIME = "datetime";

	/**
	 * 缓存数据库数据变化广播
	 */
	public static final String DB_ACTION_ON_DATA_CHANGE_ADD = "com.ns.protocol_DB_ACTION_ON_DATA_CHANGE_ADD";

	public static final  String KEY_BD_FRIEND_ID = "FRIEND_ID";//友邻id
}
