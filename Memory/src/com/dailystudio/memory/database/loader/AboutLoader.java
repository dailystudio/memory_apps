package com.dailystudio.memory.database.loader;

import android.content.Context;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.nativelib.application.AndroidApplication;

/**
 * Created by nan on 2015/2/4.
 */
public class AboutLoader extends AbsAsyncDataLoader<AndroidApplication> {

    public AboutLoader(Context context) {
        super(context);
    }

    @Override
    public AndroidApplication loadInBackground() {
        final Context context = getContext();
        if (context == null) {
            return null;
        }

        AndroidApplication thisApp =
                new AndroidApplication(getContext().getPackageName());

        thisApp.queryAndFillInfo(getContext());

        return thisApp;
    }

}
