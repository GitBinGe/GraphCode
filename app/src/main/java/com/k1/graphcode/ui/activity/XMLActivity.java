package com.k1.graphcode.ui.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.k1.graphcode.R;
import com.k1.graphcode.block.project.Project;
import com.k1.graphcode.block.project.ProjectCache;
import com.k1.graphcode.ui.views.CustomScrollView;
/**
 * 
 * @author BinGe
 *	xml页面
 */
public class XMLActivity extends BaseActivity implements Runnable {

	private TextView mXmlText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xml);
		// mXmlText = (TextView) findViewById(R.id.text);

		ViewGroup vg = (ViewGroup) findViewById(R.id.xml_layout);
		mXmlText = new TextView(this);
		mXmlText.setTextColor(Color.WHITE);
		mXmlText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		mXmlText.setText("正在生成xml文件...");

		CustomScrollView scroll = new CustomScrollView(this);
		vg.addView(scroll);

		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		scroll.addView(mXmlText, lp);
		new Thread(this).start();
	}

	@Override
	public void run() {
		final Project project = ProjectCache.getInstence().getCurrentProject();
		project.save(null);
		runOnUiThread(new Runnable() {
			public void run() {
				TextView title = (TextView) findViewById(R.id.title);
				title.setText(project.getName());
			}
		});
		try {
			// String path = Saver.getInstence().createXml(project);
			String path = project.createXml();
			final StringBuffer sb = new StringBuffer();
			File file = new File(path);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			Paint paint = mXmlText.getPaint();
			int width = 0;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
				float w = paint.measureText(line);
				width = Math.max((int) w, width);
			}
			br.close();

			final int textWidth = width;
			runOnUiThread(new Runnable() {
				public void run() {
					FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) mXmlText
							.getLayoutParams();
					lp.width = textWidth;
					mXmlText.setLayoutParams(lp);
					mXmlText.setText(sb.toString());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			runOnUiThread(new Runnable() {
				public void run() {
					mXmlText.setText("生成xml失败");
				}
			});
		}
	}

}
