package com.k1.graphcode.block.logic;

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
import com.k1.graphcode.block.HexagonBlock;
import com.k1.graphcode.block.RoundBlock;
import com.k1.graphcode.block.compute.BlockCompute;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.block.input.HexagonBlockInput;
import com.k1.graphcode.block.input.RoundBlockInput;
import com.k1.graphcode.block.variable.BlockVar;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.utils.Density;

/**
 * 
 * @author BinGe
 * 逻辑型block
 */
public class BlockLogic extends HexagonBlock {

	public static final int CHILD_TYPE_ROUND = 1;
	public static final int CHILD_TYPE_HEXAGON = 2;

	private int mType = CHILD_TYPE_ROUND;

	private Path mPath = new Path();
	protected RectF mOperation;
	protected String mOperationText;
	protected RectF mLeftRectF, mRightRectF;
	protected Block mLeftBlock, mRightBlock;
	private int mRemove;

	protected BlockInput mLeftInput = new BlockInput();
	protected BlockInput mRightInput = new BlockInput();

	private Paint mTextPaint = new Paint();

	public BlockLogic(int type, String operation) {
		this();
		mType = type;
		mOperationText = operation;
		if (type == CHILD_TYPE_ROUND) {
			mLeftInput.setDialogTitle("Value");
			mRightInput.setDialogTitle("Value");
			mLeftInput.setValue("0");
			mRightInput.setValue("0");
		}
		setBackground(Const.Colors.BlockDefault.LOGIC);
		mTextPaint.setTextSize(BLOCK_WIDTH / 2);
		mTextPaint.setColor(Color.WHITE);
	}

