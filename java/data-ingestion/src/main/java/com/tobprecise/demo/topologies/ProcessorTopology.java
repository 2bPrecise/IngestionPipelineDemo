package com.tobprecise.demo.topologies;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.KillOptions;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

import com.tobprecise.demo.bolts.AgeBolt;
import com.tobprecise.demo.bolts.ConverterBolt;
import com.tobprecise.demo.bolts.DbWriterBolt;
import com.tobprecise.demo.bolts.DiscardBolt;
import com.tobprecise.demo.config.AppConfig;
import com.tobprecise.demo.config.AppConfigReader;

public class ProcessorTopology {
	public static void main(String[] args) throws Exception {
		
		AppConfig appConfig = AppConfigReader.read(args[0]);
	
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout(Components.SPOUT, new KafkaSpout(AppConfigReader.createKafkaSpoutConfig(appConfig, appConfig.inboxTopic)));
		
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
