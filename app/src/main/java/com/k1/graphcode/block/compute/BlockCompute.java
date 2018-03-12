package com.k1.graphcode.block.compute;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.RectF;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.RoundBlock;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.block.input.RoundBlockInput;
import com.k1.graphcode.block.variable.BlockVar;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.utils.Density;
/**
 * 
 * @author BinGe
 * 计算block的基类
 */
public class BlockCompute extends RoundBlock {

	private Path mPath = new Path();
	private RectF mOperation;
	private RectF mLeftRectF, mRightRectF;
	private Block mLeftBlock, mRightBlock;
	private String mOperationText;
	private int mRemove;
	private BlockInput mLeftInput = new BlockInput();
	private BlockInput mRightInput = new BlockInput();

	public BlockCompute() {
		super(Density.dip2px(100), Density.dip2px(30));
		mLeftRectF = new RectF(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
		mRightRectF = new RectF(mLeftRectF);
		mOperation = new RectF(0, 0, BLOCK_HEIGHT, 20);
		mLeftInput.setDialogTitle("Value");
		mRightInput.setDialogTitle("Value");
		mLeftInput.setValue("0");
		mRightInput.setValue("0");
		setBackground(Const.Colors.BlockDefault.COMPUTE);
	}

	public BlockCompute(String operation) {
		this();
		mOperationText = operation;
	}

	@Override
	public Class<?>[] getChildKinds() {
		return new Class<?>[] { RoundBlock.class, BlockCompute.class };
	}

	public void setLeft(Block block) {
		mLeftBlock = block;
		addBlockAnimation(block);
		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
	}

	public void setRight(Block block) {
		mRightBlock = block;
		addBlockAnimation(block);
		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
	}

	public void setLeftNoAnimation(Block block) {
		mLeftBlock = block;
		addBlock(block);
		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
	}

	public void setRightNoAnimation(Block block) {
		mRightBlock = block;
		addBlock(block);
		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
	}

	@Override
	public void removeBlockAnimation(Block block) {
		if (block == mLeftBlock) {
			mRemove = 1;
			mLeftBlock = null;
			mLeftRectF.set(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
		} else if (block == mRightBlock) {
			mRemove = 2;
			mRightBlock = null;
			mRightRectF.set(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
		}
		remove(block);

		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
	}

	@Override
	public void backBlockAnimation(Block block) {
		if (mRemove == 1) {
			setLeft(block);
		} else if (mRemove == 2) {
			setRight(block);
		}
		mRemove = 0;
		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
	}

	@Override
	public boolean onBlockDragMove(Block b, float x, float y, Path out) {
		if (!isMyChilds(b)) {
			return false;
		}
		if (mLeftBlock == null) {
			if (mLeftRectF.contains(x, y)) {
				out.reset();
				out.addRoundRect(mLeftRectF, BLOCK_ROUND, BLOCK_ROUND,
						Direction.CCW);
				return true;
			}
		} else {
			if (mLeftBlock.onBlockDragMove(b, x, y, out)) {
				return true;
			}
		}
		if (mRightBlock == null) {
			if (mRightRectF.contains(x, y)) {
				out.reset();
				out.addRoundRect(mRightRectF, BLOCK_ROUND, BLOCK_ROUND,
						Direction.CCW);
				return true;
			}
		} else {
			if (mRightBlock.onBlockDragMove(b, x, y, out)) {
				return true;
			}
		}
		return super.onBlockDragMove(b, x, y, out);
	}

	@Override
	public boolean isMyChilds(Block b) {
		return b instanceof BlockVar || b instanceof RoundBlockInput
				|| b instanceof BlockCompute;
	}

	@Override
	public boolean onBlockDragUp(Block b, float x, float y) {
		if (!isMyChilds(b)) {
			return false;
		}
		if (mLeftRectF.contains(x, y)) {
			if (mLeftBlock == null) {
				setLeft(b);
				return true;
			} else {
				return mLeftBlock.onBlockDragUp(b, x, y);
			}
		} else if (mRightRectF.contains(x, y)) {
			if (mRightBlock == null) {
				setRight(b);
				return true;
			} else {
				return mRightBlock.onBlockDragUp(b, x, y);
			}
		}

		return super.onBlockDragUp(b, x, y);
	}

	@Override
	public RectF computeRect() {
		RectF rf = getRect();
		if (mLeftBlock != null) {
			mLeftRectF.set(mLeftBlock.computeRect());
		}
		if (mRightBlock != null) {
			mRightRectF.set(mRightBlock.computeRect());
		}
		float height = Math.max(mLeftRectF.height(), mRightRectF.height());
		height = BLOCK_PADDING_TOP + height + BLOCK_PADDING_BOTTOM;
		float width = BLOCK_PADDING_LEFT + mLeftRectF.width()
				+ mRightRectF.width() + BLOCK_WIDTH / 2 + BLOCK_PADDING_RIGHT;

		rf.right = rf.left + width;
		rf.bottom = rf.top + height;
		return rf;
	}

	@Override
	public void layout(float left, float top) {
		float l = getLeft() + BLOCK_PADDING_LEFT;
		float t = getTop() + getHeight() / 2 - mLeftRectF.height() / 2;
		mLeftRectF.offsetTo(l, t);
		if (mLeftBlock != null) {
			removeInput(mLeftInput);
			mLeftBlock.offsetTo(l, t);
			if (mLeftBlock.isPayingAnimation()) {
				RectF rf = mLeftBlock.getAnimationLayoutRect();
				mLeftBlock.offsetTo(rf.left, rf.top);
			}
		} else {
			mLeftInput.setRect(mLeftRectF);
			addInput(mLeftInput);
		}

		l = getRight() - BLOCK_PADDING_RIGHT - mRightRectF.width();
		t = getTop() + getHeight() / 2 - mRightRectF.height() / 2;
		mRightRectF.offsetTo(l, t);
		if (mRightBlock != null) {
			removeInput(mRightInput);
			mRightBlock.offsetTo(l, t);
			if (mRightBlock.isPayingAnimation()) {
				RectF rf = mRightBlock.getAnimationLayoutRect();
				mRightBlock.offsetTo(rf.left, rf.top);
			}
		} else {
			mRightInput.setRect(mRightRectF);
			addInput(mRightInput);
		}

		mOperation.left = mLeftRectF.right;
		mOperation.right = mRightRectF.left;
		mOperation.top = mLeftRectF.top;
		mOperation.bottom = mLeftRectF.bottom;
	}

	@Override
	protected void draw(Canvas canvas) {

		Paint p = getPaint();

		mPath.reset();
		mPath.addRoundRect(getRect(), BLOCK_ROUND, BLOCK_ROUND, Direction.CW);
		mPath.setFillType(FillType.EVEN_ODD);
		canvas.drawPath(mPath, p);

		int color = p.getColor();
		p.setColor(VAR_COLOR);
		canvas.drawRoundRect(mLeftRectF, BLOCK_ROUND, BLOCK_ROUND, p);
		canvas.drawRoundRect(mRightRectF, BLOCK_ROUND, BLOCK_ROUND, p);
		p.setColor(color);

		stroke(mPath, canvas);

		if (mOperationText != null) {
			drawCenterText(canvas, mOperation, mOperationText);
		}
	}

	public void drawCenterText(Canvas canvas, RectF rect, String text) {
		Paint t = new Paint();
		t.setColor(Color.WHITE);
		t.setTextSize(rect.width() / text.length());
		FontMetrics fm = t.getFontMetrics();
		float x = rect.centerX() - t.measureText(text) / 2;
		float y = rect.centerY() + fm.descent;
		canvas.drawText(text, x, y, t);
	}

	/**
	 * <leftChild> 。。。 </leftChild> <rightChild> 。。。 </rightChild>
	 * <type>OPERATOR</type> <value>MULT</value>
	 */
	@Override
	public void toXmlNodeIncludeChilds(Document document, Element parent) {
		Element leftChild = document.createElement("leftChild");
		parent.appendChild(leftChild);
		Block left = mLeftBlock;
		if (left == null) {
			Element number = document.createElement("type");
			number.setTextContent("NUMBER");
			leftChild.appendChild(number);

			Element value = document.createElement("value");
			value.setTextContent(mLeftInput.getValue());
			leftChild.appendChild(value);
		} else {
			left.toXmlNodeIncludeChilds(document, leftChild);
		}

		Element rightChild = document.createElement("rightChild");
		parent.appendChild(rightChild);
		Block right = mRightBlock;
		if (right == null) {
			Element number = document.createElement("type");
			number.setTextContent("NUMBER");
			rightChild.appendChild(number);

			Element value = document.createElement("value");
			value.setTextContent(mRightInput.getValue());
			rightChild.appendChild(value);
		} else {
			right.toXmlNodeIncludeChilds(document, rightChild);
		}

		Element type = document.createElement("type");
		type.setTextContent("OPERATOR");
		parent.appendChild(type);

	}

	@Override
	public Element toXmlNode(Document document, Element parent) {
		return null;
	}

	@Override
	public JSONObject toJsonIncludeChilds() {
		JSONObject parent = toJson();
		try {
			Block left = mLeftBlock;
			if (left == null) {
				parent.put("left", mLeftInput.getValue());
			} else {
				parent.put("left", left.toJsonIncludeChilds());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			Block right = mRightBlock;
			if (right == null) {
				parent.put("right", mRightInput.getValue());
			} else {
				parent.put("right", right.toJsonIncludeChilds());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parent;
	}

	@Override
	public void json2Block(JSONObject json) {
		try {
			String leftValue = json.getString("left");
			JSONObject leftJson = null;
			try {
				leftJson = new JSONObject(leftValue);
			} catch (JSONException e) {
				leftJson = null;
				e.printStackTrace();
			}
			if (leftJson == null) {
				mLeftInput.setValue(leftValue);
			} else {
				Block leftBlock = Block.newFromJson(leftJson);
				setLeftNoAnimation(leftBlock);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			String rightValue = json.getString("right");
			JSONObject rightJson = null;
			try {
				rightJson = new JSONObject(rightValue);
			} catch (JSONException e) {
				rightJson = null;
				e.printStackTrace();
			}
			if (rightJson == null) {
				mRightInput.setValue(rightValue);
			} else {
				Block rightBlock = Block.newFromJson(rightJson);
				setRightNoAnimation(rightBlock);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void element2Variable(Block parent, Element element, Node node) {
		node = element.getFirstChild();
		BlockInput input = mLeftInput;
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				String tag = e.getTagName();
				if (tag.equals("leftChild")) {
					input = mLeftInput;
				} else if (tag.equals("rightChild")) {
					input = mRightInput;
				}
				Node child = node.getFirstChild();
				while (child != null) {
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element eChild = (Element) child;
						String childTag = eChild.getTagName();
						if (childTag.equals("leftChild")) {
							Block b = getTypeBlockWidthElement(e);
							if (b != null) {
								b.element2Variable(b, e, null);
								if (input == mLeftInput) {
									setLeftNoAnimation(b);
								} else if (input == mRightInput) {
									setRightNoAnimation(b);
								}
							}
						} else if (childTag.equals("type")) {
							String type = eChild.getTextContent();
							if (type.equals("NUMBER")) {
								Element valueElement = (Element) getNextElementNode(eChild);
								String value = valueElement.getTextContent();
								input.setValue(value);
							} else if (type.equals("USER_VARIABLE")) {
								Block b = getTypeBlockWidthElement(e);
								b.element2Variable(b, e, null);
								if (input == mLeftInput) {
									setLeftNoAnimation(b);
								} else if (input == mRightInput) {
									setRightNoAnimation(b);
								}
							}
							break;
						}
					}
					child = getNextElementNode(child);
				}
			}
			node = getNextElementNode(node);
		}
	}

}
