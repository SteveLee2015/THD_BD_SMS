package thd.bd.sms.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化工具
 * @author lerry
 *
 */
public class DateUtils {

	public static String getDateTimeStr(long time) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(time);
		String timeStr = format.format(date);
		return timeStr;
	}

	public static String getRNDateTimeStr(long time) {

		//SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss.ss");
		SimpleDateFormat format = new SimpleDateFormat("HHmmss.ss");
		Date date = new Date(time);
		String timeStr = format.format(date);
		return timeStr;
	}

	public static String getDateTime(long time) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		String timeStr = format.format(date);
		return timeStr;
	}
	
}
