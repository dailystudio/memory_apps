package com.dailystudio.memory.application.utils;

import java.util.List;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.os.SystemClock;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;

public class PrintUtils {
	
	public static void printTasks(List<RunningTaskInfo> tasks) {
		if (tasks == null) {
			return;
		}
		
		final int count = tasks.size();
		if (count <= 0) {
			return;
		}
		
		RunningTaskInfo tInfo = null;
		for (int i = 0; i < count; i++) {
			tInfo = tasks.get(i);
			printTask(tInfo);
		}
	}
	
	public static void printTask(RunningTaskInfo tInfo) {
		if (tInfo == null) {
			return;
		}
		
		Logger.debug("Task %03d: runAct(%d) / allAct(%d), desc(%s), baseActivity(%s) / topActivity(%s)",
				tInfo.id,
				tInfo.numRunning,
				tInfo.numActivities,
				tInfo.description,
				tInfo.baseActivity,
				tInfo.topActivity);
	}

	
	public static void printServices(List<RunningServiceInfo> services) {
		if (services == null) {
			return;
		}
		
		final int count = services.size();
		if (count <= 0) {
			return;
		}
		
		RunningServiceInfo sInfo = null;
		for (int i = 0; i < count; i++) {
			sInfo = services.get(i);
			printService(sInfo);
		}
	}

	public static void printService(RunningServiceInfo sInfo) {
		if (sInfo == null) {
			return;
		}
		
		Logger.debug("Service: pid(%d), uid(%d), comp(%s), active(%s), lastActivitTime(%s)",
				sInfo.pid,
				sInfo.uid,
				sInfo.service,
				CalendarUtils.timeToReadableString(System.currentTimeMillis() - SystemClock.elapsedRealtime() + sInfo.activeSince),
				CalendarUtils.timeToReadableString(System.currentTimeMillis() - SystemClock.elapsedRealtime() + sInfo.lastActivityTime));
	}

	public static void printProcesses(List<RunningAppProcessInfo> processes) {
		if (processes == null) {
			return;
		}
		
		final int count = processes.size();
		if (count <= 0) {
			return;
		}
		
		RunningAppProcessInfo pInfo = null;
		for (int i = 0; i < count; i++) {
			pInfo = processes.get(i);
			printProcess(pInfo);
		}
	}

	public static void printProcess(RunningAppProcessInfo pInfo) {
		if (pInfo == null) {
			return;
		}
		
		Logger.debug("Process: pid(%d), uid(%d), processName(%s)",
				pInfo.pid,
				pInfo.uid,
				pInfo.processName);
	}

}
