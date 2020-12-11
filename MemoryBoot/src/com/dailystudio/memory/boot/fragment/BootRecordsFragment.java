package com.dailystudio.memory.boot.fragment;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.boot.LoaderIds;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.boot.loader.BootRecordsLoader;
import com.dailystudio.memory.record.MemoryRecord;
import com.dailystudio.memory.record.MemoryRecordDatabaseModal;
import com.dailystudio.memory.boot.record.Records;
import com.dailystudio.memory.ui.utils.DateTimePrintUtils;

public class BootRecordsFragment extends AbsLoaderFragment<List<MemoryRecord>> {

	private TextView mUptimeFastest;
	private TextView mUptimeFastestDelta;
	private TextView mUptimeSlowest;
	private TextView mUptimeSlowestDelta;
	
	private TextView mLifetimeShortest;
	private TextView mLifetimeShortestDelta;
	private TextView mLifetimeLongest;
	private TextView mLifetimeLongestDelta;
	
	private List<MemoryRecord> mRecords;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_boot_records, null);
		
		setupViews(view);
		
		return view;
	}

	private void setupViews(View fragmentView) {
		if (fragmentView == null) {
			return;
		}
		
		mUptimeFastest = (TextView) fragmentView.findViewById(
				R.id.record_fastest_uptime);		
		mUptimeFastestDelta = (TextView) fragmentView.findViewById(
				R.id.record_fastest_uptime_delta);		
		
		mUptimeSlowest = (TextView) fragmentView.findViewById(
				R.id.record_slowest_uptime);		
		mUptimeSlowestDelta = (TextView) fragmentView.findViewById(
				R.id.record_slowest_uptime_delta);		
		
		mLifetimeShortest = (TextView) fragmentView.findViewById(
				R.id.record_shortest_liftime);		
		mLifetimeShortestDelta = (TextView) fragmentView.findViewById(
				R.id.record_shortest_liftime_delta);		
		
		mLifetimeLongest = (TextView) fragmentView.findViewById(
				R.id.record_longest_liftime);		
		mLifetimeLongestDelta = (TextView) fragmentView.findViewById(
				R.id.record_longest_liftime_delta);		
	}

	@Override
	public Loader<List<MemoryRecord>> onCreateLoader(int loaderId, Bundle args) {
		return new BootRecordsLoader(getActivity());
	}

	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_BOOT_RECORDS_LOADER;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<List<MemoryRecord>> loader,
			List<MemoryRecord> data) {
		mRecords = data;
		
		Logger.debug("mRecords = %s", mRecords);
		if (mRecords == null) {
			return;
		}
		
		final Context context = getActivity();
		
		for (MemoryRecord r: mRecords) {
			if (Records.RECORD_FASTEST_BOOTUP.equals(r.getRecordName())) {
				updateRecordView(context, mUptimeFastest, r);
				updateRecordDeltaView(context, mUptimeFastestDelta, r, false);
			} else if (Records.RECORD_SLOWEST_BOOTUP.equals(r.getRecordName())) {
				updateRecordView(context, mUptimeSlowest, r);
				updateRecordDeltaView(context, mUptimeSlowestDelta, r, false);
			} else if (Records.RECORD_SHORTEST_LEFTTIME.equals(r.getRecordName())) {
				updateRecordView(context, mLifetimeShortest, r);
				updateRecordDeltaView(context, mLifetimeShortestDelta, r, true);
			} else if (Records.RECORD_LONGEST_LIFETIME.equals(r.getRecordName())) {
				updateRecordView(context, mLifetimeLongest, r);
				updateRecordDeltaView(context, mLifetimeLongestDelta, r, true);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (mRecords == null) {
			return;
		}
		
		final Context context = getActivity();
		
		for (MemoryRecord r: mRecords) {
			MemoryRecordDatabaseModal.reviewRecord(context, r.getRecordName());
		}
	}

	private void updateRecordView(Context context, TextView recrodView,
			MemoryRecord record) {
		if (context == null 
				|| recrodView == null
				|| record == null) {
			return;
		}
		
		String val = null;
		
		if (recrodView != null) {
			val = DateTimePrintUtils.printDurationString(context, 
					record.getRecordScore());
			recrodView.setText(val == null ? 
					getDefaultRecordString(context) : val);
		}
	}

	private void updateRecordDeltaView(Context context, TextView deltaView,
			MemoryRecord record, boolean isIncreaseGood) {
		if (context == null 
				|| deltaView == null
				|| record == null) {
			return;
		}
		
		String val = null;
		
		if (deltaView != null) {
			final long deltaScore = record.getDeltaScore();
			StringBuilder builder = new StringBuilder();
			
			if (deltaScore > 0) {
				builder.append('+');
			} else if (deltaScore < 0) {
				builder.append('-');
			}
			
			val = DateTimePrintUtils.printDurationString(context, 
					deltaScore);
			builder.append(val);
			
			deltaView.setText(val == null ? 
					getDefaultRecordString(context) : builder.toString());

			Resources res = context.getResources();

			if (res != null) {
				int colorResId = R.color.black;
				
				if (deltaScore > 0) {
					colorResId = (isIncreaseGood ? 
							R.color.see_green : R.color.tomato_red);
				} else if (deltaScore < 0) {
					colorResId = (isIncreaseGood ? 
							R.color.tomato_red : R.color.see_green);
				}
				
				deltaView.setTextColor(res.getColor(colorResId));
			}
			
			if (record.isReviewed()) {
				deltaView.setVisibility(record.isReviewed() ?
						View.GONE : View.VISIBLE);
			}
		}
	}

	private String getDefaultRecordString(Context context) {
		if (context == null) {
			return null;
		}
		
		return context.getString(R.string.error_unknow);
	}
	
	@Override
	public void onLoaderReset(Loader<List<MemoryRecord>> loader) {
	}

}
 