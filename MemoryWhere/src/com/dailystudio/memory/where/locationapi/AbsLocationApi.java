package com.dailystudio.memory.where.locationapi;

import android.content.Context;
import android.graphics.Bitmap;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nan on 2015/3/30.
 */
public abstract class AbsLocationApi {

    public static interface LocationChangedListener {

        public void onLocationChanged(double lat, double lon, double alt, long time);

    }

    private Context mAppContext;

    private List<LocationChangedListener> mListeners =
            new ArrayList<LocationChangedListener>();


    public AbsLocationApi(Context context) {
        mAppContext = context;
    }

    public Context getContext() {
        return mAppContext;
    }

    public void addLocationChangedListener(LocationChangedListener l) {
        if (l == null) {
            return;
        }

        mListeners.add(l);
    }

    protected void triggerLocationUpdate(double lat, double lon, double alt, long time) {
        Logger.debug("new loc update: [lat: %f, lon: %f, alt: %f, time: %s]",
                lat, lon, alt, CalendarUtils.timeToReadableString(time));
        if (mListeners == null || mListeners.size() <= 0) {
            return;
        }

        for (LocationChangedListener l: mListeners) {
            l.onLocationChanged(lat, lon, alt, time);
        }
    }

    public abstract MemoryLocation getCurrentLocation();
    public abstract void startTracking();
    public abstract void stopTracking();

    public abstract Bitmap getMapThumbnail(double lat, double lon, double alt);

}
