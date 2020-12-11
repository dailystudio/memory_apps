package com.dailystudio.memory.where.activity;

import com.dailystudio.app.location.Compass;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.BaseLocationObject;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;
import com.dailystudio.memory.where.locationapi.AbsLocationApi;
import com.dailystudio.memory.where.locationapi.BaiduLocationApi;
import com.dailystudio.memory.where.utils.GeoPointUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;

public class NowWhereActivity extends MapActionBarActivity {
	
	private class TranslateLocationAsyncTask extends AsyncTask<Void, Void, LatLng> {

		private double mLat;
		private double mLon;
		private MarkerOptions mMakerOpt;
		
		private TranslateLocationAsyncTask(double lat, double lon, MarkerOptions opts) {
			mLat = lat;
			mLon = lon;
			mMakerOpt = opts;
		}
		
		@Override
		protected LatLng doInBackground(Void... params) {
			Thread.currentThread().setPriority(Process.THREAD_PRIORITY_BACKGROUND);

			LatLng latlon = GeoPointUtils.convertToLatLng(NowWhereActivity.this,
					mLat, mLon);

			return latlon;
		}
		
		@Override
		protected void onPostExecute(LatLng result) {
			super.onPostExecute(result);
			
			Logger.debug("translated latlon: %s, for [%f, %f]",
					result, mLat, mLon);
			if (result != null) {
				doCenterToLocation(result, mMakerOpt);
			}
		}
		
	}
	
	private final int REQUEST_UPDATE_CAMERA_DELAY = 2500;  
	private final int REQUEST_UPDATE_LOCATION_DELAY = 1000;  
	
	private final double BEARING_CHANGE_THRESHOLD = 10.0;  
	private final double LOCATION_CHANGE_THRESHOLD = 50.0;  
	
    private Compass mCompass;

	private AbsLocationApi mLocationApi;

    private boolean mLockCameraRotateUpdate = true;
    
    private double mPendingBearing = 0;
    private boolean mBearingDirty = false;
    
    private MemoryLocation mPendingLocation = null;
    private boolean mLocationDirty = false;
    
    private boolean mInitCamera = false;
    
    private boolean mSkipRotateAfterUnlockCamera = false;
    
