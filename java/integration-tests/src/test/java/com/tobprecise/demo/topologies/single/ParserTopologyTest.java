package com.tobprecise.demo.topologies.single;

import org.junit.Test;

import com.tobprecise.demo.topologies.ParserTopology;
import com.tobprecise.topologies.TopologyTestBase;

public class ParserTopologyTest extends TopologyTestBase {
	
	@Test
	public void runParserTopology() throws Exception {
		
		
		//
		//
		// run topology
		//
		//
		runTopology((args) -> ParserTopology.main(args), 30);
		
	}
	
}
