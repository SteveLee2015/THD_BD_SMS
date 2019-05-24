package thd.bd.sms.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.thd.cmd.manager.entity.EncodeMode;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thd.bd.sms.bean.CoodrinateDate;
import thd.bd.sms.bean.LocationParam;
import thd.bd.sms.sharedpreference.SharedPreferencesHelper;
//import com.baidu.mapapi.utils.CoordinateConverter;

public class Utils {
    private static final String TAG = "Utils";
    public static boolean checkBoxSelect = false;
    private static double PI = 3.14159265358979323846;


    private static final double bj54a = 6378245.0;

    private static final double bj54f = 298.3;

    private static DisplayMetrics mDisplayMetrics = null;


    public static DisplayMetrics getmDisplayMetrics() {
        return mDisplayMetrics;
    }

    public static void setmDisplayMetrics(DisplayMetrics mDisplayMetrics) {
        Utils.mDisplayMetrics = mDisplayMetrics;
    }

    public static AlertDialog createAlertDialog(Context mContext, String title,
                                                String message, boolean cancelable,
                                                OnClickListener negativeListener, String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        builder.setNegativeButton(negativeText, negativeListener);
        return builder.create();
    }

    public static String showTwoBitNum(int num) {
        String str = String.valueOf(num);
        if (num < 10) {
            str = "0" + str;
        }
        return str;
    }

    public static int dp2pixel(int dpvalue) {
        //Log.i("SNR", mDisplayMetrics.densityDpi + "");
        if (mDisplayMetrics == null)
            return 0;
        return (int) ((mDisplayMetrics.densityDpi / 160.0f) * dpvalue);
    }

