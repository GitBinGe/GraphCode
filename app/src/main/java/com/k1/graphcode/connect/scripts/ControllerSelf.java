package com.k1.graphcode.connect.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.k1.graphcode.R;
import com.k1.graphcode.connect.Controller;
import com.k1.graphcode.connect.ControllerData;
import com.k1.graphcode.connect.LoadingCallback;
import com.k1.graphcode.connect.RequestCallback;
import com.k1.graphcode.ui.activity.MainActivity;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;
import com.k1.graphcode.utils.FileManager;
import com.k1.graphcode.utils.Setting;

public class ControllerSelf extends Scripts implements TextWatcher {

	private List<String> mScriptsFile;
	private String mRunningScript;
	private boolean mIsScriptRunning;
	private ListView mListView;
	private ScriptsListAdapter mAdapter;
	private String mSearch;

	public ControllerSelf(JSONObject json) {
		super(json);
	}

	@Override
	public void setJson(JSONObject json) {
		try {
			if (mScriptsFile == null) {
				mScriptsFile = new ArrayList<String>();
			}
			mScriptsFile.clear();
			JSONArray scripts = json.getJSONArray("files");
			if (scripts != null) {
				for (int i = 0; i < scripts.length(); i++) {
					String script = scripts.getString(i);
					mScriptsFile.add(script);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		mRunningScript = null;
		try {
			mRunningScript = json.getString("running_script");
		} catch (JSONException e) {
			mRunningScript = null;
		}

		mIsScriptRunning = false;
		try {
			String run = json.getString("running");
			if (run != null && run.toLowerCase().equals("true")) {
				mIsScriptRunning = true;
			} else {
				mIsScriptRunning = false;
			}
		} catch (JSONException e) {
		}
	}

	@Override
	public View initView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.script_self,
				null);
		mListView = (ListView) view.findViewById(R.id.list);
		mAdapter = new ScriptsListAdapter();
		mListView.setAdapter(mAdapter);
		EditText search = (EditText) view.findViewById(R.id.search);
		search.addTextChangedListener(this);
		return view;
	}

	@Override
	public void update() {
		View view = getView();
		if (view != null) {
			String ip = Setting.getInstence().getIP();
			String ssid = Setting.getInstence().getSSID();
			TextView name = (TextView) view.findViewById(R.id.name);
			name.setText("IP : " + ip);
			TextView port = (TextView) view.findViewById(R.id.port);
			port.setText("SSID : " + ssid);

			TextView current = (TextView) view.findViewById(R.id.current);
			if (mIsScriptRunning) {
				current.setText("RUNNING : " + getRunningScript());
			} else {
				current.setText("RUNNING : N/A");
			}

			mAdapter.notifyDataSetChanged();
		}
	}

	public boolean isScriptRunning() {
		return mIsScriptRunning;
	}

	public String getRunningScript() {
		return mRunningScript == null ? "" : mRunningScript;
	}

	public List<String> getScriptsFile() {
		return mScriptsFile;
	}

	class ScriptsListAdapter extends BaseAdapter implements OnClickListener {

		private List<String> mScripts;

		public ScriptsListAdapter() {
			mScripts = new ArrayList<String>();
		}

		@Override
		public int getCount() {
			return mScripts.size();
			// return mScriptsFile == null ? 0 : mScriptsFile.size();
		}

		@Override
		public void notifyDataSetChanged() {
			mScripts.clear();
			List<String> list = new ArrayList<String>();
			list.addAll(mScriptsFile);
			for (String name : list) {
				if (TextUtils.isEmpty(mSearch)) {
					mScripts.add(name);
				} else {
					if (name.toLowerCase().contains(mSearch.toLowerCase())) {
						mScripts.add(name);
					}
				}
			}
			super.notifyDataSetChanged();
		}

		@Override
		public String getItem(int position) {
			return mScripts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mListView.getContext())
						.inflate(R.layout.view_info_item, null);
			}
			TextView nameView = (TextView) convertView.findViewById(R.id.name);

			String scripts = getItem(position);
			nameView.setText(scripts.replaceAll(".xml", ""));
			convertView.findViewById(R.id.layout_button).setVisibility(
					View.VISIBLE);

			View delete = convertView.findViewById(R.id.delete);
			delete.setTag(scripts);
			delete.setOnClickListener(this);
			View download = convertView.findViewById(R.id.download);
			download.setTag(scripts);
			download.setOnClickListener(this);
			View play = convertView.findViewById(R.id.play);
			play.setTag(scripts);
			play.setOnClickListener(this);
			View stop = convertView.findViewById(R.id.stop);
			stop.setTag(scripts);
			stop.setOnClickListener(this);
			if (mIsScriptRunning
					&& !TextUtils.isEmpty(mRunningScript)
					&& (mRunningScript.contains(scripts) || scripts
							.contains(mRunningScript))) {
				stop.setVisibility(View.VISIBLE);
				play.setVisibility(View.GONE);
			} else {
				stop.setVisibility(View.GONE);
				play.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			switch (v.getId()) {
			case R.id.delete:
				if (tag != null) {
					final String name = tag.toString();
					CustomDialog.createMessageDialog(v.getContext(),
							R.string.comfirm_delete, new ReturnResults() {
								public void result(Object o) {
									if (o != null && o.equals("YES")) {
										Controller.getInstence().delete(name,
												new RequestCallback() {
													public void error(String e) {
														showTips("Delete failed!");
													}

													public void callback(
															ControllerData data) {
														showTips("Delete successful!");
													}
												});
									}
								}
							}).show();
				}
				break;
			case R.id.download:
				if (tag != null) {
					final String name = tag.toString();
					boolean iscontains = false;
					try {
						File file = new File(FileManager.getXmlPath());
						File[] files = file.listFiles();
						for (File xml : files) {
							if (xml.getName().equals(name)) {
								iscontains = true;
								break;
							}
						}
					} catch (Exception e) {
					}
					if (!iscontains) {
						dwonload(name);
						return;
					}
					CustomDialog.createMessageDialog(v.getContext(),
							R.string.comfirm_overrite, new ReturnResults() {
								public void result(Object o) {
									if (o != null && o.equals("YES")) {
										dwonload(name);
									}
								}
							}).show();
				}
				break;
			case R.id.play:
				if (tag != null) {
					String name = tag.toString();
					Controller.getInstence().start(name, new RequestCallback() {
						public void error(String e) {
							// showTips("Start failed!");
						}

						public void callback(ControllerData data) {
							// showTips("Start successful!");
						}
					});
				}
				break;
			case R.id.stop:
				if (tag != null) {
					// showTips("Stopping...");
					String name = tag.toString();
					Controller.getInstence().stop(name, new RequestCallback() {
						public void error(String e) {
							// showTips("Stop failed!");
						}

						public void callback(ControllerData data) {
							// showTips("Stop successful!");
						}
					});
				}
				break;
			}
		}

		private void dwonload(final String name) {
			final AlertDialog dialog = new AlertDialog.Builder(
					mListView.getContext()).create();
			dialog.show();
			dialog.getWindow().setContentView(R.layout.loading);
			TextView title = (TextView) dialog.findViewById(R.id.title);
			title.setText("Downloading...");
			dialog.findViewById(R.id.ok).setOnClickListener(
					new OnClickListener() {
						public void onClick(View v) {
							dialog.cancel();
						}
					});
			final ProgressBar progressBar = (ProgressBar) dialog
					.findViewById(R.id.progress);
			Controller.getInstence().download(name, new LoadingCallback() {
				public void progress(final int percentage) {
					mListView.getHandler().post(new Runnable() {
						public void run() {
							progressBar.setProgress(percentage);
						}
					});
				}

				public void finish() {
					showTips("Download successful!");
					mListView.getHandler().postDelayed(new Runnable() {
						public void run() {
							MainActivity ma = (MainActivity) mListView
									.getContext();
							ma.addXml(name);
							dialog.cancel();
						}
					}, 200);
				}

				public void error() {
					mListView.getHandler().postDelayed(new Runnable() {
						public void run() {
							dialog.cancel();
						}
					}, 100);
					showTips("Download failed!");
				}
			});
		}

		public void showTips(final String info) {
			mListView.getHandler().post(new Runnable() {
				public void run() {
					Toast.makeText(mListView.getContext(), info,
							Toast.LENGTH_SHORT).show();
				}
			});
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		mSearch = TextUtils.isEmpty(s) ? "" : s.toString();
		mAdapter.notifyDataSetChanged();
	}

}
