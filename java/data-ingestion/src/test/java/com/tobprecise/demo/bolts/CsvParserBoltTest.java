package com.tobprecise.demo.bolts;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.Testing;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.testing.MkTupleParam;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.junit.Test;

import com.tobprecise.demo.bolts.MemoryOutputCollector;
import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.FileMetadata;
import com.tobprecise.demo.entities.dto.EntityDto;

public class CsvParserBoltTest {

	@Test
	public void test() throws IOException {
		CsvParserBolt bolt = new CsvParserBolt();
		
		MemoryOutputCollector collector = new MemoryOutputCollector();
		OutputCollector outputCollector = new OutputCollector(collector);

		Map<String, Object> config = new HashMap<String, Object>();
		AppConfig appConfig = new AppConfig();
		appConfig.fileBasePath = "/";
		AppConfigReader.write(config, appConfig);

		File temp = File.createTempFile("test1", ".csv"); 
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
	    bw.write("giVEnName,FAmilyName,gender\naaa,bbb,ccc\naa1,bb1,cc1");
	    bw.close();

	    FileMetadata metadata = new FileMetadata();
	    metadata.setFileId("1234");
	    metadata.setOriginalName(temp.getName());
	    metadata.setRelativePath(temp.getAbsolutePath());
	    
		MkTupleParam params = new MkTupleParam();
		params.setFields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD);
	    Tuple input = Testing.testTuple(new Values("context123", metadata), params);

	    bolt.prepare(config, null, outputCollector);
	    
	    bolt.execute(input);
	    
	    assertEquals(2, collector.values.size());

	    {
		    Values emitted = ((Values) collector.values.get(0));
		    String resultContext = (String) emitted.get(0);
		    EntityDto resultDto = (EntityDto) emitted.get(1);	    
	    	assertEquals("context123", resultContext);
	    	assertEquals("aaa", resultDto.givenname);
	    	assertEquals("bbb", resultDto.familyname);
	    	assertEquals("ccc", resultDto.gender);
	    }
	    {
		    Values emitted = ((Values) collector.values.get(1));
		    String resultContext = (String) emitted.get(0);
		    EntityDto resultDto = (EntityDto) emitted.get(1);	    
	    	assertEquals("context123", resultContext);
	    	assertEquals("aa1", resultDto.givenname);
	    	assertEquals("bb1", resultDto.familyname);
	    	assertEquals("cc1", resultDto.gender);
	    }
	}

}
