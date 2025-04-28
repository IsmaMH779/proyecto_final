<template>
  <div class="tournament-app">
    <!-- Loader -->
    <div v-if="isLoading" class="loader-container">
      <div class="loader"></div>
    </div>

    <!-- Contenido principal -->
    <div v-else class="content-container">
      <!-- Perfil de la tienda -->
      <div class="card store-profile">
        <div class="store-avatar">
          <img
            :src="'http://localhost:8081/images/profile/' + profileImageUrl" 
            alt="Store"
          />
        </div>
        <div class="store-info">
          <h2 class="store-name">
            {{ organizerName || "Cargando nombre..." }}
          </h2>
          <p class="store-location">
            {{ `${tournament.location}, ${tournament.address}` }}
          </p>
        </div>
      </div>

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
              {{ tournament.startDate.split("T")[0] }}
            </div>
            <div class="detail-item">
              <strong>Horario:</strong>
              {{ tournament.startDate.split("T")[1] }}
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

          <p v-if="tournament.registrations.length === 0">
            No hay jugadores inscritos todavía.
          </p>

          <div v-else class="players-list">
            <div
              v-for="registration in tournament.registrations"
              :key="registration.id"
              class="player-card"
            >
              <div class="player-info">
                <p class="player-name">
                  {{ playerNames[registration.playerId] || "Cargando..." }}
                </p>
                <p class="player-date">
                  Inscrito el {{ registration.registrationDate.split("T")[0] }}
                </p>
              </div>

              <!-- Botón de borrar solo si es organizador -->
              <button
                v-if="isOrganizer"
                class="delete-button"
                @click="openDeleteModal(registration.playerId)"
              >
                X
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Confirmación -->
    <div v-if="showDeleteModal" class="modal-overlay">
      <div class="modal">
        <p>¿Seguro que quieres eliminar este jugador?</p>
        <div class="modal-actions">
          <button class="confirm-btn" @click="confirmDelete">Confirmar</button>
          <button class="cancel-btn" @click="cancelDelete">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRoute } from "vue-router";

const activeTab = ref("info");
const tournament = ref({
  name: "",
  startDate: "",
  format: "",
  location: "",
  address: "",
  registrations: [],
  organizerId: null,
});
const isLoading = ref(true);
const playerNames = ref({});
const organizerName = ref("");
const profileImageUrl = ref();
const route = useRoute();
const isOrganizer = ref(false);

const showDeleteModal = ref(false);
const registrationToDelete = ref(null);

// obtiene username de jugador
async function fetchPlayerName(playerId) {
  try {
    const { data } = await axios.get(
      `http://localhost:8081/api/players/${playerId}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
    return data.username;
  } catch {
    return "— error —";
  }
}

// obtiene info del organizador
async function fetchOrganizerData(organizerId) {
  try {
    const { data } = await axios.get(
      `http://localhost:8081/api/organizers/${organizerId}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
    profileImageUrl.value = data.imageUrl;
    return data.username;
  } catch {
    return "— error —";
  }
}

// carga nombres de todos los jugadores
async function loadAllPlayerNames(registrations) {
  const promises = registrations.map(async (r) => {
    const name = await fetchPlayerName(r.playerId);
    playerNames.value[r.playerId] = name;
  });
  await Promise.all(promises);
}

// abre modal de confirmacion
function openDeleteModal(registrationId) {
  registrationToDelete.value = registrationId;
  showDeleteModal.value = true;
}

// confirma y borra jugador
async function confirmDelete() {
  try {
    await axios.delete(
      `http://localhost:8082/api/tournaments/${tournament.value.id}/${registrationToDelete.value}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
    // refresca la lista de jugadores
    tournament.value.registrations = tournament.value.registrations.filter(
      (r) => r.id !== registrationToDelete.value
    );
  } catch (e) {
    console.error("Error eliminando registro:", e);
  } finally {
    showDeleteModal.value = false;
  }
}

// cancelar borrado
function cancelDelete() {
  registrationToDelete.value = null;
  showDeleteModal.value = false;
}

onMounted(async () => {
  try {
    const { data: tournamentData } = await axios.get(
      `http://localhost:8082/api/tournaments/${route.params.id}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
    tournament.value = tournamentData;

    if (tournamentData.registrations?.length) {
      await loadAllPlayerNames(tournamentData.registrations);
    }

    if (tournamentData.organizerId) {
      organizerName.value = await fetchOrganizerData(tournamentData.organizerId);
    }

    // verificar si el usuario logeado es el organizador
    const { data: me } = await axios.get(
      `http://localhost:8081/api/organizers/me`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );
  
    isOrganizer.value = me.id == tournamentData.organizerId;
    console.log(isOrganizer.value)
  } catch (e) {
    console.error("Error cargando datos:", e);
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
/* contenido original respetado */
.content-container {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 1rem;
}
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.tournament-app {
  padding: 2rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
}
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
.divider {
  height: 1px;
  background-color: #d1d5db;
  margin: 0 1rem;
}
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
.tab-content {
  color: #1a2841 !important;
  background-color: #e0e1dd;
  padding: 1rem;
}
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
  .content-container {
    padding: 0;
  }
}

/* Estilos solo para el listado de jugadores */
.players-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.player-card {
  background: #ffffff;
  border: 1px solid #d1d5db;
  padding: 1rem;
  border-radius: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 6px rgba(26, 40, 65, 0.05);
}
.player-info {
  display: flex;
  flex-direction: column;
}
.player-name {
  font-weight: 600;
  font-size: 1.1rem;
}
.player-date {
  font-size: 0.9rem;
  color: #555;
}
.delete-button {
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  font-size: 1.25rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:  0.2s;
}
.delete-button:hover {
  background: #dc2626;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: #ffffff;
  padding: 2rem;
  border-radius: 1rem;
  text-align: center;
}
.modal-actions {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
}
.confirm-btn {
  background: #3d5a80;
  color: #fff;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  cursor: pointer;
}
.cancel-btn {
  background: #d1d5db;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 0.5rem;
  cursor: pointer;
}
</style>