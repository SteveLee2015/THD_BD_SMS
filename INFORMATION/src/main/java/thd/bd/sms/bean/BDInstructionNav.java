package thd.bd.sms.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 北斗指令导航实体
 *
 * @author steve
 */
public class BDInstructionNav implements Parcelable {

    private long rowId;

    private String lineId;

    /**
     * 发送时间
     */
    private String createTime;

    /**
     * 目的地
     */
    private BDPoint targetPoint;

    /**
     * 必经点
     */
    private List<BDPoint> passPoints;

    /**
     * 规避点
     */
    private List<BDPoint> evadePoints;

    public String userAddress;

    public String userName;

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

    public BDPoint getTargetPoint() {
        return targetPoint;
    }

    public void setTargetPoint(BDPoint targetPoint) {
        this.targetPoint = targetPoint;
    }

    public List<BDPoint> getPassPoints() {
        return passPoints;
    }

    public void setPassPoints(List<BDPoint> passPoints) {
        this.passPoints = passPoints;
    }

    public List<BDPoint> getEvadePoints() {
        return evadePoints;
    }

    public void setEvadePoints(List<BDPoint> evadePoints) {
        this.evadePoints = evadePoints;
    }

    public String getPassPointsString() {
        if (passPoints != null) {
            String str = "";
            for (BDPoint mBDPoint : passPoints) {
                str += mBDPoint.fomatString() + ",";
            }
            return str.substring(0, str.length() - 1);
        } else {
            return null;
        }
    }

    public String getEvadePointsString() {
        if (evadePoints != null) {
            String str = "";
            for (BDPoint mBDPoint : evadePoints) {
                str += (mBDPoint.fomatString() + ",");
            }
            return str.substring(0, str.length() - 1);
        } else {
            return null;
        }
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.rowId);
        dest.writeString(this.lineId);
        dest.writeString(this.createTime);
        dest.writeString(this.userAddress);
        dest.writeString(this.userName);
        dest.writeParcelable(this.targetPoint, flags);
        dest.writeTypedList(this.passPoints);
        dest.writeTypedList(this.evadePoints);
    }

    public BDInstructionNav() {
    }

    protected BDInstructionNav(Parcel in) {
        this.rowId = in.readLong();
        this.lineId = in.readString();
        this.createTime = in.readString();
        userAddress = in.readString();
        userName = in.readString();
        this.targetPoint = in.readParcelable(BDPoint.class.getClassLoader());
        this.passPoints = in.createTypedArrayList(BDPoint.CREATOR);
        this.evadePoints = in.createTypedArrayList(BDPoint.CREATOR);
    }

    public static final Creator<BDInstructionNav> CREATOR = new Creator<BDInstructionNav>() {
        @Override
        public BDInstructionNav createFromParcel(Parcel source) {
            return new BDInstructionNav(source);
        }

        @Override
        public BDInstructionNav[] newArray(int size) {
            return new BDInstructionNav[size];
        }
    };
}
