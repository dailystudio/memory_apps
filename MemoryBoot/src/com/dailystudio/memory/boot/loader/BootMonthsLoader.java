package com.dailystudio.memory.boot.loader;

import android.content.Context;

import com.dailystudio.memory.boot.MemoryBoot;
import com.dailystudio.memory.loader.MonthsLoader;

public class BootMonthsLoader extends MonthsLoader<MemoryBoot> {


	public BootMonthsLoader(Context context) {
		super(context);
	}

	@Override
	protected Class<MemoryBoot> getObjectClass() {
		return MemoryBoot.class;
	}
	
}
