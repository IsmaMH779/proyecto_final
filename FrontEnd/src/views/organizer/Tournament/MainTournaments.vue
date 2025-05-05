<template>
  <ion-page>
    <ion-content :scroll-y="true">
      <div class="tournaments-app">
        <div class="content-container">

          <!-- Torneos Activos (active = true) -->
          <div class="section-header">
              <h1 class="section-title">Torneos Activos</h1>
            </div>

          <div v-if="activeTournaments.length === 0" class="empty-state">
            <ion-icon :icon="trophyOutline" class="empty-icon" />
            <p>No tienes torneos activos actualmente</p>
          </div>

          <div v-else class="tournaments-grid">
            <div
              v-for="tournament in activeTournaments"
              :key="tournament.id"
              class="tournament-card tournament-card-active"
            >
              <div class="tournament-header">
                <div class="tournament-game-badge">[{{ tournament.game }}]</div>
                <h2 class="tournament-name">{{ tournament.name }}</h2>
              </div>

              <div class="tournament-details" @click="navigateToLiveTournament(tournament.id)">
                <!-- Fecha -->
                <div class="detail-item">
                  <ion-icon :icon="calendarOutline" />
                  <span>{{ formatDate(tournament.startDate) }}</span>
                </div>
                <!-- Hora -->
                <div class="detail-item">
                  <ion-icon :icon="timeOutline" />
                  <span>{{ formatTime(tournament.startDate) }}</span>
                </div>
                <!-- Formato -->
                <div class="detail-item">
                  <ion-icon :icon="peopleOutline" />
                  <span>{{ formatType(tournament.format) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Separador -->
          <div class="section-divider">
            <span>Tus Torneos Creados</span>
          </div>

          <!-- Torneos Creados (active = false) -->
          <div v-if="loading" class="loader-container">
            <div class="loader" />
          </div>
          <div v-else>
            <div v-if="createdTournaments.length === 0" class="empty-state">
              <ion-icon :icon="alertCircleOutline" class="empty-icon" />
              <p>No has creado aún ningún torneo</p>
            </div>
            <div v-else class="tournaments-grid">
              <div
                v-for="tournament in createdTournaments"
                :key="tournament.id"
                class="tournament-card"
                :class="{ 'tournament-card-startable': hasReachedStartDate(tournament.startDate) }"
              >
                <div class="tournament-header">
                  <div class="tournament-game-badge">[{{ tournament.game }}]</div>
                  <h2 class="tournament-name">{{ tournament.name }}</h2>
                  <ion-button
                    v-if="hasReachedStartDate(tournament.startDate)"
                    fill="clear"
                    size="small"
                    class="start-btn"
                    @click.stop="askStart(tournament)"
                  >
                    <ion-icon slot="icon-only" :icon="playOutline" />
                  </ion-button>
                </div>
                <div class="tournament-details" @click="navigateToTournament(tournament.id)">
                  <div class="detail-item">
                    <ion-icon :icon="calendarOutline" />
                    <span>{{ formatDate(tournament.startDate) }}</span>
                  </div>
                  <div class="detail-item">
                    <ion-icon :icon="timeOutline" />
                    <span>{{ formatTime(tournament.startDate) }}</span>
                  </div>
                  <div class="detail-item">
                    <ion-icon :icon="peopleOutline" />
                    <span>{{ formatType(tournament.format) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>

      <!-- Modal Confirmar Inicio -->
      <ion-modal 
        :is-open="showStartConfirm" 
        @didDismiss="showStartConfirm = false"
        class="custom-modal"
      >
        <div class="modal-wrapper">
          <div class="modal-header">
            <ion-icon :icon="trophyOutline" class="modal-icon" />
            <h2>Confirmar Inicio de Torneo</h2>
          </div>
          
          <div class="modal-content">
            <p>¿Estás seguro de que deseas iniciar el torneo <strong>{{ toStart?.name }}</strong>?</p>
            <div class="modal-tournament-info" v-if="toStart">
              <div class="modal-info-item">
                <ion-icon :icon="calendarOutline" />
                <span>{{ formatDate(toStart.startDate) }}</span>
              </div>
              <div class="modal-info-item">
                <ion-icon :icon="timeOutline" />
                <span>{{ formatTime(toStart.startDate) }}</span>
              </div>
              <div class="modal-info-item">
                <ion-icon :icon="peopleOutline" />
                <span>{{ formatType(toStart.format) }}</span>
              </div>
            </div>
          </div>
          
          <div class="modal-actions">
            <ion-button 
              expand="block" 
              class="confirm-btn"
              @click="startTournament()"
            >
              <ion-icon slot="start" :icon="playOutline" />
              Sí, iniciar torneo
            </ion-button>
            
            <ion-button 
              expand="block" 
              fill="outline" 
              class="cancel-btn"
              @click="showStartConfirm = false"
            >
              Cancelar
            </ion-button>
          </div>
        </div>
      </ion-modal>

    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import {
  IonPage,
  IonContent,
  IonIcon,
  IonButton,
  IonModal
} from '@ionic/vue'
import {
  trophyOutline,
  alertCircleOutline,
  calendarOutline,
  peopleOutline,
  playOutline,
  timeOutline,
  push,
  sync
} from 'ionicons/icons'

const router = useRouter()
const tournaments = ref([])
const loading = ref(true)

// Estado modales
const showStartConfirm = ref(false)
const toStart = ref(null)

// Verificar periódicamente si hay nuevos torneos que cumplan la condición
let intervalId = null
const checkTournaments = () => {
  fetchTournaments()
}

const fetchTournaments = async () => {
  try {
    const token = localStorage.getItem('token')
    const { data } = await axios.get('http://localhost:8082/api/tournaments/organizer', {
      headers: { Authorization: `Bearer ${token}` }
    })
    tournaments.value = data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

// Filtrar y ordenar según los criterios especificados
// Ordenar por fecha de inicio (más próxima primero)
const activeTournaments = computed(() =>
  tournaments.value
    .filter(t => t.active === true && !t.closed)
    .sort((a, b) => new Date(a.startDate) - new Date(b.startDate)) // Fecha más próxima primero
)

const createdTournaments = computed(() =>
  tournaments.value
    .filter(t => t.active === false && !t.closed)
    .sort((a, b) => new Date(a.startDate) - new Date(b.startDate)) // Fecha más próxima primero
)

// Formateo
const formatDate = d =>
  new Date(d).toLocaleDateString('es-ES', { year:'numeric', month:'long', day:'numeric' })
const formatTime = d =>
  new Date(d).toLocaleTimeString('es-ES', { hour:'2-digit', minute:'2-digit' })
const formatType = f => ({
  Direct_elimination: 'Eliminación directa',
  Round_robin: 'Liga',
  Swiss_system: 'Sistema suizo'
}[f] || f)

// Función para determinar si ya llegó o pasó la fecha de inicio
const hasReachedStartDate = date => {
  const tournamentDate = new Date(date)
  const currentDate = new Date()
  return currentDate >= tournamentDate // La fecha actual es igual o posterior a la fecha del torneo
}

const askStart = t => { 
  toStart.value = t
  showStartConfirm.value = true 
}

const startTournament = async () => {
  showStartConfirm.value = false
  // Aquí iría la lógica para iniciar el torneo en el backend
  console.log('Iniciando torneo:', toStart.value.id)
  
  // cambiar el estado del torneo
  activateTournament()

  //router.push(`/web/organizer-liveTournament/${toStart.value.id}`)

  // Refrescar los datos después de iniciar el torneo
  loading.value = true
  await fetchTournaments()
}

const activateTournament = async () => {
  const token = localStorage.getItem('token');

  try {
    await axios.put(`http://localhost:8082/api/tournaments/${toStart.value.id}/activate`, null, {
      headers: { Authorization: `Bearer ${token}` }
    });
  } catch (e) {
    console.error(e);
  }

  try {
    const tournament = await fetchTournament();
    console.log(tournament)
    await axios.post(`http://localhost:8083/api/matchmaking/start`, tournament, {
      headers: { Authorization: `Bearer ${token}` }
    });
  } catch (e) {
    console.error(e);
  }
};

const fetchTournament = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await axios.get(`http://localhost:8082/api/tournaments/${toStart.value.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    return response.data;
  } catch (e) {
    console.error(e);
  }
};


const navigateToTournament = id => router.push(`/web/organizer-tournament-profile/${id}`)
const navigateToLiveTournament = id => router.push(`/web/organizer-liveTournament/${id}`)


onMounted(() => {
  fetchTournaments()
  // Verificar cada minuto si hay cambios en las fechas
  intervalId = setInterval(checkTournaments, 60000)
})

// Limpiar el intervalo cuando el componente se desmonta
onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId)
  }
})
</script>

<style scoped>
.tournaments-app {
  background: #F5EFE7;
  min-height: 100%;
}
.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
}
.section-divider {
  text-align: center;
  margin: 3rem 0;
  position: relative;
}
.section-divider span {
  background: #F5EFE7;
  padding: 0 1rem;
  position: relative;
  z-index: 1;
  color: #415a77;
  font-weight: 500;
}
.section-divider::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 0; right: 0;
  height: 2px;
  background: #e0e1dd;
  z-index: 0;
}
.empty-state {
  text-align: center;
  padding: 2rem;
  background: #e0e1dd;
  border-radius: 1rem;
  margin: 2rem 0;
}
.empty-state p {
  color: #3d5a80;
  font-weight: 600;
}
.empty-icon {
  font-size: 3rem;
  color: #3d5a80;
  margin-bottom: 1rem;
}
.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 2rem 0 1.5rem;
  }
  .section-title {
    font-size: 1.75rem;
    color: #1a2841;
    margin: 0;
  }
.tournaments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill,minmax(300px,1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}
.tournament-card {
  background-color: #e0e1dd;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(26,40,65,0.1);
  transition: transform .2s, box-shadow .2s;
  cursor: pointer;
  border: 2px solid transparent;
}
.tournament-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(26,40,65,0.15);
  border-color: #415a77;
}
/* Estilo para torneos activos (arriba) */
.tournament-card-active {
  border-color: #415a77;
  box-shadow: 0 6px 16px rgba(65, 90, 119, 0.2);
}
/* Estilo mejorado para torneos que se pueden iniciar (abajo) */
.tournament-card-startable {
  border-color: #415a77;
  animation: shadow-pulse 2s infinite;
}
@keyframes shadow-pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(65, 90, 119, 0.7);
  }
  70% {
    box-shadow: 0 0 15px 10px rgba(65, 90, 119, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(65, 90, 119, 0);
  }
}
.tournament-header {
  padding: 1.25rem;
  border-bottom: 2px solid #415a77;
  position: relative;
}
.tournament-game-badge {
  display: inline-block;
  font-weight: 600;
  color: #3d5a80;
  margin-bottom: .25rem;
}
.tournament-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a2841;
  margin: 0;
  line-height: 1.4;
}
/* Botón de inicio con estilo acorde a la página */
.start-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  color: #F5EFE7;
  background-color: #415a77;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  --background: #415a77;
  --background-hover: #3d5a80;
  --color: #F5EFE7;
  --border-radius: 50%;
  --box-shadow: 0 4px 8px rgba(65, 90, 119, 0.3);
  transition: all 0.3s ease;
}
.start-btn:hover {
  --box-shadow: 0 6px 12px rgba(65, 90, 119, 0.5);
  transform: scale(1.1);
}
.tournament-details {
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: .75rem;
}
.detail-item {
  display: flex;
  align-items: center;
  gap: .75rem;
  color: #415a77;
}
.detail-item ion-icon {
  font-size: 1.1rem;
  color: #3d5a80;
}

/* Estilos para el modal personalizado */
.custom-modal::part(content) {
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(26, 40, 65, 0.2);
  overflow: hidden;
  max-width: 400px;
  width: 90%;
  height: auto;
  margin: auto;
}

.modal-wrapper {
  background-color: #F5EFE7;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.modal-header {
  background-color: #415a77;
  color: #F5EFE7;
  padding: 1.5rem;
  text-align: center;
  position: relative;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.modal-icon {
  font-size: 2.5rem;
  margin-bottom: 0.75rem;
  color: #F5EFE7;
}

.modal-content {
  padding: 1.5rem;
  flex-grow: 1;
}

.modal-content p {
  margin-top: 0;
  color: #1a2841;
  font-size: 1.1rem;
  text-align: center;
}

.modal-tournament-info {
  background-color: #e0e1dd;
  border-radius: 12px;
  padding: 1rem;
  margin-top: 1rem;
}

.modal-info-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #415a77;
  margin-bottom: 0.5rem;
}

.modal-info-item:last-child {
  margin-bottom: 0;
}

.modal-info-item ion-icon {
  font-size: 1.1rem;
  color: #3d5a80;
}

.modal-actions {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.confirm-btn {
  --background: #415a77;
  --background-hover: #3d5a80;
  --color: #F5EFE7;
  --border-radius: 8px;
  --box-shadow: 0 4px 8px rgba(65, 90, 119, 0.3);
  font-weight: 600;
}

.cancel-btn {
  --color: #415a77;
  --border-color: #415a77;
  --border-radius: 8px;
  font-weight: 500;
}

.loader-container { text-align:center; padding:3rem; }
.loader { border:4px solid #f3f3f3; border-top:4px solid #3d5a80;
  border-radius:50%; width:40px; height:40px; animation:spin 1s linear infinite;
  margin:0 auto;
}
@keyframes spin { to { transform:rotate(360deg); } }
@media (max-width:768px) {
  .tournaments-grid { grid-template-columns:1fr; }
}
</style>