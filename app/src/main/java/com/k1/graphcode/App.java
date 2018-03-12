package com.k1.graphcode;

import android.app.Application;

import com.k1.graphcode.block.project.Saver;
import com.k1.graphcode.connect.Controller;
import com.k1.graphcode.constant.Const;
import com.k1.graphcode.utils.CrashHandler;
import com.k1.graphcode.utils.Density;
import com.k1.graphcode.utils.ImageUtils;
import com.k1.graphcode.utils.Setting;

/**
 * 
 * @author BinGe app启动时执行些onCreate（），对整个app中需要先初始化的数据进行初始化
 */
public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//分辨率
		Density.init(this);
		//设置项
		Setting.newInstence(this);
		//常量
		Const.init(this);
		//保存工具
		Saver.newInstence(this);
		//图片工具类
		ImageUtils.newInstence(this);
		//控制器，单例实例化
		Controller.newInstence(this);
		//crash保存工具类
		CrashHandler.getInstance().init(this);
	}

}
