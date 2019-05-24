package thd.bd.sms.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import thd.bd.sms.bean.DBLocation;
import thd.bd.sms.bean.GpsLocationEvent;
import thd.bd.sms.bean.SatelliteInfo;
import thd.bd.sms.bean.SatelliteInfoEvent;

/**
 * Created by asdfg on 2017/5/23.
 */

public class GspStatesManager {
    private BroadcastReceiver mSatliteReceiver;
    protected static Hashtable<Integer, SatelliteInfo> mGpsMap = new Hashtable<Integer, SatelliteInfo>();
    protected static ArrayList<SatelliteInfo> gpslist;
    private long time = 0;
    private static int[] mBDFixNumberArray = new int[33];
    private static int[] mGPSFixNumberArray = new int[33];
    SharedPreferences mSharePref;
    public static Context mContext;

    public DBLocation mLocation;
    ArrayList<WeakReference<ISatellitesListener>> mSatellitesListeners = new ArrayList<WeakReference<ISatellitesListener>>();
    ArrayList<WeakReference<ILocationListener>> mLocationListeners = new ArrayList<WeakReference<ILocationListener>>();
    LocationManager locationManager ;
    public int beamnumber = 0;
    private GspStatesManager() {
//        mSharePref = mContext.getSharedPreferences("BD_BLUETOOTH_PREF", 0);
        EventBus.getDefault().register(this);
    }

    public static GspStatesManager getInstance() {
        return InnerState.instance;
    }


    private static class InnerState {
        public static GspStatesManager instance = new GspStatesManager();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(GpsLocationEvent event){
        if(event != null && event.mLocation != null){
            if (mLocation == null)
                mLocation = new DBLocation();
            mLocation.setLatitude(event.mLocation.getLatitude());
            mLocation.setLongitude(event.mLocation.getLongitude());
            mLocation.setAltitude(event.mLocation.getAltitude());
            mLocation.setTime(event.mLocation.getTime());
            mLocation.mHeightUnit = "M";
            mLocation.mGroundSpeed =event.mLocation.getSpeed();
            mLocation.mGroundDirection = event.mLocation.getBearing();
            onLocationListener(mLocation);
            updateRnss(mLocation);
        }

    }

    public interface ISatellitesListener {
        void onSatelliteInfo(ArrayList<SatelliteInfo> list);

        void onUpdateRnss(DBLocation location);
    }

    public interface ILocationListener {
        void onLocationChanged(DBLocation location);
    }

    public synchronized void addSatellitesListener(ISatellitesListener listener) {
        mSatellitesListeners.add(new WeakReference<ISatellitesListener>(listener));
    }

    public synchronized void addLocationListener(ILocationListener listener) {
        mLocationListeners.add(new WeakReference<ILocationListener>(listener));
    }

    public synchronized void removeStatellitesListener(ISatellitesListener listener) {
        WeakReference<ISatellitesListener> tag = null;
        for (WeakReference<ISatellitesListener> reference : mSatellitesListeners) {
            if (reference.get() == listener) {
                tag = reference;
                break;
            }
        }
        if (tag != null) {
            mSatellitesListeners.remove(tag);
        }
    }

    public synchronized void removeLocationListener(ILocationListener listener) {
        WeakReference<ILocationListener> tag = null;
        for (WeakReference<ILocationListener> reference : mLocationListeners) {
            if (reference.get() == listener) {
                tag = reference;
                break;
            }
        }
        if (tag != null) {
            mLocationListeners.remove(tag);
        }
    }

    private void onLocationListener(DBLocation location) {
        ILocationListener listener = null;
        for (WeakReference<ILocationListener> reference : mLocationListeners) {
            listener = reference.get();
            if (listener != null) {
                listener.onLocationChanged(location);
            }
        }
    }

    private void onSatlitesStatus(ArrayList<SatelliteInfo> list) {
        ISatellitesListener listener = null;
        for (WeakReference<ISatellitesListener> reference : mSatellitesListeners) {
            listener = reference.get();
            if (listener != null) {
                listener.onSatelliteInfo(list);
            }
        }

    }

    private void updateRnss(DBLocation gga) {
        ISatellitesListener listener = null;
        for (WeakReference<ISatellitesListener> reference : mSatellitesListeners) {
            listener = reference.get();
            if (listener != null) {
                listener.onUpdateRnss(gga);
            }
        }
    }

    public synchronized void clearData() {
        beamnumber = 0;
        mGpsMap.clear();
        gpslist.clear();
        onSatlitesStatus(gpslist);
    }

    public int getEnableCount() {
        int count = 0;
        if (gpslist != null && gpslist.size() > 0) {
            for (SatelliteInfo info : gpslist) {
                if (info != null && info.mUsedInFix == true) {
                    count++;
                }
            }
        }
        return count;
    }

    public void onTerminate() {
        EventBus.getDefault().unregister(this);
    }

}
