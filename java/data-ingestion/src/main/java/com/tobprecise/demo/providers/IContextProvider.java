package com.tobprecise.demo.providers;

public interface IContextProvider {
	String initializeContext(String fileId, String fileName);
	void setExpectedItems(String contextId, int items);
	String contextIdWithItem(String contextId, int itemId);
	void finishItem(String contextAndItemId);
	Boolean isFinished(String contextId);
}
