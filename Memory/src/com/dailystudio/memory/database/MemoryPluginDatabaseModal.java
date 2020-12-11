package com.dailystudio.memory.database;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Xml;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.Constants;
import com.dailystudio.memory.boot.R;
import com.dailystudio.memory.plugin.MemoryPluginInfo;
import com.dailystudio.memory.utils.XmlHelper;

public class MemoryPluginDatabaseModal {

	public static MemoryPluginObject addOrUpdatePlugin(Context context, MemoryPluginInfo pInfo) {
		if (context == null || pInfo == null) {
			return null;
		}
		
		final ComponentName component = pInfo.getComponent();
		if (component == null) {
			return null;
		}
		
		TimeCapsuleDatabaseWriter<MemoryPluginObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginObject>(context, MemoryPluginObject.class);
		
		MemoryPluginObject pluginObject = findPlugin(context, pInfo);
		if (pluginObject != null) {
//            Logger.warnning("UPDATE existed plugin: pInfo = [%s]", pInfo);
            
    		pluginObject.setLabel(pInfo.getLabel());
    		pluginObject.setIcon(pInfo.getIconResource());
    		pluginObject.setTime(System.currentTimeMillis());
    		
            writer.update(pluginObject);
			
            return pluginObject;
		}
		
		pluginObject = new MemoryPluginObject(context);
		pluginObject.setLabel(pInfo.getLabel());
		pluginObject.setIcon(pInfo.getIconResource());
		pluginObject.setTime(System.currentTimeMillis());
		pluginObject.setComponent(component.flattenToShortString());
		pluginObject.setPakcage(component.getPackageName());
		pluginObject.setUsageCount(0);
		
		writer.insert(pluginObject);
		
		return pluginObject;
	}
	
	public static Set<String> listExistedPluginsInPackage(Context context, String pkgName) {
		if (context == null) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<MemoryPluginObject> reader =
			new TimeCapsuleDatabaseReader<MemoryPluginObject>(context, MemoryPluginObject.class);
		
		Query query = new Query(MemoryPluginObject.class);
		
		if (pkgName != null) {
			ExpressionToken selToken = 
				MemoryPluginObject.COLUMN_PACKAGE.eq(pkgName);
			if (selToken == null) {
				return null;
			}
			
			query.setSelection(selToken);
		}
		
		List<MemoryPluginObject> plugins = reader.query(query);
		if (plugins == null || plugins.size() <= 0) {
			return null;
		}
		
		Set<String> existedPlugins = new HashSet<String>();
		for (MemoryPluginObject p: plugins) {
			existedPlugins.add(p.getComponent());
		}
		
		return existedPlugins;
	}
	
	public static MemoryPluginObject findPlugin(Context context, MemoryPluginInfo pInfo) {
		if (context == null || pInfo == null) {
			return null;
		}
		
		final ComponentName component = pInfo.getComponent();
		if (component == null) {
			return null;
		}
		
		DatabaseReader<MemoryPluginObject> reader =
			new DatabaseReader<MemoryPluginObject>(context, MemoryPluginObject.class);

		Query query = reader.getQuery();
		
		ExpressionToken selToken = 
			MemoryPluginObject.COLUMN_COMPONENT.eq(component.flattenToShortString());
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		return reader.queryLastOne(query);
	}
	
	public static void removePlugins(Context context, Set<String> removePlugins) {
		Logger.debug("[TO REMOVE] plugins: [%s]", removePlugins);
		if (context == null || removePlugins == null) {
			return;
		}
		
		if (removePlugins.size() <= 0) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<MemoryPluginObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginObject>(context, MemoryPluginObject.class);
		
		Query query = new Query(MemoryPluginObject.class);
		
		ExpressionToken selToken = 
			MemoryPluginObject.COLUMN_COMPONENT.inValues(
					removePlugins.toArray(new String[0]));
		if (selToken == null) {
			return;
		}
		
		query.setSelection(selToken);
		
		writer.delete(query);
	}

