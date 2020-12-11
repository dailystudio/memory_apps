package com.dailystudio.memory.where.activity;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.utils.GeoPointUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public abstract class BaseGeoPointMapActivity extends MapActionBarActivity {

	private class QueryAddressAsyncTask extends AsyncTask<LatLng, Void, String> {

		private final static int MAX_RETRY_TIMES = 3;
		private final static int RETRY_INTERVAL = 500;
		
		private Context mContext;
		
		private QueryAddressAsyncTask(Context context) {
			mContext = context;
		}
		
		@Override
		protected String doInBackground(LatLng... params) {
			if (params == null || params.length <= 0) {
				return null;
			}
			
			final String addrSep = getString(R.string.geo_address_separator);
			
			final LatLng latlon = (LatLng)params[0];
			
			String address = null;
			for (int retry = 0; retry < MAX_RETRY_TIMES; retry++) {
				address = GeoPointUtils.getAddressOfLatLng(mContext, latlon, addrSep);
				if (!TextUtils.isEmpty(address)) {
					break;
				}

				try {
					Thread.sleep(RETRY_INTERVAL);
				} catch (InterruptedException e) {
					Logger.warnning("retry sleep interrupted: %s",
							e.toString());
				}
			}
			
			return address;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (TextUtils.isEmpty(result)) {
				result = getString(R.string.error_unknow);
			}
			
			if (mAddressView != null) {
				mAddressView.setText(result);
			}
		}
	}
	
	private TextView mAddressView;
	
	private double mLatitude;
	private double mLongitude;
	
	private QueryAddressAsyncTask mQueryAddressAsyncTask;
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		setupActionBar();
		
		setupViews();
		
		bindIntent(getIntent());
	}
	
	@Override
	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		
		bindIntent(newIntent);
	}
	
	@Override
	protected void onPause() {
		super.onPause(); 
		
		stopQueryAddress();
//		stopCheckAndSaveMapCache();
	}
	
	protected void bindIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		
		mLatitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0.f);
		mLongitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0.f);
		
		moveToGeoPoint();
	}

	private void setupActionBar() {
	}
	
	private void setupViews() {
        mAddressView = (TextView) findViewById(R.id.geo_address);
	}
	
	private void moveToGeoPoint() {
		final GoogleMap map = getMap();
		if (map == null) {
			return;
		}
		
		Logger.debug("POINT TO: lat: %f, lon: %f",
				mLatitude,
				mLongitude);
		final LatLng latlng = GeoPointUtils.convertToLatLng(this,
				mLatitude, mLongitude);
		if (latlng == null) {
			return;
		}
		
		map.addMarker(new MarkerOptions()
			.position(latlng));
		
		moveCameraTo(latlng, false);
		
		queryAddress(latlng);
	}
	
	private void queryAddress(LatLng latlng) {
		if (latlng == null) {
			return;
		}
		
		stopQueryAddress();
		
		mQueryAddressAsyncTask = new QueryAddressAsyncTask(this);		
		
		mQueryAddressAsyncTask.executeOnExecutor(
				AsyncTask.THREAD_POOL_EXECUTOR,
				latlng);
	}
	
	private void stopQueryAddress() {
		if (mQueryAddressAsyncTask != null
				&& mQueryAddressAsyncTask.getStatus() == AsyncTask.Status.RUNNING
				&& mQueryAddressAsyncTask.isCancelled() == false) {
			mQueryAddressAsyncTask.cancel(true);
		}

		mQueryAddressAsyncTask = null;
	}
	
}
