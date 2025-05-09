<template>
  <ion-page>
    <ion-content class="no-horizontal-scroll">
      <div class="calendar-container">
        <div class="calendar-layout">
          <!-- Lado izquierdo: Calendario (2/3) -->
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
          
          <!-- Lado derecho: Panel de eventos (1/3) -->
          <div class="section events-section">
            <div class="section-header">
              <h2>Mis Eventos</h2>
              <button class="add-event-btn" @click="openCreateEventModal">
                <ion-icon name="add-outline"></ion-icon>
                Crear Evento
              </button>
            </div>
            
            <!-- Lista de eventos creados -->
            <div class="events-list-container">
              <div v-if="userEvents.length > 0" class="events-list">
                <div 
                  v-for="(event, index) in userEvents" 
                  :key="index" 
                  class="event-card"
                >
                  <div class="event-date">
                    <div class="event-day">{{ formatDay(event.date) }}</div>
                    <div class="event-month">{{ formatMonth(event.date) }}</div>
                  </div>
                  <div class="event-details">
                    <h3 class="event-title">{{ event.title }}</h3>
                    <div class="event-info">
                      <div class="event-time">
                        <ion-icon name="time-outline"></ion-icon>
                        {{ event.time }}
                      </div>
                      <div class="event-description" v-if="event.description">
                        <ion-icon name="document-text-outline"></ion-icon>
                        {{ event.description }}
                      </div>
                    </div>
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
    
    <!-- Modal para ver detalles de evento del calendario -->
    <div class="modal-overlay" v-if="showEventModal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedEvent.title }}</h3>
          <button class="close-button" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <div class="modal-detail">
            <ion-icon name="calendar-outline"></ion-icon>
            <span>{{ formatDate(new Date(selectedEvent.time?.start)) }}</span>
          </div>
          <div class="modal-detail">
            <ion-icon name="time-outline"></ion-icon>
            <span>{{ formatEventTime(selectedEvent.time?.start) }}</span>
          </div>
          <div class="modal-detail" v-if="selectedEvent.description">
            <ion-icon name="location-outline"></ion-icon>
            <span>{{ selectedEvent.description }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="action-button">Ver detalles</button>
        </div>
      </div>
    </div>
    
    <!-- Modal para crear nuevo evento -->
    <div class="modal-overlay" v-if="showCreateModal" @click="closeCreateModal">
      <div class="modal-content create-modal" @click.stop>
        <div class="modal-header">
          <h3>Crear Nuevo Evento</h3>
          <button class="close-button" @click="closeCreateModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="createEvent" class="create-event-form">
            <div class="form-group">
              <label for="event-title">Título</label>
              <input 
                type="text" 
                id="event-title" 
                v-model="newEvent.title" 
                placeholder="Título del evento"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="event-date">Fecha</label>
              <input 
                type="date" 
                id="event-date" 
                v-model="newEvent.date"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="event-time">Hora</label>
              <input 
                type="time" 
                id="event-time" 
                v-model="newEvent.time"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="event-description">Descripción</label>
              <textarea 
                id="event-description" 
                v-model="newEvent.description" 
                placeholder="Descripción del evento"
                rows="3"
              ></textarea>
            </div>
            
            <div class="form-actions">
              <button type="button" class="cancel-button" @click="closeCreateModal">Cancelar</button>
              <button type="submit" class="submit-button">Crear Evento</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </ion-page>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import { IonPage, IonContent, IonIcon } from "@ionic/vue";
import { Qalendar } from "qalendar";
import "qalendar/dist/style.css";
import axios from "axios";

const selectedDate = ref(new Date());
const events = ref([]);
const showEventModal = ref(false);
const selectedEvent = ref({});
const calendarRef = ref(null);

// Lista de eventos creados por el usuario
const userEvents = ref([]);

// Modal para crear eventos
const showCreateModal = ref(false);
const newEvent = ref({
  title: '',
  date: '',
  time: '',
  description: ''
});

// Configuración del calendario con indicadores de eventos (puntitos)
const config = {
  defaultMode: "month",
  week: {
    startsOn: 1, // Semana comienza en lunes
    nDays: 7,
  },
  style: {
    fontFamily: "'Roboto', sans-serif",
  },
  eventDialog: {
    isCustom: true, // Usamos nuestro propio modal
    isDisabled: false, // Permitimos que los eventos sean clickables
  },
  dayBoundaries: {
    start: 7,
    end: 22,
  },
  // Mostrar puntitos en lugar de eventos en la vista de mes
  monthConfig: {
    showEventsIndicator: true, // Muestra puntitos para indicar eventos
    showEventTime: false, // No muestra la hora en la vista de mes
  },
  // Configuración para la vista de agenda (día/semana)
  eventList: {
    showDayName: true,
    showDate: true,
  }
};

// Eventos del día seleccionado
const selectedDayEvents = computed(() => {
  const currentDate = selectedDate.value;
  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();
  const day = currentDate.getDate();
  
  return events.value.filter(event => {
    const eventDate = new Date(event.time.start);
    return eventDate.getFullYear() === year && 
           eventDate.getMonth() === month && 
           eventDate.getDate() === day;
  });
});

// Función para traer eventos desde la API
async function fetchEvents() {
  try {
    const { data } = await axios.get(
      "http://localhost:8082/api/tournaments/organizer",
      { headers: { Authorization: `Bearer ${localStorage.getItem("token")}` } }
    );
    
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
        description: tournament.location || 'Ubicación no disponible',
        color: '#1a2841',
        isEditable: false,
      };
    });
  } catch (error) {
    console.error("Error al obtener eventos:", error);
  }
}

