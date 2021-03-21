/*
  The MIT License (MIT)

  Copyright (c) 2012 Brendan Nee <brendan@blinktag.com>
  Copyright (c) 2020 Matthew O'Leary <matthew.oleary58@gmail.com>

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

'use-strict';

const path = require('path');

const fetch = require('node-fetch');
const fs = require('fs-extra');
const parse = require('csv-parse');
const stripBomStream = require('strip-bom-stream');
const tmp = require('tmp-promise');
const untildify = require('untildify');
const Promise = require('bluebird');

const models = require('./models/models');
const { connectDb } = require('./db');
const { unzip } = require('./file-utils');
const logUtils = require('./log-utils');
const utils = require('./utils');
const { addCustomColumns, addCustomValues, addCustomIndexes } = require('./custom-queries');

const downloadFiles = async task => {
	task.log(`Downloading GTFS from ${task.agency_url}`);

	task.path = `${task.downloadDir}/${task.agency_key}-gtfs.zip`;

	const response = await fetch(task.agency_url, { method: 'GET', headers: task.agency_headers || {} });

	if (response.status !== 200) {
		throw new Error('Couldn’t download files');
	}

	const buffer = await response.buffer();

	await fs.writeFile(task.path, buffer);
	task.log('Download successful');
};

const getTextFiles = async folderPath => {
	const files = await fs.readdir(folderPath);
	return files.filter(filename => filename.slice(-3) === 'txt');
};

const readFiles = async task => {
	const gtfsPath = untildify(task.path);
	task.log(`Importing GTFS from ${task.path}\r`);
	if (path.extname(gtfsPath) === '.zip') {
		try {
			await unzip(gtfsPath, task.downloadDir);
			const textFiles = await getTextFiles(task.downloadDir);

			// If no .txt files in this directory, check for subdirectories and copy them here
			if (textFiles.length === 0) {
				const files = await fs.readdir(task.downloadDir);
				const folders = files.map(filename => path.join(task.downloadDir, filename)).filter(source => fs.lstatSync(source).isDirectory());

				if (folders.length > 1) {
					throw new Error(`More than one subfolder found in zip file at ${task.path}. Ensure that .txt files are in the top level of the zip file, or in a single subdirectory.`);
				} else if (folders.length === 0) {
					throw new Error(`No .txt files found in ${task.path}. Ensure that .txt files are in the top level of the zip file, or in a single subdirectory.`);
				}

				const subfolderName = folders[0];
				const directoryTextFiles = await getTextFiles(subfolderName);

				if (directoryTextFiles.length === 0) {
					throw new Error(`No .txt files found in ${task.path}. Ensure that .txt files are in the top level of the zip file, or in a single subdirectory.`);
				}

				await Promise.all(directoryTextFiles.map(async fileName => fs.rename(path.join(subfolderName, fileName), path.join(task.downloadDir, fileName))));
			}
		} catch (error) {
			task.error(error);
			console.error(error);
			throw new Error(`Unable to unzip file ${task.path}`);
		}
	} else {
		// Local file is unzipped, just copy it from there.
		await fs.copy(gtfsPath, task.downloadDir);
	}
};

const createEmptyTables = async cnx => {
	return Promise.all(models.map(async model => {
		if (!model.schema) {
			return;
		}

		const request = await cnx.request();
		await request.query(`DROP TABLE IF EXISTS ${model.filenameBase};`);

		const columns = model.schema.map(column => {
			let check = '';
			if (column.min !== undefined && column.max) {
				check = `CHECK( ${column.name} >= ${column.min} AND ${column.name} <= ${column.max} )`;
			} else if (column.min) {
				check = `CHECK( ${column.name} >= ${column.min} )`;
			} else if (column.max) {
				check = `CHECK( ${column.name} <= ${column.max} )`;
			}

			const primary = column.primary ? 'PRIMARY KEY' : '';
			const identity = column.identity ? 'IDENTITY(1,1)' : '';
			const required = column.required ? 'NOT NULL' : '';
			const columnDefault = column.default ? 'DEFAULT ' + column.default : '';
			return `${column.name} ${column.type} ${identity} ${required} ${check} ${primary} ${columnDefault}`;
		});

		await request.query(`CREATE TABLE ${model.filenameBase} (${columns.join(', ')});`);

		await Promise.all(model.schema.map(async column => {
			if (column.index) {
				await request.query(`DROP INDEX IF EXISTS ${model.filenameBase}.idx_${model.filenameBase}_${column.name}`);
				const unique = column.index === 'unique' ? 'UNIQUE' : '';
				await request.query(`CREATE ${unique} INDEX idx_${model.filenameBase}_${column.name} ON ${model.filenameBase} (${column.name});`);
			}
		}));
	}));
};

const formatLine = (line, model, totalLineCount) => {
	const lineNumber = totalLineCount + 1;
	for (const fieldName of Object.keys(line)) {
		const columnSchema = model.schema.find(schema => schema.name === fieldName);

		// Remove columns not part of model
		if (!columnSchema) {
			delete line[fieldName];
			continue;
		}

		// Remove null values
		if (line[fieldName] === null || line[fieldName] === '') {
			delete line[fieldName];
		}

		// Convert fields that should be integer
		if (columnSchema.type === 'integer') {
			const value = Number.parseInt(line[fieldName], 10);

			if (Number.isNaN(value)) {
				delete line[fieldName];
			} else {
				line[fieldName] = value;
			}
		}

		// Convert fields that should be float
		if (columnSchema.type === 'real') {
			const value = Number.parseFloat(line[fieldName]);

			if (Number.isNaN(value)) {
				delete line[fieldName];
			} else {
				line[fieldName] = value;
			}
		}

		// Validate required
		if (columnSchema.required === true) {
			if (line[fieldName] === undefined || line[fieldName] === '') {
				throw new Error(`Missing required value in ${model.filenameBase}.txt for ${fieldName} on line ${lineNumber}.`);
			}
		}

		// Validate minimum
		if (columnSchema.min !== undefined) {
			if (line[fieldName] < columnSchema.min) {
				throw new Error(`Invalid value in ${model.filenameBase}.txt for ${fieldName} on line ${lineNumber}: below minimum value of ${columnSchema.min}.`);
			}
		}

		// Validate maximum
		if (columnSchema.max !== undefined) {
			if (line[fieldName] > columnSchema.max) {
				throw new Error(`Invalid value in ${model.filenameBase}.txt for ${fieldName} on line ${lineNumber}: above maximum value of ${columnSchema.max}.`);
			}
		}
	}

	// Convert to midnight timestamp
	const timestampFormat = [
		'start_time',
		'end_time',
		'arrival_time',
		'departure_time'
	];

	for (const fieldName of timestampFormat) {
		if (line[fieldName]) {
			line[`${fieldName}stamp`] = utils.calculateHourTimestamp(line[fieldName]);
		}
	}

	return line;
};

/* eslint-disable no-eq-null, eqeqeq, unicorn/no-for-loop, no-negated-condition */
const importLines = async (task, lines, model, totalLineCount) => {
	if (lines.length === 0) {
		return;
	}

	const linesToImportCount = lines.length;
	const values = [];
	const fieldNames = model.schema.map(column => column.name);
	const fieldNamesInUse = [];

	const request = await task.cnx.request();
	let value = '';

	while (lines.length) {
		const line = lines.pop();
		const valueList = [];
		for (let i = 0; i < fieldNames.length; i++) {
			const fieldName = fieldNames[i];
			// If column is not an identity column, and value of line[fieldName] is undefined, insert null
			if (!(model.schema[i].identity)) {
				if (line[fieldName] != null) {
					// Replace single quotes within strings with double single quotes to prevent sql error
					value = line[fieldName].toString();
					value = value.replaceAll('\'', '\'\'');
					valueList.push('\'' + value + '\'');
				} else {
					valueList.push('NULL');
				}

				if (!fieldNamesInUse.includes(fieldName)) {
					fieldNamesInUse.push(fieldName);
				}
			}
		}

		values.push(valueList);
	}

	const formattedValues = [];

	for (const value of values) {
		const formattedValue = '(' + value.join(', ') + ')';
		formattedValues.push(formattedValue);
	}

	try {
		await request.query(`INSERT INTO ${model.filenameBase}(${fieldNamesInUse.join(', ')}) VALUES ${formattedValues.join(',')};`);
	} catch (error) {
		task.warn(`Check ${model.filenameBase}.txt for invalid data between lines ${totalLineCount - linesToImportCount} and ${totalLineCount}.`);
		throw error;
	}

	task.log(`Importing - ${model.filenameBase}.txt - ${totalLineCount} lines imported\r`, true);
};

