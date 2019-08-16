package com.tobprecise.demo.topologies.single;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.shade.org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.tobprecise.demo.entities.FileMetadata;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.providers.IContextProvider;
import com.tobprecise.demo.providers.IEntityProducer;
import com.tobprecise.demo.providers.IFileProvider;
import com.tobprecise.demo.providers.ProviderFactory;
import com.tobprecise.demo.topologies.ParserTopology;
import com.tobprecise.topologies.TopologyTestBase;

public class ParserTopologyTest extends TopologyTestBase {
	
	private static final String CsvFileId = UUID.randomUUID().toString();
	private static final String CsvFileName = UUID.randomUUID().toString() + ".csv";
	private static final String CsvContextId = UUID.randomUUID().toString();
	private static final String CsvContent = "givenname,familyname,gender\naa0,aa1,aa2\nbb0,bb1,bb2";
	
	private static final String JsonFileId = UUID.randomUUID().toString();
	private static final String JsonFileName = UUID.randomUUID().toString() + ".json";
	private static final String JsonContextId = UUID.randomUUID().toString();
	private static final String JsonContent = "[{\"givenname\":\"cc0\",\"familyname\":\"cc1\",\"gender\":\"cc2\"},{\"givenname\":\"dd0\",\"familyname\":\"dd1\",\"gender\":\"dd2\"}]";
	
	@Test
	public void runParserTopology() throws Exception {

		//
		//
		// simulate csv file from api
		//
		//
		IContextProvider mockContextProvider = Mockito.mock(IContextProvider.class);
		IFileProvider mockFileProvider = Mockito.mock(IFileProvider.class);
		IEntityProducer mockEntityProducer = Mockito.mock(IEntityProducer.class);
				
		ProviderFactory.mockContextProvider = mockContextProvider;
		ProviderFactory.mockFileProvider = mockFileProvider;
		ProviderFactory.mockEntityProducer = mockEntityProducer;
		
	    FileMetadata csvMetadata = new FileMetadata();
	    csvMetadata.setFileId(CsvFileId);
	    csvMetadata.setOriginalName(CsvFileName);
	    csvMetadata.setRelativePath("/");
	    
	    FileMetadata jsonMetadata = new FileMetadata();
	    jsonMetadata.setFileId(JsonFileId);
	    jsonMetadata.setOriginalName(JsonFileName);
	    jsonMetadata.setRelativePath("/");
	    
		when(mockContextProvider.initializeContext(CsvFileId, CsvFileName)).thenReturn(CsvContextId);
		when(mockContextProvider.initializeContext(JsonFileId, JsonFileName)).thenReturn(JsonContextId);

		when(mockFileProvider.download(csvMetadata)).thenReturn(IOUtils.toInputStream(CsvContent, Charset.defaultCharset()));
		when(mockFileProvider.download(jsonMetadata)).thenReturn(IOUtils.toInputStream(JsonContent, Charset.defaultCharset()));
	    
		when(mockEntityProducer.produce(any<EntityDto>());
		
		Gson gson = new Gson();
		KafkaSpout.produce("inbox", gson.toJson(csvMetadata));
		KafkaSpout.produce("inbox", gson.toJson(jsonMetadata));

		//
		//
		// run topology
		//
		//
		runTopology((args) -> ParserTopology.main(args), 30);
		
		
		//
		//
		// assert results
		//
		//
	    verify(mockContextProvider, times(1)).initializeContext(CsvFileId, CsvFileName);
	    verify(mockContextProvider, times(1)).initializeContext(JsonFileId, JsonFileName);
	    
	    verify(mockContextProvider, times(1)).contextIdWithItem(CsvContextId, 0);
	    verify(mockContextProvider, times(1)).contextIdWithItem(CsvContextId, 1);
	    
	    verify(mockContextProvider, times(1)).contextIdWithItem(JsonContextId, 0);
	    verify(mockContextProvider, times(1)).contextIdWithItem(JsonContextId, 1);
	    
	    verify(mockEntityProducer, times(4)).produce(isA(EntityDto.class));
	    List<EntityDto> results = producerArgs.getAllValues();
	    assertEquals(4, results.size());
	    Collections.sort(results, new Comparator<EntityDto>() {
	    	@Override
	    	public int compare(EntityDto a, EntityDto b) {
	    		return a.givenname.compareTo(b.givenname);
	    	}
	    });
	    assertEquals("aa0", results.get(0).givenname);
	    assertEquals("bb1", results.get(1).familyname);
	    assertEquals("cc2", results.get(2).gender);
	    assertEquals("dd1", results.get(2).familyname);
	}
	
}
