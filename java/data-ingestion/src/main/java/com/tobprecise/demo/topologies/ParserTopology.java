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

import com.tobprecise.demo.bolts.CsvParserBolt;
import com.tobprecise.demo.bolts.DiscardBolt;
import com.tobprecise.demo.bolts.FileTypeDispatcherBolt;
import com.tobprecise.demo.bolts.JsonParserBolt;
import com.tobprecise.demo.bolts.KafkaProducerBolt;

public class ParserTopology {
	public static void main(String[] args) throws Exception {
		
		TopologyBuilder builder = new TopologyBuilder();
		
		KafkaSpoutRetryService retryService = new KafkaSpoutRetryExponentialBackoff(
				TimeInterval.milliSeconds(10),
				TimeInterval.milliSeconds(10),
				20,
				TimeInterval.milliSeconds(5000)
				);
		KafkaSpoutConfig<String, String> config = KafkaSpoutConfig.builder("127.0.0.1", "inbox")
				.setRetry(retryService)
				.setFirstPollOffsetStrategy(FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST)
				.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE)
				.setProp(ConsumerConfig.GROUP_ID_CONFIG, "storm.inbox")
				.build();

		builder.setSpout(Components.SPOUT, new KafkaSpout(config));
		
		builder.setBolt(Components.DISPATCHER, new FileTypeDispatcherBolt())
			.shuffleGrouping(Components.SPOUT);
		
		builder.setBolt(Components.CSV, new CsvParserBolt())
			.shuffleGrouping(Components.DISPATCHER, Streams.CSV);
		
		builder.setBolt(Components.JSON, new JsonParserBolt())
			.shuffleGrouping(Components.DISPATCHER, Streams.JSON);
		
		builder.setBolt(Components.PRODUCER, new KafkaProducerBolt())
			.shuffleGrouping(Components.CSV, Streams.CSV)
			.shuffleGrouping(Components.JSON, Streams.JSON);

		builder.setBolt(Components.DISCARD, new DiscardBolt())
			.shuffleGrouping(Components.DISPATCHER, Streams.DISCARD)
			.shuffleGrouping(Components.CSV, Streams.DISCARD)
			.shuffleGrouping(Components.JSON, Streams.DISCARD);
		
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
	
	final static String TOPOLOGY_ID = "parser-topology";
	
	class Components {
		final static String SPOUT = "inbox-spout";
		final static String DISPATCHER = "file-type-dispatcher";
		final static String CSV = "csv-parser";
		final static String JSON = "json-parser";
		final static String PRODUCER = "kafka-producer";
		final static String DISCARD = "discard-bolt";
	}
	
	public class Streams {
		public final static String CSV = "csv-stream";
		public final static String JSON = "json-stream";
		public final static String DISCARD = "discard-stream";
	}
}
