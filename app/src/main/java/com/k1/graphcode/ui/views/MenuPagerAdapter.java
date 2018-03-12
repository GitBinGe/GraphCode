package com.k1.graphcode.ui.views;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.k1.graphcode.R;

public class MenuPagerAdapter extends PagerAdapter implements OnPageChangeListener{

	private final static int[] PAGERS = new int[] {
			R.layout.activity_main_projects, R.layout.activity_main_devices };
	private View[] mViews = new View[PAGERS.length];
	private Devices mDevices;
	
	public MenuPagerAdapter(Context context) {
		mDevices = new Devices(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		for (int i = 0; i < mViews.length; i++) {
			mViews[i] = inflater.inflate(PAGERS[i], null);
			if (PAGERS[i] == R.layout.activity_main_devices) {
				ViewGroup vg = (ViewGroup) mViews[i]
						.findViewById(R.id.devices_content);
				vg.addView(mDevices, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
			}
		}
	}

	public View getProjectListView() {
		return mViews[0].findViewById(R.id.list_projects);
	}
	
	public EditText getSearchView() {
		return (EditText) mViews[0].findViewById(R.id.search);
	}
	
	public Devices getDevicesView() {
		return mDevices;
	}
	
	public void updateConnectButton(boolean success) {
		ImageView connectView = (ImageView) mViews[1].findViewById(R.id.connect);
		if (success) {
			connectView.setImageResource(R.mipmap.connect_success);
		} else {
			connectView.setImageResource(R.mipmap.connect);
		}
	}

	@Override
	public int getCount() {
		return mViews.length;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView(mViews[position]);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(mViews[position]);
		return mViews[position];
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 == 0) {
//			mDevices.updateDevicesStatus();
			ListView lv = (ListView) getProjectListView();
			ProjectAdapter adapter = (ProjectAdapter)lv.getAdapter();
			adapter.notifyDataSetChanged();
		}
	}

}
