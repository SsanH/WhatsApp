const express = require('express');
const app = express();
const cors = require('cors');
app.use(cors());
app.use(express.static('public'));

const http = require('http');
const server = http.createServer(app);
const { Server } = require("socket.io");
const io = new Server(server,{
    cors: {
        origin: "*",
    }
});

let clientSocketIds = {};
module.exports = {
    io: io,
    app: app,
    clientSocketIds: clientSocketIds,
};
 
io.on('connection', (socket) => {
    socket.on('add_user', (username) => {
        clientSocketIds[username] = socket.id;
    });

    socket.on('delete_user', (username) => {
        delete clientSocketIds[username];
    });
});

const bodyParser = require('body-parser');
app.use(bodyParser.json({limit: '5mb'}));
app.use(bodyParser.urlencoded({ extended: true }));

const mongoose = require('mongoose').default;
mongoose.connect('mongodb://localhost:27017/Whatsapp', {
     useNewUrlParser: true,
     useUnifiedTopology: true
});

app.use(express.static('public'));

const login = require('./routes/login');
app.use('/api/Tokens', login);

const register = require('./routes/user');
app.use('/api/Users', register);

const chat = require('./routes/chat');
app.use('/api/Chats', chat);

server.listen(5000);