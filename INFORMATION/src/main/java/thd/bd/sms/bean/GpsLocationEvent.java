package thd.bd.sms.bean;

import android.location.Location;

/**
 * Created by asdfg on 2017/6/21.
 */

public class GpsLocationEvent {
    public Location mLocation;
    public GpsLocationEvent(Location location){
        mLocation = location;
    }

}
