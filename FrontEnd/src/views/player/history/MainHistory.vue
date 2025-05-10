<template>
  <ion-page>
    <div class="tournament-container">
      <!-- Indicador de carga -->
      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p class="loading-text">Cargando historial de torneos...</p>
      </div>

      <!-- Lista de torneos cerrados -->
      <div v-else class="tournaments-list">
        <div
          v-for="(tournament, index) in tournaments"
          :key="index"
          class="tournament-card"
        >
          <div class="tournament-card-header">
            <div class="tournament-name">{{ tournament.name }}</div>
            <div class="tournament-date">{{ formatDate(tournament.startDate) }}</div>
          </div>

          <div class="tournament-card-content">
            <div class="tournament-details">
              <div class="detail-item">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                  <circle cx="9" cy="7" r="4"></circle>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                  <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                </svg>
                <span>{{ tournament.playerNumbers }} Participantes</span>
              </div>
              <div class="detail-item">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"></path>
                  <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"></path>
                  <path d="M4 22h16"></path>
                  <path
                    d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"
                  ></path>
                  <path
                    d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"
                  ></path>
                  <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"></path>
                </svg>
                <span>{{ formatGameType(tournament.format) }}</span>
              </div>
              <div class="detail-item">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="18"
                  height="18"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <rect x="2" y="6" width="20" height="12" rx="2"></rect>
                  <path d="M12 12h.01"></path>
                  <path d="M17 12h.01"></path>
                  <path d="M7 12h.01"></path>
                </svg>
                <span>{{ tournament.game }}</span>
              </div>
            </div>

            <div v-if="tournament.winner" class="winner-section">
              <div class="winner-label">Ganador</div>
              <div class="winner-info">
                <div class="winner-avatar">
                  <img
                    :src="tournament.winner.imageUrl ? `http://localhost:8081/images/profile/${tournament.winner.imageUrl}` : defaultProfileImage"
                    alt="Avatar"
                    onerror="this.onerror=null; this.src='/default-avatar.png'"
                  />
                </div>
                <div class="winner-name">{{ tournament.winner.username }}</div>
              </div>
            </div>
            <div v-else class="winner-section no-winner">
              <div class="winner-label">Sin ganador registrado</div>
            </div>

            <div class="tournament-actions">
              <button class="view-details-button" @click="viewTournamentDetails(tournament.id)">Ver Detalles</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Mensaje cuando no hay torneos -->
      <div v-if="!isLoading && tournaments.length === 0" class="no-tournaments">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="48"
          height="48"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
        <p>No hay torneos completados en el historial</p>
      </div>
    </div>
  </ion-page>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { IonPage } from "@ionic/vue";
import axios from "axios";
import { useRouter } from "vue-router";
import defaultProfileImage from '@/assets/profile_assets/default-profile-image.svg';

const router = useRouter();
const tournaments = ref([]);
const isLoading = ref(true);
const playerCache = ref({});

// Formatear la fecha
const formatDate = (dateString) => {
  if (!dateString) return "Fecha no disponible";
  
  const date = new Date(dateString);
  const options = { year: 'numeric', month: 'short', day: 'numeric' };
  return date.toLocaleDateString('es-ES', options);
};

// Formatear el tipo de juego
const formatGameType = (format) => {
  if (!format) return "Formato desconocido";
  
  const formats = {
    "Direct_elimination": "Eliminación directa",
    "Groups": "Fase de grupos",
    "League": "Liga",
    "Swiss": "Sistema suizo"
  };
  
  return formats[format] || format;
};

// Ver detalles del torneo
const viewTournamentDetails = (tournamentId) => {
  if (!tournamentId) return;
  router.push(`/web/player-liveTournament/${tournamentId}`);
};

// Obtener datos del jugador
const fetchPlayerData = async (id) => {
  if (!id) return null;
  
  // Verificar si ya tenemos los datos en caché
  if (playerCache.value[id]) {
    return playerCache.value[id];
  }
  
  try {
    const token = localStorage.getItem('token');
    const { data } = await axios.get(`http://localhost:8081/api/players/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    
    // Guardar en caché
    playerCache.value[id] = data;
    return data;
  } catch (error) {
    console.error(`Error al obtener datos del jugador ${id}:`, error);
    return { 
      username: `Jugador ${id}`,
      imageUrl: null
    };
  }
};

// Función para obtener los torneos cerrados
const fetchClosedTournaments = async () => {
  try {
    isLoading.value = true;
    const token = localStorage.getItem('token');
    const { data } = await axios.get(
      "http://localhost:8082/api/tournaments/historial/player",
      {
        headers: { Authorization: `Bearer ${token}` }
      }
    );
    
    // Procesar los datos de los torneos
    const processedTournaments = await Promise.all(
      data.map(async (tournament) => {
        // Si hay un ganador, obtener sus datos
        if (tournament.winnerId) {
          const winnerData = await fetchPlayerData(tournament.winnerId);
          tournament.winner = winnerData;
        }
        

        return tournament;
      })
    );
    
    tournaments.value = processedTournaments;
    console.log("Torneos procesados:", tournaments.value);
  } catch (error) {
    console.error('Error al obtener torneos cerrados:', error);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchClosedTournaments();
});
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
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
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

/* Lista de torneos */
.tournaments-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

/* Tarjeta de torneo */
.tournament-card {
  background-color: #e0e1dd;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.tournament-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(26, 40, 65, 0.15);
}

.tournament-card-header {
  background: linear-gradient(135deg, #1a2841 0%, #3d5a80 100%);
  padding: 1.25rem;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tournament-name {
  font-size: 1.25rem;
  font-weight: 600;
}

.tournament-date {
  font-size: 0.875rem;
  opacity: 0.9;
}

.tournament-card-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.tournament-details {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #3d5a80;
  font-size: 0.875rem;
}

.winner-section {
  background-color: rgba(61, 90, 128, 0.05);
  border-radius: 0.75rem;
  padding: 1rem;
  margin-bottom: 1.5rem;
  flex-grow: 1;
}

.no-winner {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #777;
  font-style: italic;
}

.winner-label {
  font-size: 0.875rem;
  color: #3d5a80;
  margin-bottom: 0.75rem;
  font-weight: 500;
}

.winner-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.winner-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #3d5a80;
  background-color: #f0f0f0;
}

.winner-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.winner-name {
  font-weight: 600;
  color: #1a2841;
}

.tournament-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: auto;
}

.view-details-button {
  background-color: #3d5a80;
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0.75rem 1.25rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.view-details-button:hover {
  background-color: #2d4a70;
}

/* Mensaje de no hay torneos */
.no-tournaments {
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
  color: #3d5a80;
  text-align: center;
  gap: 1.5rem;
}

.no-tournaments svg {
  opacity: 0.7;
}

.no-tournaments p {
  font-size: 1.125rem;
  font-weight: 500;
}

/* Responsive */
@media (max-width: 768px) {
  .tournaments-list {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }

  .tournament-header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .tournaments-list {
    grid-template-columns: 1fr;
  }

  .tournament-card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .tournament-details {
    flex-direction: column;
    gap: 0.75rem;
  }

  .tournament-header-content {
    padding: 1rem;
  }

  .tournament-title {
    font-size: 1.5rem;
  }
  
  .tournament-container {
    padding: 0.5rem;
  }
}
</style>