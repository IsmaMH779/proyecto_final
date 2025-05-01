<template>
  <ion-page>
    <div class="custom-calendar">
      <Qalendar :selected-date="new Date()" :events="events" :config="config" />
    </div>
  </ion-page>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { IonPage } from "@ionic/vue";
import { Qalendar } from "qalendar";
import "qalendar/dist/style.css";
import axios from "axios";

// Lista reactiva de eventos
const events = ref([]);

// Configuración del calendario
const config = {
  defaultMode: "month",
};

// Función para traer eventos desde la API
async function fetchEvents() {
  try {
    const { data } = await axios.get(
      "http://localhost:8082/api/tournaments/organizer",
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

// Lanza la consulta al montar el componente
onMounted(() => {
  fetchEvents();
});
</script>

<style scoped>
.custom-calendar {
  margin: 15px auto 0 auto;
  width: 100%;
}

@media (min-width: 1022px) {
  .custom-calendar {
  margin: auto;
  width: 100%;
  /* Restar la altura del navbar */
  height: calc(100dvh - var(--ion-navbar-height, 56px));
  /* Alternativa moderna */
  height: 100dvh;
  max-height: 100%;
}
}

/* Hacer que el calendario ocupe todo el espacio */
:deep(.calendar-root-wrapper),
:deep(.calendar-root),
:deep(.calendar-month) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

:deep(.agenda__event) {
  background-color: #1a2841;
}

:deep(.calendar-month__wrapper) {
  flex: 1;
}

ion-page {
  height: 100%;
}

/* Ocultar parte del header */
:deep(.date-picker__value-display),
:deep(.calendar-header__mode-picker) {
  display: none;
}

/* Resaltar el día actual */
:deep(.calendar-month__weekday.is-today .calendar-month__day-date) {
  background-color: #1a2841;
  color: #ffffff !important;
}

/* Resaltar día seleccionado */
:deep(.qalendar-is-small .calendar-month__weekday.is-selected) {
  box-shadow: inset 0 0 0 3px #1a2841;
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

/* Estilos de la vista Agenda */
:deep(.agenda__wrapper .agenda__header .agenda__header-date) {
  background-color: #1a2841;
}

:deep(.agenda__wrapper .agenda__header .agenda__header-day-name) {
  color: #1a2841;
}

/* Modal */
:deep(.event-flyout__info-wrapper) {
  background-color: #E0E1DD;
}
:deep(.qalendar-is-small .calendar-month__event .calendar-month__event-color) {
  background-color: #1a2841;
}
</style>