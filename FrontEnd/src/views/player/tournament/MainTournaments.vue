<template>
    <ion-page>
      <ion-content :scroll-y="true">
        <div class="tournaments-app">
          <div class="content-container">
            <!-- Sección de Torneos Activos -->
            <div class="section-header">
              <h1 class="section-title">Torneos Activos</h1>
            </div>
            
            <div v-if="activeTournaments.length === 0" class="empty-state">
              <ion-icon :icon="trophyOutline" class="empty-icon"></ion-icon>
              <p>No tienes torneos activos actualmente</p>
            </div>
            
            <div v-else class="tournaments-grid">
              <div 
                v-for="tournament in activeTournaments" 
                :key="tournament.id"
                class="tournament-card active"
                @click="navigateToLiveTournament(tournament.id)"
              >
                <div class="tournament-header">
                    <div class="tournament-game-badge">[{{ tournament.game }}]</div>
                    <h2 class="tournament-name">{{ tournament.name }}</h2>
                    <span class="active-badge">ACTIVO</span>
                </div>
                
                <div class="tournament-details">
                  <div class="detail-item">
                    <ion-icon :icon="calendarOutline"></ion-icon>
                    <span>{{ formatDate(tournament.startDate) }}</span>
                  </div>
                  
                  <div class="detail-item">
                    <ion-icon :icon="peopleOutline"></ion-icon>
                    <span>{{ tournament.format }}</span>
                  </div>
                  
                  <div class="detail-item">
                    <ion-icon :icon="locationOutline"></ion-icon>
                    <span>{{ tournament.location }}</span>
                  </div>
                </div>
              </div>
            </div>
  
            <!-- Separador -->
            <div class="section-divider">
              <span>Tus torneos inscritos</span>
            </div>
  
            <!-- Sección de Todos los Torneos -->
            <div v-if="loading" class="loader-container">
              <div class="loader"></div>
            </div>
            
            <div v-else>
              <div v-if="tournaments.length === 0" class="empty-state">
                <ion-icon :icon="alertCircleOutline"></ion-icon>
                <p>No estás inscrito en ningún torneo</p>
                <ion-button fill="clear" @click="navigateToSearch">
                  Buscar torneos
                  <ion-icon slot="end" :icon="searchOutline"></ion-icon>
                </ion-button>
              </div>
              
              <div v-else class="tournaments-grid">
                <div 
                  v-for="tournament in nonActiveTournaments" 
                  :key="tournament.id"
                  class="tournament-card"
                  @click="navigateToTournament(tournament.id)"
                >
                  <div class="tournament-header">
                    <div class="tournament-game-badge">[{{ tournament.game }}]</div>
                    <h2 class="tournament-name">{{ tournament.name }}</h2>
                    <span v-if="tournament.closed" class="closed-badge">CERRADO</span>
                  </div>
                  
                  <div class="tournament-details">
                    <div class="detail-item">
                      <ion-icon :icon="calendarOutline"></ion-icon>
                      <span>{{ formatDate(tournament.startDate) }}</span>
                    </div>
                    
                    <div class="detail-item">
                      <ion-icon :icon="peopleOutline"></ion-icon>
                      <span>{{ formatType(tournament.format) }}</span>
                    </div>
                    
                    <div class="detail-item">
                      <ion-icon :icon="locationOutline"></ion-icon>
                      <span>{{ tournament.location }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </ion-content>
    </ion-page>
  </template>
  
  <script setup>
  import { IonPage, IonContent, IonButton, IonIcon } from '@ionic/vue';
  import { ref, computed, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  import { 
    trophyOutline,
    alertCircleOutline,
    searchOutline,
    calendarOutline,
    peopleOutline,
    locationOutline
  } from 'ionicons/icons';
  
  const API_BASE = import.meta.env.VITE_TOURNAMENT_API || 'http://localhost:8082';
  const router = useRouter();
  
  const tournaments = ref([]);
  const loading = ref(true);


  
  // Obtener torneos del jugador
  const fetchTournaments = async () => {
    try {
      const token = localStorage.getItem('token');
      const { data } = await axios.get(`${API_BASE}/api/tournaments/player`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      tournaments.value = data;
    } catch (error) {
      console.error('Error fetching tournaments:', error);
    } finally {
      loading.value = false;
    }
  };
  
  // Filtrar torneos activos
  const activeTournaments = computed(() => 
    tournaments.value.filter(t => t.active && !t.closed)
  );

  // Torneos NO activos
const nonActiveTournaments = computed(() => 
  tournaments.value.filter(t => !(t.active && !t.closed))
);
  
  // Formatear fecha
  const formatDate = (dateString) => {
    const options = { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric', 
      hour: '2-digit', 
      minute: '2-digit' 
    };
    return new Date(dateString).toLocaleDateString('es-ES', options);
  };
  
  // Formatear tipo de torneo
  const formatType = (format) => {
    const types = {
      'Direct_elimination': 'Eliminación directa',
      'Round_robin': 'Liga',
      'Swiss_system': 'Sistema suizo'
    };
    return types[format] || format;
  };
  
  // Navegación
  const navigateToTournament = (id) => {
    router.push(`/web/player-tournament-profile/${id}`);
  };

  const navigateToLiveTournament = (id) => router.push(`/web/player-liveTournament/${id}`)
  
  const navigateToSearch = () => {
    router.push('/web/tournament-search');
  };
  
  onMounted(fetchTournaments);
  </script>
  
  <style scoped>
  /* Estilos base */
  .tournaments-app {
    background-color: #F5EFE7;
    min-height: 100%;
  }
  
  .content-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 1rem;
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
  
  .filter-badge {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: rgba(61, 90, 128, 0.1);
    padding: 0.5rem 1rem;
    border-radius: 2rem;
    color: #3d5a80;
    font-weight: 500;
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
    background: #e0e1dd;
    border-radius: 1rem;
    padding: 1.25rem;
    box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
    transition: transform 0.2s ease;
    cursor: pointer;
  }
  
  .tournament-card:hover {
    transform: translateY(-3px);
  }
  
  .tournament-card.active {
    border-left: 6px solid #3d5a80;
  }
  
  .tournament-header {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1rem;
  position: relative;
  padding-right: 80px; /* Espacio para el badge */
}

.tournament-game-badge {
  font-size: 1.1rem;
  font-weight: 700;
  color: #3d5a80;
  background: rgba(61, 90, 128, 0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 1rem;
  align-self: flex-start;
  margin-bottom: 0.5rem;
}

.tournament-name {
  font-size: 1.5rem;
  color: #1a2841;
  margin: 0;
  line-height: 1.3;
  font-weight: 600;
}

.active-badge,
.closed-badge {
  position: absolute;
  top: 0;
  right: 0;
  padding: 0.25rem 1rem;
  border-radius: 1rem;
  font-size: 0.85rem;
  font-weight: 700;
  color: #1a2841;
}
  
  /* Detalles del torneo */
  .tournament-details {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .detail-item {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    color: #415a77;
    font-size: 0.9rem;
  }
  
  .detail-item ion-icon {
    font-size: 1.1rem;
    color: #3d5a80;
  }
  
  /* Estados vacíos */
  .empty-state {
    text-align: center;
    padding: 2rem;
    background: #e0e1dd;
    border-radius: 1rem;
    margin: 2rem 0;
  }

  .empty-state p {
  color: #3d5a80; /* Azul más intenso */
  font-weight: 600; /* Texto más grueso */
  }
  
  .empty-state ion-icon {
    font-size: 3rem;
    color: #3d5a80;
    margin-bottom: 1rem;
  }
  
  /* Separador */
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
    left: 0;
    right: 0;
    height: 2px;
    background: #e0e1dd;
    z-index: 0;
  }
  
  /* Loader */
  .loader-container {
    text-align: center;
    padding: 3rem;
  }
  
  .loader {
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3d5a80;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    animation: spin 1s linear infinite;
    margin: 0 auto;
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  
  /* Responsive */
  @media (max-width: 768px) {
    .section-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 1rem;
    }
    
    .tournaments-grid {
      grid-template-columns: 1fr;
    }
  }
  </style>