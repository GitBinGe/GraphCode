package com.k1.graphcode.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.k1.graphcode.R;
import com.k1.graphcode.utils.Setting;

/**
 * 
 * 	@author BinGe
 *	设置页面
 */
public class SettingActivity extends BaseActivity implements OnClickListener {

	private EditText mSsidEdit, mIPEdit, mTimeEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		mSsidEdit = (EditText) findViewById(R.id.ssid);
		mSsidEdit.setText(Setting.getInstence().getSSID());

		mIPEdit = (EditText) findViewById(R.id.ip);
		mIPEdit.setText(Setting.getInstence().getIP());

		mTimeEdit = (EditText) findViewById(R.id.refresh_rate);
		mTimeEdit.setText(Setting.getInstence().getRefreshRate() + "");

		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.debug).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.save) {
			Setting.getInstence().setIP(mIPEdit.getText().toString());
			Setting.getInstence().setSSID(mSsidEdit.getText().toString());
			Editable obj = mTimeEdit.getText();
			if (!TextUtils.isEmpty(obj)) {
				Setting.getInstence().setRefreshRate(
						Long.valueOf(obj.toString()));
			}
			finishWithAnimationHorizontalOut();
		} else if (v.getId() == R.id.debug) {
			openActivityWithAnimationHorizontalIn(DebugActivity.class);
		}
	}
}
