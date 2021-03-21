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
	filenameBase: 'feed_info',
	schema: [
		{
			name: 'id',
			type: 'integer',
			primary: true
		},
		{
			name: 'feed_publisher_name',
			type: 'varchar(255)',
			required: true
		},
		{
			name: 'feed_publisher_url',
			type: 'varchar(2047)',
			required: true
		},
		{
			name: 'feed_lang',
			type: 'varchar(255)',
			required: true
		},
		{
			name: 'default_lang',
			type: 'varchar(255)'
		},
		{
			name: 'feed_start_date',
			type: 'integer'
		},
		{
			name: 'feed_end_date',
			type: 'integer'
		},
		{
			name: 'feed_version',
			type: 'varchar(255)'
		},
		{
			name: 'feed_contact_email',
			type: 'varchar(255)'
		},
		{
			name: 'feed_contact_url',
			type: 'varchar(2047)'
		}
	]
};
