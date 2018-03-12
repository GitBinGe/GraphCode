package com.k1.graphcode.connect;

public interface RequestCallback {
	public void callback(ControllerData data);
	public void error(String info);
}

