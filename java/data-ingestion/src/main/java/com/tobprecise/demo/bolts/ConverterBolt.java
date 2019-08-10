package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.tobprecise.demo.topologies.ProcessorTopology;

public class ConverterBolt  extends BaseRichBolt {

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ProcessorTopology.Streams.ACT, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ProcessorTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD, RecordScheme.REASON));		
	}

}
