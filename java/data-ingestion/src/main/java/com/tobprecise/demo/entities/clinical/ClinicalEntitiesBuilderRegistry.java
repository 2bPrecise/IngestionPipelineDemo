package com.tobprecise.demo.entities.clinical;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClinicalEntitiesBuilderRegistry {
	
	private Map<String, ClinicalEntityBuilder> registry;
	
	public ClinicalEntitiesBuilderRegistry() {
		registry = new HashMap<String, ClinicalEntityBuilder>();
		registry.put("demography", new DemographyBuilder());
		registry.put("medication", new MedicationBuilder());
	}
	
	public ClinicalEntityBuilder get(String type) {
		return registry.get(type.trim().toLowerCase(Locale.ROOT));
	}
}