	public static MemoryPluginActivityObject addOrUpdatePluginActivity(Context context,
			ResolveInfo rInfo) {
		if (context == null || rInfo == null) {
			return null;
		}
		
		TimeCapsuleDatabaseWriter<MemoryPluginActivityObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginActivityObject>(context, 
					MemoryPluginActivityObject.class);
		
		MemoryPluginActivityObject activityObject = findPluginActivity(context, rInfo);
		
		String category = null;
		
		MemoryPluginActivityCategoryObject categoryObject = 
			createPluginActivityCategory(context, rInfo);
		
		if (categoryObject != null) {
			category = categoryObject.getCategory();

			addOrUpdatePluginActivityCategory(context, categoryObject);
		}
		
		if (activityObject != null) {
/*            Logger.warnning("UPDATE existed activity: rInfo = [%s], activity = [%s]",
            		rInfo, activityObject);
*/            
            updatePluginActivityObject(context, activityObject, rInfo);
            
            if (category != null) {
            	activityObject.setCategory(category);
            }
            
            writer.update(activityObject);
			
            return activityObject;
		}
		
		activityObject = createPluginActivityObject(context, rInfo);
		
        if (category != null) {
        	activityObject.setCategory(category);
        }

		writer.insert(activityObject);
		
		addOrUpdatePluginActivityCategory(context, categoryObject);
		
		return activityObject;
	}

	public static List<MemoryPluginActivityObject> listActivitiesInPackage(Context context, String pkgName) {
		if (context == null) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<MemoryPluginActivityObject> reader = 
			new TimeCapsuleDatabaseReader<MemoryPluginActivityObject>(context, MemoryPluginActivityObject.class);
		
		Query query = new Query(MemoryPluginActivityObject.class);
		
		if (pkgName != null) {
			ExpressionToken selToken = 
				MemoryPluginActivityObject.COLUMN_PACKAGE.eq(pkgName);
			if (selToken == null) {
				return null;
			}
			
			query.setSelection(selToken);
		}
		
		return reader.query(query);
	}

	public static Set<String> listExistedActivitiesInPackage(Context context, String pkgName) {
		List<MemoryPluginActivityObject> activities = 
			listActivitiesInPackage(context, pkgName);
		if (activities == null || activities.size() <= 0) {
			return null;
		}
		
		Set<String> existedActivities = new HashSet<String>();
		for (MemoryPluginActivityObject p: activities) {
			existedActivities.add(p.getComponent());
		}
		
		return existedActivities;
	}
	
	public static List<MemoryPluginActivityObject> listActivitiesInCategory(
			Context context, String category) {
		if (context == null || category == null) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<MemoryPluginActivityObject> reader = 
			new TimeCapsuleDatabaseReader<MemoryPluginActivityObject>(context, 
					MemoryPluginActivityObject.class);
		
		Query query = new Query(MemoryPluginActivityObject.class);
		
		ExpressionToken selToken = 
			MemoryPluginActivityObject.COLUMN_CATEGORY.eq(category);
		if (selToken == null) {
			return null;
		}
			
		query.setSelection(selToken);
		
		return reader.query(query);
	}

	public static void removeActivities(Context context, Set<String> removeActivities) {
		Logger.debug("[TO REMOVE] activities: [%s]", removeActivities);
		if (context == null || removeActivities == null) {
			return;
		}
		
		if (removeActivities.size() <= 0) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<MemoryPluginActivityObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginActivityObject>(context, 
					MemoryPluginActivityObject.class);
		
		Query query = new Query(MemoryPluginActivityObject.class);
		
		ExpressionToken selToken = 
			MemoryPluginActivityObject.COLUMN_COMPONENT.inValues(
					removeActivities.toArray(new String[0]));
		if (selToken == null) {
			return;
		}
		
		query.setSelection(selToken);
		
		writer.delete(query);
	}

