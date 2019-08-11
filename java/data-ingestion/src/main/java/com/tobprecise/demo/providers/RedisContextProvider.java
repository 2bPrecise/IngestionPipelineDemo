package com.tobprecise.demo.providers;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.redis.common.container.JedisCommandsContainerBuilder;
import org.apache.storm.redis.common.container.JedisCommandsInstanceContainer;

public class RedisContextProvider implements IContextProvider {

	private JedisCommandsInstanceContainer container;

	public RedisContextProvider(String host, int port) {
		this(new JedisPoolConfig.Builder().setHost(host).setPort(port).build());
	}
	
	public RedisContextProvider(JedisPoolConfig jedisPoolConfig) {
		Objects.requireNonNull(jedisPoolConfig);
		this.container = JedisCommandsContainerBuilder.build(jedisPoolConfig);
	}
	
	public String initializeContext(String fileId, String fileName) {
		String contextId = UUID.randomUUID().toString();
		String key = contextKey(contextId);
		container.getInstance().hset(key, "fileId", fileId.toString());
		container.getInstance().hset(key, "fileName", fileName);
		String reversekey = contextKey(fileId);
		container.getInstance().hset(reversekey, "contextId", contextId);
		return contextId;
	}

	public void setExpectedItems(String contextId, int items) {
		String key = contextKey(contextId);
		container.getInstance().hincrBy(key, "items", items);
	}
	
	public void finishItem(String contextId, int itemId) {
		String key = contextKey(contextId);
		container.getInstance().hincrBy(key, Integer.toString(itemId), 1);		
	}

	public Boolean isFinished(String contextId) {
		String key = contextKey(contextId);
		Map<String, String> all = container.getInstance().hgetAll(key);
		int nItems = Integer.valueOf(all.get("items"));
		for (int i = 0; i < nItems; i++) {
			String itemId = Integer.toString(i);
			if (!all.containsKey(itemId) || "0".equals(all.get(itemId))) {
				return false;
			}
		}
		return true;
	}
	
	private String contextKey(String contextId) {
		return "context" + contextId.toString();
	}

}
