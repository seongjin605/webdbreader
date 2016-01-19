package com.igloosec.scripter.statistics;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.igloosec.scripter.common.SingletonInstanceRepo;
import com.igloosec.scripter.script.ScriptThread;
import com.igloosec.scripter.service.ScriptScoreStatisticsService;

public class ScriptScoreStatistics {
	//category
	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	public static final String ERROR_LOG = "errorLog";
	
	private ScriptScoreStatisticsService scriptScoreStatisticsService = SingletonInstanceRepo.getInstance(ScriptScoreStatisticsService.class);
	
	private ScriptScore4Category counters = new ScriptScore4Category();
	private Timer timer = new Timer();
	
	public ScriptScoreStatistics() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (ScriptScoreStatistics.class) {
					JSONArray oldCounts = counters.removeOldCounts((long) (60 * 1000));
					for (int i = 0; i < oldCounts.length(); i++) {
						JSONObject oldCount = oldCounts.getJSONObject(i);
						scriptScoreStatisticsService.insertStatistics(
								oldCount.getString("script"), 
								oldCount.getString("category"), 
								oldCount.getLong("timestamp"), 
								oldCount.getLong("count"));
					}
				}
				
				scriptScoreStatisticsService.deleteUnderTimestamp(System.currentTimeMillis() - (6 * 60 * 60 * 1000));
			}
		}, 60 * 1000, 60 * 1000);
	}

	public void incrementCount(String category){
		incrementCount(category, 1);
	}
	
	public void incrementCount(String category, Integer count){
		String scriptName = ScriptThread.currentThread().getScriptName();
		
		synchronized (ScriptScoreStatistics.class) {
			counters.incrementCount(category, scriptName, System.currentTimeMillis(), count);
		}
	}
	
	
	class ScriptScore4Category {
		private Map<String, ScriptScore4Script> counters = Maps.newHashMap();
		
		void incrementCount(String category, String script, Long timestamp, Integer count){
			ScriptScore4Script counter = counters.get(category);
			if(counter == null){
				counter = new ScriptScore4Script();
				counters.put(category, counter);
			}
			counter.incrementCount(script, timestamp, count);
		}
		
		JSONArray removeOldCounts(Long adjustTime){
			JSONArray oldCounts = new JSONArray();
			for(Entry<String, ScriptScore4Script> counterEntry : counters.entrySet()){
				String category = counterEntry.getKey();
				JSONArray oldCount = counterEntry.getValue().removeOldCounts(adjustTime);
				while(oldCount.length() != 0){
					JSONObject countObj = (JSONObject) oldCount.remove(0);
					countObj.put("category", category);
					oldCounts.put(countObj);
				}
			}
			return oldCounts;
		}
	}
	
	class ScriptScore4Script {
		private Map<String, ScriptScore4Timestamp> counters = Maps.newHashMap();
		
		void incrementCount(String script, Long timestamp, Integer count){
			ScriptScore4Timestamp counter = counters.get(script);
			if(counter == null){
				counter = new ScriptScore4Timestamp();
				counters.put(script, counter);
			}
			counter.incrementCount(timestamp, count);
		}
		
		JSONArray removeOldCounts(Long adjustTime){
			JSONArray oldCounts = new JSONArray();
			for(Entry<String, ScriptScore4Timestamp> counterEntry : counters.entrySet()){
				String scriptName = counterEntry.getKey();
				JSONArray oldCount = counterEntry.getValue().removeOldCounts(adjustTime);
				while(oldCount.length() != 0){
					JSONObject countObj = (JSONObject) oldCount.remove(0);
					countObj.put("script", scriptName);
					oldCounts.put(countObj);
				}
			}
			return oldCounts;
		}
	}
	
	class ScriptScore4Timestamp {
		private Map<Long, AtomicLong> counters = Maps.newHashMap();
		
		void incrementCount(Long timestamp, Integer count){
			timestamp = timestamp - (timestamp % (60*1000) );
			AtomicLong counter = counters.get(timestamp);
			if(counter == null) {
				counter = new AtomicLong(1L);
				counters.put(timestamp, counter);
			}
			counter.addAndGet(count);
		}
		
		JSONArray removeOldCounts(Long adjustTime){
			JSONArray oldCounts = new JSONArray();
			
			Long standardTime = System.currentTimeMillis() - adjustTime;
			Set<Long> oldTimestamps = Sets.newHashSet();
			for(Long timestamp : counters.keySet()){
				if(timestamp < standardTime)
					oldTimestamps.add(timestamp);
			}
			
			for(Long oldTimestamp : oldTimestamps) {
				Long count = counters.remove(oldTimestamp).get();
				oldCounts.put(new JSONObject().put("timestamp", oldTimestamp).put("count", count));
			}
			
			return oldCounts;
		}
	}
}