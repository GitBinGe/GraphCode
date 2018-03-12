package com.k1.graphcode.block;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * 
 * @author BinGe
 *	六边形的block
 */
public class HexagonBlock extends Block {

	private Path mPath = new Path();
	
	public HexagonBlock() {
		super();
	}
	
	public HexagonBlock(float w, float h) {
		super(w,h);
	}

	@Override
	protected void draw(Canvas canvas) {
		initHexagonPath(getRect(), mPath);
		canvas.drawPath(mPath, getPaint());
	}
	
	public static void initHexagonPath(RectF r, Path path) {
		path.reset();
		if (!r.isEmpty()) {
			float len = r.height() / 2;
			float x = r.left;
			float y = r.top + len;
			path.moveTo(x, y);
			x = r.left + len / 2;
			y = r.top;
			path.lineTo(x, y);
			x = r.right - len / 2;
			y = r.top;
			path.lineTo(x, y);
			x = r.right;
			y = r.top + len;
			path.lineTo(x, y);
			x = r.right - len / 2;
			y = r.bottom;
			path.lineTo(x, y);
			x = r.left + len / 2;
			y = r.bottom;
			path.lineTo(x, y);
			path.close();
		}
	}

}
