package com.tobprecise.demo.entities.clinical;

import java.util.Date;

public interface IClinicalAct extends IClinicalEntity {

	String getPatientId();
	void setPatientId(String patientId);

	String getClinicalActId();
	void setClinicalActId(String clinicalActId);

	Date getStart();
	void setStart(Date Start);
	
	Long getAge();
	void setAge(Long age);
}
