package com.tobprecise.demo.providers;

public interface IContextProvider {
	String initializeContext(String fileId, String fileName);
	void setExpectedItems(String contextId, int items);
	void finishItem(String contextId, int itemId);
	Boolean isFinished(String contextId);
}
