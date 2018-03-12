package com.k1.graphcode.block;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;

import com.k1.graphcode.utils.Density;
import com.k1.graphcode.utils.ImageUtils;
/**
 * 
 * @author BinGe
 *	侧边栏中的分类信息
 */
public class BlockType {

	private String mName;
	private int mColor;
	private List<Block> mBlocks;
	private LinearBlock mBlock;
	private Paint mPaint;
	private Class<?>[] mChilds;

	public BlockType(String name, int color, Class<?>[] childs) {
		mPaint = new Paint();
		mPaint.setTextSize(Density.sp2px(16));
		mPaint.setColor(Color.WHITE);
		mName = name;
		mColor = color;
		mChilds = childs;
	}

	public void draw(Canvas canvas, RectF r) {
		Bitmap bmp = makeBitmap(r);
		canvas.drawBitmap(bmp, null, r, mPaint);
	}

	private Bitmap makeBitmap(RectF r) {
		Bitmap bitmap = ImageUtils.getInstence().getImage(mName, null);
		if (bitmap != null) {
			return bitmap;
		}
		int width = Math.round(r.width());
		int height = Math.round(r.height());
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.rotate(-90, width / 2, height / 2);
		FontMetrics fm = mPaint.getFontMetrics();
		float baseline = (height - fm.bottom - fm.top) / 2;
		float centerX = (width - mPaint.measureText(mName)) / 2;
		mPaint.setColor(mColor);
		canvas.drawText(mName, centerX, baseline, mPaint);
		canvas.rotate(90, width / 2, height / 2);
		ImageUtils.getInstence().setImage(mName, bitmap);
		return bitmap;
	}

	public String getName() {
		return mName;
	}

	public List<Block> getBlocks() {
		if (mBlocks == null) {
			mBlocks = new ArrayList<Block>();
			for (Class<?> child : mChilds) {
				try {
					Block b = (Block) child.newInstance();
					// b.setColor(mColor);
					mBlocks.add(b);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			// BlockCompute b1 = new BlockCompute();
			// b1.setColor(Color.RED);
			// BlockAdd b2 = new BlockAdd();
			// b2.setColor(Color.YELLOW);
			// b1.setLeft(b2);
			//
			// BlockSub b3 = new BlockSub();
			// b3.setColor(Color.GREEN);
			// b2.setLeft(b3);
			// mBlocks.add(b1);
		}
		return mBlocks;
	}

	public Block getBlock() {
		if (mBlock == null) {
			mBlock = new LinearBlock(Density.dip2px(15));
			for (Class<?> child : mChilds) {
				try {
					Block b = (Block) child.newInstance();
					b.setUseCacheIncludeChilds(true);
//					b.setBackground(mColor);
					mBlock.addBlock(b);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
//			BlockControl b1 = new BlockControl();
//			b1.setBackground(Color.RED);
//			mBlock.addBlock(b1);
//
//			BlockCompute b2 = new BlockCompute();
//			b2.setBackground(Color.RED);
//			b1.setVariable(b2, 0);
			
//			BlockCompute b3 = new BlockCompute();
//			b3.setBackground(Color.GRAY);
//			b1.addVariable(b3, 1);
			
//			BlockCompute b4 = new BlockCompute();
//			b4.setBackground(Color.GREEN);
//			b3.setLeft(b4);
			
//			BlockControl b5 = new BlockControl();
//			b5.setBackground(Color.RED);
//			b1.setControl(b5, 0);
//			
//			BlockControl b6 = new BlockControl();
//			b6.setBackground(Color.RED);
//			b5.setControl(b6, 0);
		}
		return mBlock;
	}

}
