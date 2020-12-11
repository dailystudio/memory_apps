package com.dailystudio.memory.application.databaseobject;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.nativelib.application.IResourceObject;

public abstract class AbsResMemoryObject extends TimeCapsule implements IResourceObject {
	
	private IResourceObject mResObject;

	public AbsResMemoryObject(Context context) {
		super(context);
	}
	
	@Override
	public void resolveResources(Context context) {
		mResObject = createResObject();
		
		mResObject.resolveResources(context);
	}
	
	@Override
	public boolean isResourcesResolved() {
		return (mResObject != null 
				&& mResObject.isResourcesResolved());
	}

	@Override
	public Drawable getIcon() {
		if (mResObject == null) {
			return null;
		}
		
		return mResObject.getIcon();
	}

	@Override
	public CharSequence getLabel() {
		if (mResObject == null) {
			return null;
		}
		
		return mResObject.getLabel();
	}

	@Override
	public void setIconDimension(int width, int height) {
		if (mResObject == null) {
			return;
		}
		
		mResObject.setIconDimension(width, height);
	}

	@Override
	public int getIconWidth() {
		if (mResObject == null) {
			return 0;
		}
		
		return mResObject.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		if (mResObject == null) {
			return 0;
		}
		
		return mResObject.getIconWidth();
	}
	
	public IResourceObject getResObject() {
		return mResObject;
	}
	
	abstract protected IResourceObject createResObject();
	
}
