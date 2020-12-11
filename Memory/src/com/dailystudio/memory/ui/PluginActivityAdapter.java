package com.dailystudio.memory.ui;

import com.dailystudio.memory.R;

import android.content.Context;

import com.dailystudio.memory.database.MemoryPluginActivityObject;

public class PluginActivityAdapter 
	extends ResourceObjectAdapter<MemoryPluginActivityObject> {

	public PluginActivityAdapter(Context context) {
		super(context, R.layout.activity_list_item, MemoryPluginActivityObject.class);
	}

    @Override
    protected int getIconScaledHeight() {
        return (int)mContext.getResources().getDimension(R.dimen.plugin_icon_size);
    }

    @Override
    protected int getIconScaledWidth() {
        return (int)mContext.getResources().getDimension(R.dimen.plugin_icon_size);
    }

}
