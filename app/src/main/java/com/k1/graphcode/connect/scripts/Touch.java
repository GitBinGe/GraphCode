package com.k1.graphcode.connect.scripts;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.k1.graphcode.R;

public class Touch extends Scripts {

	public Touch(JSONObject json) {
		super(json);
	}

	@Override
	public int getIcon() {
		if (isOnline()) {
			return R.mipmap.touch;
		} else {
			return R.mipmap.touch_offline;
		}
	}

	@Override
	public View initView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.script_touch,
				null);
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

			TextView input = (TextView) view.findViewById(R.id.input);
			input.setText("Input : " + getInput());
		}
	}

}