/* eslint-disable no-use-extend-native/no-use-extend-native */
const importFiles = task => {
	// Loop through each GTFS file
	return Promise.mapSeries(models, model => {
		return new Promise((resolve, reject) => {
			// Filter out excluded files from config
			if (task.exclude && task.exclude.includes(model.filenameBase)) {
				task.log(`Skipping - ${model.filenameBase}.txt\r`);
				return resolve();
			}

			const filepath = path.join(task.downloadDir, `${model.filenameBase}.txt`);

			if (!fs.existsSync(filepath)) {
				if (!model.nonstandard) {
					task.log(`Importing - ${model.filenameBase}.txt - No file found\r`);
				}

				return resolve();
			}

			task.log(`Importing - ${model.filenameBase}.txt\r`);

			const lines = [];
			let totalLineCount = 0;
			const maxInsertVariables = 800;
			const parser = parse({
				columns: true,
				relax: true,
				trim: true,
				skip_empty_lines: true,
				...task.csvOptions
			});

			parser.on('readable', async () => {
				let record;
				/* eslint-disable-next-line no-cond-assign */
				while (record = parser.read()) {
					try {
						totalLineCount += 1;
						lines.push(formatLine(record, model, totalLineCount));

						// If we have a bunch of lines ready to insert, then do it
						if (lines.length >= maxInsertVariables / model.schema.length) {
							/* eslint-disable-next-line no-await-in-loop */
							await importLines(task, lines, model, totalLineCount);
						}
					} catch (error) {
						reject(error);
					}
				}
			});

			parser.on('end', async () => {
				// Insert all remaining lines
				await importLines(task, lines, model, totalLineCount).catch(reject);
				resolve();
			});

			parser.on('error', reject);

			fs.createReadStream(filepath)
				.pipe(stripBomStream())
				.pipe(parser);
		})
			.catch(error => {
				throw error;
			});
	});
};

