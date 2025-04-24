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
            <input type="text" v-model="form.name" required class="custom-input" />
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
            <span class="input-title">Provincia:</span>
            <input
              type="text"
              v-model="search"
              @input="showSuggestions = true"
              @blur="setTimeout(() => showSuggestions = false, 200)"
              class="custom-input"
              autocomplete="off"
            />
            <ul v-if="showSuggestions && filteredProvinces.length" class="suggestions-list">
              <li
                v-for="prov in filteredProvinces"
                :key="prov.code"
                @click="selectProvince(prov.name)"
              >
                {{ prov.name }}
              </li>
            </ul>
          </label>


          <label>
            <span class="input-title">Dirección:</span>
            <input type="text" v-model="form.address" required class="custom-input" />
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
            <input type="datetime-local" v-model="form.startDate" :min="minDateTime" required class="date-input" />
          </label>
        </div>
      </div>

      <div class="tournament-footer">
        <form @submit.prevent="handleSubmit">
          <ion-button expand="block" type="submit" class="create-btn">CREAR</ion-button>
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
import { ref, computed, onMounted } from "vue";
import provincias from '@/assets/data/location/locations.json';

// Formulario
const form = ref({
  name: "",
  game: "",
  format: "",
  maxPlayers: "",
  location: "",
  address: "",
  startDate: "",
});

const submitted = ref(false);

// Fecha mínima para el input de fecha
const today = new Date();
const pad = (n) => (n < 10 ? "0" + n : n);
const minDateTime = `${today.getFullYear()}-${pad(today.getMonth() + 1)}-${pad(
  today.getDate()
)}T${pad(today.getHours())}:${pad(today.getMinutes())}`;

// Provincias y búsqueda
const provinces = ref([]);
const search = ref("");
const showSuggestions = ref(false);

const filteredProvinces = computed(() => {
  if (!search.value) return [];
  return provinces.value.filter((prov) =>
    prov.name.toLowerCase().includes(search.value.toLowerCase())
  );
});

const selectProvince = (name) => {
  search.value = name;
  form.value.location = name;
  showSuggestions.value = false;
};

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

onMounted(async () => {
  provinces.value = await provincias;
});
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

.custom-select {}

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

/* Estilos dropdown */
.suggestions-list {
  list-style: none;
  margin: 0;
  padding: 0;
  border: 1px solid #415a77;
  border-top: none;
  max-height: 200px;
  overflow-y: auto;
  background-color: #fff;
  position: absolute;
  z-index: 10;
  width: 100%;
}

.suggestions-list li {
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}

.suggestions-list li:hover {
  background-color: #d3d3d3;
}

</style>
