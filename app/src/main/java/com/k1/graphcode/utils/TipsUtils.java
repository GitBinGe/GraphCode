package com.k1.graphcode.utils;

import android.content.Context;
import android.widget.Toast;

public class TipsUtils {

	public static void showTips(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
