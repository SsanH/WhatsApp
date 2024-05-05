const messageCounter = require('../models/messageCounter');

async function createMessageCounter() {
    const newMessageCounter = new messageCounter({
        counter: 0
    });
    return await newMessageCounter.save();
}

async function getMessageCounter() {
    return await messageCounter.findOne();
}

async function incrementMessageCounter() {
    const count = await getMessageCounter();
    if (count === null) {
        await createMessageCounter();
        return 0;
    }
    count.counter++;
    await count.save();
    return count.counter;
}

module.exports = { getMessageCounter, incrementMessageCounter, createMessageCounter };