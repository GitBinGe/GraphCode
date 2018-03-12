package com.k1.graphcode.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

import com.k1.graphcode.R;

public class BaseActivity extends Activity{

	/**
	 * 水平页面切换动画
	 * 
	 * @param activity
	 */
	public void openActivityWithAnimationHorizontalIn(Class<?> activity) {
		Intent intent = new Intent(this, activity);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}
	
	public void openActivityWithAnimationHorizontalIn(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
	}
	
	public void finishWithAnimationHorizontalOut () {
		finish();
		overridePendingTransition(R.anim.back_in, R.anim.back_out);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			finishWithAnimationHorizontalOut();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	
}
