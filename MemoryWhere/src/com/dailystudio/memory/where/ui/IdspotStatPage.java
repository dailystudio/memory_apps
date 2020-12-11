package com.dailystudio.memory.where.ui;

public class IdspotStatPage {

	public int labelResId;
	public String fragmentName;
	
	public IdspotStatPage(int titleResId, String frgName) {
		labelResId = titleResId;
		fragmentName = frgName;
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): labelResId = 0x%08x, fragmentName = %s",
				getClass().getSimpleName(),
				hashCode(),
				labelResId,
				fragmentName);
	}
	
}
