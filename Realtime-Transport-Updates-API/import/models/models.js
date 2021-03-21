/*
  The MIT License (MIT)

  Copyright (c) 2012 Brendan Nee <brendan@blinktag.com>

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

/* eslint-disable no-unused-vars */

const agency = require('./gtfs/agency');
const attributions = require('./gtfs/attributions');
const calendarDates = require('./gtfs/calendar-dates');
const calendar = require('./gtfs/calendar');
const fareAttributes = require('./gtfs/fare-attributes');
const fareRules = require('./gtfs/fare-rules');
const feedInfo = require('./gtfs/feed-info');
const frequencies = require('./gtfs/frequencies');
const levels = require('./gtfs/levels');
const pathways = require('./gtfs/pathways');
const routes = require('./gtfs/routes');
const shapes = require('./gtfs/shapes');
const stopTimes = require('./gtfs/stop-times');
const stops = require('./gtfs/stops');
const transfers = require('./gtfs/transfers');
const translations = require('./gtfs/translations');
const trips = require('./gtfs/trips');

const directions = require('./non-standard/directions');
const stopAttributes = require('./non-standard/stop-attributes');
const timetables = require('./non-standard/timetables');
const timetablePages = require('./non-standard/timetable-pages');
const timetableStopOrder = require('./non-standard/timetable-stop-order');
const timetableNotes = require('./non-standard/timetable-notes');
const timetableNotesReferences = require('./non-standard/timetable-notes-references');

module.exports = [
	agency,
	routes,
	stops,
	trips,
	calendar,
	calendarDates,
	transfers,
	shapes,
	stopTimes
];
