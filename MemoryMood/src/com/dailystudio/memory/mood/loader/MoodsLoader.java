package com.dailystudio.memory.mood.loader;

import java.util.List;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.memory.mood.Mood;
import com.dailystudio.memory.mood.Moods;

public class MoodsLoader extends AbsAsyncDataLoader<List<Mood>> {

	public MoodsLoader(Context context) {
		super(context);
	}

	@Override
	public List<Mood> loadInBackground() {
		return Moods.listMoods();
	}
	
}
