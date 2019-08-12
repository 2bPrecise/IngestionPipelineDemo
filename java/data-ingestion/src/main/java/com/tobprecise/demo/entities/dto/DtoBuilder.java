package com.tobprecise.demo.entities.dto;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;

public class DtoBuilder {

	public static EntityDto build(Map<String, String> map) {
		HashMap<String, String> mapCi = new HashMap<String, String>();
		for (String key: map.keySet()) {
			mapCi.put(key.toLowerCase(Locale.ROOT), map.get(key));
		}
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(mapCi), EntityDto.class);
	}
}
