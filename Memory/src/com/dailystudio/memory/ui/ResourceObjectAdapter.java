package com.dailystudio.memory.ui;

import com.dailystudio.app.utils.BitmapUtils;
import com.dailystudio.memory.R;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.app.utils.ThumbAsyncDecoder;
import com.dailystudio.app.utils.ThumbCacheManager;
import com.dailystudio.app.widget.SimpleDatabaseObjectCursorAdapter;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.memory.database.MemoryResouceObject;

public class ResourceObjectAdapter <T extends MemoryResouceObject>
	extends SimpleDatabaseObjectCursorAdapter<T> implements Observer {

	public ResourceObjectAdapter(Context context, 
			int layout, Class<? extends T> klass) {
		this(context, layout, klass, DatabaseObject.VERSION_LATEST);
	}
	
	public ResourceObjectAdapter(Context context, 
			int layout, Class<? extends T> klass, int version) {
		super(context, layout, klass, version);
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		if (view == null) {
			return;
		}
		
		final T resObject = dumpItem(c);
		if (resObject == null) {
			return;
		}
		
		ImageView iconView = (ImageView)view.findViewById(R.id.res_object_icon);
		if (iconView != null) {
			final String iconId = resObject.getIconIdentifier();
			
			Bitmap bitmap = ThumbCacheManager.queryCachedThumb(iconId);
			if (bitmap == null) {
				ThumbAsyncDecoder.requestDecodeResourceThumb(context, 
						iconId, resObject.getPackage(), resObject.getIcon());

				iconView.setImageResource(R.drawable.ic_launcher);
			} else {
                final int w = getIconScaledWidth();
                final int h = getIconScaledHeight();

                if (w > 0 && h > 0) {
                    bitmap = BitmapUtils.scaleBitmap(bitmap, w, h);
                }

				iconView.setImageBitmap(bitmap);
			}
		}
		
		TextView labelView = (TextView)view.findViewById(R.id.res_object_label);
		if (labelView != null) {
			labelView.setText(resObject.getLabel());
		}
		
		bindView(labelView, context, c, resObject);
	}

	protected void bindView(View view, Context context, Cursor c, T dumpedObject) {
		
	}

    protected int getIconScaledWidth() {
        return -1;
    }

    protected int getIconScaledHeight() {
        return -1;
    }

	@Override
	public void update(Observable observable, Object data) {
		notifyDataSetChanged();
	}
	
}
