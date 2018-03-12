package com.k1.graphcode.ui.views.edit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.text.InputType;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.k1.graphcode.R;
import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockInput;
import com.k1.graphcode.block.IRoot;
import com.k1.graphcode.block.LinearBlock;
import com.k1.graphcode.block.control.BlockControl;
import com.k1.graphcode.block.control.BlockMain;
import com.k1.graphcode.block.control.BlockSubProgram;
import com.k1.graphcode.block.control.BlockTrigger;
import com.k1.graphcode.block.output.BlockMotor;
import com.k1.graphcode.block.output.BlockSetColor;
import com.k1.graphcode.block.output.BlockSetLED;
import com.k1.graphcode.block.project.Project;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.constant.Const.Colors;
import com.k1.graphcode.ui.activity.BaseActivity;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.utils.AnimationTimes;
import com.k1.graphcode.utils.Density;
import com.k1.graphcode.utils.GravityInterpolator;
import com.k1.graphcode.utils.ImageUtils;
import com.k1.graphcode.utils.LogUtils;

/**
 * 
 * @author BinGe 图形编辑类，所有的图形编辑都在这个类里面
 */
public class BlockEditor implements OnGestureListener {

	private final static int SCROLL_NULL = 0;
	private final static int SCROLL_DRAG = 1;
	private final static int SCROLL_XY = 2;

	private VelocityTracker mVelocityTracker;
	private GravityInterpolator mGravity;
	private RectF mRectF = new RectF();

	private int mPadding = 20;
	private int mWidth;
	private int mHeight;
	private float mStartX, mStartY;
	private int mScrollType = 0;

	private LinearBlock mRoot;
	private Block mDragBlock;
	private Block mDragInner;
	private Block mParent;
	private View mView;
	private boolean mTouchable = true;

	private Path mLightPath = new Path();
	private Paint mLightPaint = new Paint();

	private Bitmap mRecycleClose, mRecycleOpen, mRecycle;
	private RectF mRecycleRect;
	private AnimationTimes mAnimationTimes;
	private float mRecycleX, mRecycleY, mCurrentX, mCurrentY;
	private Block mRecycleBlock;
	private DecelerateInterpolator mInterpolator = new DecelerateInterpolator(3);

	private int mEditType = Const.EditType.MAIN;

	private BlockInput mBlockInput;

	private GestureDetectorCompat mDetector;

	public BlockEditor(View view) {
		mView = view;
		mDetector = new GestureDetectorCompat(view.getContext(), this);
		mPadding = Density.dip2px(20);

		mGravity = new GravityInterpolator();
		mGravity.setGravity(Density.screenHeight() / 150 / 1000f);

		mRoot = new LinearBlock(Density.dip2px(30));
		mRoot.setChildType(new Class<?>[] { IRoot.class });

		mRecycleClose = ImageUtils.getInstence().getImage(R.mipmap.recycle0);
		mRecycleOpen = ImageUtils.getInstence().getImage(R.mipmap.recycle);
		mRecycle = mRecycleClose;
		mRecycleRect = new RectF();
		mAnimationTimes = new AnimationTimes();
		mAnimationTimes.setDuration(500);

		mLightPaint.setColor(Color.RED & 0x88ffffff);
		mLightPaint.setStyle(Style.STROKE);
		mLightPaint.setStrokeWidth(Density.dip2px(2));
	}

	public boolean isEmpty() {
		return mRectF == null || mRectF.isEmpty();
	}

	// 设置当前编辑的区域
	public void setRect(Rect rect) {

		mWidth = mView.getWidth();
		mHeight = mView.getHeight();

		mRectF.set(rect);
		mRoot.offsetTo(mPadding, mPadding);

		int w = Density.dip2px(70);
		mRecycleRect.set(0, 0, w, w);
		mRecycleRect.offsetTo(mWidth - w, mHeight - w);

		mRoot.requestComputeRect();
		mRoot.requestLayout();
		mRoot.setUseCacheIncludeChilds(false);
		mView.invalidate();
	}

	private String name = null;

	// 设置当前编辑的类型，主程序，还是子程序，还是trigger
	public void setEditType(int type, String blockName) {
		name = blockName;
		Project project = ProjectCache.getInstence().getCurrentProject();
		Block block = project.getBlock(blockName);
		mRoot.removeAll();
		mEditType = type;
		if (block != null) {
			mRoot.addBlock(block);
			return;
		}
		switch (mEditType) {
		case Const.EditType.MAIN:
			block = new BlockMain();
			break;
		case Const.EditType.SUB:
			block = new BlockSubProgram();
			break;
		case Const.EditType.TRIGGER:
			block = new BlockTrigger();
			break;
		}
		if (block != null) {
			block.setBackground(Colors.BlockDefault.CONTROL);
			block.setName(blockName);
			mRoot.addBlock(block);
		}

	}

