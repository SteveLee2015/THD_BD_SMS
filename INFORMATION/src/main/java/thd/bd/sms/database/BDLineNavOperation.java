package thd.bd.sms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import thd.bd.sms.base.DatabaseHelper;
import thd.bd.sms.base.DatabaseHelper.LineNavColumns;
import thd.bd.sms.bean.BDLineNav;
import thd.bd.sms.bean.BDPoint;
import thd.bd.sms.utils.Utils;

/**
 * 指令导航表操作类
 *
 * @author steve
 */
public class BDLineNavOperation {


    private static final String TAG = "BDLineNavOperation";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CREATED = "created";

    private DatabaseHelper databaseHelper;
    private Context context;
    private SQLiteDatabase sqliteDatabase;

    public BDLineNavOperation(Context mContext) {
        this.context = mContext;
        databaseHelper = new DatabaseHelper(context);
        sqliteDatabase = databaseHelper.getReadableDatabase();
    }


    /**
     * 增加北斗路线导航
     *
     * @param lineId       线路id
     * @param currentIndex 当前条序号
     * @param totalNum     总序号
     * @param passPointStr 途经点
     * @return
     */
    public long insert(String lineId, String currentIndex, String totalNum, String passPointStr, String createTime) {

        Log.d(TAG, "insert: ");
        ContentValues contentValues = new ContentValues();
        contentValues.put(LineNavColumns.NAV_LINE_ID, lineId);
        contentValues.put(LineNavColumns.CREATED_TIME, createTime);
        contentValues.put(LineNavColumns.CURRENT_INDEX, currentIndex);
        contentValues.put(LineNavColumns.TOTAL_NUMBER, totalNum);
        contentValues.put(LineNavColumns.PASS_POINT, passPointStr);
        long id = sqliteDatabase.insert(LineNavColumns.TABLE_NAME, null, contentValues);
        return id;
    }

    /**
     * 删除
     *
     * @param rowId
     * @return
     */
    public boolean delete(long rowId) {
        boolean istrue = sqliteDatabase.delete(LineNavColumns.TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
        return istrue;
    }

    public boolean delete(String lineId) {
        boolean istrue = sqliteDatabase.delete(LineNavColumns.TABLE_NAME, LineNavColumns.NAV_LINE_ID + "='" + lineId + "'", null) > 0;
        return istrue;
    }


    public boolean delete(String lineId, String currentIndex) {
        Log.d(TAG, "delete: String lineId, String currentIndex" + lineId + "==" + currentIndex);
        // 按照条件删除
        //boolean istrue = sqliteDatabase.delete(LineNavColumns.TABLE_NAME, LineNavColumns.NAV_LINE_ID + "='" + lineId + "'", null) > 0;
        //db.delete（“表名”，“xxx=? and yyy=?”,new String[]{"aaa","bbb"}）；可以试试
        String where = LineNavColumns.NAV_LINE_ID + "= ? and " + LineNavColumns.CURRENT_INDEX + " = ? ";

        boolean istrue = sqliteDatabase.delete(LineNavColumns.TABLE_NAME, where, new String[]{lineId, currentIndex}) > 0;
        return istrue;
    }


    /**
     * 全部删除
     *
     * @param
     * @return
     */
    public boolean delete() {
        Log.d(TAG, "delete: 全部删除");
        boolean istrue = sqliteDatabase.delete(LineNavColumns.TABLE_NAME, null, null) > 0;
        return istrue;
    }

    /**
     * 判断当前lineID的线路是否接收完
     *
     * @return
     */
    public boolean checkLineNavComplete(String lineID) {
        Log.d(TAG, "checkLineNavComplete: lineID=" + lineID);
        Cursor mCursor = sqliteDatabase.query(true,
                LineNavColumns.TABLE_NAME,
                new String[]{
                        LineNavColumns._ID,
                        LineNavColumns.NAV_LINE_ID,
                        LineNavColumns.CREATED_TIME,
                        LineNavColumns.CURRENT_INDEX,
                        LineNavColumns.TOTAL_NUMBER,
                        LineNavColumns.PASS_POINT},
                LineNavColumns.NAV_LINE_ID + "='" + lineID + "'", null, null, null, null, null, null);
        boolean isture = false;
        ArrayList<String> currentIndexs = new ArrayList<>();
        String total = null;
        while (mCursor.moveToNext()) {
            //当前条序号数
            String index = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.CURRENT_INDEX));
            total = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.TOTAL_NUMBER));
            currentIndexs.add(index);
            //由于序列号是从0开始的.
            //当前条序号+1 = 总序数
