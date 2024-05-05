const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const messageSchema = new Schema({
    id: Number,
    content: String,
    sender: { username : String },
    created: String
});

module.exports = mongoose.model('Message', messageSchema);