<template>
    <ion-page>
      <div class="profilepag-container">
        <div class="profile-header">
          <div 
            class="profile-img-container"
            :class="{ 'edit-highlight': editMode }"
            @click="handleProfileImageClicker"
          >
            <img 
              v-if="userData.imageUrl" 
              :src="'http://localhost:8081/images/profile/' + userData.imageUrl" 
            />
            <img v-else :src="defaultProfileImage" />
          </div>
  
          <div class="username-wrapper">
            <span ref="usernameSpan" class="hidden-span">{{ userData.username }}</span>
            <div class="username-field">
              <input 
                v-if="editMode" 
                v-model="userData.username" 
                class="custom-input username-text with-margin"
                :class="{ 'input-error': usernameError }"
                :style="{ width: inputWidth + 'px' }"
                @input="adjustWidth"
              />
              <p v-else class="username-text with-margin">{{ userData.username }}</p>
              <p v-if="editMode && usernameError" class="error-message">
                El nombre de usuario ya está en uso
              </p>
            </div>
          </div>
  
          <button class="edit-profile-button" @click="toggleEditMode">
            <img class="edit-profile-button-icon" :src="editMode ? saveIcon : editIcon">
            <p class="edit-profile-button-text">{{ editMode ? 'Guardar cambios' : 'Editar perfil' }}</p>
          </button>
        </div>
  
        <div class="profile-content">
          <div>
            <p class="profile-content-title">Información general</p>
            <!-- Ahora solo se muestra la localidad una única vez -->
            <div class="field-wrapper">
              <span ref="locationSpan" class="hidden-span">{{ userData.location }}</span>
              <select
                v-if="editMode"
                v-model="userData.location"
                class="custom-select profile-content-title-text with-margin"
              >
                <option value="" disabled selected>Localidad</option>
                <option value="barcelona">Barcelona</option>
                <option value="girona">Girona</option>
                <option value="tarragona">Tarragona</option>
                <option value="lleida">Lleida</option>
              </select>
              <p v-else class="profile-content-title-text with-margin">{{ userData.location }}</p>
            </div>
  
            <div class="field-wrapper">
              <span ref="phone1Span" class="hidden-span">{{ userData.phoneNumber1 || 'No hay teléfono registrado' }}</span>
              <input 
                v-if="editMode" 
                v-model="userData.phoneNumber1"
                class="custom-input profile-content-title-text with-margin"
                :style="{ width: phoneWidth + 'px' }"
                @input="onPhoneInput"
                :placeholder="'No hay teléfono registrado'"
                inputmode="numeric"
              />
              <p v-else class="profile-content-title-text with-margin">
                {{ userData.phoneNumber1 || 'No hay teléfono registrado' }}
              </p>
              <p v-if="editMode && userData.phoneNumber1 && !isPhoneValid" class="error-message">
                (debe tener 9 dígitos y empezar por 6, 7 o 9)
              </p>
            </div>
  
            <div class="field-wrapper">
              <span ref="phone2Span" class="hidden-span">{{ userData.phoneNumber2 || 'No hay teléfono registrado' }}</span>
              <input 
                v-if="editMode" 
                v-model="userData.phoneNumber2"
                class="custom-input profile-content-title-text with-margin"
                :style="{ width: phoneWidth + 'px' }"
                @input="onPhoneInput2"
                :placeholder="'No hay teléfono registrado'"
                inputmode="numeric"
              />
              <p v-else class="profile-content-title-text with-margin">
                {{ userData.phoneNumber2 || 'No hay teléfono registrado' }}
              </p>
            </div>
  
            <div class="field-wrapper">
              <span ref="addressSpan" class="hidden-span">{{ userData.address || 'No hay dirección registrada' }}</span>
              <input 
                v-if="editMode" 
                v-model="userData.address"
                class="custom-input profile-content-title-text with-margin"
                :style="{ width: addressWidth + 'px' }"
                :placeholder="'No hay dirección registrada'"
              />
              <p v-else class="profile-content-title-text with-margin">
                {{ userData.address || 'No hay dirección registrada' }}
              </p>
            </div>
  
            <div class="field-wrapper">
              <span ref="descriptionSpan" class="hidden-span">{{ userData.description || 'No hay descripción registrada' }}</span>
              <input 
                v-if="editMode" 
                v-model="userData.description"
                class="custom-input profile-content-title-text with-margin"
                :style="{ width: descriptionWidth + 'px' }"
                :placeholder="'No hay descripción registrada'"
              />
              <p v-else class="profile-content-title-text with-margin">
                {{ userData.description || 'No hay descripción registrada' }}
              </p>
            </div>
          </div>
  
          <div>
            <p class="profile-content-title">Información del torneo</p>
            <p class="profile-content-title-text with-margin">torneos organizados</p>
            <p class="profile-content-title-text with-margin">torneos en curso</p>
          </div>
  
          <div>
            <p class="profile-content-title">Insignias</p>
          </div>
        </div>
      </div>
  
      <!-- Image picker modal -->
      <ImagePickerModal :isOpen="showImageModal" profileRole="organizer" @close="showImageModal = false" @uploaded="onImageUploaded" />
    </ion-page>
  </template>
  
  <script>
  import { ref, onMounted, nextTick, computed } from 'vue';
  import { IonPage } from '@ionic/vue';
  import axios from 'axios';
  import editIconPath from '@/assets/profile_assets/profile_settings.svg';
  import saveIconPath from '@/assets/profile_assets/saveCheck_icon.svg';
  import { useRouter } from 'vue-router';
  import defaultProfileImage from '@/assets/profile_assets/default-profile-image.svg';
  import ImagePickerModal from '@/components/ImagePickerModal.vue';
  
  export default {
    components: { IonPage, ImagePickerModal },
    setup() {
      const router = useRouter();
      const editMode = ref(false);
      const editIcon = ref(editIconPath);
      const saveIcon = ref(saveIconPath);
      const usernameError = ref(false);
      const showImageModal = ref(false);
  
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
        if (editMode.value) openImagePicker();
      };
  
      const openImagePicker = () => {
        showImageModal.value = true;
      };
  
      function onImageUploaded(newImageUrl) {
        userData.value.imageUrl = newImageUrl;
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
          if (usernameSpan.value) inputWidth.value = usernameSpan.value.offsetWidth + 10;
          if (locationSpan.value) locationWidth.value = locationSpan.value.offsetWidth + 10;
          if (phone1Span.value) phoneWidth.value = phone1Span.value.offsetWidth + 10;
          if (addressSpan.value) addressWidth.value = addressSpan.value.offsetWidth + 10;
          if (descriptionSpan.value) descriptionWidth.value = descriptionSpan.value.offsetWidth + 10;
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
          }
        } catch (error) {
          console.error('Error al obtener datos del usuario', error);
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
        isPhoneValid
      };
    }
  };
  </script>
  
  <style scoped>
  .profilepag-container {
    background-color: #F5EFE7;
    height: 100vh;
    padding: 30px;
  }
  
  .profile-header {
    border-bottom: 4px solid #1B263B;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 10px 20px 0;
    position: relative;
    height: 150px;
  }
  
  .profile-img-container {
    height: 142.63px;
    width: 142.63px;
    border-radius: 132px;
    border: 3px solid #1B263B;
    overflow: hidden;
    cursor: pointer;
    transition: box-shadow 0.3s ease;
  }
  
  .profile-img-container img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .edit-highlight {
    box-shadow: 0 0 10px 4px rgba(65, 90, 119, 0.6);
  }
  
  .username-wrapper {
    display: flex;
    flex-grow: 1;
    align-items: center;
  }
  
  .username-field {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
  
  .username-text {
    color: #1B263B;
    font-size: 32px;
    font-weight: 400;
    margin-left: 50px;
  }
  
  .custom-input {
    border: none;
    border-bottom: 2px solid #1B263B;
    outline: none;
    background-color: transparent;
    font-size: 32px;
    font-weight: 400;
    color: #1B263B;
    min-width: 50px;
    width: 100%;
    padding: 5px 0;
  }
  
  .input-error {
    border-bottom: 2px solid red !important;
  }
  
  .custom-select {
    appearance: none;
    border: none;
    border-bottom: 2px solid #1B263B;
    background-color: transparent;
    font-size: 32px;
    font-weight: 400;
    color: #1B263B;
    min-width: 50px;
    width: 100%;
    padding: 5px 0;
  }
  
  .custom-select:focus {
    outline: none;
    border-color: #1B263B;
  }
  
  option {
    font-size: 32px;
    color: #1B263B;
    padding: 10px;
  }
  
  .hidden-span {
    visibility: hidden;
    white-space: nowrap;
    font-size: 32px;
    font-weight: 400;
    position: absolute;
  }
  
  .profile-content {
    display: flex;
    justify-content: space-between;
    width: 100%;
    margin-top: 20px;
  }
  
  .profile-content-title {
    color: #1B263B;
    font-size: 32px;
    font-weight: 400;
    border-bottom: 2px solid #1B263B;
    padding: 0 10px;
    text-align: center;
  }
  
  .profile-content-title-text {
    color: #415A77;
    font-size: 25px;
    font-weight: 400;
    padding: 0 10px;
  }
  
  .with-margin {
    display: block;
    margin: 25px 0;
  }
  
  .error-message {
    color: red;
    font-size: 14px;
    margin-top: -20px;
    margin-bottom: 20px;
    margin-left: 10px;
  }
  
  .edit-profile-button {
    margin-left: auto;
    display: flex;
    flex-direction: row;
    background-color: #415A77;
    border-radius: 50px;
    width: max-content;
    align-items: center;
    padding: 5px 20px;
    position: absolute;
    bottom: 0;
    right: 0;
    margin-bottom: 10px;
  }
  
  .edit-profile-button:hover {
    background-color: #1B263B;
  }
  
  .edit-profile-button-icon {
    margin-right: 10px;
  }
  
  .edit-profile-button-text {
    color: #FFF;
    text-align: center;
    font-size: 20px;
    margin-left: 20px;
  }
  
  .edit-profile-button p {
    margin: 0;
  }
  </style>
  