//            if (Integer.valueOf(index).intValue() + 1 == Integer.valueOf(total).intValue()) {
//                isture = true;
//            }
        }
        if (Integer.parseInt(total) == currentIndexs.size()) {
            isture = true;
        } else {
            isture = false;
        }
        mCursor.close();
        return isture;
    }

    /**
     * 1.首先根据lineID进行分组
     *
     * @param
     * @return
     * @throws SQLException
     */
    public BDLineNav get(String lineID) throws SQLException {
        //查询
        Cursor mCursor = sqliteDatabase.query(true, LineNavColumns.TABLE_NAME,
                new String[]{LineNavColumns._ID, LineNavColumns.NAV_LINE_ID, LineNavColumns.CREATED_TIME, LineNavColumns.CURRENT_INDEX,
                        LineNavColumns.TOTAL_NUMBER, LineNavColumns.PASS_POINT}, LineNavColumns.NAV_LINE_ID + "='" + lineID + "'", null, null, null, null, null, null);
        BDLineNav mBDLineNav = new BDLineNav();
        mBDLineNav.setLineId(lineID);
        ArrayList<BDPoint> passPoints = new ArrayList<BDPoint>();
        while (mCursor.moveToNext()) {
            String passPointStr = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.PASS_POINT));
            if (passPointStr != null && !"".equals(passPointStr)) {
                String temp[] = passPointStr.split(",");
                for (int i = 0; i < temp.length / 2; i++) {
                    BDPoint mBDPoint = new BDPoint();
                    mBDPoint.setLon(Utils.lonStr2Double(temp[i * 2]));
                    mBDPoint.setLonDirection(Utils.getLonDirection(temp[i * 2]));
                    mBDPoint.setLat(Utils.latStr2Double(temp[i * 2 + 1]));
                    mBDPoint.setLatDirection(Utils.getLatDirection(temp[i * 2 + 1]));
                    passPoints.add(mBDPoint);
                }
            }
        }
        mBDLineNav.setPassPoints(passPoints);
        mCursor.close();
        return mBDLineNav;
    }


    // 获取线路id
    public List<String> getNavLineIds() throws SQLException {


        Cursor mCursor = sqliteDatabase.query(
                true,
                LineNavColumns.TABLE_NAME,
                new String[]{
                        LineNavColumns._ID,
                        LineNavColumns.NAV_LINE_ID,
                        LineNavColumns.CREATED_TIME,
                        LineNavColumns.CURRENT_INDEX,
                        LineNavColumns.TOTAL_NUMBER,
                        LineNavColumns.PASS_POINT},
                //null, null,LineNavColumns.NAV_LINE_ID,null,null,null);
                null, null, null, null, null, null, null);

        List<String> navList = new ArrayList<String>();
        while (mCursor.moveToNext()) {

            String lineID = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.NAV_LINE_ID));
            navList.add(lineID);

        }

        return navList;

    }

    public List<BDPoint> getNavLinePassPointsByLineId(String navLineId, String currentIndex) throws SQLException {

        //String where = LineNavColumns.NAV_LINE_ID + "= ? ";
        String where = LineNavColumns.NAV_LINE_ID + "= ? and " + LineNavColumns.CURRENT_INDEX + " = ? ";
        Cursor mCursor = sqliteDatabase.query(
                true,
                LineNavColumns.TABLE_NAME,
                new String[]{
                        LineNavColumns._ID,
                        LineNavColumns.NAV_LINE_ID,
                        LineNavColumns.CREATED_TIME,
                        LineNavColumns.CURRENT_INDEX,
                        LineNavColumns.TOTAL_NUMBER,
                        LineNavColumns.PASS_POINT},
                //null, null,LineNavColumns.NAV_LINE_ID,null,null,null);
                where, new String[]{navLineId, currentIndex}, null, null, null, null, null);

        List<BDPoint> passPoints = new ArrayList<BDPoint>();
        // String time = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.KEY_CREATED_TIME));

        // Toast.makeText(context, "............"+time, Toast.LENGTH_SHORT).show();
        while (mCursor.moveToNext()) {

            String passPointStr = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.PASS_POINT));
            if (passPointStr != null && !"".equals(passPointStr)) {
                String temp[] = passPointStr.split(",");
                for (int i = 0; i < temp.length / 2; i++) {
                    BDPoint mBDPoint = new BDPoint();
                    mBDPoint.setLon(Utils.lonStr2Double(temp[i * 2]));
                    mBDPoint.setLonDirection(Utils.getLonDirection(temp[i * 2]));
                    mBDPoint.setLat(Utils.latStr2Double(temp[i * 2 + 1]));
                    mBDPoint.setLatDirection(Utils.getLatDirection(temp[i * 2 + 1]));
                    passPoints.add(mBDPoint);
                }
            }

        }

        return passPoints;

    }

    /**
     * @param
     * @return
     * @throws SQLException
     */
