package com.tobprecise.demo.entities.dto;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

public class DtoSerializer implements Serializer<EntityDto> {

	private Kryo kryo = new Kryo();

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	    kryo.register(EntityDto.class);
	}

	@Override
	public byte[] serialize(String topic, EntityDto data) {
		Output output = new Output();
		kryo.writeObject(output, data);
		byte[] result = output.getBuffer();
		output.close();
		return result;
	}

	@Override
	public void close() {
	}

}
