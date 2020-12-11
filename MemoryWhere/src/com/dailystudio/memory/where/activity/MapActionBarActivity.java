package com.dailystudio.memory.where.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dailystudio.memory.activity.ActionBarActivity;
import com.dailystudio.memory.where.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActionBarActivity extends ActionBarActivity {

	protected GoogleMap getMap() {
		SupportMapFragment mapFragment = 
				getMapFragment();
		if (mapFragment == null) {
			return null;
		}
		
		return mapFragment.getMap();
	}
	
	protected int getMapFragmentId() {
		return R.id.fragment_google_map;
	}
	
	protected SupportMapFragment getMapFragment() {
		FragmentManager frgmgr = getSupportFragmentManager();
		if (frgmgr == null) {
			return null;
		}
		
		Fragment fragment = 
				frgmgr.findFragmentById(getMapFragmentId());
		if (fragment instanceof SupportMapFragment == false) {
			return null;
		}

		return (SupportMapFragment)fragment;
	}
	
	protected void moveCameraTo(LatLng latlng, boolean animated) {
		if (latlng == null) {
			return;
		}
		
		final GoogleMap map = getMap();
		if (map == null) {
			return;
		}
		
		CameraPosition cameraPosition = getAnimateCameraPosition(latlng);

		if (animated) {
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
					mCameraUpdateCallback);
		} else {
			map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	protected CameraPosition getAnimateCameraPosition(LatLng latlng) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
		    .target(latlng)
		    .zoom(17)
		    .tilt(60)
		    .build();

		return cameraPosition;
	}
	
	protected void onMoveCameraCancel() {
	}

	protected void onMoveCameraFinish() {
	}

	private CancelableCallback mCameraUpdateCallback = new CancelableCallback() {
		
		@Override
		public void onFinish() {
			onMoveCameraFinish();	
		}
		
		@Override
		public void onCancel() {
			onMoveCameraCancel();			
		}
		
	};

}
