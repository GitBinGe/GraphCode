package com.k1.graphcode.utils;

import android.util.Log;

import com.k1.graphcode.BuildConfig;

public class LogUtils {
	
	public static void d(Object log) {
		log("bin", log);
	}

	private static void log(String tag, Object log) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, log != null ? log.toString() : "null");
		}
	}
	
}
