package com.dailystudio.memory.application.appwidget;

import java.util.List;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.dailystudio.app.utils.BitmapUtils;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Constants;
import com.dailystudio.memory.application.R;
import com.dailystudio.memory.application.databaseobject.FavoriteApp;
import com.dailystudio.memory.appwidget.AppWidgetDataAsyncTask;
import com.dailystudio.nativelib.application.AndroidApplication;

public class  FavoriteAppsWidgetDataAsyncTask extends
		AppWidgetDataAsyncTask<FavoriteApp> {

	private boolean mIncludeSysApps = false;
	private int mFavoriteClass = Constants.FAVORITE_CLASS_WEEK;

	public FavoriteAppsWidgetDataAsyncTask(Context context) {
		super(context);
	}

	public FavoriteAppsWidgetDataAsyncTask(Context context,
			boolean incSysApps, int favorite) {
		super(context);
		
		mIncludeSysApps = incSysApps;
		mFavoriteClass = favorite;
	}
	
	protected boolean isIncludeSysApps() {
		return mIncludeSysApps;
	}
	
	protected int getFavoriteClass() {
		return mFavoriteClass;
	}

	@Override
	protected Query getQuery(Class<FavoriteApp> klass) {
		Query query = new Query(FavoriteApp.class);
		
		ExpressionToken selToken = 
				FavoriteApp.COLUMN_FAVORTIE_CLASS.eq(mFavoriteClass);
		if (!mIncludeSysApps && selToken != null) {
			selToken = selToken.and(FavoriteApp.COLUMN_SYSTEM_APP.eq(0));
		}
		
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		final int count = getContext().getResources().getInteger(
				R.integer.config_app_widget_favorties_count);
		
		ExpressionToken limitToken = new ExpressionToken(count);
			
		query.setLimit(limitToken);
		
		return query;
	}

	@Override
	protected RemoteViews bindRemoteViews(List<FavoriteApp> data) {
		Logger.debug("data = %s", data);
		final Context context = getContext();
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_favortie_apps);

		final Resources res = context.getResources();
		if (res == null) {
			return remoteViews;
		}
		
		final int N = getContext().getResources().getInteger(
				R.integer.config_app_widget_favorties_count);
		
		String slotIdName = null;
		int slotId = -1;
		FavoriteApp fapp = null;
		for (int i = 0; i < N; i++) {
			slotIdName = String.format("fapp_slot_%d", i);
			
			slotId = res.getIdentifier(slotIdName, "id", context.getPackageName());
			Logger.debug("%s = 0x%08x",
					slotIdName, slotId);
			
			if (data == null 
					|| i < 0 || i >= data.size()) {
				fapp = null;
			} else {
				fapp = data.get(i);
			}
			
			bindFavoriteApp(context, remoteViews, slotId, fapp);
		}
		
		return remoteViews;
	}

	private void bindFavoriteApp(Context context, RemoteViews parentRemoteViews, 
			int slotViewId, FavoriteApp fapp) {
		if (context == null || parentRemoteViews == null 
				|| slotViewId <= 0) {
			return;
		}
		
		RemoteViews nestedView = new RemoteViews(context.getPackageName(),
				R.layout.layout_app_widget_favorite_app);
		
		final Resources res = context.getResources();
		
		final int iconw = res.getDimensionPixelSize(R.dimen.app_widget_res_icon_size);
		final int iconh = res.getDimensionPixelSize(R.dimen.app_widget_res_icon_size);
		
		if (fapp != null) {
			nestedView.setViewVisibility(R.id.res_label, View.VISIBLE);
			nestedView.setViewVisibility(R.id.res_icon, View.VISIBLE);

			fapp.setHiResIconRequired(true);
			fapp.setIconDimension(iconw, iconh);
			fapp.resolveResources(context);
		
			nestedView.setTextViewText(R.id.res_label, fapp.getLabel());
	
	        Drawable d = fapp.getIcon();

	        final AndroidApplication app = 
					new AndroidApplication(fapp.getPackageName());
	        final boolean existed = app.isInstalled(context);
	       
	        Intent launchIntent = null;
	        if (existed) {
		        launchIntent = app.getLaunchIntent(context);
	        }
	        
			if (d instanceof BitmapDrawable) {
				Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
				if (!existed) {
					bitmap = BitmapUtils.createGrayScaledBitmap(bitmap);
				}
				
				nestedView.setImageViewBitmap(R.id.res_icon, 
						bitmap);
			}
			
			PendingIntent pendingIntent = null;
	        if (launchIntent != null) {
	        	pendingIntent = PendingIntent.getActivity(
		        		context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        }

	        parentRemoteViews.setOnClickPendingIntent(
	        		slotViewId, pendingIntent);
		} else {
			nestedView.setViewVisibility(R.id.res_label, View.INVISIBLE);
			nestedView.setViewVisibility(R.id.res_icon, View.VISIBLE);
			
			if (Build.VERSION.SDK_INT < 11) { // Android v3.0+
				nestedView.setImageViewResource(R.id.res_icon,
						R.drawable.ic_app_favorite_empty);
			} else {
				final AndroidApplication app = 
						new AndroidApplication(context.getPackageName());

				Drawable d = app.getFullResIcon(context, 
						context.getPackageName(),
							R.drawable.ic_app_favorite_empty);
				if (d instanceof BitmapDrawable) {
					nestedView.setImageViewBitmap(R.id.res_icon, 
							((BitmapDrawable)d).getBitmap());
				}
			}

			nestedView.setTextViewText(R.id.res_label, null);

			Intent i = new Intent();
	        
	        PendingIntent pendingIntent = PendingIntent.getActivity(
	        		context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
	
	        parentRemoteViews.setOnClickPendingIntent(
	        		slotViewId, pendingIntent);
		}
		
        parentRemoteViews.removeAllViews(slotViewId);
		parentRemoteViews.addView(slotViewId, nestedView);
	}
	
	@Override
	protected ComponentName getAppWidgetProvider() {
		return new ComponentName(getContext(), 
				FavoriteAppsWidgetProvider.class);
	}

	@Override
	protected Class<FavoriteApp> getObjectClass() {
		return FavoriteApp.class;
	}

}
