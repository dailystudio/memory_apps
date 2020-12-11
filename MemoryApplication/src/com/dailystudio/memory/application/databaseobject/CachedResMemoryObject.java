package com.dailystudio.memory.application.databaseobject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.app.utils.BitmapUtils;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.application.Directories;

public abstract class CachedResMemoryObject extends AbsResMemoryObject {
	
	private String mCachedLabel;
	private Drawable mCachedIcon;
	
	public CachedResMemoryObject(Context context) {
		super(context);
	}
	
	abstract protected String getResourcePackage();
	
	@Override
	public Drawable getIcon() {
		Drawable icon = super.getIcon();
		
		if (icon == null) {
			icon = mCachedIcon;
		}
		
		return icon;
	}

	@Override
	public CharSequence getLabel() {
		CharSequence label = mCachedLabel;
		
		if (label == null) {
			label = super.getLabel();
		}
		
		return label;
	}

	@Override
	public void resolveResources(Context context) {
		super.resolveResources(context);
		
		final CharSequence label = getLabel();
		if (label == null 
				|| label.equals(getResourcePackage())) {
			mCachedLabel = getCachedLabel();
		}
		
		if (getIcon() == null) {
			mCachedIcon = getCachedIcon();
		}
	}
	
	public String getCachedLabel() {
		String cachedLabel = null;
		
		DatabaseReader<AppLabelCache> reader = 
				new DatabaseReader<AppLabelCache>(mContext,
						AppLabelCache.class);
		
		Query query = new Query(AppLabelCache.class);
		
		ExpressionToken selToken = 
				AppLabelCache.COLUMN_PACKAGE_NAME.eq(getResourcePackage());
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		AppLabelCache labelCache = reader.queryLastOne(query);
		Logger.debug("labelCache = %s", labelCache);
		if (labelCache != null) {
			cachedLabel = labelCache.getCachedLabel();
		}
		
		return cachedLabel;
	}
	
	public Drawable getCachedIcon() {
		Drawable cachedIcon = null;
		
		final String resCachePath = getIconCachePath();
		
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(resCachePath);
		} catch (OutOfMemoryError e) {
			Logger.warnning("read cache icon for pkg[%s] failure: %s",
					getResourcePackage(),
					e.toString());
			bitmap = null;
		}

		if (bitmap != null) {
			cachedIcon = new BitmapDrawable(mContext.getResources(), bitmap);
		}
		
		return cachedIcon;
	}
	
	public void cacheAppResources() {
		if (isResourcesResolved() == false) {
			resolveResources(mContext);
		}
		
		final CharSequence label = getLabel();
		if (label != null) {
			DatabaseConnectivity connectivity = 
					new DatabaseConnectivity(mContext,
							AppLabelCache.class);
			
			AppLabelCache labelCache = new AppLabelCache(mContext);
			
			labelCache.setTime(System.currentTimeMillis());
			labelCache.setPackageName(getResourcePackage());
			labelCache.setCachedLabel(label.toString());
			
			connectivity.insert(labelCache);
		}
		
		final Drawable icon = getIcon();
		if (icon instanceof BitmapDrawable) {
			final String resCachePath = getIconCachePath();
			if (resCachePath != null) {
				final Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
				if (bitmap != null) {
					BitmapUtils.saveBitmap(bitmap, resCachePath);
				}
			}
		}
	}
	
	public String getLabelCachePath() {
		return composeResourceCachePath("label");
	}
	
	public String getIconCachePath() {
		return composeResourceCachePath("icon");
	}
	
	public String composeResourceCachePath(String resType) {
		if (resType == null) {
			return null;
		}
		
		String cachesDir = Directories.getAppResCachesDirectory(mContext);
		if (cachesDir == null) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder(cachesDir);
		
		builder.append("/");
		builder.append(getResourcePackage());
		builder.append(".");
		builder.append(resType);
		
		return builder.toString();
	}
	
}
