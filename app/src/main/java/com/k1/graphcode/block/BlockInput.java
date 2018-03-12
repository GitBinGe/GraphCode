package com.k1.graphcode.block;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
/**
 * 
 * @author BinGe
 *	block中的输入块，如port，dir，setColor中的输入框等
 */
public class BlockInput {

	public static final int TYPE_INPUT_VALUE = 101;
	public static final int TYPE_INPUT_SELECT = 102;

	private int mType = TYPE_INPUT_VALUE;

	private RectF mRectF = new RectF();

	private String mTitle = "";
	private String mValue = null;

	private String[] mAdapter;
	private ValueChangedListener mValueChangedListener;

	public BlockInput() {

	}

	public BlockInput(int type) {
		mType = type;
	}
	
	public void setValueChangedListener(ValueChangedListener l) {
		mValueChangedListener = l;
	}

	public void setAdapter(String[] adapter) {
		mAdapter = adapter;
		if (mAdapter != null && mAdapter.length > 0) {
			mValue = mAdapter[0];
		}
	}

	public String[] getAdapter() {
		return mAdapter;
	}

	public void setDialogTitle(String title) {
		mTitle = title;
	}

	public String getDialogTitle() {
		return mTitle;
	}

	public void setType(int type) {
		mType = type;
	}

	public int getType() {
		return mType;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(String value) {
		this.mValue = value;
		if (mValueChangedListener != null) {
			mValueChangedListener.valueChcanged(value);
		}
	}

	public RectF getRect() {
		return mRectF;
	}

	public void setRect(RectF rf) {
		this.mRectF.set(rf);
	}

	public void draw(Canvas canvas) {
		if (TextUtils.isEmpty(mValue)) {
			return;
		}

		Paint p = new Paint();
		p.setTextAlign(Align.CENTER);
		p.setColor(Color.BLACK);

		RectF rf = new RectF(mRectF);
		if (mType == TYPE_INPUT_SELECT) {
			RectF r = new RectF(rf);
			float len = rf.height() / 4;
			r.inset(0, len);
			r.left = r.right - len * 2;
			r.inset(len / 4, len / 2);
			Path path = new Path();
			path.moveTo(r.left, r.top);
			path.lineTo(r.right, r.top);
			path.lineTo(r.centerX(), r.bottom);
			path.close();

			canvas.drawPath(path, p);
			// canvas.drawRect(r, p);

			rf.left += len;
			rf.right -= len * 2;

		}

		String text = String.valueOf(mValue);
		p.setTextSize(rf.height() / 2);
		if (p.measureText(text) > rf.width()) {
			int length = text.length();
			if (length < 4) {
				length = 4;
			}
			p.setTextSize(rf.width() / length * 2.2f);
		}
		FontMetrics fm = p.getFontMetrics();
		float h = fm.bottom - fm.top;
		float y = rf.top + rf.height() - (rf.height() - h) / 2 - fm.bottom;
		canvas.drawText(text, rf.centerX(), y, p);

	}

	public interface ValueChangedListener {
		public void valueChcanged(String value);
	}
	
}