	public BlockLogic() {
		super(Density.dip2px(100), Density.dip2px(30));
		mLeftRectF = new RectF(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
		mRightRectF = new RectF(mLeftRectF);
		mOperation = new RectF(0, 0, Density.dip2px(20), 20);
	}

	@Override
	public Class<?>[] getChildKinds() {
		return new Class<?>[] { RoundBlock.class, BlockCompute.class };
	}

	public void setLeft(Block block) {
		// removeBlock(mLeftBlock);
		mLeftBlock = block;
		addBlock(block);

		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
	}

	public void setRight(Block block) {
		// removeBlock(mRightBlock);
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
		BlockControl control = getRootControl(this);
		if (control != null) {
			control.rebuildCcache();
		}
		if (mRemove == 1) {
			setLeft(block);
		} else if (mRemove == 2) {
			setRight(block);
		}
		mRemove = 0;
	}

	@Override
	public boolean onBlockDragMove(Block b, float x, float y, Path out) {
		if (mRightBlock == null) {
			if (isMyChilds(b)) {
				if (mRightRectF.contains(x, y)) {
					out.reset();
					out.addRoundRect(mRightRectF, BLOCK_ROUND, BLOCK_ROUND,
							Direction.CCW);
					return true;
				}
			}
		} else {
			if (mRightBlock.onBlockDragMove(b, x, y, out)) {
				return true;
			}
		}
		if (mLeftBlock == null) {
			if (isMyChilds(b)) {
				if (mLeftRectF.contains(x, y)) {
					out.reset();
					out.addRoundRect(mLeftRectF, BLOCK_ROUND, BLOCK_ROUND,
							Direction.CCW);
					return true;
				}
			}
		} else {
			if (mLeftBlock.onBlockDragMove(b, x, y, out)) {
				return true;
			}
		}
		return super.onBlockDragMove(b, x, y, out);
	}

	@Override
	public boolean onBlockDragUp(Block b, float x, float y) {
		if (mLeftRectF.contains(x, y)) {
			if (mLeftBlock == null) {
				if (!isMyChilds(b)) {
					return false;
				}
				setLeft(b);
				return true;
			} else {
				return mLeftBlock.onBlockDragUp(b, x, y);
			}
		} else if (mRightRectF.contains(x, y)) {
			if (mRightBlock == null) {
				if (!isMyChilds(b)) {
					return false;
				}
				setRight(b);
				return true;
			} else {
				return mRightBlock.onBlockDragUp(b, x, y);
			}
		}

		return super.onBlockDragUp(b, x, y);
	}

	@Override
	public boolean isMyChilds(Block b) {
		switch (mType) {
		case CHILD_TYPE_ROUND:
			return b instanceof BlockVar || b instanceof RoundBlockInput
					|| b instanceof BlockCompute;
		case CHILD_TYPE_HEXAGON:
			return b instanceof BlockLogic || b instanceof HexagonBlockInput;
		}
		return false;
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
		float width = BLOCK_ROUND * 2 + BLOCK_PADDING_LEFT + mLeftRectF.width()
				+ mRightRectF.width() + BLOCK_PADDING_RIGHT;
		float textWidth = mTextPaint.measureText(mOperationText);
		width += textWidth;

		rf.right = rf.left + width;
		rf.bottom = rf.top + height;
		return rf;
	}

	@Override
	public void layout(float left, float top) {
		float l = getLeft() + BLOCK_PADDING_LEFT + BLOCK_ROUND;
		float t = getTop() + getHeight() / 2 - mLeftRectF.height() / 2;
		mLeftRectF.offsetTo(l, t);
		if (mLeftBlock != null) {
			removeInput(mLeftInput);
			mLeftBlock.offsetTo(l, t);
		} else {
			mLeftInput.setRect(mLeftRectF);
			addInput(mLeftInput);
		}

		l = getRight() - BLOCK_PADDING_RIGHT - BLOCK_ROUND
				- mRightRectF.width();
		t = getTop() + getHeight() / 2 - mRightRectF.height() / 2;
		mRightRectF.offsetTo(l, t);
		if (mRightBlock != null) {
			removeInput(mRightInput);
			mRightBlock.offsetTo(l, t);
		} else {
			mRightInput.setRect(mRightRectF);
			addInput(mRightInput);
		}
		if (mLeftRectF.right < mRightRectF.left) {
			mOperation.top = mLeftRectF.top;
			mOperation.bottom = mLeftRectF.bottom;
			mOperation.left = mLeftRectF.right;
			mOperation.right = mRightRectF.left;
		}

		if (mType == CHILD_TYPE_HEXAGON) {
			removeInput(mLeftInput);
			removeInput(mRightInput);
		}

	}

	@Override
	protected void draw(Canvas canvas) {
		initHexagonPath(getRect(), mPath);
		mPath.setFillType(FillType.EVEN_ODD);
		canvas.drawPath(mPath, getPaint());
		stroke(mPath, canvas);

		Paint p = getPaint();
		int color = p.getColor();
		p.setColor(VAR_COLOR);
		switch (mType) {
		case CHILD_TYPE_ROUND:
			canvas.drawRoundRect(mLeftRectF, BLOCK_ROUND, BLOCK_ROUND, p);
			canvas.drawRoundRect(mRightRectF, BLOCK_ROUND, BLOCK_ROUND, p);
			break;
		case CHILD_TYPE_HEXAGON:
			Path path = new Path();
			path.setFillType(FillType.EVEN_ODD);
			initHexagonPath(mLeftRectF, path);
			canvas.drawPath(path, p);
			initHexagonPath(mRightRectF, path);
			canvas.drawPath(path, p);
			break;
		}
		p.setColor(color);

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
			BlockInput input = mLeftInput;
			if (input != null) {
				value.setTextContent(input.getValue());
			} else {
				value.setTextContent("0");
			}
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
			BlockInput input = mRightInput;
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
				if (mType == CHILD_TYPE_ROUND) {
					parent.put("left", mLeftInput.getValue());
				}
			} else {
				parent.put("left", left.toJsonIncludeChilds());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			Block right = mRightBlock;
			if (right == null) {
				if (mType == CHILD_TYPE_ROUND) {
					parent.put("right", mRightInput.getValue());
				}
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
				setLeft(leftBlock);
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
				setRight(rightBlock);
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
							b.element2Variable(b, e, null);
							if (input == mLeftInput) {
								setLeft(b);
							} else if (input == mRightInput) {
								setRight(b);
							}
							break;
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
									setLeft(b);
								} else if (input == mRightInput) {
									setRight(b);
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