function handlePeriodChange(newDateString) {
  // newDateString viene en formato ISO, p. ej. "2025-05-10"
  selectedDate.value = new Date(newDateString);
}

function handleEventClick(event) {
  selectedEvent.value = event;
  showEventModal.value = true;
}

function handleDateClick(dateStr) {
  // Actualizar la fecha seleccionada cuando se hace clic en un día
  selectedDate.value = new Date(dateStr);
  
  // Pre-llenar la fecha en el formulario de nuevo evento
  const formattedDate = new Date(dateStr).toISOString().split('T')[0];
  newEvent.value.date = formattedDate;
}

function closeModal() {
  showEventModal.value = false;
}

// Funciones para el modal de crear evento
function openCreateEventModal() {
  // Pre-llenar la fecha con la fecha seleccionada en el calendario
  const formattedDate = selectedDate.value.toISOString().split('T')[0];
  newEvent.value = {
    title: '',
    date: formattedDate,
    time: '12:00',
    description: ''
  };
  showCreateModal.value = true;
}

function closeCreateModal() {
  showCreateModal.value = false;
}

function createEvent() {
  // Añadir el nuevo evento a la lista de eventos del usuario
  userEvents.value.push({...newEvent.value});
  
  // Ordenar eventos por fecha
  userEvents.value.sort((a, b) => new Date(a.date) - new Date(b.date));
  
  // Cerrar el modal
  closeCreateModal();
}

// Formatear fecha: DD/MM/YYYY
function formatDate(date) {
  if (!date) return '';
  const d = new Date(date);
  return d.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
}

// Formatear hora: HH:MM
function formatEventTime(timeString) {
  if (!timeString) return '';
  const date = new Date(timeString);
  return date.toLocaleTimeString('es-ES', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  });
}

// Formatear día: DD
function formatDay(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.getDate().toString().padStart(2, '0');
}

// Formatear mes: MMM
function formatMonth(dateString) {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('es-ES', { month: 'short' });
}

