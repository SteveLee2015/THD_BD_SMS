package thd.bd.sms.bean;

import android.graphics.drawable.Drawable;

/**
 * 收件箱条目的封装对象
 * @author Administrator
 *
 */
public class Item {
	
	
        public String send_id;//发件人id
        public String send_name="未设置";//发件人名称
        public String send_date;//发件时间
        public String send_content;//发送内容
        public Drawable message_flag;//图标
        public String message_flag_not_drawable;//消息标识 0收件  1发件   2草稿   3未读
        public Boolean checked;//是否被选中
        public Long rowId;//_ID

}
