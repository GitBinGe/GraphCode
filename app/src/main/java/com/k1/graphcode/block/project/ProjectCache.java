package com.k1.graphcode.block.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.k1.graphcode.utils.FileManager;

/**
 * 
 * project 的缓存类，当程序启动时会从xml读取数据并转换成project保存在这里，当用户重新编辑的数据也会更新到这里，
 * 退出时保存些缓存的数据到xml
 */
public class ProjectCache {

	private static ProjectCache sCache;

	public static ProjectCache getInstence() {
		if (sCache == null) {
			sCache = new ProjectCache();
		}
		return sCache;
	}

	private Map<String, Project> mProjectMap;
	private List<Project> mProjects;
	private String mProject = "test";

	private ProjectCache() {
		mProjects = new ArrayList<Project>();
		mProjectMap = new HashMap<String, Project>();
		// Saver.getInstence().initFromLocal(mProjectMap);
		initFromFile();
	}

	private void initFromFile() {
		File folder = new File(FileManager.getXmlPath());
		File[] files = folder.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				addProject(file);
			}
		}
	}

	public void renameProject(String oldName, String newName) {
		String newFile = FileManager.rename(oldName, newName);
		int index = mProjects.indexOf(mProjectMap.get(oldName));
		removeProject(oldName);
		addProject(new File(newFile), index);
	}

	public void addProject(File file) {
		String name = file.getName();
		if (name.endsWith(".xml")) {
			name = name.substring(0, name.length() - 4);
			Project project = new Project(file);
			mProjectMap.put(name, project);
			mProjects.add(project);
		}
	}

	public void addProject(File file, int index) {
		String name = file.getName();
		if (name.endsWith(".xml")) {
			name = name.substring(0, name.length() - 4);
			Project project = new Project(file);
			mProjectMap.put(name, project);
			mProjects.add(index, project);
		}
	}

	public List<Project> getProjects() {
		return mProjects;
	}

	public int getProjectCount() {
		return mProjectMap.size();
	}

	public void removeProject(String name) {
		if (mProjectMap.containsKey(name)) {
			Project project = mProjectMap.get(name);
			mProjects.remove(project);
			mProjectMap.remove(name);
			FileManager.removeXML(name);
		}
	}

	public Project getProject(String mame) {
		if (!mProjectMap.containsKey(mame)) {
			mProjectMap.put(mame, new Project(mame));
			mProjects.add(mProjectMap.get(mame));
		}
		return mProjectMap.get(mame);
	}

	public void setCurrentProject(String name) {
		mProject = name;
	}

	public Project getCurrentProject() {
		return getProject(mProject);
	}

}