	private WakeLock mWakeLock;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getContentViewResId());
		
		initWakelock();
		
		initLocationObjects();
		setupMaps();
	}

	private void initWakelock() {
		PowerManager pm = 
				(PowerManager) getSystemService(Context.POWER_SERVICE);
		if (pm == null) {
			return;
		}
		
		mWakeLock = pm.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, 
				getClass().getSimpleName());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mWakeLock != null) {
			mWakeLock.acquire();
		}
		
		startTrackingMyLocation();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (mWakeLock != null) {
			mWakeLock.release();
		}

		stopTrackingMyLocation();
	}

	protected int getContentViewResId() {
		return R.layout.activity_now_where;
	}
	
	protected void setupMaps() {
	}
	
	protected void initLocationObjects() {
		mCompass = new Compass(this);

		mLocationApi = new BaiduLocationApi(this);
		mLocationApi.addLocationChangedListener(mLocationChangedListener);
	}
	
	protected void startTrackingMyLocation() {
		mCompass.beginAnalyzeBearing();
		mHandler.post(mPeroidicalUpdateCameraRunable);
		
		showPrompt(getString(R.string.prompt_update_location));
		setSktipRotateAfterUnlockCamera(false);

		if (mLocationApi != null) {
			mLocationApi.startTracking();
			requestCenterLocation(
					mLocationApi.getCurrentLocation());
		}
	}

	protected void stopTrackingMyLocation() {
		mCompass.endAnalyzeBearing();
		
		mHandler.removeCallbacks(mPeroidicalUpdateCameraRunable);
		mHandler.removeMessages(EVENT_ROTATE_CAMERA);
		mHandler.removeMessages(EVENT_UPDATE_LOCATION);

		if (mLocationApi != null) {
			mLocationApi.stopTracking();
		}
	}
	
	protected MarkerOptions getMyLocationMarkerOption(MemoryLocation location) {
		if (location == null) {
			return null;
		}
		
		return new MarkerOptions()
			.title(getString(R.string.marker_now));
	}
	
	protected void centerToMyLocation() {
		if (mLocationApi != null) {
			centerToMyLocation(mLocationApi.getCurrentLocation());
		}
	}
	
	protected void centerToMyLocation(MemoryLocation location) {
		mPendingLocation = location;
		
		centerToLocation(location, 
				getMyLocationMarkerOption(location));
		
		mLocationDirty = false;
	}
	
	protected void centerToLocation(MemoryLocation location,
			MarkerOptions markerOpt) {
		if (location == null) {
			return;
		}
		
		centerToLocation(location.getLatitude(),
				location.getLongitude(), markerOpt);
	}
	
	protected void centerToLocation(double lat, double lon,
			MarkerOptions markerOpt) {
		Logger.debug("translate address for: %f, %f", lat, lon);
		new TranslateLocationAsyncTask(lat, lon, markerOpt).executeOnExecutor(
				AsyncTask.THREAD_POOL_EXECUTOR,
				(Void)null);
	}
	
	private void doCenterToLocation(LatLng latLon,
			MarkerOptions markerOpt) {
		if (latLon == null) {
			return;
		}
		
		final GoogleMap map = getMap();
		if (map != null) {
			map.clear();
			map.addMarker(markerOpt.position(latLon)).showInfoWindow();
		}
		
		moveCameraTo(latLon, true);
	}
	
	@Override
	protected void moveCameraTo(LatLng latlng, boolean animated) {
		mLockCameraRotateUpdate = true;
		
		mHandler.removeMessages(EVENT_ROTATE_CAMERA);
		mHandler.removeMessages(EVENT_UPDATE_LOCATION);
		super.moveCameraTo(latlng, animated);
	}
	
	@Override
	protected void onMoveCameraCancel() {
		super.onMoveCameraCancel();
		
		hidePrompt();
		
		mHandler.postDelayed(mUnlockCamearUpdateRunnable,
				1000);
	}

	@Override
	public void onMoveCameraFinish() {
		super.onMoveCameraFinish();
		
		hidePrompt();
		
		mHandler.postDelayed(mUnlockCamearUpdateRunnable,
				1000);
	}
	
	@Override
	protected CameraPosition getAnimateCameraPosition(LatLng latlng) {
		final GoogleMap map = getMap();
		if (map == null) {
			return super.getAnimateCameraPosition(latlng);
		}

		CameraPosition cameraPosition = null;
		if (mInitCamera == false) {
			cameraPosition = super.getAnimateCameraPosition(latlng);
			mInitCamera = true;
		} else {
			CameraPosition oldCameraPos = map.getCameraPosition();
			if (oldCameraPos == null) {
				cameraPosition = super.getAnimateCameraPosition(latlng);
			} else {
				cameraPosition = new CameraPosition.Builder(oldCameraPos)
				    .target(latlng)
				    .build();
			}
		}

		return cameraPosition;
	}
	
	protected void setSktipRotateAfterUnlockCamera(boolean skip) {
		mSkipRotateAfterUnlockCamera = skip;
	}
	
	protected void rotateCamera() {
		rotateCamera((int)mCompass.getBearing());
	}
	
	protected void rotateCamera(double newBearing) {
		final GoogleMap map = getMap();
		if (map == null) {
			return;
		}

		CameraPosition oldCameraPos = map.getCameraPosition();
		if (oldCameraPos == null) {
			return;
		}
		
		CameraPosition updateCameraPos = new CameraPosition.Builder(oldCameraPos)
		    .bearing((int)newBearing)
		    .build();

		mPendingBearing = newBearing;
		
		map.animateCamera(CameraUpdateFactory.newCameraPosition(updateCameraPos));

		Logger.debug("clear dirty: [mPendingBearing: %3.1f]",
				newBearing);
		
		mBearingDirty = false;
		
		hidePrompt();
		Logger.debug("CAMERA: EXC on %s",
				CalendarUtils.timeToReadableString(
						System.currentTimeMillis()));
	}
	
	private synchronized void requestRotateCamera() {
		requestRotateCamera(REQUEST_UPDATE_CAMERA_DELAY);
	}
	
	private synchronized void requestRotateCamera(long delayMillis) {
		mHandler.removeMessages(EVENT_ROTATE_CAMERA);

		Message rmsg = mHandler.obtainMessage(EVENT_ROTATE_CAMERA);

		Logger.debug("CAMERA: REQ on %s",
				CalendarUtils.timeToReadableString(
						System.currentTimeMillis()));
		
		if (delayMillis <= 0) {
			mHandler.sendMessage(rmsg);
		} else {
			showPrompt(getString(R.string.prompt_rotate_camera));
			
			mHandler.sendMessageDelayed(rmsg, delayMillis);
		}
	}
	
	private synchronized void requestCenterLocation(MemoryLocation loc) {
		requestCenterLocation(loc, REQUEST_UPDATE_LOCATION_DELAY);
	}
	
	private synchronized void requestCenterLocation(MemoryLocation loc, long delayMillis) {
		Logger.debug("[REQUEST]: loc = %s", loc);
		mHandler.removeMessages(EVENT_UPDATE_LOCATION);

		Message rmsg = mHandler.obtainMessage(EVENT_UPDATE_LOCATION);
		rmsg.obj = loc;
		
		if (delayMillis <= 0) {
			mHandler.sendMessage(rmsg);
		} else {
			mHandler.sendMessageDelayed(rmsg, delayMillis);
		}
	}
	
	private AbsLocationApi.LocationChangedListener mLocationChangedListener =
			new AbsLocationApi.LocationChangedListener() {

			@Override
			public void onLocationChanged(double lat, double lon, double alt, long time) {
//			if (mInitCamera == false) {
//				return;
//			}

			MemoryLocation loc = new MemoryLocation(getApplicationContext());

			loc.setLatitude(lat);
			loc.setLongitude(lon);
			loc.setAltitude(alt);
			loc.setTime(time);
			Logger.debug("new location: [%s]", loc);

			if (mPendingLocation != null) {
				final double delta = BaseLocationObject.getDistanceBetween(
						mPendingLocation.getLatitude(), mPendingLocation.getLongitude(),
						lat, lon);
				if (delta > LOCATION_CHANGE_THRESHOLD) {
					if (!mLocationDirty) {
						Logger.debug("request dirty: delta = %f, [oldLoc: (%f, %f), loc: (%f, %f)]",
								delta,
								mPendingLocation.getLatitude(),
								mPendingLocation.getLongitude(),
								lat,
								lon);
						mLocationDirty = true;

						showPrompt(getString(R.string.prompt_update_location));
						requestCenterLocation(loc);
					}
				} else {
					Logger.debug("skip same loc: delta = %f, [oldLoc: (%f, %f), loc: (%f, %f)]",
							delta,
							mPendingLocation.getLatitude(),
							mPendingLocation.getLongitude(),
							lat,
							lon);
				}
			} else {
				showPrompt(getString(R.string.prompt_update_location));
				requestCenterLocation(loc);
			}
		}

};

	private Runnable mUnlockCamearUpdateRunnable = new Runnable() {

	@Override
	public void run() {
		mLockCameraRotateUpdate = false;
		if (!mSkipRotateAfterUnlockCamera) {
			requestRotateCamera();
		}
		}
		
	};
	
	private Runnable mPeroidicalUpdateCameraRunable = new Runnable() {
		
		@Override
		public void run() {
			double oldBearing = mPendingBearing;
			
			final double bearing = mCompass.getBearing();
			final double delta = Math.abs(bearing - oldBearing);
			
			if (!mLockCameraRotateUpdate) {
				if (delta > BEARING_CHANGE_THRESHOLD) {
					if (!mBearingDirty) {
						Logger.debug("request dirty: delta = %f, [oldBearing: %3.1f, bearing: %3.1f]",
								delta, oldBearing, bearing);
						mBearingDirty = true;
					
						requestRotateCamera();
					} 
				} 
			}
			
			mHandler.postDelayed(mPeroidicalUpdateCameraRunable, 100);
		}
		
	};

	private final static int EVENT_ROTATE_CAMERA = 0x1;
	private final static int EVENT_UPDATE_LOCATION = 0x2;
	
	protected Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case EVENT_ROTATE_CAMERA:
					rotateCamera();
					
					break;
				
				case EVENT_UPDATE_LOCATION:
					if (msg.obj instanceof MemoryLocation) {
						final MemoryLocation loc = (MemoryLocation)msg.obj;
						
						centerToMyLocation(loc);
					}
					
					break;
			}
		}
		
	};
	
}
