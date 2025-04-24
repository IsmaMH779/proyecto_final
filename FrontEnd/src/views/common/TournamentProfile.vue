<template>
  <div class="tournament-app">
    <!-- Loader -->
    <div v-if="isLoading" class="loader-container">
      <div class="loader"></div>
    </div>

    <!-- Contenido principal -->
    <div v-else>
      <!-- Perfil de la tienda -->
      <div class="card store-profile">
        <div class="store-avatar">
          <img
            src="https://v0.dev/placeholder.svg?height=80&width=80"
            alt="Store"
          />
        </div>
        <div class="store-info">
          <h2 class="store-name">Nombre de la tienda</h2>
          <p class="store-location">Localidad (calle, número, etc)</p>
        </div>
      </div>

      <!-- Separador -->
      <div class="divider"></div>

      <!-- Pestañas -->
      <div class="tabs-container">
        <div class="tabs-header">
          <button
            @click="activeTab = 'info'"
            :class="['tab-button', { active: activeTab === 'info' }]"
          >
            Información
          </button>
          <button
            @click="activeTab = 'players'"
            :class="['tab-button', { active: activeTab === 'players' }]"
          >
            Jugadores
          </button>
        </div>

        <!-- Tab Información -->
        <div v-show="activeTab === 'info'" class="card tab-content">
          <h1 class="tournament-title">{{ tournament.name }}</h1>
          <div class="tournament-details">
            <div class="detail-item">
              <strong>Fecha:</strong>
              {{ tournament.startDate.split('T')[0] }}
            </div>
            <div class="detail-item">
              <strong>Horario:</strong>
              {{ tournament.startDate.split('T')[1] }}
            </div>
            <div class="detail-item">
              <strong>Formato:</strong> {{ tournament.format }}
            </div>
            <div class="detail-item">
              <strong>Premio inscripción:</strong> Sobre x1
            </div>
            <div class="detail-item">
              <strong>Premio ganador:</strong> Booster Box x1
            </div>
          </div>
        </div>

        <!-- Tab Jugadores -->
        <div v-show="activeTab === 'players'" class="card tab-content">
          <h2 class="section-title">Jugadores inscritos</h2>
          <p>No hay jugadores inscritos todavía.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

const activeTab = ref('info')
const tournament = ref({ name: '', startDate: '', format: '' })
const isLoading = ref(true)

const route = useRoute()

onMounted(() => {
  const id = route.params.id
  axios
    .get(`http://localhost:8082/api/tournaments/${id}`, {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    })
    .then((response) => {
      tournament.value = response.data
    })
    .catch((error) => {
      console.error('Error al obtener datos:', error)
    })
    .finally(() => {
      isLoading.value = false
    })
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
.tournament-app {
  min-height: 100vh;
  padding: 2rem 1rem;
  background-color: #f9f5f0;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
}

/* Componente tarjeta genérico */
.card {
  background-color: #e0e1dd;
  border-radius: 1rem;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  padding: 1.5rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(26, 40, 65, 0.15);
}

/* Loader */
.loader-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
.loader {
  border: 8px solid #eee;
  border-top: 8px solid #1a2841;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Perfil de la tienda */
.store-profile {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.store-avatar {
  flex-shrink: 0;
  width: 5rem;
  height: 5rem;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #1a2841;
}
.store-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.store-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
.store-name {
  font-size: 1.5rem;
  font-weight: 700;
}
.store-location {
  font-size: 0.9rem;
  color: #415a77;
}

/* Separador */
.divider {
  height: 1px;
  background-color: #d1d5db;
  margin: 0 1rem;
}

/* Pestañas */
.tabs-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.tabs-header {
  display: flex;
  border-radius: 1rem;
  overflow: hidden;
  background-color: #1a2841;
}
.tab-button {
  flex: 1;
  padding: 0.75rem;
  font-size: 1rem;
  font-weight: 600;
  color: #e0e1dd;
  background: none;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.tab-button:not(.active):hover {
  background-color: #3d5a80;
}
.tab-button.active {
  background-color: #3d5a80;
}

/* Contenido de pestañas */
.tab-content {
  /* Asegura que el texto sea siempre oscuro */
  color: #1a2841 !important;
  background-color: transparent;
  padding: 1rem;
}

/* Títulos */
.tournament-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #1a2841;
}
.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #1a2841;
}

/* Detalles del torneo en grid */
.tournament-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
}
.detail-item {
  background: #fff;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  box-shadow: 0 2px 6px rgba(26, 40, 65, 0.08);
  font-size: 1rem;
}

/* Responsive */
@media (max-width: 600px) {
  .tournament-app {
    padding: 1rem;
  }
  .card {
    padding: 1rem;
  }
  .store-profile {
    flex-direction: column;
    align-items: center;
  }
  .tournament-details {
    grid-template-columns: 1fr;
  }
}
</style>
