package com.k1.graphcode.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.View;
import android.view.View.OnClickListener;

import com.k1.graphcode.utils.Density;

/**
 * 
 * @author BinGe title左边的菜单返回按钮
 */
public class OptionButton extends View implements OnClickListener {

	private boolean mShowBack;
	private boolean mShowMenu;
	private OnClickListener mMenuClickListener;
	private OnClickListener mBackClickListener;

	private Paint mPaint;

	private Bitmap mLineBitmap;
	private Matrix mMatrix;

	private Anim mCenterAnim;
	private Anim mTopAnim;
	private Anim mBottomAnim;

	private boolean isBack = true;
	private boolean mHasBack = false;
	private boolean mHasMenu = false;

	public OptionButton(Context context, boolean back, boolean menu) {
		super(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(Density.dip2px(1));
		mShowBack = back;
		mShowMenu = menu;
		if (mShowBack && mShowMenu) {
			mCenterAnim = new Anim(180, 0, 1, 1, 0, 0, 0.5f, 0.5f);
			mTopAnim = new Anim(315, 0, 0.8f, 1f, 0, 0, 0.5f, 0.65f);
			mBottomAnim = new Anim(45, -180, 0.8f, 1f, 0, 1, 0.5f, 0.35f);
		} else if (mShowBack) {
			mCenterAnim = new Anim(180, 180, 1, 1, 0, 0, 0.5f, 0.5f);
			mTopAnim = new Anim(315, 315, 0.8f, 0.8f, 0, 0, 0.5f, 0.5f);
			mBottomAnim = new Anim(45, 45, 0.8f, 0.8f, 0, 0, 0.5f, 0.5f);
		} else if (mShowMenu) {
			mCenterAnim = new Anim(0, 0, 1, 1, 0, 0, 0.5f, 0.5f);
			mTopAnim = new Anim(0, 0, 1f, 1f, 0, 0, 0.65f, 0.65f);
			mBottomAnim = new Anim(-180, -180, 1f, 1f, 1, 1, 0.35f, 0.35f);
		}

		setOnClickListener(this);
	}

	public void setOption(boolean back, boolean menu) {
		mHasBack = back;
		mHasMenu = menu;
	}

	public void setDefault(boolean back, boolean menu) {
		if (back) {
			isBack = true;
			mCenterAnim.initBackData();
			mTopAnim.initBackData();
			mBottomAnim.initBackData();
		} else {
			isBack = false;
			mCenterAnim.initMenuData();
			mTopAnim.initMenuData();
			mBottomAnim.initMenuData();
		}
	}

	@Override
	public void onClick(View v) {
		if (mHasBack && !mHasMenu) {
			if (mBackClickListener != null) {
				mBackClickListener.onClick(v);
			}
			return;
		} else if (!mHasBack && mHasMenu) {
			if (mMenuClickListener != null) {
				mMenuClickListener.onClick(v);
			}
			return;
		}

		if (isBack) {
			gotoMenu();
			if (mBackClickListener != null) {
				mBackClickListener.onClick(v);
			}
		} else {
			gotoBack();
			if (mMenuClickListener != null) {
				mMenuClickListener.onClick(v);
			}
		}
	}

	public void setMenuClickListener(OnClickListener l) {
		mMenuClickListener = l;
	}

	public void setBackClickListener(OnClickListener l) {
		mBackClickListener = l;
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

	PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0,
			Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(pfd);
		int width = getWidth();
		int height = getHeight();
		int centerX = width / 2;
		int centerY = height / 2;
		int lineWidth = (int) (height * 0.05f);
		int lineLength = (int) (width * 0.36f);
		if (mLineBitmap == null) {
			mLineBitmap = Bitmap.createBitmap((int) lineLength,
					(int) lineWidth, Config.ARGB_8888);
			float r = lineLength * 0.06f;
			Canvas c = new Canvas(mLineBitmap);
			c.setDrawFilter(pfd);
			RectF rf = new RectF(0, 0, lineLength, lineWidth);
			c.drawRoundRect(rf, r, r, mPaint);
		}
		if (mMatrix == null) {
			mMatrix = new Matrix();
		}
		float startX = centerX - lineLength / 2;
		float startY = centerY - lineWidth * 0.55f;
		mMatrix.reset();
		mMatrix.setTranslate(startX, startY);
		mMatrix.postRotate(mCenterAnim.currentAngle(), centerX, centerY);
		canvas.drawBitmap(mLineBitmap, mMatrix, mPaint);

		startX = centerX - lineLength / 2
				+ (mBottomAnim.currentTransX() * lineLength);
		startY = height * mBottomAnim.currentTransY() - lineWidth / 2;
		mMatrix.reset();
		mMatrix.setTranslate(startX, startY);
		mMatrix.postRotate(mBottomAnim.currentAngle(), startX, startY
				+ lineWidth / 2);
		mMatrix.postScale(mBottomAnim.currentScale(),
				mBottomAnim.currentScale(), startX, startY + lineWidth / 2);
		canvas.drawBitmap(mLineBitmap, mMatrix, mPaint);

		startX = centerX - lineLength / 2;
		startY = height * mTopAnim.currentTransY() - lineWidth / 2;
		mMatrix.reset();
		mMatrix.setTranslate(startX, startY);
		mMatrix.postRotate(mTopAnim.currentAngle(), startX, startY + lineWidth
				/ 2);
		mMatrix.postScale(mTopAnim.currentScale(), mTopAnim.currentScale(),
				startX, startY + lineWidth / 2);
		canvas.drawBitmap(mLineBitmap, mMatrix, mPaint);

		if (mStartTime > 0) {
			playAnimation();
		}

		canvas.drawLine(width, height * 0.25f, width, height * 0.75f, mPaint);
	}

	private long mStartTime;

	public void gotoMenu() {
		isBack = false;
		mStartTime = System.currentTimeMillis();
		mTopAnim.setGotoMenuData();
		mBottomAnim.setGotoMenuData();
		mCenterAnim.setGotoMenuData();
		playAnimation();
	}

	public void gotoBack() {
		isBack = true;
		mStartTime = System.currentTimeMillis();
		mTopAnim.setGotoBackData();
		mBottomAnim.setGotoBackData();
		mCenterAnim.setGotoBackData();
		playAnimation();
	}

	private void playAnimation() {
		float t = (System.currentTimeMillis() - mStartTime) / 400f;
		if (t <= 1) {
			compute(t);
		} else {
			compute(1);
			mStartTime = 0;
		}
	}

	private void compute(float t) {
		mTopAnim.compute(t);
		mBottomAnim.compute(t);
		mCenterAnim.compute(t);
		invalidate();
	}

	class Anim {

		private float BACK_ANGLE;
		private float MENU_ANGLE;
		private float BACK_SCALE;
		private float MENU_SCALE;
		private float BACK_TRANX;
		private float MENU_TRANX;
		private float BACK_TRANY;
		private float MENU_TRANY;

		private float mStartAngle;
		private float mEndAngle;
		private float mCurrentAngle;

		private float mStartScale;
		private float mEndScale;
		private float mCurrentScale;

		private float mStartTransX;
		private float mEndTransX;
		private float mCurrentTranX;

		private float mStartTransY;
		private float mEndTransY;
		private float mCurrentTranY;

		public Anim(float backAngle, float menuAngle, float backScale,
				float menuScale, float backTranX, float menuTranX,
				float backTranY, float menuTranY) {
			BACK_ANGLE = backAngle;
			MENU_ANGLE = menuAngle;
			BACK_SCALE = backScale;
			MENU_SCALE = menuScale;
			BACK_TRANX = backTranX;
			MENU_TRANX = menuTranX;
			BACK_TRANY = backTranY;
			MENU_TRANY = menuTranY;

			initBackData();
		}

		public void initBackData() {
			mCurrentAngle = BACK_ANGLE;
			mCurrentScale = BACK_SCALE;
			mCurrentTranX = BACK_TRANX;
			mCurrentTranY = BACK_TRANY;
		}

		public void initMenuData() {
			mCurrentAngle = MENU_ANGLE;
			mCurrentScale = MENU_SCALE;
			mCurrentTranX = MENU_TRANX;
			mCurrentTranY = MENU_TRANY;
		}

		public void setGotoMenuData() {
			mStartAngle = mCurrentAngle;
			mEndAngle = MENU_ANGLE;

			mStartScale = mCurrentScale;
			mEndScale = MENU_SCALE;

			mStartTransX = mCurrentTranX;
			mEndTransX = MENU_TRANX;

			mStartTransY = mCurrentTranY;
			mEndTransY = MENU_TRANY;
		}

		public void setGotoBackData() {
			mStartAngle = mCurrentAngle;
			mEndAngle = BACK_ANGLE;

			mStartScale = mCurrentScale;
			mEndScale = BACK_SCALE;

			mStartTransX = mCurrentTranX;
			mEndTransX = BACK_TRANX;

			mStartTransY = mCurrentTranY;
			mEndTransY = BACK_TRANY;
		}

		public void compute(float t) {
			mCurrentAngle = mStartAngle + (mEndAngle - mStartAngle) * t;
			mCurrentScale = mStartScale + (mEndScale - mStartScale) * t;
			mCurrentTranX = mStartTransX + (mEndTransX - mStartTransX) * t;
			mCurrentTranY = mStartTransY + (mEndTransY - mStartTransY) * t;
		}

		public float currentAngle() {
			return mCurrentAngle;
		}

		public float currentScale() {
			return mCurrentScale;
		}

		public float currentTransX() {
			return mCurrentTranX;
		}

		public float currentTransY() {
			return mCurrentTranY;
		}

	}

}
