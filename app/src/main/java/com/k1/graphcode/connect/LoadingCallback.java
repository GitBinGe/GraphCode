package com.k1.graphcode.connect;

public interface LoadingCallback {
	public void progress(int percentage);
	public void finish();
	public void error();
}

