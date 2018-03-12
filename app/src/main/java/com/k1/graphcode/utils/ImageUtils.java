package com.k1.graphcode.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.k1.graphcode.callback.Callback;

public class ImageUtils {

	private static ImageUtils utils;

	public static void newInstence(Context context) {
		utils = new ImageUtils(context);
	}

	public static ImageUtils getInstence() {
		return utils;
	}

	private Context mContext;
	private LruCache<String, Bitmap> mImageCache;

	private ImageUtils() {
	}

	private ImageUtils(Context context) {
		mContext = context;
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mImageCache = new LruCache<String, Bitmap>(cacheSize) {
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	public Bitmap getImage(String name, Callback cb) {
		Bitmap bitmap = mImageCache.get(name);
		if (bitmap != null) {
			return bitmap;
		}
		new Thread() {
			public void run() {

			}
		}.start();
		return null;
	}

	public Bitmap getImage(int id, Callback cb) {
		Bitmap bitmap = mImageCache.get(id + "");
		if (bitmap != null) {
			return bitmap;
		}
		new Thread() {
			public void run() {

			}
		}.start();
		return null;
	}
	
	public Bitmap getImage(int id) {
		if (id  > 0) {
			Bitmap bitmap = mImageCache.get(id + "");
			if (bitmap != null) {
				return bitmap;
			}
			return BitmapFactory.decodeResource(mContext.getResources(), id);
		}
		return null;
	}

	public void setImage(String name, Bitmap bitmap) {
		mImageCache.put(name, bitmap);
	}

}
