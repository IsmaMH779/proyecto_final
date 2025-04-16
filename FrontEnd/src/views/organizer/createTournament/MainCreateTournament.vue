<!-- <template>
  <ion-page>
    <div class="header">Crear torneo</div>
    <div class="content">
      <div class="file-container">
        <form @submit.prevent="handleSubmit">
          <label>
            <span class="input-title">Nombre:</span>
            <input type="text" v-model="form.name" required class="custom-input"/>
          </label>

          <label>
            <span class="input-title">Juego:</span>
            <select v-model="form.game" required class="custom-select">
              <option value="" disabled selected></option>
              <option value="Magic">Magic</option>
              <option value="Pokemon">Pokemon</option>
              <option value="One Piece">One Piece</option>
            </select>
          </label>

          <label>
            <span class="input-title">Formato:</span>
            <select v-model="form.format" required class="custom-select">
              <option value="" disabled selected></option>
              <option value="Direct_elimination">Eliminación directa</option>
            </select>
          </label>

          <label>
            <span class="input-title">Jugadores maximos:</span>
            <select v-model="form.maxPlayers" required class="custom-select">
              <option value="" disabled selected></option>
              <option value="4">4</option>
              <option value="16">16</option>
            </select>
          </label>

          <label>
            <span class="input-title">Dirección:</span>
            <input type="text" v-model="form.location" required class="custom-input"/>
          </label>

          <label>
            <span class="input-title">Fecha de inicio:</span>
            <input
              type="datetime-local"
              v-model="form.startDate"
              :min="minDateTime"
              required
            />
          </label>

          <ion-button expand="block" type="submit">Guardar</ion-button>
        </form>

        <div v-if="submitted">
          <ion-text color="success">Formulario enviado correctamente</ion-text>
        </div>
      </div>
    </div>
  </ion-page>
</template>

<script setup>
import { IonPage, IonButton, IonText } from "@ionic/vue";
import axios from "axios";
import { ref } from "vue";

// Form data
const form = ref({
  name: "",
  game: "",
  format: "",
  maxPlayers: "",
  location: "",
  startDate: "",
});

const submitted = ref(false);

// Fecha mínima (hoy con hora)
const today = new Date();
const pad = (n) => (n < 10 ? "0" + n : n);
const minDateTime = `${today.getFullYear()}-${pad(today.getMonth() + 1)}-${pad(
  today.getDate()
)}T${pad(today.getHours())}:${pad(today.getMinutes())}`;

const handleSubmit = async () => {
  const response = await axios.post(
    "http://localhost:8082/api/tournaments",
    form.value,
    {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );

  console.log(response.data);

  submitted.value = true;
};
</script>

<style scoped>
.header {
  background-color: #e0e1dd;
  height: 123px;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  color: black;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.5rem;
}

.content {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.file-container {
    background-color: #E0E1DD;
    border: 2px solid #0D1B2A;
    border-radius: 8px;
    width: 80%;
    padding: 15px;
    box-shadow: 0 4px 10px #0D1B2A;

}

.input-title {
    
}

label {
  display: block;
  margin: 12px 0;
  color: black;
}

.custom-select {
    border: none;
    outline: none;
    font-size: 16px;
    color: #415A77;
    background-color: transparent;
    font-size: 20px;
    margin-left: 19px;
    border-bottom: 2px solid #415A77;
  }

  .custom-input {
    border: none;
    outline: none;
    font-size: 16px;
    color: #415A77;
    background-color: transparent;
    margin-left: 19px;
    font-size: 20px;
    border-bottom: 2px solid #415A77;
  }
</style>-->

<template>
  <ion-page class="page-container">
    <div class="tournament-container">

      <div class="tournament-header">
        Crear Torneo
      </div>

      <div class="tournament-content">

        <div class="column">
          <label>
            <span class="input-title">Nombre:</span>
            <input
              type="text"
              v-model="form.name"
              required
              class="custom-input"
            />
          </label>

          <label>
            <span class="input-title">Juego:</span>
            <select v-model="form.game" required class="custom-select">
              <option value="" disabled selected></option>
              <option value="Magic">Magic</option>
              <option value="Pokemon">Pokemon</option>
              <option value="One Piece">One Piece</option>
            </select>
          </label>

          <label>
            <span class="input-title">Dirección:</span>
            <input
              type="text"
              v-model="form.location"
              required
              class="custom-input"
            />
          </label>
        </div>

        <div class="column">
          <label>
            <span class="input-title">Formato:</span>
            <select v-model="form.format" required class="custom-select">
              <option value="" disabled selected></option>
              <option value="Direct_elimination">Eliminación directa</option>
            </select>
          </label>

          <label>
            <span class="input-title">Jugadores máximos:</span>
            <select v-model="form.maxPlayers" required class="custom-select">
              <option value="" disabled selected></option>
              <option value="4">4</option>
              <option value="16">16</option>
            </select>
          </label>

          <label>
            <span class="input-title">Fecha de inicio:</span>
            <input
              type="datetime-local"
              v-model="form.startDate"
              :min="minDateTime"
              required
              class="date-input"
            />
          </label>
        </div>
      </div>

      <div class="tournament-footer">
        <form @submit.prevent="handleSubmit">
          <ion-button expand="block" type="submit" class="create-btn"
            >CREAR</ion-button
          >
        </form>
        <div v-if="submitted" class="success-message">
          <ion-text color="success">
            Formulario enviado correctamente
          </ion-text>
        </div>
      </div>
    </div>
  </ion-page>
</template>
<script setup>
import { IonPage, IonButton, IonText } from "@ionic/vue";
import axios from "axios";
import { ref } from "vue";

const form = ref({
  name: "",
  game: "",
  format: "",
  maxPlayers: "",
  location: "",
  startDate: "",
});

const submitted = ref(false);

const today = new Date();
const pad = (n) => (n < 10 ? "0" + n : n);
const minDateTime = `${today.getFullYear()}-${pad(today.getMonth() + 1)}-${pad(
  today.getDate()
)}T${pad(today.getHours())}:${pad(today.getMinutes())}`;

const handleSubmit = async () => {
  const response = await axios.post(
    "http://localhost:8082/api/tournaments",
    form.value,
    {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );

  console.log(response.data);
  submitted.value = true;
};
</script>
<style scoped>
.page-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh; 
}

.tournament-container {
  width: 90%;
  max-width: 900px;
  margin: 0 auto;
  background-color: #e0e1dd; 
  border: 2px solid #0d1b2a;
  border-radius: 12px;
  box-shadow: 0 4px 10px #0d1b2a;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.tournament-header {
  font-size: 1.8rem;
  font-weight: bold;
  color: #0d1b2a;
  margin-bottom: 15px;
  text-align: center;
}

.tournament-content {
  display: flex;
  width: 100%;
  justify-content: space-around; 
  margin-bottom: 20px;
}

.column {
  width: 45%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

label {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
  color: #0d1b2a;
  font-weight: 500;
}
.input-title {
  margin-bottom: 5px;
  font-size: 1rem;
}

.custom-input,
.custom-select,
.date-input {
  border: none;
  outline: none;
  font-size: 1rem;
  color: #415a77;
  background-color: transparent;
  border-bottom: 2px solid #415a77;
  padding: 5px 2px;
}

.custom-select {
}

.tournament-footer {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.create-btn {
  --background: #3c5062; 
  --background-hover: #2f3f4d;
  --color: #ffffff;
  margin-bottom: 10px; 
}

.success-message {
  font-size: 1rem;
  color: green;
}
</style>
