package com.tobprecise.demo.bolts;
/**
 Copyright 2014 Daniel Giribet <dani - calidos.cat>
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.storm.spout.ISpoutOutputCollector;
import org.apache.storm.task.IOutputCollector;
import org.apache.storm.tuple.Tuple;

public class MemoryOutputCollector implements IOutputCollector, ISpoutOutputCollector {

	public List<Object> values = new ArrayList<Object>();
	public List<String> streams = new ArrayList<String>();

	public List<Integer> emit(String streamId, Collection<Tuple> anchors, List<Object> tuple) {
		values.add(tuple);
		streams.add(streamId);
		return null;
	}

	public void emitDirect(int taskId, String streamId, Collection<Tuple> anchors, List<Object> tuple) {
		values.add(tuple);
		streams.add(streamId);
	}

	public void ack(Tuple input) {

	}

	public void fail(Tuple input) {

	}

	public void reportError(Throwable error) {

	}

	public List<Object> getValues() {
		return values;
	}

	@Override
	public void resetTimeout(Tuple arg0) {
	}

	@Override
	public List<Integer> emit(String streamId, List<Object> tuple, Object messageId) {
		values.add(tuple);
		streams.add(streamId);
		return null;
	}

	@Override
	public void emitDirect(int taskId, String streamId, List<Object> tuple, Object messageId) {
		values.add(tuple);
		streams.add(streamId);
	}

	@Override
	public long getPendingCount() {
		return values.size();
	}

	@Override
	public void flush() {		
	}

}