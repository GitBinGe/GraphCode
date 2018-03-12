package com.k1.graphcode.ui.views.edit;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.BlockType;
import com.k1.graphcode.block.LinearBlock;
import com.k1.graphcode.block.project.Project;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.block.sub.BlockCall;
import com.k1.graphcode.block.sub.BlockSublock;
import com.k1.graphcode.block.variable.BlockVar;
import com.k1.graphcode.block.variable.BlockVarControl;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;
import com.k1.graphcode.utils.Density;
import com.k1.graphcode.utils.GravityInterpolator;
import com.k1.graphcode.utils.LogUtils;

/**
 * 
 * @author BinGe
 *	侧边栏,block选择列表
 */
public class SideBar implements OnGestureListener {

	private final static int ACTION_DOWN_X = 101;
	private final static int ACTION_DOWN_Y = 102;

	private int mTouchAction = -1;

	private static int SIDEBAR_PADDING = 25;

	private RectF mLineRect = new RectF();
	private RectF mBlockTypeRect = new RectF();

	private RectF mRect = new RectF();
	private Paint mPaint = new Paint();

	private RectF mTypeRect;
	private LinearBlock mLinearBlock;

	private View mView;
	private AnimHandler mHandler;
	private GestureDetectorCompat mDetector;

	private float startX, startY;
	private RectF mTempRect = new RectF();
	private RectF mCurrent;

	private boolean mTouchable = false;

	private float[] mTouchDownPoint = new float[2];
	private DragListener mDragListener;

	private RectF mButtonRect = new RectF();

	private LinearBlock mVariableLayout;

	private Bitmap mTypeBitmap;
	private float mTouchStartY;
	private boolean mTypeMoveable = false;

	private int mAddable = 0;

	public SideBar(View view) {
		mView = view;
		mDragListener = (DragListener) view;
		mHandler = new AnimHandler();
		mTypeRect = new RectF();

		mLinearBlock = new LinearBlock();
		for (int i = 0; i < Const.BLOCK_TYPE.length; i++) {
			mLinearBlock.addBlock(Const.BLOCK_TYPE[i].getBlock());
			if (Const.BLOCK_TYPE[i].getName().equals("Variable")) {
				mVariableLayout = (LinearBlock) Const.BLOCK_TYPE[i].getBlock();
				mVariableLayout.removeAll();
			}
		}

		Project project = ProjectCache.getInstence().getCurrentProject();
		for (Block varBlocks : project.getVariableScript()) {
			String varName = varBlocks.getName();
			addVarBlock(varName);
		}

		LinearBlock subLayout = (LinearBlock) Const.BLOCK_TYPE[Const.BLOCK_TYPE.length - 1]
				.getBlock();
		subLayout.removeAll();
		BlockCall bc = new BlockCall();
		subLayout.addBlock(bc);
		for (Block subBlocks : project.getSubScript()) {
			BlockSublock sub = new BlockSublock();
			sub.setName(subBlocks.getName());
			subLayout.addBlock(sub);
		}

		mDetector = new GestureDetectorCompat(view.getContext(), this);
		SIDEBAR_PADDING = Density.dip2px(25);

		mGravity.setGravity(Density.screenHeight() / 150 / 1000f);
	}

	public void setEditType(int type) {
		// mEditType = type;
	}

	public boolean isEmpty() {
		return mRect == null || mRect.isEmpty();
	}

//	设置侧边栏的绘制区
	public void setRect(Rect r) {

		mRect.set(r);

		int typeWidth = Density.dip2px(40);

		// 类型选择条
		mTypeRect.set(r.left, r.top, typeWidth, r.bottom);

		int height = Density.dip2px(100);
		mBlockTypeRect.set(mTypeRect.left, mTypeRect.top, mTypeRect.left
				+ typeWidth, height);
		int line = Density.dip2px(2);
		mLineRect.set(mTypeRect.left, mTypeRect.top,
				mTypeRect.left + typeWidth, mTypeRect.top + line);
		int count = Const.BLOCK_TYPE.length;
		mTypeRect.bottom = mTypeRect.top + mBlockTypeRect.height() * count
				+ mLineRect.height() * (count - 1) * 0;

		// 控件块选择条
		int padding = Density.dip2px(15);
		mLinearBlock.offsetTo(mTypeRect.right + padding, 0);

		int buttomHeight = Density.dip2px(50);
		mRect.bottom -= buttomHeight;
		mButtonRect.set(mRect.left, mRect.bottom, mRect.right, r.bottom);

		scrollBlockX(-mRect.width());

		mLinearBlock.requestComputeRect();
		mLinearBlock.requestLayout();
		mView.invalidate();
	}

	public boolean contains(float x, float y) {
		boolean isContains = mRect.contains(x, y);
		return isContains;
	}

	public float getRight() {
		return mRect.right;
	}