	// 保存
	public void saveBlock() {
		Project projece = ProjectCache.getInstence().getCurrentProject();
		Block block = mRoot.getChilds().get(0);
		block.setParent(null);
		mRoot.removeAll();
		projece.saveBlock(mEditType, block);

		BaseActivity a = (BaseActivity) mView.getContext();
		a.finishWithAnimationHorizontalOut();
	}

	// 放大
	public void zoomIn() {
		Project projece = ProjectCache.getInstence().getCurrentProject();
		Block block = mRoot.getChilds().get(0);
		projece.saveBlock(mEditType, block);

		ProjectCache.getInstence().getCurrentProject().zoomIn();
		setEditType(mEditType, name);
		mView.invalidate();
	}

	// 缩小
	public void zoomOut() {
		Project projece = ProjectCache.getInstence().getCurrentProject();
		Block block = mRoot.getChilds().get(0);
		projece.saveBlock(mEditType, block);

		ProjectCache.getInstence().getCurrentProject().zoomOut();
		setEditType(mEditType, name);
		mView.invalidate();

	}

	public void zoomReset() {
		// mScaleTo = 100;
		mView.invalidate();
	}

	// 拖动block时会调用此方法
	public void dragBlockMove(Block b, float x, float y) {
		// mScaleTo = 100;
		boolean bool = false;
		bool = mRoot.onBlockDragMove(b, b.dragX(), b.dragY(), mLightPath);
		if (!bool) {
			mLightPath.reset();
			if (mParent == null) {
				mRecycle = mRecycleOpen;
			} else if (mRecycleRect.contains(x, y)) {
				mRecycle = mRecycleOpen;
			} else {
				mRecycle = mRecycleClose;
			}
		} else {
			mRecycle = mRecycleClose;
		}
	}

	public void dragBlockUp(Block b, float x, float y) {
		mLightPath.reset();
		if (mRecycleRect.contains(x, y)) {
			delete(b);
			mRecycle = mRecycleOpen;
			return;
		}
		mDragBlock = b;
		boolean drag = false;
		drag = mRoot.onBlockDragUp(b, b.dragX(), b.dragY());
		if (!drag) {
			if (mParent != null) {
				mParent.backBlockAnimation(b);
				return;
			}
			mRecycle = mRecycleOpen;
			delete(b);
		}
	}

	public void delete(Block b) {
		b.setUseCacheIncludeChilds(true);
		mRecycleBlock = b;
		mCurrentX = 0;
		mCurrentY = 0;
		mRecycleX = mRecycleRect.centerX() - b.getRect().centerX();
		mRecycleY = mRecycleRect.centerY() - b.getRect().centerY();
		mAnimationTimes.start();
	}