const executeCustomSqlQueries = async task => {
	// Any custom queries to db after initial GTFS setup can be called here
	task.log('Executing custom queries on database: ' + task.cnx.config.database);

	// Add custom columns to tables
	try {
		await addCustomColumns(task);
	} catch (error) {
		task.warn('Error adding custom columns to tables');
		throw error;
	}

	// Add values
	try {
		await addCustomValues(task);
	} catch (error) {
		task.warn('Error updating custom columns');
		throw error;
	}

	// Adding indexes
	try {
		await addCustomIndexes(task);
	} catch (error) {
		task.warn('Error creating indexes...');
		throw error;
	}

	task.log('Successfully executed custom SQL queries... \n');
};

module.exports = async config => {
	const log = logUtils.log(config);
	const logError = logUtils.logError(config);
	const logWarning = logUtils.logWarning(config);
	const cnx = await connectDb();

	const agencyCount = config.agencies.length;
	log(`Starting the GTFS import for ${agencyCount} ${utils.pluralize('file', agencyCount)} \n`);

	await createEmptyTables(cnx);

	await Promise.mapSeries(config.agencies, async agency => {
		if (!agency.agency_key) {
			throw new Error('No Agency Key provided.');
		}

		if (!agency.url && !agency.path) {
			throw new Error('No Agency URL or path provided.');
		}

		const { path, cleanup } = await tmp.dir({ unsafeCleanup: true });

		const task = {
			exclude: agency.exclude,
			agency_key: agency.agency_key,
			agency_url: agency.url,
			agency_headers: agency.headers || false,
			downloadDir: path,
			path: agency.path,
			csvOptions: config.csvOptions || {},
			cnx,
			log: (message, overwrite) => {
				log(`${task.agency_key}: ${message}`, overwrite);
			},
			warn: message => {
				logWarning(message);
			},
			error: message => {
				logError(message);
			}
		};

		if (task.agency_url) {
			await downloadFiles(task);
		}

		await readFiles(task);
		await importFiles(task);
		await executeCustomSqlQueries(task);

		cleanup();
		task.log('Completed GTFS import');
	});

	log(`Completed GTFS import for ${agencyCount} ${utils.pluralize('file', agencyCount)}\n`);
};
