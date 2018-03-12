package com.k1.graphcode.ui.views.dialog;

import java.util.ArrayList;
import java.util.List;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.k1.graphcode.R;

/**
 * 
 * @author BinGe
 * 
 * 所有dialog都使用这个工具类
 * 
 *         <style name="dialog" parent="@android:style/Theme.Dialog"> <item name
 *         ="android:windowFrame">@null</item>
 *         <item name="android:windowIsFloating">true</item>
 *         <item name="android:windowIsTranslucent">true</item>
 *         <item name="android:windowNoTitle">true</item>
 *         <item name="android:windowBackground">@android:color/transparent
 *         </item>
 *         <item name="android:backgroundDimEnabled">true</item> </style>
 * 
 */

public class CustomDialog extends Dialog {

	public final static int TYPE_MESSAGE = 0;
	public final static int TYPE_EDIT = 1;
	public final static int TYPE_PROGRESS = 2;
	public final static int TYPE_RADIO = 3;
	public final static int TYPE_LOADING = 4;

	private int mTypeFlag = TYPE_MESSAGE;
	private View mRoot;
	private ReturnResults mReturnResult; // 返回结果
	int mWidth, mHeitht;

	private String mNotitleMessage; // 无标题对话框 信息

	// 无标题对话框 构造方法

	public static CustomDialog createMessageDialog(Context context, int message, ReturnResults result) {
		CustomDialog dialog = new CustomDialog(context, TYPE_MESSAGE, context.getResources().getString(message),
				result);
		return dialog;
	}

	public static CustomDialog createMessageDialog(Context context, String message, ReturnResults result) {
		CustomDialog dialog = new CustomDialog(context, TYPE_MESSAGE, message, result);
		return dialog;
	}

	private CustomDialog(Context context, int type, String message, ReturnResults returnResult) {
		super(context, R.style.dialog);
		mNotitleMessage = message;
		mTypeFlag = type;
		mReturnResult = returnResult;
		init();
	}

	private String mEditTitle; // 编辑对话框 标题
	private String mText;

	// 编辑对话框 构造方法
	public static CustomDialog createEditDialog(Context context, String title, String text, ReturnResults result) {
		CustomDialog dialog = new CustomDialog(context, TYPE_EDIT, title, text, result);
		return dialog;
	}

	public CustomDialog(Context context, int type, String title, String text, ReturnResults returnResult) {
		super(context, R.style.dialog);
		mEditTitle = title.toString();
		mTypeFlag = type;
		mReturnResult = returnResult;
		mText = text;
		init();
	}

	// 编辑对话框 构造方法
	public static CustomDialog createEditDialog(Context context, int inputType, String title, ReturnResults result) {
		CustomDialog dialog = new CustomDialog(context, TYPE_EDIT, inputType, new StringBuffer(title), result);
		return dialog;
	}

	public CustomDialog(Context context, int type, int inputType, StringBuffer title, ReturnResults returnResult) {
		super(context, R.style.dialog);
		mEditTitle = title.toString();
		mTypeFlag = type;
		mReturnResult = returnResult;
		init();
	}

	private int mPrpgressCounts;// 进度条对话框 时间

	// 进度条对话框 构造方法

	public CustomDialog(Context context, int type, int counts) {
		super(context, R.style.dialog);
		mPrpgressCounts = counts;
		mTypeFlag = type;
		init();
	}

	private String mRadioTitle; // 单选对话框 标题
	private List<String> mRadioItems; // 单选对话框 选项
	private String mValue;
	private int mPosition;

	// 单选对话框 构造方法

	// public st

	public static CustomDialog createSelecterDialog(Context context, String title, List<String> radioItems,
			int position, ReturnResults returnResult) {
		String current = null;
		if (radioItems.size() > position) {
			current = radioItems.get(position);
		}
		return createSelecterDialog(context, title, radioItems, current, returnResult);
	}

	public static CustomDialog createSelecterDialog(Context context, String title, List<String> radioItems,
			String current, ReturnResults returnResult) {
		CustomDialog dialog = new CustomDialog(context, TYPE_RADIO, title, radioItems, current, returnResult);
		return dialog;
	}

	public static CustomDialog createSelecterDialog(Context context, String title, List<String> radioItems,
			ReturnResults returnResult) {
		CustomDialog dialog = new CustomDialog(context, TYPE_RADIO, title, radioItems, returnResult);
		return dialog;
	}

