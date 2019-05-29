package thd.bd.sms.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import thd.bd.sms.base.DatabaseHelper;
import thd.bd.sms.base.DatabaseHelper.RDCacheColumns;
import thd.bd.sms.bean.BDCache;


/**
 * rd缓存数据操作类
 *
 * @author llg052
 */
public class RDCacheOperation {

    //	public static final String KEY_TITLE = "title";
//	public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ROW_TAG = "TAG";
//	public static final String KEY_CREATED = "created";

    private DatabaseHelper databaseHelper;
    private Context context;
//	private SQLiteDatabase sqliteDatabase;

    public RDCacheOperation(Context mContext) {
        this.context = mContext;
        databaseHelper = new DatabaseHelper(context);
//		sqliteDatabase = databaseHelper.getWritableDatabase();
        //sqliteDatabase = SQLiteDatabase.openOrCreateDatabase(file, MODE_PROVITE);
    }

    /**
     * 增加一条缓存
     *
     * @param cache
     * @return
     */
    public long insert(BDCache cache) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RDCacheColumns.COLUMNS_SEND_ADDRESS, cache.getSendAddress());
        contentValues.put(RDCacheColumns.COLUMNS_MSG_TYPE, cache.getMsgType());
        contentValues.put(RDCacheColumns.COLUMNS_PRIORITY, cache.getPriority());
        //contentValues.put(RDCacheColumns.COLUMNS_TAG ,cache.getTag());
        contentValues.put(RDCacheColumns.COLUMNS_MSG_CONTENT, cache.getMsgContent());
        contentValues.put(RDCacheColumns.COLUMNS_WORD, cache.getCacheContent());
        long id = database.insert(RDCacheColumns.TABLE_NAME, null, contentValues);
        closeDB(database);
        return id;
    }

    /**
     * 获取所有
     *
     * @return
     * @throws SQLException ASC
     *                      DESC
     */
    public List<BDCache> getAll() throws SQLException {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String orderBy = RDCacheColumns.COLUMNS_PRIORITY + " ASC";
        //String orderBy = RDCacheColumns.COLUMNS_PRIORITY + " DESC";
        // 查询
        Cursor mCursor = database.query(
                true,
                RDCacheColumns.TABLE_NAME,
                new String[]{
                        RDCacheColumns._ID,
                        RDCacheColumns.COLUMNS_SEND_ADDRESS,
                        RDCacheColumns.COLUMNS_MSG_TYPE,
                        RDCacheColumns.COLUMNS_PRIORITY,
                        //RDCacheColumns.COLUMNS_TAG,
                        RDCacheColumns.COLUMNS_MSG_CONTENT,
                        RDCacheColumns.COLUMNS_WORD},
                null,
                null,
                null,
                null,
                orderBy,
                null);
        List<BDCache> list = new ArrayList<BDCache>();
        while (mCursor.moveToNext()) {
            BDCache cache = new BDCache();
            cache.setId(Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(RDCacheColumns._ID))));
            cache.setSendAddress(mCursor.getString(mCursor.getColumnIndex(RDCacheColumns.COLUMNS_SEND_ADDRESS)));
            cache.setMsgType(mCursor.getString(mCursor.getColumnIndex(RDCacheColumns.COLUMNS_MSG_TYPE)));
            cache.setPriority(mCursor.getInt(mCursor.getColumnIndex(RDCacheColumns.COLUMNS_PRIORITY)));
            //cache.setTag(mCursor.getString(mCursor.getColumnIndex(RDCacheColumns.COLUMNS_TAG)));
            cache.setMsgContent(mCursor.getString(mCursor.getColumnIndex(RDCacheColumns.COLUMNS_MSG_CONTENT)));
            cache.setCacheContent(mCursor.getString(mCursor.getColumnIndex(RDCacheColumns.COLUMNS_WORD)));
            list.add(cache);
        }
        mCursor.close();
        closeDB(database);
        return list;
    }

    /**
     * 获取数据库数据条数
     *
     * @return
     */
    public int getCount() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        //查询
        Cursor mCursor = database.query(true, RDCacheColumns.TABLE_NAME,
                new String[]{
                        RDCacheColumns._ID,
                        RDCacheColumns.COLUMNS_SEND_ADDRESS,
                        RDCacheColumns.COLUMNS_MSG_TYPE,
                        RDCacheColumns.COLUMNS_PRIORITY,
                        //RDCacheColumns.COLUMNS_TAG,
                        RDCacheColumns.COLUMNS_MSG_CONTENT,
                        RDCacheColumns.COLUMNS_WORD},
                null, null, null, null, null, null);
        int count = mCursor.getCount();
        mCursor.close();
        closeDB(database);
        return count;
    }

    /**
     * 获取首条数据
     *
     * @return
     * @throws SQLException
     */
    public BDCache getFirst() throws SQLException {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        //String orderBy = RDCacheColumns.COLUMNS_PRIORITY + " DESC";
        String orderBy = RDCacheColumns.COLUMNS_PRIORITY + " ASC";
        Cursor mCursor = database.query(
                false,
                RDCacheColumns.TABLE_NAME,
                new String[]{
                        RDCacheColumns._ID,
                        RDCacheColumns.COLUMNS_SEND_ADDRESS,
                        RDCacheColumns.COLUMNS_MSG_TYPE,
                        RDCacheColumns.COLUMNS_PRIORITY,
                        //RDCacheColumns.COLUMNS_TAG,
                        RDCacheColumns.COLUMNS_MSG_CONTENT,
                        RDCacheColumns.COLUMNS_WORD},
                null, null, null, null, orderBy, null);
        if (mCursor == null) {
            closeDB(database);
            return null;
        } else {
            boolean istrue = mCursor.moveToFirst();
            if (istrue) {
                BDCache cache = new BDCache();
                cache.setId(Integer.parseInt(mCursor.getString(mCursor
                        .getColumnIndex(RDCacheColumns._ID))));
                cache.setSendAddress(mCursor.getString(mCursor
                        .getColumnIndex(RDCacheColumns.COLUMNS_SEND_ADDRESS)));
                cache.setMsgType(mCursor.getString(mCursor
                        .getColumnIndex(RDCacheColumns.COLUMNS_MSG_TYPE)));
                cache.setPriority(mCursor.getInt(mCursor
                        .getColumnIndex(RDCacheColumns.COLUMNS_PRIORITY)));
//				cache.setTag(mCursor.getString(mCursor
//						.getColumnIndex(RDCacheColumns.COLUMNS_TAG)));
                cache.setMsgContent(mCursor.getString(mCursor
                        .getColumnIndex(RDCacheColumns.COLUMNS_MSG_CONTENT)));
                cache.setCacheContent(mCursor.getString(mCursor
                        .getColumnIndex(RDCacheColumns.COLUMNS_WORD)));
                mCursor.close();
                closeDB(database);
                return cache;
            } else {
                mCursor.close();
                closeDB(database);
                return null;
            }
        }
    }

    /**
     * 按id删除
     *
     * @param rowId
     * @return
     */
    public boolean delete(long rowId) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        boolean result = database.delete(RDCacheColumns.TABLE_NAME, KEY_ROWID + "="
                + rowId, null) > 0;
        closeDB(database);
        return result;
    }

    /**
     * 按id删除
     *
     * @param tag
     * @return
     */
    public boolean delete(String tag) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        boolean result = database.delete(RDCacheColumns.TABLE_NAME, KEY_ROW_TAG + "="
                + tag, null) > 0;
        closeDB(database);
        return result;
    }

    /**
     * 按对象删除
     *
     * @param cache
     * @return
     */
    public boolean delete(BDCache cache) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int delete = database.delete(RDCacheColumns.TABLE_NAME, KEY_ROWID + "=" + cache.getId(), null);
        boolean result = delete > 0;
        closeDB(database);
        return result;
    }

    /**
     * 删除所有的
     *
     * @return
     */
    public boolean deleteAll() {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        boolean result = database.delete(RDCacheColumns.TABLE_NAME, null, null) > 0;
        closeDB(database);
        return result;
    }

    //	/**
//	 * 关闭数据库
//	 */
//	public void close() {
//		if (databaseHelper != null) {
//			databaseHelper.close();
//		}
//		if (sqliteDatabase != null) {
//			sqliteDatabase.close();
//		}
//	}
    public void closeDB(SQLiteDatabase database) {
        if (database != null) {
            database.close();
        }
    }
}
