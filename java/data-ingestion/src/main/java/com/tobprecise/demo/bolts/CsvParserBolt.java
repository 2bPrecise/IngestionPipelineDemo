package com.tobprecise.demo.bolts;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.FileMetadata;
import com.tobprecise.demo.entities.dto.DtoBuilder;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.providers.IContextProvider;
import com.tobprecise.demo.providers.IFileProvider;
import com.tobprecise.demo.providers.ProviderFactory;
import com.tobprecise.demo.topologies.ParserTopology;

public class CsvParserBolt  extends BaseRichBolt {

	private OutputCollector _collector;
	private IFileProvider _fileProvider;
	private IContextProvider _contextProvider;
	private CSVFormat _csvFormat;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_fileProvider = ProviderFactory.getFileProvider(appConfig);
		_contextProvider = ProviderFactory.getContextProvider(appConfig);
		_csvFormat = CSVFormat.EXCEL.withHeader().withIgnoreSurroundingSpaces().withTrim();
	}

	@Override
	public void execute(Tuple input) {
		String context = input.getStringByField(RecordScheme.CONTEXT_ID);
		FileMetadata metadata = (FileMetadata) input.getValueByField(RecordScheme.RECORD);
		
		try (Reader reader = new InputStreamReader(_fileProvider.download(metadata))) {
			Iterable<CSVRecord> records = _csvFormat.parse(reader);
			int recordNumber = 0;
			for (CSVRecord record : records) {
				EntityDto row = DtoBuilder.build(record.toMap());
				_collector.emit(ParserTopology.Streams.CSV, input, new Values(context, row));
				recordNumber++;
			}
			_contextProvider.setExpectedItems(context, recordNumber);
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
