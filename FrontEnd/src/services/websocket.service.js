import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs.min.js';


class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.subscriptions = {};
  }

  // metodo para conectarse al websocket
  connect(serverUrl = 'http://localhost:8083/ws') {
    return new Promise((resolve, reject) => {
      try {
        this.client = new Client({
          webSocketFactory: () => new SockJS(serverUrl),
          connectHeaders: {},
          debug: function (str) {
            // console.log(str);
          },
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
          onConnect: frame => {
            this.connected = true;
            console.log('Conectado a WebSocket:', frame);
            resolve(frame);
          },
          onStompError: error => {
            console.error('Error STOMP:', error);
            reject(error);
          },
          onWebSocketError: event => {
            console.error('Error WebSocket:', event);
            this.connected = false;
            reject(event);
          }
        });

        this.client.activate();
      } catch (error) {
        console.error('Error al inicializar WebSocket:', error);
        reject(error);
      }
    });
  }

  // metodo para desconcetarse al websocket
  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.connected = false;
      this.subscriptions = {};
    }
  }

  // metodo para suscribirse al canal especificado
  subscribe(destination, callback) {
    if (!this.client || !this.client.connected) {
      console.error('No conectado a WebSocket');
      return null;
    }

    if (!this.subscriptions[destination]) {
      this.subscriptions[destination] = this.client.subscribe(destination, message => {
        try {
          const payload = JSON.parse(message.body);
          callback(payload);
        } catch (error) {
          console.error('Error al procesar mensaje:', error);
          callback(message.body);
        }
      });
    }

    return this.subscriptions[destination];
  }
// metodo para salir de la suscripcion al canal especificado
  unsubscribe(destination) {
    if (this.subscriptions[destination]) {
      this.subscriptions[destination].unsubscribe();
      delete this.subscriptions[destination];
    }
  }

  // metodo para solicitar el torneo actualizado
  send(destination, message) {
    if (!this.client || !this.client.connected) {
      console.error('No conectado a WebSocket');
      return;
    }

    this.client.publish({
      destination: destination,
      body: JSON.stringify(message)
    });
  }

  isConnected() {
    return this.connected && this.client && this.client.connected;
  }
}

export default new WebSocketService();