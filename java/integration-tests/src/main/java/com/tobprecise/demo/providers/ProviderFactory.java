package com.tobprecise.demo.providers;

import com.tobprecise.demo.config.AppConfig;

public class ProviderFactory {
	
	public static IPatientProvider mockPatientProvider;
	public static IContextProvider mockContextProvider;
	public static IFileProvider mockFileProvider;
	public static IEntityProducer mockEntityProducer;
	
	public static IPatientProvider getPatientProvider(AppConfig config) {
		if (mockPatientProvider != null) {
			return mockPatientProvider;
		}
		return new MongoPatientProvider(config.mongoConnectionString);
	}
	
	public static IContextProvider getContextProvider(AppConfig config) {
		if (mockContextProvider != null) {
			return mockContextProvider;
		}
		return new RedisContextProvider(config.redisHost, config.redisPort);
	}
	
	public static IFileProvider getFileProvider(AppConfig config) {
		if (mockFileProvider != null) {
			return mockFileProvider;
		}
		return new DiskFileProvider(config.fileBasePath);
	}
	
	public static IEntityProducer getEntityRecordsProducer(AppConfig config) {
		if (mockEntityProducer != null) {
			return mockEntityProducer;
		}
		return new KafkaEntityProducer(config.kafkaBroker, config.recordsTopic);
	}
}
