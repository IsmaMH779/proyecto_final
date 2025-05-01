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
            :src="`http://localhost:8081/images/profile/${profileImageUrl}`"
            alt="Store"
          />
        </div>
        <div class="store-info">
          <h2 class="store-name">
            {{ organizerName || 'Cargando nombre...' }}
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
          <!-- Header: título y botón de inscripción -->
          <div class="header-container">
            <h1 class="tournament-title">{{ tournament.name }}</h1>
            <div>
              <button
                v-if="isPlayer && !alreadyRegistered"
                @click="registerToTournament"
                class="register-btn tab-register-btn"
              >
                Inscribirme
              </button>
              <span
                v-else-if="isPlayer && alreadyRegistered"
                class="already-registered tab-already-registered"
              >
                ¡Ya estás inscrito!
              </span>
            </div>
          </div>

          <div class="tournament-details">
            <!-- Fecha y hora -->
            <div class="detail-item">
              <div class="detail-header">
                <strong>Fecha:</strong>
                <button
                  v-if="isOrganizer"
                  class="edit-button"
                  @click="openDateTimeEditor('date')"
                >
                  {{ isEditingDate ? 'Guardar' : 'Editar' }}
                </button>
              </div>
              <div class="detail-content">
                {{ tournament.startDate.split('T')[0] }}
                <div v-if="isEditingDate" class="edit-container">
                  <input
                    type="date"
                    v-model="editedDate"
                    class="date-input"
                  />
                </div>
              </div>
            </div>

            <div class="detail-item">
              <div class="detail-header">
                <strong>Horario:</strong>
                <button
                  v-if="isOrganizer"
                  class="edit-button"
                  @click="openDateTimeEditor('time')"
                >
                  {{ isEditingTime ? 'Guardar' : 'Editar' }}
                </button>
              </div>
              <div class="detail-content">
                {{ tournament.startDate.split('T')[1].substring(0,5) }}
                <div v-if="isEditingTime" class="edit-container">
                  <input
                    type="time"
                    v-model="editedTime"
                    class="time-input"
                  />
                </div>
              </div>
            </div>

            <div class="detail-item">
              <strong>Formato:</strong> {{ tournament.format }}
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
                  {{ playerNames[registration.playerId] || 'Cargando...' }}
                </p>
                <p class="player-date">
                  Inscrito el {{ registration.registrationDate.split('T')[0] }}
                </p>
              </div>

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

      <!-- Modal Confirmación para eliminar jugador -->
      <div v-if="showDeleteModal" class="modal-overlay">
        <div class="modal">
          <p>¿Seguro que quieres eliminar este jugador?</p>
          <div class="modal-actions">
            <button class="confirm-btn" @click="confirmDelete">Confirmar</button>
            <button class="cancel-btn" @click="cancelDelete">Cancelar</button>
          </div>
        </div>
      </div>

      <!-- Modal Confirmación para Fecha/Hora -->
      <div v-if="showDateTimeModal" class="modal-overlay">
        <div class="modal">
          <p>
            ¿Confirmar cambios en la
            {{ editingType === 'date' ? 'fecha' : 'hora' }} del torneo?
          </p>
          <div class="modal-actions">
            <button class="confirm-btn" @click="confirmDateTimeChange">Confirmar</button>
            <button class="cancel-btn" @click="cancelDateTimeChange">Cancelar</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = ref('info')
const tournament = ref({
  name: '',
  startDate: '',
  format: '',
  location: '',
  address: '',
  registrations: [],
  organizerId: null,
  entryFee: '',
  winnerPrize: ''
})
const isLoading = ref(true)
const playerNames = ref({})
const organizerName = ref('')
const profileImageUrl = ref('')
const isOrganizer = ref(false)
const isPlayer = ref(false)
const me = ref(null)

// Editores de fecha/hora
const isEditingDate = ref(false)
const isEditingTime = ref(false)
const editedDate = ref('')
const editedTime = ref('')
const showDateTimeModal = ref(false)
const editingType = ref(null)

// Eliminar jugador
const showDeleteModal = ref(false)
const registrationToDelete = ref(null)

// Computed: ¿ya inscrito?
const alreadyRegistered = computed(() => {
  if (!me.value || !tournament.value.registrations) return false
  return tournament.value.registrations.some(
    r => String(r.playerId) === String(me.value.id)
  )
})

// Fetch nombres
async function fetchPlayerName(playerId) {
  try {
    const { data } = await axios.get(
      `http://localhost:8081/api/players/${playerId}`,
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    )
    return data.username
  } catch {
    return '— error —'
  }
}
async function loadAllPlayerNames(regs) {
  const promises = regs.map(r =>
    fetchPlayerName(r.playerId).then(name => {
      playerNames.value[r.playerId] = name
    })
  )
  await Promise.all(promises)
}

// Fetch organizador
async function fetchOrganizerData(id) {
  try {
    const { data } = await axios.get(
      `http://localhost:8081/api/organizers/${id}`,
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    )
    profileImageUrl.value = data.imageUrl
    return data.username
  } catch {
    return '— error —'
  }
}

