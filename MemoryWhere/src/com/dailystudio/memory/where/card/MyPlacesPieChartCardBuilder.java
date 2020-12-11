package com.dailystudio.memory.where.card;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleQueryBuilder;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.card.CardElements;
import com.dailystudio.memory.card.PeroidDatabaseObjectsChartCardBuilder;
import com.dailystudio.memory.chart.ChartBuilder;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.chart.AbsIdspotStatPieChartBuilder;
import com.dailystudio.memory.where.chart.AbsIdspotStatPieChartBuilder.SharedArguments;
import com.dailystudio.memory.where.chart.MyPlacesPieChartBuilder;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;
import com.dailystudio.memory.where.hotspot.IdentifiedHotspotColors;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

public class MyPlacesPieChartCardBuilder 
	extends PeroidDatabaseObjectsChartCardBuilder<IdspotHistory> {

	private final static String CHART_CARD_TEMPL = "card_my_places_pie_templ.html";

	private final static String RPL_KEY_PIE_CHART_TITLE = "pie_chart_title";
	private final static String RPL_KEY_PIE_LEGEND = "pie_chart_legend";
	private final static String RPL_KEY_PIE_LEGEND_COUNTS = "pie_chart_legend_counts";
	
	private final static String TIME_FORMAT = "MMM. dd";


	public MyPlacesPieChartCardBuilder(Context context) {
		super(context, CHART_CARD_TEMPL, Cards.CARD_MY_PLACES_PIE_CHART_FILE);
		
		final long now = System.currentTimeMillis();

		final long weekStart = CalendarUtils.getStartOfWeek(now);
		final long weekEnd = CalendarUtils.getEndOfWeek(now);
		Logger.debug("build card for[%s - %s], now = %s", 
				CalendarUtils.timeToReadableStringWithoutTime(weekStart),
				CalendarUtils.timeToReadableStringWithoutTime(weekEnd),
				CalendarUtils.timeToReadableString(now));
		setPeroid(weekStart, weekEnd);
	}
	
    @Override
    protected void buildCardElementsWithDatabaseObjects(Context context,
    		CardElements elements, List<IdspotHistory> objects) {
    	super.buildCardElementsWithDatabaseObjects(context, elements, objects);
    	
    	String tfmt = context.getString(
    			R.string.time_print_date_format_without_year);
    	if (tfmt == null) {
    		tfmt = TIME_FORMAT;
    	}
    	
    	SimpleDateFormat formater = new SimpleDateFormat(tfmt);
    	String titleVal = String.format("%s (%s - %s)",
				context.getString(R.string.dashboard_my_places),
				formater.format(getPeroidStart()),
				formater.format(getPeroidEnd()));
    	
    	String legendVal = null;
    	String countVal = null;

    	Object csArgs = getChartSharedArguments();
    	if (csArgs instanceof SharedArguments) {
    		SharedArguments args = (SharedArguments)csArgs;
    		
    		legendVal = buildLegendHtmlList(context, args);
    		
    		if (args.mSumDurations != null) {
    			final Set<HotspotIdentity> identities =
    					args.mSumDurations.keySet();
    			if (identities != null) {
        			countVal = String.valueOf(identities.size());
    			}
    		}
    		Logger.debug("legendVal = [%s], countVal = [%s]",
    				legendVal, countVal);
    	}
    	

    	elements.putElement(RPL_KEY_PIE_CHART_TITLE, titleVal);
    	elements.putElement(RPL_KEY_PIE_LEGEND, legendVal);
    	elements.putElement(RPL_KEY_PIE_LEGEND_COUNTS, countVal);
    }
    
    private String buildLegendHtmlList(Context context, SharedArguments args) {
    	if (context ==  null || args == null) {
    		return null;
    	}
    	
    	final Resources res = context.getResources();
    	if (res == null) {
    		return null;
    	}
    	
		if (args.mSumDurations == null &&
				args.mSumDurations.size() <= 0) {
			return null;
		}
		
		List<HotspotIdentity> sortedIdentities = 
				AbsIdspotStatPieChartBuilder.sortIdentities(
						args.mSumDurations.keySet());
		
		HotspotIdentityInfo hInfo = null;
	    int colorResId = 0;
	    int color = Color.BLACK;
	    String seriesTitle = null;
	    
	    StringBuilder builder = new StringBuilder();
	    
		for (HotspotIdentity identity: sortedIdentities) {
			Logger.debug("identity: %s, colorResId = %d, loader = 0x%08x", 
					identity,
					colorResId, 
					this.hashCode());
			colorResId = IdentifiedHotspotColors.getColorOfHotspotIdentity(
					identity);
			color = res.getColor(colorResId);
			
			hInfo = HotspotIdentifier.getIdentityInfo(identity);
			if (hInfo != null) {
				seriesTitle = context.getString(hInfo.labelResId);
			} else {
				seriesTitle = context.getString(R.string.hotspot_unknow);
			}
			
			builder.append("<li style=\'color:");
			builder.append(androidColorToHtmlColor(color));
			builder.append("\'>");
			builder.append(seriesTitle);
			builder.append("</li>");
	    }

		return builder.toString();
    }
    
    private String androidColorToHtmlColor(int color) {
    	StringBuilder builder = new StringBuilder("rgba");
    	
    	builder.append('(');
    	builder.append(Color.red(color));
    	builder.append(',');
    	builder.append(Color.green(color));
    	builder.append(',');
    	builder.append(Color.blue(color));
    	builder.append(',');
    	builder.append(Color.alpha(color) / (float)255);
    	builder.append(')');
    	
    	return builder.toString();
    }

	@Override
	protected ChartBuilder<IdspotHistory> createChartBuilder() {
		return new MyPlacesPieChartBuilder(getContext(),
				getPeroidStart(), getPeroidEnd());
	}

	@Override
	protected Class<IdspotHistory> getObjectClass() {
		return IdspotHistory.class;
	}
	
	@Override
	protected Query getQuery(Class<IdspotHistory> klass) {
		final long start = getPeroidStart();
		final long end = getPeroidEnd();
		
		if (end <= start) {
			return super.getQuery(getObjectClass());
		}
		
		TimeCapsuleQueryBuilder builer =
			new TimeCapsuleQueryBuilder(getObjectClass());
		
		Query query = builer.getQueryForIntersect(
				IdspotHistory.COLUMN_TIME, 
				IdspotHistory.COLUMN_DURATION,
				start, end);	
		if (query == null) {
			return query;
		}
		
		ExpressionToken selToken = 
				query.getSelection();
		
		IdspotHistory history = 
				IdspotHistoryDatabaseModal.getLastHistory(getContext());
		if (history == null || history.getDuration() > 0) {
			return query;
		}
		
		ExpressionToken moreSelToken = null;
		if (history.getTime() < end) {
			moreSelToken = IdspotHistory.COLUMN_ID.eq(history.getId());
		}
		
		if (moreSelToken != null) {
			if (selToken == null) {
				selToken = moreSelToken;
			} else {
				selToken = selToken.or(moreSelToken);
			}
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		return query;
	}
	
	@Override
	public int getChartWidth() {
		final Resources res = getContext().getResources();
		if (res == null) {
			return super.getChartWidth();
		}
		
		final int w = res.getDimensionPixelSize(
				R.dimen.ds_card_my_places_chart_width);
		if (w <= 0) {
			return super.getChartWidth();
		}
		
		return w;
	}
	
	@Override
	public int getChartHeight() {
		final Resources res = getContext().getResources();
		if (res == null) {
			return super.getChartHeight();
		}
		
		final int h = res.getDimensionPixelSize(
				R.dimen.ds_card_my_places_chart_height);
		if (h <= 0) {
			return super.getChartHeight();
		}
		
		return h;
	}
	
}
