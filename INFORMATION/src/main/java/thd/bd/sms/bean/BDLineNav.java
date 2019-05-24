package thd.bd.sms.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 北斗路线导航实体
 * @author llg
 */
public class BDLineNav implements Parcelable {

	
	private String lineId;



	private String createTime;

	/**
	 * 必经点
	 */
	private ArrayList<BDPoint> passPoints;
	
	
	public String getLineId() {
		return lineId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public ArrayList<BDPoint> getPassPoints() {
		return passPoints;
	}

	public void setPassPoints(ArrayList<BDPoint> passPoints) {
		this.passPoints = passPoints;
	}



	public String getPassPointsString(){
		if(passPoints!=null){
		  String str="";
		  for(BDPoint mBDPoint:passPoints){
			 str+=mBDPoint.fomatString()+","+"\r\n";
		  }
		  return str.substring(0,str.length()-1);
		}else{
		  return null;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.lineId);
		dest.writeString(this.createTime);
		dest.writeTypedList(this.passPoints);
	}

	public BDLineNav() {
	}

	protected BDLineNav(Parcel in) {
		this.lineId = in.readString();
		this.createTime = in.readString();
		this.passPoints = in.createTypedArrayList(BDPoint.CREATOR);
	}

	public static final Creator<BDLineNav> CREATOR = new Creator<BDLineNav>() {
		@Override
		public BDLineNav createFromParcel(Parcel source) {
			return new BDLineNav(source);
		}

		@Override
		public BDLineNav[] newArray(int size) {
			return new BDLineNav[size];
		}
	};
}
