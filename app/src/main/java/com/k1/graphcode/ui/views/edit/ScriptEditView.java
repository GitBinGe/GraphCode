package com.k1.graphcode.ui.views.edit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.k1.graphcode.block.Block;
import com.k1.graphcode.ui.views.edit.SideBar.SideListener;
import com.k1.graphcode.utils.LogUtils;

public class ScriptEditView extends View implements DragListener {

    private boolean onDrag = false;
    private SideBar mSidebar;
    private BlockEditor mEditor;
    private Block mDragBlock;
    private float mStartX, mStartY;

    public ScriptEditView(Context context) {
        super(context);
        init(context);
    }

    public ScriptEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mSidebar = new SideBar(this);
        mEditor = new BlockEditor(this);
    }

    public void setEditType(int type, String blockName) {
        mEditor.setEditType(type, blockName);
    }

    public void saveBlock() {
        mEditor.saveBlock();
    }

    public void zoomIn() {
        mEditor.zoomIn();
        invalidate();
    }

    public void zoomOut() {
        mEditor.zoomOut();
        invalidate();
    }

    public void zoomReset() {
        mEditor.zoomReset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mEditor.draw(canvas);

        if (mSidebar.getRight() > 0) {
            mSidebar.draw(canvas);
        }

        if (mDragBlock != null) {
            mDragBlock.dispatchDraw(canvas, new RectF(0, 0, getWidth(),
                    getHeight()));
        }

        mEditor.drawLightPath(canvas);

    }

    public void setSideListner(SideListener l) {
        mSidebar.setSideListener(l);
    }

    public void showSide() {
        mSidebar.show();
    }

    public void hideSide() {
        mSidebar.hide();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!onDrag) {
            if (!mSidebar.touchEvent(event)) {
                mEditor.touch(event);
            }
        }
        if (mDragBlock != null) {
            if (mStartX == 0 && mStartY == 0) {
                mStartX = event.getX();
                mStartY = event.getY();
            }
            float x = event.getX() - mStartX;
            float y = event.getY() - mStartY;
            mDragBlock.offset(x, y);
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                mEditor.dragBlockMove(mDragBlock, event.getX(), event.getY());
            }
            mStartX = event.getX();
            mStartY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mStartX = 0;
            mStartY = 0;
            if (mDragBlock != null) {
                mEditor.dragBlockUp(mDragBlock, event.getX(), event.getY());
            }
            mDragBlock = null;
            onDrag = false;
        }
        invalidate();
        return true;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int height = getMeasuredHeight();
//        int width = getMeasuredWidth();
//        LogUtils.d("width1 :  " + getWidth());
//        LogUtils.d("height1 :  " + getHeight());
//        LogUtils.d("width2 :  " + getMeasuredWidth());
//        LogUtils.d("height2 :  " + getMeasuredHeight());
//        if (mSidebar.isEmpty()) {
//            mSidebar.setRect(new Rect(0, 0, width * 8 / 10, height));
//        }
//        if (mEditor.isEmpty()) {
//            mEditor.setRect(new Rect(0, 0, width, height));
//        }
//    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            if (mSidebar.isEmpty()) {
                mSidebar.setRect(new Rect(0, 0, getWidth() * 8 / 10, getHeight()));
            }
            if (mEditor.isEmpty()) {
                mEditor.setRect(new Rect(0, 0, getWidth(), getHeight()));
            }
        }
    }

    @Override
    public void onDrag(Block b) {
        onDrag = true;
        mDragBlock = b;
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

}
