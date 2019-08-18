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
	public String medicationname;
	public String rxnumber;
	public String medicationstart;
	public String medicationend;
	public String medicationstatus;
	// age is calculated according to start and date of birth
	
	@Override
	public String toString() {
		return "EntityDto [type=" + type + ", contextid=" + contextid + ", patientid=" + patientid + ", givenname="
				+ givenname + ", familyname=" + familyname + ", gender=" + gender + ", dateofbirth=" + dateofbirth
				+ ", isdeceased=" + isdeceased + ", clinicalactid=" + clinicalactid + ", medicationname="
				+ medicationname + ", rxnumber=" + rxnumber + ", medicationstart=" + medicationstart
				+ ", medicationend=" + medicationend + ", medicationstatus=" + medicationstatus + "]";
	}
	
}
