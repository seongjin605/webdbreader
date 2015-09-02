package com.igloosec.webdbreader.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.igloosec.webdbreader.common.SingletonInstanceRepo;
import com.igloosec.webdbreader.service.OperationHistoryService;
import com.igloosec.webdbreader.service.ScriptScoreStatisticsService;
import com.igloosec.webdbreader.service.ScriptService;
import com.igloosec.webdbreader.statistics.ScriptScoreStatistics;
import com.igloosec.webdbreader.util.Util;
import com.igloosec.webdbreader.util.jade.JadeHttpServlet;
import com.sun.jersey.api.uri.UriTemplate;

import de.neuland.jade4j.exceptions.JadeCompilerException;

public class Test extends JadeHttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		
		String pathInfo = req.getPathInfo();
		if(pathInfo == null) pathInfo = "/";
		Map<String, String> pathParams = new HashMap<String, String>();
		
		try{
			if(new UriTemplate("/").match(pathInfo, pathParams)){
				resp.getWriter().print(jade("test.jade", null));
				resp.getWriter().flush();
			} else{
				Map<String, Object> model = Maps.newHashMap();
				resp.getWriter().print(jade("error.jade", model));
				resp.getWriter().flush();
			} //if
		} catch(IllegalArgumentException e){
			logger.error(String.format("%s, errmsg: %s", e.getClass().getSimpleName(), e.getMessage()));
			Map<String, Object> model = Maps.newHashMap();
			resp.getWriter().print(jade("error.jade", model));
			resp.getWriter().flush();
		} catch(Exception e){
			logger.error(String.format("%s, errmsg: %s", e.getClass().getSimpleName(), e.getMessage()), e);
			Map<String, Object> model = Maps.newHashMap();
			resp.getWriter().print(jade("error.jade", model));
			resp.getWriter().flush();
		} //catch
	} //doGet
} //class