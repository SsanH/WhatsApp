const mongoose = require('mongoose');
const User = require('./user').schema;
const Message = require('./message').schema;

const Schema = mongoose.Schema;

const chatSchema = new Schema({
    users: [User],
    messages: [Message],
    lastMessage: Message
});

module.exports = mongoose.model('Chat', chatSchema);