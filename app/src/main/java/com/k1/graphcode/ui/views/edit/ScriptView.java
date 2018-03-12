package com.k1.graphcode.ui.views.edit;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.k1.graphcode.R;
import com.k1.graphcode.ui.views.OptionButton;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;
import com.k1.graphcode.ui.views.edit.SideBar.SideListener;
import com.k1.graphcode.utils.Density;

public class ScriptView extends RelativeLayout implements SideListener {

	private ScriptEditView mEdit;
	private OptionButton mOptionButton;
	private View mSave;
	private Handler mHandler;

	public ScriptView(Context context) {
		this(context, null);
	}

	public ScriptView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setTitle(String title) {
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(title);
	}

	/**
	 * 设置为主程序编辑还是子程序编辑
	 * 
	 * @param type
	 */
	public void setEditType(int type, String blockName) {
		mEdit.setEditType(type, blockName);
	}

	public void saveBlock() {
		mEdit.saveBlock();
	}
	
	private void init(Context context) {
		setBackgroundColor(getResources().getColor(R.color.color_background));
		mHandler = new Handler();
		FrameLayout title = (FrameLayout) LayoutInflater.from(context)
				.inflate(R.layout.layout_title, this)
				.findViewById(R.id.layout_title);

		mSave = findViewById(R.id.save);
//		mSave.setVisibility(View.VISIBLE);
		mSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mEdit.saveBlock();
			}
		});
		
		View zoomIn = findViewById(R.id.zoom_in);
		zoomIn.setVisibility(View.VISIBLE);
		zoomIn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mEdit.zoomIn();
			}
		});
		
		View zoomOut = findViewById(R.id.zoom_out);
		zoomOut.setVisibility(View.VISIBLE);
		zoomOut.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mEdit.zoomOut();
			}
		});
		
		View zoomReset = findViewById(R.id.zoom_reset);
//		zoomReset.setVisibility(View.VISIBLE);
		zoomReset.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mEdit.zoomReset();
			}
		});

		OptionButton ob = new OptionButton(context, true, true);
		ob.setDefault(false, true);
		ob.setColor(Color.WHITE);
		ob.setBackClickListener(new OnClickListener() {
			public void onClick(View v) {
				hideSideBar();
			}
		});
		ob.setMenuClickListener(new OnClickListener() {
			public void onClick(View v) {
				showSideBar();
			}
		});
		FrameLayout.LayoutParams obParams = new FrameLayout.LayoutParams(
				Density.dip2px(40), Density.dip2px(40), Gravity.LEFT
						| Gravity.CENTER);
		title.addView(ob, obParams);
		mOptionButton = ob;

		// view.setVisibility(View.INVISIBLE);
		mEdit = new ScriptEditView(context);
		mEdit.setSideListner(this);
		RelativeLayout.LayoutParams programParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		programParams.addRule(RelativeLayout.BELOW, R.id.layout_title);
		addView(mEdit, programParams);
		// View title =
		// LayoutInflater.from(context).inflate(R.layout.layout_title, this);
	}

	private void showSideBar() {
		mEdit.showSide();
		delayClick();
	}

	private void hideSideBar() {
		mEdit.hideSide();
		delayClick();
	}

	public void delayClick() {
		mOptionButton.setClickable(false);
		mHandler.postDelayed(new Runnable() {
			public void run() {
				mOptionButton.setClickable(true);
			}
		}, 500);
	}

	public void showEditDialog(String title, int inputType, ReturnResults cb) {
		CustomDialog.createEditDialog(getContext(), inputType, title, cb)
				.show();
	}

	public void showSelectorDialog(String title, String[] adapter,
			final ReturnResults cb) {
		CustomDialog.createSelecterDialog(getContext(), title, adapter, cb)
				.show();
	}
	
	public void showSelectorDialog(String title, String[] adapter, int current,
			final ReturnResults cb) {
		CustomDialog.createSelecterDialog(getContext(), title, adapter, adapter[current],cb)
				.show();
	}
	
	public void showSelectorDialog(String title, String[] adapter, String current,
			final ReturnResults cb) {
		CustomDialog.createSelecterDialog(getContext(), title, adapter, current,cb)
				.show();
	}

	public void showConfirmationDialog(String title, String msg,
			ReturnResults cb) {
		CustomDialog.createMessageDialog(getContext(), msg, cb).show();
	}

	@Override
	public void onSideChanged(boolean show) {
		if (show) {
			mOptionButton.gotoBack();
		} else {
			mOptionButton.gotoMenu();
		}
	}

}
