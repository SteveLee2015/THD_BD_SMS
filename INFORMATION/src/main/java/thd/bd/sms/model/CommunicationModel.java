package thd.bd.sms.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.List;
import thd.bd.sms.database.DataBaseHelper.BDMessageColumns;

import thd.bd.sms.bean.BDMsgBean;
import thd.bd.sms.contract.CommunicationContract;
import thd.bd.sms.database.BDMsgBeanDao;
import thd.bd.sms.database.DataBaseHelper;

public class CommunicationModel implements CommunicationContract.Model {

    private BDMsgBeanDao bdMsgBeanDao;
    private List<BDMsgBean> messagesList;
    private SQLiteDatabase sqliteDatabase;


    @Override
    public List<BDMsgBean> loadAllMessages(Context context,int page) {
        /*String orderBy = DataBaseHelper.BDMessageColumns._ID + " desc";
        //查询
        Cursor mCursor = sqliteDatabase.query(true, BDMessageColumns.TABLE_NAME, new String[]{BDMessageColumns._ID, BDMessageColumns.COLUMNS_USER_ADDRESS,
                BDMessageColumns.COLUMNS_MSG_TYPE, BDMessageColumns.COLUMNS_USER_NAME,
                BDMessageColumns.COLUMNS_SEND_TIME, BDMessageColumns.COLUMNS_MSG_LEN,
                BDMessageColumns.COLUMNS_MSG_CONTENT, BDMessageColumns.COLUMNS_CRC,
                BDMessageColumns.COLUMNS_FLAG
        }, null, null, BDMessageColumns.COLUMNS_USER_ADDRESS, null, orderBy, null);
        return mCursor;*/
        return null;
    }

}
