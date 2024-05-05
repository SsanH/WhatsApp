const User = require('../models/user');

const isUsernameTaken = async (username) => {
    const user = await User.findOne({ username });
    return user !== null;
}

const createUser = async (username, password, displayName, profilePic) => {
    if (await isUsernameTaken(username)) {
        throw new Error('Username is already taken');
    }
    const newUser = new User({
        username,
        password,
        displayName,
        profilePic,
    });
    return await newUser.save();
}

const getUserByUsername = async (username) => {
    const user = await User.findOne({ username });
    if (!user) {
        throw new Error('User not found');
    }
    return user;
}

module.exports = { createUser, getUserByUsername, isUsernameTaken };