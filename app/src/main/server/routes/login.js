const loginController = require('../controllers/login');

const express = require('express');
var router = express.Router()

router.route('/').post(loginController.processLogin);

module.exports = router;