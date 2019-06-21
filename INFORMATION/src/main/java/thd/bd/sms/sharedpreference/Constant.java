package thd.bd.sms.sharedpreference;

public class Constant {

    public static final String APP_NAME = "SMS_COMMUNICATION";

    public static final String SP_KEY_RN_LOCATION_LON = "SP_RN_LOCATION_LON";//在SharedPreferences里记录的rn 经度值
    public static final String SP_KEY_RN_LOCATION_LAT = "SP_RN_LOCATION_LAT";//在SharedPreferences里记录的rn 纬度值


    public static final String SP_RECORDED_KEY_COUNT = "SP_RECORDED_KEY_COUNT";//在SharedPreferences里记录的待发送的数目

    public static final String SP_CARD_INFO_COMMLEVEL = "CARD_INFO_COMMLEVEL";//卡信息-等级
    public static final String SP_CARD_INFO_CHECKENCRYPITION = "CARD_INFO_CHECKENCRYPITION";//卡信息-是否加密
    //    public static final String SP_IS_CHECKED_PHONE="IS_CHECKED_PHONE";//发送短报文界面是否选中手机卡发送
    public static final String SP_CARD_INFO_SERICEFEQ = "CARD_INFO_SERICEFEQ";//卡信息-频度
    public static final String SP_CARD_INFO_ADDRESS = "CARD_INFO_ADDRESS";//卡信息-是否有卡+卡号


    public static final String SP_KEY_RD_LOCATION_LON="SP_RN_LOCATION_LON";//在SharedPreferences里记录的rd 定位数据 经度
    public static final String SP_KEY_RD_LOCATION_LAT="SP_RN_LOCATION_LAT";//在SharedPreferences里记录的rd 定位数据 纬度
    public static final String SP_KEY_RD_LOCATION_EARTHHEIGHT="SP_RN_LOCATION_EARTHHEIGHT";//在SharedPreferences里记录的rd 定位数据 高程
    public static final String SP_KEY_RD_LOCATION_TIME="SP_RN_LOCATION_TIME";//在SharedPreferences里记录的rd 定位数据 时间


    public static final String SP_RD_REPORT_STATE="RD_REPORT_STATE";//rd 连续位置报告的状态
    public static final String SP_RN_REPORT_STATE="RN_REPORT_STATE";//rn 连续位置报告的状态

    public static final String SP_KEY_SOS_NUM="SP_KEY_SOS_NUM";//在SharedPreferences里记录的sos 上报平台号码
    public static final String SP_KEY_SOS_CONTENT="SP_KEY_SOS_CONTENT";//在SharedPreferences里记录的sos 上报平台内容

    public static final String SP_KEY_CITY="KEY_CITY";//在SharedPreferences里记录的当前城市名
}
