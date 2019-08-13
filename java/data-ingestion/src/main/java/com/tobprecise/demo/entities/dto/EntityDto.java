package com.tobprecise.demo.entities.dto;

import java.io.Serializable;

// all fields should be lower-case for easy deserializing
public class EntityDto implements Serializable {
	
	// indicate demography or medication
	public String type;
	
	// helper to carry the context between topologies
	public String contextid;

	// all records must have a patient id
	public String patientid;

	// Demography specific fields
	public String givenname;
	public String familyname;
	public String gender;
	public String dateofbirth;
	public String isdeceased;
	
	// Medication specific fields
	public String clinicalactid;
	public String rxnumber;
	public String start;
	public String end;
	public String status;
	// age is calculated according to start and date of birth
}
