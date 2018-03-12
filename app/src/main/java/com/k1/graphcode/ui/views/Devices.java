package com.k1.graphcode.ui.views;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.k1.graphcode.BuildConfig;
import com.k1.graphcode.R;
import com.k1.graphcode.connect.Controller;
import com.k1.graphcode.connect.ControllerData;
import com.k1.graphcode.connect.RequestCallback;
import com.k1.graphcode.connect.scripts.Scripts;
import com.k1.graphcode.ui.activity.MainActivity;
import com.k1.graphcode.utils.Density;
import com.k1.graphcode.utils.ImageUtils;
import com.k1.graphcode.utils.Setting;
/**
 * 
 * @author BinGe
 *	deivces页面的界面
 */
public class Devices extends FrameLayout implements OnClickListener,
		RequestCallback {

	private TextView mData;
	private TextView mControl;
	private Map<String, String> mControlInfo;
	private Rect mControlRect;
	private Paint mPaint;

	private LinearLayout mLeft;
	private LinearLayout mRight;

	private Handler mHandler;

	// private MessageView mMessageView;
	private InfoView mInfoView;
	private Controller mController;

	private String mCurrent = "-1";

	public Devices(Context context) {
		super(context);

		mHandler = new Handler();
		mController = Controller.getInstence();

		int width = Density.dip2px(100);
		int height = Density.dip2px(200);

		mControlRect = new Rect(0, 0, width, height);
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(Density.dip2px(1));

		mControl = new TextView(context);
		mControl.setGravity(Gravity.CENTER);
		mControl.setTextColor(Color.WHITE);
		mControl.setBackgroundResource(R.drawable.general_selector);
		mControl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		mControl.setText("Controller");
		mControl.setOnClickListener(this);
		FrameLayout.LayoutParams controlParams = new FrameLayout.LayoutParams(
				width, height, Gravity.CENTER);
		addView(mControl, controlParams);

		mControlInfo = new LinkedHashMap<String, String>();

		mLeft = new LinearLayout(context);
		mLeft.setOrientation(LinearLayout.VERTICAL);
		addView(mLeft);
		// ColorStateList csl = (ColorStateList)
		// getResources().getColorStateList(
		// R.color.text_selector);
		int margins = Density.dip2px(7);
		for (int i = 0; i < 4; i++) {
			ImageView iv = new ImageView(context);
			iv.setImageBitmap(ImageUtils.getInstence().getImage(R.mipmap.na));
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			iv.setTag((1 + i) + "");
			iv.setOnClickListener(this);

			FrameLayout frame = new FrameLayout(context);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					Gravity.RIGHT | Gravity.CENTER);
			frame.addView(iv, params);

			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
			p.setMargins(margins, margins, 0, margins);
			mLeft.addView(frame, p);
		}

		mRight = new LinearLayout(context);
		mRight.setOrientation(LinearLayout.VERTICAL);
		addView(mRight);
		for (int i = 0; i < 4; i++) {
			ImageView iv = new ImageView(context);
			iv.setImageBitmap(ImageUtils.getInstence().getImage(R.mipmap.na));
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			iv.setTag((5 + i) + "");
			iv.setOnClickListener(this);

			FrameLayout frame = new FrameLayout(context);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					Gravity.LEFT | Gravity.CENTER);
			frame.addView(iv, params);

			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
			p.setMargins(0, margins, margins, margins);
			mRight.addView(frame, p);
		}

		mData = new TextView(context);
		mData.setTextColor(Color.WHITE);
		addView(mData);

		mInfoView = new InfoView(context);
		mInfoView.setVisibility(View.GONE);
		addView(mInfoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		if (isNetworkNormal()) {
			mController.connect(this);
		}
	}

	public void update() {

	}

	public void connect() {
		if (!mController.isConnected()) {
			if (isNetworkNormal()) {
				mController.connect(this);
			}
		} else {
			mController.disconnect();
			updateControllerUI();
		}
		invalidate();
	}

	public boolean isNetworkNormal() {
		if (BuildConfig.DEBUG) {
			return true;
		}
		String ssid = Setting.getInstence().getSSID();
		if (ssid == null) {
			Toast.makeText(getContext(), "连接失败 error 01", Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			ConnectivityManager connec = (ConnectivityManager) getContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if ((connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState() != NetworkInfo.State.CONNECTED)) {
				Toast.makeText(getContext(), "连接失败 error 02",
						Toast.LENGTH_SHORT).show();
				return false;
			}
			WifiManager wifiManager = (WifiManager) getContext()
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String wifi = wifiInfo.getSSID();
			if (!TextUtils.isEmpty(wifi)) {
				if (wifi.startsWith("\"") && wifi.endsWith("\"")) {
					wifi = wifi.substring(1, wifi.length() - 1);
				}
				if (!wifi.equals(ssid)) {
					Toast.makeText(getContext(), "连接失败 error 03",
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void callback(ControllerData data) {
		mHandler.post(new Runnable() {
			public void run() {
				if (mController.getInfo().isNull()) {
					if (!isNetworkNormal()) {
						mController.disconnect();
					}
				}
				updateControllerUI();
			}
		});
	}

	@Override
	public void error(String e) {
	}

	public void updateControllerUI() {
		ControllerData data = mController.getInfo();

		MainActivity ma = (MainActivity) getContext();
		ma.updateConnectButton(!data.isNull());

		for (int i = 0; i < mLeft.getChildCount(); i++) {
			ImageView tv = (ImageView) mLeft.findViewWithTag("" + (1 + i));
			tv.setImageBitmap(ImageUtils.getInstence().getImage(R.mipmap.na));
			tv.setTag(R.id.tag_script, null);
		}
		for (int i = 0; i < mRight.getChildCount(); i++) {
			ImageView tv = (ImageView) mRight.findViewWithTag("" + (5 + i));
			tv.setImageBitmap(ImageUtils.getInstence().getImage(R.mipmap.na));
			tv.setTag(R.id.tag_script, null);
		}

		Map<String, Scripts> childs = data.getChilds();
		Iterator<Entry<String, Scripts>> it = childs.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,Scripts> entry = it.next();
			Scripts scripts = entry.getValue();
			String port = entry.getKey();
			scripts.setCurrentPort(port);
			if (!TextUtils.isEmpty(port)) {
				ImageView tv = (ImageView) findViewWithTag(port);
				if (tv != null) {
					tv.setTag(R.id.tag_script, scripts);
					if (Integer.valueOf(port) < 5) {
						tv.setImageBitmap(ImageUtils.getInstence().getImage(
								scripts.getIcon()));
					} else {
						tv.setImageBitmap(ImageUtils.getInstence().getImage(
								scripts.getIcon()));
					}
				}
			}
		}

		mControlInfo.clear();
		if (!data.isNull()) {
			mControlInfo.put("name", "Controller");
			mControlInfo.put("SSID", Setting.getInstence().getSSID());
			mControlInfo.put("IP", Setting.getInstence().getIP());
			if (mController.getInfo().isScriptRunning()) {
				mControlInfo.put("RunningScript", mController.getInfo()
						.runningScript());
			}
			if (mInfoView.isShow()) {
				if (mCurrent != null && mCurrent.equals("main")) {
					mInfoView.update(data.getSelf());	
				} else {
					Scripts current = data.getChilds().get(mCurrent);
					current.setCurrentPort(mCurrent);
//					mInfoView.update(current);
				}
			}
		} else {
			mInfoView.hide();
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int w = getWidth();
		int h = getHeight();
		mControlRect.offsetTo(w / 2 - mControlRect.width() / 2, h / 2
				- mControlRect.height() / 2);
		FrameLayout.LayoutParams leftParams = new LayoutParams(
				(w - mControlRect.width()) / 3, mControlRect.height(),
				Gravity.LEFT | Gravity.CENTER);
		mLeft.setLayoutParams(leftParams);

		FrameLayout.LayoutParams rightParams = new LayoutParams(
				(w - mControlRect.width()) / 3, mControlRect.height(),
				Gravity.RIGHT | Gravity.CENTER);
		mRight.setLayoutParams(rightParams);

	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		int w = getWidth();
		int h = getHeight();

		if (mController.isConnected()) {
			mPaint.setAlpha(255);
		} else {
			mPaint.setAlpha(120);
		}
		
		// 画control
		mControlRect.offsetTo(w / 2 - mControlRect.width() / 2, h / 2
				- mControlRect.height() / 2);
		canvas.drawRect(mControlRect, mPaint);

		// 画control的外设
		int scale = 6;
		Rect rect = new Rect();
		int w1 = mControlRect.width() / scale;
		int h1 = mControlRect.width() / scale / 2;
		rect.set(0, 0, w1, h1);
		rect.offsetTo(mControlRect.left - rect.width(), mControlRect.top);
		int len = mControlRect.height() / 8;
		for (int i = 0; i < 4; i++) {
			rect.offsetTo(mControlRect.left - rect.width(), mControlRect.top
					+ len * (i * 2 + 1) - rect.height() / 2);
			canvas.drawRect(rect, mPaint);
			canvas.drawLine(mLeft.getRight() + 10, rect.centerY(), rect.left,
					rect.centerY(), mPaint);
			rect.offset(rect.width(), 0);
			String text = String.valueOf(1 + i);
			mPaint.setTextSize(rect.height() * 1.2f);
			FontMetrics fm = mPaint.getFontMetrics();
			float h2 = fm.bottom - fm.top;
			float y = rect.top + rect.height() - (rect.height() - h2) / 2
					- fm.bottom;
			canvas.drawText(text,
					rect.centerX() - mPaint.measureText(text) / 2, y, mPaint);
		}
		for (int i = 0; i < 4; i++) {
			rect.offsetTo(mControlRect.right, mControlRect.top + len
					* (i * 2 + 1) - rect.height() / 2);
			canvas.drawRect(rect, mPaint);
			canvas.drawLine(mRight.getLeft() - 10, rect.centerY(), rect.right,
					rect.centerY(), mPaint);
			rect.offset(-rect.width(), 0);
			String text = String.valueOf(5 + i);
			mPaint.setTextSize(rect.height() * 1.2f);
			FontMetrics fm = mPaint.getFontMetrics();
			float h2 = fm.bottom - fm.top;
			float y = rect.top + rect.height() - (rect.height() - h2) / 2
					- fm.bottom;
			canvas.drawText(text,
					rect.centerX() - mPaint.measureText(text) / 2, y, mPaint);
		}
		super.dispatchDraw(canvas);
	}

	@Override
	public void onClick(View v) {
		if (v == mControl) {
			if (mController.isConnected()) {
				mCurrent = "main";
				mInfoView.showScripts(mController.getInfo().getSelf());
			}
			return;
		}
		Scripts s = (Scripts) v.getTag(R.id.tag_script);
		if (s != null) {
			mCurrent = v.getTag().toString();
			s.setCurrentPort(mCurrent);
			mInfoView.showScripts(s);
		}

	}

}
