const chat = require('../services/chat');
const admin = require('../firebase');
const fcmMap = require('../models/fcmMap');

const createChat = async (req, res) => {
    try {
        const newChat = await chat.createChat([req.username, req.body.username]);
        return await res.status(200).json({ id: newChat._id, user: newChat.users[1]});
    }
    catch (error) {
        return error.message === "User not found" ? await res.status(404).json({ message: "User not found" }) : await res.status(409).json({ message: "Cannot create chat with yourself" });
    }
}

const getChatById = async (req, res) => {
    const chat = await chat.getChatById(req.params.id);
    if (chat === null) {
        return await res.status(404).json({ message: "Chat not found" });
    }
    return await res.status(200).json({users: chat.users, messages: chat.messages, lastMessage: chat.lastMessage});
}

const deleteChatById = async (req, res) => {
    const deletedChat = await chat.deleteChatById(req.params.id);
    if (deletedChat === null) {
        return await res.status(404).json({ message: "Chat not found" });
    }
    return await res.status(204);
}

const getChatsByUserId = async (req, res) => {
    const chats = await chat.getChatsByUserId(req.username);
    let chatsInfo = [];
    for (let i = 0; i < chats.length; i++) {
        let chatInfo = {
            id: chats[i]._id,
            user: chats[i].users[1].username === req.username ? chats[i].users[0] : chats[i].users[1],
            lastMessage: chats[i].lastMessage
        }
        chatsInfo.push(chatInfo);
    }
    return await res.status(200).send(chatsInfo);
}

const addMessage = async (req, res) => {
    const chatId = req.params.id;
    const message = req.body.msg;
    const sender = req.username;
    try {
        const newMessage = await chat.addMessage(chatId, message, sender);
        const chatInfo = await chat.getChatById(chatId);
        const receiver = chatInfo.users[0].username === sender ? chatInfo.users[1].username : chatInfo.users[0].username;
        androidUser = await fcmMap.findOne({username : receiver});
        if (androidUser !== null) {
            try {
              await admin.messaging().send({
                token: androidUser.appToken,
                notification: {
                  title: newMessage.sender.username,
                  body: newMessage.content,
                },
                data: {
                  id: newMessage.id.toString(),
                  created: newMessage.created,
                  content: newMessage.content,
                  senderName: newMessage.sender.username,
                },
              });
            } catch (error) {
              console.error('Error sending notification:', error);
            }
          }
          
        return await res.status(201).send({
            id: newMessage.id,
            created: newMessage.created,
            content: newMessage.content,
            sender: {username: newMessage.sender.username} 
        });
    }
    catch (error) {
        return await res.status(404).json({ message: "Chat not found" });
    }
}

const getMessages = async (req, res) => {
    const chatId = req.params.id;
    const messages = await chat.getMessages(chatId);
    return await res.status(200).send(messages);
}

module.exports = { createChat, getChatById, deleteChatById, getChatsByUserId, addMessage, getMessages };