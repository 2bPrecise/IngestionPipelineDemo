package com.tobprecise.demo.bolts;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
import org.mockito.Mockito;

import com.tobprecise.demo.bolts.MemoryOutputCollector;
import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.FileMetadata;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.providers.IContextProvider;
import com.tobprecise.demo.providers.ProviderFactory;

public class CsvParserBoltTest {

	private static final String CONTEXTID = "context123";
	
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
	    bw.write("giVEnName,FAmilyName,gender\naaa,bbb,ccc\naa1,bb1,cc1\naa2,bb2,cc2");
	    bw.close();

	    FileMetadata metadata = new FileMetadata();
	    metadata.setFileId("1234");
	    metadata.setOriginalName(temp.getName());
	    metadata.setRelativePath(temp.getAbsolutePath());
	    
		MkTupleParam params = new MkTupleParam();
		params.setFields(RecordScheme.CONTEXT_ID, RecordScheme.FILE_METADATA);
	    Tuple input = Testing.testTuple(new Values(CONTEXTID, metadata), params);

	    IContextProvider mockContext = Mockito.mock(IContextProvider.class);
	    ProviderFactory.mockContextProvider = mockContext;
	    
	    bolt.prepare(config, null, outputCollector);
	    
	    bolt.execute(input);
	    
	    verify(mockContext, times(1)).contextIdWithItem(CONTEXTID, 0);
	    verify(mockContext, times(1)).contextIdWithItem(CONTEXTID, 1);
	    verify(mockContext, times(1)).contextIdWithItem(CONTEXTID, 2);
	    verify(mockContext, times(1)).setExpectedItems(CONTEXTID, 3);

	    assertEquals(3, collector.getPendingCount());
	    {
		    Values emitted = ((Values) collector.values.get(0));
		    EntityDto resultDto = (EntityDto) emitted.get(1);	    
	    	assertEquals("aaa", resultDto.givenname);
	    	assertEquals("bbb", resultDto.familyname);
	    	assertEquals("ccc", resultDto.gender);
	    }
	    {
		    Values emitted = ((Values) collector.values.get(1));
		    EntityDto resultDto = (EntityDto) emitted.get(1);	    
	    	assertEquals("aa1", resultDto.givenname);
	    	assertEquals("bb1", resultDto.familyname);
	    	assertEquals("cc1", resultDto.gender);
	    }
	    {
		    Values emitted = ((Values) collector.values.get(2));
		    EntityDto resultDto = (EntityDto) emitted.get(1);	    
	    	assertEquals("aa2", resultDto.givenname);
	    	assertEquals("bb2", resultDto.familyname);
	    	assertEquals("cc2", resultDto.gender);
	    }
	}

}
