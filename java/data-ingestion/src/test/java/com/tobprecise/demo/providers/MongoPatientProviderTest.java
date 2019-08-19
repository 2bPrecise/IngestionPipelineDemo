package com.tobprecise.demo.providers;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.tobprecise.demo.entities.clinical.Demography;
import com.tobprecise.demo.entities.clinical.Patient;
import com.tobprecise.demo.entities.clinical.Demography.Gender;
import com.tobprecise.demo.entities.clinical.Medication;

public class MongoPatientProviderTest extends MongoTestBase {

	private IPatientProvider provider = new MongoPatientProvider(CONNECTION_STRING);

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		MongoClientURI clientURI = new MongoClientURI(CONNECTION_STRING);
		try (MongoClient client = new MongoClient(clientURI)) {
			MongoDatabase database = client.getDatabase(clientURI.getDatabase());
			DeleteResult result = database.getCollection("patients").deleteMany(new Document());
			assertTrue(result.wasAcknowledged());
		}
	}
	
	@Test
	public void testPatientExistsAndSave() {
		Patient patient0 = getMockPatient();
		assertFalse(provider.patientExists(patient0.getPatientId()));
		provider.savePatient(patient0.getPatientId(), patient0);
		assertTrue(provider.patientExists(patient0.getPatientId()));
		Patient patient1 = provider.getPatient(patient0.getPatientId());
		assertDeepEquals("Same patiet data", patient0, patient1);
	}
	
	@Test
	public void testSaveDemography() {
		Patient patient0 = getMockPatient();
		assertFalse(provider.patientExists(patient0.getPatientId()));
		provider.saveDemography(patient0.getPatientId(), patient0.getDemography());
		assertTrue(provider.patientExists(patient0.getPatientId()));
		Patient patient1 = provider.getPatient(patient0.getPatientId());
		patient0.setMedications(null);
		assertDeepEquals("Same patiet data", patient0, patient1);
	}

	@Test
	public void testSaveMedication() {
		Patient patient0 = getMockPatient();
		assertFalse(provider.patientExists(patient0.getPatientId()));
		provider.saveMedication(patient0.getPatientId(), patient0.getMedications().get(1));
		assertTrue(provider.patientExists(patient0.getPatientId()));
		Patient patient1 = provider.getPatient(patient0.getPatientId());
		patient0.setDemography(null);
		patient0.setMedications(Arrays.asList(patient0.getMedications().get(1)));
		assertDeepEquals("Same patiet data", patient0, patient1);
	}

	@Test
	public void testSaveDemographyMedications() {
		Patient patient0 = getMockPatient();
		assertFalse(provider.patientExists(patient0.getPatientId()));
		provider.saveDemography(patient0.getPatientId(), patient0.getDemography());
		provider.saveMedication(patient0.getPatientId(), patient0.getMedications().get(0));
		provider.saveMedication(patient0.getPatientId(), patient0.getMedications().get(1));
		assertTrue(provider.patientExists(patient0.getPatientId()));
		Patient patient1 = provider.getPatient(patient0.getPatientId());
		assertDeepEquals("Same patiet data", patient0, patient1);
	}
	
	private Patient getMockPatient() {
		Patient patient = new Patient();
		patient.setPatientId("123");
		Demography demography = new Demography();
		demography.setGivenName("qqq");
		demography.setFamilyName("www");
		demography.setGender(Gender.FEMALE);
		demography.setDateOfBirth(new Date());
		demography.setIsDeceased(false);
		demography.setPatientId("123");
		patient.setDemography(demography);
		Medication med1 = new Medication();
		med1.setPatientId("123");
		med1.setClinicalActId("123m1");
		med1.setAge(23L);
		med1.setStart(new Date(123456L));
		med1.setEnd(new Date(123466L));
		med1.setName("some med");
		med1.setRxNumber("456");
		Medication med2 = new Medication();
		med2.setPatientId("123");
		med2.setClinicalActId("123m2");
		med2.setAge(21L);
		med2.setStart(new Date(123356L));
		med2.setEnd(new Date(123366L));
		med2.setName("some med 2");
		med2.setRxNumber("567");
		patient.setMedications(Arrays.asList(med1, med2));
		return patient;
	}
}