// Función para forzar el refresco del calendario
function forceCalendarRefresh() {
  if (calendarRef.value) {
    // Forzar un reflow del DOM
    const width = calendarRef.value.offsetWidth;
    
    // Crear un evento de resize para forzar a Qalendar a recalcular su layout
    window.dispatchEvent(new Event('resize'));
  }
}

// Lanza la consulta al montar el componente
onMounted(async () => {
  await fetchEvents();
  
  // Esperar a que el DOM se actualice
  await nextTick();
  
  // Forzar el refresco del calendario para solucionar el bug de visualización inicial
  setTimeout(forceCalendarRefresh, 100);
});
</script>

<style scoped>
/* Estilos generales */
.no-horizontal-scroll {
  --background: #f9f5f0;
  overflow-x: hidden;
}

.calendar-container {
  background: #f9f5f0;
  max-width: 1400px;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
  padding: 1rem;
  box-sizing: border-box;
  position: relative;
  overflow-x: hidden;
  min-height: calc(100vh - 2rem);
}

/* Layout principal: calendario (2/3) y eventos (1/3) */
.calendar-layout {
  display: flex;
  gap: 1.5rem;
  width: 100%;
  height: 100%;
}

/* Sección del calendario (2/3) */
.calendar-section {
  flex: 2;
  display: flex;
  flex-direction: column;
}

