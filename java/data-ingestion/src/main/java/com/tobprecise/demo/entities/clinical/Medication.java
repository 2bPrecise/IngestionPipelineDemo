package com.tobprecise.demo.entities.clinical;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Medication implements IClinicalAct {
	//@Getter @Setter private String patientId;
	@Getter @Setter private String clinicalActId;
	@Getter @Setter private String rxNumber;
	@Getter @Setter private Date start;
	@Getter @Setter private Date end;
	@Getter @Setter private Long age;
	@Getter @Setter private Status status;
	
	public enum Status { 
		Active, Inactive, EnteredInError
	}
}
