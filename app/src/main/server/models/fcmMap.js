const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const fcmMapSchema = new Schema({
    username: String,
    appToken: String
});

module.exports = mongoose.model('fcmMap', fcmMapSchema);