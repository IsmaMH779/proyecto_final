<template>
  <div class="tournament-container">
    <div class="tournament-header">
      <div class="header-background"></div>
      <div class="tournament-header-content">
        <h1 class="tournament-title">Bracket del Torneo</h1>
        <div class="tournament-info">
          <div class="tournament-badge">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 2h12a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2Z"></path>
              <path d="M8 2v20"></path>
              <path d="M16 2v20"></path>
              <path d="M2 12h4"></path>
              <path d="M18 12h4"></path>
              <path d="M10 12h4"></path>
            </svg>
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
      <bracket :rounds="rounds">
        <template #player="{ player }">
          <div 
            class="player-card" 
            :class="{
              'player-winner': player?.winner === true,
              'player-pending': player?.winner === null,
              'player-empty': !player?.id
            }"
            @click="handlePlayerClick(player.id)"
          >
            <div class="player-info">
              <div class="player-avatar" v-if="player?.id">
                <img :src="`http://localhost:8081/images/profile/${player?.img}`" alt="Avatar" />
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
    </div>
  </div>
</template>
 
<script setup>
import Bracket from "vue-tournament-bracket";
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import WebSocketService from "@/services/websocket.service";

const route = useRoute();
const tournamentData = ref(null);
const rounds = ref([]);
const isLoading = ref(true);
const playerDataCache = ref({});
const isConnected = ref(false);

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
        // Obtener datos de player1 (si existe)
        let player1 = { id: null, name: "Por determinar", winner: null };
        if (match.player1) {
          const player1Data = await fetchPlayerData(match.player1.playerId);
          player1 = {
            id: match.player1.playerId,
            name: player1Data.username || `Player ${match.player1.playerId}`,
            winner: match.winner === match.player1.playerId,
            img: player1Data.imageUrl
          };
        }
        
        // Obtener datos de player2 (si existe)
        let player2 = { id: null, name: "Por determinar", winner: null };
        if (match.player2) {
          const player2Data = await fetchPlayerData(match.player2.playerId);
          player2 = {
            id: match.player2.playerId,
            name: player2Data.username || `Player ${match.player2.playerId}`,
            winner: match.winner === match.player2.playerId,
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
    
    console.log('Transformación de datos completada:', transformedRounds);
    return transformedRounds;
    
  } catch (error) {
    console.error('Error en la transformación de datos:', error);
    return [];
  }
};

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

const handlePlayerClick = async (playerId) => {
  const token = localStorage.getItem('token');

  await axios.put(
    `http://localhost:8083/api/matchmaking/advance/${route.params.id}/${playerId}`,
    null,
    {
      headers: { Authorization: `Bearer ${token}` }
    }
  );
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
  background-color: #f9f5f0;
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
}

/* Estilos para el bracket */
:deep(.vue-tournament-bracket) {
  --bracket-line-color: #3d5a80;
  --bracket-line-width: 2px;
  padding: 2rem 0;
}

:deep(.vue-tournament-bracket__round) {
  margin-right: 2.5rem; /* Reducido de 4rem a 2.5rem */
}

:deep(.vue-tournament-bracket__round:last-child) {
  margin-right: 0;
}

:deep(.vue-tournament-bracket__match) {
  margin: 1.5rem 0;
}

/* Tarjeta de jugador */
.player-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  min-width: 180px; /* Reducido de 200px a 180px */
  box-shadow: 0 2px 6px rgba(26, 40, 65, 0.08);
  transition: all 0.3s ease;
  border-left: 3px solid #3d5a80;
  position: relative;
  overflow: hidden;
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

/* Estilos para jugadores ganadores */
.player-winner {
  border-left-color: #3d5a80;
  background: linear-gradient(to right, rgba(61, 90, 128, 0.05), transparent);
  box-shadow: 0 0 15px rgba(61, 90, 128, 0.5);
  animation: winner-pulse 2s infinite;
}

@keyframes winner-pulse {
  0% {
    box-shadow: 0 0 10px rgba(61, 90, 128, 0.5);
  }
  50% {
    box-shadow: 0 0 20px rgba(61, 90, 128, 0.8);
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
    margin-right: 1.5rem; /* Aún más reducido en móviles */
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
    margin-right: 1rem; /* Mínimo margen en móviles pequeños */
  }
}
</style>