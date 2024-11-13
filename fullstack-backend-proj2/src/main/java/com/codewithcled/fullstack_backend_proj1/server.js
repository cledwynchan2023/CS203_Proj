
import express from 'express';
import { createServer } from 'http';
import { setupWebSocket } from './websocket-server.js'; // Update this import

const app = express();
const server = createServer(app);

// Your existing middleware and routes

// Pass the server instance to the WebSocket setup
setupWebSocket(server);

server.listen(8082, () => {
    console.log('HTTP server listening on port 8082');
});