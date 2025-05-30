<template>
  <ion-page>
    <ion-content class="no-horizontal-scroll">
      <div class="player-home">

        <!-- Mis torneos de esta semana -->
        <div class="section tournaments">
          <div class="section-header">
            <h2>Torneos de esta semana</h2>
            <div class="navigation-arrows" v-if="pageCount > 1">
              <button class="nav-btn" @click="scrollWeekTournaments('left')">
                <ion-icon :icon="chevronBack" />
              </button>
              <button class="nav-btn" @click="scrollWeekTournaments('right')">
                <ion-icon :icon="chevronForward" />
              </button>
            </div>
          </div>

          <!-- Placeholder cuando no hay torneos -->
          <div v-if="tournamentChunks.length === 0" class="empty-state">
            <ion-icon :icon="trophyOutline" class="empty-icon" />
            <p>No tienes torneos de esta semana</p>
          </div>

          <div
            v-else
            class="weekly-tournaments-container"
            ref="weeklyTournamentsContainer"
            @scroll="onWeekScroll"
          >
            <div
              v-for="(chunk, idx) in tournamentChunks"
              :key="idx"
              class="tournament-chunk"
            >
              <div
                v-for="tournament in chunk"
                :key="tournament.id"
                class="tournament-card-week"
                :class="{
                  'active-tournament': tournament.active,
                  'today-tournament': !tournament.active && isToday(tournament.startDate)
                }"
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
                    <span :class="{ 'missing-location': !tournament.location }">
                      {{ tournament.location || 'Ubicación no disponible' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <transition-group name="dot-move" tag="div" class="dots-container" v-if="filteredSortedTournaments.length > 0">
            <span
              v-if="showLeftIndicator"
              key="left-indicator"
              class="small-dot"
            ></span>
            <span
              v-for="page in visiblePages"
              :key="page"
              class="dot"
              :class="{ active: currentPage === page }"
              @click="goToPage(page)"
            ></span>
            <span
              v-if="showRightIndicator"
              key="right-indicator"
              class="small-dot"
            ></span>
          </transition-group>
        </div>

        <!-- Mis estadísticas -->
        <div class="section stats">
          <div class="stat-card">
            <div class="stat-value">{{ monthlyJoinedTournaments }}</div>
            <div class="stat-label">Torneos inscritos este mes</div>
          </div>
        </div>

        <!-- Mis eventos de la semana -->
        <div class="section events">
          <div class="section-header">
            <h2>Eventos de esta semana</h2>
            <div class="navigation-arrows" v-if="eventPageCount > 1">
              <button class="nav-btn" @click="scrollEvents('left')">
                <ion-icon :icon="chevronBack" />
              </button>
              <button class="nav-btn" @click="scrollEvents('right')">
                <ion-icon :icon="chevronForward" />
              </button>
            </div>
          </div>

          <!-- Placeholder cuando no hay eventos -->
          <div v-if="weeklyEvents.length === 0" class="empty-state">
            <ion-icon :icon="calendarOutline" class="empty-icon" />
            <p>No tienes eventos de esta semana</p>
          </div>
          
          <div
            v-else
            class="events-container"
            ref="eventsContainer"
            @scroll="onEventScroll"
          >
            <div
              v-for="(chunk, idx) in eventChunks"
              :key="idx"
              class="event-chunk"
            >
              <div class="event-grid">
                <div
                  v-for="event in chunk"
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
          
          <transition-group name="dot-move" tag="div" class="dots-container" v-if="weeklyEvents.length > 0">
            <span
              v-if="showLeftEventIndicator"
              key="left-event-indicator"
              class="small-dot"
            ></span>
            <span
              v-for="page in visibleEventPages"
              :key="page"
              class="dot"
              :class="{ active: currentEventPage === page }"
              @click="goToEventPage(page)"
            ></span>
            <span
              v-if="showRightEventIndicator"
              key="right-event-indicator"
              class="small-dot"
            ></span>
          </transition-group>
        </div>

      </div>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { IonPage, IonContent, IonIcon } from '@ionic/vue'
import { chevronBack, chevronForward, trophyOutline } from 'ionicons/icons'
import { calendarOutline, timeOutline, locationOutline } from 'ionicons/icons'

const API_URL = 'http://localhost:8082'
function getToken() { 
  return localStorage.getItem('token') || '' 
}

// Torneos semanales para jugador
const weeklyTournaments = ref([])
const monthlyJoinedTournaments = ref(0)
const totalRegistrations = ref(0)
const weeklyTournamentsContainer = ref(null)

const currentPage = ref(0)
const filteredSortedTournaments = computed(() =>
  weeklyTournaments.value
    .filter(t => !t.closed)
    .sort((a, b) => new Date(a.startDate) - new Date(b.startDate))
)

const tournamentChunks = computed(() => {
  const arr = filteredSortedTournaments.value
  const chunks = []
  for (let i = 0; i < arr.length; i += 3) {
    chunks.push(arr.slice(i, i + 3))
  }
  return chunks
})
const pageCount = computed(() => tournamentChunks.value.length)
const visiblePages = computed(() => {
  const total = pageCount.value
  if (total <= 3) return Array.from({ length: total }, (_, i) => i)
  let start = currentPage.value - 1
  if (start < 0) start = 0
  if (start > total - 3) start = total - 3
  return [start, start + 1, start + 2]
})
const showLeftIndicator = computed(() => currentPage.value > 1)
const showRightIndicator = computed(() => currentPage.value < pageCount.value - 2)

function onWeekScroll() {
  const c = weeklyTournamentsContainer.value
  if (!c) return
  const page = Math.round(c.scrollLeft / c.clientWidth)
  if (page !== currentPage.value) currentPage.value = page
}
function goToPage(page) {
  const c = weeklyTournamentsContainer.value
  if (!c) return
  currentPage.value = page
  c.scrollTo({ left: c.clientWidth * page, behavior: 'smooth' })
}
function scrollWeekTournaments(dir) {
  const c = weeklyTournamentsContainer.value
  if (!c) return
  if (dir === 'right' && currentPage.value < pageCount.value - 1) currentPage.value++
  if (dir === 'left' && currentPage.value > 0) currentPage.value--
  c.scrollBy({ left: dir === 'right' ? c.clientWidth : -c.clientWidth, behavior: 'smooth' })
}

// Estadísticas de jugador
async function fetchPlayerStats() {
  try {
    const res = await fetch(`${API_URL}/api/tournaments/stats/player`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    const d = await res.json()
    monthlyJoinedTournaments.value = d.tournamentsThisMonth
    totalRegistrations.value     = d.playersThisMonth
  } catch (e) {
    console.error('Error cargando stats jugador:', e)
  }
}

// Cargar torneos semanales de jugador
async function fetchWeeklyPlayerTournaments() {
  try {
    const res = await fetch(`${API_URL}/api/tournaments/weekly/player`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    weeklyTournaments.value = await res.json()
  } catch (e) {
    console.error('Error cargando torneos semanales jugador:', e)
  }
}

// Eventos semanales desde el backend
const weeklyEvents = ref([])
async function fetchWeeklyEvents() {
  try {
    const res = await fetch(`${API_URL}/api/events/weekly`, {
      headers: { Authorization: `Bearer ${getToken()}` }
    })
    weeklyEvents.value = (await res.json()).map(ev => ({
      id: ev.id,
      name: ev.title,
      startDate: ev.eventTime
    }))
  } catch (e) {
    console.error('Error cargando eventos semanales:', e)
  }
}

// Paginación de eventos (2x2)
const eventsContainer      = ref(null)
const currentEventPage    = ref(0)
const eventChunks         = computed(() => {
  const arr = weeklyEvents.value
  const chunks = []
  for (let i = 0; i < arr.length; i += 4) {
    chunks.push(arr.slice(i, i + 4))
  }
  return chunks
})
const eventPageCount       = computed(() => eventChunks.value.length)
const visibleEventPages    = computed(() => {
  const total = eventPageCount.value
  if (total <= 3) return Array.from({ length: total }, (_, i) => i)
  let start = currentEventPage.value - 1
  if (start < 0) start = 0
  if (start > total - 3) start = total - 3
  return [start, start + 1, start + 2]
})
const showLeftEventIndicator  = computed(() => currentEventPage.value > 1)
const showRightEventIndicator = computed(() => currentEventPage.value < eventPageCount.value - 2)
function onEventScroll() {
  const c = eventsContainer.value
  if (!c) return
  const page = Math.round(c.scrollLeft / c.clientWidth)
  if (page !== currentEventPage.value) currentEventPage.value = page
}
function goToEventPage(page) {
  const c = eventsContainer.value
  if (!c) return
  currentEventPage.value = page
  c.scrollTo({ left: c.clientWidth * page, behavior: 'smooth' })
}
function scrollEvents(dir) {
  const c = eventsContainer.value
  if (!c) return
  if (dir === 'right' && currentEventPage.value < eventPageCount.value - 1) currentEventPage.value++
  if (dir === 'left' && currentEventPage.value > 0) currentEventPage.value--
  c.scrollBy({ left: dir === 'right' ? c.clientWidth : -c.clientWidth, behavior: 'smooth' })
}

// Utilidades
const isToday = ds => new Date(ds).toDateString() === new Date().toDateString()
const formatDay   = ds => new Date(ds).getDate()
const formatMonth = ds => new Date(ds).toLocaleString('es-ES', { month: 'short' })
const formatTime  = ds => new Date(ds).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })

onMounted(async () => {
  await Promise.all([
    fetchWeeklyPlayerTournaments(),
    fetchPlayerStats(),
    fetchWeeklyEvents()
  ])
})
</script>
  
<style scoped>
/* Scroll vertical */
ion-content {
  --background: #f9f5f0;
  overflow-y: auto;
}

.no-horizontal-scroll {
  overflow-x: hidden;
}

/* Contenedor principal */
.player-home {
  background: #f9f5f0;
  max-width: 1200px;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
  padding: 1rem;
  display: grid;
  grid-template-rows: auto auto auto;
  gap: 1rem;
  box-sizing: border-box;
  position: relative; 
  overflow-x: hidden; 
}

/* Secciones */
.section {
  padding: 1rem;
  width: 100%;
  background-color: #e0e1dd;
  border-radius: 1rem;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  overflow: hidden;
}

.section:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(26, 40, 65, 0.15);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding: 0 0.5rem;
}

.section-header h2 {
  color: #1a2841;
  font-size: 1.5rem;
  margin: 0;
  font-weight: 600;
}

/* Placeholder para estados vacíos */
.empty-state {
  text-align: center;
  padding: 2rem;
  background: #e0e1dd;
  border-radius: 1rem;
  margin: 0;
}

.empty-state p {
  color: #3d5a80;
  font-weight: 600;
  margin: 0;
}

.empty-icon {
  font-size: 3rem;
  color: #3d5a80;
  margin-bottom: 1rem;
}

/* Navegación flechas */
.navigation-arrows {
  display: flex;
  gap: 0.5rem;
}
.nav-btn {
  background: linear-gradient(145deg, #3d5a80, #2c4366);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.nav-btn ion-icon {
  color: #ffffff;
  font-size: 1.2rem;
}
.nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}
.nav-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Torneos semanales */
.weekly-tournaments-container {
  display: flex;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  scrollbar-width: none;
  -ms-overflow-style: none;
  max-width: 100%;
  width: 100%;
  position: relative;
  padding: 10px 0; 
  margin: 0; 
  overflow-y: visible; 
}

.weekly-tournaments-container::-webkit-scrollbar {
  display: none;
}

.tournament-chunk {
  flex: 0 0 100%;
  display: flex;
  gap: 1rem;
  scroll-snap-align: start;
  padding: 10px 1rem; 
  width: 100%;
  min-width: 100%;
  box-sizing: border-box;
  justify-content: flex-start; 
}

.tournament-card-week {
  flex: 0 0 calc((100% - 2rem) / 3);
  background: #fff;
  border-radius: 16px;
  padding: 1.25rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: relative;
  overflow: visible; 
  transform-origin: center center;
  margin: 0; 
}

.tournament-card-week:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.tournament-header {
  margin-bottom: 1rem;
  position: relative;
}

.tournament-game-badge {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: #ffffff;
  display: inline-block;
  padding: 0.4rem 0.8rem;
  border-radius: 30px;
  font-size: 0.8rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.tournament-name {
  color: #1a2841;
  font-size: 1.2rem;
  font-weight: 700;
  margin: 0.5rem 0;
  line-height: 1.3;
}

.tournament-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #4a5568;
  font-size: 0.9rem;
}

.detail-item ion-icon {
  color: #3d5a80;
  font-size: 1.1rem;
}

.missing-location {
  color: #3d5a80;
  font-style: italic;
}

.today-tournament {
  position: relative;
}

.today-tournament::before {
  content: "Hoy";
  position: absolute;
  top: 0;
  right: 0;
  background: #3d5a80;
  color: white;
  padding: 0.3rem 0.8rem;
  font-size: 0.7rem;
  font-weight: 700;
  border-radius: 0 16px 0 8px; 
  box-shadow: -2px 2px 4px rgba(0, 0, 0, 0.1);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Puntos e indicadores */
.dots-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 1.5rem;
  margin-top: 1rem;
}
.dot, .small-dot {
  border-radius: 50%;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.dot {
  width: 10px;
  height: 10px;
  background: #ccc;
  cursor: pointer;
  margin: 0 .25rem;
}
.dot.active {
  background: #3d5a80;
  transform: scale(1.3);
  box-shadow: 0 0 0 2px rgba(61, 90, 128, 0.3);
}
.dot:hover {
  background: #aaa;
}
.small-dot {
  width: 5px;
  height: 5px;
  background: #aaa;
  opacity: 0.6;
  pointer-events: none;
  margin: 0 .25rem;
}

/* Transiciones entre dots */
.dot-move-enter-from,
.dot-move-leave-to {
  opacity: 0;
  transform: scale(0.3);
}
.dot-move-enter-active {
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
}
.dot-move-leave-active {
  transition: all 0.5s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: absolute;
}
.dot-move-enter-to,
.dot-move-leave-from {
  opacity: 1;
  transform: scale(1);
}

/* Estadísticas rápidas */
.stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
}
.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  flex: 1;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}
.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}
.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #3d5a80, #8eacc5);
}
.stat-value {
  font-size: 2.5rem;
  color: #1a2841;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 0.5rem;
}
.stat-label {
  color: #3d5a80;
  font-size: 0.9rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Eventos semanales */
.events-container {
  display: flex;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  scrollbar-width: none;
  -ms-overflow-style: none;
  max-width: 100%;
  width: 100%;
  position: relative;
  padding: 10px 0; 
  overflow-y: visible; 
}

.events-container::-webkit-scrollbar {
  display: none;
}

.event-chunk {
  flex: 0 0 100%;
  scroll-snap-align: start;
  padding: 10px 1rem;
  width: 100%;
  min-width: 100%;
  box-sizing: border-box;
}

/* Grid para mostrar 2x2 eventos */
.event-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, auto);
  gap: 1rem;
  width: 100%;
}

