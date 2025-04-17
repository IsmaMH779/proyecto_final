<template>
    <div class="tournament-app">
      <!-- Header -->
      <div class="header">
        <p class="header-title">Torneos</p>
      </div>
  
      <!-- Store Profile -->
      <div class="store-profile">
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
  
      <div class="divider"></div>
  
      <!-- Tabs -->
      <div class="tabs-container">
        <div class="tabs-header">
          <button
            @click="activeTab = 'info'"
            :class="['tab-button', { active: activeTab === 'info' }]"
          >
            Información del torneo
          </button>
          <button
            @click="activeTab = 'players'"
            :class="['tab-button', { active: activeTab === 'players' }]"
          >
            Lista de jugadores
          </button>
        </div>
  
        <!-- Tournament Info Tab -->
        <div v-if="activeTab === 'info'" class="tab-content">
          <div class="content-container">
            <h1 class="tournament-title">
              {{ tournament.name }}
            </h1>
  
            <div class="tournament-details">
              <div class="details-column">
                <p class="detail-item">Fecha: {{ tournament.startDate.split("T")[0] }}</p>
                <p class="detail-item">Horario: {{ tournament.startDate.split("T")[1] }}</p>
                <p class="detail-item">Formato: {{ tournament.format }}</p>
              </div>
  
              <div class="details-column">
                <p class="detail-item">Premio por participar: Sobre x1</p>
                <p class="detail-item">Premio por ganar: Booster Box x1</p>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Players List Tab -->
        <div v-if="activeTab === 'players'" class="tab-content">
          <div class="content-container">
            <h2 class="section-title">Jugadores inscritos</h2>
            <p>No hay jugadores inscritos todavía.</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute } from 'vue-router';

export default {
  setup() {
    const activeTab = ref("info");
    const tournament = ref();
    const route = useRoute();  // Obtener la instancia de la ruta

    // Llamada a la API para obtener el torneo
    onMounted(() => {
      const id = route.params.id;

      axios.get(`http://localhost:8082/api/tournaments/${id}`, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        })
        .then((response) => {
          tournament.value = response.data;
          console.log(tournament.value)
        })
        .catch((error) => {
          console.error("Error al obtener datos:", error);
        });
    });

    return {
      activeTab,
      tournament,
    };
  },
};
</script>
  
  <style scoped>
  .header {
    background-color: #e0e1dd;
    height: 123px;
    box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
    color: black;
    display: flex;
    align-items: center;
    padding-left: 30px;
  }
  
  .header-title {
    color: #1b263b;
    font-size: 30px;
    width: max-content;
  }
  
  /* Estilos base */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  
  body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
      Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
    background-color: #f9f5f0;
    color: #333;
    line-height: 1.5;
  }
  
  /* Estructura principal */
  .tournament-app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }
  
  /* Store Profile */
  .store-profile {
    padding: 1rem;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .store-avatar {
    height: 5rem;
    width: 5rem;
    border-radius: 9999px;
    background-color: #e0e1dd;
    border: 2px solid #1a2841;
    overflow: hidden;
  }
  
  .store-avatar img {
    height: 100%;
    width: 100%;
    object-fit: cover;
  }
  
  .store-info {
    display: flex;
    flex-direction: column;
  }
  
  .store-name {
    font-size: 1.25rem;
    font-weight: 700;
    color: #1a2841;
  }
  
  .store-location {
    color: #666;
  }
  
  /* Divider */
  .divider {
    height: 1px;
    background-color: #d1d5db;
    margin: 0 1rem;
  }
  
  /* Tabs */
  .tabs-container {
    width: 100%;
  }
  
  .tabs-header {
    display: flex;
    width: 100%;
    background-color: #1a2841;
    color: #ffffff;
  }
  
  .tab-button {
    flex: 1;
    padding: 0.75rem 0;
    font-size: 1rem;
    background: none;
    border: none;
    color: #ffffff;
    cursor: pointer;
  }
  
  .tab-button.active {
    background-color: #3d5a80;
  }
  
  .tab-content {
    background-color: #3d5a80;
    color: #ffffff;
    padding: 1.5rem;
    min-height: 60vh;
  }
  
  .content-container {
    max-width: 56rem;
    margin: 0 auto;
  }
  
  .tournament-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin-bottom: 2rem;
  }
  
  .tournament-details {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .detail-item {
    font-size: 1.25rem;
    margin-bottom: 1rem;
  }
  
  .pricing-info {
    margin-top: 1rem;
  }
  
  .section-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin-bottom: 1.5rem;
  }
  </style>
  