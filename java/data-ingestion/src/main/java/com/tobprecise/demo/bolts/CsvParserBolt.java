package com.tobprecise.demo.bolts;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.FileMetadata;
import com.tobprecise.demo.entities.dto.Idto;
import com.tobprecise.demo.providers.IFileProvider;
import com.tobprecise.demo.providers.ProviderFactory;
import com.tobprecise.demo.topologies.ParserTopology;

public class CsvParserBolt  extends BaseRichBolt {

	private OutputCollector _collector;
	private IFileProvider _fileProvider;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_fileProvider = ProviderFactory.getFileProvider(appConfig);
	}

	@Override
	public void execute(Tuple input) {
		String context = input.getStringByField(RecordScheme.CONTEXT_ID);
		FileMetadata metadata = (FileMetadata) input.getValueByField(RecordScheme.RECORD);
		
		try (CSVReader reader = new CSVReader(new InputStreamReader(_fileProvider.download(metadata)))) {
			List<Idto> results = new CsvToBeanBuilder(reader).withType(Idto.class).build().parse();
			for (Idto row: results) {
				_collector.emit(ParserTopology.Streams.CSV, input, new Values(context, row));
			}
		} catch (Throwable t) { 
			_collector.emit(ParserTopology.Streams.DISCARD, input, new Values(context, metadata, "Error: " + t.getMessage()));
		}
		_collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(ParserTopology.Streams.CSV, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ParserTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.FILE_METADATA, RecordScheme.REASON));		
	}

}
