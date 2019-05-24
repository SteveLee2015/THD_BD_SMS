package thd.bd.sms.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 友邻实体
 * rowId 接收时间  友邻总数  当前序号  当前友邻id  经度  经度方向   纬度  纬度方向
 * @author llg
 *
 */
public class FriendBDPoint implements Parcelable {


	private long rowId;
	
	/**
	 * 经度
	 */
	private String lon;
	
	/**
	 * 经度方向
	 */
	private String lonDirection;
	
	/**
	 * 纬度
	 */
	private String lat;
	
	/**
	 * 纬度方向
	 */
	private String latDirection;
	
	
	/**
	 * 友邻id
	 * @return
	 */
	private String friendID;
	
	/**
	 * 当前的序号
	 */
	private String currentID;
	
	
	
	/**
	 * 接收时间
	 */
	private String receiveTime;
	
	
	/**
	 * 友邻总数
	 */
	private String friendCount;
	
	
	

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}


	public String getLonDirection() {
		return lonDirection;
	}

	public void setLonDirection(String lonDirection) {
		this.lonDirection = lonDirection;
	}

	public String getLatDirection() {
		return latDirection;
	}

	public void setLatDirection(String latDirection) {
		this.latDirection = latDirection;
	}

	public String getFriendID() {
		return friendID;
	}

	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}

	public String getCurrentID() {
		return currentID;
	}

	public void setCurrentID(String currentID) {
		this.currentID = currentID;
	}

	
	
	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}
	
	

	public String getFriendCount() {
		return friendCount;
	}

	public void setFriendCount(String friendCount) {
		this.friendCount = friendCount;
	}
	
	

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}




	@Override
	public String toString() {
		return "FriendBDPoint [rowId=" + rowId + ", lon=" + lon
				+ ", lonDirection=" + lonDirection + ", lat=" + lat
				+ ", latDirection=" + latDirection + ", friendID=" + friendID
				+ ", currentID=" + currentID + ", receiveTime=" + receiveTime
				+ ", friendCount=" + friendCount + "]";
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.rowId);
		dest.writeString(this.lon);
		dest.writeString(this.lonDirection);
		dest.writeString(this.lat);
		dest.writeString(this.latDirection);
		dest.writeString(this.friendID);
		dest.writeString(this.currentID);
		dest.writeString(this.receiveTime);
		dest.writeString(this.friendCount);
	}

	public FriendBDPoint() {
	}

	protected FriendBDPoint(Parcel in) {
		this.rowId = in.readLong();
		this.lon = in.readString();
		this.lonDirection = in.readString();
		this.lat = in.readString();
		this.latDirection = in.readString();
		this.friendID = in.readString();
		this.currentID = in.readString();
		this.receiveTime = in.readString();
		this.friendCount = in.readString();
	}

	public static final Creator<FriendBDPoint> CREATOR = new Creator<FriendBDPoint>() {
		@Override
		public FriendBDPoint createFromParcel(Parcel source) {
			return new FriendBDPoint(source);
		}

		@Override
		public FriendBDPoint[] newArray(int size) {
			return new FriendBDPoint[size];
		}
	};
}
