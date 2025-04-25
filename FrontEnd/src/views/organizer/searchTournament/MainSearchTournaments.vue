<template>
  <ion-page>
    <ion-content class="tournament-app" :fullscreen="true">
      <!-- Pantalla de búsqueda inicial -->
      <div v-if="!hasSearched" class="search-screen">
        <div class="card filter-card">
          <div class="filter-grid">
            <div class="filter-item autocomplete-container" ref="autocompleteContainer">
              <label>Localidad</label>
              <ion-input
                v-model="locationSearch"
                placeholder="Busca y selecciona localidad"
                @input="onLocationInput"
                @focus="showAllSuggestions"
              />
              <ul v-if="showSuggestions && filteredLocations.length" class="suggestions-list">
                <li
                  v-for="prov in filteredLocations"
                  :key="prov.code"
                  @mousedown.prevent="selectLocation(prov.name)"
                >
                  {{ prov.name }}
                </li>
              </ul>
            </div>
            <div class="filter-item">
              <label>Juego</label>
              <ion-input v-model="filters.game" placeholder="Ej. Fútbol, Catan" />
            </div>
            <div class="filter-item">
              <label>Mes</label>
              <ion-select v-model="filters.month" placeholder="Mes">
                <ion-select-option
                  v-for="m in months"
                  :key="m.value"
                  :value="m.value"
                >
                  {{ m.label }}
                </ion-select-option>
              </ion-select>
            </div>
            <div class="filter-item">
              <label>Día</label>
              <ion-select v-model="filters.day" placeholder="Día" :disabled="!filters.month">
                <ion-select-option
                  v-for="d in days"
                  :key="d"
                  :value="d"
                >
                  {{ d }}
                </ion-select-option>
              </ion-select>
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
      <div v-else>
        <div class="card filter-card">
          <div class="filter-grid">
            <div class="filter-item autocomplete-container" ref="autocompleteContainer">
              <label>Localidad</label>
              <ion-input
                v-model="locationSearch"
                placeholder="Busca y selecciona localidad"
                @input="onLocationInput"
                @focus="showAllSuggestions"
              />
              <ul v-if="showSuggestions && filteredLocations.length" class="suggestions-list">
                <li
                  v-for="prov in filteredLocations"
                  :key="prov.code"
                  @mousedown.prevent="selectLocation(prov.name)"
                >
                  {{ prov.name }}
                </li>
              </ul>
            </div>
            <div class="filter-item">
              <label>Juego</label>
              <ion-input v-model="filters.game" placeholder="Ej. Fútbol, Catan" />
            </div>
            <div class="filter-item">
              <label>Mes</label>
              <ion-select v-model="filters.month" placeholder="Mes">
                <ion-select-option
                  v-for="m in months"
                  :key="m.value"
                  :value="m.value"
                >
                  {{ m.label }}
                </ion-select-option>
              </ion-select>
            </div>
            <div class="filter-item">
              <label>Día</label>
              <ion-select v-model="filters.day" placeholder="Día" :disabled="!filters.month">
                <ion-select-option
                  v-for="d in days"
                  :key="d"
                  :value="d"
                >
                  {{ d }}
                </ion-select-option>
              </ion-select>
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
              <div
                v-for="t in tournaments"
                :key="t.name + t.startDate"
                class="card tournament-card"
              >
                <div class="card-header">
                  <h3 class="tournament-name">[{{ capitalize(t.game) }}] {{ t.name }}</h3>
                  <span class="date-display">{{ formatDate(t.startDate) }}</span>
                </div>
                <div class="card-body">
                  <div class="detail-item"><strong>Organizador:</strong> {{ t.organizerName }}</div>
                  <div class="detail-item"><strong>Localidad:</strong> {{ capitalize(t.location) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import {
  IonPage,
  IonContent,
  IonSelect,
  IonSelectOption,
  IonInput,
  IonButton,
  IonIcon,
} from '@ionic/vue';
import axios from 'axios';
import locations from '@/assets/data/location/locations.json';

export default {
  name: 'TournamentSearch',
  components: {
    IonPage,
    IonContent,
    IonSelect,
    IonSelectOption,
    IonInput,
    IonButton,
    IonIcon,
  },
  setup() {
    const router = useRouter();
    const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8082';

    const filters = ref({ game: '', month: '', day: '' });
    const locationSearch = ref('');
    const showSuggestions = ref(false);
    const tournaments = ref([]);
    const isLoading = ref(false);
    const hasSearched = ref(false);
    const autocompleteContainer = ref(null);

    const months = [
      { value: '01', label: 'Enero' },
      { value: '02', label: 'Febrero' },
      { value: '03', label: 'Marzo' },
      { value: '04', label: 'Abril' },
      { value: '05', label: 'Mayo' },
      { value: '06', label: 'Junio' },
      { value: '07', label: 'Julio' },
      { value: '08', label: 'Agosto' },
      { value: '09', label: 'Septiembre' },
      { value: '10', label: 'Octubre' },
      { value: '11', label: 'Noviembre' },
      { value: '12', label: 'Diciembre' },
    ];
    const days = Array.from({ length: 31 }, (_, i) => String(i + 1).padStart(2, '0'));

    const filteredLocations = computed(() => {
      const term = locationSearch.value.trim().toLowerCase();
      return locations.filter(prov => 
        prov.name.toLowerCase().includes(term)
      );
    });

    const showAllSuggestions = () => {
      showSuggestions.value = true;
      locationSearch.value = '';
    };

    const onLocationInput = () => {
      showSuggestions.value = true;
    };
    
    const selectLocation = (name) => {
      locationSearch.value = name;
      showSuggestions.value = false;
    };

    const handleClickOutside = (event) => {
      if (!autocompleteContainer.value?.contains(event.target)) {
        showSuggestions.value = false;
      }
    };

    onMounted(() => {
      document.addEventListener('click', handleClickOutside);
    });

    onBeforeUnmount(() => {
      document.removeEventListener('click', handleClickOutside);
    });

    const searchTournaments = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        router.push('/auth/login');
        return;
      }

      hasSearched.value = true;
      isLoading.value = true;
      try {
        const params = {
          location: locationSearch.value.trim(),
          game: filters.value.game,
        };
        
        // Manejo de fechas
        if (filters.value.month) {
          let dateFilter = `${new Date().getFullYear()}-${filters.value.month}`;
          if (filters.value.day) dateFilter += `-${filters.value.day}`;
          params.date = dateFilter;
        }

        const response = await axios.get(
          `${API_BASE}/api/tournaments/search`,
          { 
            params: Object.fromEntries(
              Object.entries(params).filter(([_, v]) => v !== '')
            ),
            headers: { Authorization: `Bearer ${token}` } 
          }
        );
        tournaments.value = Array.isArray(response.data)
          ? response.data
          : [];
      } catch (error) {
        console.error('Error al buscar torneos:', error);
        tournaments.value = [];
      } finally {
        isLoading.value = false;
      }
    };

    const formatDate = (iso) => {
      const d = new Date(iso);
      return d.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      });
    };

    const capitalize = (str) =>
      str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();

    return {
      filters,
      locationSearch,
      showSuggestions,
      filteredLocations,
      tournaments,
      isLoading,
      hasSearched,
      autocompleteContainer,
      months,
      days,
      searchTournaments,
      formatDate,
      capitalize,
      onLocationInput,
      selectLocation,
      showAllSuggestions,
    };
  },
};
</script>

<style scoped>
.tournament-app {
  --background: #ffffff;
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
  width: 90%;
  max-width: 600px;
  margin: 0 auto 1rem;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
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
  color: #000;
  padding: 0.5rem;
  cursor: pointer;
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

@media (max-width: 600px) {
  .filter-grid {
    grid-template-columns: 1fr;
  }
  .results-grid {
    grid-template-columns: 1fr;
  }
}
</style>