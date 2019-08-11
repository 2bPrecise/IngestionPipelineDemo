package com.tobprecise.demo.config;

import java.io.Serializable;

public class AppConfig implements Serializable {
	public String fileBasePath;
	public String kafkaBroker;
	public String inboxTopic;
	public String recordsTopic;
	public String mongoConnectionString;
	public String redisHost;
	public int redisPort;
	public boolean submit;
}
