package com.k1.graphcode.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class Setting {

	private static Setting sLocal;

	public static Setting newInstence(Context context) {
		sLocal = new Setting(context);
		return sLocal;
	}

	public static Setting getInstence() {
		return sLocal;
	}

	private SharedPreferences mPref;

	private Setting() {
	}

	private Setting(Context context) {
		mPref = context.getSharedPreferences("setting", 0);
	}

	public void set(String key, String value) {
		mPref.edit().putString(key, value).commit();
	}

	public String get(String key) {
		return mPref.getString(key, null);
	}

	public void setSSID(String value) {
		mPref.edit().putString("ssid", value).commit();
	}

	public String getSSID() {
		return mPref.getString("ssid", null);
	}

	public void setIP(String value) {
		mPref.edit().putString("ip", value).commit();
	}

	public String getIP() {
		return mPref.getString("ip", "192.168.1.1");
	}

	public void setRefreshRate(long time) {
		mPref.edit().putLong("refresh_rate", time <= 0 ? 1000 : time).commit();
	}

	public long getRefreshRate() {
		return mPref.getLong("refresh_rate", 1000);
	}

	public List<String> getDebugList() {
		String debug = mPref.getString("debug", "");
		String[] list = debug.split("~");
		if (list == null) {
			list = new String[0];
		}
		List<String> values = new ArrayList<String>();
		for (String str : list) {
			if (!TextUtils.isEmpty(str)){
				values.add(str);
			}
		}
		return values;
	}

	public void setDebugList(List<String> list) {
		StringBuffer sb = new StringBuffer();
		if (list != null && list.size() > 0) {
			for (String value : list) {
				sb.append(value + "~");
			}
		}
		mPref.edit().putString("debug", sb.toString()).commit();
	}

}
