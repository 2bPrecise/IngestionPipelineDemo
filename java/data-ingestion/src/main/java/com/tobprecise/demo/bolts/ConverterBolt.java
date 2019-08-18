package com.tobprecise.demo.bolts;

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
import com.tobprecise.demo.entities.clinical.ClinicalEntitiesBuilderRegistry;
import com.tobprecise.demo.entities.clinical.IClinicalEntity;
import com.tobprecise.demo.entities.clinical.IClinicalAct;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.topologies.ProcessorTopology;

public class ConverterBolt  extends BaseRichBolt {

	private static final Logger Log = LoggerFactory.getLogger(ConverterBolt.class);
	
	private OutputCollector _collector;
	private ClinicalEntitiesBuilderRegistry _clinicalEntityBuilderRegistry;
	private Gson _gson;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		Log.debug("preparing");
		_collector = collector;	
		_clinicalEntityBuilderRegistry = new ClinicalEntitiesBuilderRegistry();
		_gson = new Gson();
	}

	@Override
	public void execute(Tuple input) {
		Log.trace("executing on {}", input);
		
		EntityDto dto = null;
		try {
			String inputJson = input.getStringByField(RecordScheme.VALUE);
			dto = _gson.fromJson(inputJson, EntityDto.class);
		} catch (Exception ex) {
			_collector.emit(ProcessorTopology.Streams.DISCARD, input, new Values(null, null, ex.getMessage()));
			_collector.ack(input);
			Log.trace("discarding", ex);
			return;
		}
		
		IClinicalEntity clinical = null;
		try {
			clinical = _clinicalEntityBuilderRegistry.get(dto.type).build(dto);	
		} catch (Exception ex) {
			_collector.emit(ProcessorTopology.Streams.DISCARD, input, new Values(dto.contextid, dto, ex.getMessage()));
			_collector.ack(input);
			Log.trace("discarding", ex);
			return;
		}

		if (clinical instanceof IClinicalAct) {
			_collector.emit(ProcessorTopology.Streams.ACT, input, new Values(dto.contextid, clinical));
			Log.trace("emitting {} {} as act", dto.contextid, clinical);
		} else {
			_collector.emit(input, new Values(dto.contextid, clinical));
			Log.trace("emitting {} {} not act", dto.contextid, clinical);
		}
		
		_collector.ack(input);
		Log.trace("acking");
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ProcessorTopology.Streams.ACT, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ProcessorTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD, RecordScheme.REASON));		
	}

}
