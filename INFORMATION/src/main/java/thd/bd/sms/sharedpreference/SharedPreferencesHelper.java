package thd.bd.sms.sharedpreference;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * 保存信息配置类
 *
 * @author lerry
 */
public class SharedPreferencesHelper {
    public static SharedPreferences sharedPreferences;

    public static void init(Application application) {
        sharedPreferences = application.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE);
    }

    public static void put(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (value.getClass() == String.class) {
            editor.putString(key, (String) value);
        } else if (value.getClass() == Boolean.class) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value.getClass() == Integer.class) {
            editor.putInt(key, (Integer) value);
        }else if (value.getClass() == Float.class) {
            editor.putFloat(key, (Float) value);
        }
        editor.commit();
    }


    /**
     * 清除所有数据
     */
    public static void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(Constant.APP_NAME);

        editor.clear();
        editor.commit();
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }


    /**
     * 查询某个key是否存在
     */
    public static Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public static double getLastLat() {
        return sharedPreferences.getFloat(Constant.SP_KEY_RN_LOCATION_LAT, 0.0f);
    }

    public static double getLastLng() {
        return sharedPreferences.getFloat(Constant.SP_KEY_RN_LOCATION_LON, 0.0f);
    }

    public static int getRecordedCount() {
        return sharedPreferences.getInt(Constant.SP_RECORDED_KEY_COUNT, 0);
    }

    public static int getCardInfoCommlevel() {
        return sharedPreferences.getInt(Constant.SP_CARD_INFO_COMMLEVEL, 0);
    }

    public static String getCardInfoCheckEncryption() {
        return sharedPreferences.getString(Constant.SP_CARD_INFO_CHECKENCRYPITION, "");
    }

    public static boolean getIsCheckedPhone(String name) {
        return sharedPreferences.getBoolean(name, false);
    }

    //频度
    public static int getSericeFeq() {
        return sharedPreferences.getInt(Constant.SP_CARD_INFO_SERICEFEQ, 0);
    }
    //是否有卡+卡号
    public static String getCardAddress() {
        return sharedPreferences.getString(Constant.SP_CARD_INFO_ADDRESS, "");
    }

    //RD高度
    public static float getRDHeight() {
        return sharedPreferences.getFloat(Constant.SP_KEY_RD_LOCATION_EARTHHEIGHT, 0.0f);
    }

    public static String getRDTime() {
        return sharedPreferences.getString(Constant.SP_KEY_RD_LOCATION_TIME, "");
    }

    //RD连续位置报告状态
    public static boolean getRDReportState() {
        return sharedPreferences.getBoolean(Constant.SP_RD_REPORT_STATE, false);
    }

    //RN连续位置报告状态
    public static boolean getRNReportState() {
        return sharedPreferences.getBoolean(Constant.SP_RN_REPORT_STATE, false);
    }

    public static float getRDLat() {
        return sharedPreferences.getFloat(Constant.SP_KEY_RD_LOCATION_LAT, 0.0f);
    }

    public static float getRDLon() {
        return sharedPreferences.getFloat(Constant.SP_KEY_RD_LOCATION_LON, 0.0f);
    }

    public static String getSosNum() {
        return sharedPreferences.getString(Constant.SP_KEY_SOS_NUM, "");
    }

    public static String getSosContent() {
        return sharedPreferences.getString(Constant.SP_KEY_SOS_CONTENT, "");
    }

    public static String getCity() {
        return sharedPreferences.getString(Constant.SP_KEY_CITY, "");
    }
}
