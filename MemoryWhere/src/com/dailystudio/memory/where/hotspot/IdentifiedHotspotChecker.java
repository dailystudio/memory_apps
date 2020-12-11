package com.dailystudio.memory.where.hotspot;

import java.util.List;

import android.content.Context;

import com.dailystudio.app.async.PeroidicalAsyncChecker;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.memory.where.databaseobject.Hotspot;
import com.dailystudio.memory.where.databaseobject.HotspotDatabaseModal;

public class IdentifiedHotspotChecker extends PeroidicalAsyncChecker {

    public IdentifiedHotspotChecker(Context context) {
        super(context);
    }

    @Override
    public long getCheckInterval() {
        return (3 * CalendarUtils.HOUR_IN_MILLIS);
    }

    @Override
    protected void doCheck(long now, long lastTimestamp) {
        final List<Hotspot> hpCandidates = 
                HotspotDatabaseModal.getHotspotCandidates(mContext);
        if (hpCandidates == null || hpCandidates.size() <= 0) {
            return;
        }
        
        final HotspotFinder finder = new HotspotFinder();
        final List<Hotspot> hotspots = 
                finder.findHotspot(hpCandidates);
        if (hotspots == null || hotspots.size() <= 0) {
            return;
        }
        
        HotspotIdentifier.identifyAndSaveHotspots(mContext, hotspots);
    }
    
}
