'use-strict';

// Here we bundle all routes we add to the API into the index.js

const queries = require('./queries');

module.exports.register = async server => {
	await queries.register(server);
};
