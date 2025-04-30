<template>
    <ion-page>
      <div class="custom-calendar">
        <Qalendar :selected-date="new Date()" :events="events" :config="config" />
      </div>
    </ion-page>
  </template>
  
  <script>
  import { IonPage } from '@ionic/vue';
  import { Qalendar } from "qalendar";
  import "qalendar/dist/style.css";
  
  export default {
    components: {
      IonPage,
      Qalendar,
    },
  
    data() {
      return {
        events: [
          {
            title: "Meeting with Dora",
            with: "Albert Einstein",
            time: { start: "2025-04-27 04:52", end: "2025-04-30 05:37" },
            color: "green",
            isEditable: true,
            id: "de471c78cb5c",
            description:
              "Think of me as Yoda. Only instead of being little and green, I wear suits and I'm awesome.",
          }
        ],
  
        config: {
          defaultMode: "month",
        },
      };
    },
  };
  </script>
  
  <style scoped>
  .custom-calendar {
    margin: auto;
    width: 100%;
    /* Usar calc para restar la altura del navbar */
    height: calc(100dvh - var(--ion-navbar-height, 56px));
    /* Alternativa con CSS moderno */
    height: 100dvh;
    max-height: 100%;
    display: flex;
    flex-direction: column;
  }
  
  /* Hacer que el calendario ocupe todo el espacio disponible */
  :deep(.calendar-root-wrapper) {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  
  :deep(.calendar-root) {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  
  :deep(.calendar-month) {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  
  :deep(.calendar-month__wrapper) {
    flex: 1;
  }
  
  ion-page {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  
  /* Ocultar parte del header */
  :deep(.calendar-header__period) {
    display: none;
  }
  
  /* Resaltar el día actual */
  :deep(.calendar-month__weekday.is-today .calendar-month__day-date) {
    background-color: #16596F;
    /* Azul oscuro */
    color: white !important;
    /* Día actual en blanco */
    font-weight: bold;
    border-radius: 50%;
  }
  
  /* Resaltar día seleccionado */
  :deep(.qalendar-is-small .calendar-month__weekday.is-selected) {
    box-shadow: inset 0 0 0 3px #16596F;
  }
  
  /* Fondo general del calendario */
  :deep(.calendar-root-wrapper .calendar-root) {
    background-color: #EBEBEA !important;
    /* Blanco */
    color: black !important;
  }
  
  /* Color del mes y año en el header */
  :deep(.calendar-header__period-name) {
    color: black !important;
    font-weight: bold;
  }
  
  /* Cambiar el color de los días (todos en negro excepto el actual) */
  :deep(.calendar-month__day-date) {
    color: #000000 !important;
    /* Negro */
    font-weight: bold;
  }
  
  /* Cambiar el color del día actual a blanco */
  :deep(.calendar-month__weekday.is-today .calendar-month__day-date) {
    color: #FFFFFF !important;
    /* Blanco */
  }
  
  /* Cambiar el color de los días de la semana */
  :deep(.calendar-month__day-name) {
    color: #000000 !important;
    font-weight: bold;
  }
  
  /* Cambiar color del día actual (evento) */
  :deep(.agenda__wrapper .agenda__header .agenda__header-date) {
    background-color: #16596F;
  }
  
  :deep(.agenda__wrapper .agenda__header .agenda__header-day-name) {
    color: #16596F;
  }
  </style>