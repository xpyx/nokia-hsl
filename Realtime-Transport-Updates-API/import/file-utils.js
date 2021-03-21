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

const fs = require('fs-extra');
const unzipper = require('unzipper');

/*
 * Attempt to parse the specified config JSON file.
 */
exports.getConfig = async config => {
	try {
		const parsedConfig = JSON.parse(config);
		return parsedConfig;
	} catch (error) {
		console.error(new Error('Cannot parse configuration file. Check to ensure that it is valid JSON.'));
		throw error;
	}
};

/*
 * Prepare the specified directory for saving HTML timetables by deleting
 * everything.
 */
exports.prepDirectory = async exportPath => {
	await fs.remove(exportPath);
	await fs.ensureDir(exportPath);
};

/*
 * Unzip a zipfile into a specified directory
 */
exports.unzip = (zipfilePath, exportPath) => {
	/* eslint-disable new-cap */
	return fs.createReadStream(zipfilePath)
		.pipe(unzipper.Extract({ path: exportPath }))
		.on('entry', entry => entry.autodrain())
		.promise();
	/* eslint-enable new-cap */
};
