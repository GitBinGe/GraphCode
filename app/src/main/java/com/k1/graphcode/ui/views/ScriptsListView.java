package com.k1.graphcode.ui.views;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.k1.graphcode.R;
import com.k1.graphcode.utils.Density;

public class ScriptsListView extends ListView {

	private ScriptsAdapter mScriptsAdapter;
	private Handler mHandler;
	private List<String> mList;

	public ScriptsListView(Context context) {
		super(context);
		mHandler = new Handler();
		setBackgroundColor(Color.TRANSPARENT);
		setDivider(null);
		setSelector(android.R.color.transparent);
		mScriptsAdapter = new ScriptsAdapter();
		setAdapter(mScriptsAdapter);
	}
	
	public void setListInfo(List<String> list) {
		mList = list;
		mScriptsAdapter.notifyDataSetChanged();
	}

	class ScriptsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		@Override
		public String getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Item itemView = null;
			if (convertView == null) {
				itemView = new Item(getContext());
				convertView = itemView;
			} else {
				itemView = (Item) convertView;
			}
			itemView.setName(getItem(position));
			return convertView;
		}

	}

	class Item extends RelativeLayout implements OnClickListener {

		private TextView mNameView;
		private ImageView mPlayView;
		private ImageView mDownView;
		private ImageView mDeleteView;

		@SuppressLint("ResourceType")
		public Item(Context context) {
			super(context);
			int height = Density.dip2px(50);
			int padding = Density.dip2px(15);
			setPadding(padding, 0, padding, 0);
			mNameView = new TextView(context);
			mNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			mNameView.setTextColor(Color.WHITE);
			mNameView.setPadding(0, padding, 0, padding);
			mNameView.setGravity(Gravity.CENTER);
			addView(mNameView, LayoutParams.WRAP_CONTENT,
					LayoutParams.MATCH_PARENT);

			View line = new View(getContext());
			line.setBackgroundColor(0x33ffffff);
			RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, Density.dip2px(1) / 2);
			lineParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			addView(line, lineParams);

			mPlayView = new ImageView(context);
			mPlayView.setId(138);
			mPlayView.setOnClickListener(this);
			mPlayView.setBackgroundResource(R.drawable.general_selector);
			mPlayView.setImageResource(R.mipmap.play);
			mPlayView.setScaleType(ScaleType.CENTER);
			RelativeLayout.LayoutParams playParams = new RelativeLayout.LayoutParams(
					height, height);
			playParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			addView(mPlayView, playParams);

			mDownView = new ImageView(context);
			mDownView.setId(139);
			mDownView.setOnClickListener(this);
			mDownView.setBackgroundResource(R.drawable.general_selector);
			mDownView.setImageResource(R.mipmap.download);
			mDownView.setScaleType(ScaleType.CENTER);
			RelativeLayout.LayoutParams downParams = new RelativeLayout.LayoutParams(
					height, height);
			downParams.addRule(RelativeLayout.LEFT_OF, mPlayView.getId());
			addView(mDownView, downParams);

			mDeleteView = new ImageView(context);
			mDeleteView.setOnClickListener(this);
			mDeleteView.setBackgroundResource(R.drawable.general_selector);
			mDeleteView.setImageResource(R.mipmap.delete);
			mDeleteView.setScaleType(ScaleType.CENTER);
			RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(
					height, height);
			deleteParams.addRule(RelativeLayout.LEFT_OF, mDownView.getId());
			addView(mDeleteView, deleteParams);
		}

		public void setName(String name) {
			mNameView.setText(name);
		}

		@Override
		public void onClick(View v) {
			if (v == mPlayView) {
//				String ip = Setting.getInstence().getIP();
//				HttpUtils.post(ip, HttpUtils.PORT,
//						HttpUtils.PATH_SCRIPTS_START, mNameView.getText()
//								.toString(), new RequestCallback() {
//							public void callback(Object data) {
//								showDebugLog(data.toString());
//							}
//						});
			}

		}

		public void showDebugLog(final String msg) {
			mHandler.post(new Runnable() {
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getContext()); // 先得到构造器
					builder.setTitle("LOG"); // 设置标题
					builder.setMessage(msg); // 设置内容
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() { // 设置确定按钮
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss(); // 关闭dialog
								}
							});
					builder.show();
				}
			});
		}

	}

}
