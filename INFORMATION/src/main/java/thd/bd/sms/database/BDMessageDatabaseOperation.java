package thd.bd.sms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import thd.bd.sms.R;
import thd.bd.sms.bean.Item;
import thd.bd.sms.database.DataBaseHelper.BDMessageColumns;

import thd.bd.sms.bean.BDMessageInfo;
import thd.bd.sms.utils.Utils;

public class BDMessageDatabaseOperation {

    ////短信标识  0-收件箱  1-发件箱  2-草稿箱  3-表示未读
    public static final String MSG_TYPE_SEND = "1";
    public static final String MSG_TYPE_WANT_SEND = "2";
    public static final String MSG_TYPE_NOT_READ = "3";
    public static final String MSG_TYPE_RECEIVER = "0";
    public static final String KEY_ROWID = "_id";
    private Context context;
    private DataBaseHelper databaseHelper;
    private SQLiteDatabase sqliteDatabase;
    private SimpleDateFormat sdf = null;
    private int YEAR = 0, MONTH = 0, DAY = 0;

    public BDMessageDatabaseOperation(Context mContext) {
        this.context = mContext;
        databaseHelper = new DataBaseHelper(context);
        sqliteDatabase = databaseHelper.getWritableDatabase();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public long insert(BDMessageInfo mBDMessageInfo, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BDMessageColumns.COLUMNS_USER_ADDRESS, mBDMessageInfo.getmUserAddress().startsWith("0") ? mBDMessageInfo.getmUserAddress().substring(1) : mBDMessageInfo.getmUserAddress());
        contentValues.put(BDMessageColumns.COLUMNS_MSG_TYPE, mBDMessageInfo.getMsgType());
        if (context != null) {
            try {
                String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + "=?";
                Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID}, selection, new String[]{mBDMessageInfo.getmUserAddress()}, null);
                if (cursor != null){
                       if(cursor.getCount() > 0) {
                           Cursor nameCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                                   ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))}, null);

                           if (nameCursor != null) {
                               if (nameCursor.getCount() > 0) {
                                   String username = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                   if (!TextUtils.isEmpty(username)) {
                                       mBDMessageInfo.setUserName(username);
                                   }
                               }
                               nameCursor.close();
                           }

                       }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        contentValues.put(BDMessageColumns.COLUMNS_USER_NAME, mBDMessageInfo.getUserName() != null ? mBDMessageInfo.getUserName() : "");
        contentValues.put(BDMessageColumns.COLUMNS_SEND_TIME, mBDMessageInfo.getmSendTime());
        contentValues.put(BDMessageColumns.COLUMNS_MSG_LEN, mBDMessageInfo.getMessage().length());
        contentValues.put(BDMessageColumns.COLUMNS_MSG_CONTENT, mBDMessageInfo.getMessage());
        contentValues.put(BDMessageColumns.COLUMNS_CRC, "");
        contentValues.put(BDMessageColumns.COLUMNS_FLAG, type); //短信标识  0-收件箱  1-发件箱  2-草稿箱  3-表示未读
        long id = sqliteDatabase.insert(BDMessageColumns.TABLE_NAME, null, contentValues);
        return id;
    }

    //得到首页显示的短信数据
    public Cursor getHomeMessages() {
        String orderBy = BDMessageColumns._ID + " desc";
        //查询
        Cursor mCursor = sqliteDatabase.query(true, BDMessageColumns.TABLE_NAME, new String[]{BDMessageColumns._ID, BDMessageColumns.COLUMNS_USER_ADDRESS,
                BDMessageColumns.COLUMNS_MSG_TYPE, BDMessageColumns.COLUMNS_USER_NAME,
                BDMessageColumns.COLUMNS_SEND_TIME, BDMessageColumns.COLUMNS_MSG_LEN,
                BDMessageColumns.COLUMNS_MSG_CONTENT, BDMessageColumns.COLUMNS_CRC,
                BDMessageColumns.COLUMNS_FLAG
        }, null, null, BDMessageColumns.COLUMNS_USER_ADDRESS, null, orderBy, null);
        return mCursor;
    }

    public int getTotalNumByPhone(String phoneNumber) {
        Cursor mCursor = sqliteDatabase.query(true, BDMessageColumns.TABLE_NAME, new String[]{BDMessageColumns._ID, BDMessageColumns.COLUMNS_USER_ADDRESS,
                BDMessageColumns.COLUMNS_MSG_TYPE, BDMessageColumns.COLUMNS_USER_NAME,
                BDMessageColumns.COLUMNS_SEND_TIME, BDMessageColumns.COLUMNS_MSG_LEN,
                BDMessageColumns.COLUMNS_MSG_CONTENT, BDMessageColumns.COLUMNS_CRC,
                BDMessageColumns.COLUMNS_FLAG
        }, BDMessageColumns.COLUMNS_USER_ADDRESS + "=? ", new String[]{phoneNumber}, null, null, null, null);
        int size = mCursor.getCount();
        if (mCursor != null) {
            mCursor.close();
        }
        return size;
    }

    /**
     * 分页
     */
    public Cursor loadPage(int firstVisibleItem, int visibleLastIndex) {
        String orderBy = BDMessageColumns._ID + " desc";
        //查询
        Cursor mCursor = sqliteDatabase.query(true, BDMessageColumns.TABLE_NAME, new String[]{BDMessageColumns._ID, BDMessageColumns.COLUMNS_USER_ADDRESS,
                BDMessageColumns.COLUMNS_MSG_TYPE, BDMessageColumns.COLUMNS_USER_NAME,
                BDMessageColumns.COLUMNS_SEND_TIME, BDMessageColumns.COLUMNS_MSG_LEN,
                BDMessageColumns.COLUMNS_MSG_CONTENT, BDMessageColumns.COLUMNS_CRC,
                BDMessageColumns.COLUMNS_FLAG
        }, null, null, null, null, orderBy, String.valueOf(firstVisibleItem) + "," + String.valueOf(visibleLastIndex));
        return mCursor;
    }

    /**
     * 分页
     */
    public Cursor loadPage(String phoneNumber, int firstVisibleItem, int visibleLastIndex) {
        String orderBy = BDMessageColumns._ID + " desc";
        //查询
        Cursor mCursor = sqliteDatabase.query(true, BDMessageColumns.TABLE_NAME, new String[]{BDMessageColumns._ID, BDMessageColumns.COLUMNS_USER_ADDRESS,
                BDMessageColumns.COLUMNS_MSG_TYPE, BDMessageColumns.COLUMNS_USER_NAME,
                BDMessageColumns.COLUMNS_SEND_TIME, BDMessageColumns.COLUMNS_MSG_LEN,
                BDMessageColumns.COLUMNS_MSG_CONTENT, BDMessageColumns.COLUMNS_CRC,
                BDMessageColumns.COLUMNS_FLAG
        }, BDMessageColumns.COLUMNS_USER_ADDRESS + "=? and " + BDMessageColumns.COLUMNS_FLAG + "!=2", new String[]{phoneNumber}, null, null, null, null);


        return mCursor;
    }

    /**
     * 删除
     *
     * @param rowId
     * @return
     */
    public boolean delete(long rowId) {
        return sqliteDatabase.delete(BDMessageColumns.TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean delete() {

        return sqliteDatabase.delete(BDMessageColumns.TABLE_NAME, null, null) > 0;
    }

    public boolean delete(String phoneNum) {
        return sqliteDatabase.delete(BDMessageColumns.TABLE_NAME, BDMessageColumns.COLUMNS_USER_ADDRESS + "=?", new String[]{phoneNum}) > 0;
    }

    /**
     * 分页
     */
    public Cursor getDraftMessages(String phoneNumber) {
        String orderBy = BDMessageColumns._ID + " desc";
        //查询
        Cursor mCursor = sqliteDatabase.query(true, BDMessageColumns.TABLE_NAME, new String[]{BDMessageColumns._ID, BDMessageColumns.COLUMNS_USER_ADDRESS,
                BDMessageColumns.COLUMNS_MSG_TYPE, BDMessageColumns.COLUMNS_USER_NAME,
                BDMessageColumns.COLUMNS_SEND_TIME, BDMessageColumns.COLUMNS_MSG_LEN,
                BDMessageColumns.COLUMNS_MSG_CONTENT, BDMessageColumns.COLUMNS_CRC,
                BDMessageColumns.COLUMNS_FLAG
        }, BDMessageColumns.COLUMNS_USER_ADDRESS + "=? and " + BDMessageColumns.COLUMNS_FLAG + "=2", new String[]{phoneNumber}, null, null, orderBy, null);
        return mCursor;
    }


    /**
     * 更新当前短信的状态(发件人,收件人，未读)
     */
    public boolean updateMessageStatus(long rowId, int flag) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BDMessageColumns.COLUMNS_FLAG, flag);
        return sqliteDatabase.update(BDMessageColumns.TABLE_NAME, contentValues, BDMessageColumns._ID + "=" + rowId, null) > 0;
    }

    public boolean updateMessageContent(long rowId, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BDMessageColumns.COLUMNS_MSG_CONTENT, content);
        return sqliteDatabase.update(BDMessageColumns.TABLE_NAME, contentValues, BDMessageColumns._ID + "=" + rowId, null) > 0;
    }


    ///////////////////////////////////////////////////////////////////////////////////

    public List<Map<String, Object>> build(Cursor cursor) {
        List<Map<String, Object>> templist = new ArrayList<Map<String, Object>>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            String msg = cursor.getString(cursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_MSG_CONTENT));
            if (msg.length() > 28) {
                msg = msg.substring(0, 26) + "...";
            }
            String flagTAG = cursor.getString((cursor.getColumnIndex(BDMessageColumns.COLUMNS_FLAG)));
            if ("2".equals(flagTAG)) {
                msg = "[草稿]" + msg;
            }

            //SEND_NAME
            map.put(BDMessageColumns._ID, cursor.getLong(cursor
                    .getColumnIndex(BDMessageColumns._ID)));

            map.put("COLUMNN_ID", cursor.getString(cursor.getColumnIndex(BDMessageColumns._ID)));
            map.put("SEND_CONTENT_SIZE", cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_MSG_LEN)));
            map.put("SEND_CONTENT", msg);
            String date = cursor.getString((cursor.getColumnIndex(BDMessageColumns.COLUMNS_SEND_TIME)));

            map.put("PHONE_NUMBER", cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_ADDRESS)));
            map.put("MESSAGE_FLAG", cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_FLAG)));
            map.put("MESSAGE_ID", cursor.getString(cursor.getColumnIndex(BDMessageColumns._ID)));
            map.put("SEND_NAME", cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_NAME)));
            /**
             * 1.如果短信日期不是当天的信息,则仅仅显示月、日 2.如果短信日期不是当年的信息,则显示年、月、日
             */
            String reg = "[0-9,-]{2}:[0-9,-]{2}";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(date);
            if (matcher.matches()) {// 如果事件格式00:00
                date = date.replaceAll("-", "0");
            } else {// 時間格式yyyy-MM-dd HH:mm:ss
                String[] date1 = date.split(" ");
                String[] time = date1[0].split("-");
                if (time[2].equals(Utils.showTwoBitNum(day))
                        && time[1].equals(Utils.showTwoBitNum(month))
                        && time[0].equals(String.valueOf(year))) {
                    // 显示时分秒
                    date = date1[1];
                } else if (time[2].equals(Utils.showTwoBitNum(day))
                        && (!time[1].equals(Utils.showTwoBitNum(month)) || !time[0]
                        .equals(String.valueOf(year)))) {
                    // 显示月、日
                    date = time[1] + "月" + time[2] + "日";
                } else if (!time[2].equals(Utils.showTwoBitNum(day))
                        && !time[1].equals(Utils.showTwoBitNum(month))
                        && !time[0].equals(String.valueOf(year))) {
                    // 显示年、月、日
                    date = time[0] + "年" + time[1] + "月" + time[2] + "日";
                }
            }
            map.put("SEND_DATE", date);
            String flag = cursor.getString((cursor.getColumnIndex(BDMessageColumns.COLUMNS_FLAG)));
            map.put("MESSAGE_FLAG_NOT_DRAWABLE", flag);
            if (flag != null && "0".equals(flag)) {//收件
                String receiverAddress = cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_ADDRESS));
                String userName = cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_NAME));
                map.put("SEND_NAME", userName);
                map.put("SEND_ID", receiverAddress);
                map.put("MESSAGE_FLAG", context.getResources().getDrawable(R.drawable.bak_for_other));
            } else if (flag != null && "1".equals(flag)) {//发件
                String senderAddress = cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_ADDRESS));
                if (senderAddress.contains("(") && senderAddress.contains(")")) {
                    String senderAddressNumber = senderAddress.substring(senderAddress.indexOf("(") + 1, senderAddress.indexOf(")"));
                    String senderAddressName = Utils.getContactNameFromPhoneNum(context, senderAddressNumber);
                    if (senderAddressName != null && !"".equals(senderAddressName)) {
                        senderAddress = senderAddressName;
                    }
                }
                String userName = cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_NAME));
                map.put("SEND_NAME", userName);
                map.put("SEND_ID", senderAddress);
                map.put("MESSAGE_FLAG", context.getResources().getDrawable(R.drawable.bak_for_other));
            } else if (flag != null && "2".equals(flag)) {
                //草稿
                String userid = cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_ADDRESS));
                if (userid != null && !"".equals(userid)) {
                    map.put("SEND_ID", userid);
                } else {
                    map.put("SEND_ID", "草稿");
                }
                map.put("MESSAGE_FLAG", context.getResources().getDrawable(R.drawable.bak_for_other));
            } else if (flag != null && "3".equals(flag)) {//未读
                String userName = cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_NAME));
                map.put("SEND_NAME", userName);
                map.put("SEND_ID", cursor.getString(cursor.getColumnIndex(BDMessageColumns.COLUMNS_USER_ADDRESS)));
                map.put("MESSAGE_FLAG", context.getResources().getDrawable(R.drawable.bak_for_other));
            } else {

            }
            templist.add(map);
        }
        return templist;
    }

    /**
     * 转化为item对象
     *
     * @param map
     * @param
     */
    public Item object2Item(Map<String, Object> map) {

        Item item = new Item();

        String userName = String.valueOf(map.get("SEND_NAME"));
        String send_id = String.valueOf(map.get("SEND_ID"));
        String send_content = String.valueOf(map.get("SEND_CONTENT"));
        String send_date = String.valueOf(map.get("SEND_DATE"));
        Long rowId = Long.valueOf(String.valueOf(map.get("COLUMNN_ID")));
        Drawable message_flag = (Drawable) (map.get("MESSAGE_FLAG"));
        String message_flag_not_drawable = String.valueOf(map.get("MESSAGE_FLAG_NOT_DRAWABLE"));
        Boolean checked = false;
        item.send_id = send_id;
        item.send_name = userName;
        item.send_content = send_content;
        item.send_date = send_date;
        item.message_flag = message_flag;
        item.message_flag_not_drawable = message_flag_not_drawable;
        item.checked = checked;
        item.rowId = rowId;

        return item;
    }


    //填充数据
    public List<Map<String, Object>> iniMsgData(Cursor mCursor) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (mCursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(BDMessageColumns._ID, mCursor.getLong(mCursor
                    .getColumnIndex(BDMessageColumns._ID)));
            map.put(BDMessageColumns.COLUMNS_CRC, mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_CRC)));
            String flag = mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_FLAG));
            map.put(BDMessageColumns.COLUMNS_FLAG, flag);
            String header = "";
            if ("0".equals(flag)) {
                //header = "[收件]";
            } else if ("1".equals(flag)) {
                //header = "[发件]";
            } else if ("2".equals(flag)) {
                header = "[草稿]";
            } else if ("3".equals(flag)) {
                //header = "[未读]";
            } else if ("4".equals(flag)) {
                //header = "[救援]";
            } else if ("5".equals(flag)) {
                //header = "[位置]";
            }
            map.put("COLUMNS_MSG_HEADER", header);
            map.put(BDMessageColumns.COLUMNS_MSG_CONTENT, header +
                    mCursor.getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_MSG_CONTENT)));
            map.put(BDMessageColumns.COLUMNS_MSG_LEN, mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_MSG_LEN)));

            map.put(BDMessageColumns.COLUMNS_MSG_TYPE, mCursor
                    .getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_MSG_TYPE)));
            map.put(BDMessageColumns.COLUMNS_USER_NAME,
                    mCursor.getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_USER_NAME)));

            String time = mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_SEND_TIME));
            // 如果读取的日期的年份是今年。如果日期不是当前日期的话，显示，显示 "月份"+"日期" 例如：07-25
            // 如果日期是当前日期的话，显示 “时”+"分" 例如:"08:30"
            // 如果读取的日期的年份是不是今年，则显示"年"+"月" 例如:"2013-08"
            try {
                Date date = sdf.parse(time);
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTime(date);
                int mYear = mCalendar.get(Calendar.YEAR);
                int mMonth = mCalendar.get(Calendar.MONTH) + 1;
                int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                if (YEAR == mYear) {
                    if (MONTH == mMonth && DAY == mDay) {
                        time = Utils.changeToTwoBitNumber(mCalendar
                                .get(Calendar.HOUR_OF_DAY))
                                + ":"
                                + Utils.changeToTwoBitNumber(mCalendar
                                .get(Calendar.MINUTE));
                    } else {
                        time = Utils.changeToTwoBitNumber(mMonth) + "-"
                                + Utils.changeToTwoBitNumber(mDay);
                    }
                } else {
                    time = mYear + "-" + Utils.changeToTwoBitNumber(mMonth);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.put(BDMessageColumns.COLUMNS_SEND_TIME, time);
            map.put(BDMessageColumns.COLUMNS_USER_ADDRESS,
                    mCursor.getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_USER_ADDRESS)));
            list.add(map);
        }
        mCursor.close();
        return list;
    }

    ////////////////////////////////////////////////////////////////////
    public List<Map<String, Object>> getDataFromCursor(Cursor mCursor) {

        List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();

        while (mCursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(BDMessageColumns._ID, mCursor.getLong(mCursor.getColumnIndex(BDMessageColumns._ID)));
            map.put(BDMessageColumns.COLUMNS_CRC, mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_CRC)));
            map.put(BDMessageColumns.COLUMNS_FLAG, mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_FLAG)));

            map.put(BDMessageColumns.COLUMNS_MSG_CONTENT,
                    mCursor.getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_MSG_CONTENT)));

            map.put(BDMessageColumns.COLUMNS_MSG_LEN, mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_MSG_LEN)));
            map.put(BDMessageColumns.COLUMNS_MSG_TYPE, mCursor
                    .getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_MSG_TYPE)));
            map.put(BDMessageColumns.COLUMNS_USER_NAME,
                    mCursor.getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_USER_NAME)));
            String time = mCursor.getString(mCursor
                    .getColumnIndex(BDMessageColumns.COLUMNS_SEND_TIME));
            map.put(BDMessageColumns.COLUMNS_SEND_TIME, time);
            map.put(BDMessageColumns.COLUMNS_USER_ADDRESS,
                    mCursor.getString(mCursor
                            .getColumnIndex(BDMessageColumns.COLUMNS_USER_ADDRESS)));
            listData.add(map);
        }
        mCursor.close();
        return listData;
    }

}
