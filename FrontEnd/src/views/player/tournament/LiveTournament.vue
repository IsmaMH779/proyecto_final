<template>
  <div class="tournament-container">
    <div class="tournament-header">
      <div class="header-background"></div>
      <div class="tournament-header-content">
        <h1 class="tournament-title">{{ tournamentData?.name }}</h1>
        <div class="tournament-info">
          <div class="tournament-badge">
            <span v-if="tournamentData?.active">Activo</span>
            <span v-else>Cerrado</span>
          </div>
          <div class="tournament-badge">
            <span>{{ participantsCount }} Participantes</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Indicador de carga -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">Cargando bracket del torneo...</p>
    </div>

    <!-- Bracket -->
    <div v-else class="bracket-section">
      <div class="bracket-container">
        <bracket :rounds="rounds">
          <template #player="{ player, opponent }">
            <div 
              class="player-card" 
              :class="{
                'player-winner': player?.winner === true,
                'player-loser': opponent?.winner === true,
                'player-pending': player?.winner === null,
                'player-empty': !player?.id
              }"
              @click="!tournamentWinner && player?.id ? confirmPlayerAdvance(player?.id) : null"
              :style="tournamentWinner ? { cursor: 'default' } : {}"
            >
              <div class="player-info">
                <div class="player-avatar" v-if="player?.id">
                  <img 
                    :src="player?.img ? `http://localhost:8081/images/profile/${player.img}` : defaultProfileImage"
                    alt="Avatar"
                    onerror="this.onerror=null; this.src=defaultProfileImage"
                  />
                </div>
                <div class="player-name">
                  {{ player?.name || "Por determinar" }}
                </div>
              </div>
              <div class="player-status" v-if="player?.winner === true">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 6 9 17l-5-5"></path>
                </svg>
              </div>
            </div>
          </template>
        </bracket>
        
        <!-- Tarjeta del ganador final -->
        <div v-if="tournamentWinner" class="winner-card-container">
          <div class="winner-card">
            <div class="winner-header">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
                <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
                <path d="M4 22h16"></path>
                <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"></path>
                <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"></path>
                <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"></path>
              </svg>
              <h3>Ganador del Torneo</h3>
            </div>
            <div class="winner-content">
              <div class="winner-avatar">
                <img 
                    :src="player?.img ? `http://localhost:8081/images/profile/${player.img}` : defaultProfileImage"
                    alt="Avatar"
                    onerror="this.onerror=null; this.src=defaultProfileImage"
                  />
              </div>
              <div class="winner-name">{{ tournamentWinner.name }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
 
<script setup>
import Bracket from "vue-tournament-bracket";
import { ref, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import WebSocketService from "@/services/websocket.service";
import confetti from 'canvas-confetti';
import defaultProfileImage from '@/assets/profile_assets/default-profile-image.svg';

const route = useRoute();
const tournamentData = ref(null);
const rounds = ref([]);
const isLoading = ref(true);
const playerDataCache = ref({});
const isConnected = ref(false);
const tournamentWinner = ref(null);
const showConfirmModal = ref(false);
const selectedPlayerId = ref(null);
const confettiShown = ref(false);

// Número de participantes
const participantsCount = computed(() => {
  if (!tournamentData.value || !tournamentData.value.rounds || !tournamentData.value.rounds.length) {
    return 0;
  }
  
  // Contar jugadores únicos en la primera ronda
  const firstRound = tournamentData.value.rounds[0];
  const uniquePlayerIds = new Set();
  
  if (firstRound.games) {
    firstRound.games.forEach(match => {
      if (match.player1?.playerId) uniquePlayerIds.add(match.player1.playerId);
      if (match.player2?.playerId) uniquePlayerIds.add(match.player2.playerId);
    });
  }
  
  return uniquePlayerIds.size;
});

// Obtener datos del jugador
const fetchPlayerData = async (id) => {
  if (!id) return { username: "Por determinar" };

  // Check if player data is already cached
  if (playerDataCache.value[id]) {
    return playerDataCache.value[id];
  }
  
  try {
    const token = localStorage.getItem('token');
    const { data } = await axios.get(`http://localhost:8081/api/players/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    
    // Cache the player data
    playerDataCache.value[id] = data;
    return data;
  } catch (error) {
    console.error(`Error al obtener datos del jugador ${id}:`, error);
    return { username: `Player ${id}` };
  }
};

// Transformar los datos de forma asíncrona
const transformDataToRounds = async (data) => {
  if (!data || !data.rounds || !data.rounds.length) {
    return [];
  }
  
  try {
    // Crear un array de promesas para todas las rondas
    const roundsPromises = data.rounds.map(async (round) => {
      // Crear un array de promesas para todos los partidos de esta ronda
      const gamesPromises = round.games.map(async (match) => {
        // Obtener datos de player1
        let player1 = { id: null, name: "Por determinar", winner: null };
        if (match.player1) {
          const player1Data = await fetchPlayerData(match.player1.playerId);

          player1 = {
            id: match.player1.playerId,
            name: player1Data.username || `Player ${match.player1.playerId}`,
            winner: match.player1.winner,
            img: player1Data.imageUrl
          };
        }
        
        // Obtener datos de player2
        let player2 = { id: null, name: "Por determinar", winner: null };
        if (match.player2) {
          const player2Data = await fetchPlayerData(match.player2.playerId);

          player2 = {
            id: match.player2.playerId,
            name: player2Data.username || `Player ${match.player2.playerId}`,
            winner: match.player2.winner,
            img: player2Data.imageUrl
          };
        }
        
        return {
          player1,
          player2
        };
      });
      
      // Esperar a que se resuelvan todas las promesas de los partidos
      const games = await Promise.all(gamesPromises);
      
      return {
        games
      };
    });
    
    // Esperar a que se resuelvan todas las promesas de las rondas
    const transformedRounds = await Promise.all(roundsPromises);
    
    // Buscar al ganador del torneo (última ronda, último partido)
    if (transformedRounds.length > 0) {
      const lastRound = transformedRounds[transformedRounds.length - 1];
      console.log(lastRound)
      if (lastRound.games && lastRound.games.length > 0) {
        const finalMatch = lastRound.games[0];
        if (finalMatch.player1.winner) {
          tournamentWinner.value = finalMatch.player1;
        } else if (finalMatch.player2.winner) {
          tournamentWinner.value = finalMatch.player2;
        } else {
          tournamentWinner.value = null;
        }
      }
    }
    
    console.log('Transformación de datos completada:', transformedRounds);
    return transformedRounds;
    
  } catch (error) {
    console.error('Error en la transformación de datos:', error);
    return [];
  }
};

// Función para lanzar confeti
const launchConfetti = () => {
  const duration = 5 * 1000;
  const animationEnd = Date.now() + duration;
  const defaults = { startVelocity: 30, spread: 360, ticks: 60, zIndex: 0 };

  function randomInRange(min, max) {
    return Math.random() * (max - min) + min;
  }

  const interval = setInterval(function() {
    const timeLeft = animationEnd - Date.now();

    if (timeLeft <= 0) {
      return clearInterval(interval);
    }

    const particleCount = 50 * (timeLeft / duration);
    
    // Confeti desde la izquierda
    confetti({
      ...defaults,
      particleCount,
      origin: { x: 0.1, y: 0.5 },
      colors: ['#3d5a80', '#98c1d9', '#e0fbfc', '#ee6c4d', '#293241'],
      angle: randomInRange(55, 125)
    });
    
    // Confeti desde la derecha
    confetti({
      ...defaults,
      particleCount,
      origin: { x: 0.9, y: 0.5 },
      colors: ['#3d5a80', '#98c1d9', '#e0fbfc', '#ee6c4d', '#293241'],
      angle: randomInRange(235, 305)
    });
  }, 250);
};

// Observar cambios en el ganador del torneo
watch(tournamentWinner, (newValue) => {
  if (newValue && !confettiShown.value) {
    // Esperar un momento para que la UI se actualice antes de mostrar el confeti
    setTimeout(() => {
      launchConfetti();
      confettiShown.value = true;
    }, 500);
  }
});

const connectWebSocket = async () => {
  try {
    await WebSocketService.connect();
    isConnected.value = true;
    
    // Suscribirse a actualizaciones del torneo
    WebSocketService.subscribe(`/topic/tournament/${route.params.id}`, async (data) => {
      tournamentData.value = data;
      console.log('Actualización del torneo recibida:', data);
      
      // Transformar los datos recibidos (esperar a que termine)
      rounds.value = await transformDataToRounds(data);
      
      // Ocultar el loader una vez que todo está listo
      isLoading.value = false;
    });
    
    // Solicitar datos iniciales del torneo
    WebSocketService.send(`/app/tournament/${route.params.id}`, {});
  } catch (error) {
    console.error('Error al conectar:', error);
    isConnected.value = false;
  }
};

onMounted(() => {
  connectWebSocket();
});

onUnmounted(() => {
  // desconectarse del canal cuando se cierre la pagina
  WebSocketService.unsubscribe(`/topic/tournament/${route.params.id}`);
  WebSocketService.disconnect();
})
</script>

<style scoped>
/* Reset básico */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* Contenedor principal */
.tournament-container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
  padding: 1rem 0;
  max-width: 1200px;
  margin: 0 auto;
}

/* Header del torneo */
.tournament-header {
  position: relative;
  margin-bottom: 2rem;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
}

.header-background {
  height: 100px;
  background: linear-gradient(135deg, #1a2841 0%, #3d5a80 100%);
}

.tournament-header-content {
  position: relative;
  padding: 1.5rem 2rem;
  background-color: #e0e1dd;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
}

.tournament-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a2841;
  margin: 0;
}

.tournament-info {
  display: flex;
  gap: 1rem;
}

.tournament-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background-color: rgba(61, 90, 128, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 2rem;
  color: #3d5a80;
  font-weight: 500;
  font-size: 0.875rem;
}

/* Contenedor de carga */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #e0e1dd;
  border-radius: 1rem;
  padding: 3rem 1.5rem;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  margin-bottom: 2rem;
  min-height: 300px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(61, 90, 128, 0.1);
  border-radius: 50%;
  border-top-color: #3d5a80;
  animation: spin 1s linear infinite;
  margin-bottom: 1.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  color: #3d5a80;
  font-size: 1rem;
  font-weight: 500;
}

/* Sección del bracket */
.bracket-section {
  background-color: #e0e1dd;
  border-radius: 1rem;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  margin-bottom: 2rem;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  max-width: 100%; 
  position: relative;
}

/* Contenedor del bracket con espacio adicional */
.bracket-container {
  display: flex;
  align-items: center;
  min-width: min-content;
  padding: 0 1rem;
}

/* Ajustes específicos para móvil */
@media (max-width: 768px) {
  .bracket-section {
    padding: 1.5rem 0.5rem; 
    margin-left: -0.5rem;
    margin-right: -0.5rem;
    width: calc(100% + 1rem);
  }
  
  .bracket-container {
    padding: 0 1rem 1rem 1rem;
  }
  
  /* Ajustar el bracket para móvil */
  :deep(.vue-tournament-bracket) {
    padding-left: 0.5rem;
  }
  
  /* Asegurar que las tarjetas de jugador tengan suficiente espacio */
  .player-card {
    min-width: 140px;
    margin-left: 0.25rem;
  }
}

/* Ajustes para pantallas muy pequeñas */
@media (max-width: 480px) {
  .bracket-section {
    padding: 1rem 0.25rem;
  }
  
  .bracket-container {
    padding: 0 0.5rem 0.5rem 0.5rem;
  }
  
  /* Hacer que el bracket sea más compacto */
  :deep(.vue-tournament-bracket__round) {
    margin-right: 1.25rem;
  }
  
  /* Hacer las tarjetas más pequeñas pero legibles */
  .player-card {
    min-width: 130px;
    padding: 0.5rem 0.5rem;
  }
}

/* Asegurar que el bracket tenga suficiente espacio para renderizarse */
:deep(.vue-tournament-bracket) {
  min-width: min-content;
}

/* Mejorar la visualización de las líneas del bracket */
:deep(.vue-tournament-bracket__line) {
  min-width: 20px;
}

/* Estilos para el bracket */
:deep(.vue-tournament-bracket) {
  --bracket-line-color: #3d5a80;
  --bracket-line-width: 2px;
  padding: 2rem 0;
}

:deep(.vue-tournament-bracket__round) {
  margin-right: 2.5rem;
}

:deep(.vue-tournament-bracket__round:last-child) {
  margin-right: 0;
}

:deep(.vue-tournament-bracket__match) {
  margin: 1.5rem 0;
}

/* Sobrescribir los estilos de la librería que añaden bordes rojos/verdes */
:deep(.vtb-item-players .winner),
:deep(.vtb-item-players .defeated),
:deep(.vtb-item-players),
:deep(.vtb-item-players .defeatedAttachment) {
  background: transparent !important;
}

/* Tarjeta de jugador */
.player-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  min-width: 180px;
  box-shadow: 0 2px 6px rgba(26, 40, 65, 0.08);
  transition: all 0.3s ease;
  border-left: 3px solid #3d5a80;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.player-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: transparent;
  z-index: -1;
}

.player-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(26, 40, 65, 0.15);
}

.player-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.player-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  background-color: rgba(61, 90, 128, 0.1);
}

.player-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.player-name {
  font-weight: 500;
  color: #1a2841;
}

.player-status {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background-color: #3d5a80;
  border-radius: 50%;
  color: white;
}

/* Estilos para jugadores ganadores  */
.player-winner {
  border-left-color: #3d5a80;
  background: #fff;
  box-shadow: 0 0 15px rgba(61, 90, 128, 0.5);
  animation: winner-glow 3s infinite;
  transform: scale(1.05);
  z-index: 5;
}

@keyframes winner-glow {
  0% {
    box-shadow: 0 0 10px rgba(61, 90, 128, 0.5);
  }
  50% {
    box-shadow: 0 0 20px rgba(61, 90, 128, 0.8), 0 0 30px rgba(61, 90, 128, 0.4);
  }
  100% {
    box-shadow: 0 0 10px rgba(61, 90, 128, 0.5);
  }
}

.player-winner .player-status {
  background-color: #3d5a80;
  animation: status-pulse 2s infinite;
}

@keyframes status-pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

/* Estilos para jugadores perdedores - Fondo grisáceo y menos prominente */
:deep(.vtb-item-players .defeated) .player-card,
:deep(.vtb-item-players .defeatedAttachment) .player-card,
.player-loser {
  border-left-color: #ccc !important;
  background-color: #e9e9e9 !important;
  opacity: 0.8 !important;
  box-shadow: none !important;
  filter: grayscale(40%) !important;
  transition: all 0.3s ease !important;
  transform: scale(0.98) !important;
  z-index: 1 !important;
}

:deep(.vtb-item-players .defeated) .player-name,
:deep(.vtb-item-players .defeatedAttachment) .player-name,
.player-loser .player-name {
  color: #777 !important;
}

:deep(.vtb-item-players .defeated) .player-avatar,
:deep(.vtb-item-players .defeatedAttachment) .player-avatar,
.player-loser .player-avatar {
  opacity: 0.7 !important;
}

:deep(.vtb-item-players .defeated):hover .player-card,
:deep(.vtb-item-players .defeatedAttachment):hover .player-card,
.player-loser:hover {
  transform: scale(0.98) !important;
  box-shadow: none !important;
}

/* Estilos para jugadores ganadores a través de las clases de la biblioteca */
:deep(.vtb-item-players .winner) .player-card {
  transform: scale(1.05) !important;
  z-index: 5 !important;
}

/* Estilos para jugadores perdedores - Nuevo estilo apagado */
.player-loser {
  border-left-color: #ccc;
  background-color: #f5f5f5;
  opacity: 0.7;
  box-shadow: none;
  filter: grayscale(40%);
  transition: all 0.3s ease;
}

.player-loser .player-name {
  color: #777;
}

.player-loser .player-avatar {
  opacity: 0.7;
}

.player-loser:hover {
  transform: none;
  box-shadow: none;
}

/* Estilos para jugadores pendientes */
.player-pending {
  border-left-color: #415a77;
  background-color: #fff;
}

/* Estilos para posiciones vacías */
.player-empty {
  border-left-color: #e0e1dd;
  background-color: rgba(255, 255, 255, 0.7);
  opacity: 0.7;
  cursor: default;
}

/* Tarjeta del ganador */
.winner-card-container {
  margin-left: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.winner-card {
  background-color: #fff;
  border-radius: 0.75rem;
  box-shadow: 0 4px 15px rgba(61, 90, 128, 0.2);
  padding: 1.5rem;
  min-width: 220px;
  border-left: 5px solid #3d5a80;
  animation: winner-card-glow 3s infinite;
}

@keyframes winner-card-glow {
  0% {
    box-shadow: 0 4px 15px rgba(61, 90, 128, 0.2);
  }
  50% {
    box-shadow: 0 4px 25px rgba(61, 90, 128, 0.5), 0 0 40px rgba(61, 90, 128, 0.3);
  }
  100% {
    box-shadow: 0 4px 15px rgba(61, 90, 128, 0.2);
  }
}

.winner-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  color: #3d5a80;
}

.winner-header h3 {
  font-size: 1.125rem;
  font-weight: 600;
}

.winner-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.winner-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #3d5a80;
  box-shadow: 0 4px 10px rgba(61, 90, 128, 0.3);
}

.winner-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.winner-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a2841;
  text-align: center;
}

/* Modal de confirmación */
.confirm-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.confirm-modal {
  background-color: #fff;
  border-radius: 0.75rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  width: 90%;
  max-width: 400px;
  overflow: hidden;
}

.confirm-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background-color: #3d5a80;
  color: #fff;
}

.confirm-modal-header h3 {
  margin: 0;
  font-size: 1.25rem;
}

.close-button {
  background: none;
  border: none;
  color: #fff;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.confirm-modal-content {
  padding: 1.5rem;
}

.confirm-modal-content p {
  margin-bottom: 1.5rem;
  color: #1a2841;
}

.confirm-modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.cancel-button, .confirm-button {
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-button {
  background-color: #e0e1dd;
  color: #1a2841;
  border: none;
}

.cancel-button:hover {
  background-color: #d1d2ce;
}

.confirm-button {
  background-color: #3d5a80;
  color: #fff;
  border: none;
}

.confirm-button:hover {
  background-color: #2d4a70;
}

/* Responsive */
@media (max-width: 768px) {
  .tournament-header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .player-card {
    min-width: 160px;
  }
  
  :deep(.vue-tournament-bracket__round) {
    margin-right: 1.5rem; 
  }
  
  .bracket-container {
    flex-direction: column;
  }
  
  .winner-card-container {
    margin-left: 0;
    margin-top: 2rem;
    width: 100%;
  }
  
  .winner-card {
    width: 100%;
    max-width: 300px;
    margin: 0 auto;
  }
}

@media (max-width: 480px) {
  .tournament-header-content {
    padding: 1rem;
  }
  
  .tournament-title {
    font-size: 1.5rem;
  }
  
  .bracket-section {
    padding: 1rem;
  }
  
  .player-card {
    min-width: 140px;
    padding: 0.5rem 0.75rem;
  }
  
  .player-avatar {
    width: 24px;
    height: 24px;
  }
  
  :deep(.vue-tournament-bracket__round) {
    margin-right: 1rem; 
  }
}
</style>