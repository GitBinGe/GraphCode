package com.k1.graphcode.block.input;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.RectF;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.RoundBlock;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.utils.Density;

public class RoundBlockInput extends RoundBlock {

	private Path mPath = new Path();
	private String mInputTypeText = "";
	private RectF mInputTypeRect = new RectF();
	private RectF mPortRect = new RectF();
	private BlockInput mInput;
	private String mXmlValue;

	private Paint mPaint = new Paint();

	public RoundBlockInput() {
		super(Density.dip2px(120), Density.dip2px(30));
	}

	public RoundBlockInput(String inputType, String xmlValue, int w, int h) {
		super(w, h);
		mInputTypeText = inputType;
		mPath.setFillType(FillType.EVEN_ODD);
		mInput = new BlockInput(BlockInput.TYPE_INPUT_SELECT);
		mInput.setDialogTitle("Port");
		mInput.setAdapter(Const.PART);
		mXmlValue = xmlValue;
		setBackground(Const.Colors.BlockDefault.INPUT_AI);

		mPaint = new Paint();
		mPaint.setTextSize(BLOCK_ROUND);
		mPaint.setColor(Color.WHITE);
	}

	@Override
	public boolean isMyChilds(Block b) {
		return false;
	}

	@Override
	public RectF computeRect() {
		RectF rectF = super.computeRect();
		float left = rectF.left;
		float top = rectF.top;
		float width = mPaint.measureText(mInputTypeText) + BLOCK_PADDING_LEFT
				* 2;
		width += mPortRect.width() + BLOCK_PADDING_LEFT * 2;
		float height = BLOCK_PADDING_TOP * 2 + BLOCK_HEIGHT;
		rectF.set(0, 0, width, height);
		rectF.offsetTo(left, top);
		return rectF;
	}

	@Override
	public void layout(float left, float top) {
		RectF r = getRect();
		r.offsetTo(left, top);
		mPortRect.set(r.right - BLOCK_WIDTH - BLOCK_PADDING_RIGHT, r.top,
				r.right, r.bottom);
		mPortRect.offset(-BLOCK_PADDING_RIGHT, 0);
		mPortRect.inset(0, BLOCK_PADDING_TOP);
		mInputTypeRect.set(r.left, r.top, mPortRect.left - BLOCK_PADDING_LEFT,
				r.bottom);
		mInputTypeRect.inset(BLOCK_ROUND, 0);
		mInput.setRect(mPortRect);
		addInput(mInput);
	}

	@Override
	protected void draw(Canvas canvas) {
		mPath.reset();
		mPath.addRoundRect(getRect(), BLOCK_ROUND, BLOCK_ROUND, Direction.CW);
		canvas.drawPath(mPath, getPaint());
		int color = getPaint().getColor();
		getPaint().setColor(VAR_COLOR);
		canvas.drawRoundRect(mPortRect, BLOCK_ROUND, BLOCK_ROUND, getPaint());
		getPaint().setColor(color);
		stroke(mPath, canvas);
		drawLeftText(canvas, mInputTypeRect, mInputTypeText);
	}

	public void drawLeftText(Canvas canvas, RectF rect, String text) {
		Paint t = new Paint();
		t.setColor(Color.WHITE);
		t.setTextSize(BLOCK_ROUND);
		FontMetrics fm = t.getFontMetrics();
		float x = rect.left;
		float y = rect.centerY() + fm.descent;
		canvas.drawText(text, x, y, t);

	}

	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element left = document.createElement("leftChild");
		parent.appendChild(left);

		Element type = document.createElement("type");
		type.setTextContent("STRING");
		left.appendChild(type);

		BlockInput input = getInput(0);
		String port = input.getValue();
		port = port.substring(port.length() - 1);

		Element value = document.createElement("value");
		value.setTextContent(port);
		left.appendChild(value);

		type = document.createElement("type");
		type.setTextContent("FUNCTION");
		parent.appendChild(type);

		value = document.createElement("value");
		value.setTextContent(mXmlValue);
		parent.appendChild(value);
	}

	@Override
	public JSONObject toJsonIncludeChilds() {
		JSONObject parent = toJson();
		try {
			parent.put("port", mInput.getValue());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return parent;
	}

	@Override
	public void json2Block(JSONObject json) {
		try {
			String value = json.getString("port");
			mInput.setValue(value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void element2Variable(Block parent, Element element, Node node) {
		node = element.getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				String tag = e.getTagName();
				Node child = node.getFirstChild();
				if (tag.contains("leftChild")) {
					while (child != null) {
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							Element eChild = (Element) child;
							String childTag = eChild.getTagName();
							if (childTag.equals("type")) {
								String type = eChild.getTextContent();
								if (type.equals("STRING")) {
									Element valueElement = (Element) getNextElementNode(eChild);
									String value = valueElement
											.getTextContent();
									mInput.setValue("Port " + value);
								}
							}
						}
						child = getNextElementNode(child);
					}
				}
			}
			node = getNextElementNode(node);
		}
	}

}
