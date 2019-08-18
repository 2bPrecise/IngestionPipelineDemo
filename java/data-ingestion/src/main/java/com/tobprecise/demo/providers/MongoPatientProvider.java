package com.tobprecise.demo.providers;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.tobprecise.demo.entities.clinical.Demography;
import com.tobprecise.demo.entities.clinical.Medication;
import com.tobprecise.demo.entities.clinical.Patient;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class MongoPatientProvider implements IPatientProvider {

	private Datastore datastore;

	public MongoPatientProvider(String uri) {
		this(new MongoClientURI(uri));
	}
	
	public MongoPatientProvider(MongoClientURI uri) {
		MongoClient mongoClient = new MongoClient(uri);
		final Morphia morphia = new Morphia();
		morphia.mapPackage("com.tobprecise.demo.entities.clinical");
		datastore = morphia.createDatastore(mongoClient, "xcgr");
		datastore.ensureIndexes();
	}
	
	public Patient getPatient(String patientId) {
		List<Patient> found = datastore.createQuery(Patient.class)
        .filter("patientId", patientId).find().toList();
		if (found.size() > 0) { 
			return found.get(0);
		}
		return null;
	}

	public void saveDemography(String patientId, Demography demography) {
		Patient patient = getPatient(patientId);
		if (patient == null) {
			return;
		}
		patient.setDemography(demography);
		datastore.save(patient);
	}

	public void saveMedication(String patientId, Medication medication) {
		Patient patient = getPatient(patientId);
		if (patient == null) {
			return;
		}
		if (patient.getMedications() == null) {
			patient.setMedications(new ArrayList<Medication>());
		}
		patient.getMedications().add(medication);
		datastore.save(patient);
	}

	public boolean patientExists(String patientId) {
		return 0 < datastore.createQuery(Patient.class)
		        .filter("patientId", patientId).count();
	}

	public void savePatient(String patientId, Patient patient) {
		datastore.save(patient);
	}

}
