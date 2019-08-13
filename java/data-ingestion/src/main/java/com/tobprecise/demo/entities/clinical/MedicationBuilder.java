package com.tobprecise.demo.entities.clinical;

import com.tobprecise.demo.entities.dto.EntityDto;

public class MedicationBuilder extends ClinicalEntityBuilder<Medication> {

	@Override
	public Medication build(EntityDto dto) {
		Medication medication = new Medication();
		medication.setPatientId(dto.patientid);
		medication.setClinicalActId(dto.clinicalactid);
		medication.setRxNumber(dto.rxnumber);
		medication.setStart(resolveDate(dto.start));
		medication.setEnd(resolveDate(dto.end));
		medication.setStatus(resolveEnum(Medication.Status.class, dto.status));
		return medication;
	}

}
