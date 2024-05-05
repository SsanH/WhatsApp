const login = require('../services/login');
const jwt = require('jsonwebtoken');

const processLogin = async (req, res) => {
    try {
        const token = await login.processLogin(req.body);
        return res.status(201).send(token);
    } catch (error) {
        return res.status(401).json({ message: error.message });
    }
}

const isLoggedIn = (req, res, next) => {
    if (!req.headers.authorization) {
        return res.status(401).send('Authorization header is required');
    }
    const token = req.headers.authorization.split(' ')[1];
    try {
        const data = jwt.verify(token, "san please don't tell anyone");
        req.username = data.username;
        next();
    } catch (error) {
        return res.status(401).send('Invalid JWT token');
    }
}

module.exports = { processLogin, isLoggedIn };