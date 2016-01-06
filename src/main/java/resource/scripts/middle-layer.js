importPackage(Packages.com.igloosec.scripter.script.bindingsV1);
importPackage(Packages.com.igloosec.scripter.script.bindingsV2);
importPackage(Packages.com.igloosec.scripter.script.bindingsV2.base);
importPackage(Packages.com.igloosec.scripter.script.bindingsV2.headpipe);
importPackage(Packages.com.igloosec.scripter.script.bindingsV2.pipe);


var dummy = function(count) {
	return new DummyPipeHead(count);
};

// common --------------------------------------------------------
var data = function(data) {
	return new DataPipeHead(data);
};

var repo = function(key, value) {
	if(value == null) {
		return new SimpleRepo().get(key);
	} else {
		return new SimpleRepo().set(key, value);
	}
};

// date --------------------------------------------------------
var now = function() {
	return date(new Date());
};

var date = function(arg) {
	var javaDateObj = null;
	if(arg instanceof java.util.Date) {
		javaDateObj = arg;
	} else if(arg instanceof java.lang.Integer || arg instanceof java.lang.Long || typeof arg === 'number') {
		javaDateObj = new Date(arg);
	} else if(arg instanceof Date) {
		javaDateObj = new Date(arg.getTime());
	}

	return {
		getDate: function() {
			return javaDateObj;
		}, 
		format: function(format) {
			return new java.text.SimpleDateFormat(format).format(javaDateObj);
		}
	};
};


// schedule --------------------------------------------------------
var schedule = function(period) {
	var scheduler;
	return {
		run: function(callback) {
			scheduler = new SchedulePipeHead(period, callback);
			scheduler.run();
		}
	};
}

// old api --------------------------------------------------------
var apiV1 = function(callback) {
	var dateUtil = new DateUtil();
	var dbHandler = new DbHandler();
	var fileExporter = new FileExporter();
	var httpUtil = new HttpUtil();
	var runtimeUtil = new RuntimeUtil();
	var scheduler = new Scheduler();
	var simpleRepo = new SimpleRepo();
	var stringUtil = new StringUtil();
	return callback(dateUtil, dbHandler, fileExporter, httpUtil, runtimeUtil, scheduler, simpleRepo, stringUtil);
};

// logger --------------------------------------------------------
var log = function(msg) {
	return data(msg).log('info').run();
};

var debug = function(msg) {
	return data(msg).log('debug').run();
};

var warn = function(msg) {
	return data(msg).log('warn').run();
};

var error = function(msg) {
	return data(msg).log('error').run();
};

// database --------------------------------------------------------
var database = function(jdbc) {
	return {
		select: function(query) {
			return DBSelectPipe({
				driver: jdbc.driver,
				connUrl: jdbc.connUrl,
				username: jdbc.username,
				password: jdbc.password,
				query: query
			});
		}, update: function(query) {
			return data(query).dbUpdate(jdbc);
		}
	};
};