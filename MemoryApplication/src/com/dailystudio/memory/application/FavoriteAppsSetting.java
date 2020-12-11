package com.dailystudio.memory.application;

public class FavoriteAppsSetting {

	public boolean includeSystemApps;
	public int favortieClass;
	public int labelResId;
	public int iconResId;
	public int displayLabelResId;
	
	public FavoriteAppsSetting(boolean includeSys,
			int favorite, int label, int icon,
			int displayLabel) {
		includeSystemApps = includeSys;
		favortieClass = favorite;
		labelResId = label;
		iconResId = icon;
		displayLabelResId = displayLabel;
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): displayLabel[0x%08x], includeSys[%s], favoriteClass[%d], label[0x%08x], icon[0x%08x]",
				getClass().getSimpleName(),
				hashCode(),
				displayLabelResId,
				includeSystemApps,
				favortieClass,
				labelResId,
				iconResId);
	}

}
