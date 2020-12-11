package com.dailystudio.memory.loader.step;

import android.content.Context;
import android.content.Intent;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.memory.R;
import com.dailystudio.memory.activity.AboutActivity;
import com.dailystudio.memory.ask.MemoryAsk;
import com.dailystudio.memory.ask.MemoryQuestion;
import com.dailystudio.memory.ask.QuestionIds;
import com.dailystudio.memory.loader.MemoryLoaderStep;

public class CheckAndInitMemoryAskStep extends MemoryLoaderStep {

	public CheckAndInitMemoryAskStep(Context context) {
		super(context);
	}

	@Override
	public boolean loadInBackground() {
		DatabaseReader<MemoryQuestion> reader = 
			new DatabaseReader<MemoryQuestion>(mContext, 
					MemoryQuestion.class);
		
		Query query = new Query(MemoryQuestion.class);
		
		ExpressionToken selToken = 
			MemoryQuestion.COLUMN_STATE.neq(MemoryQuestion.STATE_ASKED)
				.and(MemoryQuestion.COLUMN_STATE.neq(MemoryQuestion.STATE_CANCELLED));
		if (selToken != null) {
			query.setSelection(selToken);
		}
		
		final long qCount = reader.queryCount(query);
		if (qCount > 0) {
			return true;
		}
		
//		MemoryQuestionDatabaseModal.addQuestion(mContext, 
//				QuestionIds.QID_HELLO_MEMORY, 
//				mContext.getPackageName(), 
//				R.string.ask_question_welcome);
		
		final Context appContext = mContext.getApplicationContext();
		
		Intent welcomeIntent = new Intent();
		
		welcomeIntent.setClass(appContext, 
				AboutActivity.class);
		
		MemoryAsk.askQuestion(appContext, 
				QuestionIds.QID_HELLO_MEMORY, 
				R.string.ask_question_welcome, 
				MemoryQuestion.PRIORITY_NORMAL,
				welcomeIntent);
				
		return true;
	}

}
