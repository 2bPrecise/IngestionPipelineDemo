package com.tobprecise.demo.entities.clinical;

import java.time.Instant;
import java.util.Date;

import com.tobprecise.demo.entities.dto.EntityDto;

public abstract class ClinicalEntityBuilder<T extends IClinicalEntity> {
	public abstract T build(EntityDto dto);
	
	protected <E extends Enum<?>> E resolveEnum(Class<E> enumeration, String value) {
		value = trim(value);
		if (value == null) {
			return null;
		}
		for (E each : enumeration.getEnumConstants()) {
			if (each.name().compareToIgnoreCase(value) == 0) {
				return each;
			}
		}
		return null;
	}
	
	protected Date resolveDate(String value) {
		value = trim(value);
		if (value == null) {
			return null;
		}
		Instant instant = Instant.parse(value);
		return Date.from(instant);
	}
	
	protected Boolean resolveBoolean(String value) {
		value = trim(value);
		return Boolean.valueOf(value);
	}
	
	private String trim(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return value.trim();
	}
}
