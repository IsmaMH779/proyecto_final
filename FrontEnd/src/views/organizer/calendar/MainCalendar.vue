<template>
  <ion-page>
    <ion-content>
      <div class="calendar-container">
        <div class="calendar-layout">
          <!-- Calendario -->
          <div class="section calendar-section">
            <div class="section-header">
              <h2>Calendario de Torneos</h2>
            </div>
            <div class="fixed-calendar qalendar-is-small" ref="calendarRef">
              <Qalendar
                :selected-date="selectedDate"
                :events="events"
                :config="config"
                @period-change="handlePeriodChange"
                @click-event="handleEventClick"
                @click-date="handleDateClick"
              />
            </div>
          </div>

          <!-- Panel eventos -->
          <div class="section events-section">
            <div class="section-header">
              <h2>Mis Eventos</h2>
              <button class="add-event-btn" @click="openCreateEventModal">
                <ion-icon name="calendar-outline" class="btn-icon"></ion-icon>
                Crear Evento
              </button>
            </div>

            <div class="events-list-container">
              <div v-if="userEvents.length > 0" class="events-list">
                <div
                  v-for="event in userEvents"
                  :key="event.id"
                  class="event-card"
                >
                  <div class="event-date">
                    <div class="event-day">{{ formatDay(event.date) }}</div>
                    <div class="event-month">{{ formatMonth(event.date) }}</div>
                  </div>
                  <div class="event-details">
                    <h3 class="event-title">
                      <ion-icon name="trophy-outline" class="event-icon"></ion-icon>
                      {{ event.title }}
                    </h3>
                    <div class="event-info">
                      <div class="event-time">
                        <ion-icon name="time-outline" class="event-icon"></ion-icon>
                        {{ event.time }}
                      </div>
                      <div class="event-description" v-if="event.description">
                        <ion-icon name="document-text-outline" class="event-icon"></ion-icon>
                        {{ event.description }}
                      </div>
                    </div>
                  </div>
                  <div class="event-actions">
                    <button class="event-action-btn edit-btn" @click="editEvent(event)">
                      <ion-icon name="create-outline"></ion-icon>
                      Editar
                    </button>
                    <button class="event-action-btn delete-btn" @click="confirmDeleteEvent(event.id)">
                      <ion-icon name="trash-outline"></ion-icon>
                      Eliminar
                    </button>
                  </div>
                </div>
              </div>
              <div v-else class="no-events-message">
                <ion-icon name="calendar-outline" class="empty-icon"></ion-icon>
                <p>No has creado ningún evento</p>
                <p class="empty-subtitle">Haz clic en "Crear Evento" para añadir uno nuevo</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ion-content>

    <!-- Modal ver detalles -->
    <div class="modal-overlay" v-if="showEventModal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedEvent.title }}</h3>
          <button class="close-button" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="modal-detail">
            <ion-icon name="calendar-outline" class="modal-icon"></ion-icon>
            <span class="modal-text">{{ formatDate(selectedEvent.time?.start) }}</span>
          </div>
          <div class="modal-detail">
            <ion-icon name="time-outline" class="modal-icon"></ion-icon>
            <span class="modal-text">{{ formatEventTime(selectedEvent.time?.start) }}</span>
          </div>
          <div class="modal-detail" v-if="selectedEvent.description">
            <ion-icon name="document-text-outline" class="modal-icon"></ion-icon>
            <span class="modal-text">{{ selectedEvent.description }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="action-button">Ver detalles</button>
        </div>
      </div>
    </div>

    <!-- Modal crear/editar evento -->
    <div class="modal-overlay" v-if="showCreateModal" @click="closeCreateModal">
      <div class="modal-content create-modal" @click.stop>
        <div class="modal-header">
          <h3>{{ newEvent.id ? 'Editar Evento' : 'Crear Nuevo Evento' }}</h3>
          <button class="close-button" @click="closeCreateModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveEvent" class="create-event-form">
            <div class="form-group">
              <label for="event-title">
                <ion-icon name="trophy-outline" class="form-icon"></ion-icon>
                Título
              </label>
              <input id="event-title" type="text" v-model="newEvent.title" required />
            </div>
            <div class="form-group">
              <label for="event-date">
                <ion-icon name="calendar-outline" class="form-icon"></ion-icon>
                Fecha
              </label>
              <input id="event-date" type="date" v-model="newEvent.date" required />
            </div>
            <div class="form-group">
              <label for="event-time">
                <ion-icon name="time-outline" class="form-icon"></ion-icon>
                Hora
              </label>
              <input id="event-time" type="time" v-model="newEvent.time" required />
            </div>
            <div class="form-group">
              <label for="event-description">
                <ion-icon name="document-text-outline" class="form-icon"></ion-icon>
                Descripción
              </label>
              <textarea id="event-description" v-model="newEvent.description" rows="3"></textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-button" @click="closeCreateModal">Cancelar</button>
              <button type="submit" class="submit-button">{{ newEvent.id ? 'Actualizar' : 'Crear' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Modal de confirmación para eliminar -->
    <div class="modal-overlay" v-if="showDeleteModal" @click="cancelDelete">
      <div class="modal-content delete-modal" @click.stop>
        <div class="modal-header delete-header">
          <h3>Confirmar eliminación</h3>
          <button class="close-button" @click="cancelDelete">×</button>
        </div>
        <div class="modal-body">
          <div class="delete-confirmation">
            <ion-icon name="warning-outline" class="warning-icon"></ion-icon>
            <p class="delete-text">¿Estás seguro de que deseas eliminar este evento?</p>
            <p class="delete-subtext">Esta acción no se puede deshacer.</p>
          </div>
        </div>
        <div class="modal-footer delete-footer">
          <button class="cancel-button" @click="cancelDelete">Cancelar</button>
          <button class="delete-confirm-btn" @click="proceedWithDelete">Eliminar</button>
        </div>
      </div>
    </div>
  </ion-page>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onBeforeUnmount } from 'vue';
import { IonPage, IonContent, IonIcon } from '@ionic/vue';
import { Qalendar } from 'qalendar';
import 'qalendar/dist/style.css';
import axios from 'axios';
import { 
  addOutline,
  calendarOutline,
  timeOutline,
  documentTextOutline,
  trophyOutline,
  trashOutline,
  createOutline,
  warningOutline
} from 'ionicons/icons';
import { addIcons } from 'ionicons';

// Registrar todos los iconos
addIcons({
  'add-outline': addOutline,
  'calendar-outline': calendarOutline,
  'time-outline': timeOutline,
  'document-text-outline': documentTextOutline,
  'trophy-outline': trophyOutline,
  'trash-outline': trashOutline,
  'create-outline': createOutline,
  'warning-outline': warningOutline
});

const selectedDate = ref(new Date());
const events = ref([]);
const showEventModal = ref(false);
const selectedEvent = ref({});
const showCreateModal = ref(false);
const newEvent = ref({ id: null, title: '', date: '', time: '', description: '' });
const showDeleteModal = ref(false);
const eventToDeleteId = ref(null);
const calendarRef = ref(null);

const config = {
  defaultMode: 'month',
  week: { startsOn: 1, nDays: 7 },
  style: { fontFamily: "'Roboto', sans-serif'" },
  eventDialog: { isCustom: true, isDisabled: false },
  dayBoundaries: { start: 7, end: 22 },
  monthConfig: { showEventsIndicator: true, showEventTime: false },
  eventList: { showDayName: true, showDate: true }
};

const userEvents = computed(() => {
  return events.value.map(e => ({
    id: e.id,
    title: e.title,
    date: e.time.start.split(' ')[0],
    time: formatEventTime(e.time.start),
    description: e.description
  }));
});

async function fetchTournaments() {
    try {
      const { data } = await axios.get(
        "http://localhost:8082/api/tournaments/player",
        { headers: { Authorization: `Bearer ${localStorage.getItem("token")}` } }
      );
      events.value = data;
      // Mapeamos cada torneo al formato que Qalendar necesita
      events.value = data.map((tournament, index) => {
        // Convertir la fecha ISO a "YYYY-MM-DD hh:mm"
        const fecha = tournament.startDate.replace("T", " ").slice(0, 16);
        return {
          title: tournament.name,
          time: {
            start: fecha,
            end: fecha,
          },
          id: tournament.id ?? index,
          description: null, // por ahora nulo
        };
      });
    } catch (error) {
      console.error("Error al obtener eventos:", error);
    }
  }

async function fetchEvents() {
  try {
    const { data } = await axios.get(
      'http://localhost:8082/api/events',
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    );
    events.value = data.map(e => {
      const fecha = e.eventTime.replace('T', ' ').slice(0, 16);
      return { id: e.id, title: e.title, time: { start: fecha, end: fecha }, description: e.description || '' };
    });
  } catch (err) {
    console.error('Error al obtener eventos:', err);
  }
}

function handlePeriodChange(dateStr) {
  selectedDate.value = new Date(dateStr);
}

function handleEventClick(event) {
  selectedEvent.value = event;
  showEventModal.value = true;
}

function handleDateClick(dateStr) {
  selectedDate.value = new Date(dateStr);
  newEvent.value = { id: null, title: '', date: dateStr.split('T')[0], time: '12:00', description: '' };
}

function closeModal() { showEventModal.value = false; }

function openCreateEventModal() {
  const d = selectedDate.value.toISOString().split('T')[0];
  newEvent.value = { id: null, title: '', date: d, time: '12:00', description: '' };
  showCreateModal.value = true;
}

function editEvent(event) {
  newEvent.value = {
    id: event.id,
    title: event.title,
    date: event.date,
    time: event.time,
    description: event.description || ''
  };
  showCreateModal.value = true;
}

function closeCreateModal() { showCreateModal.value = false; }

async function saveEvent() {
  const dto = {
    title: newEvent.value.title,
    description: newEvent.value.description,
    eventTime: new Date(`${newEvent.value.date}T${newEvent.value.time}`)
  };
  try {
    if (newEvent.value.id) {
      await axios.put(
        `http://localhost:8082/api/events/${newEvent.value.id}`,
        dto,
        { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
      );
    } else {
      await axios.post(
        'http://localhost:8082/api/events',
        dto,
        { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
      );
    }
    await fetchEvents();
    closeCreateModal();
  } catch (err) {
    console.error('Error al guardar evento:', err);
  }
}

function confirmDeleteEvent(id) {
  eventToDeleteId.value = id;
  showDeleteModal.value = true;
}

function cancelDelete() {
  showDeleteModal.value = false;
  eventToDeleteId.value = null;
}

async function proceedWithDelete() {
  try {
    await axios.delete(
      `http://localhost:8082/api/events/${eventToDeleteId.value}`,
      { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
    );
    await fetchEvents();
    showDeleteModal.value = false;
    eventToDeleteId.value = null;
  } catch (err) {
    console.error('Error al eliminar evento:', err);
  }
}

function formatDate(date) {
  if (!date) return '';
  return new Date(date).toLocaleDateString('es-ES',{ day:'2-digit', month:'2-digit', year:'numeric' });
}

function formatEventTime(timeStr) {
  if (!timeStr) return '';
  return new Date(timeStr).toLocaleTimeString('es-ES',{ hour:'2-digit', minute:'2-digit', hour12:false });
}

function formatDay(date) {
  if (!date) return '';
  return new Date(date).getDate().toString().padStart(2,'0');
}

function formatMonth(date) {
  if (!date) return '';
  return new Date(date).toLocaleString('es-ES',{ month:'short' });
}

function forceCalendarRefresh() {
  if (calendarRef.value) {
    const calendarElement = calendarRef.value.$el;
    calendarElement.style.display = 'none';
    requestAnimationFrame(() => {
      calendarElement.style.display = 'block';
      window.dispatchEvent(new Event('resize'));
    });
  }
}

onMounted(async () => {
  await fetchTournaments()
  await fetchEvents();
  await nextTick();
  forceCalendarRefresh();
  window.addEventListener('resize', forceCalendarRefresh);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', forceCalendarRefresh);
});
</script>

<style scoped>
/* Estilos generales */
ion-content {
  --background: #f9f5f0;
}

.calendar-container {
  background: #f9f5f0;
  max-width: 1600px;
  width: 100%;
  margin: 0 auto;
  padding: 1rem;
  box-sizing: border-box;
  overflow: visible !important;
}

.calendar-layout {
  display: flex;
  gap: 1.5rem;
  width: 100%;
  height: 100%;
}

.calendar-section {
  flex: 1.5;
  display: flex;
  flex-direction: column;
}

.events-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 350px;
}

.section {
  padding: 1.5rem;
  background-color: #e0e1dd;
  border-radius: 1rem;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.section:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(26, 40, 65, 0.15);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  padding: 0 0.5rem;
}

.section-header h2 {
  color: #1a2841;
  font-size: 1.5rem;
  margin: 0;
  font-weight: 600;
}

.fixed-calendar {
  width: 100%;
  height: 650px;
  overflow: visible;
  position: relative;
  display: flex;
  flex-direction: column;
}

.add-event-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.add-event-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

.events-list-container {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem;
  background-color: #f5efe7;
  border-radius: 0.75rem;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.events-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.event-card {
  display: flex;
  flex-wrap: wrap;
  background: #fff;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.event-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.event-date {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: white;
  border-radius: 8px;
  padding: 0.75rem;
  text-align: center;
  min-width: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.event-day {
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1;
}

.event-month {
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
  min-width: 0; /* Permite que el texto se ajuste */
}

.event-title {
  color: #1a2841;
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.event-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.event-time, .event-description {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #1a2841;
  font-size: 0.9rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.event-icon {
  color: #3d5a80;
  font-size: 1.1rem;
  min-width: 1.1rem;
  flex-shrink: 0;
}

.event-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-left: auto;
  justify-content: center;
  flex-shrink: 0;
}

.event-action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  white-space: nowrap;
}

.edit-btn {
  background-color: rgba(61, 90, 128, 0.1);
  color: #3d5a80;
}

.edit-btn:hover {
  background-color: rgba(61, 90, 128, 0.2);
}

.delete-btn {
  background-color: rgba(230, 57, 70, 0.1);
  color: #e63946;
}

.delete-btn:hover {
  background-color: rgba(230, 57, 70, 0.2);
}

.no-events-message {
  width: 100%;
  padding: 2rem;
  text-align: center;
  color: #4a5568;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.empty-icon {
  font-size: 3rem;
  color: #3d5a80;
  opacity: 0.5;
  margin-bottom: 1rem;
}

.empty-subtitle {
  font-size: 0.9rem;
  color: #718096;
  margin-top: 0.5rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 1rem;
  box-sizing: border-box;
}

.modal-content {
  background-color: #f5efe7;
  border-radius: 16px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  animation: modal-appear 0.3s ease-out;
}

.create-modal {
  max-width: 600px;
}

.delete-modal {
  max-width: 450px;
}

.modal-header {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: white;
  padding: 1.25rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.delete-header {
  background: linear-gradient(135deg, #e63946, #c1121f);
}

.modal-header h3 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.close-button:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.modal-body {
  padding: 1.5rem;
}

.modal-detail {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background-color: #ffffff;
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.modal-icon {
  color: #3d5a80;
  font-size: 1.25rem;
  min-width: 1.25rem;
}

.modal-text {
  color: #1a2841;
  font-size: 1rem;
}

.modal-footer {
  padding: 1rem 1.5rem 1.5rem;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.delete-footer {
  border-top: 1px solid #e2e8f0;
  padding-top: 1.25rem;
}

.action-button {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

.create-event-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #1a2841;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-icon {
  color: #3d5a80;
  font-size: 1.1rem;
}

.form-group input, .form-group textarea {
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  background-color: white;
  color: #1a2841;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input:focus, .form-group textarea:focus {
  outline: none;
  border-color: #3d5a80;
  box-shadow: 0 0 0 3px rgba(61, 90, 128, 0.2);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 0.5rem;
}

.cancel-button {
  background-color: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-button:hover {
  background-color: #cbd5e0;
}

.submit-button {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

.delete-confirmation {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 1rem 0;
}

.warning-icon {
  font-size: 3rem;
  color: #e63946;
  margin-bottom: 1rem;
}

.delete-text {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a2841;
  margin-bottom: 0.5rem;
}

.delete-subtext {
  font-size: 0.9rem;
  color: #4a5568;
}

.delete-confirm-btn {
  background: linear-gradient(135deg, #e63946, #c1121f);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.delete-confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

@keyframes modal-appear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Estilos específicos del calendario */
:deep(.calendar-root-wrapper),
:deep(.calendar-root) {
  background-color: #f5efe7 !important;
  color: #1a2841 !important;
  width: 100% !important;
  height: 100% !important;
}

:deep(.calendar-month) {
  background-color: #f5efe7 !important;
  height: 100% !important;
}

:deep(.calendar-month__weekday) {
  background-color: #f5efe7 !important;
  border-color: #e0e1dd !important;
  min-width: 120px !important;
  height: 100px !important;
}

:deep(.calendar-month__day-date) {
  color: #1a2841 !important;
  font-weight: 600 !important;
}

:deep(.calendar-month__day-name) {
  color: #3d5a80 !important;
  font-weight: 600 !important;
}

:deep(.calendar-header) {
  background-color: #f5efe7 !important;
  padding: 0 100px !important;
  min-width: auto !important;
  width: 100% !important;
}

:deep(.calendar-header__period-name) {
  color: #1a2841 !important;
  font-weight: 700 !important;
  font-size: 1.3rem !important;
}

:deep(.calendar-header__nav-buttons) {
  position: absolute;
  right: 20px;
  gap: 20px;
}

:deep(.calendar-header__nav-button) {
  background: linear-gradient(145deg, #3d5a80, #2c4366) !important;
  transform: scale(1.3);
  z-index: 1000;
}

:deep(.calendar-header__nav-icon) {
  color: white !important;
}

:deep(.calendar-month__event-indicator) {
  background-color: #3d5a80 !important;
  width: 6px !important;
  height: 6px !important;
  border-radius: 50% !important;
  margin: 2px !important;
}

:deep(.calendar-month__event-indicators) {
  display: flex;
  justify-content: center;
  margin-top: 4px;
}

:deep(.calendar-month__weekday.is-today) {
  background-color: rgba(61, 90, 128, 0.05) !important;
}

:deep(.calendar-month__weekday.is-today .calendar-month__day-date) {
  background-color: #3d5a80 !important;
  color: white !important;
}

:deep(.calendar-month__weekday.is-selected) {
  box-shadow: inset 0 0 0 2px #3d5a80 !important;
}

:deep(.calendar-month__weekday:hover) {
  background-color: rgba(61, 90, 128, 0.1) !important;
}

/* Estilos para los eventos en el calendario */
:deep(.agenda__event) {
  background: linear-gradient(145deg, #3d5a80, #2c4366) !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1) !important;
  padding: 10px !important;
  margin-bottom: 8px !important;
  transition: all 0.3s ease !important;
}

:deep(.day-view__event-container) {
  background: linear-gradient(145deg, #3d5a80, #2c4366) !important;
  border-radius: 6px !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1) !important;
  border-left: none !important;
  transition: all 0.3s ease !important;
}

/* Ajustes para la vista responsiva */
@media (max-width: 1525px) {
  .calendar-container {
    max-width: 100%;
    padding: 20px;
  }
  
  :deep(.calendar-header) {
    padding: 0 50px !important;
  }
  
  :deep(.calendar-month__weekday) {
    min-width: 100px !important;
  }
}

@media (max-width: 1200px) {
  :deep(.calendar-month__weekday) {
    min-width: 80px !important;
    height: 90px !important;
  }
}

@media (max-width: 1024px) {
  .calendar-layout {
    flex-direction: column;
  }
  
  .calendar-section, .events-section {
    width: 100%;
    min-width: unset;
  }
  
  .fixed-calendar {
    height: 500px;
  }
  
  :deep(.calendar-month__weekday) {
    min-width: 70px !important;
    height: 80px !important;
  }
  
  .event-card {
    flex-wrap: wrap;
  }
  
  .event-details {
    width: calc(100% - 80px);
  }
  
  .event-actions {
    width: 100%;
    flex-direction: row;
    margin-top: 1rem;
    margin-left: 0;
  }
  
  .event-action-btn {
    flex: 1;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  :deep(.calendar-month__weekday) {
    min-width: 60px !important;
    height: 60px !important;
  }
  
  .section {
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  :deep(.calendar-month__weekday) {
    min-width: 40px !important;
    height: 50px !important;
  }
  
  .section {
    padding: 0.75rem;
  }
  
  .event-card {
    flex-direction: column;
  }
  
  .event-date {
    flex-direction: row;
    justify-content: center;
    gap: 0.5rem;
    padding: 0.5rem;
    margin-bottom: 0.5rem;
  }
  
  .event-details {
    width: 100%;
    padding-left: 0;
  }
}

/* ocultar opciones de la libreria */
:deep(.date-picker__value-display) {
  display: none;
}

:deep(.calendar-header__mode-picker .calendar-header__mode-value) {
  display: none;
}
</style>

