package com.k1.graphcode.ui.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.k1.graphcode.R;
import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.project.Project;
import com.k1.graphcode.block.project.Project.CompleteListener;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.ui.views.OptionButton;
import com.k1.graphcode.ui.views.ScriptItemView;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;
import com.k1.graphcode.utils.Density;
/**
 * 
 * @author BinGe
 *	点击主页面中的project后进入该页面,显示一个project的所有scripts
 */
public class ProjectActivity extends BaseActivity implements CompleteListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String name = getIntent().getStringExtra("project_name");
		ProjectCache.getInstence().setCurrentProject(name);
		setContentView(R.layout.activity_project);

		Project project = ProjectCache.getInstence().getCurrentProject();
		project.initWidthFile(new CompleteListener() {
			public void complete(Project project) {
				initProject(project);
			}
		});

		TextView title = (TextView) findViewById(R.id.title);
		title.setText(name);
		FrameLayout titleLayout = (FrameLayout) findViewById(R.id.layout_title);
		OptionButton ob = new OptionButton(this, true, false);
		ob.setColor(Color.WHITE);
		ob.setBackClickListener(new OnClickListener() {
			public void onClick(View v) {
				ProjectCache.getInstence().getCurrentProject()
						.save(ProjectActivity.this);
				ProjectActivity.this.finishWithAnimationHorizontalOut();
			}
		});
		FrameLayout.LayoutParams obParams = new FrameLayout.LayoutParams(
				Density.dip2px(40), Density.dip2px(40), Gravity.LEFT
						| Gravity.CENTER);
		titleLayout.addView(ob, obParams);
		ob.setVisibility(View.GONE);
		findViewById(R.id.save).setVisibility(View.VISIBLE);
		findViewById(R.id.save).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ProjectCache.getInstence().getCurrentProject()
						.save(ProjectActivity.this);
			}
		});
		findViewById(R.id.xml).setVisibility(View.VISIBLE);
		findViewById(R.id.xml).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				gotoXmlActivity();
			}
		});
	}

	public void gotoXmlActivity() {
		runOnUiThread(new Runnable() {
			public void run() {
				Intent intent = new Intent(ProjectActivity.this,
						XMLActivity.class);
				intent.putExtra("project_name", ProjectCache.getInstence()
						.getCurrentProject().getName());
				ProjectActivity.this
						.openActivityWithAnimationHorizontalIn(intent);
			}
		});
	}

	@Override
	public void complete(Project project) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(ProjectActivity.this, "保存成功", Toast.LENGTH_SHORT)
						.show();
				finishWithAnimationHorizontalOut();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initProject(ProjectCache.getInstence().getCurrentProject());
	}

	private void initProject(final Project project) {
		runOnUiThread(new Runnable() {
			public void run() {
				initScriptList((LinearLayout) findViewById(R.id.main_layout),
						project.getMainScript());
				initScriptList((LinearLayout) findViewById(R.id.sub_layout),
						project.getSubScript());
				initScriptList(
						(LinearLayout) findViewById(R.id.trigger_layout),
						project.getTriggerScript());
			}
		});
	}

	private void initScriptList(LinearLayout root, List<Block> list) {
		root.removeAllViews();
		for (Block block : list) {
			ScriptItemView item = (ScriptItemView) root.findViewWithTag(block);
			if (item == null) {
				item = new ScriptItemView(this, block);
				root.addView(item, 0);
			}
		}

	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.add_main:
			CustomDialog.createEditDialog(this, "Script name","",
					new ReturnResults() {
						public void result(Object o) {
							Intent intent = new Intent(ProjectActivity.this,
									EditActivity.class);
							intent.putExtra("script_type", Const.EditType.MAIN);
							intent.putExtra("script_name", o.toString());
							openActivityWithAnimationHorizontalIn(intent);
						}
					}).show();
			break;
		case R.id.add_sub:
			CustomDialog.createEditDialog(this, "Script name","",
					new ReturnResults() {
						public void result(Object o) {
							Intent intent = new Intent(ProjectActivity.this,
									EditActivity.class);
							intent.putExtra("script_type", Const.EditType.SUB);
							intent.putExtra("script_name", o.toString());
							openActivityWithAnimationHorizontalIn(intent);
						}
					}).show();
			break;
		case R.id.add_trigger:
			CustomDialog.createEditDialog(this, "Script name","",
					new ReturnResults() {
						public void result(Object o) {
							Intent intent = new Intent(ProjectActivity.this,
									EditActivity.class);
							intent.putExtra("script_type",
									Const.EditType.TRIGGER);
							intent.putExtra("script_name", o.toString());
							openActivityWithAnimationHorizontalIn(intent);
						}
					}).show();
			break;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ProjectCache.getInstence().getCurrentProject().save(null);
			finishWithAnimationHorizontalOut();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}
