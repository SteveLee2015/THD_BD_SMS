package thd.bd.sms.bean;

/**
 * Created by asdfg on 2017/6/21.
 */

public class SatelliteInfoEvent {
    public SatelliteInfo[] dbSatelliteStatus;
    public SatelliteInfoEvent(SatelliteInfo[]  satelliteInfos){
        dbSatelliteStatus = satelliteInfos;
    }
}