	//绘制侧边栏
	public void draw(Canvas canvas) {

		LogUtils.d("rect : "+ mRect);

		int color = (int) (0x55 * mRect.right / mRect.width()) << 24;
		canvas.drawColor(color);
		mPaint.setColor(Const.Colors.COLOR_BACKGROUND);
		canvas.drawRect(mRect, mPaint);

		canvas.save();
		canvas.clipRect(mRect);

		drawType(canvas);

		mLinearBlock.dispatchDraw(canvas, mRect);

		RectF textRect = new RectF(mButtonRect);

		if (mAddable == 1) {
			mPaint.setColor(0x33000000);
			canvas.drawRect(textRect, mPaint);
		}
		canvas.restore();

		mPaint.setColor(Const.Colors.COLOR_ADD_BACKGROUND);
		canvas.drawRect(mButtonRect, mPaint);
		mPaint.setColor(0xff9e9e9e);
		String text = "+ ADD VAR";
		mPaint.setTextSize(textRect.height() / 4);
		FontMetrics fm = mPaint.getFontMetrics();
		canvas.drawText(text,
				textRect.centerX() - mPaint.measureText(text) / 2,
				textRect.centerY() + fm.descent, mPaint);

	}

	private void drawType(Canvas canvas) {

		if (mTypeBitmap != null) {
			canvas.drawBitmap(mTypeBitmap, mTypeRect.left, mTypeRect.top, null);
			return;
		}

		mTypeBitmap = Bitmap.createBitmap((int) mTypeRect.width(),
				(int) mTypeRect.height(), Config.ARGB_4444);
		Canvas c = new Canvas(mTypeBitmap);

		float oldTop = mTypeRect.top;
		float oldLeft = mTypeRect.left;
		mTypeRect.offsetTo(0, 0);
		for (int i = 0; i < Const.BLOCK_TYPE.length; i++) {
			float top = mTypeRect.top + mBlockTypeRect.height() * i;
			if (i > 0) {
				top += mLineRect.height();
			}
			mBlockTypeRect.offsetTo(mTypeRect.left, top);
			BlockType bt = Const.BLOCK_TYPE[i];
			bt.draw(c, mBlockTypeRect);
			mLineRect.offsetTo(mTypeRect.left, mBlockTypeRect.bottom);
			mPaint.setColor(Const.Colors.COLOR_LINE);
			c.drawRect(mLineRect, mPaint);
		}
		mTypeRect.offsetTo(oldLeft, oldTop);
		drawType(canvas);
	}

	private void addVarBlock(String name) {
		BlockVar var = new BlockVar();
		var.setName(name);
		var.setUseCacheIncludeChilds(true);
		BlockVarControl control = new BlockVarControl();
		control.setVar(var);
		control.setUseCacheIncludeChilds(true);
		mVariableLayout.addBlock(var);
		mVariableLayout.addBlock(control);
	}

//	侧边栏的触摸事件
	public boolean touchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			RectF touchRect = new RectF(mRect);
			touchRect.right += SIDEBAR_PADDING;
			if (touchRect.contains(event.getX(), event.getY())) {
				mTouchable = true;
			} else {
				mTouchable = false;
			}

