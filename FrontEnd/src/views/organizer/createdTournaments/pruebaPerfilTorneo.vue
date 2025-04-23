<template>
    <div class="tournament-app">
      <!-- Header -->
      <div class="header">
        <p class="header-title">Información del torneo</p>
        <div class="user-info">
          <span class="username">USERNAME</span>
          <div class="user-avatar">
            <img src="https://v0.dev/placeholder.svg?height=32&width=32" alt="User" />
          </div>
        </div>
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
            <h1 class="tournament-title">{{ tournament.name }}</h1>
            <div class="tournament-details">
              <div class="details-column">
                <p class="detail-item"><strong>Fecha:</strong> {{ tournament.date }}</p>
                <p class="detail-item"><strong>Horario:</strong> {{ tournament.time }}</p>
                <p class="detail-item"><strong>Formato:</strong> {{ tournament.format }}</p>
                <p class="detail-item"><strong>Preinscripción:</strong> {{ tournament.fees.pre }} </p>
                <p class="detail-item"><strong>Precio día de torneo:</strong> {{ tournament.fees.onsite }}</p>
              </div>
              <div class="details-column">
                <p class="detail-item"><strong>Premio por participar:</strong> {{ tournament.prizes.participation }}</p>
                <p class="detail-item"><strong>Premio por ganar:</strong> {{ tournament.prizes.winner }}</p>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Players List Tab -->
        <div v-else class="tab-content">
          <div class="content-container">
            <h2 class="section-title">Jugadores inscritos</h2>
            <p>No hay jugadores inscritos todavía.</p>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  
  export default {
    setup() {
      const activeTab = ref('info');
      const tournament = ref({
        name: '[POKEMON] Store Tournament Off - Meta Event',
        date: 'Sábado 9 de Noviembre',
        time: '10:00 AM',
        format: 'Suizo',
        fees: {
          pre: '6€',
          onsite: '8€'
        },
        prizes: {
          participation: 'Sobre x1',
          winner: 'Booster Box x1'
        }
      });
  
      return {
        activeTab,
        tournament
      };
    }
  };
  </script>
  
  <style scoped>
  /* Reset */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  
  /* App Background */
  .tournament-app {
    background-color: #f9f5f0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
      Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  }
  
  /* Header */
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #e0e1dd;
    padding: 0 1.5rem;
    height: 60px;
    border-bottom: 2px solid #1a2841;
  }
  .header-title {
    color: #1b263b;
    font-size: 1.25rem;
    font-weight: 600;
  }
  .user-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }
  .username {
    color: #1b263b;
    font-size: 0.9rem;
  }
  .user-avatar img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: 1px solid #1a2841;
  }
  
  /* Store Profile */
  .store-profile {
    display: flex;
    align-items: center;
    gap: 1rem;
    background-color: #f9f5f0;
    padding: 1rem 1.5rem;
    border-bottom: 2px solid #1a2841;
  }
  .store-avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    overflow: hidden;
    border: 2px solid #1a2841;
    background-color: #e0e1dd;
  }
  .store-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .store-name {
    color: #1a2841;
    font-size: 1.25rem;
    font-weight: 700;
  }
  .store-location {
    color: #666;
    font-size: 0.9rem;
  }
  
  /* Tabs */
  .tabs-container {
    margin-top: 1rem;
  }
  .tabs-header {
    display: flex;
    border-bottom: 1px solid #1a2841;
    background-color: #e0e1dd;
  }
  .tab-button {
    flex: 1;
    padding: 0.75rem 0;
    font-size: 0.95rem;
    background: none;
    border: none;
    color: #1b263b;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  .tab-button.active {
    background-color: #f9f5f0;
    border-top-left-radius: 8px;
    border-top-right-radius: 8px;
    border-bottom: 3px solid #1a2841;
  }
  .tab-button:not(.active):hover {
    background-color: #e0e1dd;
  }
  
  /* Tab Content */
  .tab-content {
    background-color: #7192aa;
    color: #f9f5f0;
    padding: 1.5rem;
    border-radius: 0 0 8px 8px;
  }
  .content-container {
    max-width: 800px;
    margin: 0 auto;
  }
  .tournament-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin-bottom: 1.5rem;
  }
  
  /* Details Grid */
  .tournament-details {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 1.5rem;
  }
  .details-column .detail-item {
    font-size: 1rem;
    margin-bottom: 0.75rem;
  }
  .details-column .detail-item strong {
    color: #e0e1dd;
  }
  
  /* Section Title (players) */
  .section-title {
    font-size: 1.25rem;
    font-weight: 700;
    margin-bottom: 1rem;
  }
  </style>
  