package com.k1.graphcode.connect.scripts;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.k1.graphcode.R;

public class Scripts {

	private static Map<String, Scripts> mScriptMap = new HashMap<String, Scripts>();;

	public static Scripts createWidthJson(JSONObject json) {
		try {
			String name = json.getString("name");
			String tem = name.toLowerCase();
			if (tem.contains("motor")) {
				if (!mScriptMap.containsKey("motor")) {
					mScriptMap.put("motor", new Motor(json));
				} else {
					mScriptMap.get("motor").setJson(json);
				}
				return mScriptMap.get("motor");
			} else if (tem.contains("rgb")) {
				if (!mScriptMap.containsKey("rgb")) {
					mScriptMap.put("rgb", new RGBLed(json));
				} else {
					mScriptMap.get("rgb").setJson(json);
				}
				return mScriptMap.get("rgb");

			} else if (tem.contains("led")) {
				if (!mScriptMap.containsKey("led")) {
					mScriptMap.put("led", new Led(json));
				} else {
					mScriptMap.get("led").setJson(json);
				}
				return mScriptMap.get("led");

			} else if (tem.contains("light")) {
				if (!mScriptMap.containsKey("light")) {
					mScriptMap.put("light", new Light(json));
				} else {
					mScriptMap.get("light").setJson(json);
				}
				return mScriptMap.get("light");
			} else if (tem.contains("sound")) {
				if (!mScriptMap.containsKey("sound")) {
					mScriptMap.put("sound", new Sound(json));
				} else {
					mScriptMap.get("sound").setJson(json);
				}
				return mScriptMap.get("sound");

			} else if (tem.contains("touch")) {
				if (!mScriptMap.containsKey("touch")) {
					mScriptMap.put("touch", new Touch(json));
				} else {
					mScriptMap.get("touch").setJson(json);
				}
				return mScriptMap.get("touch");

			} else if (tem.contains("ultrasonic")) {
				if (!mScriptMap.containsKey("ultrasonic")) {
					mScriptMap.put("ultrasonic", new Ultrasonic(json));
				} else {
					mScriptMap.get("ultrasonic").setJson(json);
				}
				return mScriptMap.get("ultrasonic");

			} else if (tem.contains("temperature")) {
				if (!mScriptMap.containsKey("temperature")) {
					mScriptMap.put("temperature", new Temperature(json));
				} else {
					mScriptMap.get("temperature").setJson(json);
				}
				return mScriptMap.get("temperature");

			} else if (tem.contains("infraredtube")) {
				if (!mScriptMap.containsKey("infraredtube")) {
					mScriptMap.put("infraredtube", new InfraredTube(json));
				} else {
					mScriptMap.get("infraredtube").setJson(json);
				}
				return mScriptMap.get("infraredtube");
			}
		} catch (Exception e) {

		}
		return null;
	}
	
	private String port;
	private String name;
	private String input;
	private boolean isOnline;

	private View mView;
	
	public Map<String, JSONObject> mDatas;

	Scripts(JSONObject json) {
		mDatas = new HashMap<String, JSONObject>();
		setJson(json);
	}
	
	public void setJson(JSONObject json) {
		port = getValue(json, "port");
		name = getValue(json, "name");
		input = getValue(json, "input");

		String online = getValue(json, "online");
		if (!TextUtils.isEmpty(online) && online.toLowerCase().equals("true")) {
			isOnline = true;
		} else {
			isOnline = false;
		}
		mDatas.put(port, json);
	}

	public JSONObject getJsonObject(String port) {
		return mDatas.get(port);
	}
	
	public void clean() {
		mDatas.clear();
	}
	
	public void setCurrentPort(String port) {
		setJson(getJsonObject(port));
		update();
	}
	
	public String getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public String getInput() {
		return input.replace("\n", "");
	}

	public int getIcon() {
		return R.mipmap.na;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public String getValue(JSONObject json, String key) {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	public void update() {

	}

	public final View getView(Context context) {
		if (mView == null) {
			mView = initView(context);
		}
		return mView;
	}

	public final View getView() {
		return mView;
	}

	public View initView(Context context) {
		return null;
	}

	public void showTips(final String msg) {
		if (mView != null) {
			mView.getHandler().post(new Runnable() {
				public void run() {
					Toast.makeText(mView.getContext(), msg, Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
	}

}