	private static void addOrUpdatePluginActivityCategory(Context context,
			MemoryPluginActivityCategoryObject categoryObject) {
		if (context == null || categoryObject == null) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<MemoryPluginActivityCategoryObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginActivityCategoryObject>(context, 
					MemoryPluginActivityCategoryObject.class);
		
		MemoryPluginActivityCategoryObject oldCategoryObject = findPluginActivityCategory(
				context, categoryObject.getPackage(), categoryObject.getCategory());
		
		if (oldCategoryObject != null) {
/*            Logger.warnning("UPDATE existed category: newCategoryObject = [%s]",
            		categoryObject);
*/            
            oldCategoryObject.setLabel(categoryObject.getLabel());
            oldCategoryObject.setIcon(categoryObject.getIcon());
            
            writer.update(oldCategoryObject);
			
            return;
		}
		
		writer.insert(categoryObject);
	}
	
	public static List<MemoryPluginActivityCategoryObject> listActivityCategoriesInPackage(
			Context context, String pkgName) {
		if (context == null) {
			return null;
		}
		
		TimeCapsuleDatabaseReader<MemoryPluginActivityCategoryObject> reader = 
			new TimeCapsuleDatabaseReader<MemoryPluginActivityCategoryObject>(context, 
					MemoryPluginActivityCategoryObject.class);
		
		Query query = new Query(MemoryPluginActivityCategoryObject.class);
		
		if (pkgName != null) {
			ExpressionToken selToken = 
				MemoryPluginActivityCategoryObject.COLUMN_PACKAGE.eq(pkgName);
			if (selToken == null) {
				return null;
			}
			
			query.setSelection(selToken);
		}
		
		return reader.query(query);
	}
	
	public static Set<String> listExistedActivityCategoriesInPackage(Context context, String pkgName) {
		List<MemoryPluginActivityCategoryObject> categories = 
			listActivityCategoriesInPackage(context, pkgName);
		if (categories == null || categories.size() <= 0) {
			return null;
		}
		
		Set<String> existedCategories = new HashSet<String>();
		for (MemoryPluginActivityCategoryObject p: categories) {
			existedCategories.add(p.getCategory());
		}
		
		return existedCategories;
	}

	public static void removeActivitCategories(Context context, Set<String> removeCategories) {
		Logger.debug("[TO REMOVE] categories: [%s]", removeCategories);
		if (context == null || removeCategories == null) {
			return;
		}
		
		if (removeCategories.size() <= 0) {
			return;
		}
		
		TimeCapsuleDatabaseWriter<MemoryPluginActivityCategoryObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginActivityCategoryObject>(context, 
					MemoryPluginActivityCategoryObject.class);
		
		Query query = new Query(MemoryPluginActivityCategoryObject.class);
		
		ExpressionToken selToken = 
			MemoryPluginActivityCategoryObject.COLUMN_CATEGORY.inValues(
					removeCategories.toArray(new String[0]));
		if (selToken == null) {
			return;
		}
		
		query.setSelection(selToken);
		
		writer.delete(query);
	}
	
	public static MemoryPluginActivityObject findPluginActivity(Context context,
			ResolveInfo rInfo) {
		final ComponentName component = toComponentName(rInfo);
		if (component == null) {
			return null;
		}
		
		return findPluginActivity(context, component);
	}
	
	public static MemoryPluginActivityObject findPluginActivity(Context context,
			ComponentName component) {
		if (context == null || component == null) {
			return null;
		}
		
		DatabaseReader<MemoryPluginActivityObject> reader =
			new DatabaseReader<MemoryPluginActivityObject>(context, 
					MemoryPluginActivityObject.class);

		Query query = reader.getQuery();
		
		ExpressionToken selToken = 
			MemoryPluginActivityObject.COLUMN_COMPONENT.eq(component.flattenToShortString());
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		return reader.queryLastOne(query);
	}
	