	// 用户的触摸事件
	public void touch(MotionEvent event) {
		mGravity.endAnimation();
		if (!mTouchable) {
			return;
		}
		if (mScrollType != SCROLL_DRAG) {
			mDetector.onTouchEvent(event);
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			} else {
				mVelocityTracker.clear();
			}
			mVelocityTracker.addMovement(event);

			mStartX = event.getX();
			mStartY = event.getY();

			mScrollType = SCROLL_NULL;
			Block touchBlock = getTouchBlock(mRoot, mStartX, mStartY);
			if (touchBlock != null) {
				mBlockInput = touchBlock.getBlocInput(mStartX, mStartY);
			}

			break;
		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.addMovement(event);
			mVelocityTracker.computeCurrentVelocity(1000);

			if (mScrollType == SCROLL_NULL) {
				float lenX = event.getX() - mStartX;
				float lenY = event.getY() - mStartY;

				touchBlock = getTouchBlock(mRoot, mStartX, mStartY);
				if (touchBlock == null) {
					mScrollType = SCROLL_XY;
					mStartX = event.getX();
					mStartY = event.getY();
					break;
				} else {
					if (touchBlock instanceof LinearBlock) {
						mScrollType = SCROLL_XY;
						mStartX = event.getX();
						mStartY = event.getY();
						break;
					} else if (touchBlock instanceof BlockControl) {

						if (touchBlock instanceof BlockMotor
								|| touchBlock instanceof BlockSetColor
								|| touchBlock instanceof BlockSetLED) {

						} else {
							BlockControl bc = (BlockControl) touchBlock;
							if (!bc.controlContains(mStartX, mStartY)) {
								mScrollType = SCROLL_XY;
								mStartX = event.getX();
								mStartY = event.getY();
								break;
							}
						}

					}
				}

				if (Math.abs(lenX) < Const.TOUCH_SLOP
						&& Math.abs(lenY) < Const.TOUCH_SLOP) {
					break;
				}
				mBlockInput = null;
				float speedX = Math.abs(mVelocityTracker.getXVelocity());
				float speedY = Math.abs(mVelocityTracker.getYVelocity());
				if (speedX > Const.TOUCH_SLOP_VELOCITY
						|| speedY > Const.TOUCH_SLOP_VELOCITY) {
					mScrollType = SCROLL_XY;
					mStartX = event.getX();
					mStartY = event.getY();
				} else {
					// mScrollType = SCROLL_DRAG;
					mScrollType = SCROLL_XY;
					mStartX = event.getX();
					mStartY = event.getY();
				}
				break;
			}
			if (mScrollType == SCROLL_DRAG) {
				if (mDragInner == null) {
					mDragInner = getTouchBlock(mRoot, mStartX, mStartY);
					mDragInner.setUseCacheIncludeChilds(true);
					if (mDragInner == mRoot || mDragInner instanceof BlockMain) {
						mDragInner = null;
						break;
					}
					if (mDragInner != null) {
						Block parent = mDragInner.getParent();
						if (parent != null) {
							mParent = parent;
							parent.removeBlockAnimation(mDragInner);
						}
					}
				}
				float x = event.getX() - mStartX;
				float y = event.getY() - mStartY;
				if (mDragInner != null) {
					mDragInner.offset(x, y);
					dragBlockMove(mDragInner, event.getX(), event.getY());
				}
				mStartX = event.getX();
				mStartY = event.getY();
			} else if (mScrollType == SCROLL_XY) {
				float x = event.getX() - mStartX;
				float y = event.getY() - mStartY;

				scrollRoot(x, y);

				mStartX = event.getX();
				mStartY = event.getY();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:

			if (mDragInner != null) {
				dragBlockUp(mDragInner, event.getX(), event.getY());
				mDragInner = null;
			} else {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				if (mScrollType == SCROLL_XY) {
					mStartY = 0;
					velocityOnUp(mVelocityTracker.getXVelocity(),
							mVelocityTracker.getYVelocity());
				}

			}

			if (mScrollType == SCROLL_NULL && mBlockInput != null) {
				touchBlock = getTouchBlock(mRoot, event.getX(), event.getY());
				if (touchBlock != null) {
					BlockInput bi = touchBlock.getBlocInput(event.getX(),
							event.getY());
					if (mBlockInput == bi) {
						blockInputClick(bi);
					}
				}
			}
			mScrollType = SCROLL_NULL;
			mBlockInput = null;
			mParent = null;
		}
	}

	private void blockInputClick(final BlockInput bi) {
		ScriptView ev = (ScriptView) mView.getParent();
		if (bi.getType() == BlockInput.TYPE_INPUT_VALUE) {
			ev.showEditDialog(bi.getDialogTitle(), InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL,
					new CustomDialog.ReturnResults() {
						public void result(Object o) {
							bi.setValue(o != null ? o.toString() : null);
							mView.invalidate();
						}
					});
		} else {
			ev.showSelectorDialog(bi.getDialogTitle(), bi.getAdapter(),
					bi.getValue(), new CustomDialog.ReturnResults() {
						public void result(Object o) {
							bi.setValue(o != null ? o.toString() : null);
							mView.invalidate();
						}
					});
		}
	}

	public void velocityOnUp(float velocityX, float velocityY) {
		mGravity.compute(velocityY);
		mGravity.start();
		mStartY = 0;
		mView.invalidate();
	}

	private void scroll(float t) {
		t = mGravity.getInterpolation(t);
		float current = mGravity.len * t;
		float y = current - mStartY;
		scrollRoot(0, y);
		mStartY = current;
	}

	public void scrollRoot(float x, float y) {
		float top = mRoot.getTop() + y;
		float left = mRoot.getLeft() + x;
		if (top + mRoot.getHeight() < mHeight - mPadding) {
			top = mHeight - mPadding - mRoot.getHeight();
		}
		if (left + mRoot.getWidth() < mWidth - mPadding) {
			left = mWidth - mPadding - mRoot.getWidth();
		}
		if (top > mPadding) {
			top = mPadding;
		}
		if (left > mPadding) {
			left = mPadding;
		}
		mRoot.offsetTo(left, top);
	}

