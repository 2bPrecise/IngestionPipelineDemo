package com.tobprecise.topologies;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public abstract class TopologyTestBase  {	
	
	private static MongodProcess mongod;
	private static MongodExecutable mongodExe;

	protected Properties prop = new Properties();
	
	@Rule
	public TemporaryFolder lakeFolder = new TemporaryFolder();
	@Rule
	public TemporaryFolder configFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		prop.setProperty("fileBasePath", lakeFolder.getRoot().toString());
		prop.setProperty("inboxTopic", "inbox");
		prop.setProperty("recordsTopic", "records");
		prop.setProperty("kafkaBroker", "local");
		prop.setProperty("mongoConnectionString", "mongodb://localhost:27027/clinical");
		prop.setProperty("submit", "false");
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("DEBUG.MONGO", "true");
		
		IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.V4_0_2)
				.cmdOptions(new MongoCmdOptionsBuilder().useStorageEngine("mmapv1").verbose(true).build())
				.net(new Net(27027, Network.localhostIsIPv6())).build();

		mongodExe = MongodStarter.getDefaultInstance().prepare(mongodConfig);
		mongod = mongodExe.start();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (mongod != null) {
			mongod.stop();
		}
		if (mongodExe != null) {
			mongodExe.stop();
		}
	}
	
	protected final void runTopology(RunnableTopology topologyRunner, int timeoutSeconds) throws Exception {
		prop.setProperty("localRunForSeconds", Integer.toString(timeoutSeconds));
		// write modified properties file - which will be loaded by topology
		File configFile = configFolder.newFile();
		try (FileWriter writer = new FileWriter(configFile)) {
			PrintWriter out = new PrintWriter(writer);
	        for (Object key : prop.keySet()) {
	            Object val = prop.get(key);
	            out.println(key + ": " + val);
	        }
		} catch (Exception e) {
			fail(e.getMessage());
			return;
		}
        String configFileFullPath = configFile.getAbsolutePath();
        topologyRunner.apply(new String[] {configFileFullPath});
	}
		
}
