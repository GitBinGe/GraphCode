package com.k1.graphcode.utils;

import android.view.animation.DecelerateInterpolator;

public class GravityInterpolator {

	public float len;
	public float time;
	private float a = 10 / 1000f;
	private DecelerateInterpolator mInterpolation = new DecelerateInterpolator();
	private boolean mIsPaying;
	private long mStartTime;

	public GravityInterpolator() {
		a = Density.screenHeight() / 150 / 1000f;
	}

	public GravityInterpolator(float a) {
		this.a = a;
	}

	public void setGravity(float a) {
		this.a = a;
	}
	
	public void start() {
		mStartTime = System.currentTimeMillis();
		mIsPaying = true;
	}
	
	public float getCurrent() {
		float tt = System.currentTimeMillis() - mStartTime;
		float t = tt /time;
		if (t < 1) {
			
		} else {
			t = 1;
		}
		return t;
	}
	
	public void endAnimation() {
		mIsPaying = false;
	}
	
	public boolean isPlaying() {
		return mIsPaying;
	}
	
	public void setVelocity(float v) {
		compute(v);
	}

	public void setLen(float l) {
		len = l;
		time = 200f;
	}
	
	public void compute(float v) {
		a = Density.screenHeight() / 150 / 1000f;
		v = v / 1000f;
		time = v / a;
		len = a / 2 * time * Math.abs(time);
		time = Math.abs(time);
	}

	public float getInterpolation(float t) {
		return mInterpolation.getInterpolation(t);
	}
	
}
