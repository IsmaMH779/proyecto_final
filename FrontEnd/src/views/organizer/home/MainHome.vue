<template>
  <ion-page class="full-height">
    <ion-content :scroll-y="true" class="full-height">
      <div class="organizer-home">
        
        <!-- Torneos de esta semana -->
        <div class="section">
          <div class="section-header">
            <h2>Torneos de esta semana</h2>
            <div class="navigation-arrows" v-if="tournamentChunks.length > 1">
              <ion-icon :icon="chevronBack" @click="scrollWeekTournaments('left')" />
              <ion-icon :icon="chevronForward" @click="scrollWeekTournaments('right')" />
            </div>
          </div>
          
          <div class="weekly-tournaments-container" ref="weeklyTournamentsContainer">
            <div
              v-for="(chunk, idx) in tournamentChunks"
              :key="idx"
              class="tournament-chunk"
            >
              <div
                v-for="tournament in chunk"
                :key="tournament.id"
                class="tournament-card-week"
                :class="{ 'today-tournament': isToday(tournament.startDate) }"
              >
                <div class="tournament-header">
                  <div class="tournament-game-badge">{{ tournament.game }}</div>
                  <h2 class="tournament-name">{{ tournament.name }}</h2>
                </div>
                
                <div class="tournament-details">
                  <div class="detail-item">
                    <ion-icon :icon="calendarOutline" />
                    <span>{{ new Date(tournament.startDate).toLocaleDateString() }}</span>
                  </div>
                  <div class="detail-item">
                    <ion-icon :icon="timeOutline" />
                    <span>{{ formatTime(tournament.startDate) }}</span>
                  </div>
                  <div class="detail-item">
                    <ion-icon :icon="locationOutline" />
                    <!-- Si no llega tournament.location, muestro texto por defecto -->
                    <span :class="{ 'missing-location': !tournament.location }">
                      {{ tournament.location || 'Ubicación no disponible' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Estadísticas Rápidas -->
        <div class="quick-stats">
          <div class="stat-card">
            <div class="stat-value">{{ monthlyCreatedTournaments }}</div>
            <div class="stat-label">Torneos organizados este mes</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ totalPlayers }}</div>
            <div class="stat-label">Inscripciones totales este mes</div>
          </div>
        </div>

        <!-- Eventos de la semana (hardcodeado) -->
        <div class="section">
          <div class="section-header">
            <h2>Eventos de esta semana</h2>
          </div>
          
          <div class="calendar-cards">
            <div 
              v-for="event in weeklyEvents"
              :key="event.id"
              class="calendar-event-card"
              :class="{ 'today-event': isToday(event.startDate) }"
            >
              <div class="event-date">
                <div class="day">{{ formatDay(event.startDate) }}</div>
                <div class="month">{{ formatMonth(event.startDate) }}</div>
              </div>
              <div class="event-details">
                <h3 class="event-name">{{ event.name }}</h3>
                <p class="event-time">{{ formatTime(event.startDate) }}</p>
              </div>
            </div>
          </div>
        </div>

      </div>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { IonPage, IonContent, IonIcon } from '@ionic/vue'
import { chevronBack, chevronForward } from 'ionicons/icons'
import { calendarOutline, timeOutline, locationOutline } from 'ionicons/icons'

const API_URL = 'http://localhost:8082'
function getToken() { return localStorage.getItem('jwt') || '' }

const weeklyTournaments = ref([])
const monthlyCreatedTournaments = ref(0)
const totalPlayers = ref(0)
const weeklyTournamentsContainer = ref(null)

// Agrupa de 3 en 3
const tournamentChunks = computed(() => {
  const chunks = []
  for (let i = 0; i < weeklyTournaments.value.length; i += 3) {
    chunks.push( weeklyTournaments.value.slice(i, i + 3) )
  }
  return chunks
})

const scrollWeekTournaments = (direction) => {
  const c = weeklyTournamentsContainer.value
  if (!c) return
  const w = c.clientWidth
  c.scrollBy({ left: direction==='right'? w : -w, behavior: 'smooth' })
}

const isToday = dateString =>
  new Date(dateString).toDateString() === new Date().toDateString()

const formatDay   = ds => new Date(ds).getDate()
const formatMonth = ds => new Date(ds).toLocaleString('es-ES',{month:'short'})
const formatTime  = ds => new Date(ds).toLocaleTimeString('es-ES',{hour:'2-digit',minute:'2-digit'})

