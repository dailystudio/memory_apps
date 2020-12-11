package com.dailystudio.memory.ui;

import com.dailystudio.memory.R;

import android.content.Context;

import com.dailystudio.memory.database.MemoryPluginActivityCategoryObject;

public class PluginActivityCategoryAdapter 
	extends ResourceObjectAdapter<MemoryPluginActivityCategoryObject> {

	public PluginActivityCategoryAdapter(Context context) {
		super(context, R.layout.category_list_item, MemoryPluginActivityCategoryObject.class);
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
