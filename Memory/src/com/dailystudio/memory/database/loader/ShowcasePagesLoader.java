package com.dailystudio.memory.database.loader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.plugin.MemoryPluginInfo;
import com.dailystudio.memory.plugin.PluginManager;
import com.dailystudio.memory.ui.ShowcasePage;

public class ShowcasePagesLoader extends AbsAsyncDataLoader<List<ShowcasePage>> {

	public ShowcasePagesLoader(Context context) {
		super(context);
	}
	
	@Override
	public List<ShowcasePage> loadInBackground() {
		List<MemoryPluginInfo> pInfos = PluginManager.listPlugins();
		if (pInfos == null) {
			return null;
		}
		
		List<ShowcasePage> pages = new ArrayList<ShowcasePage>();
		
		Uri uri = null;
		ShowcasePage sp = null;
		for (MemoryPluginInfo pInfo: pInfos) {
			uri = pInfo.getShowcasePage();
			if (uri == null) {
				continue;
			}
			
			sp = new ShowcasePage(pInfo.getPackageName(), 
					pInfo.getShowcasePage());
			Logger.debug("sp = %s", sp);
			
			pages.add(sp);
		}
		
		return pages;
	}

}
