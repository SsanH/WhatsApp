const userController = require('../controllers/user');
const { isLoggedIn } = require('../controllers/login');

const express = require('express');
var router = express.Router();

router.route('/').post(userController.createUser);
router.route('/:username').get(isLoggedIn, userController.getUser);

module.exports = router;