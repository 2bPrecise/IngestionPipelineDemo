package com.tobprecise.demo.entities.clinical;

import com.tobprecise.demo.entities.dto.EntityDto;

public class DemographyBuilder extends ClinicalEntityBuilder<Demography> {

	@Override
	public Demography build(EntityDto dto) {
		Demography demography = new Demography();
		demography.setPatientId(dto.patientid);
		demography.setGivenName(dto.givenname);
		demography.setFamilyName(dto.familyname);
		demography.setGender(resolveEnum(Demography.Gender.class, dto.gender));
		demography.setDateOfBirth(resolveDate(dto.dateofbirth));
		demography.setIsDeceased(resolveBoolean(dto.isdeceased));
		return demography;
	}

}
