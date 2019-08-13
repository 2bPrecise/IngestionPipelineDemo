package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.tobprecise.demo.entities.clinical.ClinicalEntitiesBuilderRegistry;
import com.tobprecise.demo.entities.clinical.IClinicalEntity;
import com.tobprecise.demo.entities.clinical.IClinicalAct;
import com.tobprecise.demo.entities.dto.EntityDto;
import com.tobprecise.demo.topologies.ProcessorTopology;

public class ConverterBolt  extends BaseRichBolt {

	private OutputCollector _collector;
	private ClinicalEntitiesBuilderRegistry _clinicalEntityBuilderRegistry;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;	
		_clinicalEntityBuilderRegistry = new ClinicalEntitiesBuilderRegistry();
	}

	@Override
	public void execute(Tuple input) {
		
		EntityDto dto = null;
		try {
			dto = (EntityDto) input.getValueByField(RecordScheme.VALUE);
		} catch (Exception ex) {
			_collector.emit(ProcessorTopology.Streams.DISCARD, input, new Values(null, null, ex.getMessage()));
		}
		
		IClinicalEntity clinical = null;
		try {
			clinical = _clinicalEntityBuilderRegistry.get(dto.type).build(dto);	
		} catch (Exception ex) {
			_collector.emit(ProcessorTopology.Streams.DISCARD, input, new Values(dto.contextid, dto, ex.getMessage()));
		}

		if (clinical instanceof IClinicalAct) {
			_collector.emit(ProcessorTopology.Streams.ACT, input, new Values(dto.contextid, clinical));
		} else {
			_collector.emit(input, new Values(dto.contextid, clinical));
		}
		
		_collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ProcessorTopology.Streams.ACT, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ProcessorTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD, RecordScheme.REASON));		
	}

}