// Registrar jugador
async function registerToTournament() {
  try {
    await axios.post(
      `http://localhost:8082/api/tournaments/${tournament.value.id}/register`,
      {},
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    )
    const { data: refreshed } = await axios.get(
      `http://localhost:8082/api/tournaments/${tournament.value.id}`,
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    )
    tournament.value = refreshed
    if (refreshed.registrations.length) {
      await loadAllPlayerNames(refreshed.registrations)
    }
  } catch (e) {
    console.error('Error al inscribir:', e)
    alert('No se pudo inscribir. Puede que el torneo esté lleno o ya estés inscrito.')
  }
}

// Eliminar inscripción
function openDeleteModal(pid) {
  registrationToDelete.value = pid
  showDeleteModal.value = true
}

async function confirmDelete() {
  try {
    await axios.delete(
      `http://localhost:8082/api/tournaments/${tournament.value.id}/${registrationToDelete.value}`,
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    )
    // Filtrar por playerId para eliminar inmediatamente de la lista
    tournament.value.registrations = tournament.value.registrations.filter(
      r => String(r.playerId) !== String(registrationToDelete.value)
    )
    // Limpiar cache de nombre
    delete playerNames.value[registrationToDelete.value]
  } catch (e) {
    console.error('Error eliminando registro:', e)
  } finally {
    showDeleteModal.value = false
  }
}

function cancelDelete() {
  showDeleteModal.value = false
}

// Editar fecha/hora
function openDateTimeEditor(type) {
  if (type === 'date') {
    if (isEditingDate.value) {
      editingType.value = 'date'
      showDateTimeModal.value = true
    } else {
      editedDate.value = tournament.value.startDate.split('T')[0]
      isEditingDate.value = true
    }
  } else {
    if (isEditingTime.value) {
      editingType.value = 'time'
      showDateTimeModal.value = true
    } else {
      editedTime.value = tournament.value.startDate.split('T')[1].substring(0,5)
      isEditingTime.value = true
    }
  }
}
async function confirmDateTimeChange() {
  try {
    let newDate = tournament.value.startDate
    if (editingType.value === 'date') {
      newDate = `${editedDate.value}T${newDate.split('T')[1]}`
    } else {
      newDate = `${newDate.split('T')[0]}T${editedTime.value}:00`
    }
    await axios.put(
      `http://localhost:8082/api/tournaments/${tournament.value.id}`,
      { ...tournament.value, startDate: newDate },
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    )
    tournament.value.startDate = newDate
    isEditingDate.value = false
    isEditingTime.value = false
  } catch (e) {
    console.error('Error actualizando fecha/hora:', e)
  } finally {
    showDateTimeModal.value = false
  }
}
function cancelDateTimeChange() {
  isEditingDate.value = false
  isEditingTime.value = false
  showDateTimeModal.value = false
}

// Carga inicial
onMounted(async () => {
  try {
    const { data } = await axios.get(
      `http://localhost:8082/api/tournaments/${route.params.id}`,
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    )
    tournament.value = data
    if (data.registrations.length) {
      await loadAllPlayerNames(data.registrations)
    }
    if (data.organizerId) {
      organizerName.value = await fetchOrganizerData(data.organizerId)
    }

    let meData
    try {
      const res = await axios.get(
        `http://localhost:8081/api/players/me`,
        { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
      )
      meData = res.data
      isPlayer.value = true
    } catch {
      const res = await axios.get(
        `http://localhost:8081/api/organizers/me`,
        { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
      )
      meData = res.data
      isOrganizer.value = String(meData.id) === String(data.organizerId)
    }
    me.value = meData
  } catch (e) {
    console.error('Error cargando datos:', e)
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
/* General */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.content-container {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 1rem;
}

/* App Layout */
.tournament-app {
  padding: 2rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
}

/* Card */
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

/* Store Profile */
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

.store-prize {
  margin-top: 0.5rem;
  font-size: 0.9rem;
  color: #415a77;
}

/* Divider */
.divider {
  height: 1px;
  background-color: #d1d5db;
  margin: 0 1rem;
}

/* Tabs */
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

/* Header */
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 1rem;
}

.tournament-title {
  flex: 1;
  margin-right: 1rem;
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #1a2841;
}

.tab-register-btn,
.tab-already-registered {
  margin-top: 0;
}

/* Tournament Details */
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

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.detail-content {
  position: relative;
}

.edit-button {
  background: #3d5a80;
  color: white;
  border: none;
  border-radius: 0.25rem;
  padding: 0.25rem 0.5rem;
  font-size: 0.8rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.edit-button:hover {
  background: #2c3e50;
}

.edit-container {
  margin-top: 0.5rem;
}

.date-input,
.time-input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  color: #ffffff;
  background-color: #1a2841;
  border-radius: 0.25rem;
  font-size: 1rem;
  width: 100%;
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

  .content-container {
    padding: 0;
  }
}

/* Players List */
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
  transition: 0.2s;
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

/* Registration */
.register-btn {
  background: #3d5a80;
  color: #e0e1dd;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 0.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.register-btn:hover {
  background: #2c3e50;
}

.already-registered {
  color: #4a5568;
  font-style: italic;
}

</style>