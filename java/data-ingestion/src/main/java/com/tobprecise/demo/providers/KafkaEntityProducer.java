package com.tobprecise.demo.providers;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;
import com.tobprecise.demo.entities.dto.DtoSerializer;
import com.tobprecise.demo.entities.dto.EntityDto;

public class KafkaEntityProducer implements IEntityProducer {

	private KafkaProducer<String, EntityDto> _producer;
	private String _topic;

	public KafkaEntityProducer(String brokers, String topic) {
		Properties config = new Properties();		
		config.put(ProducerConfig.ACKS_CONFIG, "all");
		config.put(ProducerConfig.RETRIES_CONFIG, 2);
		config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		config.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DtoSerializer.class.getName());
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
		_producer = new KafkaProducer<String, EntityDto>(config);
		_topic = topic;
	}
	
	@Override
	public void produce(EntityDto entity) throws Exception {
		_producer.send(new ProducerRecord<String, EntityDto>(_topic, entity)).get();
	}

}
