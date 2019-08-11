package com.tobprecise.demo.entities.clinical;

import java.util.Date;

public interface IClinicalAct {
	String getClinicalActId();
	void setClinicalActId(String clinicalActId);
	Date getStart();
	void setStart(Date Start);
	Long getAge();
	void setAge(Long age);
}
