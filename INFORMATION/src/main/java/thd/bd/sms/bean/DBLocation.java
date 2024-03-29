package thd.bd.sms.bean;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asdfg on 2017/5/4.
 */

public class DBLocation implements Parcelable {

    private double mLatitude;
    private double mLongitude;
    private long mTime;
    private double mAltitude;
    public String mHeightUnit;
    public double mGroundSpeed; //地面速度 米/秒
    public double mGroundDirection; //地面航向

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    private String timeStr;

    public DBLocation() {

    }

    public DBLocation(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mTime = location.getTime();
        mAltitude = location.getAltitude();
    }

    public DBLocation(Parcel in) {
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mTime = in.readLong();
        mAltitude = in.readDouble();
        mHeightUnit = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeLong(mTime);
        dest.writeDouble(mAltitude);
        dest.writeString(mHeightUnit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DBLocation> CREATOR = new Creator<DBLocation>() {
        @Override
        public DBLocation createFromParcel(Parcel in) {
            return new DBLocation(in);
        }

        @Override
        public DBLocation[] newArray(int size) {
            return new DBLocation[size];
        }
    };

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    /**
     * Get the latitude, in degrees.
     * <p>
     * <p>All locations generated by the {@link LocationManager}
     * will have a valid latitude.
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Set the latitude, in degrees.
     */
    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    /**
     * Get the longitude, in degrees.
     * <p>
     * <p>All locations generated by the {@link LocationManager}
     * will have a valid longitude.
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Set the longitude, in degrees.
     */
    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getAltitude() {
        return mAltitude;
    }

    /**
     * Set the altitude, in meters above the WGS 84 reference ellipsoid.
     * <p>
     * <p>Following this call will return true.
     */
    public void setAltitude(double altitude) {
        mAltitude = altitude;
    }

}
