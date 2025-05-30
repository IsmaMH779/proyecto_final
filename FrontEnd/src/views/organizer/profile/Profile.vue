<template>
  <ion-page>
    <ion-content :scroll-y="true">
      <div class="organizer-app">
        <!-- Loader -->
        <div v-if="isLoading" class="loader-container">
          <div class="loader"></div>
          <p class="loader-text">Cargando perfil...</p>
        </div>

        <!-- Contenido principal -->
        <div v-else class="content-container">
          <!-- Header con información básica -->
          <div class="profile-header">
            <div class="header-background"></div>
            
            <div class="profile-header-content">
              <div 
                class="profile-avatar"
                @click="handleProfileImageClicker"  
              >
                <img 
                  v-if="userData.imageUrl" 
                  :src="'http://localhost:8081/images/profile/' + userData.imageUrl" 
                  alt="Perfil"
                />
                <img v-else :src="defaultProfileImage" alt="Perfil por defecto" />
                <div class="edit-avatar-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 3a2.85 2.85 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3Z"></path>
                  </svg>
                </div>
              </div>
              
              <div class="profile-info">
                <div class="username-container">
                  <span ref="usernameSpan" class="hidden-span">{{ userData.username }}</span>
                  <input 
                    v-if="editMode" 
                    v-model="userData.username" 
                    class="username-input"
                    :class="{ 'input-error': usernameError }"
                    :style="{ width: inputWidth + 'px' }"
                    @input="adjustWidth"
                  />
                  <h1 v-else class="username-display">{{ userData.username }}</h1>
                  <p v-if="editMode && usernameError" class="error-message">
                    El nombre de usuario ya está en uso
                  </p>
                </div>
                
                <div class="location-badge" v-if="userData.location">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path>
                    <circle cx="12" cy="10" r="3"></circle>
                  </svg>
                  <span>{{ userData.location }}</span>
                </div>
              </div>
              
              <button class="edit-profile-button" @click="toggleEditMode">
                <svg v-if="!editMode" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M17 3a2.85 2.85 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3Z"></path>
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 6 9 17l-5-5"></path>
                </svg>
                <span>{{ editMode ? 'Guardar cambios' : 'Editar perfil' }}</span>
              </button>
            </div>
          </div>

          <!-- Pestañas de navegación -->
          <div class="tabs-navigation">
            <button 
              v-for="tab in tabs" 
              :key="tab.id"
              @click="activeTab = tab.id"
              :class="['tab-button', { active: activeTab === tab.id }]"
            >
              <span>{{ tab.name }}</span>
            </button>
          </div>

          <!-- Contenido de las pestañas -->
          <div class="tab-content-container">
            <!-- Tab Información -->
            <div v-show="activeTab === 'info'" class="tab-content">
              <div class="section">
                <h2 class="section-title">Información de contacto</h2>
                
                <div class="contact-grid">
                  <!-- Teléfono principal -->
                  <div class="contact-item">
                    <div class="contact-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                      </svg>
                    </div>
                    <div class="contact-details">
                      <h3>Teléfono principal</h3>
                      <div class="field-wrapper">
                        <span ref="phone1Span" class="hidden-span">{{ userData.phoneNumber1 || 'No especificado' }}</span>
                        <input 
                          v-if="editMode" 
                          v-model="userData.phoneNumber1"
                          class="edit-input"
                          :style="{ width: phoneWidth + 'px' }"
                          @input="onPhoneInput"
                          placeholder="No especificado"
                          inputmode="numeric"
                        />
                        <p v-else class="contact-value">{{ userData.phoneNumber1 || 'No especificado' }}</p>
                        <p v-if="editMode && userData.phoneNumber1 && !isPhoneValid" class="error-message">
                          Debe tener 9 dígitos y empezar por 6, 7 o 9
                        </p>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Teléfono secundario -->
                  <div class="contact-item">
                    <div class="contact-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                      </svg>
                    </div>
                    <div class="contact-details">
                      <h3>Teléfono secundario</h3>
                      <div class="field-wrapper">
                        <span ref="phone2Span" class="hidden-span">{{ userData.phoneNumber2 || 'No especificado' }}</span>
                        <input 
                          v-if="editMode" 
                          v-model="userData.phoneNumber2"
                          class="edit-input"
                          :style="{ width: phoneWidth + 'px' }"
                          @input="onPhoneInput2"
                          placeholder="No especificado"
                          inputmode="numeric"
                        />
                        <p v-else class="contact-value">{{ userData.phoneNumber2 || 'No especificado' }}</p>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Dirección -->
                  <div class="contact-item">
                    <div class="contact-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M3 9h18v10a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V9Z"></path>
                        <path d="m3 9 2.45-4.9A2 2 0 0 1 7.24 3h9.52a2 2 0 0 1 1.8 1.1L21 9"></path>
                        <path d="M12 3v6"></path>
                      </svg>
                    </div>
                    <div class="contact-details">
                      <h3>Dirección</h3>
                      <div class="field-wrapper">
                        <span ref="addressSpan" class="hidden-span">{{ userData.address || 'No especificada' }}</span>
                        <input 
                          v-if="editMode" 
                          v-model="userData.address"
                          class="edit-input"
                          :style="{ width: addressWidth + 'px' }"
                          placeholder="No especificada"
                        />
                        <p v-else class="contact-value">{{ userData.address || 'No especificada' }}</p>
                      </div>
                    </div>
                  </div>
                  
                  <!-- Localidad (solo en modo edición) -->
                  <div class="contact-item" v-if="editMode">
                    <div class="contact-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path>
                        <circle cx="12" cy="10" r="3"></circle>
                      </svg>
                    </div>
                    <div class="contact-details">
                      <h3>Localidad</h3>
                      <div class="field-wrapper">
                        <select
                          v-model="userData.location"
                          class="location-select"
                        >
                          <option value="" disabled>Seleccionar localidad</option>
                          <option value="barcelona">Barcelona</option>
                          <option value="girona">Girona</option>
                          <option value="tarragona">Tarragona</option>
                          <option value="lleida">Lleida</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="section">
                <h2 class="section-title">Descripción</h2>
                <div class="description-container">
                  <span ref="descriptionSpan" class="hidden-span">{{ userData.description || 'No hay descripción registrada' }}</span>
                  <textarea 
                    v-if="editMode" 
                    v-model="userData.description"
                    class="description-textarea"
                    placeholder="Añade una descripción sobre ti o tu tienda..."
                    rows="4"
                  ></textarea>
                  <p v-else class="description-text">
                    {{ userData.description || 'No hay descripción registrada' }}
                  </p>
                </div>
              </div>
            </div>

            <!-- Tab Torneos -->
            <div v-show="activeTab === 'tournaments'" class="tab-content">
              <div class="section">
                <h2 class="section-title">Resumen de torneos</h2>
                <div class="stats-container">
                  <div class="stat-card">
                    <div class="stat-value">0</div>
                    <div class="stat-label">Torneos organizados</div>
                  </div>
                  <div class="stat-card">
                    <div class="stat-value">0</div>
                    <div class="stat-label">Torneos activos</div>
                  </div>
                  <div class="stat-card">
                    <div class="stat-value">0</div>
                    <div class="stat-label">Participantes totales</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Tab Insignias -->
            <div v-show="activeTab === 'badges'" class="tab-content">
              <div class="section">
                <h2 class="section-title">Mis insignias</h2>
                <div class="empty-state">
                  <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="8" r="7"></circle>
                    <polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"></polyline>
                  </svg>
                  <p>No has conseguido ninguna insignia todavía</p>
                  <p class="empty-state-subtext">Organiza torneos para desbloquear insignias</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Image picker modal -->
        <ImagePickerModal :isOpen="showImageModal" profileRole="organizer" @close="showImageModal = false" @uploaded="onImageUploaded" />
      </div>
    </ion-content>
  </ion-page>
