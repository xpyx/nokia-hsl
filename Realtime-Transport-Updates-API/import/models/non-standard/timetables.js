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

module.exports = {
	filenameBase: 'timetables',
	nonstandard: true,
	schema: [
		{
			name: 'id',
			type: 'integer',
			primary: true
		},
		{
			name: 'timetable_id',
			type: 'varchar(255)'
		},
		{
			name: 'route_id',
			type: 'varchar(255)'
		},
		{
			name: 'direction_id',
			type: 'integer',
			min: 0,
			max: 1
		},
		{
			name: 'start_date',
			type: 'integer'
		},
		{
			name: 'end_date',
			type: 'integer'
		},
		{
			name: 'monday',
			type: 'integer',
			required: true,
			min: 0,
			max: 1
		},
		{
			name: 'tuesday',
			type: 'integer',
			required: true,
			min: 0,
			max: 1
		},
		{
			name: 'wednesday',
			type: 'integer',
			required: true,
			min: 0,
			max: 1
		},
		{
			name: 'thursday',
			type: 'integer',
			required: true,
			min: 0,
			max: 1
		},
		{
			name: 'friday',
			type: 'integer',
			required: true,
			min: 0,
			max: 1
		},
		{
			name: 'saturday',
			type: 'integer',
			required: true,
			min: 0,
			max: 1
		},
		{
			name: 'sunday',
			type: 'integer',
			required: true,
			min: 0,
			max: 1
		},
		{
			name: 'start_time',
			type: 'varchar(255)'
		},
		{
			name: 'start_timestamp',
			type: 'integer'
		},
		{
			name: 'end_time',
			type: 'varchar(255)'
		},
		{
			name: 'end_timestamp',
			type: 'integer'
		},
		{
			name: 'timetable_label',
			type: 'varchar(255)'
		},
		{
			name: 'service_notes',
			type: 'varchar(255)'
		},
		{
			name: 'orientation',
			type: 'varchar(255)'
		},
		{
			name: 'timetable_page_id',
			type: 'varchar(255)'
		},
		{
			name: 'timetable_sequence',
			type: 'integer',
			min: 0,
			index: true
		},
		{
			name: 'direction_name',
			type: 'varchar(255)'
		},
		{
			name: 'include_exceptions',
			type: 'integer',
			min: 0,
			max: 1
		},
		{
			name: 'show_trip_continuation',
			type: 'integer',
			min: 0,
			max: 1
		}
	]
};
