const sql = require('mssql');
const { dockerDbConfig } = require('./config');
let pool = null;

exports.connectDb = async () => {
	try {
		if (pool) {
			return pool;
		}

		pool = await sql.connect(dockerDbConfig);
		pool.on('error', async err => {
			console.log(err);
			await this.disconnectDb();
		});
		return pool;
	} catch (error) {
		console.log(error);
		pool = null;
	}
};

exports.disconnectDb = async () => {
	try {
		await pool.close();
		pool = null;
	} catch (error) {
		pool = null;
		console.log(error);
	}
};
