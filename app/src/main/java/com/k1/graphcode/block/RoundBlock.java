package com.k1.graphcode.block;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;

/**
 * 
 * @author BinGe
 *	圆角block
 */
public class RoundBlock extends Block {

	private Path mPath = new Path();
	private RectF mRectF = new RectF();

	public RoundBlock() {
		super();
	}

	public RoundBlock(float w, float h) {
		super(w, h);
	}
	
	@Override
	public void setUseCacheOnlySelf(boolean useCache) {
		if (!useCache) {
			mPath.reset();
		}
		super.setUseCacheOnlySelf(useCache);
	}

	@Override
	protected void draw(Canvas canvas) {
		if (mPath.isEmpty()) {
			mPath.addRoundRect(getRect(), BLOCK_ROUND, BLOCK_ROUND,
					Direction.CW);
		} else {
			mPath.computeBounds(mRectF, false);
			mPath.offset(getLeft() - mRectF.left, getTop() - mRectF.top);
		}
		canvas.drawPath(mPath, getPaint());
		stroke(mPath, canvas);
	}

}
