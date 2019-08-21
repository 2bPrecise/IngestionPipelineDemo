package com.tobprecise.demo.entities.clinical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import com.tobprecise.demo.entities.clinical.Demography.Gender;
import com.tobprecise.demo.entities.dto.EntityDto;

public class DemographyBuilderTest {
	
	@Test
	public void BuildFullDemography() {
		EntityDto dto = new EntityDto();
		dto.patientid = "123";
		dto.givenname = "456";
		dto.familyname = "789";
		dto.gender = "Female";
		dto.dateofbirth = "1950-03-03";
		dto.isdeceased = "true";
		DemographyBuilder builder = new DemographyBuilder();
		Demography demo = builder.build(dto);
		assertEquals("123", demo.getPatientId());
		assertEquals("456", demo.getGivenName());
		assertEquals("789", demo.getFamilyName());
		assertEquals(Gender.FEMALE, demo.getGender());
		assertEquals("Fri Mar 03", demo.getDateOfBirth().toString().substring(0, 10));
		assertTrue(demo.getIsDeceased());		
	}
	

	@Test
	public void BuildDemographyDeceased() {
		EntityDto dto = new EntityDto();
		DemographyBuilder builder = new DemographyBuilder();

		dto.isdeceased = "TRue";
		assertTrue(builder.build(dto).getIsDeceased());

		dto.isdeceased = "FalSE";
		assertFalse(builder.build(dto).getIsDeceased());

		dto.isdeceased = "xxx";
		assertFalse(builder.build(dto).getIsDeceased());

		dto.isdeceased = "";
		assertFalse(builder.build(dto).getIsDeceased());

		dto.isdeceased = null;
		assertFalse(builder.build(dto).getIsDeceased());

	}
	
	@Test
	public void BuildDemographyGenders() {
		EntityDto dto = new EntityDto();
		DemographyBuilder builder = new DemographyBuilder();

		dto.gender = "MAle";
		assertEquals(Gender.MALE, builder.build(dto).getGender());

		dto.gender = "feMALE";
		assertEquals(Gender.FEMALE, builder.build(dto).getGender());
		
		dto.gender = "unKNown";
		assertEquals(Gender.UNKNOWN, builder.build(dto).getGender());

		dto.gender = "xxx";
		assertNull(builder.build(dto).getGender());
		
		dto.gender = "";
		assertNull(builder.build(dto).getGender());

		dto.gender = null;
		assertNull(builder.build(dto).getGender());
		


	}
	
	@Test
	public void BuildDemographyDates() {
		EntityDto dto = new EntityDto();
		DemographyBuilder builder = new DemographyBuilder();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		dto.dateofbirth = "1950-09-13";
		assertEquals("1950-09-1300:00:00", formatter.format(builder.build(dto).getDateOfBirth()));

		dto.dateofbirth = "20500323";
		assertEquals("2050-03-2300:00:00", formatter.format(builder.build(dto).getDateOfBirth()));
		
		dto.dateofbirth = "1950-09-13T16:09:02";
		assertEquals("1950-09-1316:09:02", formatter.format(builder.build(dto).getDateOfBirth()));

		dto.dateofbirth = "Tue, 3 Jun 2008 11:05:30 GMT";
		assertNull(builder.build(dto).getDateOfBirth());

		dto.dateofbirth = "1950-02-30";
		assertNull(builder.build(dto).getDateOfBirth());

		dto.dateofbirth = "xxxx";
		assertNull(builder.build(dto).getDateOfBirth());

		dto.dateofbirth = "";
		assertNull(builder.build(dto).getDateOfBirth());

		dto.dateofbirth = null;
		assertNull(builder.build(dto).getDateOfBirth());

	}
}
