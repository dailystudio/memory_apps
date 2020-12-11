package com.dailystudio.memory.ui;

import java.util.Observable;
import java.util.Observer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.app.utils.ThumbAsyncDecoder;
import com.dailystudio.app.utils.ThumbCacheManager;
import com.dailystudio.memory.R;
import com.dailystudio.memory.searchable.MemorySearchableContent;

public class MemorySearchResultAdapter extends SimpleCursorAdapter implements Observer {

	public MemorySearchResultAdapter(Context context) {
		super(context, R.layout.layout_search_result, null, new String[0], new int[0], 0);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		MemorySearchableContent content = 
				MemorySearchableContent.parseFromCursor(cursor);
		if (content == null) {
			return;
		}
		
		ImageView icon1View = (ImageView) view.findViewById(R.id.search_icon_1);
		if (icon1View != null) {
			icon1View.setVisibility((content.icon1ResId == null) ?
					View.INVISIBLE : View.VISIBLE);
			
			Bitmap bitmap = ThumbCacheManager.queryCachedThumb(content.icon1ResId);
			if (bitmap == null) {
				ThumbAsyncDecoder.requestDecodeAndroidResourceSchemeThumb(context, 
						content.icon1ResId, content.icon1ResId);
				
				icon1View.setImageDrawable(null);
			} else {
				icon1View.setImageBitmap(bitmap);
			}
		}
		
		ImageView icon2View = (ImageView) view.findViewById(R.id.search_icon_2);
		if (icon2View != null) {
			icon2View.setVisibility((content.icon2ResId == null) ?
					View.GONE : View.VISIBLE);
			
			Bitmap bitmap = ThumbCacheManager.queryCachedThumb(content.icon2ResId);
			if (bitmap == null) {
				if (content.icon2ResId != null) {
					Uri uri = Uri.parse(content.icon2ResId);
					if (uri != null) {
						final String scheme = uri.getScheme();
						if (ContentResolver.SCHEME_FILE.equals(scheme)) {
							ThumbAsyncDecoder.requestDecodeFileThumb(context, 
									content.icon2ResId, uri.getSchemeSpecificPart());
						} else if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(scheme)) {
							ThumbAsyncDecoder.requestDecodeAndroidResourceSchemeThumb(context, 
									content.icon2ResId, content.icon2ResId);
						} 
					}
				}
				
				icon2View.setImageDrawable(null);
			} else {
				icon2View.setImageBitmap(bitmap);
			}
		}
		
		TextView text1View = (TextView) view.findViewById(R.id.search_text_1);
		if (text1View != null) {
			text1View.setText(content.text1);
		}
		
		TextView text2View = (TextView) view.findViewById(R.id.search_text_2);
		if (text2View != null) {
			text2View.setText(content.text2);
		}
		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		notifyDataSetChanged();
	}

}