    private static MemoryPluginActivityObject createPluginActivityObject(Context context, 
    		ResolveInfo rInfo) {
    	if (context == null || rInfo == null) {
    		return null;
    	}
    	
		final ActivityInfo activityInfo = rInfo.activityInfo;
		if (activityInfo == null) {
			return null;
		}

		final ApplicationInfo applicationInfo = activityInfo.applicationInfo;
		if (applicationInfo == null) {
			return null;
		}

		MemoryPluginActivityObject activity = new MemoryPluginActivityObject(context);
    	activity.setTime(System.currentTimeMillis());
    	activity.setCategory(Constants.DAILY_ACTIVITY_CATEGORY_DEFAILT);
    	activity.setUsageCount(0l);
    	activity.setIcon(activityInfo.icon);
    	
        final ComponentName component = toComponentName(rInfo);
        activity.setComponent(component.flattenToShortString());
		activity.setPakcage(component.getPackageName());
		
        updatePluginActivityObject(context, activity, rInfo);
        
        return activity;
    }
    
    private static void updatePluginActivityObject(Context context, 
    		MemoryPluginActivityObject activity, ResolveInfo rInfo) {
    	if (context == null || activity == null || rInfo == null) {
    		return;
    	}
    	
    	if (rInfo.activityInfo == null) {
    		return;
    	}
    	
    	final PackageManager pm = context.getPackageManager();
    	if (pm == null) {
    		return;
    	}
    	
    	CharSequence title = rInfo.loadLabel(pm);
        if (title == null) {
        	title = rInfo.activityInfo.name;
        }
        
        activity.setLabel(title == null ? null : title.toString());
        activity.setIcon(rInfo.activityInfo.icon);
        
//        mpao.icon = activityInfo.loadIcon(pm);
        
    }
    
    public static ComponentName toComponentName(ResolveInfo rInfo) {
    	if (rInfo == null) {
    		return null;
    	}
    	
		final ActivityInfo activityInfo = rInfo.activityInfo;
		if (activityInfo == null) {
			return null;
		}

		final ApplicationInfo applicationInfo = activityInfo.applicationInfo;
		if (applicationInfo == null) {
			return null;
		}

    	return new ComponentName(applicationInfo.packageName, 
    			activityInfo.name);
    } 
    
    private static MemoryPluginActivityCategoryObject findPluginActivityCategory(
			Context context, String pkgName, String category) {
		if (context == null || pkgName == null || category == null) {
			return null;
		}
		
		DatabaseReader<MemoryPluginActivityCategoryObject> reader =
			new DatabaseReader<MemoryPluginActivityCategoryObject>(context, 
					MemoryPluginActivityCategoryObject.class);

		Query query = reader.getQuery();
		
		ExpressionToken selToken = 
			MemoryPluginActivityCategoryObject.COLUMN_PACKAGE.eq(pkgName)
				.and(MemoryPluginActivityCategoryObject.COLUMN_CATEGORY.eq(category));
		if (selToken == null) {
			return null;
		}
		
		query.setSelection(selToken);
		
		return reader.queryLastOne(query);
	}

	private static MemoryPluginActivityCategoryObject createDefaultCategory(Context context) {
		if (context == null) {
			return null;
		}
		
		MemoryPluginActivityCategoryObject category = 
			new MemoryPluginActivityCategoryObject(context);
		
		category.setCategory(Constants.DAILY_ACTIVITY_CATEGORY_DEFAILT);
		category.setPakcage(context.getPackageName());
		category.setIcon(R.drawable.ic_launcher);
		category.setLabel(context.getString(R.string.default_category_label));
		category.setTime(System.currentTimeMillis());
		category.setUsageCount(0l);
		
		return category;
    }
    