/* Sección de eventos (1/3) */
.events-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* Estilos comunes para las secciones */
.section {
  padding: 1.5rem;
  background-color: #e0e1dd;
  border-radius: 1rem;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  overflow: hidden;
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

/* Calendario */
.fixed-calendar {
  width: 100%;
  height: 600px;
  overflow: visible;
  position: relative;
  flex: 1;
}

/* Botón para añadir evento */
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

.add-event-btn ion-icon {
  font-size: 1.2rem;
}

/* Contenedor de la lista de eventos */
.events-list-container {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem;
  background-color: #f5efe7;
  border-radius: 0.75rem;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* Lista de eventos */
.events-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

/* Tarjeta de evento */
.event-card {
  display: flex;
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

/* Fecha del evento */
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

/* Detalles del evento */
.event-details {
  padding-left: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.event-title {
  color: #1a2841;
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 600;
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
  color: #4a5568;
  font-size: 0.9rem;
}

.event-time ion-icon, .event-description ion-icon {
  color: #3d5a80;
  font-size: 1rem;
}

/* Mensaje de no eventos */
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

/* Modal overlay */
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

/* Contenido del modal */
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

/* Cabecera del modal */
.modal-header {
  background: linear-gradient(135deg, #3d5a80, #2c4366);
  color: white;
  padding: 1.25rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

/* Cuerpo del modal */
.modal-body {
  padding: 1.5rem;
}

.modal-detail {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #4a5568;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.modal-detail:last-child {
  margin-bottom: 0;
}

.modal-detail ion-icon {
  color: #3d5a80;
  font-size: 1.25rem;
}

/* Pie del modal */
.modal-footer {
  padding: 1rem 1.5rem 1.5rem;
  display: flex;
  justify-content: flex-end;
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

/* Formulario para crear evento */
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
}

.form-group input, .form-group textarea {
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  background-color: white;
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

/* Estilos para el calendario */
:deep(.calendar-root-wrapper),
:deep(.calendar-root),
:deep(.calendar-month) {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

:deep(.calendar-month__wrapper) {
  flex: 1;
  overflow: hidden;
}

/* Puntitos para indicar eventos */
:deep(.calendar-month__event-indicator) {
  background-color: #1a2841 !important;
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

/* Fondo general del calendario */
:deep(.calendar-root-wrapper .calendar-root) {
  background-color: #f5efe7 !important;
  color: #000000 !important;
}

/* Color del mes y año en el header */
:deep(.calendar-header__period-name) {
  color: #000000 !important;
  font-weight: bold;
  font-size: 1.2rem;
}

/* Color de los días */
:deep(.calendar-month__day-date) {
  color: #000000 !important;
  font-weight: bold;
}

/* Color de los nombres de día */
:deep(.calendar-month__day-name) {
  color: #1a2841 !important;
  font-weight: bold;
}

/* Resaltar el día actual */
:deep(.calendar-month__weekday.is-today .calendar-month__day-date) {
  background-color: #1a2841;
  color: #ffffff !important;
}

/* Resaltar día seleccionado */
:deep(.calendar-month__weekday.is-selected) {
  box-shadow: inset 0 0 0 3px #1a2841;
}

/* Hacer que los días sean clickables */
:deep(.calendar-month__weekday) {
  cursor: pointer;
  transition: background-color 0.2s;
}

:deep(.calendar-month__weekday:hover) {
  background-color: rgba(61, 90, 128, 0.1);
}

/* Botones de navegación */
:deep(.calendar-header__nav-button) {
  background: linear-gradient(145deg, #3d5a80, #2c4366);
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

:deep(.calendar-header__nav-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

:deep(.calendar-header__nav-button:active) {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:deep(.calendar-header__nav-icon) {
  color: #ffffff;
}

/* Estilos para los eventos nativos de Qalendar */
:deep(.agenda__event) {
  background: linear-gradient(145deg, #3d5a80, #2c4366) !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1) !important;
  padding: 10px !important;
  margin-bottom: 8px !important;
  transition: all 0.3s ease !important;
}

:deep(.agenda__event:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15) !important;
}

:deep(.agenda__event-name) {
  color: white !important;
  font-weight: 600 !important;
  font-size: 1rem !important;
}

:deep(.agenda__event-time) {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 0.85rem !important;
  margin-top: 4px !important;
}

:deep(.agenda__event-description) {
  color: rgba(255, 255, 255, 0.8) !important;
  font-size: 0.85rem !important;
  margin-top: 6px !important;
}

/* Estilos para la vista de día/semana */
:deep(.day-view__event-container) {
  background: linear-gradient(145deg, #3d5a80, #2c4366) !important;
  border-radius: 6px !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1) !important;
  border-left: none !important;
  transition: all 0.3s ease !important;
}

:deep(.day-view__event-container:hover) {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15) !important;
}

:deep(.day-view__event-name) {
  color: white !important;
  font-weight: 600 !important;
}

:deep(.day-view__event-time) {
  color: rgba(255, 255, 255, 0.9) !important;
}

/* Estilos para el header de la agenda */
:deep(.agenda__header) {
  background-color: #f5efe7 !important;
  border-bottom: 2px solid #e0e1dd !important;
}

:deep(.agenda__header-date) {
  background-color: #1a2841 !important;
  color: white !important;
  border-radius: 50% !important;
  width: 36px !important;
  height: 36px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  font-weight: bold !important;
}

:deep(.agenda__header-day-name) {
  color: #1a2841 !important;
  font-weight: 600 !important;
}

/* Responsive */
@media (max-width: 1024px) {
  .calendar-layout {
    flex-direction: column;
  }
  
  .calendar-section, .events-section {
    flex: none;
    width: 100%;
  }
  
  .fixed-calendar {
    height: 500px;
  }
}

@media (max-width: 768px) {
  .fixed-calendar {
    height: 450px;
  }
  
  .section {
    padding: 1rem;
  }
  
  .section-header h2 {
    font-size: 1.3rem;
  }
  
  .add-event-btn {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .fixed-calendar {
    height: 400px;
  }
  
  .section {
    padding: 0.75rem;
  }
  
  .section-header h2 {
    font-size: 1.1rem;
  }
  
  .event-card {
    flex-direction: column;
  }
  
  .event-date {
    margin-bottom: 1rem;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem;
  }
  
  .event-day, .event-month {
    font-size: 1rem;
  }
  
  .event-details {
    padding-left: 0;
  } 
  
  .modal-content {
    max-width: 100%;
  }
}
</style>