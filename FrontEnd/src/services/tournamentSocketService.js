import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

let stompClient = null;

export function connectToTournament(tournamentId, onUpdate) {
  // Crear la conexión SockJS
  const socket = new SockJS('http://localhost:8083/ws-tournaments');
  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onConnect: () => {
      // Nos suscribimos al canal específico del torneo
      stompClient.subscribe(
        `/topic/tournament/${tournamentId}`,
        message => {
          if (message.body) {
            const tournament = JSON.parse(message.body);
            onUpdate(tournament);
          }
        }
      );
    },
    onStompError: frame => {
      console.error('STOMP error', frame);
    }
  });
  stompClient.activate();
}

export function disconnect() {
  if (stompClient) {
    stompClient.deactivate();
  }
}
