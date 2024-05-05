const fcmMap = require('../models/fcmMap');
const userServices = require('../services/user');

const createUser = async (req, res) => {
    var newUser;
    try {
        newUser = await userServices.createUser(req.body.username, req.body.password, req.body.displayName, req.body.profilePic);
        return res.status(201).json(newUser);
    }
    catch (err) {
        return res.status(409).json({ message: err.message });
    }
}

const getUser = async (req, res) => {
    const username = req.params.username;
    try {
        const user = await userServices.getUserByUsername(username);
        return res.status(200).json(user);
    }
    catch (err) {
        return res.status(404).json({ message: err.message });
    }
}

module.exports = { createUser, getUser };