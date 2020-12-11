package com.dailystudio.memory.where.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.fragment.IdspotPieStatChartFragment;
import com.dailystudio.memory.where.fragment.IdspotWeekStatChartFragment;
import com.dailystudio.memory.where.ui.IdspotStatPage;

public class IdspotStatPagesLoader extends AbsAsyncDataLoader<List<IdspotStatPage>> {

	public IdspotStatPagesLoader(Context context) {
		super(context);
	}
	
	@Override
	public List<IdspotStatPage> loadInBackground() {
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		List<IdspotStatPage> pages = new ArrayList<IdspotStatPage>();
		
		pages.add(new IdspotStatPage(R.string.idspot_stat_pie_chart_title, 
				IdspotPieStatChartFragment.class.getName()));
		
		pages.add(new IdspotStatPage(R.string.idspot_stat_line_chart_title, 
				IdspotWeekStatChartFragment.class.getName()));
		
		return pages;
	}

}
