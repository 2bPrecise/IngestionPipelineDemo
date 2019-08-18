package com.tobprecise.demo.bolts;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.FileMetadata;
import com.tobprecise.demo.entities.dto.DtoBuilder;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.providers.IContextProvider;
import com.tobprecise.demo.providers.IFileProvider;
import com.tobprecise.demo.providers.ProviderFactory;
import com.tobprecise.demo.topologies.ParserTopology;

public class JsonParserBolt  extends BaseRichBolt {

	private static final Logger Log = LoggerFactory.getLogger(JsonParserBolt.class);
	
	private OutputCollector _collector;
	private IFileProvider _fileProvider;
	private IContextProvider _contextProvider;
	private Gson _gson;
	private Type _typeToken;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		Log.debug("preparing");
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_fileProvider = ProviderFactory.getFileProvider(appConfig);
		_contextProvider = ProviderFactory.getContextProvider(appConfig);
		_gson = new Gson();
		_typeToken = new TypeToken<List<Map<String,String>>>(){}.getType();		
	}

	@Override
	public void execute(Tuple input) {
		Log.trace("executing on {}", input);
		String contextId = input.getStringByField(RecordScheme.CONTEXT_ID);
		FileMetadata metadata = (FileMetadata) input.getValueByField(RecordScheme.FILE_METADATA);
		
		try (Reader reader = new InputStreamReader(_fileProvider.download(metadata))) {
			List<Map<String,String>> records = _gson.fromJson(reader, _typeToken);
			_contextProvider.setExpectedItems(contextId, records.size());
			int recordNumber = 0;
			for (Map<String,String> record: records) {
				EntityDto row = DtoBuilder.build(record);
				String recordContextId = _contextProvider.contextIdWithItem(contextId, recordNumber);
				_collector.emit(ParserTopology.Streams.JSON, input, new Values(recordContextId, row));
				Log.trace("emitting {} {}", recordContextId, row);
				recordNumber++;
			}
		} catch (Throwable t) { 
			_collector.emit(ParserTopology.Streams.DISCARD, input, new Values(contextId, metadata, "Error: " + t.getMessage()));
			Log.trace("discarding", t);
		}
		_collector.ack(input);
		Log.trace("acking {}", input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(ParserTopology.Streams.JSON, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ParserTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.FILE_METADATA, RecordScheme.REASON));		
	}

}
