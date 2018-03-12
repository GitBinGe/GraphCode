package com.k1.graphcode.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.os.Environment;

public class FileManager {

	private static final String SEPARATOR = File.separator;
	private static final String K1_EXTERNAL_ROOT = "Graph";

	private static final String K1_EXTERNAL_STORAGE = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ SEPARATOR
			+ K1_EXTERNAL_ROOT;

	private static String XML = "xml";
	private static String CRASH = "crash";

	private static String getK1StoreExternalPath(String folder) {
		File file = new File(K1_EXTERNAL_STORAGE + SEPARATOR + folder);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	public static String getXmlPath() {
		return getK1StoreExternalPath(XML);
	}

	public static String copyXML(String name, String to) {
		String fromPath = getXmlPath() + "/" + name + ".xml";
		String toPath = getXmlPath() + "/" + to + ".xml";
		try {
			FileInputStream input = new FileInputStream(fromPath);
			BufferedInputStream inBuff = new BufferedInputStream(input);
			FileOutputStream output = new FileOutputStream(toPath);
			BufferedOutputStream outBuff = new BufferedOutputStream(output);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
			inBuff.close();
			outBuff.close();
			output.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return toPath;
	}

	public static String rename(String from, String to) {
		String fromPath = getXmlPath() + "/" + from + ".xml";
		String toPath = getXmlPath() + "/" + to + ".xml";
		File file = new File(fromPath);
		File newFile = new File(toPath);
		file.renameTo(newFile);
		return newFile.getAbsolutePath();
	}

	public static void removeXML(String name) {
		File file = new File(getXmlPath() + "/" + name + ".xml");
		file.delete();
	}

	public static String getCrashPath() {
		return getK1StoreExternalPath(CRASH);
	}

	public static void save(InputStream is) {
		// File folder = new File(getXmlPath());
	}

	public static void delete(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

}
