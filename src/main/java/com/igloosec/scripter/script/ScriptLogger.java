package com.igloosec.scripter.script;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.igloosec.scripter.common.SingletonInstanceRepo;
import com.igloosec.scripter.service.LoggerService;
import com.igloosec.scripter.service.NotiService;

public class ScriptLogger {
	private static final Logger logger = LoggerFactory.getLogger(ScriptLogger.class);
	private NotiService notiService = SingletonInstanceRepo.getInstance(NotiService.class);
	private LoggerService loggerService = SingletonInstanceRepo.getInstance(LoggerService.class);
	private String scriptName;

	public ScriptLogger(String scriptName) {
		this.scriptName = scriptName;
	}
	
	public void info(String msg) {
		logger.info(msg);
		loggerService.dispatchMsg(scriptName, System.currentTimeMillis(), "info", msg);
	} 
	
	public void debug(String msg) {
		logger.debug(msg);
		loggerService.dispatchMsg(scriptName, System.currentTimeMillis(), "debug", msg);
	} 
	
	public void warn(String msg) {
		logger.warn(msg);
		loggerService.dispatchMsg(scriptName, System.currentTimeMillis(), "warn", msg);
	} 
	
	public void error(String msg) {
		logger.error(msg);
		loggerService.dispatchMsg(scriptName, System.currentTimeMillis(), "error", msg);
		notiService.dispatchErrLog(scriptName, System.currentTimeMillis(), msg);
	} 
	
	public void error(String msg, Throwable e) {
		logger.error(msg, e);
		loggerService.dispatchMsg(scriptName, System.currentTimeMillis(), "error", msg);
		loggerService.dispatchMsg(scriptName, System.currentTimeMillis(), "error", ExceptionUtils.getStackTrace(e));
		notiService.dispatchErrLog(scriptName, System.currentTimeMillis(), msg);
		notiService.dispatchErrLog(scriptName, System.currentTimeMillis(), ExceptionUtils.getStackTrace(e));
	} 
} 