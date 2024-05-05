const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const userSchema = new Schema({
    username: String,
    password: String,
    displayName: String,
    profilePic: String,
});


module.exports = mongoose.model('User', userSchema);