package com.k1.graphcode.block.project;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;

public class Saver {

	private static Saver sSaver;
	
	public static Saver newInstence(Context context) {
		if (sSaver == null) {
			sSaver = new Saver(context);
		}
		return sSaver;
	}
	
	public static Saver getInstence()  {
		return sSaver;
	}
	
	private Saver() {
		
	}
	
	private SharedPreferences mShared;
	
	private Saver(Context context) {
		mShared = context.getSharedPreferences("variable", 0);
	}

	//第一次保存，当本地已有数据，则不需要保存这些变量
	public void saveVariable(String project, List<String> list) {
		try {
			JSONArray array = new JSONArray();
			for (String var : list) {
				array.put(var);
			}
			mShared.edit().putString(project, array.toString()).commit();
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			array.put("var");
			mShared.edit().putString(project, array.toString()).commit();
		}
	}
	// project 创建的时候获取，初始化当前project的变量
	public List<String> getVariavles(String project) {
		List<String> list = new ArrayList<String>();
		String vars = mShared.getString(project, "");
		try {
			JSONArray array = new JSONArray(vars);
			for (int i=0;i<array.length();i++) {
				String var = array.getString(i);
				list.add(var);
			}
		} catch (Exception e) {
		}
		return list;
	}
	
	public void removeProject(String project) {
		mShared.edit().remove(project).commit();
	}
	
	public void saveVariable(String project, String var) {
		String vars = mShared.getString(project, "");
		try {
			JSONArray array = new JSONArray(vars);
			array.put(var);
			mShared.edit().putString(project, array.toString()).commit();
		} catch (Exception e) {
			JSONArray array = new JSONArray();
			array.put("var");
			mShared.edit().putString(project, array.toString()).commit();
		}
	}
	
}
