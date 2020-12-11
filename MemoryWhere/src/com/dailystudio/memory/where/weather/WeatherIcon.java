package com.dailystudio.memory.where.weather;

import java.util.HashMap;
import java.util.Map;

import com.dailystudio.memory.where.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseIntArray;

public class WeatherIcon {

	public final static String PACK_CLIMACONS = "climacons";
	
	private final static Map<String, SparseIntArray> WEATHER_ICON_PACKS = 
			new HashMap<String, SparseIntArray>();
	
	private final static SparseIntArray CLIMACONS_PACK_ICONS =
			new SparseIntArray();
	
	static {
		CLIMACONS_PACK_ICONS.put(200, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(201, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(202, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(210, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(211, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(212, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(221, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(230, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(231, R.drawable.climacons_cloud_lightning);
		CLIMACONS_PACK_ICONS.put(232, R.drawable.climacons_cloud_lightning);

		CLIMACONS_PACK_ICONS.put(300, R.drawable.climacons_cloud_drizzle_alt);
		CLIMACONS_PACK_ICONS.put(301, R.drawable.climacons_cloud_drizzle_alt);
		CLIMACONS_PACK_ICONS.put(302, R.drawable.climacons_cloud_drizzle_alt);
		CLIMACONS_PACK_ICONS.put(310, R.drawable.climacons_cloud_drizzle_alt);
		CLIMACONS_PACK_ICONS.put(311, R.drawable.climacons_cloud_drizzle_alt);
		CLIMACONS_PACK_ICONS.put(312, R.drawable.climacons_cloud_drizzle_alt);
		CLIMACONS_PACK_ICONS.put(321, R.drawable.climacons_cloud_drizzle_alt);
		
		CLIMACONS_PACK_ICONS.put(500, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(501, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(502, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(503, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(504, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(511, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(520, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(521, R.drawable.climacons_cloud_rain);
		CLIMACONS_PACK_ICONS.put(522, R.drawable.climacons_cloud_rain);
		
		CLIMACONS_PACK_ICONS.put(600, R.drawable.climacons_cloud_snow_alt);
		CLIMACONS_PACK_ICONS.put(601, R.drawable.climacons_cloud_snow_alt);
		CLIMACONS_PACK_ICONS.put(602, R.drawable.climacons_cloud_snow_alt);
		CLIMACONS_PACK_ICONS.put(611, R.drawable.climacons_cloud_snow_alt);
		CLIMACONS_PACK_ICONS.put(621, R.drawable.climacons_cloud_snow_alt);
		
		CLIMACONS_PACK_ICONS.put(701, R.drawable.climacons_cloud_fog);
		CLIMACONS_PACK_ICONS.put(711, R.drawable.climacons_cloud_fog);
		CLIMACONS_PACK_ICONS.put(721, R.drawable.climacons_cloud_fog);
		CLIMACONS_PACK_ICONS.put(731, R.drawable.climacons_cloud_fog);
		CLIMACONS_PACK_ICONS.put(741, R.drawable.climacons_cloud_fog);
		
		CLIMACONS_PACK_ICONS.put(800, R.drawable.climacons_sun);
		CLIMACONS_PACK_ICONS.put(801, R.drawable.climacons_cloud);
		CLIMACONS_PACK_ICONS.put(802, R.drawable.climacons_cloud);
		CLIMACONS_PACK_ICONS.put(803, R.drawable.climacons_cloud);
		CLIMACONS_PACK_ICONS.put(804, R.drawable.climacons_cloud);

		WEATHER_ICON_PACKS.put(PACK_CLIMACONS, CLIMACONS_PACK_ICONS);
	}
	
	public static Drawable getIcon(Context context, 
			String iconPackName, int iconId) {
		if (context == null) {
			return null;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return null;
		}

		if (TextUtils.isEmpty(iconPackName)) {
			return null;
		}
		
		final SparseIntArray iconPack = 
				WEATHER_ICON_PACKS.get(iconPackName);
		if (iconPackName == null) {
			return null;
		}
		
		final int resid = iconPack.get(iconId, -1);
		if (resid <= 0) {
			return null;
		}
		
		return res.getDrawable(resid);
	}
	
}
