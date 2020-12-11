package com.dailystudio.memory.loader;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.manager.ISingletonObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

public class MemoryStepLoader extends AsyncTask<Void, MemoryLoaderStep, Integer>
	implements ISingletonObject<Class<? extends MemoryStepLoader>> {

	public static interface Callback {

		public void onPreExecute(MemoryStepLoader loader);
		public void onPostExecute(MemoryStepLoader loader, Integer result);
		public void onCancelled(MemoryStepLoader loader);
		
	}
	
	class LooperThread extends Thread {
		
		public void run() {
			Looper.prepare();
			
			mHandler = new SyncHandler();
			
			Looper.loop();
		}
	      
	}
	
	protected Context mContext = null;
	
	private Object mWaitLock = new Object();
	private Handler mHandler = null;
	
	private Callback mCallback = null;
	
	private List<MemoryLoaderStep> mSteps = null;
	
	public MemoryStepLoader(Context context) {
		this(context, false);
	}
	
	MemoryStepLoader(Context context, boolean handlerInThread) {
		mContext = context;
		
		mSteps = new ArrayList<MemoryLoaderStep>();
		
		if (handlerInThread) {
			new LooperThread().start();
		} else {
			mHandler = new SyncHandler();
		}
	}
	
	public synchronized void addStep(MemoryLoaderStep step) {
		if (step == null) {
			return;
		}
		
		if (getStatus() == Status.RUNNING) {
			throw new IllegalStateException("Could not add step while loader is running");
		}
		
		step.attachToLoader(this);
		mSteps.add(step);
	}
	
	@Override
	final protected Integer doInBackground(Void... arg0) {
		Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
		
		boolean ret = false;
		
		int success = 0;
		for (MemoryLoaderStep step: mSteps) {
			ret = step.loadInBackground();
			
			if (ret) {
				success++;
			}
		}
		
		return success;
	}
	
	private void waitForSync() {
		synchronized (mWaitLock) {
			if (mHandler.hasMessages(SYNC_RUN)) {
				try {
					mWaitLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	final protected void postAndWait(Runnable r) {
		if (r == null) {
			return;
		}
		
		Message msg = 
			mHandler.obtainMessage(SYNC_RUN, r);
		if (msg == null) {
			return;
		}
		
		mHandler.sendMessage(msg);
		
		waitForSync();
	}
	
	public void setCallback(Callback callback) {
		mCallback = callback;
	}
	
	public void execute() {
		execute((Void)null);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (mCallback != null) {
			mCallback.onPreExecute(this);
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		if (mCallback != null) {
			mCallback.onPostExecute(this, result);
		}
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();

		if (mCallback != null) {
			mCallback.onCancelled(this);
		}
	}
	
	protected void printSteps() {
		if (mSteps == null) {
			return;
		}
		
		final int count = mSteps.size();
		if (count <= 0) {
			return;
		}
		
		for (int i = 0; i < count; i++) {
			Logger.debug("%s", mSteps.get(i));
		}
	}
	
	@Override
	public Class<? extends MemoryStepLoader> getSingletonKey() {
		return getClass();
	}
	
	private final static int SYNC_RUN = 0x10;

	@SuppressLint("HandlerLeak")
	private class SyncHandler extends Handler {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SYNC_RUN:
					if (msg.obj instanceof Runnable) {
						Runnable r = (Runnable)msg.obj;
						
						r.run();
						
						synchronized (mWaitLock) {
							mWaitLock.notifyAll();
						}
					}
					break;
			}
			
			super.handleMessage(msg);
		};
		
	};
	
}
