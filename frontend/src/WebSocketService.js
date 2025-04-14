import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const WebSocketService = {
  client: null,

  connect(onConnect) {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws', // only works if WebSocket directly
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'), // for SockJS
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('WebSocket connected');
        if (onConnect) onConnect();
      },
      debug: (str) => console.log(str),
    });

    this.client.activate();
  },

  subscribe(destination, callback) {
    if (this.client && this.client.connected) {
      this.client.subscribe(destination, (message) => {
        const body = JSON.parse(message.body);
        callback(body);
      });
    }
  },

  send(destination, body) {
    if (this.client && this.client.connected) {
        this.client.publish({
            destination,
            headers: { 'content-type': 'application/json' },
            body: JSON.stringify(body),
          });
    }
  },

  disconnect() {
    if (this.client) {
      this.client.deactivate();
    }
  }
};

export default WebSocketService;
