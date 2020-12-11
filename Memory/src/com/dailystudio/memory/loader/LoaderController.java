package com.dailystudio.memory.loader;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.manager.Manager;
import com.dailystudio.manager.SingletonManager;
import com.dailystudio.memory.loader.MemoryStepLoader.Callback;

public class LoaderController extends SingletonManager<Class<? extends MemoryStepLoader>, MemoryStepLoader> {

	public static synchronized LoaderController getInstance() {
		return Manager.getInstance(LoaderController.class);
	}
	
	private List<MemoryStepLoader> mPendingLoaders;
	private MemoryStepLoader mCurrentLoader;
	
	@Override
	protected void initMembers() {
		super.initMembers();
		
		mPendingLoaders = new ArrayList<MemoryStepLoader>();
		mCurrentLoader = null;
	}

	@Override
	public synchronized void addObject(MemoryStepLoader object) {
		if (object == null) {
			return;
		}
		
		Class<? extends MemoryStepLoader> key = object.getSingletonKey();
		if (key == null) {
			return;
		}
		
		MemoryStepLoader old = getObject(key);
		
		if (mPendingLoaders != null) {
			mPendingLoaders.remove(old);
		}

		super.addObject(object);
		
		if (mPendingLoaders != null) {
			mPendingLoaders.add(object);
		}
	}
	
	public static synchronized void startLoader(MemoryStepLoader loader) {
		LoaderController controller = LoaderController.getInstance();
		if (controller == null) {
			return;
		}
		
		loader.setCallback(controller.mLoaderCallback);
		
		controller.addObject(loader);
		
		controller.tryToExecutePendingLoaders();
	}
	
	private synchronized void tryToExecutePendingLoaders() {
		if (mCurrentLoader != null) {
			return;
		}
		
		if (mPendingLoaders == null || mPendingLoaders.size() <= 0) {
			return;
		}

		final MemoryStepLoader loader = mPendingLoaders.remove(0);
		if (loader == null) {
			return;
		}
		
		mCurrentLoader = loader;
		
		mCurrentLoader.executeOnExecutor(
				AsyncTask.THREAD_POOL_EXECUTOR,
				(Void)null);
	}

	private Callback mLoaderCallback = new Callback() {
		
		@Override
		public void onPreExecute(MemoryStepLoader loader) {
		}
		
		@Override
		public void onPostExecute(MemoryStepLoader loader, Integer result) {
			mCurrentLoader = null;
			
			tryToExecutePendingLoaders();
		}
		
		@Override
		public void onCancelled(MemoryStepLoader loader) {
			mCurrentLoader = null;
			
			tryToExecutePendingLoaders();
		}
		
	};
	
}
