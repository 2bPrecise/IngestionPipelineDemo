package com.tobprecise.demo.bolts;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;
import com.tobprecise.demo.entities.clinical.Demography;
import com.tobprecise.demo.entities.clinical.IClinicalEntity;
import com.tobprecise.demo.entities.clinical.Medication;
import com.tobprecise.demo.providers.IContextProvider;
import com.tobprecise.demo.providers.IPatientProvider;
import com.tobprecise.demo.providers.ProviderFactory;
import com.tobprecise.demo.topologies.ProcessorTopology;

public class DbWriterBolt  extends BaseRichBolt {

	private OutputCollector _collector;
	private IPatientProvider _patientProvider;
	private IContextProvider _contextProvider;

	@Override
	public void prepare(Map<String, Object> config, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		AppConfig appConfig = AppConfigReader.read(config);
		_patientProvider = ProviderFactory.getPatientProvider(appConfig);
		_contextProvider = ProviderFactory.getContextProvider(appConfig);
	}

	@Override
	public void execute(Tuple input) {
		String contextId = input.getStringByField(RecordScheme.CONTEXT_ID);
		IClinicalEntity clinical = (IClinicalEntity) input.getValueByField(RecordScheme.RECORD);
		
		if (clinical instanceof Demography) {
			_patientProvider.saveDemography(clinical.getPatientId(), (Demography)clinical);
		} else if (clinical instanceof Medication) {
			_patientProvider.saveMedication(clinical.getPatientId(), (Medication)clinical);
		}
		
		_contextProvider.finishItem(contextId);

		_collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream(ProcessorTopology.Streams.DISCARD, new Fields(RecordScheme.CONTEXT_ID, RecordScheme.RECORD, RecordScheme.REASON));		
	}

}
