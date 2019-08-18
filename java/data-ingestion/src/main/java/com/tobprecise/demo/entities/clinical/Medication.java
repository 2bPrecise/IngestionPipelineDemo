package com.tobprecise.demo.entities.clinical;

import java.util.Date;

public class Medication implements IClinicalAct {
	private String patientId;
	private String clinicalActId;
	private String name;
	private String rxNumber;
	private Date start;
	private Date end;
	private Long age;
	private Status status;
	
	public enum Status { 
		Active, Inactive, EnteredInError
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getClinicalActId() {
		return clinicalActId;
	}

	public void setClinicalActId(String clinicalActId) {
		this.clinicalActId = clinicalActId;
	}

	public String getRxName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getRxNumber() {
		return rxNumber;
	}

	public void setRxNumber(String rxNumber) {
		this.rxNumber = rxNumber;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((clinicalActId == null) ? 0 : clinicalActId.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((patientId == null) ? 0 : patientId.hashCode());
		result = prime * result + ((rxNumber == null) ? 0 : rxNumber.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Medication other = (Medication) obj;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (clinicalActId == null) {
			if (other.clinicalActId != null)
				return false;
		} else if (!clinicalActId.equals(other.clinicalActId))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		if (rxNumber == null) {
			if (other.rxNumber != null)
				return false;
		} else if (!rxNumber.equals(other.rxNumber))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Medication [patientId=" + patientId + ", clinicalActId=" + clinicalActId + ", name=" + name
				+ ", rxNumber=" + rxNumber + ", start=" + start + ", end=" + end + ", age=" + age + ", status=" + status
				+ "]";
	}

}
