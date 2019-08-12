package com.tobprecise.demo.providers;

import com.tobprecise.demo.entities.dto.EntityDto;

public interface IEntityProducer {
	void produce(EntityDto entity) throws Exception;
}
