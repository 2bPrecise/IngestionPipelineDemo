package com.tobprecise.demo.topologies.single;

import org.junit.Test;

import com.tobprecise.demo.topologies.ProcessorTopology;
import com.tobprecise.topologies.TopologyTestBase;

public class ProcessorTopologyTest extends TopologyTestBase {
	
	@Test
	public void runProcessorTopology() throws Exception {
		
		
		//
		//
		// run topology
		//
		//
		runTopology((args) -> ProcessorTopology.main(args), 30);
		
	}
	
}
