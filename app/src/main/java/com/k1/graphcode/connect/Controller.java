package com.k1.graphcode.connect;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.SystemClock;

import com.k1.graphcode.BuildConfig;
import com.k1.graphcode.utils.Setting;

public class Controller implements Runnable {

	public static Controller sController;

	public static void newInstence(Context context) {
		if (sController == null) {
			sController = new Controller(context);
		}
	}

	public static Controller getInstence() {
		return sController;
	}

	private Context mContext;
	private ControllerData mControllerData;
	private RequestCallback mCallback;
	private boolean isThreadRunning;

	private Controller(Context context) {
		mContext = context;
		mControllerData = new ControllerData();
	}

	public boolean isConnected() {
		return !mControllerData.isNull();
	}

	public void connect(RequestCallback cb) {
		mCallback = cb;
		if (!isThreadRunning) {
			isThreadRunning = true;
			new Thread(this).start();
		}
	}

	public void pause() {

	}

	public void resume() {

	}

	@Override
	public void run() {
		long test = System.currentTimeMillis();
		while (isThreadRunning) {

			// LogUtils.d("updata controller info..." + new Date());

			String ip = Setting.getInstence().getIP();
			String data = HttpUtils.get(ip, HttpUtils.PORT,
					HttpUtils.PATH_DEVICES);
			try {
				if (BuildConfig.DEBUG) {
					data = getFromAssets("json.txt");
					if (System.currentTimeMillis() - test > 3000) {
//						data = getFromAssets("json2.txt");	
					}
				}
				JSONObject infoJson = new JSONObject(data);
				mControllerData.setData(infoJson);

			} catch (JSONException e) {
				mControllerData.clear();
			} catch (NullPointerException e) {
				mControllerData.clear();
			}

			if (!mControllerData.isNull()) {
				String result = HttpUtils.get(ip, HttpUtils.PORT,
						HttpUtils.PATH_SCRIPTS);
				try {
					if (BuildConfig.DEBUG) {
						result = getFromAssets("scripts.txt");
						if (System.currentTimeMillis() - test > 5000) {
							result = getFromAssets("scripts2.txt");	
						}
					}
					JSONObject scripts = new JSONObject(result);
					mControllerData.setScriptInfo(scripts);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (mCallback != null) {
				mCallback.callback(mControllerData);
			}
			long time = Setting.getInstence().getRefreshRate();
			SystemClock.sleep(time);
		}
	}

	public void disconnect() {
		isThreadRunning = false;
		mControllerData.clear();
	}

	public void refControllerInfo() {

	}

	public ControllerData getInfo() {
		return mControllerData;
	}

	public void requestScripts() {
		String ip = Setting.getInstence().getIP();
		String result = HttpUtils.get(ip, HttpUtils.PORT,
				HttpUtils.PATH_SCRIPTS);
		try {
			if (BuildConfig.DEBUG) {
				result = getFromAssets("scripts.txt");
			}
			JSONObject scripts = new JSONObject(result);
			mControllerData.setScriptInfo(scripts);
		} catch (JSONException e) {
			mControllerData.clear();
		} catch (NullPointerException e) {
			mControllerData.clear();
		}
	}

	public void start(final String name, final RequestCallback cb) {
		String ip = Setting.getInstence().getIP();
		HttpUtils.post(ip, HttpUtils.PORT, HttpUtils.PATH_SCRIPTS_START, name,
				cb);
	}

	public void stop(final String name, final RequestCallback cb) {
		String ip = Setting.getInstence().getIP();
		HttpUtils.post(ip, HttpUtils.PORT, HttpUtils.PATH_SCRIPTS_STOP, name,
				cb);
	}

	public void delete(final String name, final RequestCallback cb) {
		String ip = Setting.getInstence().getIP();
		HttpUtils.post(ip, HttpUtils.PORT, HttpUtils.PATH_SCRIPTS_DELETE, name,
				cb);
	}

	public void download(final String name, final LoadingCallback cb) {
		String ip = Setting.getInstence().getIP();
		HttpUtils.download(ip, HttpUtils.PORT, HttpUtils.PATH_SCRIPTS_DOWNLOAD,
				name, cb);
	}

	public void update(String port, String value, RequestCallback cb) {
		String ip = Setting.getInstence().getIP();
		HttpUtils.update(ip, HttpUtils.PORT, HttpUtils.PATH_DEVICES_UPDATE,
				port, value, cb);
	}

	public void add(final String name, final LoadingCallback cb) {
		String ip = Setting.getInstence().getIP();
		HttpUtils.upload(ip, HttpUtils.PORT, HttpUtils.PATH_SCRIPTS_ADD, name,
				cb);
	}

	public String getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(mContext
					.getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null)
				Result += line;
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
