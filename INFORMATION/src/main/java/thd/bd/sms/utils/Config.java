package thd.bd.sms.utils;

public class Config {


	public static int SOS_COUNT = 0;

	public static final int CACHE_COUNT = 800;
	// 新建对话
	public static final int NEW_DIALOG = 1;
	// 回复对话
	public static final int REPLY_DIALOG = 2;

	// 设置界面 用于区分 rd rn
	public static final String FLAG_TAG = "flag_tag";
	public static final String GPS_MODE = "gps_model_set";
	// rd界面
	public static final int FLAG_RD = 3;
	// rn界面
	public static final int FLAG_RN = 4;
	// gps界面
	public static final int FLAG_GPS = 5;
	// 北斗界面
	public static final int FLAG_BD = 6;
	// 所有星图
	public static final int FLAG_ALL = 7;

	// 常用短语界面
	public static final int FLAG_USUAL_WORD = 10;

	// 状态代码
	public static final int FLAG_STATUS_CODE = 11;


	// activity 类型
	public static final String INTENT_TYPE = "intent_type";

	// 打开联系人界面是否需要返回结果
	public static final String NEED_BACK = "need_back";

	// 短报文通知notification id
	public static final int NOTIFICATION_SMS = 100;
	// 友邻位置notification id
	public static final int NOTIFICATION_LOC_REPORT = 101;
	//线路导航
	public static final int NOTIFICATION_LINE_NAVIGATION = 102;
	//指令导航
	public static final int NOTIFICATION_COMMAND_NAVIGATION = 103;
	//友邻位置
	public static final int NOTIFICATION_LOC_FRIENDS = 105;
	//rd rn 位置报告时间
	public static final int RN_RD_WAA = 1001;
	//rd 定位申请时间
	public static final int RD_DWR = 1002;


	// 1
	public static class CommType {
		public static final int COMMON_MODE = 1;
		public static final int FAST_MODE = 0;
	}

	// 2
	public static class EncodingMode {
		public static final int TEXT_MODE = 0;
		public static final int CODE_MODE = 1;
		public static final int COMPLEX_MODE = 2;
	}

	// 3
	public static class HeightFlag {
		public static final String HIGHT_USER = "H";
		public static final String COMMON_USER = "L";
	}

	// 4
	public static class HeightType {
		public static final int HAVE_HEIGHT_VALUE = 0;
		public static final int HAVE_NO_HEIGHT_VALUE = 1;
		public static final int HAVE_CHECK_HEIGHT_1 = 2;
		public static final int HAVE_CHECK_HEIGHT_2 = 3;
	}

	// 5
	public static class ImmediateLocState {
		public static final boolean LOC_IMMEDIATE_FLAG = true;
		public static final boolean LOC_NORMAL_FLAG = false;
	}

	// 6
	public static class ManagerMode {
		public static final int SET_USERDEVICE = 1;
		public static final int READ_USERDEVICE = 2;
		public static final int RETURN_USERDEVICE = 3;
	}

	// 7
	public static class QueryType {
		public static final int BROADCAST_MODE = 3;
		public static final int RESEARCH_MODE = 4;
		public static final int ADDRESS_MODE = 5;
	}

	// 8
	public static class ResponseMode {
		public static final String IS_RESPONSE_MODE = "Y";
		public static final String NOT_RESPONSE_MODE = "N";
	}

	// 9
	public static class SwitchMode {
		public static final int CLOSE_MODE = 1;
		public static final int OPEN_MODE = 2;
		public static final int ALL_CLOSE_MODE = 3;
		public static final int ALL_OPEN_MODE = 4;
	}

	// 10
	public static class ZeroValueMode {
		public static final int READ_DEVICE = 1;
		public static final int SET_DEVICE = 2;
		public static final int RETURN_DEVICE = 3;
	}
	
	public static final String INTENT_ACTION="myaction";
	public static final int INTENT_TYPE_FRIEND_LOCATION_LIST=17;//友邻位置list
    public static final int INTENT_TYPE_FRIEND_LOCATION_LIST_NOTIFYCATION=19;//友邻位置list  在notification中
    public static final int INTENT_TYPE_FRIEND_LOCATION_ITEM=18;//友邻位置item 单个
    
    
    /**RN连续报位**/
    public static final String RN_RUNNING = "正在RN连续位置报告";
    public static final String RN_WAITING = "等待RN连续位置报告";
    
    /**RD连续报位**/
    public static final String RD_RUNNING = "正在连续报告";
    public static final String RD_WAITING = "连续位置报告";
    
    /**状态连续报位**/
    public static final String SMS_RUNNING = "正在连续状态报告";
    public static final String SMS_WAITING = "等待连续状态报告";
    

}
