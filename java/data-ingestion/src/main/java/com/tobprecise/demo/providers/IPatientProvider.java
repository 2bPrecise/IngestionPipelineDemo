package com.tobprecise.demo.providers;

import com.tobprecise.demo.entities.clinical.Demography;
import com.tobprecise.demo.entities.clinical.Medication;
import com.tobprecise.demo.entities.clinical.Patient;

public interface IPatientProvider {
	Patient getPatient(String patientId);
	void savePatient(String patientId, Patient patient);
	void saveDemography(String patientId, Demography demography);
	void saveMedication(String patientId, Medication medication);
	boolean patientExists(String patientId);
}