			if (mButtonRect.contains(event.getX(), event.getY())) {
				mAddable = 1;
			}

		}

		if (mAddable > 0 && event.getAction() == MotionEvent.ACTION_UP) {
			if (mButtonRect.contains(event.getX(), event.getY())) {
				if (mAddable == 1) {
					ScriptView scriptView = (ScriptView) mView.getParent();
					scriptView.showEditDialog("输入变量名",
							InputType.TYPE_CLASS_TEXT, new ReturnResults() {
								public void result(Object o) {
									if (o != null && mVariableLayout != null) {
										BlockVar var = new BlockVar();
										var.setName(o.toString());
										var.setUseCacheIncludeChilds(true);
										BlockVarControl control = new BlockVarControl();
										control.setVar(var);
										control.setUseCacheIncludeChilds(true);
										mVariableLayout.addBlock(var);
										mVariableLayout.addBlock(control);
										ProjectCache
												.getInstence()
												.getCurrentProject()
												.saveBlock(
														Const.EditType.VARIABLE,
														var);
										mView.invalidate();
									}
								}

							});
				}
			}
			mAddable = 0;
		}

		if (!mTouchable) {
			return false;
		}

		isTouch = true;
		mDetector.onTouchEvent(event);
		// 类型条滑动事件
		if ((mTypeRect.contains(Math.round(event.getX()),
				Math.round(event.getY())) && mCurrent != mLinearBlock.getRect())
				|| mCurrent == mTypeRect) {
			scrollType(event, mTypeRect);
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			startY = event.getY();
			mTouchDownPoint[0] = startX;
			mTouchDownPoint[1] = startY;
			return true;
		case MotionEvent.ACTION_MOVE:
			if (mTouchAction == ACTION_DOWN_X) {
				float x = event.getX() - startX;
				startX = event.getX();
				scrollBlockX(x);
				if (x > 0 && mRect.left == 0) {
					for (Block b : mLinearBlock.getChilds()) {
						if (b.contains(mTouchDownPoint[0], mTouchDownPoint[1])) {
							for (Block child : b.getChilds()) {
								if (child.contains(mTouchDownPoint[0],
										mTouchDownPoint[1])) {
									if (mDragListener != null) {
										Block clone = child.clone();
										clone.setUseCacheIncludeChilds(true);
										mDragListener.onDrag(clone);
										mVelocityX = -mRect.right;
										mStartTime = System.currentTimeMillis();
										mLast = 0;
										isTouch = false;
										payScrollX(-1);
										mTouchDownPoint[0] = 0;
										mTouchDownPoint[1] = 0;
										mTouchable = false;
										return true;
									}

								}
							}
						}
					}
				}
				return true;
			} else if (mTouchAction == ACTION_DOWN_Y) {
				float y = event.getY() - startY;
				startY = event.getY();
				scrollBlockY(y);
				return true;
			}

			float lenX = Math.abs(event.getX() - startX);
			float lenY = Math.abs(event.getY() - startY);
			if (lenX > lenY) {
				// 水平方向
				if (lenX > Const.TOUCH_SLOP) {
					mTouchAction = ACTION_DOWN_X;
					startX = event.getX();
				}
			} else {
				// 垂直方向
				if (lenY > Const.TOUCH_SLOP) {
					mTouchAction = ACTION_DOWN_Y;
					startY = event.getY();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mTouchAction == ACTION_DOWN_X) {
				float currentX = event.getX();
				float x = currentX - startX;
				startX = currentX;
				scrollBlockX(x);
				if (!isFlingX) {
					int direction = 0;
					if (mRect.right > mRect.width() / 2) {
						mVelocityX = -mRect.left;
						direction = 1;
					} else {
						mVelocityX = -mRect.right;
						direction = -1;
					}
					mStartTime = System.currentTimeMillis();
					mLast = 0;
					isTouch = false;
					payScrollX(direction);
				}
			} else if (mTouchAction == ACTION_DOWN_Y) {

				float currentY = event.getY();
				float y = currentY - startY;
				startY = currentY;
				scrollBlockY(y);

			}
			isTouch = false;
			isFlingX = false;
			mTouchAction = -1;

			break;
		}

		return true;
	}

	public void scrollType(MotionEvent event, RectF rect) {
		if (rect.height() < mRect.height()) {
			return;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			mCurrent = rect;
			mTempRect.set(rect);
			startY = event.getY();

			mTouchStartY = event.getY();

			break;
		}
		case MotionEvent.ACTION_MOVE: {

			if (!mTypeMoveable) {
				float lenY = event.getY() - mTouchStartY;
				if (Math.abs(lenY) < Const.TOUCH_SLOP) {
					mTypeMoveable = false;
				} else {
					mTypeMoveable = true;
					mCurrent = rect;
					mTempRect.set(rect);
					startY = event.getY();
				}
				break;
			}

			int len = Math.round(event.getY() - startY);
			scroll(len, rect);
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (!mTypeMoveable) {
				mTempRect.setEmpty();
				mCurrent = null;
				typeClick(event.getX(), event.getY());
				break;
			}
			mTypeMoveable = false;
			int len = Math.round(event.getY() - startY);
			scroll(len, rect);
			mTempRect.setEmpty();
			mCurrent = null;
			break;
		}
		}
	}

	private void typeClick(float x, float y) {
		for (int i = 0; i < Const.BLOCK_TYPE.length; i++) {
			float top = mTypeRect.top + mBlockTypeRect.height() * i;
			if (i > 0) {
				top += mLineRect.height();
			}
			mBlockTypeRect.offsetTo(mTypeRect.left, top);
			if (mBlockTypeRect.contains(x, y)) {
				LinearBlock lb = (LinearBlock) mLinearBlock.getChilds().get(i);
				float toY = lb.getTop();
				float len = -toY;
				mGravity.setLen(len);
				mGravity.start();
				mStartTime = System.currentTimeMillis();
				mLast = 0;
				isTouch = false;
				payScrollY();
				break;
			}
		}
	}

	private void scrollBlockY(float y) {
		float top = mLinearBlock.getTop() + y;
		float bottom = mLinearBlock.getBottom() + y;
		if (top > mRect.top) {
			mLinearBlock.offsetTo(mLinearBlock.getLeft(), mRect.top);
		} else if (bottom < mRect.bottom) {
			mLinearBlock.offsetTo(mLinearBlock.getLeft(), mRect.bottom
					- mLinearBlock.getHeight());
		} else {
			mLinearBlock.offset(0, y);
		}
	}

	private void scrollBlockX(float x) {
		if ((mRect.left + x) > 0) {
			x = -mRect.left;
		} else if (mRect.right + x < 0) {
			x = -mRect.right;
		}

		mRect.offset(x, 0);
		mLinearBlock.offset(x, 0);
		mTypeRect.offset(x, 0);
		mButtonRect.offset(x, 0);
	}

	public void scroll(int len, RectF rect) {
		float top = mTempRect.top + len;
		float bottom = mTempRect.bottom + len;
		if (top > 0) {
			rect.offsetTo(rect.left, 0);
		} else if (bottom < mRect.bottom) {
			rect.offsetTo(rect.left, mRect.bottom - mTempRect.height());
		} else {
			rect.offsetTo(rect.left, top);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		for (final Block b : mLinearBlock.getChilds()) {
			if (b.contains(x, y)) {
				for (final Block child : b.getChilds()) {
					if (child.contains(x, y) && child instanceof BlockVar) {
						String name = child.getName();
						if (!TextUtils.isEmpty(name) && name.equals("Variable")) {
							return;
						}
						CustomDialog.createEditDialog(mView.getContext(),
								"Rename", name, new ReturnResults() {
									public void result(Object o) {
										String name = o.toString();
										BlockVar var = (BlockVar) child;
										String old = var.getName();
										var.setName(name);
										int index = b.getChilds().indexOf(var);
										BlockVarControl control = (BlockVarControl) b
												.getChilds().get(index + 1);
										control.setVar(name);
										control.rebuildCcache();
										b.setUseCacheIncludeChilds(false);

										ProjectCache.getInstence()
												.getCurrentProject()
												.findTheVariableByName(old, name);

										mView.invalidate();
									}
								}).show();
						return;
					}
				}
			}
		}
	}

	private long mStartTime;
	private boolean isTouch = false;
	private float mVelocityX;
	private boolean isFlingX = false;
	private float mLast;
	private GravityInterpolator mGravity = new GravityInterpolator();
	private DecelerateInterpolator mInterpolation = new DecelerateInterpolator(
			4f);

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (mTouchAction == ACTION_DOWN_Y) {
			isTouch = false;
			mGravity.setVelocity(velocityY);
			mGravity.start();
			mStartTime = System.currentTimeMillis();
			mLast = 0;
			payScrollY();
		} else if (mTouchAction == ACTION_DOWN_X) {
			mVelocityX = velocityX;
			if (mVelocityX > 0) {
				mVelocityX = -mRect.left;
			} else {
				mVelocityX = -mRect.right;
			}
			isTouch = false;
			mStartTime = System.currentTimeMillis();
			mLast = 0;
			isFlingX = true;
			payScrollX(velocityX > 0 ? 1 : -1);
		}
		return false;
	}

	class AnimHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				payScrollY();
				break;
			case 1:
				payScrollX(0);
				break;
			}
		}
	}

	public void show() {
		mGravity.endAnimation();
		mVelocityX = -mRect.left;
		isTouch = false;
		mStartTime = System.currentTimeMillis();
		mLast = 0;
		isFlingX = true;
		payScrollX(1);
	}

	public void hide() {
		mGravity.endAnimation();
		mVelocityX = -mRect.right;
		isTouch = false;
		mStartTime = System.currentTimeMillis();
		mLast = 0;
		isFlingX = true;
		payScrollX(-1);
	}

	private void payScrollX(int direction) {
		if (isTouch) {
			return;
		}

		if (direction != 0) {
			if (direction == 1) {
				mSideListener.onSideChanged(true);
			} else {
				mSideListener.onSideChanged(false);
			}
		}

		float t = (System.currentTimeMillis() - mStartTime) / 600f;
		if (t < 1) {
			t = mInterpolation.getInterpolation(t);
			mView.invalidate();
			mHandler.sendEmptyMessage(1);
		} else {
			t = 1;
		}
		float current = mVelocityX * t;
		float x = current - mLast;
		mLast = current;
		scrollBlockX(x);
	}

	private void payScrollY() {
		if (isTouch) {
			return;
		}
		float t = (System.currentTimeMillis() - mStartTime) / mGravity.time;
		if (mGravity.isPlaying()) {
			if (t >= 1) {
				mGravity.endAnimation();
				t = 1;
			}
			t = mGravity.getInterpolation(t);
			float current = mGravity.len * t;
			float y = current - mLast;
			scrollBlockY(y);
			mLast = current;
			mView.invalidate();
			mHandler.sendEmptyMessage(0);
		}
	}

	private SideListener mSideListener;

	public void setSideListener(SideListener l) {
		mSideListener = l;
	}

	public interface SideListener {
		public void onSideChanged(boolean show);
	}

}
