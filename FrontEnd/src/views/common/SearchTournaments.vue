<template>
    <ion-page>
      <ion-content class="tournament-app" :fullscreen="true">
        <!-- Pantalla de búsqueda inicial -->
        <div v-if="!hasSearched" class="search-screen">
          <div class="card filter-card">
            <div class="filter-grid">
              <!-- Localidad -->
              <div class="filter-item autocomplete-container" ref="locationContainer">
                <label>Localidad</label>
                <ion-input
                  v-model="locationSearch"
                  placeholder="Busca y selecciona localidad"
                  @focus="openLocation"
                  @click="openLocation"
                />
                <ul v-if="showLocation" class="suggestions-list">
                  <li
                    v-for="prov in filteredLocations"
                    :key="prov.code"
                    @mousedown.prevent="selectLocation(prov.name)"
                  >
                    {{ prov.name }}
                  </li>
                </ul>
              </div>
              <!-- Juego -->
              <div class="filter-item autocomplete-container" ref="gameContainer">
                <label>Juego</label>
                <ion-input
                  v-model="filters.game"
                  placeholder="Selecciona juego"
                  @focus="openGame"
                  @click="openGame"
                />
                <ul v-if="showGame" class="suggestions-list">
                  <li
                    v-for="g in filteredGames"
                    :key="g.code"
                    @mousedown.prevent="selectGame(g.name)"
                  >
                    {{ g.name }}
                  </li>
                </ul>
              </div>
              <!-- Mes -->
              <div class="filter-item autocomplete-container" ref="monthContainer">
                <label>Mes</label>
                <ion-input
                  v-model="filters.monthLabel"
                  placeholder="Mes"
                  @focus="openMonth"
                  @click="openMonth"
                  readonly
                />
                <ul v-if="showMonth" class="suggestions-list">
                  <li
                    v-for="m in months"
                    :key="m.value"
                    @mousedown.prevent="selectMonth(m)"
                  >
                    {{ m.label }}
                  </li>
                </ul>
              </div>
              <!-- Día -->
              <div class="filter-item autocomplete-container" ref="dayContainer">
                <label>Día</label>
                <ion-input
                  v-model="filters.day"
                  placeholder="Día"
                  @focus="openDay"
                  @click="openDay"
                  :disabled="!filters.month"
                />
                <ul v-if="showDay" class="suggestions-list">
                  <li
                    v-for="d in days"
                    :key="d"
                    @mousedown.prevent="selectDay(d)"
                  >
                    {{ d }}
                  </li>
                </ul>
              </div>
            </div>
            <ion-button
              class="search-button"
              expand="block"
              @click="searchTournaments"
              :disabled="isLoading"
            >
              Buscar
            </ion-button>
          </div>
        </div>
  
        <!-- Resultados tras búsqueda -->
        <div v-else class="results-wrapper">
          <div class="card filter-card">
            <div class="filter-grid">
              <!-- filtros refinados, idénticos al inicial -->
              <div class="filter-item autocomplete-container" ref="locationContainer2">
                <label>Localidad</label>
                <ion-input
                  v-model="locationSearch"
                  @focus="openLocation"
                  @click="openLocation"
                  placeholder="Busca y selecciona localidad"
                />
                <ul v-if="showLocation" class="suggestions-list">
                  <li
                    v-for="prov in filteredLocations"
                    :key="prov.code"
                    @mousedown.prevent="selectLocation(prov.name)"
                  >
                    {{ prov.name }}
                  </li>
                </ul>
              </div>
              <div class="filter-item autocomplete-container" ref="gameContainer2">
                <label>Juego</label>
                <ion-input
                  v-model="filters.game"
                  @focus="openGame"
                  @click="openGame"
                  placeholder="Selecciona juego"
                />
                <ul v-if="showGame" class="suggestions-list">
                  <li
                    v-for="g in filteredGames"
                    :key="g.code"
                    @mousedown.prevent="selectGame(g.name)"
                  >
                    {{ g.name }}
                  </li>
                </ul>
              </div>
              <div class="filter-item autocomplete-container" ref="monthContainer2">
                <label>Mes</label>
                <ion-input
                  v-model="filters.monthLabel"
                  @focus="openMonth"
                  @click="openMonth"
                  placeholder="Mes"
                  readonly
                />
                <ul v-if="showMonth" class="suggestions-list">
                  <li
                    v-for="m in months"
                    :key="m.value"
                    @mousedown.prevent="selectMonth(m)"
                  >
                    {{ m.label }}
                  </li>
                </ul>
              </div>
              <div class="filter-item autocomplete-container" ref="dayContainer2">
                <label>Día</label>
                <ion-input
                  v-model="filters.day"
                  @focus="openDay"
                  @click="openDay"
                  :disabled="!filters.month"
                  placeholder="Día"
                />
                <ul v-if="showDay" class="suggestions-list">
                  <li
                    v-for="d in days"
                    :key="d"
                    @mousedown.prevent="selectDay(d)"
                  >
                    {{ d }}
                  </li>
                </ul>
              </div>
            </div>
            <ion-button
              class="search-button"
              expand="block"
              @click="searchTournaments"
              :disabled="isLoading"
            >
              Buscar
            </ion-button>
          </div>
  
          <div class="results-section">
            <div v-if="isLoading" class="loader-container">
              <div class="loader"></div>
            </div>
            <div v-else>
              <div v-if="tournaments.length === 0" class="empty-state">
                <ion-icon name="trophy-outline"></ion-icon>
                <p>No se encontraron torneos</p>
                <span>Prueba con otros filtros</span>
              </div>
              <div v-else class="results-grid">
                <router-link
                  v-for="t in tournaments"
                  :key="t.id"
                  :to="getTournamentPath(t.id)"
                  class="card tournament-card"
                >
                  <div class="card-header">
                    <h3 class="tournament-name">
                      [{{ capitalize(t.game) }}] {{ t.name }}
                    </h3>
                    <span class="date-display">{{ formatDate(t.startDate) }}</span>
                  </div>
                  <div class="card-body">
                    <div class="detail-item">
                      <strong>Organizador:</strong> {{ t.organizerId }}
                    </div>
                    <div class="detail-item">
                      <strong>Localidad:</strong> {{ capitalize(t.location) }}
                    </div>
                  </div>
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </ion-content>
    </ion-page>
  </template>
  
  <script>
  import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
  import { useRouter } from 'vue-router'
  import { IonPage, IonContent, IonInput, IonButton, IonIcon } from '@ionic/vue'
  import axios from 'axios'
  import locations from '@/assets/data/location/locations.json'
  import gamesList from '@/assets/data/games/games.json'
  
  export default {
    name: 'TournamentSearch',
    components: { IonPage, IonContent, IonInput, IonButton, IonIcon },
    setup() {
      const router = useRouter()
      const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8082'
  
      const filters = ref({ game: '', month: '', monthLabel: '', day: '' })
      const locationSearch = ref('')
      const showLocation = ref(false)
      const showGame = ref(false)
      const showMonth = ref(false)
      const showDay = ref(false)
  
      const locationContainer = ref(null)
      const gameContainer = ref(null)
      const monthContainer = ref(null)
      const dayContainer = ref(null)
      const locationContainer2 = ref(null)
      const gameContainer2 = ref(null)
      const monthContainer2 = ref(null)
      const dayContainer2 = ref(null)
  
      const tournaments = ref([])
      const isLoading = ref(false)
      const hasSearched = ref(false)
      const userRole = ref('player')
  
      const months = [
        { value: '01', label: 'Enero' }, { value: '02', label: 'Febrero' },
        { value: '03', label: 'Marzo' }, { value: '04', label: 'Abril' },
        { value: '05', label: 'Mayo' }, { value: '06', label: 'Junio' },
        { value: '07', label: 'Julio' }, { value: '08', label: 'Agosto' },
        { value: '09', label: 'Septiembre' }, { value: '10', label: 'Octubre' },
        { value: '11', label: 'Noviembre' }, { value: '12', label: 'Diciembre' },
      ]
      const days = Array.from({ length: 31 }, (_, i) => String(i + 1).padStart(2, '0'))
  
      const filteredLocations = computed(() => {
        return locations.filter(p =>
          p.name.toLowerCase().includes(locationSearch.value.toLowerCase())
        )
      })
      const filteredGames = computed(() => {
        return gamesList.filter(g =>
          g.name.toLowerCase().includes(filters.value.game.toLowerCase())
        )
      })
  
      onMounted(async () => {
        document.addEventListener('click', handleClickOutside)
        try {
          const { data: role } = await axios.get(
            'http://localhost:8080/api/auth/me/role',
            { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
          )
          userRole.value = role
        } catch {
          userRole.value = 'player'
        }
      })
      onBeforeUnmount(() => {
        document.removeEventListener('click', handleClickOutside)
      })
  
      function openLocation() {
        locationSearch.value = ''
        showLocation.value = true
        showGame.value = showMonth.value = showDay.value = false
      }
      function selectLocation(n) {
        locationSearch.value = n
        showLocation.value = false
      }
      function openGame() {
        filters.value.game = ''
        showGame.value = true
        showLocation.value = showMonth.value = showDay.value = false
      }
      function selectGame(n) {
        filters.value.game = n
        showGame.value = false
      }
      function openMonth() {
        filters.value.month = ''
        filters.value.monthLabel = ''
        filters.value.day = ''
        showMonth.value = true
        showLocation.value = showGame.value = showDay.value = false
      }
      function selectMonth(m) {
        filters.value.month = m.value
        filters.value.monthLabel = m.label
        showMonth.value = false
      }
      function openDay() {
        if (!filters.value.month) return
        filters.value.day = ''
        showDay.value = true
        showLocation.value = showGame.value = showMonth.value = false
      }
      function selectDay(d) {
        filters.value.day = d
        showDay.value = false
      }
  
      function handleClickOutside(e) {
        ;[
          [locationContainer, showLocation],
          [gameContainer, showGame],
          [monthContainer, showMonth],
          [dayContainer, showDay],
          [locationContainer2, showLocation],
          [gameContainer2, showGame],
          [monthContainer2, showMonth],
          [dayContainer2, showDay],
        ].forEach(([refEl, flag]) => {
          if (refEl.value && !refEl.value.contains(e.target)) {
            flag.value = false
          }
        })
      }
  
      async function searchTournaments() {
        const token = localStorage.getItem('token')
        if (!token) return router.push('/auth/login')
        hasSearched.value = true
        isLoading.value = true
        try {
          const params = { location: locationSearch.value.trim(), game: filters.value.game }
          if (filters.value.month && !filters.value.day) {
            const y = new Date().getFullYear()
            params.fromDate = `${y}-${filters.value.month}-01`
            const last = new Date(y, +filters.value.month, 0).getDate()
            params.toDate = `${y}-${filters.value.month}-${String(last).padStart(2, '0')}`
          } else if (filters.value.month && filters.value.day) {
            const d = `${new Date().getFullYear()}-${filters.value.month}-${filters.value.day}`
            params.fromDate = params.toDate = d
          }
          const { data } = await axios.get(`${API_BASE}/api/tournaments/search`, {
            params,
            headers: { Authorization: `Bearer ${token}` }
          })
          tournaments.value = Array.isArray(data) ? data : []
        } catch {
          tournaments.value = []
        } finally {
          isLoading.value = false
        }
      }
  
      function getTournamentPath(id) {
        const base = userRole.value === 'organizer'
          ? 'organizer-tournament-profile'
          : 'player-tournament-profile'
        return `/web/${base}/${id}`
      }
  
      function formatDate(iso) {
        return new Date(iso).toLocaleDateString('es-ES', {
          year: 'numeric', month: 'short', day: 'numeric'
        })
      }
      function capitalize(s) {
        return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase()
      }
  
      return {
        filters, locationSearch,
        showLocation, showGame, showMonth, showDay,
        filteredLocations, filteredGames, months, days,
        tournaments, isLoading, hasSearched,
        openLocation, selectLocation,
        openGame, selectGame,
        openMonth, selectMonth,
        openDay, selectDay,
        locationContainer, gameContainer, monthContainer, dayContainer,
        locationContainer2, gameContainer2, monthContainer2, dayContainer2,
        searchTournaments, getTournamentPath,
        formatDate, capitalize
      }
    }
  }
  </script>
  
  <style scoped>
  .tournament-app {
    --background: #F5EFE7;
    height: 100%;
  }
  
  .search-screen {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
  }
  
  .card {
    background-color: #e0e1dd;
    border-radius: 1rem;
    box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
    padding: 1rem;
    margin-bottom: 1rem;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }
  .card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(26, 40, 65, 0.15);
  }
  
  .filter-card {
    width: 95%;
    max-width: 1200px;
    margin: 1.5rem auto;
  }
  
  @media (min-width: 900px) {
    .filter-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 1rem;
      margin-bottom: 1rem;
    }
  }
  @media (max-width: 899px) {
    .filter-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
      margin-bottom: 1rem;
    }
  }
  @media (max-width: 600px) {
    .filter-grid {
      grid-template-columns: 1fr;
    }
  }
  
  .filter-item label {
    display: block;
    font-size: 0.9rem;
    font-weight: 600;
    color: #1a2841;
    margin-bottom: 0.25rem;
  }
  
  .autocomplete-container {
    position: relative;
  }
  .suggestions-list {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    max-height: 200px;
    overflow-y: auto;
    background: #fff;
    border: 1px solid #ccc;
    border-radius: 0.5rem;
    z-index: 10;
    list-style: none;
    padding: 0;
    margin: 0;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  .suggestions-list li {
    padding: 0.5rem;
    cursor: pointer;
    color: #1a2841;
  }
  .suggestions-list li:hover {
    background: #f0f0f0;
  }
  
  .filter-item ion-input,
  .filter-item ion-select {
    --background: #fff;
    --border-radius: 0.5rem;
    font-size: 1rem;
    color: #1a2841;
  }
  
  .search-button {
    --background: #3d5a80;
    --color: #fff;
    font-weight: 600;
    width: 40%;
    margin: 0 auto;
  }
  
  .results-wrapper {
    padding-top: 1rem;
  }
  
  .results-section {
    padding: 0 1rem 1rem;
  }
  .results-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 1rem;
    max-width: 1200px;
    margin: 0 auto;
    justify-content: center;
  }
  .tournament-card {
    background-color: #d3d0cb;
    width: 100%;
    max-width: 300px;
    margin: 0 auto;
    text-decoration: none;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.5rem;
  }
  .tournament-name {
    font-size: 1.1rem;
    font-weight: 700;
    color: #1a2841;
  }
  .date-display {
    font-size: 0.9rem;
    color: #415a77;
  }
  .card-body .detail-item {
    background: #e0e1dd;
    border-radius: 0.5rem;
    padding: 0.5rem;
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
    color: #1a2841;
  }
  
  .loader-container,
  .empty-state {
    text-align: center;
    padding: 2rem;
    color: #415a77;
  }
  .loader {
    border: 6px solid #eee;
    border-top: 6px solid #1a2841;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    animation: spin 1s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }
  .empty-state ion-icon {
    font-size: 48px;
    margin-bottom: 0.5rem;
  }
  .empty-state p {
    font-size: 1rem;
    font-weight: 500;
  }
  .empty-state span {
    font-size: 0.85rem;
    opacity: 0.8;
  }
  ion-input[disabled] {
    --opacity: 0.5;
    pointer-events: none;
  }
  </style>
  