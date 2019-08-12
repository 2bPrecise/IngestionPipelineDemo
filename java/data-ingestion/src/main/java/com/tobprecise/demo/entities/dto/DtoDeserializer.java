package com.tobprecise.demo.entities.dto;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

public class DtoDeserializer implements Deserializer<EntityDto> {

	private Kryo kryo = new Kryo();

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
	    kryo.register(EntityDto.class);
	}

	@Override
	public EntityDto deserialize(String topic, byte[] data) {
		Input input = new Input(data);
		EntityDto result = kryo.readObject(input, EntityDto.class);
		input.close();
		return result;
	}

	@Override
	public void close() {
	}

}
