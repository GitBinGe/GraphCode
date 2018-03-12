package com.k1.graphcode.connect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.k1.graphcode.connect.scripts.ControllerSelf;
import com.k1.graphcode.connect.scripts.Scripts;

public class ControllerData {

	private boolean isNull = true;

	private Map<String, Scripts> mChilds;
	private ControllerSelf mSelf;

	public ControllerData() {
		mChilds = new HashMap<String, Scripts>();
	}

	public void setData(JSONObject json) {
		try {
			mChilds.clear();
			JSONArray dataArray = json.getJSONArray("data");
			if (dataArray != null) {
				isNull = false;
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject data = dataArray.getJSONObject(i);
					Scripts scripts = Scripts.createWidthJson(data);
					mChilds.put(scripts.getPort(), scripts);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void setScriptInfo(JSONObject json) {
		if (mSelf == null) {
			mSelf = new ControllerSelf(json);
		} else {
			mSelf.setJson(json);
		}
	}

	public List<String> getScripts() {
		if (mSelf != null) {
			return mSelf.getScriptsFile();
		}
		return null;
	}

	public String runningScript() {
		if (mSelf != null) {
			return mSelf.getRunningScript();
		}
		return null;
	}

	public boolean isScriptRunning() {
		if (mSelf != null) {
			return mSelf.isScriptRunning();
		}
		return false;
	}

	public ControllerSelf getSelf() {
		return mSelf;
	}

	public Map<String, Scripts> getChilds() {
		Map<String, Scripts> map = new HashMap<String, Scripts>();
		map.putAll(mChilds);
		return map;
	}

	public boolean isNull() {
		return isNull;
	}

	public void clear() {
		isNull = true;
		mChilds.clear();
	}

}
