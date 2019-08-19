package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscardBolt  extends BaseRichBolt {

	private static final Logger Log = LoggerFactory.getLogger(DiscardBolt.class);	
			
	private OutputCollector _collector;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		Log.debug("preparing");
		_collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		Log.warn("discarded {}", input);
		_collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

}
