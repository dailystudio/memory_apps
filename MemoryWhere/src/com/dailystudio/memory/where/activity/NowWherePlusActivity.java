package com.dailystudio.memory.where.activity;

import java.io.File;

import com.dailystudio.app.fragment.AbsAdapterFragment.OnListItemSelectedListener;
import com.dailystudio.app.utils.FileUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.activity.ActionBar;
import com.dailystudio.memory.where.Directories;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.fragment.EnvironmentFragment;
import com.dailystudio.memory.where.fragment.WeatherFragment;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;
import com.dailystudio.memory.where.weather.OpenWeatherMapData;
import com.dailystudio.memory.where.weather.OpenWeatherMapWeatherRequest;
import com.dg.libs.rest.callbacks.HttpCallback;
import com.dg.libs.rest.domain.ResponseStatus;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class NowWherePlusActivity extends NowWhereActivity 
	implements OnListItemSelectedListener {
	
	private static final int REQUEST_TAKE_PHOTO = 1;

	private String mCurrentPhotoPath;

	private View mTakePhotoButton;
    private View mIdspotListButton;
	private View mMyLocationButton;
	
	private boolean mResetCamera = false;
   
	private Rect mHitRect = new Rect();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setupActionBar();

		hideFragmentOnCreate(findFragment(R.id.fragment_idspots_list));
		hideFragmentOnCreate(findFragment(R.id.fragment_weather));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		mHandler.removeCallbacks(mDelayedResetWeatherRunnable);
		mHandler.removeCallbacks(mDelayedShowWeatherRunnable);
	}
	
	protected int getContentViewResId() {
		return R.layout.activity_now_where_plus;
	}
	
	private void setupActionBar() {
		final ActionBar actbar = getCompatibleActionBar();
		if (actbar == null) {
			return;
		}
		
        ViewGroup v = (ViewGroup)LayoutInflater.from(this)
    		.inflate(R.layout.actbar_now_where, null);

	    actbar.setCustomView(v,
	        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
	                ActionBar.LayoutParams.WRAP_CONTENT,
	                Gravity.CENTER_VERTICAL | Gravity.RIGHT));
	    
	    mTakePhotoButton = v.findViewById(R.id.take_photo);
	    if (mTakePhotoButton != null) {
	    	mTakePhotoButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dispatchTakePictureIntent();
				}

			});
	    }
	    
	    mIdspotListButton = v.findViewById(R.id.idpsot_list_button);
	    if (mIdspotListButton != null) {
	    	mIdspotListButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					toggleIdspotListFragment();
				}

			});
	    }
	    
	    mMyLocationButton = v.findViewById(R.id.my_location);
	    if (mMyLocationButton != null) {
	    	mMyLocationButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mHandler.postDelayed(mResumeTrackMyLocationRunnable,
							UI_ACTION_DELAY);
				}

			});
	    }
	}

	@Override
	public void onListItemSelected(Object itemData) {
		if (itemData instanceof IdentifiedHotspot == false) {
			return;
		}

		hideIdspotListFragment();
		
		stopTrackingMyLocation();

		IdentifiedHotspot idspot = (IdentifiedHotspot)itemData;
		
		setSktipRotateAfterUnlockCamera(true);
		centerToIdspotLocation(idspot);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_TAKE_PHOTO) {
	    	if (mCurrentPhotoPath != null) {
	    		if (resultCode == RESULT_OK) {
		    		Logger.debug("add temp photo to gallery: file [%s]", mCurrentPhotoPath);
		    		
			    	MediaScannerConnection.scanFile(this,
		                    new String[] { mCurrentPhotoPath }, null,
		                    new MediaScannerConnection.OnScanCompletedListener() {
		                
			    		public void onScanCompleted(String path, Uri uri) {
		                    Logger.debug("Scanned: file = %s, uri = %s",
		                    		path, uri);
		                }
		                
		            });
		    	} else {
		    		Logger.debug("delete temp photo: file [%s]", mCurrentPhotoPath);
		    		
		    		FileUtils.deleteFile(mCurrentPhotoPath);
		    	}
	    		
	    		mCurrentPhotoPath = null;
	    	}
	    }
	}
	
	private void queryWeatherOfLocation() {
		final GoogleMap map = getMap();
		if (map == null) {
			return;
		}
		
		final CameraPosition pos = map.getCameraPosition();
		if (pos == null || pos.target == null) {
			return;
		}
		
		queryWeatherOfLocation(pos.target.latitude, pos.target.longitude);
	}
	
	private File createImageFile() {
	    String imageFileName = Directories.generatePhotoPath(this);
	    
	    boolean created = FileUtils.checkOrCreateFile(imageFileName);
	    if (created == false) {
			return null;
		}

	    mCurrentPhotoPath = imageFileName;
	    
	    Logger.debug("create tmp photo: file [%s]", mCurrentPhotoPath);
	    
	    return new File(imageFileName);
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        File photoFile = createImageFile();
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private void queryWeatherOfLocation(double lat, double lon) {
//		hideFragment(R.id.fragment_weather, R.anim.weather_panel_anim_out);
//		resetWeather();

		OpenWeatherMapWeatherRequest request = 
				new OpenWeatherMapWeatherRequest(lat, lon,
						mWeatherRequestHandler);
		Logger.debug("request = %s", request);

		request.executeAsync();
	}
	
	private void resetWeather() {
		mHandler.removeCallbacks(mDelayedResetWeatherRunnable);
		mHandler.removeCallbacks(mDelayedShowWeatherRunnable);
		
		mHandler.postDelayed(mDelayedResetWeatherRunnable, UI_ACTION_DELAY);
	}
	
	private void showWeather() {
		mHandler.removeCallbacks(mDelayedResetWeatherRunnable);
		mHandler.removeCallbacks(mDelayedShowWeatherRunnable);
		
		mHandler.postDelayed(mDelayedShowWeatherRunnable, UI_ACTION_DELAY);
	}

	protected void centerToIdspotLocation(IdentifiedHotspot idspot) {
		if (idspot == null) {
			return;
		}
		
		final MarkerOptions markerOpt = 
				getIdspotMarkerOption(idspot);
		
		final HotspotIdentity identity = idspot.getIdentity();
	    final HotspotIdentityInfo hiInfo = 
	            HotspotIdentifier.getIdentityInfo(identity);
	
		final CharSequence prompt = String.format(
				getString(R.string.prompt_go_idspot),
				(hiInfo == null ? "" : getString(hiInfo.labelResId)).toLowerCase());
	
		showPrompt(prompt);
		
		mResetCamera = true;
		centerToLocation(idspot.getLatitude(),
				idspot.getLongitude(), markerOpt);
	}
	
	@Override
	protected void onMoveCameraCancel() {
		super.onMoveCameraCancel();
	
		queryWeatherOfLocation();
		showWeather();
	}

	@Override
	public void onMoveCameraFinish() {
		super.onMoveCameraFinish();
		
		queryWeatherOfLocation();
		showWeather();
	}

	@Override
	protected void centerToLocation(double lat, double lon,
			MarkerOptions markerOpt) {
		super.centerToLocation(lat, lon, markerOpt);
		
//		queryWeatherOfLocation(lat, lon);
		hideFragment(R.id.fragment_weather, R.anim.weather_panel_anim_out);
		resetWeather();
	}
	
	@Override
	protected CameraPosition getAnimateCameraPosition(LatLng latlng) {
		CameraPosition cameraPosition = null;
		
		if (mResetCamera) {
			cameraPosition = new CameraPosition.Builder()
			    .target(latlng)
			    .zoom(17)
			    .tilt(60)
			    .build();

			mResetCamera = false;
		} else {
			cameraPosition = super.getAnimateCameraPosition(latlng);
		}

		return cameraPosition;
	}
	
	protected MarkerOptions getIdspotMarkerOption(IdentifiedHotspot idspot) {
		if (idspot == null) {
			return null;
		}
		
		final HotspotIdentity identity = idspot.getIdentity();
	    final HotspotIdentityInfo hiInfo = 
	            HotspotIdentifier.getIdentityInfo(identity);
	
		final String title = String.format(
				getString(R.string.marker_idspot_templ),
				(hiInfo == null ? "" : getString(hiInfo.labelResId)).toLowerCase());
		
		return new MarkerOptions()
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
			.title(title);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		
		if (action == MotionEvent.ACTION_DOWN) {
			final int x = (int)event.getRawX();
			final int y = (int)event.getRawY();
			
			final Fragment idspotListFragment = 
					findFragment(R.id.fragment_idspots_list);
			if (idspotListFragment != null 
					&& idspotListFragment.isVisible()) {
				
				idspotListFragment.getView().getGlobalVisibleRect(mHitRect);
				
				if (!mHitRect.contains(x, y)) {
					boolean hitCustomizedButton = false;
					View mOverflowButton = findViewById(R.id.idpsot_list_button);
					if (mOverflowButton != null) {
						mOverflowButton.getGlobalVisibleRect(mHitRect);
						
						hitCustomizedButton = mHitRect.contains(x, y);
					}

					if (!hitCustomizedButton) {
						hideIdspotListFragment();
					}
				}
			}
		}
		
		return super.dispatchTouchEvent(event);
	}
	
	private void toggleIdspotListFragment() {
		boolean visible =
				isFragmentVisible(R.id.fragment_idspots_list);
        if (visible) {
        	hideIdspotListFragment();
        } else {
        	showIdspotListFragment();
        }
	}
	
	private void showIdspotListFragment() {
        showFragment(R.id.fragment_idspots_list, 
        		R.anim.nw_idspots_anim_in);
	}
	
	private void hideIdspotListFragment() {
		hideFragment(R.id.fragment_idspots_list, 
        		R.anim.nw_idspots_anim_out);
	}
	
	private final static int UI_ACTION_DELAY = 200;

	private Runnable mResumeTrackMyLocationRunnable = new Runnable() {
		
		@Override
		public void run() {
			startTrackingMyLocation();
		}
		
	};
	
	private Runnable mDelayedResetWeatherRunnable = new Runnable() {
		
		@Override
		public void run() {
			Fragment fragment = findFragment(R.id.fragment_weather);
			if (fragment instanceof WeatherFragment) {
				((WeatherFragment)fragment).reset();
			}
		}
		
	};
	
	private Runnable mDelayedShowWeatherRunnable = new Runnable() {
		
		@Override
		public void run() {
			showFragment(R.id.fragment_weather, R.anim.weather_panel_anim_in);
		}
		
	};
	
	private HttpCallback<OpenWeatherMapData> mWeatherRequestHandler = new HttpCallback<OpenWeatherMapData>() {

        @Override
        public void onSuccess(OpenWeatherMapData openWeatherMapData, ResponseStatus responseStatus) {
            Logger.debug("connect success: %s", openWeatherMapData);
            Fragment fragment = null;

            fragment = findFragment(R.id.fragment_weather);
            if (fragment instanceof WeatherFragment) {
                ((WeatherFragment)fragment).setWeather(openWeatherMapData);
            }

            fragment = findFragment(R.id.fragment_environment_overlay);
            if (fragment instanceof EnvironmentFragment) {
                ((EnvironmentFragment)fragment).setWeather(openWeatherMapData);
            }
        }

        @Override
		public void onHttpError(ResponseStatus arg0) {
			Logger.debug("connect failed: %s", arg0);
		}

	};

}
