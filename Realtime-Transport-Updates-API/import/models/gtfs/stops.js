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
	filenameBase: 'stops',
	schema: [
		{
			name: 'stop_id',
			type: 'varchar(255)',
			primary: true
		},
		{
			name: 'stop_code',
			type: 'varchar(255)'
		},
		{
			name: 'stop_name',
			type: 'varchar(255)'
		},
		{
			name: 'tts_stop_name',
			type: 'varchar(255)'
		},
		{
			name: 'stop_desc',
			type: 'varchar(255)'
		},
		{
			name: 'stop_lat',
			type: 'real',
			min: -90,
			max: 90
		},
		{
			name: 'stop_lon',
			type: 'real',
			min: -180,
			max: 180
		},
		{
			name: 'zone_id',
			type: 'varchar(255)'
		},
		{
			name: 'stop_url',
			type: 'varchar(1020)'
		},
		{
			name: 'location_type',
			type: 'integer',
			min: 0,
			max: 4
		},
		{
			name: 'parent_station',
			type: 'varchar(255)',
			index: true
		},
		{
			name: 'stop_timezone',
			type: 'varchar(255)'
		},
		{
			name: 'wheelchair_boarding',
			type: 'integer',
			min: 0,
			max: 2
		},
		{
			name: 'level_id',
			type: 'varchar(255)'
		},
		{
			name: 'platform_code',
			type: 'varchar(255)'
		}
	]
};
