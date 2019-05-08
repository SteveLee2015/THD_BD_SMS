package thd.bd.sms.crashUtils;

public class Constant {

    public static final String SP_KEY_RN_LOCATION_LON="SP_RN_LOCATION_LON";//在SharedPreferences里记录的rn 经度值
    public static final String SP_KEY_RN_LOCATION_LAT="SP_RN_LOCATION_LAT";//在SharedPreferences里记录的rn 纬度值

    public static String getSpKeyRnLocationLon() {
        return SP_KEY_RN_LOCATION_LON;
    }

    public static String getSpKeyRnLocationLat() {
        return SP_KEY_RN_LOCATION_LAT;
    }
}
