package com.dailystudio.memory.where;

import android.content.Context;

import com.araneaapps.android.libs.asyncrunners.models.TaskStore;
import com.baidu.mapapi.SDKInitializer;
import com.dailystudio.app.DevBricksApplication;
import com.dailystudio.app.utils.FileUtils;
import com.dg.libs.rest.RestClientConfiguration;
import com.dg.libs.rest.authentication.AuthenticationProvider;
import com.dg.libs.rest.authentication.TokenAuthenticationProvider;
import com.dg.libs.rest.requests.RestClientRequest;

public class MemoryWhereApplication extends DevBricksApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		
		prepareDirs();

		initRestClientLibrary(getApplicationContext());

		SDKInitializer.initialize(getApplicationContext());
	}
	
	private void prepareDirs() {
		String cachesDir = Directories.getMapCachesDirectory(this);

		FileUtils.checkOrCreateNoMediaDirectory(cachesDir);
		
		String photos = Directories.getPhotosDirectory(this);

		FileUtils.checkOrCreateDirectory(photos);
	}

	private static void initRestClientLibrary(Context context) {
		if (context == null) {
			return;
		}

		TokenAuthenticationProvider.init(context);
		TaskStore.init(context);

		RestClientConfiguration builder = new RestClientConfiguration.ConfigurationBuilder()
				.setAuthenticationProvider(new AuthenticationProvider() {
					@Override
					public void authenticateRequest(RestClientRequest client) {
					}
				}).create();

		RestClientConfiguration.init(context, builder);
	}

}
