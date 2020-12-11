package com.dailystudio.memory.where.locationapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.dailystudio.app.location.LocationTracker;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;
import com.dailystudio.memory.where.utils.GeoPointUtils;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by nan on 2015/3/30.
 */
public class GoogleLocationApi extends AbsLocationApi {

    private final static String MAP_THUMB_DIMEN = "300x150";
    private final static int MAP_THUMB_SCALE = 2;
    private final static int MAP_ZOOM = 15;

    private LocationTracker mTracker;

    public GoogleLocationApi(Context context) {
        super(context);

        mTracker = new LocationTracker(context);
        mTracker.addLocationChangedListener(mLocationChangedListener);
    }

    @Override
    public MemoryLocation getCurrentLocation() {
        Location loc = mTracker.getLocation();
        if (loc == null) {
            return null;
        }

        return MemoryLocation.createMemoryLocation(getContext(), loc);
    }

    @Override
    public void startTracking() {
        if (mTracker == null) {
            return;
        }

        mTracker.beginLocationTracking();
    }

    @Override
    public void stopTracking() {
        if (mTracker == null) {
            return;
        }

        mTracker.endLocationTracking();
    }

    @Override
    public Bitmap getMapThumbnail(double lat, double lon, double alt){
        final Context context = getContext();
        if (context == null) {
            return null;
        }

        final LatLng latlng = GeoPointUtils.convertToLatLng(context,
                lat, lon);
        final String urlstr =
                "http://maps.google.com/maps/api/staticmap?"
                        + "center="
                        + latlng.latitude + "," + latlng.longitude
                        + "&zoom=" + MAP_ZOOM
                        + "&size=" + MAP_THUMB_DIMEN
                        + "&scale=" + MAP_THUMB_SCALE
                        + "&sensor=" + "false"
                        + "&markers=color:red%7C"
                        + latlng.latitude + "," + latlng.longitude;

        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(urlstr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.warnning("could not encode url[%s]: %s",
                    urlstr, e.toString());
            encodedUrl = null;
        }

        Bitmap bitmap = null;

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet(urlstr);

        InputStream istream = null;
        try {
            istream = httpClient.execute(request).getEntity().getContent();
            if (istream != null) {
                bitmap = BitmapFactory.decodeStream(istream);
            }
        } catch (IllegalStateException e) {
            Logger.warnning("could not get map thumb for [lat: %f, lon: %f], url = %s: %s",
                    lat, lon, encodedUrl, e.toString());
            bitmap = null;
        } catch (ClientProtocolException e) {
            Logger.warnning("could not get map thumb for [lat: %f, lon: %f], url = %s: %s",
                    lat, lon, encodedUrl, e.toString());
            bitmap = null;
        } catch (IOException e) {
            Logger.warnning("could not get map thumb for [lat: %f, lon: %f], url = %s: %s",
                    lat, lon, encodedUrl, e.toString());
            bitmap = null;
        } finally {
            try {
                if (istream != null) {
                    istream.close();
                }
            } catch (IOException ioe) {
                Logger.warnning("could not get map thumb for [lat: %f, lon: %f]: %s",
                        lat, lon, ioe.toString());
                bitmap = null;
            }
        }

        return bitmap;
    }



    private LocationTracker.LocationChangedListener mLocationChangedListener =
            new LocationTracker.LocationChangedListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (location == null) {
                    return;
                }

                triggerLocationUpdate(location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getTime());
            }

    };

}
