package com.k1.graphcode.block.variable;

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

public class BlockVar extends RoundBlock {

	private String mName;
	private Paint mPaint;

	public BlockVar() {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(BLOCK_HEIGHT / 2);
		setId(10010);
		setName("var");
		setBackground(Const.Colors.BlockDefault.VARIABLE);
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
		BlockVar b = (BlockVar) super.clone();
		b.setId(this.getId());
		b.setName(mName);
		b.computeRect();
		return b;
	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {

		Element type = document.createElement("type");
		type.setTextContent("USER_VARIABLE");
		parent.appendChild(type);

		Element value = document.createElement("value");
		value.setTextContent(getName());
		parent.appendChild(value);
	}

	@Override
	public JSONObject toJsonIncludeChilds() {
		JSONObject parent = toJson();
		try {
			parent.put("variable", getName());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parent;
	}
	
	@Override
	public void json2Block(JSONObject json) {
		try {
			String name = json.getString("variable");
			setName(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
