package com.dailystudio.memory.where.databaseobject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;

public class IdentifiedLocationObject extends BaseLocationObject {
    
	public static final Column COLUMN_IDENTITY = 
            new TextColumn("identity", false);

    private final static Column[] sCloumns = {
        COLUMN_IDENTITY,
    };
    
    public IdentifiedLocationObject(Context context) {
        super(context);
        
        final Template templ = getTemplate();
        
        templ.addColumns(sCloumns);
    }
    
    public void setIdentity(HotspotIdentity identity) {
        if (identity == null) {
            return;
        }
        
        setValue(COLUMN_IDENTITY, String.valueOf(identity));
    }
    
    public HotspotIdentity getIdentity() {
        String idstr = getTextValue(COLUMN_IDENTITY);
        if (idstr == null) {
            return null;
        }
    
        HotspotIdentity hi = null;
        try {
            hi = HotspotIdentity.valueOf(idstr);
        } catch (Exception e) {
            Logger.warnning("parse identity failure: idstr = %s (%s)",
                    idstr,
                    e.toString());
            hi = null;
        }
        
        return hi;
    }
    
    @Override
    public String toString() {
        return String.format("%s, identity(%20s)",
                super.toString(),
                getIdentity());
    }
    
}
