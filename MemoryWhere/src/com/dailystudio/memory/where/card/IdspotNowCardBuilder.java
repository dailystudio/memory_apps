package com.dailystudio.memory.where.card;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.card.CardBuilder;
import com.dailystudio.memory.card.CardElements;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspotDatabaseModal;
import com.dailystudio.memory.where.databaseobject.IdspotHistory;
import com.dailystudio.memory.where.databaseobject.IdspotHistoryDatabaseModal;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

public class IdspotNowCardBuilder extends CardBuilder {
	
	private final static String IDSPOT_CARD_TEMPL = "card_idspot_now_templ.html";

	private final static String RPL_KEY_IDSPOT_NAME = "idspot_name";
	private final static String RPL_KEY_IDSPOT_MAP = "idspot_map";
	
	public IdspotNowCardBuilder() {
		super(IDSPOT_CARD_TEMPL, Cards.CARD_IDSPOT_NOW_FILE);
	}
	
	@Override
	protected void buildCardElements(Context context, CardElements elements) {
		String titleVal = null;
		String mapVal = null;
		
		IdspotHistory history = 
				IdspotHistoryDatabaseModal.getLastHistory(context);
		Logger.debug("history = %s", history);
		if (history != null && history.getDuration() <= 0) {
			IdentifiedHotspot idspot = getIdspot(context, 
					history.getIdentifiedHotspotId());
			Logger.debug("idspot = %s", idspot);
			if (idspot != null) {
				final String label = idspot.getLabel(context);
				if (!TextUtils.isEmpty(label)) {
					titleVal = String.format(context.getString(R.string.dashboard_idpost_now),
							label);
				}
				
		    	Bitmap bitmap = idspot.getMapThumb(context);
		    	if (bitmap != null) {
		    		mapVal = bitmapToBase64String(bitmap);
		    	}
			}
		}
		
    	elements.putElement(RPL_KEY_IDSPOT_NAME, titleVal);
    	elements.putElement(RPL_KEY_IDSPOT_MAP, mapVal);
	}
	
	private IdentifiedHotspot getIdspot(Context context,int idspotId) {
		return IdentifiedHotspotDatabaseModal.getIdentifiedHotspot(context, 
				idspotId);
	}

}
