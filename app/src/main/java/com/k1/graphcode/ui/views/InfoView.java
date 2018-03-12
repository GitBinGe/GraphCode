package com.k1.graphcode.ui.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.k1.graphcode.R;
import com.k1.graphcode.connect.scripts.Scripts;
/**
 * 
 * @author BinGe
 *	点击外设或是controller时显示到这个view里面
 */
public class InfoView extends FrameLayout implements OnClickListener {

	private View mParent;
	private View mClose;
	private ViewGroup mScriptsViews;

	public InfoView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_info, this);
		mClose = findViewById(R.id.close);
		mClose.setOnClickListener(this);
		mParent = findViewById(R.id.parent);
		mScriptsViews = (ViewGroup) findViewById(R.id.scripts);
	}

	public void showScripts(Scripts scripts) {
		mScriptsViews.removeAllViews();
		View view = scripts.getView(getContext());
		if (view != null) {
			scripts.update();
			mScriptsViews.addView(view);
		}
		show();
	}

	public void update(Scripts scripts) {
		if (scripts != null) {
//			mScriptsViews.removeAllViews();
			View view = scripts.getView(getContext());
			View childView = null;
			if (mScriptsViews.getChildCount()>0) {
				childView = mScriptsViews.getChildAt(0);
			}
			if (view != childView) {
				mScriptsViews.removeAllViews();
				mScriptsViews.addView(view);
			}
			scripts.update();
		}
	}
	
	public boolean isShow() {
		return getVisibility() == View.VISIBLE;
	}

	private void show() {
		if (View.VISIBLE != getVisibility()) {
			TranslateAnimation t = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
					0, Animation.RELATIVE_TO_SELF, -1,
					Animation.RELATIVE_TO_SELF, 0);
			t.setDuration(200);
			mParent.startAnimation(t);
			setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void hide() {
		if (View.VISIBLE == getVisibility()) {
			setVisibility(View.GONE);
			AlphaAnimation a = new AlphaAnimation(1, 0);
			a.setDuration(200);
			startAnimation(a);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mClose) {
			hide();
		}
	}
}
