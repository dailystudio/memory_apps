package com.dailystudio.memory.mood;

import java.util.Random;

import android.content.Context;
import android.content.Intent;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.memory.ask.MemoryAsk;
import com.dailystudio.memory.ask.MemoryQuestion;
import com.dailystudio.memory.mood.activity.WhatIsYourMoodActivity;
import com.dailystudio.memory.task.Task;

public class AskMoodTask extends Task {

	private final static int sRandomHintResources[] = {
		R.string.ask_mood_hint_02,
		R.string.ask_mood_hint_03,
	};
	
	private Random mRandom = new Random();

	public AskMoodTask(Context context) {
		super(context);
	}
	
	@Override
	public void onCreate(Context context, long time) {
		super.onCreate(context, time);
		
		requestExecute();
	}

	@Override
	public void onExecute(Context context, long time) {
		final long now = System.currentTimeMillis();
		
		MemoryQuestion question = 
			MemoryAsk.getAskedQuestion(context, QuestionIds.QUESTION_ASK_MOOD);
		
		if (question != null) {
			final long timeAsked = question.getTime();
			
			final int currHour = CalendarUtils.getHour(now);
			final int askedHour = CalendarUtils.getHour(timeAsked);
			if (currHour == askedHour) {
				Logger.debug("Skip ASK repeatly in same hour(%d)",
						askedHour);
				
				return;
			}
		}
		
		final int cntResId = 
			pickHintResourceByHour(CalendarUtils.getHour(now));
		
		Intent askIntent = new Intent();
		
		askIntent.setClass(context.getApplicationContext(),
			WhatIsYourMoodActivity.class);
		
		MemoryAsk.askQuestion(context, 
				QuestionIds.QUESTION_ASK_MOOD, 
				cntResId, 
				MemoryQuestion.PRIORITY_ROUTINE,
				askIntent);
	}

	private int pickHintResourceByHour(int hour) {
		int hitres = R.string.ask_mood_hint_03;
		
		switch (hour) {
			case 8:
				hitres = R.string.ask_mood_hint_04;
				break;

			case 13:
				hitres = R.string.ask_mood_hint_05;
				break;
				
			case 23:
			case 0:
			case 1:
				hitres = R.string.ask_mood_hint_06;
				break;

			default:
				hitres = sRandomHintResources[mRandom.nextInt(
						sRandomHintResources.length)];
				break;
		}
		
		return hitres;
	}

}
