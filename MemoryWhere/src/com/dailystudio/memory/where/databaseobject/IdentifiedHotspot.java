package com.dailystudio.memory.where.databaseobject;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.Directories;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class IdentifiedHotspot extends IdentifiedLocationObject {

    public static final Column COLUMN_SIMILARITY = 
            new DoubleColumn("similarity", false);
    
    public static final Column COLUMN_DURATION = 
            new LongColumn("duration");

    public static final Column COLUMN_OCCURRENCE = 
        new IntegerColumn("occurrence");
    
    public static final Column COLUMN_DISTRIB_BITS = 
            new IntegerColumn("distrib_bits");
        
    private final static Column[] sCloumns = {
        COLUMN_SIMILARITY,
        COLUMN_DURATION,
        COLUMN_OCCURRENCE,
        COLUMN_DISTRIB_BITS,
    };
    
    public IdentifiedHotspot(Context context) {
        super(context);
        
        final Template templ = getTemplate();
        
        templ.addColumns(sCloumns);
    }
    
    public void setSimilarity(double similarity) {
        setValue(COLUMN_SIMILARITY, similarity);
    }
    
    public double getSimilarity() {
        return getDoubleValue(COLUMN_SIMILARITY);
    }
    
    public void setDuration(long duration) {
        setValue(COLUMN_DURATION, duration);
    }

    public long getDuration() {
        return getLongValue(COLUMN_DURATION);
    }

    public void setOccurrence(int occurrence) {
        setValue(COLUMN_OCCURRENCE, occurrence);
    }

    public int getOccurrence() {
        return getIntegerValue(COLUMN_OCCURRENCE);
    }
    
    public void setDistribBits(int bits) {
        setValue(COLUMN_DISTRIB_BITS, bits);
    }
    
    public int getDistribBits() {
        return getIntegerValue(COLUMN_DISTRIB_BITS);
    }

    public String getLabel(Context context) {
		HotspotIdentity identity = getIdentity();
		if (identity == null) {
			return null;
		}
		
		HotspotIdentityInfo info = HotspotIdentifier.getIdentityInfo(identity);
		if (info == null || context == null) {
			return String.valueOf(identity);
		}

		return context.getString(info.labelResId);
    }
    
    public Bitmap getMapThumb(Context context) {
		final String mapfile = Directories.composeMapCachePath(context,
				getLatitude(),
				getLongitude());
		
    	Bitmap bitmap = null;
    	try {
    		bitmap = BitmapFactory.decodeFile(mapfile);
    	} catch (OutOfMemoryError e) {
    		Logger.warnning("could not load map thumb from [%s]: %s",
    				mapfile, e.toString());
    		
    		bitmap = null;
    	}

    	return bitmap;
    }
    
    @Override
    public String toString() {
        return String.format("%s, duration(%s), occurrence(%d), similarity(%f), distribs(%s)",
                super.toString(),
                CalendarUtils.durationToReadableString(getDuration()),
                getOccurrence(),
                getSimilarity(),
                HotspotIdentityInfo.to32BitsString(getDistribBits()));
    }
    
}
