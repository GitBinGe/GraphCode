package com.k1.graphcode.ui.views;

import java.io.Serializable;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.k1.graphcode.R;
import com.k1.graphcode.utils.Density;

public class Dialog extends FrameLayout implements OnClickListener ,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextView mTitle;
	private TextView mMsg;
	private EditText mEdit;
	private TextView mButtonOK, mButtonCancel;
	private ListView mListView;
	private FrameLayout.LayoutParams mParams;
	private View mParent;

	public Dialog(Context context) {
		super(context);
		init(context);
	}
	
	public Dialog(Context context, AttributeSet attri) {
		super(context, attri);
		init(context);
	}
	
	private void init(Context context) {
		setBackgroundColor(Color.TRANSPARENT);

		LayoutInflater.from(context).inflate(R.layout.dialog, this);

		mParent = findViewById(R.id.parent);
		mParams = (LayoutParams) mParent.getLayoutParams();

		mTitle = (TextView) findViewById(R.id.title);
		mMsg = (TextView) findViewById(R.id.msg);
		mEdit = (EditText) findViewById(R.id.edit);
		mListView = (ListView) findViewById(R.id.list);
		mButtonCancel = (TextView) findViewById(R.id.cancel);
		mButtonCancel.setOnClickListener(this);
		mButtonOK = (TextView) findViewById(R.id.ok);
		mButtonOK.setOnClickListener(this);

	}

	public void showEditDialog(String title, int inputType,
			final DialogCallback cb) {
		int dp_100 = Density.dip2px(100);
		mParams.topMargin = -dp_100;
		mParams.bottomMargin = 0;
		mParent.setLayoutParams(mParams);
		show();
		mTitle.setText(title);
		mEdit.setVisibility(View.VISIBLE);
		mMsg.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
		findViewById(R.id.button_layout).setVisibility(View.VISIBLE);
		mEdit.setText("");
		mEdit.setInputType(inputType);
		mEdit.setSingleLine();
		mEdit.setFocusableInTouchMode(true);
		mEdit.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) mEdit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(mEdit, 0);
		mButtonOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mEdit.getText())) {
					if (cb != null) {
						cb.dialogReturn(mEdit.getText().toString());
						cancelDialog();
					}
				}
			}
		});
	}

	public void showSelecterDialog(String title, final String[] adapter,
			final DialogCallback cb) {
		int dp_100 = Density.dip2px(100);
		mParams.topMargin = dp_100;
		mParams.bottomMargin = dp_100;
		mParent.setLayoutParams(mParams);
		show();
		mTitle.setText(title);
		mEdit.setVisibility(View.GONE);
		mMsg.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		findViewById(R.id.button_layout).setVisibility(View.GONE);
		mListView.setAdapter(new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_list_item_1, adapter));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				cb.dialogReturn(adapter[position]);
				cancelDialog();
			}
		});
	}

	public void showConfirmationDialog(String title, String msg,
			final DialogCallback cb) {
		int dp_100 = Density.dip2px(100);
		mParams.topMargin = dp_100;
		mParams.bottomMargin = dp_100;
		mParent.setLayoutParams(mParams);
		show();
		mTitle.setText(title);
		mEdit.setVisibility(View.GONE);
		mMsg.setVisibility(View.VISIBLE);
		mMsg.setText(msg);
		mListView.setVisibility(View.GONE);
		mButtonOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Dialog.this.onClick(v);
				if (cb  != null) {
					cb.dialogReturn("YES");
				}
			}
		});
	}

	public void show() {
		setVisibility(View.VISIBLE);
		TranslateAnimation t = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
		t.setDuration(200);
		AlphaAnimation a = new AlphaAnimation(0, 1);
		a.setDuration(200);
		startAnimation(a);

	}

	@Override
	public void onClick(View v) {
		cancelDialog();
	}

	public void cancelDialog() {
		setVisibility(View.GONE);
		InputMethodManager inputManager = (InputMethodManager) getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputManager != null) {
			inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
		}
	}

	public interface DialogCallback {
		public void dialogReturn(String value);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cancelDialog();
		return true;
	}

}
