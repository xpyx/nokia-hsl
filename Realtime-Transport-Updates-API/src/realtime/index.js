'use-strict';

/* eslint-disable no-unused-vars */
const GtfsRealtimeBindings = require('gtfs-realtime-bindings');
const axios = require('axios').default;

const queries = require('./queries');
const {filterFeed, getCurrentTimestamp, getCurrentTime} = require('./utils');

const client = async (server, config, dayServiceInterval = 60000, nightServiceInterval = 180000) => {
	let feed = null;

	// Loops internalCallback with dayServiceInterval as number of milliseconds in day services, nightServiceInterval with number of milliseconds
	const start = async (dayServiceInterval, nightServiceInterval) => {
		const internalCallback = async => {
			let interval;
			interval = getCurrentTimestamp() < 21600 ? interval = nightServiceInterval : interval = dayServiceInterval;
			sendGetRequest();
			console.log('Updating realtime feed at ' + getCurrentTime());
			setTimeout(internalCallback, interval);
		};

		internalCallback();
	};

	const sendGetRequest = async () => {
		try {
			const response = await axios({
				method: 'GET',
				timeout: 15000,
				url: config.apiUrl,
				responseType: 'arraybuffer',
				// headers: {
				// 	'x-api-key': config.apiKey
				// }
			});
			if (response.status === 200) {
				feed = GtfsRealtimeBindings.transit_realtime.FeedMessage.decode(response.data);
				console.log('FeedMessage data: ', response.data)
				//feed = await filterFeed(feed);
				console.log('Realtime feed: ', feed)
				console.log('Successful GTFS-R response...');
			}
		} catch (error) {
			console.error(error);
		}
	};

	const getFeed = async => feed;

	start(dayServiceInterval, nightServiceInterval);

	return {
		start,
		queries: await queries.register(getFeed)
	};
};

module.exports = client;
