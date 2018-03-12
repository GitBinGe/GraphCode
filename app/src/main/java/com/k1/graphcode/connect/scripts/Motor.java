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
import android.widget.EditText;
import android.widget.TextView;

import com.k1.graphcode.R;
import com.k1.graphcode.connect.Controller;
import com.k1.graphcode.connect.ControllerData;
import com.k1.graphcode.connect.RequestCallback;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;

public class Motor extends Scripts {

	private String pwm;
	private String dir;
	private String rpm;
	private CustomDialog mDialog;

	private Map<String, String> mDir = new HashMap<String, String>();

	public Motor(JSONObject json) {
		super(json);
		mDir.put("stop", "0");
		mDir.put("forward", "1");
		mDir.put("backward", "2");
	}

	@Override
	public void setJson(JSONObject json) {
		super.setJson(json);
		pwm = getValue(json, "pwm");
		dir = getValue(json, "dir");
		rpm = getValue(json, "input");
	}

	public String getDir() {
		return dir;
	}

	public String getPwm() {
		return pwm;
	}

	public String getRpm() {
		return rpm == null ? "0" : rpm;
	}

	@Override
	public int getIcon() {
		if (isOnline()) {
			return R.mipmap.motor;
		} else {
			return R.mipmap.motor_offline;
		}
	}

	@Override
	public View initView(Context context) {
		mDialog = CustomDialog.createSelecterDialog(context, "DIR",
				new String[] { "stop", "forward", "backward" },
				new ReturnResults() {
					public void result(Object o) {
						TextView dir = (TextView) getView().findViewById(
								R.id.dir);
						dir.setText(o.toString());

						EditText pwm = (EditText) getView().findViewById(
								R.id.pwm_input);
						if (o.equals("stop")) {
							pwm.setText("0");
							pwm.setEnabled(false);
						} else {
							pwm.setText("");
							pwm.setEnabled(true);
						}
					}
				});
		View view = LayoutInflater.from(context).inflate(R.layout.script_motor,
				null);
		view.findViewById(R.id.dir).setOnClickListener(new OnClickListener() {
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

						TextView pwm = (TextView) getView().findViewById(
								R.id.pwm_input);
						TextView dir = (TextView) getView().findViewById(
								R.id.dir);
						String current = mDir.get(dir.getText());
						if (!TextUtils.isEmpty(current)
								&& !TextUtils.isEmpty(pwm.getText())) {
							String value = current + " "
									+ pwm.getText().toString();
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

			TextView rpm = (TextView) view.findViewById(R.id.rpm);
			rpm.setText("Speed : " + getRpm() + "(rpm)");
		}
	}

}
