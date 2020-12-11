package com.dailystudio.memory.where.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.development.Logger;
import com.dailystudio.memory.where.R;
import com.dailystudio.memory.where.databaseobject.IdentifiedHotspot;
import com.dailystudio.memory.where.hotspot.HotspotIdentifier;
import com.dailystudio.memory.where.hotspot.HotspotIdentity;
import com.dailystudio.memory.where.hotspot.HotspotIdentityInfo;

public class NowWhereIdspotAdapter extends ArrayAdapter<IdentifiedHotspot> {
	
	public NowWhereIdspotAdapter(Context context) {
		super(context, 0, new ArrayList<IdentifiedHotspot>());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = getContext();
		
		if (convertView == null)  {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.now_where_idspot_item, null);
		}
		
		final IdentifiedHotspot idspot = getItem(position);
		Logger.debug("idspot = %s", idspot);
		final HotspotIdentity identity = idspot.getIdentity();
		Logger.debug("identity = %s", identity);
		
		if (idspot != null
		        && identity != null
		        && convertView != null) {
		    final HotspotIdentityInfo hiInfo = 
		            HotspotIdentifier.getIdentityInfo(identity);
			Logger.debug("hiInfo = %s", hiInfo);
		    
		    ImageView iconView = (ImageView) convertView.findViewById(R.id.loc_icon);
		    if (iconView != null && hiInfo != null) {
		        iconView.setImageResource(hiInfo.iconResId);
		    }

			TextView typeView = (TextView) convertView.findViewById(R.id.idspot_type);
			if (typeView != null && hiInfo != null) {
				typeView.setText(hiInfo.labelResId);
			}
	
			Logger.debug("idspot = %s", idspot);
            TextView similarityView = (TextView) convertView.findViewById(
                    R.id.idspot_similarity);
            if (similarityView != null) {
                final String simLabel = context.getString(
                        R.string.label_similarity);
                double similarity = 0.f;
                
                if (idspot != null) {
                	similarity = idspot.getSimilarity();
                }
                
                similarityView.setText(String.format("(%s: %2.1f%%)",
                        simLabel, similarity * 100));
            }
		}
		
		return convertView;
	}
	
}
