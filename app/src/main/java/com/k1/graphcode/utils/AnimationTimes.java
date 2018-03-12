package com.k1.graphcode.utils;

public class AnimationTimes {

	private long startTime;
	private boolean isPaying;
	private float duration = 200f;
	private float startX, startY, endX, endY;

	public AnimationTimes() {
	}

	public void start() {
		isPaying = true;
		startTime = System.currentTimeMillis();
	}
	
	public void start(float fromX, float toX, float fromY, float toY) {
		startX = fromX;
		endX = toX;
		startY = fromY;
		endY = toY;
		isPaying = true;
		startTime = System.currentTimeMillis();
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getCurrent() {
		float t = (System.currentTimeMillis() - startTime) / duration;
		if (t < 1) {

		} else {
			t = 1;
			isPaying = false;
		}
		return t;
	}

	public float getCurrentX() {
		float t = (System.currentTimeMillis() - startTime) / duration;
		if (t < 1) {

		} else {
			t = 1;
			isPaying = false;
		}
		float x = startX + (endX - startX) * t;
		return x;
	}

	public float getCurrentY() {
		float t = (System.currentTimeMillis() - startTime) / duration;
		if (t < 1) {

		} else {
			t = 1;
			isPaying = false;
		}
		float y = startY + (endY - startY) * t;
		return y;
	}

	public boolean isPaying() {
		return isPaying;
	}

}
