package thd.bd.sms.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 北斗通讯
 * @author steven
 *
 */


@Entity
public class BDMsgBean {

	@Id
	private Long id;
	private String columnsUserAddress=""; //用户地址
	private String columnsMsgType="";//消息类别
	private String columnsSendAddress="";//发送地址
	private String columnsSendTime=""; //发送时间
	private String columnsMsgLen="";//电文长度
	private String columnsMsgContent="";//电文长度
	private String columnsCrc="";
	private String columnsMsgFlag="";//短信标识  0-收件箱  1-发件箱  2-草稿箱  3-表示未读

	@Generated(hash = 2002722602)
	public BDMsgBean(Long id, String columnsUserAddress, String columnsMsgType,
			String columnsSendAddress, String columnsSendTime, String columnsMsgLen,
			String columnsMsgContent, String columnsCrc, String columnsMsgFlag) {
		this.id = id;
		this.columnsUserAddress = columnsUserAddress;
		this.columnsMsgType = columnsMsgType;
		this.columnsSendAddress = columnsSendAddress;
		this.columnsSendTime = columnsSendTime;
		this.columnsMsgLen = columnsMsgLen;
		this.columnsMsgContent = columnsMsgContent;
		this.columnsCrc = columnsCrc;
		this.columnsMsgFlag = columnsMsgFlag;
	}
	@Generated(hash = 43066694)
	public BDMsgBean() {
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getColumnsUserAddress() {
		return columnsUserAddress;
	}
	public void setColumnsUserAddress(String columnsUserAddress) {
		this.columnsUserAddress = columnsUserAddress;
	}
	public String getColumnsMsgType() {
		return columnsMsgType;
	}
	public void setColumnsMsgType(String columnsMsgType) {
		this.columnsMsgType = columnsMsgType;
	}
	public String getColumnsSendAddress() {
		return columnsSendAddress;
	}
	public void setColumnsSendAddress(String columnsSendAddress) {
		this.columnsSendAddress = columnsSendAddress;
	}
	public String getColumnsSendTime() {
		return columnsSendTime;
	}
	public void setColumnsSendTime(String columnsSendTime) {
		this.columnsSendTime = columnsSendTime;
	}
	public String getColumnsMsgLen() {
		return columnsMsgLen;
	}
	public void setColumnsMsgLen(String columnsMsgLen) {
		this.columnsMsgLen = columnsMsgLen;
	}
	public String getColumnsMsgContent() {
		return columnsMsgContent;
	}
	public void setColumnsMsgContent(String columnsMsgContent) {
		this.columnsMsgContent = columnsMsgContent;
	}
	public String getColumnsCrc() {
		return columnsCrc;
	}
	public void setColumnsCrc(String columnsCrc) {
		this.columnsCrc = columnsCrc;
	}
	public String getColumnsMsgFlag() {
		return columnsMsgFlag;
	}
	public void setColumnsMsgFlag(String columnsMsgFlag) {
		this.columnsMsgFlag = columnsMsgFlag;
	}
}
