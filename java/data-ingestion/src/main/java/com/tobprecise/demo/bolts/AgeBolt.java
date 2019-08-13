package com.tobprecise.demo.bolts;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.clinical.IClinicalAct;
import com.tobprecise.demo.entities.clinical.Patient;
import com.tobprecise.demo.providers.IPatientProvider;
import com.tobprecise.demo.providers.ProviderFactory;
import com.tobprecise.demo.topologies.ProcessorTopology;

public class AgeBolt  extends BaseRichBolt {

	private IPatientProvider _patientProvider;
	private OutputCollector _collector;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_patientProvider = ProviderFactory.getPatientProvider(appConfig);
	}

	@Override
	public void execute(Tuple input) {
		String contextId = input.getStringByField(RecordScheme.CONTEXT_ID);
		IClinicalAct act = (IClinicalAct) input.getValueByField(RecordScheme.RECORD);

		Patient patient = _patientProvider.getPatient(act.getPatientId());
		if (patient.getDemography().getDateOfBirth() != null && act.getStart() != null) {
			long diffInMillies = act.getStart().getTime() - patient.getDemography().getDateOfBirth().getTime();
			long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
			act.setAge(diff / 365);
		}
		
		_collector.emit(ProcessorTopology.Streams.ACT, input, new Values(contextId, act));
		_collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(ProcessorTopology.Streams.ACT, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD));
		declarer.declareStream(ProcessorTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD, RecordScheme.REASON));		
	}

}
