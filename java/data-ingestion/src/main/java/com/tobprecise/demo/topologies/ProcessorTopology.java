package com.tobprecise.demo.topologies;

import java.util.HashMap;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.KillOptions;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.FirstPollOffsetStrategy;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff;
import org.apache.storm.kafka.spout.KafkaSpoutRetryService;
import org.apache.storm.kafka.spout.KafkaSpoutConfig.ProcessingGuarantee;
import org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import com.tobprecise.demo.bolts.AgeBolt;
import com.tobprecise.demo.bolts.ConverterBolt;
import com.tobprecise.demo.bolts.DbWriterBolt;
import com.tobprecise.demo.bolts.DiscardBolt;

public class ProcessorTopology {
public static void main(String[] args) throws Exception {
		
		TopologyBuilder builder = new TopologyBuilder();
		
		KafkaSpoutRetryService retryService = new KafkaSpoutRetryExponentialBackoff(
				TimeInterval.milliSeconds(10),
				TimeInterval.milliSeconds(10),
				20,
				TimeInterval.milliSeconds(5000)
				);
		KafkaSpoutConfig<String, String> config = KafkaSpoutConfig.builder("127.0.0.1", "records")
				.setRetry(retryService)
				.setFirstPollOffsetStrategy(FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST)
				.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE)
				.setProp(ConsumerConfig.GROUP_ID_CONFIG, "storm.records")
				.build();

		builder.setSpout(Components.SPOUT, new KafkaSpout(config));
		
		builder.setBolt(Components.CONVERTER, new ConverterBolt())
			.shuffleGrouping(Components.SPOUT);
		
		builder.setBolt(Components.AGE, new AgeBolt())
			.shuffleGrouping(Components.AGE, Streams.ACT);
		
		builder.setBolt(Components.WRITER, new DbWriterBolt())
			.shuffleGrouping(Components.CONVERTER)
			.shuffleGrouping(Components.AGE, Streams.ACT);

		builder.setBolt(Components.DISCARD, new DiscardBolt())
			.shuffleGrouping(Components.CONVERTER, Streams.DISCARD)
			.shuffleGrouping(Components.AGE, Streams.DISCARD)
			.shuffleGrouping(Components.WRITER, Streams.DISCARD);
		
		StormTopology topology = builder.createTopology();
		
		if (args.length > 0 && "submit".equals(args[0])) {
			StormSubmitter.submitTopologyWithProgressBar(TOPOLOGY_ID, new HashMap<String, Object>(), topology);
		} else {
			try (LocalCluster cluster = new LocalCluster()) {
				cluster.submitTopology(TOPOLOGY_ID, new HashMap<String, Object>(), topology);
				Utils.sleep(5000);
			    KillOptions opts = new KillOptions();
			    opts.set_wait_secs(5);
			    cluster.killTopologyWithOpts(TOPOLOGY_ID, opts);
				cluster.shutdown();
			}
		}

	}
	
	final static String TOPOLOGY_ID = "processor-topology";
	
	class Components {
		final static String SPOUT = "record-spout";
		final static String CONVERTER = "record-converter";
		final static String AGE = "age-calculator";
		final static String WRITER = "db-writer";
		final static String DISCARD = "discard-bolt";
	}
	
	public class Streams {
		public final static String ACT = "act-stream";
		public final static String DISCARD = "discard-stream";
	}
}
