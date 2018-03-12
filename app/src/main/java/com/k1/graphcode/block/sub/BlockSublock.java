package com.k1.graphcode.block.sub;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.RoundBlock;
import com.k1.graphcode.constant.Const;

public class BlockSublock extends RoundBlock {

	public static final String SUBBLOCK_NAME_TAG = "SUB_BLOCK_NAME";

	private String mName;
	private Paint mPaint;

	public BlockSublock() {
		super();
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(BLOCK_HEIGHT / 2);
		setId(10011);
		setName("sublock");
		setBackground(Const.Colors.BlockDefault.SUB);
	}

	public void setName(String name) {
		mName = name.trim();
	}

	public String getName() {
		return mName;
	}

	@Override
	public RectF computeRect() {
		RectF rf = getRect();
		float width = mPaint.measureText(mName) + BLOCK_ROUND * 2;
		width = Math.max(width, BLOCK_WIDTH);
		rf.right = rf.left + width;
		return rf;
	}

	@Override
	protected void draw(Canvas canvas) {
		super.draw(canvas);
		drawCenterText(canvas, mPaint, getRect(), mName);
	}

	@Override
	public Block clone() {
		BlockSublock b = (BlockSublock) super.clone();
		b.setId(this.getId());
		b.setName(mName);
		b.computeRect();
		return b;
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
	}

	@Override
	public JSONObject toJsonIncludeChilds() {
		JSONObject parent = toJson();
		try {
			parent.put("sub", getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parent;
	}

	@Override
	public void json2Block(JSONObject json) {
		try {
			String name = json.getString("sub");
			setName(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
