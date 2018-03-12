package com.k1.graphcode.ui.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.k1.graphcode.R;
import com.k1.graphcode.block.project.Project;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.connect.Controller;
import com.k1.graphcode.connect.LoadingCallback;
import com.k1.graphcode.ui.activity.MainActivity;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;

public class ProjectAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
	private ProjectCache mProjectCache;
	private onProjectClickListener mListener;
	private Handler mHandler;

	private String mSearchText;
	private List<Project> mProjects;

	public ProjectAdapter(Context context) {
		mContext = context;
		mHandler = new Handler(context.getMainLooper());
		mProjectCache = ProjectCache.getInstence();
		mListener = (onProjectClickListener) context;
		mProjects = new ArrayList<Project>();
	}

	public void search(String text) {
		mSearchText = text;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mProjects.size();
	}

	@Override
	public Project getItem(int position) {
		return mProjects.get(position);
	}

	@Override
	public void notifyDataSetChanged() {
		mProjects.clear();
		List<Project> list = new ArrayList<Project>();
		list.addAll(mProjectCache.getProjects());
		for (Project p : list) {
			if (!TextUtils.isEmpty(mSearchText)) {
				if (p.getName().toLowerCase()
						.contains(mSearchText.toLowerCase())) {
					mProjects.add(p);
				}
			} else {
				mProjects.add(p);
			}
		}
		super.notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHandler views = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_projects, null);
			convertView.setBackgroundResource(R.drawable.general_selector);
			views = new ViewHandler();
			convertView.setTag(views);
			views.name = (TextView) convertView.findViewById(R.id.name);
		} else {
			views = (ViewHandler) convertView.getTag();
		}

		Project project = getItem(position);
		views.name.setText(project.getName());

		if (mListener != null) {
			convertView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mListener.projectEdit(getItem(position));
				}
			});
		}

		View delete = convertView.findViewById(R.id.delete);
		delete.setTag(project.getName());
		delete.setOnClickListener(this);

		ImageView play = (ImageView) convertView.findViewById(R.id.upload);
		play.setTag(project.getName());
		play.setOnClickListener(this);
		if (Controller.getInstence().isConnected()) {
			play.setImageResource(R.mipmap.upload);
			play.setClickable(true);
		} else {
			play.setImageResource(R.mipmap.upload_unable);
			play.setClickable(false);
		}

		View copy = convertView.findViewById(R.id.copy);
		copy.setTag(project.getName());
		copy.setOnClickListener(this);
		
		View rename = convertView.findViewById(R.id.rename);
		rename.setTag(project.getName());
		rename.setOnClickListener(this);

		return convertView;
	}

	@Override
	public void onClick(View v) {
		final String name = (String) v.getTag();
		if (v.getId() == R.id.rename) {
			MainActivity ma = (MainActivity) mContext;
			ma.showRenameDialog(name);
		}
		if (v.getId() == R.id.delete) {
			CustomDialog dialog = CustomDialog.createMessageDialog(
					v.getContext(), R.string.comfirm_delete,
					new ReturnResults() {
						public void result(Object o) {
							if (o != null && o.equals("YES")) {
								ProjectCache.getInstence().removeProject(name);
								ProjectAdapter.this.notifyDataSetChanged();
							}
						}
					});
			dialog.show();
		} else if (v.getId() == R.id.upload) {
			boolean iscontains = false;
			try {
				List<String> xml = Controller.getInstence().getInfo()
						.getScripts();
				iscontains = xml.contains(name + ".xml");
			} catch (Exception e) {
			}
			if (!iscontains) {
				upload(name);
				return;
			}

			CustomDialog dialog = CustomDialog.createMessageDialog(
					v.getContext(), R.string.comfirm_overrite,
					new ReturnResults() {
						public void result(Object o) {
							if (o != null && o.equals("YES")) {
								upload(name);
							}
						}
					});
			dialog.show();
		} else if (v.getId() == R.id.copy) {
			MainActivity ma = (MainActivity) mContext;
			ma.showCopyDialog(v.getTag().toString());
		}
	}

	private void upload(String name) {
		showTips("正在上传");
		Controller.getInstence().add(name, new LoadingCallback() {
			public void progress(final int percentage) {
				mHandler.post(new Runnable() {
					public void run() {
						// progressBar.setProgress(percentage);
					}
				});
			}

			public void finish() {
				showTips("上传完成");
				mHandler.postDelayed(new Runnable() {
					public void run() {
						// dialog.cancel();
					}
				}, 200);
			}

			public void error() {
				mHandler.postDelayed(new Runnable() {
					public void run() {
						// dialog.cancel();
					}
				}, 100);
				showTips("上传失败");
			}
		});
	}

	public void showTips(final String info) {
		mHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(mContext, info, Toast.LENGTH_SHORT).show();
			}
		});
	}

	class ViewHandler {
		TextView name;
	}

	public interface onProjectClickListener {
		public void projectEdit(Project project);
	}

}
