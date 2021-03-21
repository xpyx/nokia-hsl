'use-strict';

/* eslint-disable import/no-unassigned-import, no-unused-vars */
const moment = require('moment');
require('moment/locale/en-ie');
moment.locale('en-ie');

/* eslint-disable no-await-in-loop */
const findNearestStopDelay = async (element, stopTimeUpdates) => {
	for (let index = stopTimeUpdates.length - 1; index >= 0; index--) {
		if (stopTimeUpdates[index].stopSequence <= element.stop_sequence) {
			const departureDelay = stopTimeUpdates[index].departure ? stopTimeUpdates[index].departure.delay : 0;
			const arrivalDelay = stopTimeUpdates[index].arrival ? stopTimeUpdates[index].arrival.delay : 0;
			element.departure_timestamp += departureDelay;
			element.arrival_timestamp += arrivalDelay;
			element.departure_time = await getTimestampAsTimeFormatted(element.departure_timestamp);
			element.arrival_time = await getTimestampAsTimeFormatted(element.arrival_timestamp);
			break;
		}
	}

	return element;
};

const filterFeed = async feed => {
	feed.entity = feed.entity.filter(feedEntity => {
		return feedEntity.id.includes('b12') ||
        feedEntity.id.includes('d12');
	});
	return feed;
};

const getDueInValue = async (arrivalTimestamp, timestamp) => {
	const eta = arrivalTimestamp - timestamp;
	return eta >= 60 ? Math.round(eta / 60).toString() : 'Due';
};

const getTimestampAsTimeFormatted = async timestamp => {
	const time = [0, 0, 0];
	time[0] = String(Math.floor(Math.floor(timestamp / 3600))).padStart(2, '0');
	time[1] = String(Math.floor(Math.floor(timestamp / 60) % 60) % 60).padStart(2, '0');
	time[2] = String(timestamp % 60).padStart(2, '0');
	return time.join(':');
};

const getWrappedTimeStampUnwrapped = async timestamp => {
	// If timestamp is not a wrapped timestamp, return the timestamp without change
	if (timestamp < 86400) {
		return timestamp;
	}

	return timestamp - 86400; // Timestamp - 24 hours in seconds
};

// Get seconds since midnight
const getCurrentTimestamp = async => {
	let time = moment().format('LTS').split(':');
	time = (Number(time[0]) * 3600) + (Number(time[1]) * 60) + Number(time[2]);
	return time;
};

const getCurrentTime = async => {
	return moment().format('LTS');
};

module.exports = {
	findNearestStopDelay,
	filterFeed,
	getDueInValue,
	getTimestampAsTimeFormatted,
	getWrappedTimeStampUnwrapped,
	getCurrentTimestamp,
	getCurrentTime
};
