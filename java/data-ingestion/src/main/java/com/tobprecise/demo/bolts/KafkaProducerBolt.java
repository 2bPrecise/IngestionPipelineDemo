package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.providers.IEntityProducer;
import com.tobprecise.demo.providers.ProviderFactory;

public class KafkaProducerBolt  extends BaseRichBolt {

	private static final Logger Log = LoggerFactory.getLogger(KafkaProducerBolt.class);
	
	private OutputCollector _collector;
	private IEntityProducer _producer;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		Log.debug("preparing");
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_producer = ProviderFactory.getEntityRecordsProducer(appConfig);
	}

	@Override
	public void execute(Tuple input) {
		Log.trace("executing on {}", input);
		EntityDto entity = (EntityDto) input.getValueByField(RecordScheme.RECORD);
		try {
			_producer.produce(entity);
			_collector.ack(input);
			Log.trace("acking {}", input);
		} catch (Exception e) {
			_collector.fail(input);
			Log.trace("failing {}", e);
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
