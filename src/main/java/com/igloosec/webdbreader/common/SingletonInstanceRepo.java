package com.igloosec.webdbreader.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonInstanceRepo {
	private static final Logger logger = LoggerFactory.getLogger(SingletonInstanceRepo.class);
	private static Map<Class, Object> instances = new HashMap<Class, Object>();

//	static {
//		instances.put(FileWriteStatistics.class, new FileWriteStatistics());
//
//		instances.put(DerbyDataSource.class, new DerbyDataSource());
//
//		instances.put(ScriptExecutor.class, new ScriptExecutor());
//
//		instances.put(DatabaseService.class, new DatabaseService());
//		instances.put(EmbedDbService.class, new EmbedDbService());
//		instances.put(FileWriteStatisticsService.class, new FileWriteStatisticsService());
//		instances.put(ScriptService.class, new ScriptService());
//
//		instances.put(EmbedDbDAO.class, new EmbedDbDAO());
//		instances.put(FileWriteStatisticsDAO.class, new FileWriteStatisticsDAO());
//		instances.put(MainDAO.class, new MainDAO());
//		instances.put(ScriptDAO.class, new ScriptDAO());
//	} // static

	public static <T> T getInstance(Class<T> clazz) {
		Object instance = instances.get(clazz);
		try {
			if(instance == null){
				instance = clazz.newInstance();
				instances.put(clazz, instance);
			} //if
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(String.format("%s, errmsg: %s", e.getClass().getSimpleName(), e.getMessage()), e);
		} //catch
		return (T) instance;
	} // getInstance
} // class