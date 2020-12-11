package com.dailystudio.memory.where.hotspot;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.dailystudio.app.async.PeroidicalAsyncChecker;
import com.dailystudio.app.utils.BitmapUtils;
import com.dailystudio.app.utils.FileUtils;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Directories;
import com.dailystudio.memory.where.card.Cards;
import com.dailystudio.memory.where.card.WhereCardsUpdater;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.locationapi.AbsLocationApi;
import com.dailystudio.memory.where.locationapi.LocationApiManager;

public class IdentifiedHotspotMapsChecker extends PeroidicalAsyncChecker {

    public IdentifiedHotspotMapsChecker(Context context) {
        super(context);
    }

    @Override
    public long getCheckInterval() {
        return (CalendarUtils.DAY_IN_MILLIS);
    }

    @Override
    protected void doCheck(long now, long lastTimestamp) {
    	List<IdentifiedHotspot> idspots = 
    			listIdentifiedHotspots(mContext);
    	
    	if (idspots == null) {
    		return;
    	}
    	
    	for (IdentifiedHotspot idspot: idspots) {
    		cacheMapForIdentifiedHotspot(mContext, idspot);
    	}
    	
    	updateNowhereCard(mContext);
    }
    
    private void cacheMapForIdentifiedHotspot(Context context,
    		IdentifiedHotspot idspot) {
    	if (context == null || idspot == null) {
    		return;
    	}
    	
    	final double lat = idspot.getLatitude();
    	final double lon = idspot.getLongitude();
		final double alt = idspot.getAltitude();

		AbsLocationApi api = LocationApiManager.getDefaultApi(context);
		if (api == null) {
			return;
		}
    	
    	Bitmap mapThumb = api.getMapThumbnail(lat, lon, alt);
    	if (mapThumb == null) {
    		return;
    	}
    	
    	String mapfile = Directories.composeMapCachePath(
    			mContext, lat, lon);
    	
    	boolean ret = FileUtils.checkOrCreateFile(mapfile);
    	if (ret) {
        	ret = BitmapUtils.saveBitmap(mapThumb, mapfile);
    	}
    	
    	Logger.debug("cache map[%s] in %s for: idspot[%s], success = %s",
				mapThumb, mapfile, idspot, ret);
	}

    private List<IdentifiedHotspot> listIdentifiedHotspots(Context context) {
    	TimeCapsuleDatabaseReader<IdentifiedHotspot> reader = 
    			new TimeCapsuleDatabaseReader<IdentifiedHotspot>(context,
    					IdentifiedHotspot.class);
    	
    	Query query = new Query(IdentifiedHotspot.class);
    	
    	return reader.query(query);
    }
    
    private void updateNowhereCard(final Context context) {
    	new WhereCardsUpdater(context, Cards.CARD_IDSPOT_NOW).doUpdate();
    }
    
}
