package com.k1.graphcode.connect.scripts;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.k1.graphcode.R;
import com.k1.graphcode.connect.Controller;
import com.k1.graphcode.connect.ControllerData;
import com.k1.graphcode.connect.RequestCallback;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;

public class Led extends Scripts {

	private Map<String, String> mSwitch = new HashMap<String, String>();
	private CustomDialog mDialog;
	
	public Led(JSONObject json) {
		super(json);
		mSwitch.put("ON", "1");
		mSwitch.put("OFF", "0");
	}

	@Override
	public int getIcon() {
		if (isOnline()) {
			return R.mipmap.led;
		} else {
			return R.mipmap.led_offline;
		}
	}

	@Override
	public View initView(Context context) {
		mDialog = CustomDialog.createSelecterDialog(context, "SWITCH",
				new String[] { "ON", "OFF" }, new ReturnResults() {
					public void result(Object o) {
						TextView dir = (TextView) getView().findViewById(
								R.id.led_switch);
						dir.setText(o.toString());
					}
				});
		View view = LayoutInflater.from(context).inflate(R.layout.script_led,
				null);
		view.findViewById(R.id.led_switch).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						mDialog.show();
					}
				});
		view.findViewById(R.id.commit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						InputMethodManager imm = (InputMethodManager) v
								.getContext().getSystemService(
										Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
						TextView led = (TextView) getView().findViewById(
								R.id.led_switch);
						String current = mSwitch.get(led.getText());
						if (!TextUtils.isEmpty(current)) {
							String value = current;
							Controller.getInstence().update(getPort(), value,
									new RequestCallback() {
										public void error(String info) {
											showTips("上传失败");
										}

										public void callback(ControllerData data) {
											showTips("上传成功");
										}
									});
						} else {
							showTips("参数为空");
						}
					}
				});
		return view;
	}

	@Override
	public void update() {
		View view = getView();
		if (view != null) {
			TextView name = (TextView) view.findViewById(R.id.name);
			name.setText("Name : " + getName());
			TextView port = (TextView) view.findViewById(R.id.port);
			port.setText("Port : " + getPort());
		}
	}

}
