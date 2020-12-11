package com.dailystudio.memory.application.databaseobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.nativelib.application.AndroidActivity;
import com.dailystudio.nativelib.application.AndroidApplication;
import com.dailystudio.nativelib.application.IResourceObject;

public class ActivityUsageStatistics extends UsageStatistics {
	
	private final static Column[] sCloumns = {
		Usage.COLUMN_COMPONENT_ID,
	};
	
	public ActivityUsageStatistics(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sCloumns);
	}
	
	public int getComponentId() {
		return getIntegerValue(Usage.COLUMN_COMPONENT_ID);
	}

	public void setComponentId(int compId) {
		setValue(Usage.COLUMN_COMPONENT_ID, compId);
	}
		
	@Override
	protected IResourceObject createResObject() {
		final int compId = getComponentId();
		
		UsageComponent comp = 
			UsageComponentDatabaseModal.getComponent(mContext, compId);
		if (comp == null) {
			return null;
		}
		
		final ComponentName compName = 
			new ComponentName(comp.getPackageName(), 
					comp.getClassName());
		
		return new AndroidActivity(compName);
	}

	@Override
	public String toString() {
		return String.format("%s(0x%08x, id: %d): compId(%d), time(%s), duration-sum(%s)",
				getClass().getSimpleName(),
				hashCode(),
				getId(),
				getComponentId(),
				CalendarUtils.timeToReadableString(getTime()),
				CalendarUtils.durationToReadableString(getDurationSum()));
	}

	@Override
	protected String getResourcePackage() {
		return null;
	}

	public static List<UsageStatistics> convertToApplicationUsageStatistics(
			Context context,
			List<UsageStatistics> actstats) {
		if (context == null) {
			return null;
		}
		
		if (actstats == null || actstats.size() <= 0) {
			return null;
		}
		
		List<UsageStatistics> appstats =
			new ArrayList<UsageStatistics>();
		
		Map<String, ApplicationUsageStatistics> maps = 
			new HashMap<String, ApplicationUsageStatistics>();
		
		ActivityUsageStatistics activityUsage = null;
		for (UsageStatistics us: actstats) {
			if (us instanceof ActivityUsageStatistics == false) {
				continue;
			}
			
			activityUsage = (ActivityUsageStatistics)us;
/*			Logger.debug("loader = %s, activityUsage = %s", 
					this, activityUsage);
*/			
			calculateApplicatinDuration(context, maps, activityUsage);
		}
		
		Collection<ApplicationUsageStatistics> values = maps.values();
		if (values == null) {
			return null;
		}
		
		for (ApplicationUsageStatistics statistic: values) {
//			Logger.debug("statistic = %s", statistic);
			appstats.add(statistic);
		}
		
		return appstats;
	}
	
	private static void calculateApplicatinDuration(
			Context context,
			Map<String, ApplicationUsageStatistics> maps,
			ActivityUsageStatistics usage) {
		if (context == null) {
			return;
		}
		
		if (maps == null || usage == null) {
			return;
		}
		
		final int compId = usage.getComponentId();
		
		final UsageComponent comp = 
			UsageComponentDatabaseModal.getComponent(context, compId);
		if (comp == null) {
			return;
		}
		
		final String packageName = comp.getPackageName();
		if (packageName == null) {
			return;
		}
		
		final AndroidApplication app = new AndroidApplication(packageName);
		if (app.isInstalled(context) == false) {
			return;
		}
		
		ApplicationUsageStatistics statistics = maps.get(packageName);
		if (statistics == null) {
			statistics = new ApplicationUsageStatistics(context);
			
			statistics.setTime(System.currentTimeMillis());
			statistics.setPackageName(packageName);
			statistics.setDurationSum(0);
			
			maps.put(packageName, statistics);
		}
		
		long duration = statistics.getDurationSum();
		
		duration += usage.getDurationSum();
		
		statistics.setDurationSum(duration);
	}

}
