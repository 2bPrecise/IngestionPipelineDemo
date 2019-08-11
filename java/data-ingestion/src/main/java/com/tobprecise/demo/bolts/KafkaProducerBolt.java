package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.providers.IContextProvider;
import com.tobprecise.demo.providers.ProviderFactory;

public class KafkaProducerBolt  extends BaseRichBolt {

	private OutputCollector _collector;
	private IContextProvider _contextProvider;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_contextProvider = ProviderFactory.getContextProvider(appConfig);
	}

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
