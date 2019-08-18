package com.tobprecise.demo.entities.clinical;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
	

	private static final List<DateTimeFormatter> formatters = Arrays.asList(
		DateTimeFormatter.RFC_1123_DATE_TIME, // Tue, 3 Jun 2008 11:05:30 GMT
		DateTimeFormatter.ISO_INSTANT,        // 2011-12-03T10:15:30Z
		DateTimeFormatter.ISO_LOCAL_DATE,     // 2011-12-03
		DateTimeFormatter.BASIC_ISO_DATE      // 20111203
	);
			
	protected Date resolveDate(String value) {
		value = trim(value);
		if (value == null) {
			return null;
		}
		for (DateTimeFormatter formatter: formatters) {
			try {
				return Date.from(formatter.parse(value, Instant::from));
			} catch (Exception e) {}
		}
		return null;
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
