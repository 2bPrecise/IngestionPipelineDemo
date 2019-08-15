package com.tobprecise.demo.config;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.storm.Config;
import org.apache.storm.utils.Utils;
import org.junit.Test;

public class AppConfigTest {

	@Test
	public void test() throws FileNotFoundException, IOException, IllegalArgumentException, IllegalAccessException {
		File tempFile = File.createTempFile("/tmp", "conf.yaml");
		FileUtils.writeStringToFile(tempFile, "redisPort: 32769\nsubmit: true\ninboxTopic: abcd", Charset.defaultCharset());
		AppConfig appConfig = AppConfigReader.read(tempFile.getPath());
		Config stormConfig = new Config();
		AppConfigReader.write(stormConfig, appConfig);
		stormConfig.setMessageTimeoutSecs(30);
		stormConfig.setDebug(true);
		stormConfig.setNumWorkers(1);	
		
		assertTrue(Utils.isValidConf(stormConfig));
		
		AppConfig appConfig2 = AppConfigReader.read(stormConfig);
		assertEquals(32769, appConfig2.redisPort);
		assertTrue(appConfig2.submit);
		assertEquals("abcd", appConfig2.inboxTopic);
	}

}
