package thd.bd.sms.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SatelliteInfo implements Parcelable {
    public int Number = 0;
    public double mElevation = 0.0D;
    public double mAzimuth = 0.0D;
    public double SNR = 0.0D;
    public boolean mUsedInFix;
    public static final Creator<SatelliteInfo> CREATOR = new Creator() {
        public SatelliteInfo createFromParcel(Parcel in) {
            SatelliteInfo info = new SatelliteInfo();
            info.Number = in.readInt();
            info.mElevation = in.readDouble();
            info.mAzimuth = in.readDouble();
            info.SNR = in.readDouble();
            boolean[] fix = new boolean[1];
            in.readBooleanArray(fix);
            info.mUsedInFix = fix[0];
            return info;
        }

        public SatelliteInfo[] newArray(int size) {
            return new SatelliteInfo[size];
        }
    };

    public SatelliteInfo() {
    }

    public int getNumber() {
        return this.Number;
    }

    public void setNumber(int number) {
        this.Number = number;
    }

    public double getmElevation() {
        return this.mElevation;
    }

    public void setmElevation(double mElevation) {
        this.mElevation = mElevation;
    }

    public double getmAzimuth() {
        return this.mAzimuth;
    }

    public void setmAzimuth(double mAzimuth) {
        this.mAzimuth = mAzimuth;
    }

    public double getSNR() {
        return this.SNR;
    }

    public void setSNR(double sNR) {
        this.SNR = sNR;
    }

    public static Creator<SatelliteInfo> getCreator() {
        return CREATOR;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.Number);
        dest.writeDouble(this.mElevation);
        dest.writeDouble(this.mAzimuth);
        dest.writeDouble(this.SNR);
        boolean[] fix = new boolean[]{this.mUsedInFix};
        dest.writeBooleanArray(fix);
    }
}
