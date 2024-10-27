import { WebSocketServer } from 'ws'; // Properly destructure WebSocketServer

function setupWebSocket(server) {
    const wss = new WebSocketServer({ server }); // Use the existing HTTP server

    wss.on('connection', (ws) => {
        console.log('Client connected');

        ws.on('message', (message) => {
            console.log(`Received message => ${message}`);
        });

        ws.on('close', () => {
            console.log('Client disconnected');
        });
    });

    function broadcast(data) {
        wss.clients.forEach((client) => {
            if (client.readyState === WebSocketServer.OPEN) {
                client.send(JSON.stringify(data));
            }
        });
        console.log(`Broadcasted message => ${data}`);
    }

    return { broadcast };
}

export { setupWebSocket };