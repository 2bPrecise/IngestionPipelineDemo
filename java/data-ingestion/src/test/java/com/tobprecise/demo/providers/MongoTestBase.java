package com.tobprecise.demo.providers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.google.gson.Gson;
import com.tobprecise.demo.entities.clinical.Patient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class MongoTestBase {

	private static final MongodStarter starter = MongodStarter.getDefaultInstance();
	private static final int MONGO_PORT = 27027;
	protected static final String CONNECTION_STRING = String.format("mongodb://localhost:%d/clinical", MONGO_PORT);
	private static MongodExecutable mongodExe;
	private static MongodProcess mongod;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("DEBUG.MONGO", "true");
		
		IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.V4_0_2)
				.cmdOptions(new MongoCmdOptionsBuilder().useStorageEngine("mmapv1").verbose(true).build())
				.net(new Net(MONGO_PORT, Network.localhostIsIPv6())).build();

		mongodExe = starter.prepare(mongodConfig);

		mongod = mongodExe.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (mongod != null)
			mongod.stop();
		if (mongodExe != null)
			mongodExe.stop();
	}

	public String getConnectionString() {
		return CONNECTION_STRING;
	}

	public static void assertDeepEquals(String message, Object o1, Object o2) {
		assertNotSame(o1,  o2);
		if (o1 instanceof Patient) {
			((Patient)o1).setId(null);
		}
		if (o2 instanceof Patient) {
			((Patient)o2).setId(null);
		}
		Gson gson = new Gson();
		String s1 = gson.toJson(o1);
		String s2 = gson.toJson(o2);
		assertEquals("message", s1, s2);
	}

	public MongoTestBase() {
		super();
	}

}