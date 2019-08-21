package com.tobprecise.demo.entities.clinical;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
	

	private static final List<SimpleDateFormat> formatters = createSimpleDateFormatList(Arrays.asList(
		"yyyy-MM-dd'T'HH:mm:ss", // ISO_INSTANT 2011-12-03T10:15:30Z
		"yyyy-MM-dd",          // ISO_LOCAL_DATE 2011-12-03
		"yyyyMMdd"             // BASIC_ISO_DATE 20111203
	));

	
	protected Date resolveDate(String value) {
		value = trim(value);
		if (value == null) {
			return null;
		}
		for (SimpleDateFormat formatter: formatters) {
			try {
				return formatter.parse(value);
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
	
	private static List<SimpleDateFormat> createSimpleDateFormatList(List<String> patterns) {
		ArrayList<SimpleDateFormat> formats = new ArrayList<SimpleDateFormat>();
		patterns.forEach((pattern) -> {
			formats.add(createSimpleDateFormat(pattern));
		});
		return formats;
	}
	private static SimpleDateFormat createSimpleDateFormat(String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		df.setLenient(false);
		df.setTimeZone(TimeZone.getTimeZone("UTC")); // should be tenant timezone
		return df;
	}
}
