package thd.bd.sms.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 北斗指令导航和路径导航的实体类
 *
 * @author steve
 */
public class BDPoint implements Parcelable {

    /**
     * 经度
     */
    private double lon;

    /**
     * 经度方向
     */
    private String lonDirection;

    /**
     * 纬度
     */
    private double lat;

    /**
     * 纬度方向
     */
    private String latDirection;


    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return lon + "," + lonDirection + "," + lat + "," + latDirection;
    }

    public String fomatString() {

        String formatLon = String.format("%.6f", lon);
        String formatLat = String.format("%.6f", lat);
        return formatLon + "," + lonDirection + "," + formatLat + "," + latDirection;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lon);
        dest.writeString(this.lonDirection);
        dest.writeDouble(this.lat);
        dest.writeString(this.latDirection);
    }

    public BDPoint() {
    }

    protected BDPoint(Parcel in) {
        this.lon = in.readDouble();
        this.lonDirection = in.readString();
        this.lat = in.readDouble();
        this.latDirection = in.readString();
    }

    public static final Creator<BDPoint> CREATOR = new Creator<BDPoint>() {
        @Override
        public BDPoint createFromParcel(Parcel source) {
            return new BDPoint(source);
        }

        @Override
        public BDPoint[] newArray(int size) {
            return new BDPoint[size];
        }
    };
}