//    public List<BDLineNav> getNavLineList() throws SQLException {
//        //查询
//        Cursor mCursor = sqliteDatabase.query(
//                true,
//                LineNavColumns.TABLE_NAME,
//                new String[]{
//                        LineNavColumns._ID,
//                        LineNavColumns.NAV_LINE_ID,
//                        LineNavColumns.CURRENT_INDEX,
//                        LineNavColumns.TOTAL_NUMBER,
//                        LineNavColumns.PASS_POINT},
//                //null, null,LineNavColumns.NAV_LINE_ID,null,null,null);
//                null, null, null, null, null, null);
//
//        List<BDLineNav> navList = new ArrayList<BDLineNav>();
//
//        while (mCursor.moveToNext()) {
//
//            BDLineNav mBDLineNav = new BDLineNav();
//            List<BDPoint> passPoints = new ArrayList<BDPoint>();
//            /**
//             * 路线ID
//             */
//            String lineID = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.NAV_LINE_ID));
//            mBDLineNav.setLineId(lineID);
//            /**
//             * 当前索引
//             */
//            int currentIndex = mCursor.getInt(mCursor.getColumnIndex(LineNavColumns.CURRENT_INDEX));
//            /**
//             * 总数目
//             */
//            int totalCount = mCursor.getInt(mCursor.getColumnIndex(LineNavColumns.TOTAL_NUMBER));
//            /**
//             * 获得所有的必经点
//             */
//            String passPointStr = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.PASS_POINT));
//            if (passPointStr != null && !"".equals(passPointStr)) {
//                String temp[] = passPointStr.split(",");
//                for (int i = 0; i < temp.length / 2; i++) {
//                    BDPoint mBDPoint = new BDPoint();
//                    mBDPoint.setLon(Utils.lonStr2Double(temp[i * 2]));
//                    mBDPoint.setLonDirection(Utils.getLonDirection(temp[i * 2]));
//                    mBDPoint.setLat(Utils.latStr2Double(temp[i * 2 + 1]));
//                    mBDPoint.setLatDirection(Utils.getLatDirection(temp[i * 2 + 1]));
//                    passPoints.add(mBDPoint);
//                }
//            }
//
//            mBDLineNav.setPassPoints(passPoints);
//            navList.add(mBDLineNav);
//
////            /**
////             * 只保存最新数据
////             *
////             */
////            if ((currentIndex + 1) == totalCount) {//标识当前是最后一条
////                //输入该条语句
////                mBDLineNav.setPassPoints(passPoints);
////                navList.add(mBDLineNav);
////                //重新创建对象
////                passPoints = new ArrayList<BDPoint>();
////                mBDLineNav = new BDLineNav();
////            }
//
//        }
//        mCursor.close();
//        return navList;
//    }
    public int getSize() throws SQLException {
        //查询
        Cursor mCursor = sqliteDatabase.query(true, LineNavColumns.TABLE_NAME,
                new String[]{LineNavColumns._ID, LineNavColumns.NAV_LINE_ID, LineNavColumns.CREATED_TIME, LineNavColumns.CURRENT_INDEX,
                        LineNavColumns.TOTAL_NUMBER, LineNavColumns.PASS_POINT}, null, null, null, null, null, null, null);
        return mCursor.getCount();
    }


    public void close() {
        databaseHelper.close();
        sqliteDatabase.close();
    }


    //更新数据库 数据

    /**
     * 增加北斗路线导航
     *
     * @param lineId       线路id
     * @param currentIndex 当前条序号
     * @param totalNum     总序号
     * @param passPointStr 途经点
     * @return
     */
    public long update(String lineId, String currentIndex, String totalNum, String passPointStr, String createTime) {
        Log.d(TAG, "update: lineId=" + lineId + "currentIndex=" + currentIndex + "totalNum=" + totalNum + "passPointStr=" + passPointStr);

        //先删除
        delete(lineId, currentIndex);
        //再插入
        long insertID = insert(lineId, currentIndex, totalNum, passPointStr, createTime);
        return insertID;
    }

    //得到所有的条序号
    public List<Integer> getNavLineCurrentIndexsByLineId(String navLineId) {


        String where = LineNavColumns.NAV_LINE_ID + "= ? ";
        Cursor mCursor = sqliteDatabase.query(
                true,
                LineNavColumns.TABLE_NAME,
                new String[]{
                        LineNavColumns._ID,
                        LineNavColumns.NAV_LINE_ID,
                        LineNavColumns.CREATED_TIME,
                        LineNavColumns.CURRENT_INDEX,
                        LineNavColumns.TOTAL_NUMBER,
                        LineNavColumns.PASS_POINT},
                //null, null,LineNavColumns.NAV_LINE_ID,null,null,null);
                where, new String[]{navLineId}, null, null, null, null, null);

        List<Integer> navList = new ArrayList<>();

        while (mCursor.moveToNext()) {

            String currentIndex = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.CURRENT_INDEX));
            //转为 int
            int i = Integer.parseInt(currentIndex);
            navList.add(i);

        }

        return navList;

    }

    public String getCreateTimeByLineId(String navLineId) {


        String createTime = "";
        String where = LineNavColumns.NAV_LINE_ID + "= ? ";
        Cursor mCursor = sqliteDatabase.query(
                true,
                LineNavColumns.TABLE_NAME,
                new String[]{
                        LineNavColumns._ID,
                        LineNavColumns.NAV_LINE_ID,
                        LineNavColumns.CREATED_TIME,
                        LineNavColumns.CURRENT_INDEX,
                        LineNavColumns.TOTAL_NUMBER,
                        LineNavColumns.PASS_POINT},
                where, new String[]{navLineId}, null, null, null, null);


        if (mCursor.moveToNext()) {

            createTime = mCursor.getString(mCursor.getColumnIndex(LineNavColumns.CREATED_TIME));

        }


        return createTime;

    }
}
