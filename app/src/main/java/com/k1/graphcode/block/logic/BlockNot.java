package com.k1.graphcode.block.logic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.RectF;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;

public class BlockNot extends BlockLogic {

	public BlockNot() {
		super(CHILD_TYPE_HEXAGON, "NOT");
		mLeftRectF.setEmpty();
		mOperation.set(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
	}

	@Override
	public RectF computeRect() {
		RectF rf = getRect();
		if (mRightBlock != null) {
			mRightRectF.set(mRightBlock.computeRect());
		}
		float height = mRightRectF.height();
		height = BLOCK_PADDING_TOP + height + BLOCK_PADDING_BOTTOM;
		float width = BLOCK_ROUND * 2 + BLOCK_PADDING_LEFT
				+ mRightRectF.width() + mOperation.width()
				+ BLOCK_PADDING_RIGHT;

		rf.right = rf.left + width;
		rf.bottom = rf.top + height;
		return rf;
	}

	@Override
	public void layout(float left, float top) {
		float l = getLeft() + BLOCK_PADDING_LEFT + BLOCK_ROUND;
		float t = getTop() + getHeight() / 2 - mOperation.height() / 2;
		mLeftRectF.setEmpty();
		mOperation.offsetTo(l, t);
		t = getRect().centerY() - mRightRectF.height() / 2;
		mRightRectF.offsetTo(mOperation.right + BLOCK_PADDING_LEFT, t);
		if (mRightBlock != null) {
			mRightBlock.offsetTo(mRightRectF.left, mRightRectF.top);
			removeInput(mRightInput);
		} else {
			mRightInput.setRect(mRightRectF);
			addInput(mRightInput);
		}
	}
	
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element rightChild = document.createElement("rightChild");
		parent.appendChild(rightChild);
		Block right = mRightBlock;
		if (right == null) {
			Element number = document.createElement("type");
			number.setTextContent("NUMBER");
			rightChild.appendChild(number);
			
			Element value = document.createElement("value");
			BlockInput input = getInput(0);
			if (input != null) {
				value.setTextContent(input.getValue());
			} else {
				value.setTextContent("0");
			}
			rightChild.appendChild(value);
		} else {
			right.toXmlNodeIncludeChilds(document, rightChild);
		}
		
		Element type = document.createElement("type");
		type.setTextContent("OPERATOR");
		parent.appendChild(type);
		Element value = document.createElement("value");
		value.setTextContent("LOGICAL_NOT");
		parent.appendChild(value);
	}

}
