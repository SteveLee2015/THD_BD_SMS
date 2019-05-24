package thd.bd.sms.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thd.bd.sms.bean.BDContactColumn;


/**
 * 操作db的相关帮助类
 * @author llg052
 *
 */
public class DBhelper {
	
	/**
	 * 根据 号码 或 内容 模糊
	 * 查询联系人数据库
	 */
	public static List<Map<String,Object>> queryContactDB(Context mContext, String content) {
		Cursor phoneCursor=null;
		List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
		if("".equals(content)){
			return null;
		}

		if(Utils.isNumber(content)){
			Cursor phones=mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.NUMBER+" like '%"+content+"%'", null, null);
			while(phones.moveToNext()){
				Map<String,Object> map=new HashMap<String,Object>();
				//获取联系人ID
				String contactId=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				String phoneNumber=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				Cursor displayNameCursor=mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,ContactsContract.Contacts._ID+"="+contactId,null, null);
				String name="";
				if(displayNameCursor.moveToNext()){
					//获取联系人的名称
					name=displayNameCursor.getString(displayNameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				}
				map.put(BDContactColumn._ID, contactId);
				map.put(BDContactColumn.USER_NAME, name);
				map.put(BDContactColumn.CARD_NUM, phoneNumber);
				if(phoneNumber!=null&&(!"".equals(phoneNumber))){
					mList.add(map);
				}
			}
			phones.close();
		}
		return mList;
	}

	/**
	 * 根据 号码 查询联系人
	 * @param context
	 * @param phoneNum 手机号
     * @return
     */
	public static String getContactNameFromPhoneBook(Context context, String phoneNum) {
	    String contactName = "";
	    ContentResolver cr = context.getContentResolver();
	    Cursor pCur = cr.query(
	            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
	            ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
	            new String[] { phoneNum }, null);
	    if (pCur.moveToFirst()) {  
	        contactName = pCur  
	                .getString(pCur  
	                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
	        pCur.close();  
	    }  
	    return contactName;  
	}  

}
