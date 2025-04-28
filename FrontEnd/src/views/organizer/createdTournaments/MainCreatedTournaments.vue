<template>
  <ion-page>
    <ion-content :scroll-y="true">
      <div class="tournaments-app">
        <div class="content-container">
          <!-- Header de la sección -->
          <div class="section-header">
            <h1 class="section-title">Mis Torneos</h1>
            <div class="filter-badge">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z"></path>
              </svg>
              <span>Pendientes de activación</span>
            </div>
          </div>
          
          <!-- Contenido principal -->
          <div v-if="filteredTournaments.length === 0" class="empty-state">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M6 2h12a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2Z"></path>
              <path d="M8 2v20"></path>
              <path d="M16 2v20"></path>
              <path d="M2 12h4"></path>
              <path d="M18 12h4"></path>
              <path d="M10 12h4"></path>
            </svg>
            <p>No hay torneos pendientes de activación</p>
            <p class="empty-state-subtext">Todos tus torneos están activos o cerrados</p>
          </div>
          
          <div v-else class="tournaments-grid">
            <div 
              v-for="tournament in filteredTournaments" 
              :key="tournament.id"
              class="tournament-card"
              @click="showTournamentInfo(tournament)"
            >
              <div class="tournament-header">
                <div class="tournament-game-badge">[{{ tournament.game }}]</div>
                <h2 class="tournament-name">{{ tournament.name }}</h2>
              </div>
              
              <div class="tournament-details">
                <div class="detail-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                  </svg>
                  <span>{{ formatDate(tournament.startDate) }}</span>
                </div>
                
                <div class="detail-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                  </svg>
                  <span>{{ formatTime(tournament.startDate) }}</span>
                </div>
                
                <div class="detail-item format-badge">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M2 20h.01"></path>
                    <path d="M7 20v-4"></path>
                    <path d="M12 20v-8"></path>
                    <path d="M17 20v-6"></path>
                    <path d="M22 20V8"></path>
                  </svg>
                  <span>{{ tournament.format }}</span>
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
import { IonPage, IonContent } from "@ionic/vue";
import axios from "axios";

export default {
  components: {
    IonPage,
    IonContent
  },
  data() {
    return {
      tournaments: [],
    };
  },
  computed: {
    // Filtrar por torneo activo false y torneo cerrado true
    filteredTournaments() {
      return this.tournaments.filter((tournament) => {
        // Filtrar torneos con isActive: false e isClosed: false
        if (!tournament.isActive && !tournament.isClosed) {
          // Modificar el formato si es "Direct_elimination"
          if (tournament.format === "Direct_elimination") {
            tournament.format = "Eliminacion directa";
          }
          return true; // Solo devolver torneos que cumplen con la condición
        }
        return false;
      });
    },
  },
  methods: {
    showTournamentInfo(tournament) {
      this.$router.push(`/web/organizer-tournament-profile/${tournament.id}`);
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = dateString.split('T')[0];
      // Formatear la fecha si es necesario
      return date;
    },
    formatTime(dateString) {
      if (!dateString) return '';
      const time = dateString.split('T')[1];
      // Formatear la hora si es necesario
      return time;
    }
  },
  mounted() {
    axios
      .get("http://localhost:8082/api/tournaments/organizer", {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      })
      .then((response) => {
        this.tournaments = response.data;
      })
      .catch((error) => {
        console.error("Error al obtener datos:", error);
      });
  },
};
</script>

<style scoped>
/* Contenedor principal */
.tournaments-app {
  background-color: #F5EFE7;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
  padding: 1rem 0;
  min-height: 100%;
}

/* Contenedor con ancho máximo */
.content-container {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 1rem;
}

/* Header de la sección */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a2841;
  margin: 0;
}

.filter-badge {
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

/* Grid de torneos */
.tournaments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

/* Tarjeta de torneo */
.tournament-card {
  background-color: #e0e1dd;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  cursor: pointer;
  border: 2px solid transparent;
}

.tournament-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(26, 40, 65, 0.15);
  border-color: #415a77;
}

.tournament-header {
  padding: 1.25rem;
  border-bottom: 2px solid #415a77;
}

.tournament-game-badge {
  display: inline-block;
  font-weight: 600;
  color: #3d5a80;
  margin-bottom: 0.25rem;
}

.tournament-name {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a2841;
  margin: 0;
  line-height: 1.4;
}

.tournament-details {
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #415a77;
}

.detail-item svg {
  flex-shrink: 0;
}

.format-badge {
  margin-top: 0.5rem;
  padding-top: 0.5rem;
  border-top: 1px solid rgba(65, 90, 119, 0.2);
  font-weight: 600;
}

/* Estado vacío */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 1rem;
  color: #415a77;
  text-align: center;
  background-color: #e0e1dd;
  border-radius: 1rem;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
}

.empty-state svg {
  color: #3d5a80;
  opacity: 0.5;
  margin-bottom: 1rem;
}

.empty-state p {
  font-size: 1.125rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.empty-state-subtext {
  font-size: 0.875rem;
  opacity: 0.8;
}

/* Responsive */
@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .tournaments-grid {
    grid-template-columns: 1fr;
  }
}
</style>