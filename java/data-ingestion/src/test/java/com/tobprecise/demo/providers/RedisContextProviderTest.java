package com.tobprecise.demo.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.RedisServer;

public class RedisContextProviderTest {
	private static final int REDIS_PORT = 16379;
	private static final String REDIS_HOST = "127.0.0.1";
	private static RedisServer redisServer;

	@Before
	public void setUp() throws Exception {
		redisServer = new RedisServer(REDIS_PORT);
		redisServer.start();
	}

	@After
	public void tearDown() throws Exception {
		if (redisServer != null) {
			redisServer.stop();
		}
	}

	@Test
	public void TestProvider() {
		IContextProvider provider = new RedisContextProvider(REDIS_HOST, REDIS_PORT);
		String cid = provider.initializeContext("fid123", "fnameXXX");
		provider.setExpectedItems(cid, 5);
		String cid1 = provider.contextIdWithItem(cid, 1);
		String cid3 = provider.contextIdWithItem(cid, 3);
		provider.finishItem(cid1);
		provider.finishItem(cid3);
		

		try (JedisPool jedisPool = new JedisPool(REDIS_HOST, REDIS_PORT);
				Jedis jedis = jedisPool.getResource()) {
			Map<String, String> context = jedis.hgetAll("context" + cid);
			assertNotNull(context);
			assertEquals("fnameXXX", context.get("fileName"));
			assertEquals("fid123", context.get("fileId"));
			assertEquals("5", context.get("items"));
			assertNull(context.get("0"));
			assertEquals("1", context.get("1"));
			assertNull(context.get("2"));
			assertEquals("1", context.get("3"));
			assertNull(context.get("4"));
		}
	}
}