</template>

<script>
import { ref, onMounted, nextTick, computed } from 'vue';
import { IonPage, IonContent } from '@ionic/vue';
import axios from 'axios';
import editIconPath from '@/assets/profile_assets/profile_settings.svg';
import saveIconPath from '@/assets/profile_assets/saveCheck_icon.svg';
import { useRouter } from 'vue-router';
import defaultProfileImage from '@/assets/profile_assets/default-profile-image.svg';
import ImagePickerModal from '@/components/ImagePickerModal.vue';

export default {
  components: { IonPage, IonContent, ImagePickerModal },
  setup() {
    const router = useRouter();
    const editMode = ref(false);
    const editIcon = ref(editIconPath);
    const saveIcon = ref(saveIconPath);
    const usernameError = ref(false);
    const showImageModal = ref(false);
    const activeTab = ref('info');
    const isLoading = ref(true);

    const tabs = [
      { id: 'info', name: 'Información' },
      { id: 'tournaments', name: 'Torneos' },
      { id: 'badges', name: 'Insignias' }
    ];

    const userData = ref({
      username: "Cargando...",
      location: "Cargando...",
      phoneNumber1: "Cargando...",
      phoneNumber2: "Cargando...",
      address: "Cargando...",
      description: "Cargando...",
      imageUrl: null
    });

    const originalData = ref({});

    const usernameSpan = ref(null);
    const locationSpan = ref(null);
    const phone1Span = ref(null);
    const phone2Span = ref(null);
    const addressSpan = ref(null);
    const descriptionSpan = ref(null);

    const inputWidth = ref(100);
    const locationWidth = ref(100);
    const phoneWidth = ref(100);
    const addressWidth = ref(100);
    const descriptionWidth = ref(100);

    const handleProfileImageClicker = () => {
      openImagePicker();
    };

    const openImagePicker = () => {
      showImageModal.value = true;
    };

    function onImageUploaded(newImageUrl) {
      userData.value.imageUrl = newImageUrl;
      originalData.value.imageUrl = newImageUrl;
    }

    const toggleEditMode = async () => {
      if (!editMode.value) {
        editMode.value = true;
        usernameError.value = false;
        nextTick(() => adjustWidth());
        return;
      }

      const token = localStorage.getItem('token');
      if (!token) {
        router.push('/auth/login');
        return;
      }

      const changesMade = JSON.stringify(userData.value) !== JSON.stringify(originalData.value);
      if (!changesMade) {
        editMode.value = false;
        return;
      }

      try {
        // Actualizamos información del organizador
        const response = await axios.put('http://localhost:8081/api/organizers/me', userData.value, {
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          }
        });
  
        if (response.status === 200) {
          originalData.value = { ...userData.value };
          editMode.value = false;
        }
      } catch (error) {
        console.error('Error al actualizar perfil', error);
      }
    };

    const phoneRegex = /^[679]\d{8}$/;
    const isPhoneValid = computed(() => {
      const phone = userData.value.phoneNumber1;
      return !phone || phoneRegex.test(phone);
    });

    const onPhoneInput = (event) => {
      let input = event.target.value.replace(/\D/g, '');
      if (input.length > 9) input = input.slice(0, 9);
      userData.value.phoneNumber1 = input;
      adjustWidth();
    };

    const onPhoneInput2 = (event) => {
      let input = event.target.value.replace(/\D/g, '');
      if (input.length > 9) input = input.slice(0, 9);
      userData.value.phoneNumber2 = input;
      adjustWidth();
    };

    const adjustWidth = () => {
      nextTick(() => {
        if (usernameSpan.value) inputWidth.value = usernameSpan.value.offsetWidth + 20;
        if (locationSpan.value) locationWidth.value = locationSpan.value.offsetWidth + 20;
        if (phone1Span.value) phoneWidth.value = phone1Span.value.offsetWidth + 20;
        if (addressSpan.value) addressWidth.value = addressSpan.value.offsetWidth + 20;
        if (descriptionSpan.value) descriptionWidth.value = descriptionSpan.value.offsetWidth + 20;
      });
    };

    const getUserData = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          router.push('/auth/login');
          return;
        }

        const response = await axios.get('http://localhost:8081/api/organizers/me', {
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          }
        });

        if (response.status === 200) {
          userData.value = response.data;
          originalData.value = { ...userData.value };
          adjustWidth();
          isLoading.value = false;
        }
      } catch (error) {
        console.error('Error al obtener datos del usuario', error);
        isLoading.value = false;
      }
    };

    onMounted(() => {
      getUserData();
    });

    return {
      userData,
      editMode,
      toggleEditMode,
      editIcon,
      saveIcon,
      usernameError,
      handleProfileImageClicker,
      onImageUploaded,
      showImageModal,
      defaultProfileImage,
      adjustWidth,
      usernameSpan,
      locationSpan,
      phone1Span,
      phone2Span,
      addressSpan,
      descriptionSpan,
      inputWidth,
      locationWidth,
      phoneWidth,
      addressWidth,
      descriptionWidth,
      onPhoneInput,
      onPhoneInput2,
      isPhoneValid,
      activeTab,
      isLoading,
      tabs
    };
  }
};
</script>

