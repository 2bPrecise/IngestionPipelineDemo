package com.tobprecise.demo.providers;

import org.junit.Test;

public class RedisContextProviderTest {

	@Test
	public void TestProvider() {
		IContextProvider provider = new RedisContextProvider("127.0.0.1", 6379);
		String cid = provider.initializeContext("fid123", "fnameXXX");
		provider.setExpectedItems(cid, 5);
		String cid1 = provider.contextIdWithItem(cid, 1);
		String cid3 = provider.contextIdWithItem(cid, 3);
		provider.finishItem(cid1);
		provider.finishItem(cid3);
	}
}
