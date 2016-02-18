package com.igloosec.scripter;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import com.igloosec.scripter.common.Conf;
import com.igloosec.scripter.common.Path;
import com.igloosec.scripter.common.SingletonInstanceRepo;
import com.igloosec.scripter.dao.AutoStartScriptDAO;
import com.igloosec.scripter.rdb.DerbySchemaCreator;
import com.igloosec.scripter.script.ScriptExecutor;
import com.igloosec.scripter.service.ScriptService;
import com.igloosec.scripter.servlet.ChartREST;
import com.igloosec.scripter.servlet.ConfigREST;
import com.igloosec.scripter.servlet.DatabaseREST;
import com.igloosec.scripter.servlet.EmbedDbREST;
import com.igloosec.scripter.servlet.FileOutMsgWebSocketServlet;
import com.igloosec.scripter.servlet.Index;
import com.igloosec.scripter.servlet.LoggerWebSocketServlet;
import com.igloosec.scripter.servlet.MetaREST;
import com.igloosec.scripter.servlet.NotiWebSocketServlet;
import com.igloosec.scripter.servlet.ScriptREST;
import com.igloosec.scripter.servlet.ShutdownREST;
import com.igloosec.scripter.statistics.ScriptScoreStatistics;
import com.igloosec.scripter.util.Log4jConfig;

public class Server {
	public static void main(String[] args) throws Exception {
		Log4jConfig.initLog4j();
		
		registerShutdownHook();
		
		//jetty debug log to stdout
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("com.igloosec.scripter.LEVEL", "DEBUG");
		
		new DerbySchemaCreator().check();
		
		SingletonInstanceRepo.getInstance(ScriptService.class).startAutoStartScript();
		
		SingletonInstanceRepo.getInstance(ScriptScoreStatistics.class);
		
		QueuedThreadPool threadPool = new QueuedThreadPool(Conf.getAs(Conf.JETTY_THREAD_POOL_SIZE, 20));

		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server();
		server.setThreadPool(threadPool);
		server.setStopAtShutdown(true);
		
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(Conf.getAs(Conf.PORT, 8098));
		server.setConnectors(new SelectChannelConnector[] { connector });
		
		WebAppContext context = getWebAppContext();
		server.setHandler(context);
		
		server.start();
		server.join();
	}
	
	private static WebAppContext getWebAppContext() throws IOException{
		WebAppContext context = new WebAppContext();
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		context.setResourceBase(Server.class.getClassLoader().getResource("resource/static").toExternalForm());
		context.addServlet(EmbedDbREST.class, "/REST/EmbedDb/*");
		context.addServlet(ScriptREST.class, "/REST/Script/*");
		context.addServlet(DatabaseREST.class, "/REST/Database/*");
		context.addServlet(MetaREST.class, "/REST/Meta/*");
		context.addServlet(ConfigREST.class, "/REST/Config/*");
		context.addServlet(ChartREST.class, "/REST/Chart/*");
		context.addServlet(ShutdownREST.class, "/REST/Shutdown/*");
		
		context.addServlet(LoggerWebSocketServlet.class, "/WebSocket/Logger/*");
		context.addServlet(FileOutMsgWebSocketServlet.class, "/WebSocket/FileOutMsg/*");
		context.addServlet(NotiWebSocketServlet.class, "/WebSocket/Noti/*");
		
		context.addServlet(Index.class, "");
		context.addServlet(Index.class, "/Script/*");
		context.addServlet(Index.class, "/Config/*");
		context.addServlet(Index.class, "/ApiDoc/*");
		
		//new
		context.addServlet(Index.class, "/Api");
		context.addServlet(Index.class, "/Api/*");
		
		context.setContextPath("/");
		
		File workDir = new File(Path.getPackagePath().getAbsolutePath(), "work");
		FileUtils.deleteDirectory(workDir);
		context.setTempDirectory(workDir);
		return context;
	}
	
	private static void registerShutdownHook(){
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				ScriptExecutor scriptExecutor = SingletonInstanceRepo.getInstance(ScriptExecutor.class);
				Set<String> runningScripts = scriptExecutor.getRunningScripts();
				
				AutoStartScriptDAO autoStartScriptDAO = SingletonInstanceRepo.getInstance(AutoStartScriptDAO.class);
				autoStartScriptDAO.removeAll();
				autoStartScriptDAO.save(runningScripts);
			}
		});
	}
}