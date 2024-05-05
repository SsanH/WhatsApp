const chatController = require('../controllers/chat');
const { isLoggedIn } = require('../controllers/login');
const admin = require('../firebase');

const express = require('express');
var router = express.Router();

router.post('/', isLoggedIn, chatController.createChat);
router.get('/', isLoggedIn, chatController.getChatsByUserId);
router.get('/:id', isLoggedIn, chatController.getChatById);
router.delete('/:id', isLoggedIn, chatController.deleteChatById);
router.post('/:id/Messages', isLoggedIn, chatController.addMessage);
router.get('/:id/Messages', isLoggedIn, chatController.getMessages);

module.exports = router;