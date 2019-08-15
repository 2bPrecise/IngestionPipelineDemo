package com.tobprecise.topologies;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.storm.utils.Utils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class TopologyTestBase  {	
	
	private Properties prop = new Properties();
	
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
		
		prop.setProperty("submit", "false");
	}
	
	
	protected final void runTopology(RunnableTopology topologyRunner, int timeoutSeconds) throws Exception {
		Utils.sleep(5000);
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
