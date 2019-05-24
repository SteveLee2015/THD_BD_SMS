package thd.bd.sms.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class BDMessageInfo implements Parcelable {
	
	public String userName = "";

	  public int msgType = 0;

	  public String mUserAddress = "";

	  public int msgCharset = 0;

	  public String mSendTime = "";

	  /**modifyed by llg 06-1-6***/
	  public String message = null;
//	  public byte[] message = null;

	  public static final Parcelable.Creator<BDMessageInfo> CREATOR = new Parcelable.Creator() {
	    public BDMessageInfo createFromParcel(Parcel in) {
	      BDMessageInfo bdMessageInfo = new BDMessageInfo();
	      bdMessageInfo.msgType = in.readInt();
	      bdMessageInfo.mUserAddress = in.readString();
	      bdMessageInfo.msgCharset = in.readInt();
	      bdMessageInfo.mSendTime = in.readString();
	      //in.readByteArray(bdMessageInfo.message);
	      bdMessageInfo.message=in.readString();
	      bdMessageInfo.userName = in.readString();
	      return bdMessageInfo;
	    }

	    public BDMessageInfo[] newArray(int size) {
	      return new BDMessageInfo[size];
	    }
	  };

	  public String getMessage() {
		return message;
	  }

	  public void setMessage(String message) {
		this.message = message;
	  }

	  public int getMsgType() {
	    return this.msgType;
	  }

	  public void setMsgType(int msgType) {
	    this.msgType = msgType;
	  }

	  public String getmUserAddress() {
	    return this.mUserAddress;
	  }

	  public void setmUserAddress(String mUserAddress) {
	    this.mUserAddress = mUserAddress;
	  }

	  public int getMsgCharset() {
	    return this.msgCharset;
	  }

	  public void setMsgCharset(int msgCharset) {
	    this.msgCharset = msgCharset;
	  }

	  public String getmSendTime() {
	    return this.mSendTime;
	  }

	  public void setmSendTime(String mSendTime) {
	    this.mSendTime = mSendTime;
	  }

	  public int describeContents() {
	    return 0;
	  }

	  public String getUserName()
	  {
	    return this.userName;
	  }

	  public void setUserName(String userName) {
	    this.userName = userName;
	  }

	  public void writeToParcel(Parcel parcel, int flags) {
	    parcel.writeInt(this.msgType);
	    parcel.writeString(this.mUserAddress);
	    parcel.writeInt(this.msgCharset);
	    parcel.writeString(this.mSendTime);
	    parcel.writeString(this.userName);
	    parcel.writeString(this.message);
	    //parcel.writeByteArray(this.message);
	  }

	  public void dumpInfo() {
	    Log.d("BDRDSS", "============== BDMessageInfo =============");
	    Log.d("BDRDSS", "msgType : " + this.msgType + " , mUserAddress : " +
	      this.mUserAddress + "\n" + "msgCharset : " + this.msgCharset + 
	      " , mSendTime : " + this.mSendTime + "\n" + "message : " + 
	      this.message.toString());
	  }
}
