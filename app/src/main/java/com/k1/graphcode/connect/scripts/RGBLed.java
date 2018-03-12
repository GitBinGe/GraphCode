package com.k1.graphcode.connect.scripts;

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

public class RGBLed extends Scripts {

	private String red;
	private String green;
	private String blue;
	
	public RGBLed(JSONObject json) {
		super(json);
	}

	@Override
	public void setJson(JSONObject json) {
		super.setJson(json);
		String input = getInput();
//		red = getValue(json, "red");
//		green = getValue(json, "green");
//		blue = getValue(json, "blue");
		if (input != null) {
			String[] colors = input.split(" ");
			if (colors != null && colors.length == 3) {
				red = colors[0];
				green = colors[1];
				blue = colors[2];
			}
		}
	}
	
	
	@Override
	public View initView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.script_rgb,
				null);
		return view;
	}
	
	@Override
	public void update() {
		View view = getView();
		if (view != null) {
			TextView name = (TextView) view.findViewById(R.id.name);
			name.setText("Name : "+getName());
			TextView port = (TextView) view.findViewById(R.id.port);
			port.setText("Port : "+getPort());
			
			TextView red = (TextView) view.findViewById(R.id.red);
			red.setText("Red : "+getRed());
			
			TextView green = (TextView) view.findViewById(R.id.green);
			green.setText("Green : "+getGreen());
			
			TextView blue = (TextView) view.findViewById(R.id.blue);
			blue.setText("Blue : "+getBlue());

			view.findViewById(R.id.commit).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							InputMethodManager imm = (InputMethodManager) v
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

							TextView red = (TextView) getView().findViewById(
									R.id.red_input);
							TextView green = (TextView) getView().findViewById(
									R.id.green_input);
							TextView blue = (TextView) getView().findViewById(
									R.id.blue_input);
							if (!TextUtils.isEmpty(red.getText()) && !TextUtils.isEmpty(green.getText())
									&& !TextUtils.isEmpty(blue.getText())) {
								String value = red.getText() + " "
										+ green.getText() + " "+ blue.getText();
								Controller.getInstence().update(getPort(),
										value, new RequestCallback() {
											public void error(String info) {
												showTips("上传失败");
											}

											public void callback(
													ControllerData data) {
												showTips("上传成功");
											}
										});
							} else {
								showTips("参数为空");
							}
						}
					});
		}
	}
	
	public String getRed() {
		return red;
	}
	
	public String getBlue() {
		return blue;
	}
	
	public String getGreen() {
		return green;
	}
	
	@Override
	public int getIcon() {
		if (isOnline()) {
			return R.mipmap.rgb;
		} else {
			return R.mipmap.rgb_offline;
		}
	}
	
	
	
}
