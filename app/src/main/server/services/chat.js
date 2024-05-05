const chat = require('../models/chat');
const user = require('./user');
const messageCounter = require('./messageCounter');
const sock_io = require('../app');
const createChat = async (chatUsers) => {
    if (!await user.isUsernameTaken(chatUsers[1])) {
        throw new Error("User not found");
    }
    if (chatUsers[0] === chatUsers[1]) {
        throw new Error("Cannot create chat with yourself");
    }
    const newChat = new chat({
        users: [await user.getUserByUsername(chatUsers[0]), await user.getUserByUsername(chatUsers[1])],
        messages: [],
        lastMessage: null
    });
    return await newChat.save();
}

const getChatById = async (id) => {
    return await chat.findById(id);
}

const deleteChatById = async (id) => {
    return await chat.findByIdAndDelete(id);
}

const getChatsByUserId = async (userId) => {
    return await chat.find({ $or: [{ 'users.0.username': userId }, { 'users.1.username': userId }] });
}

async function getsChatUsers(chatId) {
    const chat = await getChatById(chatId);
    return chat.users;
}

const addMessage = async (chatId, message, sender) => {
    const date = new Date();
    const newMessage = {
        id: await messageCounter.incrementMessageCounter(),
        content: message,
        sender: await user.getUserByUsername(sender),
        created: date.toISOString()
    };
    const users = await getsChatUsers(chatId);
    for (let i = 0; i < users.length; i++) {
        const user = users[i];
        if (user.username in sock_io.clientSocketIds && user.username !== sender) {
            sock_io.io.to(sock_io.clientSocketIds[user.username]).emit('get_message', {id: chatId, message:newMessage});
        }
    }
    
    const chat = await getChatById(chatId);
    chat.messages.push(newMessage);
    chat.lastMessage = newMessage;
    await chat.save();
    return newMessage;
}

const getMessages = async (chatId) => {
    const chat = await getChatById(chatId);
    return chat.messages;
}

module.exports = { createChat, getChatById, deleteChatById, getChatsByUserId, addMessage, getMessages, getsChatUsers };