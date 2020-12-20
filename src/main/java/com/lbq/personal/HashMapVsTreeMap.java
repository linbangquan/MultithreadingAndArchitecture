package com.lbq.personal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class HashMapVsTreeMap {
	
	private final int LENGTH = 1000;	
	private Map<Integer, Integer> hashMap; 
	private Map<Integer, Integer> treeMap;
	private Map<Integer, Integer> linkedHashMap;
	
	@Setup(Level.Iteration)
	public void setUp() {
		this.hashMap = new HashMap<>();
		this.linkedHashMap = new LinkedHashMap<>();
		this.treeMap = new TreeMap<>((e1,e2) -> -e1.compareTo(e2));
	}
	@Benchmark
	public Map<Integer, Integer> hashMap(){
		for(int i = 0; i < LENGTH; i++) {
			for(int j = 0; j < LENGTH; j++) {
				Integer key = i * LENGTH + j;
				this.hashMap.put(key, key);
			}
		}
		for(int i = LENGTH - 1; i >= 0; i--) {
			for(int j = LENGTH - 1; j >= 0; j--) {
				Integer key = i * LENGTH + j;
				this.hashMap.remove(key);
			}
		}
		return hashMap;
	}
	@Benchmark
	public Map<Integer, Integer> treeMap(){
		for(int i = 0; i < LENGTH; i++) {
			for(int j = 0; j < LENGTH; j++) {
				Integer key = i * LENGTH + j;
				this.treeMap.put(key, key);
			}
		}
		Iterator<Map.Entry<Integer, Integer>> iterator = this.treeMap.entrySet().iterator();
		while(iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		return treeMap;
	}
	@Benchmark
	public Map<Integer, Integer> linkedHashMap(){
		for(int i = 0; i < LENGTH; i++) {
			for(int j = 0; j < LENGTH; j++) {
				Integer key = i * LENGTH + j;
				this.linkedHashMap.put(key, key);
			}
		}
		for(int i = LENGTH - 1; i >= 0; i--) {
			for(int j = LENGTH - 1; j >= 0; j--) {
				Integer key = i * LENGTH + j;
				this.linkedHashMap.remove(key);
			}
		}
		return linkedHashMap;
	}
	public static void main(String[] args) throws RunnerException {
		final Options opts = new OptionsBuilder()
				.include(HashMapVsTreeMap.class.getSimpleName())
				.forks(1)
				.measurementIterations(10)
				.warmupIterations(10)
				.build();
		new Runner(opts).run();
	}
}
