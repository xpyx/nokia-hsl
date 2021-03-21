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

const readline = require('readline');
const { noop } = require('lodash');
const chalk = require('chalk');

/*
 * Returns a log function based on config settings
 */
exports.log = config => {
	if (config.verbose === false) {
		return noop;
	}

	if (config.logFunction) {
		return config.logFunction;
	}

	return (text, overwrite) => {
		if (overwrite === true) {
			readline.clearLine(process.stdout, 0);
			readline.cursorTo(process.stdout, 0);
		} else {
			process.stdout.write('\n');
		}

		process.stdout.write(text);
	};
};

/*
 * Returns an warning log function based on config settings
 */
exports.logWarning = config => {
	if (config.logFunction) {
		return config.logFunction;
	}

	return text => {
		process.stdout.write(`\n${exports.formatWarning(text)}\n`);
	};
};

/*
 * Returns an error log function based on config settings
 */
exports.logError = config => {
	if (config.logFunction) {
		return config.logFunction;
	}

	return text => {
		process.stdout.write(`\n${exports.formatError(text)}\n`);
	};
};

/*
 * Format console warning text
 */
exports.formatWarning = text => {
	return `${chalk.yellow.underline('Warning')}${chalk.yellow(':')} ${chalk.yellow(text)}`;
};

/*
 * Format console error text
 */
exports.formatError = error => {
	return `${chalk.red.underline('Error')}${chalk.red(':')} ${chalk.red(error.message.replace('Error: ', ''))}`;
};
