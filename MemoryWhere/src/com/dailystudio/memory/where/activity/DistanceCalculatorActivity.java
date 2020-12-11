package com.dailystudio.memory.where.activity;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Constants;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.MemoryLocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DistanceCalculatorActivity extends Activity {

	private static final String LOCATION_TYPE = "vnd.android.cursor.item/location";
	
	public static final int REQUEST_CODE_PICK_FROM = 0x1;
	public static final int REQUEST_CODE_PICK_TO = 0x2;
	
	EditText mFromLatInput;
	EditText mFromLonInput;
	EditText mToLatInput;
	EditText mToLonInput;

	TextView mDisntace;

	Button mBtnPickFrom;
	Button mBtnPickTo;
	Button mBtnCalculate;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
        setContentView(R.layout.activity_distance_calculator);
        
        setupViews();
    }
	
	private void setupViews() {
		mFromLatInput = (EditText) findViewById(R.id.input_from_latitude);
		mFromLonInput = (EditText) findViewById(R.id.input_from_longitude);
		mToLatInput = (EditText) findViewById(R.id.input_to_latitude);
		mToLonInput = (EditText) findViewById(R.id.input_to_longitude);
		
		mDisntace = (TextView) findViewById(R.id.calculated_distance);

		mBtnPickFrom = (Button) findViewById(R.id.btn_pick_from);
		if (mBtnPickFrom != null) {
			mBtnPickFrom.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_PICK);
					
					i.setType(LOCATION_TYPE);
					
					ActivityLauncher.launchActivityForResult(
							DistanceCalculatorActivity.this, i, REQUEST_CODE_PICK_FROM);
				}
				
			});
		}
		
		mBtnPickTo = (Button) findViewById(R.id.btn_pick_to);
		if (mBtnPickTo != null) {
			mBtnPickTo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_PICK);
					
					i.setType(LOCATION_TYPE);

					ActivityLauncher.launchActivityForResult(
							DistanceCalculatorActivity.this, i, REQUEST_CODE_PICK_TO);
				}
				
			});
		}
		
		mBtnCalculate = (Button) findViewById(R.id.btn_calculate);
		if (mBtnCalculate != null) {
			mBtnCalculate.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					double fromLat = getLocation(mFromLatInput);
					double fromLon = getLocation(mFromLonInput);
					double toLat = getLocation(mToLatInput);
					double toLon = getLocation(mToLonInput);
					
					final MemoryLocation loc1 = new MemoryLocation(DistanceCalculatorActivity.this);
					loc1.setLatitude(fromLat);
					loc1.setLongitude(fromLon);
					Logger.debug("loc1 = %s", loc1);					
					
					final MemoryLocation loc2 = new MemoryLocation(DistanceCalculatorActivity.this);
					loc2.setLatitude(toLat);
					loc2.setLongitude(toLon);
					Logger.debug("loc2 = %s", loc2);					
					
					final double distance = MemoryLocation.getDistanceBetween(loc1, loc2);
					Logger.debug("distance = %f", distance);					

					if (mDisntace != null) {
						mDisntace.setText(String.format("%.3f", distance));
					}
				}
				
			});
		}
	}
	
	private double getLocation(EditText edit) {
		if (edit == null) {
			return .0;
		}
		
		Editable editable = edit.getText();
		if (editable == null) {
			return .0;
		}
		
		String numString = editable.toString();
		if (numString == null) {
			return .0;
		}
		
		double number = .0;
		try {
			number = Double.parseDouble(numString);
		} catch (NumberFormatException e) {
			number = .0;
		}
		
		return number;
	}	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		
		Logger.debug("data = %s", data);
		if (data == null) {
			return;
		}
		
		final double lat = data.getDoubleExtra(Constants.EXTRA_LATITUDE, .0);
		final double lon = data.getDoubleExtra(Constants.EXTRA_LONGITUDE, .0);
		
		String latText = String.valueOf(lat);
		String lonText = String.valueOf(lon);
		if (latText == null || lonText == null) {
			return;
		}
		
		if (requestCode == REQUEST_CODE_PICK_FROM) {
			if (mFromLatInput != null) {
				mFromLatInput.setText(latText);
			}
			if (mFromLonInput != null) {
				mFromLonInput.setText(lonText);
			}
		} else if (requestCode == REQUEST_CODE_PICK_TO) {
			if (mToLatInput != null) {
				mToLatInput.setText(latText);
			}
			if (mToLonInput != null) {
				mToLonInput.setText(lonText);
			}
		}
	}
	
}