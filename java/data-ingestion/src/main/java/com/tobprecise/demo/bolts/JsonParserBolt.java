package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.tobprecise.demo.topologies.ParserTopology;

public class JsonParserBolt  extends BaseRichBolt {

	private OutputCollector _collector;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(ParserTopology.Streams.JSON, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ParserTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.FILE_METADATA, RecordScheme.REASON));		
	}

}
