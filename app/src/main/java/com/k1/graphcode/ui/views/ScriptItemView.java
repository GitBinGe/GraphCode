package com.k1.graphcode.ui.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.k1.graphcode.R;
import com.k1.graphcode.block.Block;
import com.k1.graphcode.block.control.BlockMain;
import com.k1.graphcode.block.control.BlockSubProgram;
import com.k1.graphcode.block.control.BlockTrigger;
import com.k1.graphcode.block.project.Project;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.ui.activity.BaseActivity;
import com.k1.graphcode.ui.activity.EditActivity;
import com.k1.graphcode.ui.views.dialog.CustomDialog;
import com.k1.graphcode.ui.views.dialog.CustomDialog.ReturnResults;

public class ScriptItemView extends FrameLayout implements OnClickListener {

	private TextView mNameText;
	private View mDelete;
	private View mRename;

	public ScriptItemView(final Context context, Block block) {
		super(context);
		setOnClickListener(this);
		LayoutInflater.from(context).inflate(R.layout.item_script, this);
		mNameText = (TextView) findViewById(R.id.name);
		mDelete = findViewById(R.id.delete);

		mDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CustomDialog.createMessageDialog(getContext(),
						R.string.comfirm_delete, new ReturnResults() {
							public void result(Object o) {
								if (o.equals("YES")) {
									Block block = (Block) ScriptItemView.this
											.getTag();
									Object obj = block;
									Project project = ProjectCache
											.getInstence().getCurrentProject();
									if (obj instanceof BlockMain) {
										project.removeBlock(
												Const.EditType.MAIN, block);
									} else if (obj instanceof BlockSubProgram) {
										project.removeBlock(Const.EditType.SUB,
												block);
									} else if (obj instanceof BlockTrigger) {
										project.removeBlock(
												Const.EditType.TRIGGER, block);
									}
									ViewGroup vg = (ViewGroup) ScriptItemView.this
											.getParent();
									vg.removeView(ScriptItemView.this);
								}
							}
						}).show();
			}
		});
		mRename = findViewById(R.id.rename);
		mRename.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CustomDialog.createEditDialog(getContext(), "Rename script",
						mNameText.getText().toString(), new ReturnResults() {
							public void result(Object o) {
								Block block = (Block) ScriptItemView.this
										.getTag();
								Project project = ProjectCache.getInstence()
										.getCurrentProject();
								project.remaneBlock(block, o.toString());
								update(block);
							}
						}).show();
			}
		});
		update(block);
	}

	public void update(Block block) {
		setTag(block);
		mNameText.setText(block.getName());
	}

	@Override
	public void onClick(View v) {
		Block block = (Block) getTag();
		BaseActivity ba = (BaseActivity) v.getContext();
		Intent intent = new Intent(ba, EditActivity.class);
		intent.putExtra("script_name", block.getName());
		Object obj = block;
		if (obj instanceof BlockMain) {
			intent.putExtra("script_type", Const.EditType.MAIN);
		} else if (obj instanceof BlockSubProgram) {
			intent.putExtra("script_type", Const.EditType.SUB);
		} else if (obj instanceof BlockTrigger) {
			intent.putExtra("script_type", Const.EditType.TRIGGER);
		}
		ba.openActivityWithAnimationHorizontalIn(intent);
	}

}