<style scoped>
/* Reset básico */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

ion-content {
  --background: #f9f5f0;
}
/* Contenedor principal */
.organizer-app {
  background-color: #f9f5f0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
  padding: 1rem 0;
}

/* Contenedor con ancho máximo */
.content-container {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 0 1rem;
}

/* Loader */
.loader-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  gap: 1rem;
}
.loader {
  border: 4px solid rgba(61, 90, 128, 0.2);
  border-top: 4px solid #3d5a80;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
}
.loader-text {
  color: #3d5a80;
  font-size: 1rem;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Header del perfil */
.profile-header {
  position: relative;
  margin-bottom: 2rem;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
}

.header-background {
  height: 150px;
  background: linear-gradient(135deg, #1a2841 0%, #3d5a80 100%);
}

.profile-header-content {
  position: relative;
  padding: 0 2rem 2rem;
  background-color: #e0e1dd;
  display: flex;
  flex-wrap: wrap;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 4px solid #e0e1dd;
  overflow: hidden;
  margin-top: -60px;
  position: relative;
  background-color: #fff;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.2);
  cursor: pointer;
  transition: transform 0.2s ease;
}

.profile-avatar:hover {
  transform: scale(1.05);
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.edit-avatar-icon {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background-color: #3d5a80;
  color: white;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e0e1dd;
}

.profile-info {
  flex: 1;
  padding: 1rem 0 0 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.username-container {
  position: relative;
}

.hidden-span {
  visibility: hidden;
  position: absolute;
  white-space: nowrap;
  font-size: inherit;
}

.username-display {
  font-size: 2rem;
  font-weight: 700;
  color: #1a2841;
  margin: 0;
}

.username-input {
  font-size: 2rem;
  font-weight: 700;
  background-color: rgba(255, 255, 255, 0.5);
  border: 2px solid #3d5a80;
  border-radius: 0.5rem;
  padding: 0.25rem 0.5rem;
  color: #1a2841;
  outline: none;
  min-width: 120px;
}

.username-input:focus {
  background-color: white;
  border-color: #1a2841;
  box-shadow: 0 0 0 2px rgba(61, 90, 128, 0.3);
}

.location-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background-color: rgba(61, 90, 128, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 2rem;
  color: #3d5a80;
  font-weight: 500;
  font-size: 0.875rem;
  width: fit-content;
}

.edit-profile-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: #3d5a80;
  color: #fff;
  border: none;
  border-radius: 2rem;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.edit-profile-button:hover {
  background-color: #1a2841;
}

/* Navegación por pestañas */
.tabs-navigation {
  display: flex;
  background-color: #1a2841;
  border-radius: 0.75rem;
  overflow: hidden;
  margin-bottom: 2rem;
}

.tab-button {
  flex: 1;
  padding: 1rem;
  background: none;
  border: none;
  color: #e0e1dd;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.tab-button:hover:not(.active) {
  background-color: rgba(61, 90, 128, 0.5);
}

.tab-button.active {
  background-color: #3d5a80;
}

/* Contenido de las pestañas */
.tab-content-container {
  margin-bottom: 3rem;
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* Secciones */
.section {
  background-color: #e0e1dd;
  border-radius: 1rem;
  padding: 1.5rem;
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
}

.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a2841;
  margin-bottom: 1.5rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid rgba(61, 90, 128, 0.3);
}

/* Contacto */
.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.contact-item {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  background-color: #fff;
  padding: 1rem;
  border-radius: 0.75rem;
  box-shadow: 0 2px 6px rgba(26, 40, 65, 0.08);
}

.contact-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: rgba(61, 90, 128, 0.1);
  border-radius: 50%;
  color: #3d5a80;
}

.contact-details {
  flex: 1;
}

.contact-details h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #1a2841;
  margin-bottom: 0.5rem;
}

.contact-value {
  color: #415a77;
  font-size: 1rem;
}

.field-wrapper {
  position: relative;
  width: 100%;
}

.edit-input {
  width: 100%;
  border: none;
  border-bottom: 1px solid #3d5a80;
  padding: 0.25rem 0;
  font-size: 1rem;
  color: #1a2841;
  background-color: transparent;
  outline: none;
}

.edit-input:focus {
  border-bottom: 2px solid #1a2841;
}

.location-select {
  width: 100%;
  border: 1px solid #3d5a80;
  border-radius: 0.5rem;
  padding: 0.5rem;
  font-size: 1rem;
  color: #1a2841;
  background-color: #fff;
  outline: none;
}

.error-message {
  color: #e63946;
  font-size: 0.75rem;
  margin-top: 0.25rem;
}

/* Descripción */
.description-container {
  background-color: #fff;
  padding: 1.5rem;
  border-radius: 0.75rem;
  box-shadow: 0 2px 6px rgba(26, 40, 65, 0.08);
}

.description-text {
  color: #415a77;
  line-height: 1.6;
  font-size: 1rem;
}

.description-textarea {
  width: 100%;
  border: 1px solid #3d5a80;
  border-radius: 0.5rem;
  padding: 0.75rem;
  font-size: 1rem;
  color: #1a2841;
  background-color: #fff;
  resize: vertical;
  min-height: 120px;
  outline: none;
  font-family: inherit;
}

.description-textarea:focus {
  border: 2px solid #1a2841;
  box-shadow: 0 0 0 2px rgba(61, 90, 128, 0.1);
}

/* Estadísticas */
.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.stat-card {
  background-color: #fff;
  padding: 1.5rem;
  border-radius: 0.75rem;
  text-align: center;
  box-shadow: 0 2px 6px rgba(26, 40, 65, 0.08);
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: #3d5a80;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #415a77;
  font-size: 0.875rem;
}

/* Estado vacío */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 1rem;
  color: #415a77;
  text-align: center;
}

.empty-state svg {
  color: #3d5a80;
  opacity: 0.5;
  margin-bottom: 1rem;
}

.empty-state p {
  font-size: 1.125rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.empty-state-subtext {
  font-size: 0.875rem;
  opacity: 0.8;
}

/* Botón crear torneo */
.create-tournament-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: #3d5a80;
  color: #fff;
  border: none;
  border-radius: 0.5rem;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.create-tournament-button:hover {
  background-color: #1a2841;
}

/* Responsive */
@media (max-width: 768px) {
  .profile-header-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding-bottom: 1rem;
  }
  
  .profile-info {
    padding: 1rem 0 0;
    align-items: center;
  }
  
  .edit-profile-button {
    position: relative;
    top: auto;
    right: auto;
    margin-top: 1rem;
  }
  
  .contact-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-container {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 1rem;
  }
  
  .create-tournament-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .tabs-navigation {
    flex-direction: column;
  }
  
  .tab-button {
    padding: 0.75rem;
  }
}
</style>