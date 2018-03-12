package com.k1.graphcode.block.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.HexagonBlock;
import com.k1.graphcode.block.LinearBlock;
import com.k1.graphcode.block.RoundBlock;
import com.k1.graphcode.block.compute.BlockCompute;
import com.k1.graphcode.block.logic.BlockLogic;
import com.k1.graphcode.block.output.BlockMotor;
import com.k1.graphcode.block.output.BlockSetColor;
import com.k1.graphcode.block.output.BlockSetLED;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.block.sub.BlockCall;
import com.k1.graphcode.block.sub.BlockSublock;
import com.k1.graphcode.block.variable.BlockVar;
import com.k1.graphcode.block.variable.BlockVarControl;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.utils.Density;

/**
 * 
 * @author BinGe
 *	控制类block的父类
 */

public class BlockControl extends Block {

	// 根控制块
	public static final int TYPE_ROOT = 1;
	// 子控制块
	public static final int TYPE_CHILD = 2;

	public static final int TYPE_VAR_NULL = 0;
	// 根控制块
	public static final int TYPE_VAR_ROUND = 1;
	// 子控制块
	public static final int TYPE_VAR_HEXAGON = 2;

	private int mType = TYPE_CHILD;
	private int mVarType = TYPE_VAR_ROUND;
	private Path mPath = new Path();
	// text
	private String[] mTexts;
	// text所在区域
	private RectF[] mTextRectFs;

	// 条件所在区域
	private RectF[] mVariableRectFs;
	// text后面跟着条件
	private Block[] mVariableBlocks;
	private BlockInput[] mInputs;
	// 存放子控制器的父亲，
	private LinearBlock[] mControlLinear;
	// 控制块上下面范围，用于判断是添加为子控制器，还是加添为兄弟控制器
	private RectF[] mArticleRect;

	private RectF[] mLeftStretchRect;
	// private Bitmap[] mBitmapStretch;
	private RectF[] mDrawArticleRect;
	private Bitmap[] mBitmapAtricle;

	private boolean rebuildCache = true;

	private int mRemove;
	private int mRemove2 = -1;
	private static Bitmap mLeftBitmap;

	public BlockControl() {
		this(2, new int[] { 0, 1 });
	}

	public BlockControl(int control, int[] variableIndex) {
		super();
		mInputs = new BlockInput[control + 1];
		for (int i = 0; i < mInputs.length; i++) {
			mInputs[i] = new BlockInput();
			mInputs[i].setDialogTitle("Value");
			addInput(mInputs[i]);
		}
		mVariableBlocks = new Block[control + 1];
		mVariableRectFs = new RectF[control + 1];
		if (variableIndex != null) {
			for (int i = 0; i < mVariableRectFs.length; i++) {
				for (int index : variableIndex) {
					if (index == i) {
						mVariableRectFs[i] = new RectF(0, 0, BLOCK_WIDTH,
								BLOCK_HEIGHT);
					}
				}
			}
		}

		mTexts = new String[control + 1];
		mTextRectFs = new RectF[control + 1];
		for (int i = 0; i < control + 1; i++) {
			mTextRectFs[i] = new RectF(CONTROL_PADDING, 0, CONTROL_WIDTH
					- CONTROL_PADDING, CONTROL_HEIGHT);
		}
		// mBitmapStretch = new Bitmap[control];
		mLeftStretchRect = new RectF[control];
		for (int i = 0; i < control; i++) {
			mLeftStretchRect[i] = new RectF();
		}
		mControlLinear = new LinearBlock[control];
		mArticleRect = new RectF[control + 1];
		mBitmapAtricle = new Bitmap[control + 1];
		for (int i = 0; i < mArticleRect.length; i++) {
			mArticleRect[i] = new RectF();
		}
		mDrawArticleRect = new RectF[control + 1];
		for (int i = 0; i < mDrawArticleRect.length; i++) {
			mDrawArticleRect[i] = new RectF();
		}
		for (int i = 0; i < control; i++) {
			mControlLinear[i] = new LinearBlock(CONTROL_WIDTH, CONTROL_HEIGHT);
		}
		for (Block b : mControlLinear) {
			addBlock(b);
		}
		setBackground(Const.Colors.BlockDefault.CONTROL);
	}

