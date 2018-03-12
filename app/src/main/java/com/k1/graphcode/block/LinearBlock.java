package com.k1.graphcode.block;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import com.k1.graphcode.BuildConfig;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.utils.BlockAnimationUtils;
/**
 * 
 * @author BinGe
 *	线性block，所有添加到此的子block都是按顺序排列下去的
 */
public class LinearBlock extends Block {

	private int mPadding;
	private int mRemoveIndex;
	private Block mRemoveBlock;
	private BlockAnimationUtils mAnima = new BlockAnimationUtils();
	private Class<?>[] mChildType;

	public LinearBlock() {
		this(0);
	}

	public LinearBlock(int padding) {
		super();
		mPadding = padding;
	}

	public LinearBlock(float w, float h) {
		super();
		RectF rf = getRect();
		rf.bottom = rf.top + h;
		rf.right = rf.left + w;
	}

	@Override
	public void removeBlockAnimation(Block block) {
		mRemoveIndex = getChilds().indexOf(block);
		getChilds().remove(block);
		block.setParent(null);
		mRemoveBlock = block.clone();
		mAnima.start();
	}
	
	@Override
	public void backBlockAnimation(Block block) {
		addBlockAnimation(block, mRemoveIndex);
	}

	public void setChildType(Class<?>[] cls) {
		mChildType = cls;
	}

	@Override
	public void setUseCacheOnlySelf(boolean useCache) {
		super.setUseCacheOnlySelf(false);
	}
	
	@Override
	public boolean isMyChilds(Block b) {
		if (mChildType != null) {
			for (Class<?> cls : mChildType) {
				if (cls.isInstance(b)) {
					return true;
				}
			}
			return false;
		} else {
			return b instanceof BlockControl;
		}
	}

	@Override
	public RectF computeRect() {
		float padding = mPadding;
		RectF rf = getRect();
		float height = CONTROL_HEIGHT;
		float width = CONTROL_WIDTH;
		if (!isEmptyChilds()) {
			height = padding;
			width = 0;
			for (Block b : getChilds()) {
				RectF childRectF = b.computeRect();
				if (b.isPayingAnimation()) {
					float current = childRectF.height() * b.getAnimationTimes();
					height += current;
				} else {
					height += childRectF.height();
				}
				width = childRectF.width() > width ? childRectF.width() : width;
				height += padding;
			}
		}
		if (mRemoveBlock != null && mAnima.isPaying()) {
			float h = mRemoveBlock.getHeight() * (1 - mAnima.getTimes());
			if (isEmptyChilds()) {
				height = h;
			} else {
				height += h;
			}
		}
		height = Math.max(height, CONTROL_HEIGHT);
		rf.bottom = rf.top + height;
		rf.right = rf.left + width;
		return rf;
	}

	@Override
	public void layout(float left, float top) {
		float padding = mPadding;
		top = top + padding;

		if (mRemoveBlock != null) {
			getChilds().add(mRemoveIndex, mRemoveBlock);
		}

		for (Block b : getChilds()) {
			if (b == mRemoveBlock) {
				b.offsetTo(left, top);
				top = top + b.getHeight() * (1 - mAnima.getTimes()) + padding;
			} else if (b.isPayingAnimation()) {
				RectF rf = b.getAnimationLayoutRect();
				b.offsetTo(rf.left, rf.top);
				top = top + b.getHeight() * b.getAnimationTimes() + padding;
			} else {
				b.offsetTo(left, top);
				top = top + b.getHeight() + padding;
			}
		}
		if (mRemoveBlock != null) {
			getChilds().remove(mRemoveBlock);
		}
		if (!mAnima.isPaying()) {
			mRemoveBlock = null;
		}
	}

	@Override
	public boolean onBlockDragMove(Block b, float x, float y, Path out) {
		if (!contains(x, y)) {
			return false;
		}
		
		for (Block child : getChilds()) {
			if (child.contains(x, y)) {
				return child.onBlockDragMove(b, x, y, out);
			}
		}
		return super.onBlockDragMove(b, x, y, out);
	}

	@Override
	public boolean onBlockDragUp(Block b, float x, float y) {
		for (Block child : getChilds()) {
			if (child.contains(x, y)) {
				boolean ok = child.onBlockDragUp(b, x, y);
				if (ok) {
					return true;
				}
			}
		}
		if (isMyChilds(b)) {
			addBlockAnimation(b);
			return true;
		} else {
			return super.onBlockDragUp(b, x, y);
		}
	}

	@Override
	protected void draw(Canvas canvas) {
//		LogUtils.d("linear block draw");
		if (BuildConfig.DEBUG) {
			// getPaint().setStyle(Style.STROKE);
			// getPaint().setColor(Color.WHITE);
			// canvas.drawRect(getRect(), getPaint());
		}
	}

	@Override
	public boolean contains(float x, float y) {
		return super.contains(x, y);
	}

}
