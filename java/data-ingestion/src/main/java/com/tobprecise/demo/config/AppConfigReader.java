package com.tobprecise.demo.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.KafkaSpoutConfig.ProcessingGuarantee;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class AppConfigReader {
	
	public static AppConfig read(String fileName) throws FileNotFoundException, IOException {
		Yaml yaml = new Yaml(new Constructor(AppConfig.class));
		try (InputStream appConfigYaml = new FileInputStream(fileName)) {
			return (AppConfig) yaml.load(appConfigYaml);
		}
	}
	
	public static AppConfig read(Map<String, Object> stormConfig) {
		return (AppConfig) stormConfig.get("app");
	}
	public static void write(Map<String, Object> stormConfig, AppConfig appConfig) {
		stormConfig.put("app", appConfig);
	}
	
	public static KafkaSpoutConfig createKafkaSpoutConfig(AppConfig appConfig, String topic) {
		KafkaSpoutRetryService retryService = new KafkaSpoutRetryExponentialBackoff(
				TimeInterval.milliSeconds(10),
				TimeInterval.milliSeconds(10),
				20,
				TimeInterval.milliSeconds(5000)
				);
		return KafkaSpoutConfig.builder(appConfig.kafkaBroker, topic)
				.setRetry(retryService)
				.setFirstPollOffsetStrategy(FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST)
				.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE)
				.setProp(ConsumerConfig.GROUP_ID_CONFIG, "storm." + topic)
				.build();
	}
}
