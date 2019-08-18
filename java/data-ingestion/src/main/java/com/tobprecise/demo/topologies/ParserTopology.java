package com.tobprecise.demo.topologies;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.KillOptions;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import com.tobprecise.demo.bolts.CsvParserBolt;
import com.tobprecise.demo.bolts.DiscardBolt;
import com.tobprecise.demo.bolts.FileTypeDispatcherBolt;
import com.tobprecise.demo.bolts.JsonParserBolt;
import com.tobprecise.demo.bolts.KafkaProducerBolt;
import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;

public class ParserTopology {
	public static void main(String[] args) throws Exception {
		
		AppConfig appConfig = AppConfigReader.read(args[0]);
		
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout(Components.SPOUT, new KafkaSpout(
				AppConfigReader.createKafkaSpoutConfig(appConfig, appConfig.inboxTopic)
				));
		
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
		
		Config stormConfig = new Config();
		AppConfigReader.write(stormConfig, appConfig);
		stormConfig.setMessageTimeoutSecs(30);
		stormConfig.setDebug(true);
		stormConfig.setNumWorkers(1);

		if (appConfig.submit) {
			StormSubmitter.submitTopologyWithProgressBar(TOPOLOGY_ID, stormConfig, topology);
		} else {
			try (LocalCluster cluster = new LocalCluster()) {
				cluster.submitTopology(TOPOLOGY_ID, stormConfig, topology);
				Utils.sleep(appConfig.localRunForSeconds * 1000);
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
