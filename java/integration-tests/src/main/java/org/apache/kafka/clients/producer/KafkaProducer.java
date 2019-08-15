package org.apache.kafka.clients.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.KafkaSpout;

import com.google.gson.Gson;
import com.tobprecise.demo.entities.dto.EntityDto;

public class KafkaProducer<K, V> implements Producer<K, V> {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private static ConcurrentHashMap<String, List<String>> producedStrings = new ConcurrentHashMap<String, List<String>>();
	private static ConcurrentHashMap<String, List<byte[]>> producedBytes = new ConcurrentHashMap<String, List<byte[]>>();
	private boolean isBytes;
	
    public KafkaProducer(Properties properties) {
    	isBytes = properties.getProperty("value.serializer", "").contains("ByteArraySerializer");
    }
    
	@Override
	public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
		return send(record, null);
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) {
		return executor.submit(new Callable<RecordMetadata>() {
			@Override
			public RecordMetadata call() {
				long offset = saveRecord(record);
				RecordMetadata metadata = new RecordMetadata(new TopicPartition(record.topic(), 0), offset, 0, 0, (long) 0, 0, 0);
				if (callback != null) {
					callback.onCompletion(metadata, null);
				}
				return metadata;
			}
		});
	}

	private long saveRecord(ProducerRecord<K, V> record) {
		String topic = record.topic();
		long offset;
		if (isBytes) {
			producedBytes.computeIfAbsent(topic, t -> new ArrayList<byte[]>());
			producedBytes.get(topic).add((byte[]) record.value());
			offset = producedBytes.get(topic).size();
		} else {
			producedStrings.computeIfAbsent(topic, t -> new ArrayList<String>());
			producedStrings.get(topic).add((String) record.value());
			offset = producedStrings.get(topic).size();
			KafkaSpout.produce(topic, (String) record.value());
		}
		return offset;
	}
	@Override
	public void flush() {
	}

	@Override
	public List<PartitionInfo> partitionsFor(String topic) {
		return null;
	}

	@Override
	public Map<MetricName, ? extends Metric> metrics() {
		return null;
	}

	@Override
	public void close() {
	}

	@Override
	public void close(long timeout, TimeUnit unit) {	
	}

	public static List<String> getProducedStrings(String topic) {
		return producedStrings.computeIfAbsent(topic, t -> new ArrayList<String>());
	}	
	
	public static List<byte[]> getProducedByteArrays(String topic) {
		return producedBytes.computeIfAbsent(topic, t -> new ArrayList<byte[]>());
	}
	
	public static List<EntityDto> getProducedEntities() {
		List<EntityDto> records = new ArrayList<EntityDto>();
		Gson gson = new Gson();
		for (String summaryJson : getProducedStrings("gpm.geneticresults.summary")) {
			records.add(gson.fromJson(summaryJson, EntityDto.class));
		}
		return records;
	}
	
	public static void clear() {
		producedStrings.clear();
		producedBytes.clear();
	}

}
