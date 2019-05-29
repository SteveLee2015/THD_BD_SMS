package thd.bd.sms.bean;

/**
 * 缓存数据库实体bean
 * @author llg052
 *
 */
public class BDCache {
	
	public static final String SMS_FLAG = "SMS_FLAG";//短报文
	public static final String SMS_CONTINUE_FLAG = "SMS_CONTINUE_FLAG";//短报文连续
	public static final String RD_REPORT_FLAG = "RD_REPORT_FLAG";//rd位置报告
	public static final String RD_REPORT_CONTINUE_FLAG = "RD_REPORT_CONTINUE_FLAG";//rd连续位置报告
	public static final String RN_REPORT_FLAG = "RN_REPORT_FLAG";//rn位置报告
	public static final String RN_REPORT_CONTINUE_FLAG = "RN_REPORT_CONTINUE_FLAG";//rn连续位置报告
	public static final String RD_LOCATION_FLAG = "RD_LOCATION_FLAG";//rd定位申请
	public static final String RD_LOCATION_CONTINUE_FLAG = "RD_LOCATION_CONTINUE_FLAG";//rd连续定位申请
	
	public static final int PRIORITY_MAX = 0;//紧急救援
	public static final int PRIORITY_1 = 100;//最高优先级 定位申请
	public static final int PRIORITY_3 = 300;//次之优先级 短报文 位置报告
	public static final int PRIORITY_5 = 500;//最弱优先级
	
	public static final String TAG_HAS_SEND = "HAS_SEND";//已经发送
	public static final String TAG_NEED_SEND = "TAG_NEED_SEND";//等待发送
	
	private int id;
	private String sendAddress;//发件地址
	private String msgType;//消息类别
	private int priority;//消息类别
	private String tag = TAG_NEED_SEND;//消息发送tag
	private String msgContent;//消息内容
	private String cacheContent;//缓存内容
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSendAddress() {
		return sendAddress;
	}
	public String getMsgType() {
		return msgType;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public String getCacheContent() {
		return cacheContent;
	}
	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public void setCacheContent(String cacheContent) {
		this.cacheContent = cacheContent;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	@Override
	public String toString() {
		return "BDCache [id=" + id + ", sendAddress=" + sendAddress
				+ ", msgType=" + msgType + ", priority=" + priority + ", tag="
				+ tag + ", msgContent=" + msgContent + ", cacheContent="
				+ cacheContent + "]";
	}
}
