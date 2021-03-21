'use-strict';

const realtimeClient = require('../realtime');

module.exports = {
	name: 'realtime',
	version: '1.0.0',
	register: async server => {
		const config = server.app.config.gtfsr;
		// Start realtime plugin with server, config, dayInterval (default value: 60000ms), nightInterval (default value: 180000ms)
		const client = await realtimeClient(server, config);
		server.expose('client', client);
	}
};