    /**
     * 构造发送手机短信命令
     *
     * @param phoneNum
     * @param msgContent
     * @return
     */
    public static String buildSendPhoneSMS(String phoneNum, String msgContent) {
        byte[] content = null;
        byte[] phoneNumByteArray = null;
        byte[] msgContentByteArray = null;
        String resultStr = null;
        try {
            phoneNumByteArray = phoneNum.getBytes("GBK");
            msgContentByteArray = msgContent.getBytes("GBK");
            content = new byte[4 + phoneNumByteArray.length
                    + msgContentByteArray.length];
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        content[0] = (byte) 0xCC;
        content[1] = (byte) phoneNum.length();
        System.arraycopy(phoneNumByteArray, 0, content, 2,
                phoneNumByteArray.length);
        content[phoneNumByteArray.length + 2] = 0x00;
        System.arraycopy(msgContentByteArray, 0, content,
                phoneNumByteArray.length + 3, msgContentByteArray.length);
        content[phoneNumByteArray.length + 3 + msgContentByteArray.length] = (byte) 0xDD;

        return bytesToHexString2(content);
//		try {
//			resultStr = new String(content,"GBK");
//			
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return resultStr;
    }

    /**
     * 判断字符串的占的bit数，汉字占用14bit ，字符数字占用8bit
     *
     * @param str
     * @return
     */
    public static int checkStrBits(String str) {
        int chineseNum = getLenOfString(str);
        int num = str.toCharArray().length;
        //return (num - chineseNum) * 8 + (chineseNum * 14);
        return (num - chineseNum) * 8 + (chineseNum * 16);
    }

    public static int getLenOfString(String str) {
        // 汉字个数
        int chCnt = 0;
        String regEx = "[\\u4e00-\\u9fa5]"; // 如果考虑繁体字，u9fa5-->u9fff
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(regEx);
        java.util.regex.Matcher m = p.matcher(str);
        while (m.find()) {
            chCnt++;
        }
        return chCnt;
    }

    /**
     * 判断电文是汉字,代码还是混合
     *
     * @param txt
     * @return
     */
    public static int checkMsg(String txt) {
        /* 汉字统计参数 */
        int count = 0;
        int flag = 0;
        /* 正则表达式判断汉字 */
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(txt);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        /* 包含汉字 */
        if (count != 0) {
            /* 全是汉字 */
            if (count == txt.length()) {
                flag = 0;
                System.out.println("汉字!");
            } else {
                /* 混合 */
                flag = 2;
                System.out.println("混合");
            }
        } else {
            /* 不包含汉字 */
            // flag=1;
            // System.out.println("代码");
            String reg = "[0-9,A,B,C,D,E,F]*";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(txt);
            if (matcher.matches()) {
                flag = 1; // 字符
            } else {
                flag = 2;
            }
        }
        return flag;
    }


    /**
     * 判断电文是汉字,代码还是混合
     * @return
     */
    public static EncodeMode checkMsgEncodeMode(String txt) {
        /* 汉字统计参数 */
        int count = 0;
        EncodeMode flag = EncodeMode.CHINESE_CHAR;
        /* 正则表达式判断汉字 */
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(txt);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        /* 包含汉字 */
        if (count != 0) {
            /* 全是汉字 */
            if (count == txt.length()) {
                flag = EncodeMode.CHINESE_CHAR;
                Log.i(TAG , "========================>汉字");
            } else {
                /* 混合 */
                flag =EncodeMode.MIX;
                System.out.println("混合");
                Log.i(TAG , "========================>混合");
            }
        } else {
            /* 不包含汉字 */
            // flag=1;
            // System.out.println("代码");
            String reg = "[0-9,A,B,C,D,E,F]*";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(txt);
            if (matcher.matches()) {
                flag = EncodeMode.NUMBER;  //字符
                Log.i(TAG , "========================>数字");
            } else {
                flag = EncodeMode.MIX;
                Log.i(TAG , "========================>混合");
            }
        }
        return flag;
    }


    /**
     * string转bcd
     *
     * @param asc
     * @return
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
            System.out.format("%02X\n", bbt[p]);
        }
        return bbt;
    }

    public static double lonStr2Double(String lon) {
        if (lon == null || "".equals(lon)) {
            return 0;
        }
        if (lon.length() != 9) {
            //Log.e(TAG, "Parameter is error!");
            return 0;
        }
        int lonDegress = Integer.valueOf(lon.substring(0, 3));
        double dlonMin = Double.valueOf(lon.substring(3, 5) + "." + lon.substring(5, 8)).doubleValue();
        return lonDegress + dlonMin / 60.0;
    }

    public static double latStr2Double(String lat) {
        if (lat == null || "".equals(lat)) {
            return 0;
        }
        if (lat.length() != 8) {
            Log.e(TAG, "Parameter is error!");
            return 0;
        }
        int latDegress = Integer.valueOf(lat.substring(0, 2));
        double dlanMin = Double.valueOf(lat.substring(2, 4) + "." + lat.substring(4, 7)).doubleValue();
        return latDegress + dlanMin / 60.0;
    }

    public static String getLatDirection(String lat) {
        if (lat == null || "".equals(lat)) {
            return "";
        }
        if (lat.length() != 8) {
            Log.e(TAG, "Parameter is error!");
            return "";
        }
        String direction = lat.substring(7, 8);
        return direction;
    }

    public static String getLonDirection(String lon) {
        if (lon == null || "".equals(lon)) {
            return "";
        }
        if (lon.length() != 9) {
            //Log.e(TAG, "Parameter is error!");
            return "";
        }
        String direction = lon.substring(8, 9);
        return direction;
    }

    /**
     * 把byte数组转换成int
     *
     * @param b
     * @return
     */
    public static final int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    /**
     * 把以'度，分,秒，毫秒'格式的经度或纬度转换为'度'为单位
     *
     * @param array
     * @return
     */
    public static double mTranslateLonLatUnit(int[] array) {
        if (array == null) {
            return 0;
        }
        int mDegree = array[0];
        int mMinute = array[1];
        int mSecond = array[2];
        int mMilliSec = array[3];
        double mValue = mDegree + mMinute / 60.0d
                + (mSecond + (mMilliSec / 10.0d)) / 3600.d;
        return mValue;
    }

    /**
     * 今天是周几
     *
     * @param weekday
     * @return
     */
    public static String getCurrentWeekDay(int weekday) {
        String patten = "日一二三四五六";
        return String.valueOf(patten.charAt(weekday));
    }

    /**
     * 根据电话号码获得名称.
     *
     * @param context
     * @param phoneNum 电话号码
     * @return
     */
    public static String getContactNameFromPhoneNum(Context context,
                                                    String phoneNum) {
        if (phoneNum == null || "".equals(phoneNum)) {
            return null;
        }
        String contactName = null;
        Uri uri = Uri
                .parse("content://com.android.contacts/data/phones/filter/"
                        + phoneNum);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"display_name"},
                null, null, null);
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(0);
        }
        cursor.close();
        return contactName;
    }

    // 判断是否是电话号码
    public static boolean isPhoneNumber(String phoneNum) {
        if (phoneNum.length() == 11) {
            return true;
        } else {
            return false;
        }
    }

    public static String bytesToHexString2(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.replaceAll(" ", "").toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 将char字符转换成byte
     *
     * @param c
     * @return
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte checkSum(byte[] data) {
        int size = data.length;
        byte bCrc = data[0];
        for (int i = 1; i < size - 1; i++) {
            bCrc = (byte) (bCrc ^ data[i]);
        }
        return bCrc;
    }

    // 如果当前数字是小于10,则在前面补零
    public static String changeToTwoBitNumber(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return String.valueOf(num);
        }
    }

    /**
     * 插入电话号码
     *
     * @return
     */
    public static long insertPhoneNumber(Context mContext, String name,
                                         String telNumber) {
        // 第一步,在raw_contacts表中添加联系人id,raw_contacts代表操作data表中的数据
        // 第二部将联系人的各项信息添加到data表中;
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        long contactid = ContentUris.parseId(resolver.insert(
                RawContacts.CONTENT_URI, values));// 可以得到uri的ids
        // 添加姓名
        // 得到了联系人的id
        values.put(Data.RAW_CONTACT_ID, contactid);
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.DISPLAY_NAME, name);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        // 添加电话
        values.clear();
        values.put(Data.RAW_CONTACT_ID, contactid);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        values.put(Phone.NUMBER, telNumber);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        return contactid;
    }

    /**
     * 插入电话号码
     *
     * @return
     */
    public static void updatePhoneNumber(Context mContext, long contactid,
                                         String name, String telNumber) {
        // 第一步,在raw_contacts表中添加联系人id,raw_contacts代表操作data表中的数据
        // 第二部将联系人的各项信息添加到data表中;
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        // 添加姓名
        // 得到了联系人的id
        values.put(StructuredName.DISPLAY_NAME, name);
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                Data.MIMETYPE + "='" + StructuredName.CONTENT_ITEM_TYPE
                        + "' and " + Data.RAW_CONTACT_ID + "=" + contactid,
                null);
        // 添加电话
        values.clear();
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        values.put(Phone.NUMBER, telNumber);
        resolver.update(ContactsContract.Data.CONTENT_URI, values,
                Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "' and "
                        + Data.RAW_CONTACT_ID + "=" + contactid, null);
    }

    /**
     * 校验当前字符串是否是数字
     *
     * @param txt
     * @return
     */
    public static boolean isNumber(String txt) {
        String reg = "[0-9]*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(txt);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /***
     * 待迁移代码
     **/
    public static double LOCATION_REPORT_LON = 0.0d;

    public static double LOCATION_REPORT_LAT = 0.0d;

    public static double LOCATION_REPORT_ALTITUDE = 0.0d;

    public static double LOCATION_REPORT_SPEED = 0.0d;

    public static double LOCATION_REPORT_BEARING = 0.0d;

    public static double LOCATION_REPORT_ACCURACY = 0.0d;

    public static long LOCATION_REPORT_TIME = 0;

    public final static int HANDLER_LOCATION_STATUS = 10000;

    /**
     * RNSS当前的定位模式
     */
    public static int RNSS_CURRENT_LOCATION_MODEL = 0;

    /*** 待迁移代码 **/

    /**
     * 将经纬度转换成yyyyy.yy 或者 llll.ll
     *
     * @param
     * @return
     */
    public static double translateLonLat(double num) {
        if (num == 0.0) {
            return 0.0;
        }
        String str = String.valueOf(num);
        String[] temp = str.split("\\.");
        double high = Double.valueOf(temp[0]);
        double low = Double.valueOf("0." + temp[1]);
        return high * 100 + low * 60;
    }

    /**
     * 将经度yyyyy.yy转换成yyy.yyyy
     *
     * @param str
     * @return
     */
    public static String getLon(String str) {
        int degree = Integer.valueOf(str.substring(0, 3));
        double num = Double.valueOf(str.substring(3));
        double second = num / 60.0d;
        return String.valueOf(degree + second);
    }

    /**
     * 将经度llll.ll转换成ll.lll
     *
     * @param str
     * @return
     */
    public static String getLat(String str) {
        int degree = Integer.valueOf(str.substring(0, 2));
        double num = Double.valueOf(str.substring(2)).doubleValue();
        double second = num / 60.0d;
        return String.valueOf(degree + second);
    }

    /**
     * 转换经纬度
     *
     * @return
     */
    public static String lonLatTranslater(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        int degee = 0;
        int minute = 0;
        String[] temp = str.split("\\.");
        if (temp[0].length() == 5) {
            degee = Integer.valueOf(temp[0].substring(0, 3));
            minute = Integer.valueOf(temp[0].substring(3, 5));
        } else if (temp[0].length() == 4) {
            degee = Integer.valueOf(temp[0].substring(0, 2));
            minute = Integer.valueOf(temp[0].substring(2, 4));
        }
        String second = "0." + temp[1];
        DecimalFormat df = new DecimalFormat(".##");
        String sec = df.format(Double.valueOf(second) * 60);
        return (degee + "°" + minute + "′" + sec + "″");
    }

    public static String getTime(String time) {
        String hour = time.substring(0, 2);
        String minute = time.substring(2, 4);
        String second = time.substring(4, 6);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return (date + " " + hour + ":" + minute + ":" + second);
    }

    /**
     * 转换经纬度 该函数 used rnss
     *
     * @param lon
     * @param lat
     * @param height
     * @return 参考 line 2290
     */
    public static LocationParam translate(double lon, double lat,
                                          double height, int flag) {
        LocationParam coodrinate = null;
        switch (flag) {
            case 0: // 2000大地坐标
                coodrinate = new LocationParam();
                String mlon = String.valueOf(lon);
                coodrinate.setmLon(mlon);
                String mlat = String.valueOf(lat);
                coodrinate.setmLat(mlat);
                coodrinate.setmHeight(String.valueOf(height));
                break;
            case 1: // 高斯平面坐标
                /**** wrong ******/
                // CoodrinateDate date =
                // LBToGSXY(Double.valueOf(getLat(String.valueOf(lon))),
                // Double.valueOf(getLat(String.valueOf(lat))), height);
                // coodrinate.setmLon(date.getLon()+"");
                // coodrinate.setmLat(date.getLat()+"");
                // coodrinate.setmHeight(date.getHeight()+"");
                /********* 分割线 *********/
                // coodrinate = LBToGOSS(lon,lat, height);
                int mode = 2;
                if (1 == mode) {

                    coodrinate = LBToGOSS(Double.valueOf(getLon(String.valueOf(lon))),
                            Double.valueOf(getLat(String.valueOf(lat))), height);
                } else {

                    //coodrinate = LBToGaoSi(Double.valueOf(getLon(String.valueOf(lon))),
                     //       Double.valueOf(getLat(String.valueOf(lat))), height);

                    coodrinate = LBToGaoSi(lon,lat, height);
                }

                // ///////////////////////////////////

                break;
            case 2: // 麦卡托平面坐标
                // coodrinate = DD_MCTOR(lon,lat, height, 0);
                int type = 2;

                if (1 == type) {
                    coodrinate = DD_MCTOR(Double.valueOf(getLon(String.valueOf(lon))),
                            Double.valueOf(getLat(String.valueOf(lat))), height, 0);

                } else {

                    coodrinate = Maikatou(lon, lat, height);
                }


                break;
            case 3: // 空间直角坐标
                coodrinate = DD_KJZJ_llg_rnss(lon, lat, height);
                // coodrinate =
                // DD_KJZJ_llg_rnss(Double.valueOf(getLat(String.valueOf(lon))),
                // Double.valueOf(getLat(String.valueOf(lat))), height);
                // coodrinate = DD_KJZJ(Double.valueOf(getLat(String.valueOf(lon))),
                // Double.valueOf(getLat(String.valueOf(lat))), height);
                break;
            case 4: // beijing54坐标
                // TODO
                // coodrinate = DD_KJZJ(Double.valueOf(getLat(String.valueOf(lon))),
                // Double.valueOf(getLat(String.valueOf(lat))), height);

			/* bj54坐标系 */
                // coodrinate=new BDLocation();

                coodrinate = new LocationParam();
                // double[] bj54lonlat=transWGS84ToBj54(bdlocation.mLongitude,
                // bdlocation.mLatitude);
                double[] bj54lonlat = transWGS84ToBj54(lon, lat);

                String longiFormat = String.format("%.6f", bj54lonlat[0]);
                String latiFormat = String.format("%.6f", bj54lonlat[1]);

                // coodrinate.setLongitude(bj54lonlat[0]);
                coodrinate.setmLon(longiFormat);
                // coodrinate.setLatitude(bj54lonlat[1]);
                coodrinate.setmLat(latiFormat);
                // coodrinate.setEarthHeight(bdlocation.getEarthHeight());
                coodrinate.setmHeight(String.valueOf(height));

                break;
            default:
                break;
        }
        return coodrinate;
    }

    /* 将经纬度坐标转换成高斯平面坐标 */
    public static CoodrinateDate LBToGSXY(double dblLong /* in 经度 */,
                                          double dblLat/* in 纬度 */, double dblHigh/* 高程 */) {
        return DD_GOSS(dblLong, dblLat, dblHigh, 0);
    }

    public static LocationParam LBToGOSS(double dblLong /* in 经度 */,
                                         double dblLat/* in 纬度 */, double dblHigh/* 高程 */) {
        return DD_GOSS1(dblLong, dblLat, dblHigh, 0);
    }


    public static LocationParam LBToGaoSi(double dblLong, double dblLat,
                                          double dalHeight) {
        return DD_GOSS2(dblLong, dblLat, dalHeight, 500000.0d, 0.0d, 1.0d,
                -1.0d, bj54a, bj54f, 0.0d);
    }

    /*
     * **************************************************************************************************
     * - 函数名称 : DD_GOSS(FP32 L, FP32 B, FP32 H, FP32 gcycz) - 函数说明 : 大地坐标转高斯坐标 -
     * 输入参数 : L:经度;B:纬度;H:高程;gcycz:高程异常值 - 输出参数 : 无
     * ******************************
     * ********************************************************************
     */
    public static CoodrinateDate DD_GOSS(double L /* in 经度 */
            , double B/* in 纬度 */, double H /* 高程 */, double gcycz /* 高程异常 */) {
        double SINB, COSB;
        /* SINB=sinB,COSB=cosB */
        double t;
        /* t=tgB */
        double e, ns;
		/* e=(a*a-b*b)/(a*a),ns is n */
        double ls, NL;
		/* ls=l"/p", N */
        double XL;
		/* X */
        double dataf1, dataf2, dataf3, dataf4, dataf5;
		/* temp FP32 data */
        double a;
        double f;
        // 54坐标系
        // if (zbx == 0)
        // {
        // a=6378245 ;
        // /*a*/
        // f=0.0033523 ;
        // }
        // //84坐标系
        // else
        // {
        a = 6378137;
		/* a */
        f = 0.003352810664;
        // }
        // a=6378149 ; /* a */
        // f=0.0033528 ;/* f */
		/* f */
        dataf1 = B * PI / 180.0;
        SINB = Math.sin(dataf1);
        COSB = Math.cos(dataf1);
        dataf2 = SINB / COSB;
        t = dataf2 * dataf2;
		/* t=t^2=square(tgB); */

        dataf1 = a * (1.0 - f);
		/* b */
        e = (a * a - dataf1 * dataf1) / (a * a);
		/* square(e) */

        dataf2 = Math.sqrt((a * a - dataf1 * dataf1) / (dataf1 * dataf1));
		/* e' */
        dataf3 = dataf2 * COSB;
        ns = dataf3 * dataf3;
		/* square(n) */

        dataf1 = Math.floor(L / 6) * 6 + 3;
		/* L0 */
        dataf2 = L - dataf1;
        ls = dataf2 * PI / 180.0;
		/* ls=l"/p" */

        dataf1 = Math.sqrt(1.0 - e * SINB * SINB);
        NL = a / dataf1;
		/* N */

        dataf1 = 1.0 + e * 3.0 / 4.0;
        dataf1 += e * e * 45.0 / 64.0;
        dataf1 += e * e * e * 175.0 / 256.0;
        dataf1 += e * e * e * e * 11025.0 / 16384.0;
		/* ~A */

        dataf2 = e * 3.0 / 4.0;
        dataf2 += e * e * 15.0 / 16.0;
        dataf2 += e * e * e * 525.0 / 512.0;
        dataf2 += e * e * e * e * 2205.0 / 2048.0;
		/* ~B */

        dataf3 = e * e * 15.0 / 64.0;
        dataf3 += e * e * e * 105.0 / 256.0;
        dataf3 += e * e * e * e * 2205.0 / 4096.0;
		/* ~C */

        dataf4 = e * e * e * 35.0 / 512.0;
        dataf4 += e * e * e * e * 315.0 / 2048.0;
		/* ~D */

        dataf5 = e * e * e * e * 315.0 / 16384.0;
		/* ~E */

        XL = dataf1 * B * PI / 180.0;
        XL -= dataf2 * SINB * COSB;
        XL += dataf3 * SINB * COSB * (2.0 * COSB * COSB - 1.0);
        XL -= dataf4 / 3.0 * (3.0 * SINB - 4.0 * SINB * SINB * SINB)
                * (4.0 * COSB * COSB * COSB - 3.0 * COSB);
        XL *= a;
        XL *= 1.0 - e;
		/* XL */

        dataf1 = XL;
        dataf1 += NL * ls * ls * SINB * COSB / 2.0;
        dataf2 = 5.0 - t + 9.0 * ns + 4.0 * ns * ns;
        dataf3 = NL * ls * ls * ls * ls * SINB * COSB * COSB * COSB / 24.0;
        dataf1 += dataf3 * dataf2;
        dataf2 = 61.0 - 58.0 * t + t * t;
        dataf3 = NL * Math.pow(ls, 6.0) * SINB * Math.pow(COSB, 5.0) / 720.0;
        dataf1 += dataf3 * dataf2;
		/* x */
        dataf4 = NL * ls * COSB;
        dataf2 = NL * ls * ls * ls * COSB * COSB * COSB * (1 - t + ns) / 6.0;
        dataf4 += dataf2;
        dataf3 = ls * COSB;
        dataf2 = dataf3 * dataf3 * dataf3 * dataf3 * dataf3;
        dataf3 = NL * ls * dataf2 / 120.0;
        dataf2 = 5 - 18 * t + t * t + 14 * ns - 58 * ns * t;
        dataf4 += dataf3 * dataf2;

        dataf4 += 500000.0;
        dataf2 = Math.floor(L / 6) + 1;
        dataf2 *= 1000000.0;
        dataf4 += dataf2;
		/* y */
        dataf3 = H - gcycz;
		/* H */
        CoodrinateDate data = new CoodrinateDate();
        data.setLat(dataf1);
        data.setLon(dataf4);
        data.setHeight(dataf3);

        return data;
    }

    public static LocationParam DD_KJZJ(double L, double B, double H) {
        double dataf1, dataf2, dataf3, dataf4;
        // 84
		/*
		 * a= 6378137; f=0.003352810664
		 * 
		 * 
		 * //54 a = 6378245 f = 0.0033523298692
		 */

        // //54 zuobiaoxi
        // if (zbx == 0)
        // {
        // dataf3=6378245.0 ; /* a */
        // dataf1=0.0033523 ;/* f */
        // }
        // //84 zuobiaoxi
        // else
        // {
        dataf3 = 6378137.0; /* a */
        dataf1 = 0.003352810664;/* f */
        // }

        dataf2 = (1.0 - dataf1) * dataf3;/*
										 * b = (1-0.0033523)*6378245 =
										 * 6356863.2092865
										 */

        dataf1 = dataf3 * dataf3 - dataf2 * dataf2; /*
													 * a^2-b^2 = 6378245*6378245
													 * - 6356863.2092865*
													 * 6356863.2092865 =
													 * 272299418444.73970016091775
													 */
        dataf1 /= dataf3 * dataf3; /* (a^2-b^2)/a^2 = 0.00669336208471 */
		/* dataf1=e^2 */
        dataf2 = B * PI / 180.0;
        dataf4 = Math.sin(dataf2);
		/* sin(B) */
        dataf2 = dataf3 / Math.sqrt(1.0 - dataf1 * dataf4 * dataf4);
		/* N */

        dataf3 = (dataf2 * (1.0 - dataf1) + H) * dataf4;
		/* Z */
        dataf1 = (dataf2 + H) * Math.cos(B * PI / 180.0)
                * Math.cos(L * PI / 180.0);
		/* X */
        dataf4 = (dataf2 + H) * Math.cos(B * PI / 180.0)
                * Math.sin(L * PI / 180.0);
		/* Y */

        // /* Z */
        // dblX = dataf1;
        // dblY = dataf4;
        // dblZ = (dataf3);
        LocationParam coodrinate = new LocationParam();
        coodrinate.setmLon(String.valueOf(dataf1));
        coodrinate.setmLat(String.valueOf(dataf4));
        coodrinate.setmHeight(String.valueOf(dataf3));
        return coodrinate;
    }

    /**
     * 将经纬度转换为麦卡托平面坐标
     *
     * @param L
     * @param B
     * @param H
     * @param gcycz
     */
    // void DD_MCTOR(FP32 L, FP32 B, FP32 H, FP32 gcycz)
    public static LocationParam DD_MCTOR(double L, double B, double H,
                                         double gcycz) {
        double dataf1, dataf2, dataf3;
        double lk, r0, U;

        dataf1 = L / 6.0;
        dataf2 = Math.floor(dataf1) * 6.0;
        dataf2 += 3.0;
		/* L0 */
        lk = (L - dataf2) * PI / 180.0;

        // if (zbx == 0)
        // {
        // r0=6378245 ;
        // /* a */
        // dataf2=0.0033523 ;
        //
        // }
        // else
        // {
        r0 = 6378137;
		/* a */
        dataf2 = 0.003352810664;
        // }
		/* f */
        dataf1 = r0 * (1 - dataf2);
		/* b */
        dataf2 = (r0 * r0 - dataf1 * dataf1) / (r0 * r0);
		/* e^2 */
        r0 = Math.cos(PI / 6.0);
        // //54×ø±êÏµ
        // if (zbx == 0)
        // {
        // r0=6378245*r0/Math.sqrt(1-dataf2*Math.sin(PI/6.0)*Math.sin(PI/6.0));
        // }
        // //84×ø±êÏµ
        // else{
        r0 = 6378137
                * r0
                / Math.sqrt(1 - dataf2 * Math.sin(PI / 6.0)
                * Math.sin(PI / 6.0));
        // }

        dataf3 = Math.sqrt(dataf2);
		/* e */
        dataf2 = Math.sin(B * PI / 180.0);
        U = (1 - dataf3 * dataf2) / (1 + dataf3 * dataf2);
        dataf1 = Math.sqrt(U);
        U = Math.pow(dataf1, dataf3);
        dataf2 = Math.tan(B * PI / 360.0);
		/* tgB/2 */
        dataf1 = (1.0 + dataf2) / (1.0 - dataf2);
        U *= dataf1;
        // dblX = r0*log(U);
        // dblY = r0*lk;
        // dblZ = H - gcycz;

        LocationParam coodrinate = new LocationParam();
        coodrinate.setmLon(String.valueOf(r0 * Math.log(U)));
        coodrinate.setmLat(String.valueOf(r0 * lk));
        coodrinate.setmHeight(String.valueOf(H - gcycz));
        return coodrinate;
    }

    public static LocationParam Maikatou(double lon, double lat, double height) {
        LocationParam param = xy_(lon, lat, 500000, 0, 0.9996, -1, bj54a, bj54f, 0);
        param.setmHeight(height + "");
        return param;
    }


    public static LocationParam xy_(double lon, double lat, double efalsing,
                                    double nfalsing, double scale, double l0, double a, double f,
                                    double hcrr_h) {
        int i;
        double b = 0.0d, l = 0.0d, e = 0.0d, e1 = 0.0d, tt = 0.0d, n = 0.0d, g = 0.0d, m0 = 0.0d, x0_ = 0.0d, tmp = 0.0d;
        b = lat;
        l = lon;
        double[] par = new double[6];
        if (l0 == -1) {
            if (l > 0)
                l0 = (int) ((int) (l + 3) / 6. + 0.5) * 6 - 3;
            else
                l0 = (int) ((int) (l - 3) / 6. - 0.5) * 6 + 3;
        }
        l -= l0;
        if (l < -350.)
            l += 360.;
        l *= PI / 180;
        b *= PI / 180;
        e = 2.0 / f - 1.0 / f / f;
        e1 = e / (-e + 1.0);
        a += hcrr_h * (1 - e * Math.sin(b) * Math.sin(b)) / Math.sqrt(1.0 - e);
        par[0] = Math.pow(e, 5) * 43659.0 / 65536.0 + Math.pow(e, 4) * 11025.0
                / 16384.0 + 1.0;
        par[0] += e * e * 45.0 / 64.0 + Math.pow(e, 3) * 175.0 / 256.0 + e
                * 0.75;
        par[1] = e * 0.75 + e * e * 15.0 / 16.0 + Math.pow(e, 3) * 525.0
                / 512.0;
        par[1] += Math.pow(e, 4) * 2205.0 / 2048.0 + Math.pow(e, 5) * 72765.0
                / 65536.0;
        par[2] = e * e * 15.0 / 64.0 + Math.pow(e, 3) * 105.0 / 256.0;
        par[2] += Math.pow(e, 4) * 2205.0 / 4096.0 + Math.pow(e, 5) * 10395.0
                / 16384.0;
        par[3] = Math.pow(e, 3) * 35.0 / 512.0 + Math.pow(e, 4) * 315.0
                / 2048.0;
        par[3] += Math.pow(e, 5) * 31185.0 / 131072.0;
        par[4] = Math.pow(e, 4) * 315.0 / 16384.0 + Math.pow(e, 5) * 3465.0
                / 65536.0;
        par[5] = Math.pow(e, 5) * 693.0 / 131072.0;
        tmp = a * (-e + 1.0);
        par[0] *= tmp;
        for (i = 1; i < 6; i++)
            par[i] *= Math.pow(-1.0, (i * 1.0)) * tmp / (2.0 * i);
        x0_ = par[0] * b + par[1] * Math.sin(2.0 * b) + par[2]
                * Math.sin(4.0 * b);
        x0_ += par[3] * Math.sin(6.0 * b) + par[4] * Math.sin(8.0 * b) + par[5]
                * Math.sin(10.0 * b);
        tt = Math.tan(b);
        n = a / Math.sqrt(1.0 - e * Math.sin(b) * Math.sin(b));
        g = e1 * Math.cos(b) * Math.cos(b);
        m0 = l * Math.cos(b);

        double X = x0_ + n * tt * m0 * m0 / 2.0;
        X += (5.0 - tt * tt + 9.0 * g + 4.0 * g * g) * n * tt * Math.pow(m0, 4)
                / 24.0;
        X += n * Math.pow(m0, 6) * tt
                * (61.0 - 58.0 * tt * tt + tt * tt * tt * tt) / 720.0;

        double Y = n * m0 + n * Math.pow(m0, 3) * (1.0 - tt * tt + g) / 6.0;
        Y += (5.0 - 18.0 * tt * tt + tt * tt * tt * tt + 14.0 * g - 58.0 * g
                * tt * tt)
                * n * Math.pow(m0, 5) / 120.0;
        X *= scale;
        Y *= scale;
        X += nfalsing;
        Y += efalsing;
        LocationParam param = new LocationParam();
        //param.setLongitude(Y);
        param.setmLon(Y + "");
        //param.setLatitude(X);
        //param.setLatitude(X);
        param.setmLat(X + "");
        return param;
    }

    /**
     * 将字符串坐标系转换成数值
     *
     * @param str
     * @return
     */
    public static double getCoodrinate(String str) {
        if (str == null || "".equals(str) || !checkCoodrinate(str)) {
            return 0;
        }
        int degree = 0;
        int min = 0;
        int second = 0;
        double result = 0;

        String[] temp = str.split("°");
        degree = Integer.valueOf(temp[0].equals("") ? "0" : temp[0]);
        String[] temp1 = temp[1].split("′");
        min = Integer.valueOf(temp1[0].equals("") ? "0" : temp1[0]);
        second = Integer.valueOf(temp1[1].split("″")[0].equals("") ? "0"
                : temp1[1].split("″")[0]);
        result = degree + min / 60.0 + second / (60.0 * 60.0);
        return result;
    }

    /*
     * 判断是否是坐标类型
     */
    public static boolean checkCoodrinate(String str) {
        return str.matches("[0-9°′″]+");
    }

    public static LocationParam DD_KJZJ_llg_rnss(double L, double B, double H) {
        double dataf1, dataf2, dataf3, dataf4;
        // 84
		/*
		 * a= 6378137; f=0.003352810664
		 * 
		 * 
		 * //54 a = 6378245 f = 0.0033523298692
		 */

        // //54 zuobiaoxi
        // if (zbx == 0)
        // {
        dataf3 = 6378245.0; /* a */
        dataf1 = 0.0033523;/* f */
        // }
        // //84 zuobiaoxi
        // else
        // {
        // dataf3 = 6378137.0; /* a */
        // dataf1 = 0.003352810664;/* f */
        // }

        dataf2 = (1.0 - dataf1) * dataf3;/*
										 * b = (1-0.0033523)*6378245 =
										 * 6356863.2092865
										 */

        dataf1 = dataf3 * dataf3 - dataf2 * dataf2; /*
													 * a^2-b^2 = 6378245*6378245
													 * - 6356863.2092865*
													 * 6356863.2092865 =
													 * 272299418444.73970016091775
													 */
        dataf1 /= dataf3 * dataf3; /* (a^2-b^2)/a^2 = 0.00669336208471 */
		/* dataf1=e^2 */
        dataf2 = B * PI / 180.0;
        dataf4 = Math.sin(dataf2);
		/* sin(B) */
        dataf2 = dataf3 / Math.sqrt(1.0 - dataf1 * dataf4 * dataf4);
		/* N */

        dataf3 = (dataf2 * (1.0 - dataf1) + H) * dataf4;
		/* Z */
        dataf1 = (dataf2 + H) * Math.cos(B * PI / 180.0)
                * Math.cos(L * PI / 180.0);
		/* X */
        dataf4 = (dataf2 + H) * Math.cos(B * PI / 180.0)
                * Math.sin(L * PI / 180.0);
		/* Y */
        LocationParam coodrinate = new LocationParam();
        coodrinate.setmLon(dataf1 + "");
        coodrinate.setmLat(dataf4 + "");
        coodrinate.setmHeight(dataf3 + "");
        return coodrinate;
    }

    /*
     * **************************************************************************************************
     * - 函数名称 : DD_GOSS(FP32 L, FP32 B, FP32 H, FP32 gcycz) - 函数说明 : 大地坐标转高斯坐标 -
     * 输入参数 : L:经度;B:纬度;H:高程;gcycz:高程异常值 - 输出参数 : 无
     * ******************************
     * ********************************************************************
     */
    public static LocationParam DD_GOSS1(double L /* in 经度 */
            , double B/* in 纬度 */, double H /* 高程 */, double gcycz /* 高程异常 */) {
        double SINB, COSB;
		/* SINB=sinB,COSB=cosB */
        double t;
		/* t=tgB */
        double e, ns;
		/* e=(a*a-b*b)/(a*a),ns is n */
        double ls, NL;
		/* ls=l"/p", N */
        double XL;
		/* X */
        double dataf1, dataf2, dataf3, dataf4, dataf5;
		/* temp FP32 data */
        double a;
        double f;
        // 54坐标系
        // if (zbx == 0)
        // {
        // a=6378245 ;
        // /*a*/
        // f=0.0033523 ;
        // }
        // //84坐标系
        // else
        // {
        a = 6378137;
		/* a */
        f = 0.003352810664;
        // }
        // a=6378149 ; /* a */
        // f=0.0033528 ;/* f */
		/* f */
        dataf1 = B * PI / 180.0;
        SINB = Math.sin(dataf1);
        COSB = Math.cos(dataf1);
        dataf2 = SINB / COSB;
        t = dataf2 * dataf2;
		/* t=t^2=square(tgB); */

        dataf1 = a * (1.0 - f);
		/* b */
        e = (a * a - dataf1 * dataf1) / (a * a);
		/* square(e) */

        dataf2 = Math.sqrt((a * a - dataf1 * dataf1) / (dataf1 * dataf1));
		/* e' */
        dataf3 = dataf2 * COSB;
        ns = dataf3 * dataf3;
		/* square(n) */

        dataf1 = Math.floor(L / 6) * 6 + 3;
		/* L0 */
        dataf2 = L - dataf1;
        ls = dataf2 * PI / 180.0;
		/* ls=l"/p" */

        dataf1 = Math.sqrt(1.0 - e * SINB * SINB);
        NL = a / dataf1;
		/* N */

        dataf1 = 1.0 + e * 3.0 / 4.0;
        dataf1 += e * e * 45.0 / 64.0;
        dataf1 += e * e * e * 175.0 / 256.0;
        dataf1 += e * e * e * e * 11025.0 / 16384.0;
		/* ~A */

        dataf2 = e * 3.0 / 4.0;
        dataf2 += e * e * 15.0 / 16.0;
        dataf2 += e * e * e * 525.0 / 512.0;
        dataf2 += e * e * e * e * 2205.0 / 2048.0;
		/* ~B */

        dataf3 = e * e * 15.0 / 64.0;
        dataf3 += e * e * e * 105.0 / 256.0;
        dataf3 += e * e * e * e * 2205.0 / 4096.0;
		/* ~C */

        dataf4 = e * e * e * 35.0 / 512.0;
        dataf4 += e * e * e * e * 315.0 / 2048.0;
		/* ~D */

        dataf5 = e * e * e * e * 315.0 / 16384.0;
		/* ~E */

        XL = dataf1 * B * PI / 180.0;
        XL -= dataf2 * SINB * COSB;
        XL += dataf3 * SINB * COSB * (2.0 * COSB * COSB - 1.0);
        XL -= dataf4 / 3.0 * (3.0 * SINB - 4.0 * SINB * SINB * SINB)
                * (4.0 * COSB * COSB * COSB - 3.0 * COSB);
        XL *= a;
        XL *= 1.0 - e;
		/* XL */

        dataf1 = XL;
        dataf1 += NL * ls * ls * SINB * COSB / 2.0;
        dataf2 = 5.0 - t + 9.0 * ns + 4.0 * ns * ns;
        dataf3 = NL * ls * ls * ls * ls * SINB * COSB * COSB * COSB / 24.0;
        dataf1 += dataf3 * dataf2;
        dataf2 = 61.0 - 58.0 * t + t * t;
        dataf3 = NL * Math.pow(ls, 6.0) * SINB * Math.pow(COSB, 5.0) / 720.0;
        dataf1 += dataf3 * dataf2;
		/* x */
        dataf4 = NL * ls * COSB;
        dataf2 = NL * ls * ls * ls * COSB * COSB * COSB * (1 - t + ns) / 6.0;
        dataf4 += dataf2;
        dataf3 = ls * COSB;
        dataf2 = dataf3 * dataf3 * dataf3 * dataf3 * dataf3;
        dataf3 = NL * ls * dataf2 / 120.0;
        dataf2 = 5 - 18 * t + t * t + 14 * ns - 58 * ns * t;
        dataf4 += dataf3 * dataf2;

        dataf4 += 500000.0;
        dataf2 = Math.floor(L / 6) + 1;
        dataf2 *= 1000000.0;
        dataf4 += dataf2;
		/* y */
        dataf3 = H - gcycz;
		/* H */
        LocationParam data = new LocationParam();
        data.setmLat(String.valueOf(dataf1));
        data.setmLon(String.valueOf(dataf4));
        data.setmHeight(String.valueOf(dataf3));
        return data;
    }

    public static LocationParam DD_GOSS2(double dblLong, double dblLat,
                                         double dblHeight, double efalsing, double nfalsing, double scale,
                                         double l0, double a, double f, double hcrr_h) {
        double X = 0.0;
        double Y = 0.0;
        double B, L;
        //

        int i;
        double b, l, e, e1, tt, n, g, m0, x0_, tmp;
        double[] par = new double[6];

        B = dblLat;
        L = dblLong;
        b = B;
        l = L;
        // if((Lf=='W'||Lf=='w')&&L>0)l*=-1;
        // if((Bf=='S'||Bf=='s')&&B>0)b*=-1;

        if (l0 == -1) {
            if (l > 0)
                l0 = (int) ((int) (l + 3) / 6. + 0.5) * 6 - 3;
            else
                l0 = (int) ((int) (l - 3) / 6. - 0.5) * 6 + 3;
        }
        l -= l0;
        if (l < -350.)
            l += 360.;
        l *= PI / 180;
        b *= PI / 180;
        e = 2.0 / f - 1.0 / f / f;
        e1 = e / (-e + 1.0);
        a += hcrr_h * (1 - e * Math.sin(b) * Math.sin(b)) / Math.sqrt(1.0 - e);
        par[0] = Math.pow(e, 5) * 43659.0 / 65536.0 + Math.pow(e, 4) * 11025.0
                / 16384.0 + 1.0;
        par[0] += e * e * 45.0 / 64.0 + Math.pow(e, 3) * 175.0 / 256.0 + e
                * 0.75;
        par[1] = e * 0.75 + e * e * 15.0 / 16.0 + Math.pow(e, 3) * 525.0
                / 512.0;
        par[1] += Math.pow(e, 4) * 2205.0 / 2048.0 + Math.pow(e, 5) * 72765.0
                / 65536.0;
        par[2] = e * e * 15.0 / 64.0 + Math.pow(e, 3) * 105.0 / 256.0;
        par[2] += Math.pow(e, 4) * 2205.0 / 4096.0 + Math.pow(e, 5) * 10395.0
                / 16384.0;
        par[3] = Math.pow(e, 3) * 35.0 / 512.0 + Math.pow(e, 4) * 315.0
                / 2048.0;
        par[3] += Math.pow(e, 5) * 31185.0 / 131072.0;
        par[4] = Math.pow(e, 4) * 315.0 / 16384.0 + Math.pow(e, 5) * 3465.0
                / 65536.0;
        par[5] = Math.pow(e, 5) * 693.0 / 131072.0;
        tmp = a * (-e + 1.0);
        par[0] *= tmp;
        for (i = 1; i < 6; i++)
            par[i] *= Math.pow(-1.0, (i * 1.0)) * tmp / (2.0 * i);
        x0_ = par[0] * b + par[1] * Math.sin(2.0 * b) + par[2]
                * Math.sin(4.0 * b);
        x0_ += par[3] * Math.sin(6.0 * b) + par[4] * Math.sin(8.0 * b) + par[5]
                * Math.sin(10.0 * b);

        tt = Math.tan(b);
        n = a / Math.sqrt(1.0 - e * Math.sin(b) * Math.sin(b));
        g = e1 * Math.cos(b) * Math.cos(b);
        m0 = l * Math.cos(b);
        X = x0_ + n * tt * m0 * m0 / 2.0;
        X += (5.0 - tt * tt + 9.0 * g + 4.0 * g * g) * n * tt * Math.pow(m0, 4)
                / 24.0;
        X += n * Math.pow(m0, 6) * tt
                * (61.0 - 58.0 * tt * tt + tt * tt * tt * tt) / 720.0;
        Y = n * m0 + n * Math.pow(m0, 3) * (1.0 - tt * tt + g) / 6.0;
        Y += (5.0 - 18.0 * tt * tt + tt * tt * tt * tt + 14.0 * g - 58.0 * g
                * tt * tt)
                * n * Math.pow(m0, 5) / 120.0;
        X *= scale;
        Y *= scale;
        X += nfalsing;
        Y += efalsing;
        LocationParam coodrinate = new LocationParam();
        //coodrinate.setLongitude(Y);
        coodrinate.setmLon(Y + "");
        //coodrinate.setLatitude(X);
        coodrinate.setmLat(X + "");
        //coodrinate.setEarthHeight(dblHeight);
        coodrinate.setmHeight(dblHeight + "");
        return coodrinate;
    }

    /**
     * GPS的坐标WGS84坐标系转BJ54坐标系
     *
     * @param dLongitude
     * @param dLatitude
     * @return
     */
    public static double[] transWGS84ToBj54(double dLongitude, double dLatitude) {
        double EARTH_WGS84_A = 6378137.0000;
        double EARTH_WGS84_E2 = 0.00669437999013;
        double EARTH_WGS84_FLATTENING = 298.257223563;
        double EARTH_BJ54_A = 6378245.0;
        double EARTH_BJ54_E2 = 0.00667;
        double EARTH_BJ54_FLATTENING = 298.3;
        double OMIGA = 206264.8062;
        double DELTA_X = -28.3;
        double DELTA_Y = 144.9;
        double DELTA_Z = 77.5;
        double sinRenda = Math.sin(dLongitude);
        double cosRenda = Math.cos(dLongitude);
        double sinFi = Math.sin(dLatitude);
        double cosFi = Math.cos(dLatitude);
        double sinFi2 = sinFi * sinFi;
        double eSinFi = Math.sqrt(1 - EARTH_WGS84_E2 * sinFi2);
        double M = EARTH_WGS84_A * (1 - EARTH_WGS84_E2)
                / (eSinFi * eSinFi * eSinFi);
        double N = EARTH_WGS84_A / eSinFi;
        double deltaE2 = 2 * (1 - 1 / EARTH_BJ54_FLATTENING)
                * (1 / EARTH_WGS84_FLATTENING - 1 / EARTH_BJ54_FLATTENING);
        double deltaA = EARTH_BJ54_A - EARTH_WGS84_A;
        double deltaRenda = OMIGA * (cosRenda * DELTA_Y - sinRenda * DELTA_X)
                / (N * sinRenda);
        double deltaFi = OMIGA
                * ((EARTH_WGS84_A * deltaE2 + EARTH_WGS84_E2 * deltaA) * sinFi
                * cosFi + EARTH_WGS84_A * EARTH_WGS84_E2 * deltaE2
                * sinFi * sinFi * sinFi * cosFi - sinFi * cosRenda
                * DELTA_X - sinFi * sinRenda * DELTA_X + cosFi
                * DELTA_Z) / M;
        double[] bj54lonlat = new double[2];
        bj54lonlat[0] = dLongitude + deltaRenda / 3600; // 经度
        bj54lonlat[1] = dLatitude + deltaFi / 3600; // 纬度
        return bj54lonlat;
    }

    /**
     * 检测当前北斗卫星RDSS是否适合发送定位/短报文/位置报告 信号分为差，一般，良好
     *
     * @return
     */
    public static int checkCurrentRDSSStatus(int[] beamWaves) {
        int[] beams = new int[5];
        int index = 0;
        for (int i = 0; i < 5; i++) {
            beams[i] = beamWaves[i * 2] + beamWaves[i * 2 + 1];
            if (beams[i] > 0) {
                index++;
            }
        }
        // if(index>=2){
        // //当前可发送定位/短报文
        // }else if(index>=1){
        // //当前只能发送短报文
        // }else{
        // //当前信号较差,不能发送与卫星较差，请到移动到空旷的地方。
        // }
        return index;
    }


    /**
     * 获得短信的最大长度
     */
    public static int getMessageMaxLength() {
        int MESSAGE_MAX_LENGHTH = 0;

        Log.e(TAG, "getMessageMaxLength: =========getMessageMaxLength=====" );

        if ((SharedPreferencesHelper.getCardInfoCommlevel()!=0) && !"".equals(SharedPreferencesHelper.getCardInfoCheckEncryption())) {
            if ("E".equals(SharedPreferencesHelper.getCardInfoCheckEncryption())) {
                switch (SharedPreferencesHelper.getCardInfoCommlevel()) {
                    case 1:
                        MESSAGE_MAX_LENGHTH = 140-8;
                        break;
                    case 2:
                        MESSAGE_MAX_LENGHTH = 360-8;
                        break;
                    case 3:
                        MESSAGE_MAX_LENGHTH = 580-8;
                        break;
                    case 4:
                        MESSAGE_MAX_LENGHTH = 1680-8;
                        break;
                    default:
                        break;
                }
            } else {
                switch (SharedPreferencesHelper.getCardInfoCommlevel()) {
                    case 1:
                        MESSAGE_MAX_LENGHTH = 110-8;
                        break;
                    case 2:
                        MESSAGE_MAX_LENGHTH = 408-8;
                        break;
                    case 3:
                        MESSAGE_MAX_LENGHTH = 628-8;
                        break;
                    case 4:
                        MESSAGE_MAX_LENGHTH = 848-8;
                        break;
                    default:
                        break;
                }
            }
        }
        return MESSAGE_MAX_LENGHTH;
    }


    /**
     * 将GPS设备采集的原始GPS坐标转换成百度坐标
     *
     * @return
     */
//    public static LatLng baiduMapJP(LatLng sourceLatLng) {
//
//        //初始化坐标转换工具类，指定源坐标类型和坐标数据
//// sourceLatLng待转换坐标
//        CoordinateConverter converter = new CoordinateConverter()
//                .from(CoordinateConverter.CoordType.GPS)
//                .coord(sourceLatLng);
//
//        //desLatLng 转换后的坐标
//        LatLng desLatLng = converter.convert();
//
//        return desLatLng;
//    }


    /**
     * 将GPS设备采集的原始GPS坐标转换成百度坐标
     *
     * @return
     */
    /*public static LatLng baiduMapJP(LatLng sourceLatLng) {

        //初始化坐标转换工具类，指定源坐标类型和坐标数据
// sourceLatLng待转换坐标
        CoordinateConverter converter = new CoordinateConverter()
                .from(CoordinateConverter.CoordType.GPS)
                .coord(sourceLatLng);

        //desLatLng 转换后的坐标
        LatLng desLatLng = converter.convert();

        return desLatLng;
    }*/
}
