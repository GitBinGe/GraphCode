package com.k1.graphcode.utils;

import com.k1.graphcode.block.Block;

public class BlockAnimationUtils {

	private long time;
	private boolean paying;
	
	public BlockAnimationUtils() {
		
	}
	public void start() {
		paying = true;
		time = System.currentTimeMillis();
	}
	
	public float getTimes() {
		float t = (System.currentTimeMillis() - time) / Block.ANIMATION_TIME;
		if (t > 1) {
			t = 1;
			paying = false;
		}
		return t;
	}
	
	public boolean isPaying() {
		return paying;
	}
	
}
