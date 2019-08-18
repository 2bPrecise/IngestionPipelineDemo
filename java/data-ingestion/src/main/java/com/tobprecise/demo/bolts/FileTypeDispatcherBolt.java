package com.tobprecise.demo.bolts;

import java.util.Locale;
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
import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.FileMetadata;
import com.tobprecise.demo.providers.IContextProvider;
import com.tobprecise.demo.providers.ProviderFactory;
import com.tobprecise.demo.topologies.ParserTopology;

public class FileTypeDispatcherBolt extends BaseRichBolt {

	private static final Logger Log = LoggerFactory.getLogger(FileTypeDispatcherBolt.class);
	
	private IContextProvider _contextProvider;
	private OutputCollector _collector;
	private Gson _gson;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		Log.debug("preparing");
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_contextProvider = ProviderFactory.getContextProvider(appConfig);
		_gson = new Gson();
	}

	@Override
	public void execute(Tuple input) {
		Log.trace("executing on {}", input);
		
		FileMetadata metadata = null;
		
		try {
			String inputJson = input.getStringByField(RecordScheme.VALUE);
			metadata = _gson.fromJson(inputJson, FileMetadata.class);
		} catch (Exception ex) {
			_collector.emit(ParserTopology.Streams.DISCARD, input, new Values("", null, ex.getMessage()));
			_collector.ack(input);		
			Log.trace("discarding", ex);
			return;
		}
		
		String context = _contextProvider.initializeContext(metadata.getFileId(), metadata.getOriginalName());
		
		if (isCsv(metadata.getOriginalName())) {
			_collector.emit(ParserTopology.Streams.CSV, input, new Values(context, metadata));
			Log.trace("emitting {} {} to csv", context, metadata);
		}
		else if (isJson(metadata.getOriginalName())) {
			_collector.emit(ParserTopology.Streams.JSON, input, new Values(context, metadata));
			Log.trace("emitting {} {} to json", context, metadata);
		} else {
			_collector.emit(ParserTopology.Streams.DISCARD, input, new Values(context, metadata, "Unrecognized file type"));
			Log.trace("discarding {} {} - unknown format", context, metadata);
		}
		
		_collector.ack(input);
		Log.trace("acking {}", input);
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(ParserTopology.Streams.CSV, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.FILE_METADATA));
		declarer.declareStream(ParserTopology.Streams.JSON, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.FILE_METADATA));
		declarer.declareStream(ParserTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.FILE_METADATA, RecordScheme.REASON));		
	}

	private boolean isCsv(String filename) {
		return filename.toLowerCase(Locale.ROOT).endsWith("csv");
	}
	private boolean isJson(String filename) {
		return filename.toLowerCase(Locale.ROOT).endsWith("json");
	}

}
