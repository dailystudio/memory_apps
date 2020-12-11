package com.dailystudio.memory.where.locationapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nan on 2015/3/30.
 */
public class BaiduLocationApi extends AbsLocationApi {

    private final static int MAP_THUMB_WIDTH = 300;
    private final static int MAP_THUMB_HEIGHT = 150;
    private final static int MAP_THUMB_SCALE = 2;
    private final static int MAP_ZOOM = 15;

    private LocationClient mLocationClient;

    public BaiduLocationApi(Context context) {
        super(context);

        mLocationClient = new LocationClient(context);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setCoorType("gcj02");
        option.setScanSpan((int) Constants.LOCATION_REQUEST_MIN_INTERVAL);

        mLocationClient.setLocOption(option);

        mLocationClient.registerLocationListener(mLocationListener);
    }

    @Override
    public MemoryLocation getCurrentLocation() {
        BDLocation loc = mLocationClient.getLastKnownLocation();
        if (loc == null) {
            return null;
        }

        return MemoryLocation.createMemoryLocation(getContext(), loc);
    }

    @Override
    public void startTracking() {
        mLocationClient.requestLocation();
        mLocationClient.start();
    }

    @Override
    public void stopTracking() {
        mLocationClient.stop();
    }

    @Override
    public Bitmap getMapThumbnail(double lat, double lon, double alt) {
        final Context context = getContext();
        if (context == null) {
            return null;
        }

        final LatLng srcLatLng = new LatLng(lat, lon);

        /*
         * Must use Baidu API converter to
         * translate coords, or it could not display correct
         * place in Baidu static map API
         */
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.COMMON);
        converter.coord(srcLatLng);

        LatLng desLatLng = converter.convert();

        final String urlstr =
                "http://api.map.baidu.com/staticimage?"
                        + "center="
                        + desLatLng.longitude + "," + desLatLng.latitude
                        + "&zoom=" + MAP_ZOOM
                        + "&width=" + MAP_THUMB_WIDTH
                        + "&height=" + MAP_THUMB_HEIGHT
                        + "&scale=" + MAP_THUMB_SCALE
                        + "&markers="
                        + desLatLng.longitude + "," + desLatLng.latitude
                        + "markerStyles=s,A,#DE5229";
        Logger.debug("map cache url: %s", urlstr);

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

    private BDLocationListener mLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Logger.debug("new BDLocation: %s", bdLocation);
            if (bdLocation == null) {
                return;
            }

            String timestr = bdLocation.getTime();

            long time;
            try {
                SimpleDateFormat sdf =
                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                Date date = sdf.parse(timestr);

                time = date.getTime();
            } catch (ParseException e) {
                Logger.warnning("could not parse time from location: %s",
                        e.toString());
                time = 0l;
            }

            triggerLocationUpdate(
                    bdLocation.getLatitude(),
                    bdLocation.getLongitude(),
                    bdLocation.getAltitude(),
                    time);
        }

    };

}
