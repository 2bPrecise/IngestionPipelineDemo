package org.apache.storm.kafka.spout;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.utils.Utils;

public class KafkaSpout<K, V> extends BaseRichSpout {

	private SpoutOutputCollector collector;
	private String topic;
	private RecordTranslator<K, V> translator;
	KafkaSpoutConfig spoutConfig;
	
	private static ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> queue = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>>();
	private static ConcurrentHashMap<String, ConcurrentHashMap<Integer, ByteBuffer>> queueValues = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, ByteBuffer>>();
	
	public KafkaSpout(KafkaSpoutConfig spoutConfig)  {
		translator = spoutConfig.getTranslator();
		topic = spoutConfig.getTopicFilter().getTopicsString();
		this.spoutConfig = spoutConfig;
	}
		
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void close() {
	}

	@Override
	public void activate() {
	} 

	@Override
	public void deactivate() {
	}

	public void nextTuple() {
        Utils.sleep(100);
		try {
			ConcurrentLinkedQueue<Integer> topicQueue = queue.computeIfAbsent(topic, t -> new ConcurrentLinkedQueue<Integer>());
			ConcurrentHashMap<Integer, ByteBuffer> topicQueueValues = queueValues.computeIfAbsent(topic, t -> new ConcurrentHashMap<Integer, ByteBuffer>());
			Integer nextMessageId = topicQueue.remove();
			ByteBuffer value = topicQueueValues.get(nextMessageId);
			String valueStr = new String(value.array());
            final ConsumerRecord<String, String> record = new ConsumerRecord<String, String>(topic, 0, 0, null, valueStr);
            final List<Object> tuple = spoutConfig.getTranslator().apply(record);
        	collector.emit(tuple, nextMessageId);
		} catch (NoSuchElementException ex) {
		}
	}

	@Override
	public void ack(Object msgId) {
	}

	@Override
	public void fail(Object msgId) {
		ConcurrentLinkedQueue<Integer> topicQueue = queue.computeIfAbsent(topic, t -> new ConcurrentLinkedQueue<Integer>());
		topicQueue.add((Integer) msgId);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
   
        for (String stream : translator.streams()) {
            declarer.declareStream(stream, translator.getFieldsFor(stream));
        }
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	public static void produce(String topic, String msg) {
		produce(topic, ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
	}
		
	private static void produce(String topic, ByteBuffer msg) {
		Integer msgId =  (int) Math.round(Math.random() * 1000000);
		ConcurrentLinkedQueue<Integer> topicQueue = queue.computeIfAbsent(topic, t -> new ConcurrentLinkedQueue<Integer>());
		ConcurrentHashMap<Integer, ByteBuffer> topicQueueValues = queueValues.computeIfAbsent(topic, t -> new ConcurrentHashMap<Integer, ByteBuffer>());
		topicQueueValues.put(msgId, msg);
		topicQueue.add(msgId);
	}
	
	public static void clear(String topic) {
		ConcurrentLinkedQueue<Integer> topicQueue = queue.computeIfAbsent(topic, t -> new ConcurrentLinkedQueue<Integer>());
		ConcurrentHashMap<Integer, ByteBuffer> topicQueueValues = queueValues.computeIfAbsent(topic, t -> new ConcurrentHashMap<Integer, ByteBuffer>());
		topicQueueValues.clear();
		topicQueue.clear();
	}
}
