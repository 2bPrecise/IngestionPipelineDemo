package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.providers.IEntityProducer;
import com.tobprecise.demo.providers.ProviderFactory;

public class KafkaProducerBolt  extends BaseRichBolt {

	private OutputCollector _collector;
	private IEntityProducer _producer;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_producer = ProviderFactory.getEntityRecordsProducer(appConfig);
	}

	@Override
	public void execute(Tuple input) {
		EntityDto entity = (EntityDto) input.getValueByField(RecordScheme.RECORD);
		try {
			_producer.produce(entity);
			_collector.ack(input);
		} catch (Exception e) {
			_collector.fail(input);
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
