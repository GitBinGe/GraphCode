package com.k1.graphcode.ui.activity;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ListView;

import com.k1.graphcode.BuildConfig;
import com.k1.graphcode.R;
import com.k1.graphcode.block.project.Project;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.ui.views.MenuPagerAdapter;
import com.k1.graphcode.ui.views.ProjectAdapter;
import com.k1.graphcode.ui.views.ProjectAdapter.onProjectClickListener;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;
import com.k1.graphcode.utils.FileManager;
/**
 * 
 * @author BinGe
 *	主要的activity,包括project,devices
 */
public class MainActivity extends BaseActivity implements
		onProjectClickListener, OnClickListener, TextWatcher {

	private ViewPager mViewPager;
	private ListView mProjects;
	private Handler mHandler;

	private EditText mSearchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler();
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		MenuPagerAdapter adapter = new MenuPagerAdapter(this);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(adapter);
		if (BuildConfig.DEBUG) {
			// mViewPager.setCurrentItem(1);
		}
		mProjects = (ListView) adapter.getProjectListView();
		mProjects.setAdapter(new ProjectAdapter(this));
		findViewById(R.id.projects).setOnClickListener(this);
		findViewById(R.id.devices).setOnClickListener(this);
		findViewById(R.id.settings).setOnClickListener(this);

		mSearchView = adapter.getSearchView();
		mSearchView.addTextChangedListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ProjectAdapter adapter = (ProjectAdapter) mProjects.getAdapter();
		adapter.notifyDataSetChanged();
	}

	public void updateConnectButton(boolean success) {
		MenuPagerAdapter adapter = (MenuPagerAdapter) mViewPager.getAdapter();
		adapter.updateConnectButton(success);
	}

	public void addXml(String name) {
		if (TextUtils.isEmpty(name)) {
			return;
		}
		// String projectName = name.replace(".xml", "");
		// ProjectCache.getInstence().removeProject(projectName);
		// Project project = ProjectCache.getInstence().getProject(projectName);
		// ProjectCache.getInstence().setCurrentProject(projectName);
		if (!name.endsWith(".xml")) {
			name += ".xml";
		}
		String path = FileManager.getXmlPath() + "/" + name;

		File file = new File(path);
		if (file.isFile()) {
			ProjectCache.getInstence().addProject(new File(path));
			// project.initWidthFile(file);
			ProjectAdapter adapter = (ProjectAdapter) mProjects.getAdapter();
			adapter.notifyDataSetChanged();
		}
		mViewPager.setCurrentItem(0, true);
	}

	public void showCopyDialog(final String oldName) {

		CustomDialog.createEditDialog(this, "Copy \"" + oldName + "\"", oldName,
				new ReturnResults() {
					public void result(Object o) {
						if (o != null) {
							if (!TextUtils.isEmpty(o.toString())) {
								copyXml(oldName, o.toString());
							}
						}
					}
				}).show();
	}
	
	public void showRenameDialog(final String oldName) {
		CustomDialog.createEditDialog(this, "Rename \"" + oldName + "\"", oldName,
				new ReturnResults() {
					public void result(Object o) {
						rename(oldName, o.toString());
					}
				}).show();
	}
	
	public void rename(final String oldName, final String newName) {
		new Thread() {
			public void run() {
				ProjectCache.getInstence().renameProject(oldName, newName);
				runOnUiThread(new Runnable() {
					public void run() {
						ProjectAdapter adapter = (ProjectAdapter) mProjects.getAdapter();
						adapter.notifyDataSetChanged();
					}
				});
			}
		}.start();
	}

	public void copyXml(final String oldName, final String newName) {
		new Thread() {
			public void run() {
				FileManager.copyXML(oldName, newName);
				runOnUiThread(new Runnable() {
					public void run() {
						addXml(newName);
					}
				});
			}
		}.start();
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.add:
			CustomDialog.createEditDialog(this, "Project Name","",
					new ReturnResults() {
						public void result(Object o) {
							Intent intent = new Intent(MainActivity.this,
									ProjectActivity.class);
							intent.putExtra("project_name", o.toString());
							openActivityWithAnimationHorizontalIn(intent);
						}
					}).show();
			break;
		case R.id.connect:
			MenuPagerAdapter adapter = (MenuPagerAdapter) mViewPager
					.getAdapter();
			adapter.getDevicesView().connect();
			v.setClickable(false);
			mHandler.postDelayed(new Runnable() {
				public void run() {
					View view = findViewById(R.id.connect);
					view.setClickable(true);
				}
			}, 1000);
			break;
		case R.id.ref:
			adapter = (MenuPagerAdapter) mViewPager.getAdapter();
			adapter.getDevicesView().update();
			break;
		case R.id.projects:
			mViewPager.setCurrentItem(0, true);
			break;
		case R.id.devices:
			mViewPager.setCurrentItem(1, true);
			// new Client();
			break;
		case R.id.settings:
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
			openActivityWithAnimationHorizontalIn(intent);
			break;
		}
	}

	@Override
	public void projectEdit(Project project) {
		Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
		intent.putExtra("project_name", project.getName());
		openActivityWithAnimationHorizontalIn(intent);
	}

	@Override
	public void onClick(View v) {
		click(v);
	}

	public void showLoading() {
		View v = findViewById(R.id.ref);
		if (v != null) {
			RotateAnimation r = new RotateAnimation(0, 359,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			r.setDuration(500);
			r.setRepeatCount(-1);
			v.startAnimation(r);
		}
	}

	public void stopLoading() {
		View v = findViewById(R.id.ref);
		if (v != null) {
			v.clearAnimation();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CustomDialog dialog = CustomDialog.createMessageDialog(this,
					R.string.comfirm_exit, new ReturnResults() {
						public void result(Object o) {
							if (o != null && o.equals("YES")) {
								finish();
							}
						}
					});
			dialog.show();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		ProjectAdapter adapter = (ProjectAdapter) mProjects.getAdapter();
		adapter.search(s == null ? "" : s.toString());
	}

}
