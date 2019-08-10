package com.tobprecise.demo.providers;

public interface IContextProvider {
	String initializeContext(String fileId, String fileName, int items);
	void finishItem(String contextId, int itemId);
	Boolean isFinished(String contextId);
}