/* Tarjeta de evento */
.calendar-event-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  padding: 1rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  transform-origin: center center;
  overflow: visible; 
}

.calendar-event-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.event-date {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: white;
  border-radius: 12px;
  padding: 0.75rem 0.75rem;
  text-align: center;
  min-width: 60px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.event-date.today-event {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
}

.day {
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1;
}

.month {
  text-transform: uppercase;
  font-size: 0.75rem;
  letter-spacing: 1px;
  margin-top: 0.25rem;
  opacity: 0.9;
}

.event-details {
  padding-left: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.event-name {
  color: #1a2841;
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
}

.event-time {
  color: #4a5568;
  margin: 0.25rem 0 0;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
}

.event-time::before {
  content: "";
  display: inline-block;
  width: 8px;
  height: 8px;
  background-color: #3d5a80;
  border-radius: 50%;
  margin-right: 0.5rem;
}

.active-tournament {
  border-left: 6px solid #3d5a80;
  box-shadow: 0 6px 16px rgba(65, 90, 119, 0.2);
  animation: pulse-active 2s infinite;
  position: relative;
}

.active-tournament::before {
  content: "Activo";
  position: absolute;
  top: 0;
  right: 0;
  background: #415a77;
  color: white;
  padding: 0.3rem 0.8rem;
  font-size: 0.7rem;
  font-weight: 700;
  border-radius: 0 16px 0 8px;
  box-shadow: -2px 2px 4px rgba(0, 0, 0, 0.1);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* Animaciones */
@keyframes pulse-highlight {
  0%   { box-shadow: 0 0 0 0 rgba(61, 90, 128, 0.4); }
  70%  { box-shadow: 0 0 0 10px rgba(61, 90, 128, 0); }
  100% { box-shadow: 0 0 0 0 rgba(61, 90, 128, 0); }
}

@keyframes shadow-pulse {
  0%   { box-shadow: 0 0 0 0 rgba(65, 90, 119, 0.7); }
  70%  { box-shadow: 0 0 15px 10px rgba(65, 90, 119, 0); }
  100% { box-shadow: 0 0 0 0 rgba(65, 90, 119, 0); }
}

@keyframes pulse-active {
  0%   { box-shadow: 0 0 0 0 rgba(61, 90, 128, 0.4); }
  70%  { box-shadow: 0 0 0 10px rgba(61, 90, 128, 0); }
  100% { box-shadow: 0 0 0 0 rgba(61, 90, 128, 0); }
}

/* Responsive breakpoints */
@media (max-width: 1460px) {
  .player-home {
    width: 100%;
    max-width: 100%;
    padding: 1rem;
  }
  .weekly-tournaments-container {
    overflow-x: auto;
    scroll-snap-type: x mandatory;
  }
  .tournament-chunk {
    display: flex;
    flex-direction: row;
    padding: 0;
  }
  .tournament-card-week {
    flex: 0 0 calc((100% - 2rem) / 3);
    margin-bottom: 0;
  }
  
  .event-grid {
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(2, auto);
  }
}

@media (max-width: 1024px) {
  .tournament-chunk {
    flex-direction: column;
  }
  .tournament-card-week {
    flex: none;
    width: 100%;
    margin-bottom: 1rem;
    max-width: none;
  }
  
  .event-grid {
    grid-template-columns: 1fr; 
    grid-template-rows: auto;
  }
}

@media (max-width: 768px) {
  .player-home {
    padding: 0.75rem;
  }
  .section {
    padding: 1rem;
  }
  .section-header {
    padding: 0;
    margin-bottom: 1.5rem;
  }
  .section-header h2 {
    font-size: 1.3rem;
    width: 100%;
  }
  .navigation-arrows {
    display: none;
  }
  .stats {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 0.75rem;
    padding: 1rem;
  }
  .stat-card {
    flex: 0 0 calc(50% - 0.75rem);
    padding: 1.25rem 1rem;
    min-width: 0;
  }
  .stat-value {
    font-size: 2rem;
  }
  .stat-label {
    font-size: 0.8rem;
  }
  .events-container {
    width: 100%;
  }
  .nav-btn {
    width: 36px;
    height: 36px;
  }
}

@media (max-width: 490px) {
  .player-home {
    padding: 0.5rem;
  }
  .section {
    padding: 0.75rem;
    margin-bottom: 0.5rem;
  }
  .section-header {
    margin-bottom: 2rem;
  }
  .section-header h2 {
    font-size: 1.2rem;
  }
  .nav-btn {
    width: 32px;
    height: 32px;
  }
  .nav-btn ion-icon {
    font-size: 1rem;
  }
  .tournament-card-week {
    padding: 1rem;
  }
  .tournament-game-badge {
    padding: 0.3rem 0.6rem;
    font-size: 0.7rem;
  }
  .tournament-name {
    font-size: 1.1rem;
  }
  .detail-item {
    font-size: 0.85rem;
  }
  .stats {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 0.5rem;
    padding: 0.75rem;
  }
  .stat-card {
    flex: 0 0 calc(50% - 0.5rem);
    padding: 1rem 0.75rem;
  }
  .stat-value {
    font-size: 1.75rem;
  }
  .stat-label {
    font-size: 0.7rem;
  }
  .calendar-event-card {
    padding: 0.75rem;
  }
  .event-date {
    padding: 0.5rem 0.5rem;
    min-width: 50px;
  }
  .day {
    font-size: 1.25rem;
  }
  .month {
    font-size: 0.65rem;
  }
  .event-details {
    padding-left: 0.75rem;
  }
  .event-name {
    font-size: 0.9rem;
  }
  .event-time {
    font-size: 0.8rem;
  }
  
  .weekly-tournaments-container,
  .tournament-chunk,
  .tournament-card-week,
  .stats,
  .stat-card,
  .events-container,
  .event-chunk,
  .calendar-event-card {
    width: 100%;
    max-width: 100%;
    box-sizing: border-box;
  }
}

@media (max-width: 360px) {
  .player-home {
    padding: 0.25rem;
  }
  .section {
    padding: 0.5rem;
    border-radius: 0.75rem;
  }
  .section-header {
    margin-bottom: 2.25rem;
  }
  .section-header h2 {
    font-size: 1.1rem;
  }
  .nav-btn {
    width: 28px;
    height: 28px;
  }
  .nav-btn ion-icon {
    font-size: 0.9rem;
  }
  .stat-card {
    padding: 0.75rem 0.5rem;
  }
  .stat-value {
    font-size: 1.5rem;
  }
  .stat-label {
    font-size: 0.65rem;
  }
  .event-date {
    min-width: 45px;
  }
  .day {
    font-size: 1.25rem;
  }
  .month {
    font-size: 0.65rem;
  }
  .event-name {
    font-size: 0.9rem;
  }
  
  .tournament-card-week {
    padding: 0.75rem;
  }
  .tournament-game-badge {
    padding: 0.25rem 0.5rem;
    font-size: 0.65rem;
  }
  .tournament-name {
    font-size: 1rem;
    margin: 0.25rem 0;
  }
  .detail-item {
    font-size: 0.75rem;
  }
  .detail-item ion-icon {
    font-size: 1rem;
  }
}

@media (min-width: 1461px) {
  .player-home {
    max-width: 1200px;
    width: 100%;
    margin-left: auto;
    margin-right: auto;
    overflow-x: hidden;
  }
  
  .weekly-tournaments-container,
  .events-container {
    width: 100%;
    max-width: 100%;
  }
  
  .tournaments,
  .events {
    width: 100%;
    max-width: 100%;
    overflow: hidden;
  }
}

.section.tournaments {
  overflow: hidden; 
  padding: 1rem; 
}

.weekly-tournaments-container {
  overflow-y: visible;
}
</style>