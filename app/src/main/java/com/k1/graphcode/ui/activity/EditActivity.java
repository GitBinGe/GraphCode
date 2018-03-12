package com.k1.graphcode.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.k1.graphcode.constant.Const;
import com.k1.graphcode.ui.views.edit.ScriptView;
/**
 * 
 * @author BinGe
 *	图形编辑页面
 */
public class EditActivity extends BaseActivity {

	private ScriptView mScriptView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		int editType = intent.getIntExtra("script_type", Const.EditType.MAIN);
		String name = intent.getStringExtra("script_name");

		mScriptView = new ScriptView(this);
		mScriptView.setTitle(name);
		mScriptView.setEditType(editType, name == null ? "test" : name);

		setContentView(mScriptView);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mScriptView.saveBlock();
			finishWithAnimationHorizontalOut();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
