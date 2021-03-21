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
	filenameBase: 'routes',
	schema: [
		{
			name: 'route_id',
			type: 'varchar(255)',
			primary: true
		},
		{
			name: 'agency_id',
			type: 'varchar(255)'
		},
		{
			name: 'route_short_name',
			type: 'varchar(255)'
		},
		{
			name: 'route_long_name',
			type: 'varchar(255)'
		},
		{
			name: 'route_desc',
			type: 'varchar(255)'
		},
		{
			name: 'route_type',
			type: 'integer',
			required: true,
			min: 0
			// Support extended GTFS route types with no max value
			// https://developers.google.com/transit/gtfs/reference/extended-route-types
		},
		{
			name: 'route_url',
			type: 'varchar(1020)'
		},
		{
			name: 'route_color',
			type: 'varchar(255)'
		},
		{
			name: 'route_text_color',
			type: 'varchar(255)'
		},
		{
			name: 'route_sort_order',
			type: 'integer',
			min: 0
		},
		{
			name: 'continuous_pickup',
			type: 'integer',
			min: 0,
			max: 3
		},
		{
			name: 'continuous_drop_off',
			type: 'integer',
			min: 0,
			max: 3
		}
	]
};
