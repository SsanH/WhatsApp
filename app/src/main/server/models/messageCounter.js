const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const messageCounterSchema = new Schema({
    counter: Number
});

module.exports = mongoose.model('messageCounter', messageCounterSchema);