onMounted(async () => {
  await fetchWeeklyTournaments()
  await fetchStats()
})

async function fetchWeeklyTournaments() {
  try {
    const res = await fetch(`${API_URL}/api/tournaments/weekly`, {
      headers:{'Authorization':`Bearer ${getToken()}`}, credentials:'include'
    })
    if (!res.ok) throw new Error(await res.text())
    weeklyTournaments.value = await res.json()
  } catch(e){ console.error('WG:',e) }
}

async function fetchStats() {
  try {
    const res = await fetch(`${API_URL}/api/tournaments/stats`, {
      headers: { 'Authorization': `Bearer ${getToken()}` },
      credentials: 'include'
    });
    if (!res.ok) throw new Error(await res.text());
    const d = await res.json();
    monthlyCreatedTournaments.value = d.tournamentsThisMonth;
    totalPlayers.value = d.playersThisMonth;
  } catch(e) { console.error('ST:', e) }
}


const weeklyEvents = ref([
  { id:1, name:'Reunión de organizadores', startDate:new Date().toISOString() },
  { id:2, name:'Taller de reglamentos',    startDate:new Date().toISOString() }
])
</script>

<style scoped>

/* 1) Aseguramos que el scroll y el fondo ocupen todo el alto */
html, body, ion-app, ion-page.full-height, ion-content.full-height {
  height: 100%;
  margin: 0;
}
ion-content.full-height {
  --background: #F5EFE7;
}

.organizer-home {
  min-height: 100%;
  padding: 1rem;
}

/* resto de estilos (igual que antes), salvo: */

/* etiqueta de ubicación cuando falta */
.missing-location {
  color: #e76f51;
  font-style: italic;
}

.organizer-home {
  padding: 1rem;  
  background: #F5EFE7;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 1.5rem 0;
}

.section-header h2 {
  color: #1a2841;
  font-size: 1.5rem;
  margin: 0;
}

.navigation-arrows {
  display: flex;
  gap: 0.5rem;
  color: #3d5a80;
}

.weekly-tournaments-container {
  display: flex;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
}

.tournament-chunk {
  flex: 0 0 100%;
  display: flex;
  gap: 1rem;
  scroll-snap-align: start;
  padding: 0 1rem;
}

.tournament-card-week {
  /* Cada tarjeta ocupa exactamente 1/3 del ancho del chunk */
  flex: 0 0 calc((100% - 2rem) / 3);
  background: white;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.tournament-card-week:hover {
  transform: translateY(-2px);
}

.tournament-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tournament-game-badge {
  background: #3d5a80;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.tournament-name {
  color: #1a2841;
  font-size: 1.2rem;
  margin: 0;
}

.tournament-details {
  margin-top: 1rem;
  display: grid;
  gap: 0.5rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #4a5568;
}

/* Highlight del torneo de hoy */
.today-tournament {
  border-left: 6px solid #3d5a80;
  animation: pulse-highlight 2s infinite;
}

.quick-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin: 2rem 0;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.stat-value {
  font-size: 2.5rem;
  color: #1a2841;
  font-weight: 700;
}

.stat-label {
  color: #3d5a80;
  font-size: 0.9rem;
}

.calendar-cards {
  display: grid;
  gap: 1rem;
}

.calendar-event-card {
  display: flex;
  background: white;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.event-date {
  background: #3d5a80;
  color: white;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  text-align: center;
  min-width: 70px;
}

.day {
  font-size: 1.5rem;
  font-weight: 700;
}

.month {
  text-transform: uppercase;
  font-size: 0.8rem;
}

.event-details {
  padding-left: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.event-name {
  color: #1a2841;
  margin: 0;
  font-size: 1rem;
}

.event-time {
  color: #4a5568;
  margin: 0.25rem 0 0;
}

/* Highlight del evento de hoy */
.today-event .event-date {
  background: #e76f51;
}

@keyframes pulse-highlight {
  0%   { box-shadow: 0 0 0 0 rgba(61, 90, 128, 0.4); }
  70%  { box-shadow: 0 0 0 10px rgba(61, 90, 128, 0); }
  100% { box-shadow: 0 0 0 0 rgba(61, 90, 128, 0); }
}

@media (min-width: 768px) {
  .calendar-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