	@Override
	public void removeBlockAnimation(Block block) {
		if (isMyVariableBlock(block)) {
			if (mInsertInput != null) {
				for (int i = 0; i < mInsertInput.length; i++) {
					if (block == mInsertBlocks[i]) {
						mInsertBlocks[i] = null;
						mInsertRectFs[i].set(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
						mRemove2 = i;
					}
				}
			}
			for (int i = 0; i < mVariableBlocks.length; i++) {
				if (block == mVariableBlocks[i]) {
					mRemove = i;
					mVariableBlocks[i] = null;
					mVariableRectFs[i].set(0, 0, BLOCK_WIDTH, BLOCK_HEIGHT);
				}
			}
			remove(block);
			rebuildCcache();
		}
	}

	public void rebuildCcache() {
		// requestComputeRect();
		// rebuildCcache();
		rebuildCache = true;
	}

	public Block getVariableBlock(int index) {
		return mVariableBlocks[index];
	}

	public List<Block> getChildControls(int index) {
		return mControlLinear[index].getChilds();
	}

	@Override
	public void backBlockAnimation(Block block) {
		if (mRemove2 != -1) {
			insertBlockVariable(block, mRemove2, true);
			mRemove2 = -1;
		} else {
			setVariable(block, mRemove);
		}
	}

	public void setType(int controlType) {
		mType = controlType;
	}

	public void setVarType(int varType) {
		mVarType = varType;
	}

	public void setText(int index, String text) {
		mTexts[index] = text;
		Paint p = new Paint();
		p.setTextSize(BLOCK_ROUND);
		float width = p.measureText(text);
		float def = CONTROL_WIDTH - CONTROL_PADDING - CONTROL_PADDING;
		width = Math.max(width, def);
		mTextRectFs[index].right = mTextRectFs[index].left + width;
	}

	public void setControlNoAnimation(Block block, int parentIndex) {
		mControlLinear[parentIndex].addBlock(block);
	}

	public void setControlNoAnimation(Block block, int indexParent,
			int indexChild) {
		mControlLinear[indexParent].addBlock(block, indexChild);
	}

	public void setControl(Block block, int indexParent, int indexChild) {
		mControlLinear[indexParent].addBlockAnimation(block, indexChild);
	}

	public void setControl(Block block, int index) {
		setControl(block, index, mControlLinear[index].getChilds().size());
	}

	public void setVariable(Block block, int index) {
		boolean isVar = false;
		switch (mVarType) {
		case TYPE_VAR_ROUND:
			isVar = block instanceof RoundBlock
					|| block instanceof BlockCompute;
			break;
		case TYPE_VAR_HEXAGON:
			isVar = block instanceof HexagonBlock
					|| block instanceof BlockLogic;
			break;
		}
		if (isVar) {
			mVariableBlocks[index] = block;
			mVariableRectFs[index].set(block.getRect());
			addBlockAnimation(block);
		}
		rebuildCcache();
	}

	public void insertBlockVariable(Block block, int index, boolean anim) {
		if (block instanceof RoundBlock || block instanceof BlockCompute) {
			mInsertBlocks[index] = block;
			mInsertRectFs[index].set(block.getRect());
			if (anim) {
				addBlockAnimation(block);
			} else {
				addBlock(block);
			}
		}
		rebuildCcache();
	}

	public void setVariableNoAnimation(Block block, int index) {
		boolean isVar = false;
		switch (mVarType) {
		case TYPE_VAR_ROUND:
			isVar = block instanceof RoundBlock
					|| block instanceof BlockCompute;
			break;
		case TYPE_VAR_HEXAGON:
			isVar = block instanceof HexagonBlock
					|| block instanceof BlockLogic;
			break;
		}
		if (isVar) {
			mVariableBlocks[index] = block;
			mVariableRectFs[index].set(block.getRect());
			addBlock(block);
		}
		rebuildCcache();
	}

	public void setVariable(String text, Block block, int index) {
		mTexts[index] = text;
		setVariable(block, index);
	}

	public void setVariableRect(int index, RectF rect) {
		mVariableRectFs[index].set(rect);
	}

	private int controlCount() {
		return mTextRectFs.length;
	}

	@Override
	public void offset(float left, float top) {
		super.offset(left, top);
	}

	@Override
	public void offsetTo(float left, float top) {
		super.offsetTo(left, top);
	}

	@Override
	public boolean onBlockDragMove(Block b, float x, float y, Path out) {
		if (!isMyChilds(b)) {
			return false;
		}
		if (isVariableBlock(b)) {
			if (mInsertInput != null) {
				for (int i = 0; i < mInsertInput.length; i++) {
					BlockInput input = mInsertInput[i];
					if (input.getType() == BlockInput.TYPE_INPUT_VALUE) {
						Block block = mInsertBlocks[i];
						if (block == null) {
							if (input.getRect().contains(x, y)) {
								out.reset();
								out.addRoundRect(input.getRect(), BLOCK_ROUND,
										BLOCK_ROUND, Direction.CCW);
								return true;
							}
						} else {
							if (block.onBlockDragMove(b, x, y, out)) {
								rebuildCcache();
								return true;
							}
						}
					}
				}
			}

			for (int i = 0; i < mVariableRectFs.length; i++) {
				RectF rf = mVariableRectFs[i];
				if (rf != null && rf.contains(x, y)) {
					Block block = mVariableBlocks[i];
					if (block == null) {
						if (mType == TYPE_ROOT && mVarType == TYPE_VAR_NULL) {
							return false;
						}
						if (!isMyVariableBlock(b)) {
							return false;
						}
						out.reset();

						if (mVarType == TYPE_VAR_HEXAGON) {
							if (b instanceof RoundBlock) {
								return false;
							}
							HexagonBlock.initHexagonPath(rf, out);
						} else {
							if (b instanceof HexagonBlock) {
								return false;
							}
							out.addRoundRect(rf, BLOCK_ROUND, BLOCK_ROUND,
									Direction.CCW);
						}
						return true;
					} else {
						if (block.onBlockDragMove(b, x, y, out)) {
							rebuildCcache();
							return true;
						}
					}
				}
				for (LinearBlock linear : mControlLinear) {
					if (linear.contains(x, y)) {
						for (Block block : linear.getChilds()) {
							if (block.onBlockDragMove(b, x, y, out)) {
								return true;
							}
						}
					}
				}
			}
		}
		if (isControlBlock(b)) {
			for (int i = 0; i < mControlLinear.length; i++) {
				LinearBlock linear = mControlLinear[i];
				if (linear.contains(x, y)) {
					if (linear.isEmptyChilds()) {
						out.reset();
						out.addRoundRect(linear.getRect(), BLOCK_ROUND,
								BLOCK_ROUND, Direction.CCW);
						return true;
					}
					for (Block child : linear.getChilds()) {
						if (child.contains(x, y)) {
							return child.onBlockDragMove(b, x, y, out);
						}
					}
				}
			}
			RectF first = mArticleRect[0];
			RectF last = mArticleRect[mArticleRect.length - 1];
			RectF rectf = new RectF();
			// 没有子控制器的情况
			LinearBlock parent = (LinearBlock) getParent();
			if (first == last) {
				rectf.set(first);
				rectf.bottom = rectf.centerY();
				if (rectf.contains(x, y)) {
					out.reset();
					out.moveTo(rectf.left + BLOCK_ROUND, first.top);
					out.lineTo(rectf.right - BLOCK_ROUND, first.top);
					out.close();
					return true;
				} else {
					out.reset();
					out.moveTo(rectf.left + BLOCK_ROUND, first.bottom);
					out.lineTo(rectf.right - BLOCK_ROUND, first.bottom);
					out.close();
					return true;
				}
			} else if (first.contains(x, y)) {
				rectf.set(first);
				rectf.bottom = rectf.centerY();
				if (rectf.contains(x, y)) {
					if (parent.isMyChilds(b)) {
						out.reset();
						out.moveTo(rectf.left + BLOCK_ROUND, first.top);
						out.lineTo(rectf.right - BLOCK_ROUND, first.top);
						out.close();
						return true;
					}
				} else {
					out.reset();
					out.moveTo(rectf.left + CONTROL_PADDING + BLOCK_ROUND,
							first.bottom);
					out.lineTo(rectf.right - BLOCK_ROUND, first.bottom);
					out.close();
					// setControl(b, 0, 0);
					return true;
				}
			} else if (last.contains(x, y)) {
				rectf.set(last);
				rectf.bottom = rectf.centerY();
				if (rectf.contains(x, y)) {
					// setControl(b, 0);
					out.reset();
					out.moveTo(rectf.left + CONTROL_PADDING + BLOCK_ROUND,
							last.top);
					out.lineTo(rectf.right - BLOCK_ROUND, last.top);
					out.close();
					return true;
				} else {
					if (parent.isMyChilds(b)) {
						out.reset();
						out.moveTo(rectf.left + BLOCK_ROUND, last.bottom);
						out.lineTo(rectf.right - BLOCK_ROUND, last.bottom);
						out.close();
						return true;
					}
				}
			} else {
				for (int i = 1; i < mArticleRect.length - 1; i++) {
					rectf.set(mArticleRect[i]);
					if (rectf.contains(x, y)) {
						rectf.bottom = rectf.centerY();
						if (rectf.contains(x, y)) {
							// setControl(b, i - 1);
							out.reset();
							out.moveTo(rectf.left + CONTROL_PADDING
									+ BLOCK_ROUND, rectf.top);
							out.lineTo(rectf.right - BLOCK_ROUND, rectf.top);
							out.close();
							return true;
						} else {
							// setControl(b, i, 0);
							out.reset();
							out.moveTo(rectf.left + CONTROL_PADDING
									+ BLOCK_ROUND, rectf.bottom);
							out.lineTo(rectf.right - BLOCK_ROUND, rectf.bottom);
							out.close();
							return true;
						}
					}
				}
			}

		}
		return super.onBlockDragMove(b, x, y, out);
	}

	@Override
	public float dragX() {
		return mTextRectFs[0].centerX();
		// return super.dragX();
	}

	@Override
	public float dragY() {
		return mTextRectFs[0].centerY();
		// return super.dragY();
	}

	@Override
	public boolean onBlockDragUp(Block b, float x, float y) {
		if (!isMyChilds(b)) {
			return false;
		}
		if (isVariableBlock(b)) {
			if (mInsertInput != null) {
				for (int i = 0; i < mInsertInput.length; i++) {
					BlockInput input = mInsertInput[i];
					if (input.getType() == BlockInput.TYPE_INPUT_VALUE) {
						Block block = mInsertBlocks[i];
						if (block == null) {
							if (input.getRect().contains(x, y)) {
								insertBlockVariable(b, i, true);
								return true;
							}
						} else {
							boolean ok = block.onBlockDragUp(b, x, y);
							if (ok) {
								return ok;
							}
						}
					}
				}
			}

			for (int i = 0; i < mVariableRectFs.length; i++) {
				RectF rf = mVariableRectFs[i];
				if (rf != null && rf.contains(x, y)) {
					Block block = mVariableBlocks[i];
					if (block == null) {
						if (isMyVariableBlock(b)) {
							setVariable(b, i);
							return true;
						}
						return false;
					} else {
						boolean ok = block.onBlockDragUp(b, x, y);
						return ok;
					}
				}
				for (LinearBlock linear : mControlLinear) {
					if (linear.contains(x, y)) {
						for (Block block : linear.getChilds()) {
							if (block.contains(x, y)) {
								boolean ok = block.onBlockDragUp(b, x, y);
								return ok;
							}
						}
					}
				}
			}
		}
		if (isControlBlock(b)) {
			for (int i = 0; i < mControlLinear.length; i++) {
				LinearBlock linear = mControlLinear[i];
				if (linear.contains(x, y)) {
					boolean ok = false;
					for (Block child : linear.getChilds()) {
						if (child.contains(x, y)) {
							ok = child.onBlockDragUp(b, x, y);
							if (ok) {
								return true;
							}
						}
					}
					setControl(b, i);
					return true;
				}
			}
			RectF first = mArticleRect[0];
			RectF last = mArticleRect[mArticleRect.length - 1];
			RectF rectf = new RectF();
			// 没有子控制器的情况
			LinearBlock parent = (LinearBlock) getParent();
			if (first == last) {
				rectf.set(first);
				rectf.bottom = rectf.centerY();
				if (rectf.contains(x, y)) {
					int index = parent.getChilds().indexOf(this);
					parent.addBlockAnimation(b, index);
					return true;
				} else {
					int index = parent.getChilds().indexOf(this);
					parent.addBlockAnimation(b, index + 1);
					return true;
				}

			} else if (first.contains(x, y)) {
				rectf.set(first);
				rectf.bottom = rectf.centerY();
				if (rectf.contains(x, y)) {
					int index = parent.getChilds().indexOf(this);
					if (parent.isMyChilds(b)) {
						parent.addBlockAnimation(b, index);
						return true;
					}
				} else {
					setControl(b, 0, 0);
					return true;
				}
			} else if (last.contains(x, y)) {
				rectf.set(last);
				rectf.bottom = rectf.centerY();
				if (rectf.contains(x, y)) {
					setControl(b, 0);
					return true;
				} else {
					int index = parent.getChilds().indexOf(this);
					if (parent.isMyChilds(b)) {
						parent.addBlockAnimation(b, index + 1);
						return true;
					}
				}
			} else {
				for (int i = 1; i < mArticleRect.length - 1; i++) {
					rectf.set(mArticleRect[i]);
					if (rectf.contains(x, y)) {
						rectf.bottom = rectf.centerY();
						if (rectf.contains(x, y)) {
							setControl(b, i - 1);
							return true;
						} else {
							setControl(b, i, 0);
							return true;
						}
					}
				}
			}
		}
		return super.onBlockDragUp(b, x, y);
	}

	protected boolean isMyVariableBlock(Block b) {
		return b instanceof RoundBlock || b instanceof BlockCompute
				|| b instanceof HexagonBlock || b instanceof BlockLogic;
	}

	protected boolean isVariableBlock(Block b) {
		return b instanceof RoundBlock || b instanceof BlockCompute
				|| b instanceof HexagonBlock || b instanceof BlockLogic;
	}

	protected boolean isControlBlock(Block b) {
		return (b instanceof BlockControl) && !(b instanceof BlockMain)
				&& !(b instanceof BlockSubProgram)
				&& !(b instanceof BlockTrigger);
	}

	@Override
	public boolean contains(float x, float y) {
		return super.contains(x, y);
	}

	public boolean controlContains(float x, float y) {
		for (RectF r : mArticleRect) {
			if (r.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public RectF computeRect() {
		RectF rf = getRect();
		float w = 0;
		float h = 0;
		for (int index = 0; index < controlCount(); index++) {
			RectF r = mVariableRectFs[index];
			Block b = mVariableBlocks[index];
			if (r != null && b != null) {
				r.set(b.computeRect());
			}
			if (r == null) {
				h += CONTROL_HEIGHT;
				w = Math.max(w, CONTROL_WIDTH);
			} else {
				// String text = mTexts[index];
				// if (!TextUtils.isEmpty(text)) {
				// float textWidth = getPaint().measureText(text);
				// textWidth = Math.max(textWidth, mTextRectFs[index].width());
				// mTextRectFs[index].right = mTextRectFs[index].left +
				// textWidth;
				// }
				w = Math.max(w, CONTROL_PADDING + mTextRectFs[index].width()
						+ BLOCK_PADDING_LEFT + BLOCK_PADDING_RIGHT + r.width());
				h += BLOCK_PADDING_TOP + r.height() + BLOCK_PADDING_BOTTOM;
			}

			if (mControlLinear.length > index) {
				LinearBlock linear = mControlLinear[index];
				RectF linearRect = linear.computeRect();
				w = Math.max(w, CONTROL_PADDING + linearRect.width());
				h += linearRect.height();
			}
			if (mInsertRectFs != null && mInsertRectFs.length > 0 && index == 0) {
				int width = BLOCK_WIDTH + BLOCK_PADDING_LEFT * 2;
				w -= width;
				float height = 0;
				for (int i = 0; i < mInsertRectFs.length; i++) {
					Block block = mInsertBlocks[i];
					RectF rect = mInsertRectFs[i];
					if (block != null) {
						RectF blockRect = block.computeRect();
						w += (2 * BLOCK_PADDING_LEFT + blockRect.width());
						height = Math.max(blockRect.height()
								+ BLOCK_PADDING_TOP + BLOCK_PADDING_BOTTOM,
								height);

					} else {
						w += (rect.width() + BLOCK_PADDING_LEFT * 2);
					}
				}
				h = h - CONTROL_HEIGHT + height;
			}
		}
		rf.right = rf.left + Math.max(w, CONTROL_WIDTH);
		rf.bottom = rf.top + Math.max(h, CONTROL_HEIGHT);
		return rf;
	}

	private RectF[] mInsertRectFs;
	private BlockInput[] mInsertInput;
	private Block[] mInsertBlocks;

	public void insertBlockVar(BlockInput[] inputs) {
		mInsertRectFs = new RectF[inputs.length];
		mInsertInput = inputs;
		mInsertBlocks = new Block[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			mInsertRectFs[i] = new RectF(0, 0,
					BLOCK_WIDTH - BLOCK_PADDING_LEFT, BLOCK_HEIGHT);
			addInput(mInsertInput[i]);
		}
	}

	public Block getInsertBlock(BlockInput input) {
		if (mInsertInput != null) {
			for (int i = 0; i < mInsertInput.length; i++) {
				if (mInsertInput[i] == input) {
					return mInsertBlocks[i];
				}
			}
		}
		return null;
	}

	@Override
	public void layout(float left, float top) {
		float l = getLeft();
		left = l + CONTROL_PADDING;
		float height = 0;
		float width = 0;
		for (int i = 0; i < controlCount(); i++) {
			RectF r = mVariableRectFs[i];
			RectF draw = mDrawArticleRect[i];
			draw.left = l;
			draw.top = top;
			if (i > 0) {
				draw.top -= BLOCK_ROUND;
			}

			if (r == null) {
				height = CONTROL_HEIGHT;
				width = CONTROL_WIDTH;
			} else {
				height = r.height() + BLOCK_PADDING_TOP + BLOCK_PADDING_BOTTOM;
			}
			mTextRectFs[i].offsetTo(left,
					top + height / 2 - mTextRectFs[i].height() / 2);
			if (r != null) {
				r.offsetTo(mTextRectFs[i].right + BLOCK_PADDING_LEFT,
						mTextRectFs[i].centerY() - r.height() / 2);
				width = r.right - l + BLOCK_PADDING_RIGHT;
			}
			if (mVariableBlocks[i] != null) {
				if (mVariableBlocks[i].isPayingAnimation()) {
					RectF anim = mVariableBlocks[i].getAnimationLayoutRect();
					mVariableBlocks[i].offsetTo(anim.left, anim.top);
				} else {
					mVariableBlocks[i].offsetTo(r.left, r.top);
				}
				removeInput(mInputs[i]);
			} else {
				if (mVarType == TYPE_VAR_ROUND && r != null) {
					mInputs[i].setRect(r);
					addInput(mInputs[i]);
				}
			}
			if (mInsertRectFs != null && mInsertRectFs.length > 0 && i == 0) {
				float right = getRight();
				for (int k = 0; k < mInsertRectFs.length; k++) {
					int index = mInsertRectFs.length - 1 - k;
					Block block = mInsertBlocks[index];
					RectF rect = mInsertRectFs[index];
					if (block != null) {
						rect.set(block.getRect());
						height = rect.height() + BLOCK_PADDING_TOP
								+ BLOCK_PADDING_BOTTOM;
						mTextRectFs[i].offsetTo(left, top + height / 2
								- mTextRectFs[i].height() / 2);
					}
					BlockInput input = mInsertInput[index];
					right = right - rect.width() - BLOCK_PADDING_LEFT;
					rect.offsetTo(right, getRect().centerY() - rect.height()
							/ 2);
					input.setRect(rect);

					if (block != null) {
						if (block.isPayingAnimation()) {
							RectF anim = block.getAnimationLayoutRect();
							block.offsetTo(anim.left, anim.top);
						} else {
							block.offsetTo(rect.left, rect.top);
						}
						removeInput(mInsertInput[i]);
					} else {
						addInput(input);
					}
				}
				mArticleRect[0].right = getRight();
			}

			mArticleRect[i]
					.set(getLeft(), top, getLeft() + width, top + height);
			top += height;
			draw.right = width;
			draw.bottom = top;
			draw.bottom += CONTROL_GEAR_ROUND;
			if (mControlLinear.length > i) {
				mControlLinear[i].offsetTo(left, top);
				mLeftStretchRect[i].set(getLeft(), mControlLinear[i].getTop()
						+ BLOCK_ROUND, getLeft() + CONTROL_PADDING,
						mControlLinear[i].getBottom() - BLOCK_ROUND);
				top = mControlLinear[i].getBottom();
				draw.bottom = mLeftStretchRect[i].top;
			}
		}
		if (controlCount() == 1) {
			mDrawArticleRect[0].right = mDrawArticleRect[0].left + getWidth();
		}
	}

	@Override
	public void recycle() {
		super.recycle();
		if (mBitmapAtricle != null && mBitmapAtricle.length > 0) {
			for (int i = 0; i < mBitmapAtricle.length; i++) {
				if (mBitmapAtricle[i] != null) {
					mBitmapAtricle[i].recycle();
					mBitmapAtricle[i] = null;
				}
			}
		}
		// if (mBitmapStretch != null && mBitmapStretch.length > 0) {
		// for (int i = 0; i < mBitmapStretch.length; i++) {
		// if (mBitmapStretch[i] != null) {
		// mBitmapStretch[i].recycle();
		// mBitmapStretch[i] = null;
		// }
		// }
		// }
	}

	@Override
	protected void draw(Canvas canvas) {
		if (rebuildCache) {
			drawInCache();
			rebuildCache = false;
		}
		for (int i = 0; i < mDrawArticleRect.length; i++) {
			RectF des = mDrawArticleRect[i];
			canvas.drawBitmap(mBitmapAtricle[i], des.left, des.top, null);
		}

		if (mLeftBitmap == null) {
			Rect rect = new Rect(0, 0, CONTROL_PADDING, Density.dip2px(10));
			mLeftBitmap = Bitmap.createBitmap(Math.round(rect.width()),
					Math.round(rect.height()), Config.ARGB_4444);
			Canvas c = new Canvas(mLeftBitmap);
			int color = getPaint().getColor();
			c.drawColor(color);
			Paint p = getStrokePaint();
			c.drawLine(0, 0, 0, rect.height(), p);
			c.drawLine(rect.width(), 0, rect.width(), rect.height(), p);
		}

		RectF rect = new RectF();
		for (int i = 0; i < mLeftStretchRect.length; i++) {
			int max = Const.BITMAP_MAX;
			RectF des = new RectF(mLeftStretchRect[i]);
			rect.set(des);
			rect.bottom = rect.top + max;
			while (rect.bottom < des.bottom) {
				canvas.drawBitmap(mLeftBitmap, null, rect, null);
				rect.top = rect.bottom;
				rect.bottom += max;
			}
			rect.bottom = des.bottom;
			canvas.drawBitmap(mLeftBitmap, null, rect, null);
		}
		// if (mInsertInput!= null) {
		// for (BlockInput text : getr) {
		// Paint p = new Paint();
		// p.setColor(Color.RED & 0x33ffffff);
		// canvas.drawRect(getRect(), p);
		// }
		// }

	}

	private void drawInCache() {
		float old_left = getLeft();
		float old_top = getTop();
		offsetTo(0, 0);

		List<Bitmap> bitmaps = new ArrayList<Bitmap>();
		int screenHeight = CONTROL_HEIGHT * 2;
		int top = 0;
		RectF rect = new RectF();
		while (top < getHeight() + CONTROL_GEAR_ROUND) {

			boolean needDraw = false;
			rect.set(0, top, Math.round(getWidth()), top + screenHeight);
			for (int i = 0; i < mDrawArticleRect.length; i++) {
				RectF des = mDrawArticleRect[i];
				if (des.intersect(rect)) {
					needDraw = true;
					break;
				}
			}
			if (needDraw) {
				Bitmap bitmap = Bitmap.createBitmap(Math.round(getWidth()),
						screenHeight, Config.ARGB_4444);
				Canvas canvas = new Canvas(bitmap);
				offsetTo(0, -top);
				drawMySelf(canvas);
				offsetTo(0, 0);
				bitmaps.add(bitmap);
			} else {
				Bitmap bitmap = Bitmap.createBitmap(1, screenHeight,
						Config.ARGB_4444);
				bitmaps.add(bitmap);
			}

			top += screenHeight;
		}

		for (int i = 0; i < mDrawArticleRect.length; i++) {
			RectF des = mDrawArticleRect[i];
			if (mBitmapAtricle[i] != null && !mBitmapAtricle[i].isRecycled()) {
				mBitmapAtricle[i].recycle();
				mBitmapAtricle[i] = null;
			}
			mBitmapAtricle[i] = Bitmap.createBitmap(Math.round(des.width()),
					Math.round(des.height()), Config.ARGB_4444);
			Canvas c = new Canvas(mBitmapAtricle[i]);
			top = 0;
			int bottom = screenHeight;
			for (Bitmap bitmap : bitmaps) {
				if (bottom < des.top) {
					top = bottom;
					bottom += screenHeight;
					continue;
				}
				if (top > des.bottom) {
					top = bottom;
					bottom += screenHeight;
					continue;
				}
				c.drawBitmap(bitmap, des.left, top - des.top, null);
				top = bottom;
				bottom += screenHeight;
			}
		}
		offsetTo(old_left, old_top);
		for (Bitmap bitmap : bitmaps) {
			bitmap.recycle();
			bitmap = null;
		}
		// return bitmap;
	}

	private void drawMySelf(Canvas canvas) {
		computePath();
		RectF rf = new RectF();
		mPath.computeBounds(rf, false);
		float x = rf.left;
		float y = rf.top;
		mPath.offset(getLeft() - x, getTop() - y);
		canvas.drawPath(mPath, getPaint());

		Paint p = getPaint();
		int color = p.getColor();
		p.setColor(VAR_COLOR);
		for (RectF r : mVariableRectFs) {
			if (r != null) {
				switch (mVarType) {
				case TYPE_VAR_ROUND:
					canvas.drawRoundRect(r, BLOCK_ROUND, BLOCK_ROUND, p);
					break;
				case TYPE_VAR_HEXAGON:
					Path path = new Path();
					HexagonBlock.initHexagonPath(r, path);
					canvas.drawPath(path, p);
					break;
				}
			}
		}
		if (mInsertRectFs != null) {
			for (RectF r : mInsertRectFs) {
				if (r != null) {
					canvas.drawRoundRect(r, BLOCK_ROUND, BLOCK_ROUND, p);
				}
			}
		}
		p.setColor(color);

		stroke(mPath, canvas);

		for (int i = 0; i < mTextRectFs.length; i++) {
			String text = mTexts[i];
			if (!TextUtils.isEmpty(text)) {
				drawLeftText(canvas, mTextRectFs[i], text);
			}
		}

	}

	private void computePath() {
		RectF root = getRect();
		float left = root.left;
		float top = root.top;
		float right = 0;
		float bottom = 0;

		RectF rect = new RectF();
		mPath.reset();
		mPath.moveTo(left, root.top + BLOCK_ROUND);
		mPath.arcTo(new RectF(left, top, left + 2 * BLOCK_ROUND, top + 2
				* BLOCK_ROUND), 180, 90);
		RectF first = mVariableRectFs[0];
		if (mInsertInput != null) {
			right = getRight();
		} else if (first != null) {
			right = first.right + BLOCK_PADDING_RIGHT;
		} else {
			right = left + CONTROL_WIDTH;
		}
		if (mType == TYPE_CHILD) {
			mPath.lineTo(left + CONTROL_GEAR_PADDING - CONTROL_GEAR_ROUND, top);
			rect.set(0, 0, CONTROL_GEAR_ROUND * 2, CONTROL_GEAR_ROUND * 2);
			rect.offsetTo(left + CONTROL_GEAR_PADDING - rect.width() / 2, top
					- rect.height() / 2);
			mPath.arcTo(rect, 180, -180);
		}
		mPath.lineTo(right - BLOCK_ROUND, top);
		mPath.arcTo(new RectF(right - 2 * BLOCK_ROUND, top, right, top + 2
				* BLOCK_ROUND), -90, 90);

		for (int i = 0; i < mControlLinear.length; i++) {
			bottom = top + CONTROL_HEIGHT;
			if (mVariableRectFs[i] != null) {
				bottom = top + mVariableRectFs[i].height() + BLOCK_PADDING_TOP
						+ BLOCK_PADDING_BOTTOM;
			}

			// mLeftStretchRect[i].left = left;
			// mLeftStretchRect[i].top = bottom + BLOCK_ROUND;

			mPath.lineTo(right, bottom - BLOCK_ROUND);
			mPath.arcTo(new RectF(right - 2 * BLOCK_ROUND, bottom - 2
					* BLOCK_ROUND, right, bottom), 0, 90);

			mPath.lineTo(root.left + CONTROL_PADDING + CONTROL_GEAR_PADDING
					+ CONTROL_GEAR_ROUND, bottom);
			rect.set(0, 0, CONTROL_GEAR_ROUND * 2, CONTROL_GEAR_ROUND * 2);
			rect.offsetTo(root.left + CONTROL_GEAR_PADDING + CONTROL_PADDING
					- rect.width() / 2, bottom - rect.height() / 2);
			mPath.arcTo(rect, 0, 180);

			mPath.lineTo(left + CONTROL_PADDING + BLOCK_ROUND, bottom);

			top = bottom;
			left = root.left + CONTROL_PADDING;

			bottom = mControlLinear[i].getBottom();

			// mLeftStretchRect[i].right = left;
			// mLeftStretchRect[i].bottom = bottom - BLOCK_ROUND;

			mPath.arcTo(new RectF(left, top, left + BLOCK_ROUND * 2, top + 2
					* BLOCK_ROUND), -90, -90);
			mPath.lineTo(left, bottom - BLOCK_ROUND);
			mPath.arcTo(new RectF(left, bottom - BLOCK_ROUND * 2, left
					+ BLOCK_ROUND * 2, bottom), 180, -90);
			RectF var = mVariableRectFs[i + 1];
			if (var != null) {
				right = var.right + BLOCK_PADDING_RIGHT;
			} else {
				right = root.left + CONTROL_WIDTH;
			}
			top = bottom;

			mPath.lineTo(root.left + CONTROL_PADDING + CONTROL_GEAR_PADDING
					- CONTROL_GEAR_ROUND, top);
			rect.set(0, 0, CONTROL_GEAR_ROUND * 2, CONTROL_GEAR_ROUND * 2);
			rect.offsetTo(root.left + CONTROL_PADDING + CONTROL_GEAR_PADDING
					- rect.width() / 2, top - rect.height() / 2);
			mPath.arcTo(rect, 180, -180);

			mPath.lineTo(right - BLOCK_ROUND, top);
			mPath.arcTo(new RectF(right - 2 * BLOCK_ROUND, top, right, top + 2
					* BLOCK_ROUND), -90, 90);
		}
		bottom = root.bottom;
		left = root.left;
		mPath.lineTo(right, bottom - BLOCK_ROUND);
		mPath.arcTo(new RectF(right - BLOCK_ROUND * 2,
				bottom - BLOCK_ROUND * 2, right, bottom), 0, 90);
		if (mType == TYPE_CHILD) {
			mPath.lineTo(left + CONTROL_GEAR_PADDING + CONTROL_GEAR_ROUND,
					bottom);
			rect.set(0, 0, CONTROL_GEAR_ROUND * 2, CONTROL_GEAR_ROUND * 2);
			rect.offsetTo(left + CONTROL_GEAR_PADDING - rect.width() / 2,
					bottom - rect.height() / 2);
			mPath.arcTo(rect, 0, 180);
		}
		mPath.lineTo(left + BLOCK_ROUND, bottom);
		mPath.arcTo(new RectF(left, bottom - BLOCK_ROUND * 2, left
				+ BLOCK_ROUND * 2, bottom), 90, 90);
		mPath.close();

		mPath.setFillType(FillType.EVEN_ODD);

		rect.set(0, 0, BLOCK_ROUND, BLOCK_ROUND);
		rect.offsetTo(root.left - rect.width() / 2, root.top - rect.height()
				/ 2);
	}

	public Path getPath() {
		return mPath;
	}

	public JSONObject toJsonIncludeChilds() {
		JSONObject parent = toJson();
		try {
			if (mControlLinear.length > 0) {
				JSONObject controls = new JSONObject();
				parent.put("controls", controls);
				for (int i = 0; i < mControlLinear.length; i++) {
					JSONObject control = new JSONObject();
					controls.put(i + "", control);
					if (!mControlLinear[i].isEmptyChilds()) {
						for (int j = 0; j < mControlLinear[i].getChilds()
								.size(); j++) {
							control.put(j + "", mControlLinear[i].getChilds()
									.get(j).toJsonIncludeChilds());
						}
					}
				}
			}

			if (mVarType != TYPE_VAR_NULL) {
				JSONObject vars = new JSONObject();
				parent.put("vars", vars);
				for (int i = 0; i < mVariableBlocks.length; i++) {
					Block var = mVariableBlocks[i];
					if (var == null) {
						BlockInput input = getInput(i);
						if (input == null) {
							vars.put(i + "", null);
						} else {
							String value = getInput(i).getValue();
							vars.put(i + "", value);
						}
					} else {
						vars.put(i + "", var.toJsonIncludeChilds());
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return parent;
	}

	@Override
	public void json2Block(JSONObject json) {
		try {
			JSONObject controls = json.getJSONObject("controls");
			if (controls != null) {
				Iterator<?> keys = controls.keys();
				List<String> keyList = new ArrayList<String>();
				while (keys.hasNext()) {
					keyList.add(keys.next().toString());
				}
				Collections.sort(keyList);
				for (int parent = 0; parent < keyList.size(); parent++) {
					String key = keyList.get(parent);
					JSONObject childJson = controls.getJSONObject(key);
					Iterator<?> childKeys = childJson.keys();
					List<String> childkeyList = new ArrayList<String>();
					while (childKeys.hasNext()) {
						childkeyList.add(childKeys.next().toString());
					}
					Collections.sort(childkeyList);
					for (int child = 0; child < childkeyList.size(); child++) {
						String childsKey = childkeyList.get(child);
						JSONObject childControl = childJson
								.getJSONObject(childsKey);
						Block childBlock = Block.newFromJson(childControl);
						setControlNoAnimation(childBlock, parent, child);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			JSONObject vars = json.getJSONObject("vars");
			if (vars != null) {
				Iterator<?> keys = vars.keys();
				List<String> keyList = new ArrayList<String>();
				while (keys.hasNext()) {
					keyList.add(keys.next().toString());
				}
				Collections.sort(keyList);
				for (int index = 0; index < keyList.size(); index++) {
					String key = keyList.get(index);
					String var = vars.getString(key);
					JSONObject varJson = null;
					try {
						varJson = new JSONObject(var);
					} catch (JSONException e) {
						varJson = null;
					}
					if (varJson == null) {
						getInput(index).setValue(var);
					} else {
						Block childBlock = Block.newFromJson(varJson);
						setVariableNoAnimation(childBlock, index);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	class Struc {
		int index;
		BlockControl block;

		public Struc(BlockControl block) {
			this.block = block;
		}
	}

	private List<Struc> mStrucs = new ArrayList<BlockControl.Struc>();
	
	@Override
	public Node element2Block(Element list, Node node) {
		mStrucs.clear();

		Struc struc = new Struc(this);
		mStrucs.add(struc);
		// BlockControl parent = this;
		// int parentIndex = 0;
		while (node != null) {
			Element brick = (Element) node;
			String type = brick.getAttribute("type").trim();
			// LogUtils.d("type : " + type);
			BlockControl control = null;
			if (type.equals("ForeverBrick")) {
				BlockForever block = new BlockForever();
				struc.block.setControlNoAnimation(block, struc.index);
				control = block;
				struc = new Struc(block);
				mStrucs.add(struc);
			} else if (type.equals("LoopEndlessBrick")) {
				// parent = (BlockControl) parent.getParent().getParent();
				mStrucs.remove(struc);
				struc = mStrucs.get(mStrucs.size() - 1);
			} else if (type.equals("RepeatBrick")) {
				BlockRepeat block = new BlockRepeat();
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
				control = block;
				// parent = block;
				struc = new Struc(block);
				mStrucs.add(struc);
			} else if (type.equals("LoopEndBrick")) {
				// parent = (BlockControl) parent.getParent().getParent();
				mStrucs.remove(struc);
				struc = mStrucs.get(mStrucs.size() - 1);
			} else if (type.equals("IfLogicBeginBrick")) {
				BlockIfElse block = new BlockIfElse();
				// parent.setControlNoAnimation(block, parentIndex);
				// parentIndex = 0;
				struc.block.setControlNoAnimation(block, struc.index);
				control = block;
				// parent = block;
				struc = new Struc(block);
				mStrucs.add(struc);
			} else if (type.equals("IfLogicElseBrick")) {
				// parentIndex = 1;
				struc.index = 1;
			} else if (type.equals("IfLogicEndBrick")) {
				// parent = (BlockControl) parent.getParent().getParent();
				// parentIndex = 0;
				mStrucs.remove(struc);
				struc = mStrucs.get(mStrucs.size() - 1);
			} else if (type.equals("DoWhileBrick")) {
				BlockWhile block = new BlockWhile();
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
				control = block;
				// parent = block;
				struc = new Struc(block);
				mStrucs.add(struc);
			} else if (type.equals("DoWhileEndBrick")) {
				// parent = (BlockControl) parent.getParent().getParent();
				mStrucs.remove(struc);
				struc = mStrucs.get(mStrucs.size() - 1);
			} else if (type.equals("WaitBrick")) {
				BlockDelay block = new BlockDelay();
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
				control = block;
			} else if (type.equals("SetVariableBrick")) {
				BlockVarControl block = new BlockVarControl();
				Element nameElement = getNextElementByTagName(
						node.getFirstChild(), "userVariable");
				if (nameElement != null) {
					String name = nameElement.getTextContent();
					if (name != null && !name.equals("var")) {
						block.setVar(name);
						BlockVar var = new BlockVar();
						var.setName(name);
						var.setUseCacheIncludeChilds(true);
						ProjectCache.getInstence().getCurrentProject()
								.saveBlock(Const.EditType.VARIABLE, var);
					}
				}
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
				control = block;
			} else if (type.equals("IBrickMotorBrick")) {
				BlockMotor block = new BlockMotor();
				block.element2Variable(null, brick, null);
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
			} else if (type.equals("IBrickLEDBrick")) {
				BlockSetLED block = new BlockSetLED();
				block.element2Variable(null, brick, null);
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
			} else if (type.equals("IBrickRGBLedBrick")) {
				BlockSetColor block = new BlockSetColor();
				block.element2Variable(null, brick, null);
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
			} else if (type.equals("BroadcastWaitBrick")) {
				BlockCall block = new BlockCall();
				Element subElement = getFirstElementByTagName(brick,
						"broadcastMessage");
				if (subElement != null) {
					String name = subElement.getTextContent();
					if (!TextUtils.isEmpty(name)) {
						BlockSublock sub = new BlockSublock();
						sub.setName(name);
						block.setVariableNoAnimation(sub, 0);
					}
				}
				// parent.setControlNoAnimation(block, parentIndex);
				struc.block.setControlNoAnimation(block, struc.index);
			}
			if (control != null) {
				Node formulaListNode = brick.getFirstChild();
				while (formulaListNode != null) {
					if (formulaListNode.getNodeType() == Node.ELEMENT_NODE) {
						Node first = formulaListNode.getFirstChild();
						while (first != null) {
							if (first.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) first;
								if (e.getTagName().equals("formula")) {
									Block var = getTypeBlockWidthElement(e);
									if (var != null) {
										var.element2Variable(var, e, null);
										control.setVariableNoAnimation(var, 0);
									} else {
										Element typeElement = getFirstElementByTagName(
												e, "type");
										if (typeElement != null) {
											type = typeElement.getTextContent();
											if (type.equals("NUMBER")) {
												Element valueElement = getNextElementByTagName(
														typeElement, "value");
												String value = valueElement
														.getTextContent();
												BlockInput input = control
														.getInput(0);
												if (input != null) {
													input.setValue(value);
												}
											}
										}
									}
								}
							}
							first = getNextElementNode(first);
						}
					}
					formulaListNode = getNextElementNode(formulaListNode);
				}
				control = null;
			}
			node = getNextElementNode(node);
		}

		Node formulaListNode = getNextElementNode(list);
		while (formulaListNode != null) {
			Node first = formulaListNode.getFirstChild();
			while (first != null) {
				if (first.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) first;
					if (e.getTagName().equals("formula")) {
						Block var = getTypeBlockWidthElement(e);
						if (var != null) {
							var.element2Variable(var, e, null);
							setVariableNoAnimation(var, 0);
						}
					}
				}
				first = getNextElementNode(first);
			}
			formulaListNode = getNextElementNode(formulaListNode);
		}

		return node;
	}

}
