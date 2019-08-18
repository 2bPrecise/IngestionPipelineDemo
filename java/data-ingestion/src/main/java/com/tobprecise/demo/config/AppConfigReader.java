package com.tobprecise.demo.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.KafkaSpoutConfig.ProcessingGuarantee;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import static java.lang.Math.toIntExact;

public class AppConfigReader {
	
	private static final Logger Log = LoggerFactory.getLogger(AppConfigReader.class);

	public static AppConfig read(String fileName) throws FileNotFoundException, IOException {
		Yaml yaml = new Yaml(new Constructor(AppConfig.class));
		try (InputStream appConfigYaml = new FileInputStream(fileName)) {
			return (AppConfig) yaml.load(appConfigYaml);
		}
	}
	
	public static AppConfig read(Map<String, Object> stormConfig) {
		AppConfig appConfig = new AppConfig();
		for (Field field : AppConfig.class.getDeclaredFields()) {
			try {
				Object value = stormConfig.get("app." + field.getName());
				if (field.getType() == int.class && (value instanceof Long)) { 
					field.set(appConfig, toIntExact((long)value));
				} else {
					field.set(appConfig, value);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				Log.error(field.getName(), e);
			}
		}
		return appConfig;
	}
	
	public static void write(Map<String, Object> stormConfig, AppConfig appConfig) {
		for (Field field : AppConfig.class.getDeclaredFields()) {
			try {
				stormConfig.put("app." + field.getName(), field.get(appConfig));
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
		}
	}
	
	public static KafkaSpoutConfig createKafkaSpoutConfig(AppConfig appConfig, String topic, Class valueDeserializer) {
		KafkaSpoutRetryService retryService = new KafkaSpoutRetryExponentialBackoff(
				TimeInterval.milliSeconds(10),
				TimeInterval.milliSeconds(10),
				20,
				TimeInterval.milliSeconds(5000)
				);
		KafkaSpoutConfig<String, String> spoutConfig = KafkaSpoutConfig.builder(appConfig.kafkaBroker, topic)
				.setRetry(retryService)
				.setFirstPollOffsetStrategy(FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST)
				.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE)
				.setProp(ConsumerConfig.GROUP_ID_CONFIG, "storm." + topic)
	            .setProp(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer)
				.build();
		return spoutConfig;
	}
}
