package com.dailystudio.memory.application.ui;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.memory.application.R;
import com.dailystudio.memory.ui.AbsResObjectAdapter;
import com.dailystudio.nativelib.application.IResourceObject;

public abstract class AppRelatedAdapter<T extends IResourceObject> extends AbsResObjectAdapter<T> {

	public AppRelatedAdapter(Context context) {
		super(context);
	}

	@Override
	protected void bindViewWithResource(View view, Context context,
			T object) {
		super.bindViewWithResource(view, context, object);
		
		final boolean existed = isRelatedAppExisted(context, object);
		TextView labelView = (TextView) view.findViewById(R.id.res_label);
		if (labelView != null) {
			labelView.setTextAppearance(getContext(), (existed ?
					R.style.DefaultListItemText : R.style.UninstalledAppItemText));
		}
		
		ImageView iconView = (ImageView) view.findViewById(R.id.res_icon);
		if (iconView != null) {
			Drawable icon = iconView.getDrawable();
			if (icon != null) {
				if (!existed) {
					ColorMatrix cm = new ColorMatrix();      
					cm.setSaturation(0);      
					ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);      
					icon.setColorFilter(f);
				} else {
					icon.setColorFilter(null);
				}
			}
		}
	}
	
	abstract protected boolean isRelatedAppExisted(Context context, T object);
	
}
