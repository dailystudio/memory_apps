package com.dailystudio.memory.where.hotspot;

import java.util.HashMap;
import java.util.Map;

import com.dailystudio.memory.where.R;

public class IdentifiedHotspotColors {

	private final static Map<HotspotIdentity, Integer> IDSPOT_COLORS = 
			new HashMap<HotspotIdentity, Integer>();
	
	static {
		IDSPOT_COLORS.put(HotspotIdentity.HOTSPOT_UNKNOWN, R.color.gold_yellow);
		IDSPOT_COLORS.put(HotspotIdentity.HOTSPOT_HOME, R.color.tomato_red);
		IDSPOT_COLORS.put(HotspotIdentity.HOTSPOT_WORKPLACE, R.color.royal_blue);
	}
	
	public static int getColorOfHotspotIdentity(HotspotIdentity identity) {
		if (identity == null || !IDSPOT_COLORS.containsKey(identity)) {
			return R.color.black;
		}
		
		return IDSPOT_COLORS.get(identity);
	}
	
}
