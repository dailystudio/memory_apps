package com.dailystudio.memory.loader.step;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dailystudio.app.utils.ThumbCacheManager;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.database.MemoryPluginActivityCategoryObject;
import com.dailystudio.memory.database.MemoryPluginActivityObject;
import com.dailystudio.memory.database.MemoryPluginDatabaseModal;
import com.dailystudio.memory.database.MemoryPluginObject;
import com.dailystudio.memory.loader.MemoryLoaderStep;
import com.dailystudio.memory.plugin.MemoryPluginInfo;
import com.dailystudio.memory.plugin.PluginFactory;
import com.dailystudio.memory.plugin.PluginObserverable;
import com.dailystudio.nativelib.observable.NativeObservable;
import com.dailystudio.nativelib.observable.ObservableManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public abstract class BasePluginsStep extends MemoryLoaderStep {
	
	protected PackageManager mPackageManager;
	
	public BasePluginsStep(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		if (mContext != null) {
			mPackageManager = mContext.getPackageManager();
		}
	}
	
	List<MemoryPluginInfo> loadPlugins(String category) {
		return loadPlugins(category, null);
	}
	
	List<MemoryPluginInfo> loadPlugins(String category, String pkgName) {
        final PackageManager pm = mPackageManager;
        if (pm == null) {
        	return null;
        }

        List<MemoryPluginInfo> plugins = findAndCachePlugins(
        		pm, category, pkgName);
        
        findAndCacheActivities(pm, pkgName);
        
        clearUselessActivityCategories(pkgName);
        
        return plugins;
    }
	
	protected void clearDataForPackage(String pkgName) {
		if (pkgName == null) {
			return;
		}
		
        Set<String> foundPlugins = 
        	MemoryPluginDatabaseModal.listExistedPluginsInPackage(
        			mContext, pkgName);
        Set<String> foundActivities = 
        	MemoryPluginDatabaseModal.listExistedActivitiesInPackage(mContext, pkgName);

        MemoryPluginDatabaseModal.removePlugins(mContext, foundPlugins);
        MemoryPluginDatabaseModal.removeActivities(mContext, foundActivities);
        
        clearUselessActivityCategories(pkgName);
	}
	
	protected void clearCacheForPackage(String pkgName) {
		List<MemoryPluginActivityObject> activites =
			MemoryPluginDatabaseModal.listActivitiesInPackage(mContext, pkgName);
		if (activites != null) {
			for (MemoryPluginActivityObject activity: activites) {
				ThumbCacheManager.removeThumb(activity.getIconIdentifier());
			}
		}
		
		List<MemoryPluginActivityCategoryObject> categories =
			MemoryPluginDatabaseModal.listActivityCategoriesInPackage(mContext, pkgName);
		if (activites != null) {
			for (MemoryPluginActivityCategoryObject category: categories) {
				ThumbCacheManager.removeThumb(category.getIconIdentifier());
			}
		}
	}

	private List<MemoryPluginInfo> findAndCachePlugins(PackageManager pm,
			String category, String pkgName) {
        Intent intent = new Intent(Constants.ACTION_REGISTER_PLUGIN);
        
        if (category != null) {
        	intent.addCategory(category);
        } else {
        	intent.addCategory(Constants.CATEGORY_MAIN);
        }
        
        if (pkgName != null) {
            intent.setPackage(pkgName);
        }
        
        List<ResolveInfo> broadcastReceivers = pm.queryBroadcastReceivers(intent,
                PackageManager.GET_META_DATA);
//        Logger.debug("broadcastReceivers(%s)", broadcastReceivers);

        final int N = (broadcastReceivers == null ? 
        		0 : broadcastReceivers.size());
        
        Set<String> foundPlugins = 
        	MemoryPluginDatabaseModal.listExistedPluginsInPackage(
        			mContext, pkgName);
//        Logger.debug("foundPlugins(%s)", foundPlugins);
       
        List<MemoryPluginInfo> plugins = new ArrayList<MemoryPluginInfo>();
        
        @SuppressWarnings("unused")
		int count = 0;
        ResolveInfo rInfo = null;
        MemoryPluginInfo pInfo = null;
        MemoryPluginObject pluginObject = null;
        String compStr = null;
        for (int i = 0; i < N; i++) {
            rInfo = broadcastReceivers.get(i);
            
            if (rInfo == null) {
            	continue;
            }

            pInfo = PluginFactory.createPluginInfo(mContext, rInfo);
            if (pInfo == null) {
            	break;
            }
            
            compStr = pInfo.getComponent().flattenToShortString();
            
            plugins.add(pInfo);
            
            pluginObject = MemoryPluginDatabaseModal.addOrUpdatePlugin(mContext, pInfo);
            
            if (foundPlugins != null && pluginObject != null) {
            	compStr = pluginObject.getComponent();
            	
            	if (compStr != null && foundPlugins.contains(compStr)) {
//            		Logger.debug("SKIP still existed plugin: %s", compStr);
            		
            		foundPlugins.remove(compStr);
            	}
            }
            
        	count++;
        }

        MemoryPluginDatabaseModal.removePlugins(mContext, foundPlugins);
        
        return plugins;
	}
    	
	private void findAndCacheActivities(PackageManager pm, String pkgName) {
        Intent intent = new Intent(Constants.DAILY_LIFE_ACTION_MAIN);
        intent.addCategory(Constants.DAILY_LIFE_CATEGORY);
        
        if (pkgName != null) {
            intent.setPackage(pkgName);
        }
        
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 
        		PackageManager.GET_META_DATA);
//        Logger.debug("activities(%s)", activities);

        final int N = (activities == null ? 
        		0 : activities.size());
        
        Set<String> foundActivities = 
        	MemoryPluginDatabaseModal.listExistedActivitiesInPackage(mContext, pkgName);
//        Logger.debug("foundActivities(%s)", foundActivities);
        
        ResolveInfo rInfo = null;
        MemoryPluginActivityObject activityObject = null;
        String compStr = null;
        for (int i = 0; i < N; i++) {
            rInfo = activities.get(i);
            
            if (rInfo == null) {
            	continue;
            }
            
            activityObject = MemoryPluginDatabaseModal.addOrUpdatePluginActivity(
            		mContext, rInfo);
            if (foundActivities != null && activityObject != null) {
            	compStr = activityObject.getComponent();
            	
            	if (compStr != null && foundActivities.contains(compStr)) {
//            		Logger.debug("SKIP still existed activitiy: %s", compStr);
            		
            		foundActivities.remove(compStr);
            	}
            }
        }
        
        MemoryPluginDatabaseModal.removeActivities(mContext, foundActivities);
	}
	
	private void clearUselessActivityCategories(String pkgName) {
		Set<String> foundCategories = 
			MemoryPluginDatabaseModal.listExistedActivityCategoriesInPackage(
					mContext, pkgName);
//        Logger.debug("foundCategories(%s)", foundCategories);
		
        if (foundCategories == null) {
        	return;
        }
        
        Set<String> removeCategories = new HashSet<String>();
        
        List<MemoryPluginActivityObject> activities = null;
        for (String category: foundCategories) {
        	activities = MemoryPluginDatabaseModal.listActivitiesInCategory(
        			mContext, category);
        	if (activities != null && activities.size() > 0) {
/*        		Logger.debug("SKIP category with %d activities: %s", 
        				activities.size(),
        				category);
*/        		
        		continue;
        	}
        	
    		removeCategories.add(category);
       }
        
        MemoryPluginDatabaseModal.removeActivitCategories(mContext, removeCategories);
 	}
  
	void notifyPluginsChanged() {
		postAndWait(mRunnable);
	}
	
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			NativeObservable observable = 
				ObservableManager.getObservable(PluginObserverable.class);
			if (observable != null) {
				observable.notifyObservers();
			}
		}
		
	};
	
}
