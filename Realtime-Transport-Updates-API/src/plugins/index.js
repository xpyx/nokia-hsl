'use-strict';

const sql = require('./sql');
const realtime = require('./realtime');

module.exports.register = async server => {
	await server.register(sql);
	await server.register(realtime);
};
