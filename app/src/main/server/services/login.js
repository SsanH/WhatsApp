const fcmMap = require('../models/fcmMap');
const User = require('../models/user');
const jwt = require('jsonwebtoken');

const processLogin = async (user) => {
    const matchingUser = await User.findOne({ username: user.username });
    if (!matchingUser) {
      throw new Error('User not found');
    }
    if (matchingUser.password !== user.password) {
      throw new Error('Password is incorrect');
    }
    const token = jwt.sign({
      username: user.username,
    }, "san please don't tell anyone");
    if (user.appToken) {
      if (await fcmMap.findOne({ appToken: user.appToken }) !== null) {
        await fcmMap.deleteOne({ appToken: user.appToken });
      }
      await fcmMap.create({ username: user.username, appToken: user.appToken });
    }
    return token;
  };  

module.exports = { processLogin };