package com.tobprecise.demo.entities.clinical;

import com.tobprecise.demo.entities.dto.EntityDto;

public class MedicationBuilder extends ClinicalEntityBuilder<Medication> {

	@Override
	public Medication build(EntityDto dto) {
		Medication medication = new Medication();
		medication.setPatientId(dto.patientid);
		medication.setName(dto.medicationname);
		medication.setClinicalActId(dto.clinicalactid);
		medication.setRxNumber(dto.rxnumber);
		medication.setStart(resolveDate(dto.medicationstart));
		medication.setEnd(resolveDate(dto.medicationend));
		medication.setStatus(resolveEnum(Medication.Status.class, dto.medicationstatus));
		return medication;
	}

}