	private static MemoryPluginActivityCategoryObject createPluginActivityCategory(
			Context context, ResolveInfo rInfo) {
		if (context == null || rInfo == null) {
			return null;
		}
		
		MemoryPluginActivityCategoryObject categoryObject = 
			createDefaultCategory(context);
		if (categoryObject == null) {
			return null;
		}
		    	
    	final PackageManager pm = context.getPackageManager();
    	if (pm == null) {
    		return categoryObject;
    	}
    	
		final ActivityInfo activityInfo = rInfo.activityInfo;
		if (activityInfo == null) {
    		return categoryObject;
		}

		categoryObject.setPakcage(activityInfo.packageName);
		
		final ApplicationInfo applicationInfo = activityInfo.applicationInfo;
		if (applicationInfo == null) {
    		return categoryObject;
		}
		
		Resources res = null;
		try {
			res = pm.getResourcesForApplication(
					applicationInfo);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			
			res = null;
		}

		XmlPullParser parser = 
			activityInfo.loadXmlMetaData(pm, Constants.META_DATA_DAILY_ACTIVITY);
		if (parser == null) {
			return categoryObject;
		}
		
        AttributeSet attrs = Xml.asAttributeSet(parser);
        if (attrs == null) {
    		return categoryObject;
        }
        
       try {
        	int type;
            while ((type=parser.next()) != XmlPullParser.END_DOCUMENT
                    && type != XmlPullParser.START_TAG) {
                // drain whitespace, comments, etc.
            }
            
            String nodeName = parser.getName();
            if (Constants.XML_TAG_ACTIVITY_CATEGORY.equals(nodeName) == false) {
        		return categoryObject;
            }
            
            int attrCount = attrs.getAttributeCount();
            if (attrCount <= 0) {
        		return categoryObject;
            }
            
            String category = null;
            String label = null;
            int iconResId = -1;
            
            String attrName = null;
            
            for (int i = 0; i < attrCount; i++) {
            	attrName = attrs.getAttributeName(i);

            	if (Constants.META_ATTR_CATEGORY.equals(attrName)) {
            		category = XmlHelper.parseLabel(attrs, i, res);
            	} else if (Constants.META_ATTR_LABEL.equals(attrName)) {
            		label = XmlHelper.parseLabel(attrs, i, res);
            	} else if (Constants.META_ATTR_ICON.equals(attrName)) {
            		iconResId = XmlHelper.parseResource(attrs, i, res);
            	}
      
            }
            
            if (category != null) {
            	categoryObject.setCategory(category);
            }
            
            if (label != null) {
            	categoryObject.setLabel(label);
            }
            
            if (iconResId != -1) {
            	categoryObject.setIcon(iconResId);
            }
	    } catch (XmlPullParserException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }

	    return categoryObject;
	}
	
	public static void increaseActivityCategoryUsageCount(Context context,
			String pkgName,
			String category) {
		if (context == null || pkgName == null || category == null) {
			return;
		}
		
		MemoryPluginActivityCategoryObject activityCategory =
			findPluginActivityCategory(context, pkgName, category);
		if (activityCategory == null) {
			return;
		}
		
		final long prevUsageCount = activityCategory.getUsageCount();
		
		activityCategory.setUsageCount(prevUsageCount + 1);
		
		TimeCapsuleDatabaseWriter<MemoryPluginActivityCategoryObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginActivityCategoryObject>(
					context, MemoryPluginActivityCategoryObject.class);
		
		writer.update(activityCategory);
	}

	public static void increaseActivityUsageCount(Context context,
			ComponentName component) {
		if (context == null || component == null) {
			return;
		}
		
		MemoryPluginActivityObject activity =
			findPluginActivity(context, component);
		if (activity == null) {
			return;
		}
		
		final long prevUsageCount = activity.getUsageCount();
		
		activity.setUsageCount(prevUsageCount + 1);
		
		TimeCapsuleDatabaseWriter<MemoryPluginActivityObject> writer = 
			new TimeCapsuleDatabaseWriter<MemoryPluginActivityObject>(
					context, MemoryPluginActivityObject.class);
		
		writer.update(activity);
	}

}
