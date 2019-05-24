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

    public static String getLastLat() {
        return sharedPreferences.getString(Constant.SP_KEY_RN_LOCATION_LAT, "");
    }

    public static String getLastLng() {
        return sharedPreferences.getString(Constant.SP_KEY_RN_LOCATION_LON, "");
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
}
