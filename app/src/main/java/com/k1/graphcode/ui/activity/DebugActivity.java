package com.k1.graphcode.ui.activity;

import java.util.List;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.k1.graphcode.R;
import com.k1.graphcode.ui.views.Dialog;
import com.k1.graphcode.ui.views.Dialog.DialogCallback;
import com.k1.graphcode.utils.Setting;

public class DebugActivity extends BaseActivity implements OnClickListener {

	private List<String> mDebugs;
	private ListView mListView;
	private DebugAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		mDebugs = Setting.getInstence().getDebugList();
		mListView = (ListView) findViewById(R.id.debug_list);
		mAdapter = new DebugAdapter();
		mListView.setAdapter(mAdapter);
		findViewById(R.id.add).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final Dialog dialog = (Dialog) findViewById(R.id.dialog);
		dialog.showEditDialog("DEBUG KEY", InputType.TYPE_CLASS_TEXT,
				new DialogCallback() {
					public void dialogReturn(String value) {
						if (mDebugs.contains(value)) {
							Toast.makeText(DebugActivity.this, "已存在",
									Toast.LENGTH_SHORT).show();
							return;
						}
						mDebugs.add(value);
						Setting.getInstence().setDebugList(mDebugs);
						mAdapter.notifyDataSetChanged();
						dialog.cancelDialog();
					}
				});
	}

	class DebugAdapter extends BaseAdapter implements OnClickListener {

		@Override
		public int getCount() {
			return mDebugs.size();
		}

		@Override
		public String getItem(int position) {
			return mDebugs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(DebugActivity.this).inflate(
						R.layout.debug_item, null);
			}
			TextView nameView = (TextView) convertView.findViewById(R.id.name);

			String key = getItem(position);
			nameView.setText(key);

			View delete = convertView.findViewById(R.id.delete);
			delete.setTag(key);
			delete.setOnClickListener(this);

			return convertView;
		}

		@Override
		public void onClick(final View v) {
			final Dialog dialog = (Dialog) findViewById(R.id.dialog);
			dialog.showConfirmationDialog("DELETE", "删除 "
					+ v.getTag().toString() + "?", new DialogCallback() {
				public void dialogReturn(String value) {
					if (value != null && value.equals("YES")) {
						mDebugs.remove(v.getTag().toString());
						notifyDataSetChanged();
					}
					dialog.cancelDialog();
				}
			});
		}
	}

}
