<template>
  <ion-page>
    <ion-content :scroll-y="true">
      <div class="create-tournament-app">
        <div class="content-container">
          <div class="form-card">
            <div class="form-header">
              <h1 class="form-title">Crear Torneo</h1>
            </div>
            
            <div class="form-content">
              <div class="form-columns">
                <div class="form-column">
                  <div class="form-group">
                    <label class="form-label" for="tournament-name">Nombre:</label>
                    <input 
                      id="tournament-name"
                      type="text" 
                      v-model="form.name" 
                      required 
                      class="form-input" 
                      placeholder="Nombre del torneo"
                    />
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label" for="tournament-game">Juego:</label>
                    <div class="select-wrapper">
                      <select 
                        id="tournament-game"
                        v-model="form.game" 
                        required 
                        class="form-select"
                      >
                        <option value="" disabled selected>Seleccionar juego</option>
                        <option value="Magic">Magic</option>
                        <option value="Pokemon">Pokemon</option>
                        <option value="One Piece">One Piece</option>
                      </select>
                      <div class="select-arrow">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                      </div>
                    </div>
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label" for="tournament-location">Provincia:</label>
                    <div class="location-select-container">
                      <input
                        id="tournament-location"
                        type="text"
                        v-model="search"
                        @input="showSuggestions = true"
                        @blur="setTimeout(() => showSuggestions = false, 200)"
                        class="form-input"
                        autocomplete="off"
                        placeholder="Buscar provincia"
                      />
                      <div class="suggestions-container" v-if="showSuggestions && filteredProvinces.length">
                        <ul class="suggestions-list">
                          <li
                            v-for="prov in filteredProvinces"
                            :key="prov.code"
                            @click="selectProvince(prov.name)"
                            class="suggestion-item"
                          >
                            {{ prov.name }}
                          </li>
                        </ul>
                      </div>
                    </div>
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label" for="tournament-address">Dirección:</label>
                    <input 
                      id="tournament-address"
                      type="text" 
                      v-model="form.address" 
                      required 
                      class="form-input" 
                      placeholder="Dirección del torneo"
                    />
                  </div>
                </div>
                
                <div class="form-column">
                  <div class="form-group">
                    <label class="form-label" for="tournament-format">Formato:</label>
                    <div class="select-wrapper">
                      <select 
                        id="tournament-format"
                        v-model="form.format" 
                        required 
                        class="form-select"
                      >
                        <option value="" disabled selected>Seleccionar formato</option>
                        <option value="Direct_elimination">Eliminación directa</option>
                      </select>
                      <div class="select-arrow">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                      </div>
                    </div>
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label" for="tournament-players">Jugadores máximos:</label>
                    <div class="select-wrapper">
                      <select 
                        id="tournament-players"
                        v-model="form.maxPlayers" 
                        required 
                        class="form-select"
                      >
                        <option value="" disabled selected>Seleccionar número</option>
                        <option value="4">4</option>
                        <option value="16">16</option>
                      </select>
                      <div class="select-arrow">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                      </div>
                    </div>
                  </div>
                  
                  <div class="form-group">
                    <label class="form-label" for="tournament-date">Fecha de inicio:</label>
                    <input 
                      id="tournament-date"
                      type="datetime-local" 
                      v-model="form.startDate" 
                      :min="minDateTime" 
                      required 
                      class="form-input date-input" 
                    />
                  </div>
                </div>
              </div>
              
              <div class="form-actions">
                <button @click="handleSubmit" class="submit-button">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M5 12h14"></path>
                    <path d="M12 5v14"></path>
                  </svg>
                  Crear Torneo
                </button>
              </div>
              
              <div v-if="submitted" class="success-message">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                  <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
                <span>Torneo creado correctamente</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { IonPage, IonContent } from "@ionic/vue";
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
  try {
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
    
    // Resetear el formulario después de 3 segundos
    setTimeout(() => {
      submitted.value = false;
      form.value = {
        name: "",
        game: "",
        format: "",
        maxPlayers: "",
        location: "",
        address: "",
        startDate: "",
      };
      search.value = "";
    }, 3000);
  } catch (error) {
    console.error("Error al crear el torneo:", error);
  }
};

onMounted(async () => {
  provinces.value = await provincias;
});
</script>

<style scoped>
/* Contenedor principal */
.create-tournament-app {
  background-color: #f9f5f0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
  padding: 2rem 0;
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Contenedor con ancho máximo */
.content-container {
  max-width: 1000px;
  width: 100%;
  margin: 0 auto;
  padding: 0 1rem;
}

/* Tarjeta del formulario */
.form-card {
  background-color: #e0e1dd;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(26, 40, 65, 0.15);
  border: 2px solid #1a2841;
}

/* Cabecera del formulario */
.form-header {
  background-color: #1a2841;
  padding: 1.5rem 2rem;
  color: #e0e1dd;
}

.form-title {
  margin: 0;
  font-size: 1.75rem;
  font-weight: 700;
}

/* Contenido del formulario */
.form-content {
  padding: 2rem;
}

/* Columnas del formulario */
.form-columns {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

/* Grupo de formulario */
.form-group {
  margin-bottom: 1.5rem;
  position: relative;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #1a2841;
}

.form-input,
.form-select {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #3d5a80;
  border-radius: 0.5rem;
  background-color: #fff;
  color: #1a2841;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #1a2841;
  box-shadow: 0 0 0 3px rgba(61, 90, 128, 0.2);
}

.form-input::placeholder {
  color: #a0aec0;
}

/* Select personalizado */
.select-wrapper {
  position: relative;
}

.form-select {
  appearance: none;
  padding-right: 2.5rem;
}

.select-arrow {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #3d5a80;
}

/* Input de fecha */
.date-input {
  color: #1a2841;
}

/* Contenedor de sugerencias */
.location-select-container {
  position: relative;
}

.suggestions-container {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  z-index: 10;
  margin-top: 0.25rem;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.15);
}

.suggestions-list {
  max-height: 200px;
  overflow-y: auto;
  margin: 0;
  padding: 0;
  list-style: none;
  background-color: #fff;
  border: 2px solid #3d5a80;
  border-radius: 0.5rem;
}

.suggestion-item {
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
  border-bottom: 1px solid rgba(61, 90, 128, 0.1);
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-item:hover {
  background-color: rgba(61, 90, 128, 0.1);
}

/* Acciones del formulario */
.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}

.submit-button {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background-color: #3d5a80;
  color: #fff;
  border: none;
  border-radius: 0.5rem;
  padding: 0.875rem 1.5rem;
  font-size: 1.125rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.submit-button:hover {
  background-color: #1a2841;
  transform: translateY(-2px);
}

.submit-button:active {
  transform: translateY(0);
}

/* Mensaje de éxito */
.success-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: rgba(72, 187, 120, 0.1);
  border: 2px solid #48bb78;
  border-radius: 0.5rem;
  color: #2f855a;
  font-weight: 600;
}

.success-message svg {
  color: #48bb78;
}

/* Responsive */
@media (max-width: 768px) {
  .form-content {
    padding: 1.5rem;
  }
  
  .form-columns {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}

@media (max-width: 480px) {
  .create-tournament-app {
    padding: 1rem 0;
  }
  
  .form-content {
    padding: 1rem;
  }
  
  .form-header {
    padding: 1rem 1.5rem;
  }
  
  .form-title {
    font-size: 1.5rem;
  }
}
</style>