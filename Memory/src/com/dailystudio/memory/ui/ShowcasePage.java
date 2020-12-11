package com.dailystudio.memory.ui;

import android.net.Uri;

public class ShowcasePage {

	public String targetPackage;
	public Uri targetUri;
	
	public ShowcasePage(String pkgname, Uri uri) {
		targetPackage = pkgname;
		targetUri = uri;
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): pkg = %s, uri = %s",
				getClass().getSimpleName(),
				hashCode(),
				targetPackage,
				(targetUri == null ? "N/a" : targetUri.toString()));
	}
}
