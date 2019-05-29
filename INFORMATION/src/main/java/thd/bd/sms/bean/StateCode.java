package thd.bd.sms.bean;

/**
 * Created by llg on 2016/11/30.
 */

public class StateCode {

    private String msgContent;//消息内容
    private int msgCongentOrder; //消息序号
    private long rowId; //id 主键自增


    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgCongentOrder() {
        return msgCongentOrder;
    }

    public void setMsgCongentOrder(int msgCongentOrder) {
        this.msgCongentOrder = msgCongentOrder;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }
}
