package com.dailystudio.memory.fragment;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.R;
import com.dailystudio.memory.database.loader.LoaderIds;
import com.dailystudio.memory.database.loader.QueryQuoteLoader;
import com.dailystudio.memory.querypieces.quote.QuoteDigest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class DashboardQuoteFragment extends AbsLoaderFragment<QuoteDigest> {

	public static final String ACTION_VIEW_QUOTE = "memory.intent.ACTION_VIEW_QUOTE";
	public static final String EXTRA_QUOTE_ID = "memory.intent.EXTRA_QUOTE_ID";

	public static final String ACTION_UPDATE_QUOTE = "memory.intent.ACTION_UPDATE_QUOTE";
	
	public static final String ACTION_NEW_QUOTE = "memory.intent.ACTION_NEW_QUOTE";

	public static final String EXTRA_QUOTE_CATEGORY = "memory.intent.EXTRA_QUOTE_CATEGORY";
	public static final String EXTRA_QUOTE_COUNT = "memory.intent.EXTRA_QUOTE_COUNT";

	private String mQuoteId;
	
	private TextView mQuote;
	private TextView mAuthor;
	private View mQuoteLoading;
	
	private View mCtrlRefresh;
	private View mCtrlSettings;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_theysaidso, null);
		
		setupViews(view);
		
		return view;
	}
	
	private void setupViews(View view) {
		if (view == null) {
			return;
		}
		
		mQuote = (TextView) view.findViewById(R.id.quote);
		if (mQuote != null) {
			mQuote.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent quoteAppIntent = new Intent(ACTION_VIEW_QUOTE);
					
					quoteAppIntent.putExtra(EXTRA_QUOTE_ID, mQuoteId);
					quoteAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					
					ActivityLauncher.launchActivity(getActivity(), 
							quoteAppIntent);
				}
				
			});
		}
		mAuthor = (TextView) view.findViewById(R.id.author);
		mQuoteLoading = view.findViewById(R.id.quote_loading);
		
		mCtrlRefresh = view.findViewById(R.id.dashboard_refresh);
		if (mCtrlRefresh != null) {
			mCtrlRefresh.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					final Context context = getActivity();
					
					Intent i = new Intent(ACTION_UPDATE_QUOTE);
					
					context.startService(i);

					bindQuote(null);
				}
				
			});
		}

		mCtrlSettings = view.findViewById(R.id.dashboard_settings);
		if (mCtrlSettings != null) {
			mCtrlSettings.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					bindQuote(null);
				}
				
			});
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		registerNewQuoteReceiver();	
	}

	@Override
	public void onPause() {
		super.onPause();

		unregisterNewQuoteReceiver();	
	}
	
	public void bindQuote(QuoteDigest quote) {
		if (quote == null) {
			reset();
			
			return;
		}
		
		mQuoteId = quote.qid;
		
		if (mQuoteLoading != null) {
			mQuoteLoading.setVisibility(View.INVISIBLE);
		}

		if (mQuote != null) {
			mQuote.setVisibility(View.VISIBLE);
			mQuote.setText(quote.content);
		}
		
		if (mAuthor != null) {
			final String author = quote.author;
			
			StringBuilder astr = new StringBuilder();
			
			if (!TextUtils.isEmpty(author)) {
				astr.append("-- ");
				astr.append(author);
			}
			
			mAuthor.setText(astr.toString());
		}
	}
	
	public void reset() {
		mQuoteId = null;
		
		if (mQuoteLoading != null) {
			mQuoteLoading.setVisibility(View.VISIBLE);
		}

		if (mQuote != null) {
			mQuote.setVisibility(View.INVISIBLE);
		}
		
		if (mAuthor != null) {
			mAuthor.setText(null);
		}
	}
	
	@Override
	protected int getLoaderId() {
		return LoaderIds.MEMORY_QUOTE_OF_DAY;
	}

	@Override
	protected Bundle createLoaderArguments() {
		return new Bundle();
	}

	@Override
	public void onLoadFinished(Loader<QuoteDigest> arg0,
			QuoteDigest quote) {
		Logger.debug("quote = %s", quote);
		
		bindQuote(quote);
	}

	@Override
	public void onLoaderReset(Loader<QuoteDigest> arg0) {
		bindQuote(null);
	}

	@Override
	public Loader<QuoteDigest> onCreateLoader(int arg0, Bundle arg1) {
		return new QueryQuoteLoader(getActivity());
	}
	
	private void registerNewQuoteReceiver() {
		IntentFilter filter = new IntentFilter(ACTION_NEW_QUOTE);
		
		try {
			getActivity().registerReceiver(mNewQuoteReceiver, filter);
		} catch (Exception e) {
			Logger.warnning("could not register new quote receiver: %s",
					e.toString());
		}					
	}
	
	private void unregisterNewQuoteReceiver() {
		try {
			getActivity().unregisterReceiver(mNewQuoteReceiver);
		} catch (Exception e) {
			Logger.warnning("could not unregister new quote receiver: %s",
					e.toString());
		}					
	}
	
	private BroadcastReceiver mNewQuoteReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			
			final String action = intent.getAction();
			if (ACTION_NEW_QUOTE.equals(action)) {
				restartLoader();
			}
		}
		
	};

}