	public static CustomDialog createSelecterDialog(Context context, String title, String[] radioItems, String value,
			ReturnResults returnResult) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < radioItems.length; i++) {
			list.add(radioItems[i]);
		}
		return createSelecterDialog(context, title, list, value, returnResult);
	}

	public static CustomDialog createSelecterDialog(Context context, String title, String[] radioItems,
			ReturnResults returnResult) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < radioItems.length; i++) {
			list.add(radioItems[i]);
		}
		return createSelecterDialog(context, title, list, returnResult);
	}

	public CustomDialog(Context context, int type, String title, List<String> radioItems, int postion,
			ReturnResults returnResult) {
		super(context, R.style.dialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mRadioTitle = title;
		mTypeFlag = type;
		mPosition = postion;
		mRadioItems = radioItems;
		mReturnResult = returnResult;

		init();
	}

	public CustomDialog(Context context, int type, String title, List<String> radioItems, ReturnResults returnResult) {
		super(context, R.style.dialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mRadioTitle = title;
		mTypeFlag = type;
		mRadioItems = radioItems;
		mReturnResult = returnResult;

		init();
	}

	public CustomDialog(Context context, int type, String title, List<String> radioItems, String value,
			ReturnResults returnResult) {
		super(context, R.style.dialog);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mRadioTitle = title;
		mTypeFlag = type;
		mValue = value;
		mRadioItems = radioItems;
		mReturnResult = returnResult;

		init();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	public void init() {
		mWidth = Density.getInstence(getContext()).getScreenWidth();
		mHeitht = Density.getInstence(getContext()).getScreenHeight();
		switch (mTypeFlag) {
		case TYPE_EDIT:
			String[] s = { "NO", "YES" };
			mRoot = new StypeEdit(getContext(), mEditTitle, mText, s, mReturnResult);
			break;
		case TYPE_MESSAGE:
			mRoot = new DialogComfirm(getContext(), mNotitleMessage, mReturnResult);
			break;
		case TYPE_PROGRESS:
			String titleText = "下载信息";
			mRoot = new StypeProgress(getContext(), mPrpgressCounts, titleText);
			break;
		case TYPE_RADIO:
			List<String> listItems = new ArrayList<String>();
			for (int i = 0; i < 15; i++) {
				listItems.add("Item" + i);
			}
			mPosition = 0;
			if (!TextUtils.isEmpty(mValue) && mRadioItems.contains(mValue)) {
				mPosition = mRadioItems.indexOf(mValue);
			}
			String radiobutton = "确定";
			mRoot = new StypeRadio(getContext(), mRadioTitle, mRadioItems, mPosition, radiobutton, mReturnResult);

			break;
		case TYPE_LOADING:
			String text = "正在加载中...";
			mRoot = new StypeLoading(getContext(), text);
			break;

		default:
			break;
		}
		setContentView(mRoot);
	}

	class StypeEdit extends FrameLayout {
		String[] mSures;
		ReturnResults mReturnResult;
		String mTitle;
		String mText;
		Context mContext;

		public StypeEdit(Context context, String title, String text, String[] sure, ReturnResults returnResult) {
			super(context);
			// setBackgroun
			mContext = context;
			mSures = sure;
			mTitle = title;
			mText = text;
			mReturnResult = returnResult;
			initView();
		}

		public void initView() {
			CustomDrawables custonm = new CustomDrawables(mContext, (int) (mWidth * 0.8f),
					Density.getInstence(mContext).dip2px(60), 0);
			LinearLayout linear = new LinearLayout(mContext);
			linear.setOrientation(LinearLayout.VERTICAL);
			// linear.setBackgroundResource(R.drawable.linear_shape);
			LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams((int) (mWidth * 0.8f),
					LinearLayout.LayoutParams.WRAP_CONTENT);
			TextView title = new TextView(mContext);
			// title.setBackgroundResource(R.drawable.title_shape);
			title.setBackgroundDrawable(custonm.paintDrawable());
			int padding = Density.getInstence(mContext).dip2px(10);
			title.setPadding(padding, padding, padding, padding);
			title.setText(mTitle);
			title.setGravity(Gravity.CENTER);
			title.setTextColor(Color.WHITE);
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			final EditText edit = new EditText(mContext);
			edit.setBackgroundResource(color.black);
			edit.setTextColor(Color.WHITE);
			if (!TextUtils.isEmpty(mText)) {
				edit.setText(mText);
				edit.setSelection(mText.length());
			}
			LinearLayout sureLinear = new LinearLayout(mContext);
			LinearLayout.LayoutParams sureLinearParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, Density.getInstence(mContext).dip2px(50));
			sureLinearParams.weight = 1;
			Button cancelButton = new Button(mContext);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();

				}
			});
			custonm.setFlag(CustomDrawables.TYPE_LEFT_BUTTON);
			// cancelButton.setBackgroundResource(R.drawable.button_style_left);
			cancelButton.setBackgroundDrawable(custonm.paintDrawable());
			cancelButton.setTextColor(0xff5379b8);
			Button sureButton = new Button(mContext);
			// sureButton.setBackgroundResource(R.drawable.button_style_right);
			custonm.setFlag(CustomDrawables.TYPE_RIGHT_BUTTON);
			sureButton.setBackgroundDrawable(custonm.paintDrawable());
			sureButton.setTextColor(0xff5379b8);
			cancelButton.setText(mSures[0]);
			sureButton.setPadding(padding, padding, padding, padding);
			cancelButton.setPadding(padding, padding, padding, padding);
			cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			sureButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			sureButton.setText(mSures[1]);
			sureButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (!TextUtils.isEmpty(edit.getText())) {
						String message = edit.getText().toString();
						mReturnResult.result(message);
						dismiss();
					}
				}
			});
			sureLinear.addView(cancelButton, sureLinearParams);
			sureLinear.addView(sureButton, sureLinearParams);
			linear.addView(title, sureLinearParams);
			linear.addView(edit, linearParams);
			linear.addView(sureLinear, linearParams);
			this.addView(linear);

		}

	}

	class DialogComfirm extends FrameLayout {
		Context mContext;
		String mMessage;

		public DialogComfirm(Context context, String message, ReturnResults results) {
			super(context);
			mContext = context;
			mMessage = message;
			initView();
		}

		public void initView() {
			CustomDrawables custom = new CustomDrawables(mContext, (int) (mWidth * 0.8),
					Density.getInstence(mContext).dip2px(110), CustomDrawables.TYPE_PROGRESS);
			LinearLayout linear = new LinearLayout(mContext);
			linear.setBackgroundDrawable(custom.paintDrawable());
			linear.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams((int) (mWidth * 0.8f),
					LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams contextParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, Density.getInstence(mContext).dip2px(60));
			contextParams.topMargin = Density.getInstence(mContext).dip2px(10);
			contextParams.leftMargin = Density.getInstence(mContext).dip2px(20);
			contextParams.rightMargin = Density.getInstence(mContext).dip2px(20);
			contextParams.bottomMargin = Density.getInstence(mContext).dip2px(5);

			TextView messText = new TextView(mContext);
			messText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f);
			messText.setText(mMessage);

			messText.setGravity(Gravity.CENTER_VERTICAL);
			linear.addView(messText, contextParams);

			int padding = Density.getInstence(mContext).dip2px(10);
			LinearLayout sureLinear = new LinearLayout(mContext);
			LinearLayout.LayoutParams sureLinearParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, Density.getInstence(mContext).dip2px(50));
			sureLinearParams.weight = 1;
			Button cancelButton = new Button(mContext);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();

				}
			});
			custom.setFlag(CustomDrawables.TYPE_LEFT_BUTTON);
			// cancelButton.setBackgroundResource(R.drawable.button_style_left);
			cancelButton.setBackgroundDrawable(custom.paintDrawable());
			cancelButton.setTextColor(0xff5379b8);
			Button sureButton = new Button(mContext);
			// sureButton.setBackgroundResource(R.drawable.button_style_right);
			custom.setFlag(CustomDrawables.TYPE_RIGHT_BUTTON);
			sureButton.setBackgroundDrawable(custom.paintDrawable());
			sureButton.setTextColor(0xff5379b8);
			cancelButton.setText("NO");
			sureButton.setPadding(padding, padding, padding, padding);
			cancelButton.setPadding(padding, padding, padding, padding);
			cancelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			sureButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			sureButton.setText("YES");
			sureButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mReturnResult.result("YES");
					dismiss();
				}
			});
			sureLinear.addView(cancelButton, sureLinearParams);
			sureLinear.addView(sureButton, sureLinearParams);

			linear.addView(sureLinear, linearParams);
			this.addView(linear, linearParams);

		}

	}

	class StypeProgress extends FrameLayout {
		int mMaxlength;
		Context mContext;
		ProgressBar mProgress;
		int mProgressColor = 0xff69af2a;
		int mProgressBackColor = Color.BLACK;
		int time = 1;
		String titleText = "下载信息";

		public StypeProgress(Context context, int length, String titleText) {
			super(context);
			mMaxlength = 60;
			mContext = context;
			this.titleText = titleText;
			initView();
		}

		int mlength, progressWidth;

		public void initView() {
			LinearLayout linear = new LinearLayout(mContext);
			linear.setOrientation(LinearLayout.VERTICAL);
			CustomDrawables custom = new CustomDrawables(mContext, (int) (mWidth * 0.8),
					Density.getInstence(mContext).dip2px(100), CustomDrawables.TYPE_PROGRESS);
			// linear.setBackgroundResource(R.drawable.linear_shape);
			linear.setBackgroundDrawable(custom.paintDrawable());
			linear.setOrientation(LinearLayout.VERTICAL);
			// linear.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams((int) (mWidth * 0.8f),
					Density.getInstence(mContext).dip2px(100));
			LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams((int) (mWidth * 0.8f),
					LinearLayout.LayoutParams.WRAP_CONTENT);
			titleParams.bottomMargin = Density.getInstence(mContext).dip2px(10);
			titleParams.leftMargin = Density.getInstence(mContext).dip2px(15);
			titleParams.topMargin = Density.getInstence(mContext).dip2px(20);
			titleParams.rightMargin = Density.getInstence(mContext).dip2px(5);
			mlength = (int) (mWidth * 0.77) / mMaxlength;
			progressWidth = mlength;
			TextView title = new TextView(mContext);
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			title.setText("下载信息");
			linear.addView(title, titleParams);
			mProgress = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
			mProgress.setProgressDrawable(new Drawable() {

				@Override
				public void setColorFilter(ColorFilter colorFilter) {

				}

				@Override
				public void setAlpha(int alpha) {

				}

				@Override
				public int getOpacity() {
					return 0;
				}

				@Override
				public void draw(Canvas canvas) {
					Paint p = new Paint();
					p.setAntiAlias(true);
					p.setColor(0xff3a3a3a);
					int r = Density.getInstence(mContext).dip2px(3);
					int rp = Density.getInstence(mContext).dip2px(1);
					RectF backrect = new RectF(0, 0, (float) (mWidth * 0.77), Density.getInstence(mContext).dip2px(16));

					canvas.drawRoundRect(backrect, r, r, p);
					p.setColor(mProgressColor);

					RectF rect = new RectF(1, Density.getInstence(mContext).dip2px(1), progressWidth,
							Density.getInstence(mContext).dip2px(15));
					canvas.drawRoundRect(rect, rp, rp, p);

					p.setColor(Color.BLACK);
					p.setTextSize(Density.getInstence(mContext).sp2px(8));
					if (time < mMaxlength) {
						canvas.drawText(time + "%", progressWidth - Density.getInstence(mContext).dip2px(6),
								Density.getInstence(mContext).dip2px(11), p);
					}

					progressWidth = progressWidth + mlength;

					if (progressWidth < mWidth * 0.77) {

						++time;
						if (time > mMaxlength) {
							time = mMaxlength;
						}
						invalidateSelf();
						try {
							if (time > 3)
								Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			});

			mProgress.setMax(mMaxlength);

			LinearLayout.LayoutParams contextParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			contextParams.leftMargin = Density.getInstence(mContext).dip2px(5);
			contextParams.rightMargin = Density.getInstence(mContext).dip2px(5);
			linear.addView(mProgress, contextParams);

			addView(linear, linearParams);

		}

	}

	class StypeRadio extends FrameLayout {
		private Context mContext;
		ReturnResults mReturnResult;
		List<String> mRadioItems;
		String mRadioTitle, mRadioButton;
		int position = 0;
		int curent;
		String scurent;

		public StypeRadio(Context context, String title, List<String> string, int curent, String button,
				ReturnResults returnResult) {
			super(context);
			mContext = context;
			mRadioItems = string;
			mRadioTitle = title;
			mReturnResult = returnResult;
			this.curent = curent;
			mRadioButton = button;
			initView();
		}

		public StypeRadio(Context context, String title, List<String> string, String curent, String button,
				ReturnResults returnResult) {
			super(context);
			mContext = context;
			mRadioItems = string;
			mRadioTitle = title;
			scurent = curent;
			mReturnResult = returnResult;
			mRadioButton = button;
			initView();
		}

		public void initView() {
			LinearLayout linear = new LinearLayout(mContext);
			CustomDrawables custom = new CustomDrawables(mContext, (int) (mWidth * 0.8),
					Density.getInstence(mContext).dip2px(50), CustomDrawables.TYPE_TITLE);
			linear.setOrientation(LinearLayout.VERTICAL);
			linear.setGravity(Gravity.CENTER);
			// linear.setBackgroundResource(R.drawable.linear_shape);
			// linear.setBackground(custom.paintDrawable());
			LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams((int) (mWidth * 0.8f),
					(int) (mHeitht * 0.7f));
			TextView textView = new TextView(mContext);
			ListView listView = new ListView(mContext);

			listView.setBackgroundColor(0xff1e1e1e);

			int padding = Density.getInstence(mContext).dip2px(10);
			textView.setPadding(padding, padding, padding, padding);
			textView.setText(mRadioTitle);
			textView.setGravity(Gravity.CENTER);
			textView.setBackgroundDrawable(custom.paintDrawable());
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			LinearLayout.LayoutParams contextParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, Density.getInstence(mContext).dip2px(50));
			linear.addView(textView, contextParams);
			linear.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, (int) (mHeitht * 0.3f));
			Button sureButton = new Button(mContext);
			sureButton.setText(mRadioButton);
			sureButton.setClickable(false);
			sureButton.setTextColor(0xff5379b8);
			sureButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			listView.setVerticalScrollBarEnabled(false);
			custom.setFlag(CustomDrawables.TYPE_BUTTON);
			sureButton.setBackgroundDrawable(custom.paintDrawable());
			sureButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
					mReturnResult.result(mRadioItems.get(position));
				}
			});
			// sureButton.setBackgroundResource(R.drawable.button_style_left);
			linear.addView(sureButton, LinearLayout.LayoutParams.MATCH_PARENT,
					Density.getInstence(mContext).dip2px(50));

			final ListViewAdapter mListViewAdapter = new ListViewAdapter(mContext, mRadioItems, scurent);
			listView.setAdapter(mListViewAdapter);
			StateListDrawable state = new StateListDrawable();
			mListViewAdapter.setSelectID(curent);
			state.addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(Color.TRANSPARENT));

			listView.setSelector(state);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int positio, long id) {
					mListViewAdapter.setSelectID(positio); // 选中位置
					mListViewAdapter.notifyDataSetChanged(); // 刷新适配器
					position = positio;
					// RadioButton rb = (RadioButton)
					// view.findViewById(0x10002);
					// rb.setChecked(true);
					// mReturnResult.result(mRadioItems.get(position));
					// dismiss();
				}
			});

			// 自定义回调函数
			mListViewAdapter.setOncheckChanged(new OnMyCheckChangedListener() {

				@Override
				public void setSelectID(int selectID) {
					mListViewAdapter.setSelectID(selectID); // 选中位置
					mListViewAdapter.notifyDataSetChanged(); // 刷新适配器
				}
			});
			addView(linear, linearParams);
		}

	}

	class StypeLoading extends FrameLayout {

		String mTitle;
		Context mContext;
		ProgressBar progress;
		int h, w;

		public StypeLoading(Context context, String title) {
			super(context);
			mTitle = title;
			mContext = context;
			initView();
		}

		public void initView() {
			LinearLayout linearLayout = new LinearLayout(mContext);
			linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams((int) (mWidth * 0.5f),
					(int) (mWidth * 0.5f));
			LinearLayout.LayoutParams contextParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			contextParams.bottomMargin = Density.getInstence(mContext).dip2px(10);
			contextParams.topMargin = Density.getInstence(mContext).dip2px(30);
			// contextParams.leftMargin =
			// Density.getInstence(mContext).dip2px(30);
			CustomDrawables customs = new CustomDrawables(mContext, (int) (mWidth * 0.5f), (int) (mWidth * 0.5f),
					CustomDrawables.TYPE_PROGRESS);
			linearLayout.setBackgroundDrawable(customs.paintDrawable());
			h = (int) (mWidth * 0.5 - Density.getInstence(mContext).dip2px(45));
			w = (int) (mWidth * 0.5);
			progress = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
			progress.setProgressDrawable(new Drawable() {

				@Override
				public void setColorFilter(ColorFilter colorFilter) {
					// TODO Auto-generated method stub

				}

				@Override
				public void setAlpha(int alpha) {
					// TODO Auto-generated method stub

				}

				@Override
				public int getOpacity() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public void draw(Canvas canvas) {
					int r = Density.getInstence(mContext).dip2px(30);
					Paint p = new Paint();
					p.setAntiAlias(true);
					p.setColor(Color.WHITE);
					p.setStyle(Paint.Style.STROKE);
					p.setStrokeWidth(Density.getInstence(mContext).dip2px(8));
					canvas.drawCircle(w / 2, h / 3, r, p);
					// Rect rect=new Rect(w/2-r,);

				}
			});
			TextView text = new TextView(mContext);
			text.setText(mTitle);
			text.setGravity(Gravity.CENTER_HORIZONTAL);
			text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
			linearLayout.addView(text, contextParams);
			linearLayout.addView(progress, LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			addView(linearLayout, linearParams);
		}

	}

	class CustomDrawables {
		int mCheckColor = Color.BLACK;
		int mNoCheckColor = 0xff1e1e1e;
		int mWidth, mHeight;
		int mCorner = 8;
		int mFlag;
		Context mContext;
		public final static int TYPE_TITLE = 0;
		public final static int TYPE_LEFT_BUTTON = 1;
		public final static int TYPE_RIGHT_BUTTON = 2;
		public final static int TYPE_PROGRESS = 3;
		public final static int TYPE_BUTTON = 4;

		public CustomDrawables(Context context, int mWidth, int mHeight, int mFlag) {
			super();
			mContext = context;
			this.mWidth = mWidth;
			this.mHeight = mHeight;
			this.mFlag = mFlag;
			mCorner = Density.getInstence(context).dip2px(8);
		}

		public void setFlag(int flag) {
			this.mFlag = flag;
		}

		public Drawable paintDrawable() {
			Drawable drawable = null;
			switch (mFlag) {
			case TYPE_TITLE:
				drawable = new TitleDrawable();
				break;
			case TYPE_RIGHT_BUTTON:
				drawable = new SelectorDrawable(TYPE_RIGHT_BUTTON).paint();
				break;
			case TYPE_LEFT_BUTTON:
				drawable = new SelectorDrawable(TYPE_LEFT_BUTTON).paint();
				break;
			case TYPE_BUTTON:
				drawable = new SelectorDrawable(TYPE_BUTTON).paint();
				break;
			case TYPE_PROGRESS:
				drawable = new TitleDrawable(TYPE_PROGRESS);
				break;

			default:
				break;
			}

			return drawable;

		}

		class TitleDrawable extends Drawable {
			int style;

			public TitleDrawable() {
				super();
			}

			public TitleDrawable(int style) {
				super();
				this.style = style;
			}

			// 外部矩形弧度
			float[] outerR = new float[] { mCorner, mCorner, mCorner, mCorner, 0, 0, 0, 0 };
			// 内部矩形与外部矩形的距离
			RectF inset = new RectF(100, 100, 50, 50);
			// 内部矩形弧度
			float[] innerRadii = new float[] { 20, 20, 20, 20, 20, 20, 20, 20 };

			@Override
			public void draw(Canvas canvas) {
				if (style == CustomDrawables.TYPE_PROGRESS) {
					outerR = new float[] { mCorner, mCorner, mCorner, mCorner, mCorner, mCorner, mCorner, mCorner };
				}
				ShapeDrawable myShapeDrawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
				myShapeDrawable.getPaint().setColor(mNoCheckColor);
				myShapeDrawable.setBounds(0, 0, mWidth, mHeight);
				myShapeDrawable.draw(canvas);

			}

			@Override
			public void setAlpha(int alpha) {

			}

			@Override
			public void setColorFilter(ColorFilter colorFilter) {

			}

			@Override
			public int getOpacity() {
				return 0;
			}

		}

		class ButtonDrawable extends Drawable {
			boolean ischeck = false;
			int direction = 0;
			float[] outerR = new float[] { mCorner, mCorner, mCorner, mCorner, 0, 0, 0, 0 };
			int color = 0;
			int width;

			public ButtonDrawable(boolean ischeck, int direction) {
				this.ischeck = ischeck;
				this.direction = direction;
			}

			@Override
			public void draw(Canvas canvas) {
				if (ischeck) {
					color = mCheckColor;
					switch (direction) {

					case TYPE_LEFT_BUTTON:
						width = mWidth / 2;
						outerR = new float[] { 0, 0, 0, 0, 0, 0, mCorner, mCorner };
						break;
					case TYPE_RIGHT_BUTTON:
						width = mWidth / 2;
						outerR = new float[] { 0, 0, 0, 0, mCorner, mCorner, 0, 0 };
						break;
					case TYPE_BUTTON:
						width = mWidth;
						outerR = new float[] { 0, 0, 0, 0, mCorner, mCorner, mCorner, mCorner };
						break;

					default:
						break;
					}
				} else {
					color = mNoCheckColor;
					switch (direction) {
					case TYPE_LEFT_BUTTON:
						width = mWidth / 2;
						outerR = new float[] { 0, 0, 0, 0, 0, 0, mCorner, mCorner };
						break;
					case TYPE_RIGHT_BUTTON:
						width = mWidth / 2;
						outerR = new float[] { 0, 0, 0, 0, mCorner, mCorner, 0, 0 };
						break;
					case TYPE_BUTTON:
						width = mWidth;
						outerR = new float[] { 0, 0, 0, 0, mCorner, mCorner, mCorner, mCorner };
						break;
					default:
						break;
					}
				}

				ShapeDrawable myShapeDrawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
				myShapeDrawable.getPaint().setColor(color);
				myShapeDrawable.setBounds(0, 0, width, Density.getInstence(mContext).dip2px(50));
				myShapeDrawable.draw(canvas);

			}

			@Override
			public void setAlpha(int alpha) {

			}

			@Override
			public void setColorFilter(ColorFilter colorFilter) {

			}

			@Override
			public int getOpacity() {
				return 0;
			}

		}

		class SelectorDrawable {
			int direction;

			public SelectorDrawable(int direction) {
				super();
				this.direction = direction;
			}

			public Drawable paint() {
				ButtonDrawable checkback = new ButtonDrawable(true, direction);
				ButtonDrawable nocheckback = new ButtonDrawable(false, direction);
				StateListDrawable sld = new StateListDrawable();
				sld.addState(new int[] { -android.R.attr.state_pressed }, nocheckback);
				sld.addState(new int[] { android.R.attr.state_pressed }, checkback);
				return sld;

			}

		}

	}

	// 返回事件监听
	public interface ReturnResults {
		void result(Object o);
	}

	public View radioItems() {

		LinearLayout contentlinear = new LinearLayout(getContext());
		contentlinear.setId(0x10000);
		TextView textview = new TextView(getContext());
		textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		textview.setTextColor(Color.WHITE);
		textview.setId(0x10001);
		textview.setPadding(Density.getInstence(getContext()).dip2px(10), 0, 0, 0);
		RadioButton radiobut = new RadioButton(getContext());
		radiobut.setFocusable(false);
		radiobut.setClickable(false);
		radiobut.setChecked(true);
		radiobut.setId(0x10002);
		radiobut.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
		StateListDrawable sld = new StateListDrawable();
		sld.addState(new int[] { -android.R.attr.state_checked }, new RadioDrawable(false));
		sld.addState(new int[] { android.R.attr.state_checked }, new RadioDrawable(true));
		radiobut.setBackgroundDrawable(sld);
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				Density.getInstence(getContext()).dip2px(50));
		textParams.leftMargin = Density.getInstence(getContext()).dip2px(20);
		textParams.weight = 1;
		contentlinear.addView(textview, textParams);
		contentlinear.addView(radiobut, Density.getInstence(getContext()).dip2px(70),
				Density.getInstence(getContext()).dip2px(50));

		return contentlinear;

	}

	class RadioDrawable extends Drawable {
		boolean mFlag;
		int ra, r, mSmallR;

		public RadioDrawable(boolean flag) {
			super();
			mFlag = flag;
			ra = Density.getInstence(getContext()).dip2px(25);
			r = Density.getInstence(getContext()).dip2px(10);
			mSmallR = Density.getInstence(getContext()).dip2px(5);
		}

		@Override
		public void draw(Canvas canvas) {
			// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
			// Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			if (mFlag) {
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawCircle(ra, ra, r, paint);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(ra, ra, mSmallR, paint);

			} else {
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawCircle(ra, ra, r, paint);
			}
		}

		@Override
		public void setAlpha(int alpha) {

		}

		@Override
		public void setColorFilter(ColorFilter colorFilter) {

		}

		@Override
		public int getOpacity() {
			return 0;
		}

	}

	class ListViewAdapter extends BaseAdapter {
		private List<String> list;
		private int selectID;
		int poid = 3;
		String postring;

//		private OnMyCheckChangedListener mCheckChange;

		// 构造函数，用作初始化各项数据
		public ListViewAdapter(Context context, List<String> list) {
			this.list = list;
		}

		public ListViewAdapter(Context context, List<String> list, int position) {
			this.list = list;
			poid = 3;
		}

		public ListViewAdapter(Context context, List<String> list, String position) {
			this.list = list;
			postring = position;
		}

		// 获取ListView的item总数
		public int getCount() {
			return list.size();
		}

		// 获取ListView的item
		public Object getItem(int position) {
			return getItem(position);
		}

		// 获取ListView的item的ID
		public long getItemId(int position) {
			return position;
		}

		// 自定义的选中方法
		public void setSelectID(int position) {
			selectID = position;
		}

		// 获取item的视图及其中含有的操作
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;

			/**
			 * 这个是网上流行的适配器缓存View写法(软引用原理)，就不多说了。
			 */
			if (convertView == null) {
				viewCache = new ViewCache();
				// convertView =
				// LayoutInflater.from(context).inflate(R.layout.list_item,
				// null);
				convertView = radioItems();
				viewCache.linearLayout = (LinearLayout) convertView.findViewById(0x10000);

				viewCache.itemName = (TextView) convertView.findViewById(0x10001);
				viewCache.radioBtn = (RadioButton) convertView.findViewById(0x10002);

				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}

			viewCache.itemName.setText(list.get(position));

			// 核心方法，判断单选按钮被按下的位置与之前的位置是否相等，然后做相应的操作。
			if (selectID == position) {
				// viewCache.linearLayout.setBackgroundColor(Color.BLUE);
				viewCache.radioBtn.setChecked(true);
			} else {
				// viewCache.linearLayout.setBackgroundColor(0);
				viewCache.radioBtn.setChecked(false);
			}
			StateListDrawable state = new StateListDrawable();
			state.addState(new int[] { -android.R.attr.state_pressed }, new ColorDrawable(0x000000000));
			state.addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(0xddddddd));
			viewCache.linearLayout.setBackgroundDrawable(state);
			// 单选按钮的点击事件监听
			// 单选按钮的点击事件监听
			// viewCache.radioBtn.setonc

			return convertView;
		}

		// 回调函数，很类似OnClickListener吧，呵呵
		public void setOncheckChanged(OnMyCheckChangedListener l) {
//			mCheckChange = l;
		}

		// 缓存类
		class ViewCache {
			LinearLayout linearLayout;
			TextView itemID, itemName;
			RadioButton radioBtn;
		}
	}

	static class Density {

		private static Density sDensity;

		public static final float DEFAULT_SCALE = 2;

		public static Density getInstence(Context context) {
			if (sDensity == null) {
				sDensity = new Density(context);
			}
			return sDensity;
		}

		private DisplayMetrics mDM;
		private float scale;
		private float fontScale;
		private int mStatusBarHeight;

		public Density(Context context) {
			mDM = context.getResources().getDisplayMetrics();
			scale = mDM.density;
			fontScale = mDM.scaledDensity;
			mStatusBarHeight = getStatusBarHeight(context);
		}

		private int getStatusBarHeight(Context context) {
			int result = 0;
			int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
			if (resourceId > 0) {
				result = context.getResources().getDimensionPixelSize(resourceId);
			}
			return result;
		}

		public int getStatusBarHeight() {
			return mStatusBarHeight;
		}

		public int getScreenWidth() {
			return mDM.widthPixels;
		}

		public int getScreenHeight() {
			return mDM.heightPixels;
		}

		public int dip2px(float dpValue) {
			return (int) (dpValue * scale + 0.5f);
		}

		public int px2dip(float pxValue) {
			return (int) (pxValue / scale + 0.5f);
		}

		public int px2sp(float pxValue) {
			return (int) (pxValue / fontScale + 0.5f);
		}

		public int sp2px(float spValue) {
			return (int) (spValue * fontScale + 0.5f);
		}
	}

	// 单选框选中监听
	public interface OnMyCheckChangedListener {
		void setSelectID(int selectID);
	}

}
