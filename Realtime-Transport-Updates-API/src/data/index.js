'use strict';

const queries = require('./queries');
const sql = require('mssql');

const client = async (server, config) => {
	let pool = null;

	const getConnection = async () => {
		try {
			if (pool) {
				return pool;
			}

			pool = await sql.connect(config);
			pool.on('error', async error => {
				await closePool();
				throw error;
			});
			return pool;
		} catch (error) {
			pool = null;
			throw error;
		}
	};

	const closePool = async () => {
		try {
			await pool.close();
			pool = null;
		} catch (error) {
			pool = null;
			throw error;
		}
	};

	return {
		queries: await queries.register({ sql, getConnection }),
		getConnection,
		closePool
	};
};

module.exports = client;
