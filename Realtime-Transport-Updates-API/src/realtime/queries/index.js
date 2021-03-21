'use-strict';

const utils = require('../utils');

/* eslint-disable no-await-in-loop, max-depth */
const register = async getFeed => {
	const updateResultsWithRealtime = async query => {
		const feed = await getFeed();
		if (feed) {
			try {
				query.realtime_timestamp = feed.header.timestamp.low;
				const secondsSinceMidnightTimestamp = query.since_midnight_timestamp;
				for (let element of query.response) {
					const feedEntity = feed.entity.find(object => object.id === element.trip_id);

					if (feedEntity) {
						if (element.scheduleRelationship !== 3) {
							element.is_realtime = true;
							if (feedEntity.tripUpdate.stopTimeUpdate) {
								const stopTimeUpdates = feedEntity.tripUpdate.stopTimeUpdate;
								element = await utils.findNearestStopDelay(element, stopTimeUpdates);
							}
						}
					} else {
						element.is_realtime = false;
					}

					const departureTimestamp = element.departure_timestamp;
					const arrivalTimestamp = element.arrival_timestamp;

					// If departure/arrival timestamp is before the time we made the query, set arrived to be true, arrived trips will be filtered out
					if (departureTimestamp) {
						if (departureTimestamp < secondsSinceMidnightTimestamp) {
							element.arrived = true;
						} else {
							element.due_in = await utils.getDueInValue(departureTimestamp, secondsSinceMidnightTimestamp);
						}
					} else if (arrivalTimestamp) {
						if (arrivalTimestamp < secondsSinceMidnightTimestamp) {
							element.arrived = true;
						} else {
							element.due_in = await utils.getDueInValue(arrivalTimestamp, secondsSinceMidnightTimestamp);
						}
					} else {
						console.log('Error, no departure or arrival timestamp available...');
					}

					// Convert any wrapped times and timestamps to normal times e.g convert 25:30:00 to 01:30:00
					if (departureTimestamp >= 86400) {
						const unWrappedTimestamp = await utils.getWrappedTimeStampUnwrapped(departureTimestamp);
						element.departure_timestamp = unWrappedTimestamp;
						element.departure_time = await utils.getTimestampAsTimeFormatted(unWrappedTimestamp);
					}

					if (arrivalTimestamp >= 86400) {
						const unWrappedTimestamp = await utils.getWrappedTimeStampUnwrapped(arrivalTimestamp);
						element.arrival_timestamp = arrivalTimestamp;
						element.arrival_time = await utils.getTimestampAsTimeFormatted(unWrappedTimestamp);
					}
				}
			} catch {
				console.log('No realtime feed available...');
			}
		}

		// Remove trips arriving before current timestamp and cancelled trips
		query.response = query.response.filter(element => Object.prototype.hasOwnProperty.call(element, 'arrived') === false);
		query.response = query.response.sort((a, b) => (a.arrival_timestamp >= b.arrival_timestamp) ? 1 : -1);
		return query;
	};

	return {
		updateResultsWithRealtime
	};
};

module.exports = { register };
