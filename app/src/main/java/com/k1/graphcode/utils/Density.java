package com.k1.graphcode.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class Density {

	public static final float DEFAULT_SCALE = 2;

	private static float scale = 1.0f;
	private static float fontScale = 1.0f;
	private static int statusBarHeight = 40;
	private static int screenWidth = 720;
	private static int screenHeight = 1280;

	public static void init (Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		scale = dm.density;
		fontScale = dm.scaledDensity;
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		statusBarHeight = getStatusBarHeight(context);
	}

	private static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	public static int statusBarHeight() {
		return statusBarHeight;
	}

	public static int screenWidth() {
		return screenWidth;
	}
	
	public static int screenHeight() {
		return screenHeight;
	}

	public static int dip2px(float dpValue) {
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(float pxValue) {
		return (int) (pxValue / scale + 0.5f);
	}

	public static int px2sp(float pxValue) {
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(float spValue) {
		return (int) (spValue * fontScale + 0.5f);
	}
}
