var Promise = require('promise'),
	request = require('superagent'),
	util = require('util');

var checkResponse = function(err, resp) {
	var rejectTarget = null;
	if(err) rejectTarget = err;
	else if(!resp.ok) rejectTarget = resp.error;
	else if(resp.body.success !==  1) rejectTarget = resp.body.errmsg;

	return {
		fail: function(callback) {
			if(rejectTarget != null) callback(rejectTarget);
			return this;
		},
		then: function(callback) {
			if(rejectTarget == null) callback(resp.body);
			return this;
		}
	};
};

//args: jdbc, table
exports.loadColumns = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.get(util.format('/REST/Database/Columns/%s/', args.table))
			.query(args.jdbc)
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.columns);
					});
			});
	});
};

//args: jdbc
exports.loadTables = function(args) {
	return new Promise(function(resolve, reject) {
		request
			.get('/REST/Database/Tables/')
			.query(args.jdbc)
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.tables);
					});
			});
	});
};

exports.querySampleData = function(params) {
	return new Promise(function(resolve, reject) {
		request
			.get('/REST/Database/QuerySampleData/')
			.query(params)
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.sampleData);
					});
			});
	});
};

//args: title, script
exports.postScript = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.post(util.format('/REST/Script/New/%s/', args.title))
			.type('form')
			.send({
				script: args.script
			}).end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.success);
					});
			});
	});
};

//args: period, dbVendor, dbIp, dbPort, dbSid, jdbcDriver, jdbcConnUrl, 
// 			jdbcUsername, jdbcPassword, columns, table, bindingType, bindingColumn,
//			delimiter, charset, outputPath
exports.generateDb2FileScript = function(args) {
	return new Promise(function(resolve, reject) {
		request
			.get('/REST/Script/Generate/Db2File/')
			.query({
				period: args.period,
				dbVendor: args.dbVendor,
				dbIp: args.dbIp,
				dbPort: args.dbPort,
				dbSid: args.dbSid,
				jdbcDriver: args.jdbcDriver,
				jdbcConnUrl: args.jdbcConnUrl,
				jdbcUsername: args.jdbcUsername,
				jdbcPassword: args.jdbcPassword,
				columns: args.columns,
				table: args.table,
				bindingType: args.bindingType,
				bindingColumn: args.bindingColumn,
				delimiter: args.delimiter,
				charset: args.charset,
				outputPath: args.outputPath
			})
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.script);
					});
			});
	});
};

//args: title, script, dbName, jdbcDriver, jdbcConnUrl, jdbcUsername, jdbcPassword
exports.importVer1Script = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.post(util.format('/REST/Script/ImportVer1Script/%s/', args.title))
			.type('form')
			.send({
				script: args.script,
				dbName: args.dbName,
				jdbcDriver: args.jdbcDriver,
				jdbcConnUrl: args.jdbcConnUrl,
				jdbcUsername: args.jdbcUsername,
				jdbcPassword: args.jdbcPassword
			}).end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.success);
					});
			});
	});
};

//args: title, script
exports.editScript = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.post(util.format('/REST/Script/Edit/%s/', args.title))
			.type('form')
			.send({
				script: args.script
			}).end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.success);
					});
			});
	});
};

//return { SCRIPT_NAME, REGDATE, IS_RUNNING }
exports.loadScripts = function(args) {
	return new Promise(function(resolve, reject) {
		request
			.get('/REST/Script/Info/')
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.scriptInfos);
					});
			});
	});
};

//args: title
exports.loadScript = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.get(util.format('/REST/Script/Load/%s/', args.title))
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.script);
					});
			});
	});
};

//args: title
exports.loadScriptParams = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.get(util.format('/REST/Script/LoadParams/%s/', args.title))
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						if(body.parsable === 1) {
							resolve({
								parsable: 1,
								params: body.params
							});
						} else {
							resolve({
								parsable: 0,
								msg: body.msg
							});
						}
					});
			});
	});
};

//args: title, newTitle
exports.renameScript = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.post(util.format('/REST/Script/Rename/%s/', args.title))
			.type('form')
			.send({
				newTitle: args.newTitle
			})
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.success);
					});
			});
	});
};

//args: title
exports.startScript = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.post(util.format('/REST/Script/Start/%s/', args.title))
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.success);
					});
			});
	});
};

//args: title
exports.stopScript = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.post(util.format('/REST/Script/Stop/%s/', args.title))
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.success);
					});
			});
	});
};

//args: title
exports.removeScript = function(args) {
	args.title = encodeURI(args.title);

	return new Promise(function(resolve, reject) {
		request
			.post(util.format('/REST/Script/Remove/%s/', args.title))
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.success);
					});
			});
	});
};

exports.chartTotal = function() {
	return new Promise(function(resolve, reject) {
		request
			.get('/REST/Chart/ScriptScoreStatistics/Total/')
			.end(function(err, resp) {
				checkResponse(err, resp)
					.fail(function(err) {
						console.error(err);
						reject(err);
					}).then(function(body) {
						resolve(body.data);
					});
			});
	});
};

// args: scriptName, period
exports.lastStatistics = function(args) {
	args.scriptName = encodeURI(args.scriptName);

	return new Promise(function(resolve, reject) {
		request
			.get(util.format('/REST/Chart/ScriptScoreStatistics/LastStatistics/%s/', args.scriptName))
			.query({
				period: args.period
			})
			.end(function(err, resp) {
				checkResponse(err, resp)
						.fail(function(err) {
							console.error(err);
							reject(err);
						}).then(function(body) {
							resolve(body.data);
						});
			});
	});
};