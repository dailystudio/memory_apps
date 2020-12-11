package com.dailystudio.memory.loader.step;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dailystudio.memory.R;
import com.dailystudio.memory.activity.AboutActivity;
import com.dailystudio.memory.ask.MemoryAsk;
import com.dailystudio.memory.ask.MemoryQuestion;
import com.dailystudio.memory.ask.QuestionIds;
import com.dailystudio.memory.loader.MemoryLoaderStep;
import com.dailystudio.memory.promotapps.PromoteApp;
import com.dailystudio.memory.promotapps.PromoteApps;
import com.dailystudio.memory.promotapps.PromoteAppsInterface;

public class CheckAndPromoteMemoryAppStep extends MemoryLoaderStep {

	public CheckAndPromoteMemoryAppStep(Context context) {
		super(context);
	}

	@Override
	public boolean loadInBackground() {
		final Context appContext = mContext.getApplicationContext();

		final PromoteApp topPromoteApp = getTopPromoteApp();
		if (topPromoteApp == null) {
			MemoryAsk.answerQuestion(appContext, 
					QuestionIds.QID_PROMOTE_APP, 
					"done");

			return true;
		}
		
		Intent viewIntent = new Intent(Intent.ACTION_VIEW);
		
		viewIntent.setClass(appContext, 
				AboutActivity.class);
		viewIntent.setData(Uri.parse("market://details?id=" + topPromoteApp.packageName));
		
		final String qtempl = mContext.getString(R.string.promote_app_ask_templ);
		if (qtempl == null) {
			return true;
		}
		
		final String qtext = String.format(qtempl, topPromoteApp.appName);
		
		MemoryAsk.askQuestion(appContext, 
				QuestionIds.QID_PROMOTE_APP, 
				qtext, 
				MemoryQuestion.PRIORITY_NORMAL,
				viewIntent);
				
		return true;
	}

	private PromoteApp getTopPromoteApp() {
		PromoteAppsInterface pai = new PromoteAppsInterface(mContext);
		
		PromoteApps promoteapps = pai.getPromoteApps();
		if (promoteapps == null) {
			return null;
		}
		
		PromoteApp[] appslist = promoteapps.apps;
		
		if (appslist == null || appslist.length <= 0) {
			return null;
		}
		
		PromoteApp topApp = null;
		for (PromoteApp app: appslist) {
			if (!app.isInstalled(mContext) && app.isPublished) {
				topApp = app;
				
				break;
			}
		}
		
		return topApp;
	}
	
}
