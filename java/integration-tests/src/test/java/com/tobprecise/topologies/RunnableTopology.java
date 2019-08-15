package com.tobprecise.topologies;

@FunctionalInterface
public interface RunnableTopology {
   void apply(String[] args) throws Exception;
}
