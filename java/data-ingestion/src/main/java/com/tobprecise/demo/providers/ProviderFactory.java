package com.tobprecise.demo.providers;

import com.tobprecise.demo.config.AppConfig;

public class ProviderFactory {
	
	public static IPatientProvider getPatientProvider(AppConfig config) {
		return new MongoPatientProvider(config.mongoConnectionString);
	}
	
	public static IContextProvider getContextProvider(AppConfig config) {
		return new RedisContextProvider(config.redisHost, config.redisPort);
	}
	
	public static IFileProvider getFileProvider(AppConfig config) {
		return new DiskFileProvider(config.fileBasePath);
	}
	
	public static IEntityProducer getEntityRecordsProducer(AppConfig config) {
		return new KafkaEntityProducer(config.kafkaBroker, config.recordsTopic);
	}
}