	// 获取当前用户触摸到的block
	public Block getTouchBlock(Block b, float x, float y) {
		if (b.contains(x, y)) {
			if (!b.isEmptyChilds()) {
				for (Block child : b.getChilds()) {
					if (child.contains(x, y)) {
						Block block = getTouchBlock(child, x, y);
						if (block != null) {
							if (block instanceof LinearBlock) {
								if (block.isEmptyChilds()) {
									return block.getParent();
								}
							}
							return block;
						}
					}
				}
			}
			return b;
		} else {
			return null;
		}
	}

	float lastHeight = 0;

	// 绘制方法，从view传过来
	public void draw(Canvas canvas) {

		if (mGravity.isPlaying()) {
			float t = mGravity.getCurrent();
			if (t == 1) {
				mGravity.endAnimation();
			}
			scroll(t);
			mView.invalidate();
		}

		mRoot.setUseCacheIncludeChilds(true);
		if (mParent != null) {
			mParent.requestComputeRect();
			mParent.requestLayout();
			mView.invalidate();
			useCache(mParent);
		} else if (mDragBlock != null) {
			useCache(mDragBlock);
		}
		LogUtils.d("root : " + mRectF);
		mRoot.dispatchDraw(canvas, mRectF);
		if (mDragBlock != null && mDragBlock.isPayingAnimation()) {
			mDragBlock.computeAnimation();
			mView.invalidate();
		} else {
			mDragBlock = null;
		}

		if (mDragInner != null) {
			mDragInner.dispatchDraw(canvas, mRectF);
		}

		int padding = Density.dip2px(12);
		mRecycleRect.inset(padding, padding);
		canvas.drawBitmap(mRecycle, null, mRecycleRect, null);
		mRecycleRect.inset(-padding, -padding);

		if (mAnimationTimes.isPaying()) {
			float t = mAnimationTimes.getCurrent();
			t = mInterpolator.getInterpolation(t);
			float x = mRecycleX * t - mCurrentX;
			float y = mRecycleY * t - mCurrentY;
			mRecycleBlock.offset(x, y);
			canvas.save();
			canvas.scale(1 - t, 1 - t, mRecycleBlock.getRect().centerX(),
					mRecycleBlock.getRect().centerY());
			mRecycleBlock.dispatchDraw(canvas, mRectF);
			canvas.restore();
			mView.invalidate();
			mCurrentX = mRecycleX * t;
			mCurrentY = mRecycleY * t;
			if (t > 0.8f) {
				mRecycle = mRecycleClose;
			}
		} else {
			if (mRecycleBlock != null) {
				mRecycleBlock.recycleIncludeChild();
			}
			mRecycleBlock = null;
		}

		if (mBlockInput != null) {
			int color = mLightPaint.getColor();
			mLightPaint.setColor(0x88ffffff);
			mLightPaint.setStyle(Style.FILL);
			canvas.drawRoundRect(mBlockInput.getRect(), mRoot.BLOCK_ROUND,
					mRoot.BLOCK_ROUND, mLightPaint);
			mLightPaint.setStyle(Style.STROKE);
			mLightPaint.setColor(color);
		}

	}

	public void useCache(Block block) {
		block.setUseCacheOnlySelf(false);
		Block parent = block.getParent();
		if (parent != null) {
			useCache(parent);
		}
	}

	public void drawLightPath(Canvas canvas) {
		if (!mLightPath.isEmpty()) {
			canvas.drawPath(mLightPath, mLightPaint);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	// 长按事件，判断是否拖动时使用
	@Override
	public void onLongPress(MotionEvent e) {
		mScrollType = SCROLL_DRAG;
		if (mDragInner == null) {
			mDragInner = getTouchBlock(mRoot, mStartX, mStartY);
			mDragInner.setUseCacheIncludeChilds(true);
			if (mDragInner == mRoot || mDragInner instanceof BlockMain) {
				mDragInner = null;
			}
			if (mDragInner != null) {
				Block parent = mDragInner.getParent();
				if (parent != null) {
					mParent = parent;
					parent.removeBlockAnimation(mDragInner);
				}
			}
		}
		float x = e.getX() - mStartX;
		float y = e.getY() - mStartY;
		if (mDragInner != null) {
			mDragInner.offset(x, y);
			dragBlockMove(mDragInner, e.getX(), e.getY());
		}
		mStartX = e.getX();
		mStartY = e.getY();
		mView.invalidate();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

}
