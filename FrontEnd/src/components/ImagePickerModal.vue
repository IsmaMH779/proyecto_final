<template>
  <ion-modal :is-open="isOpen" @didDismiss="close" class="image-picker-modal">
    <div class="modal-container">
      <div class="modal-header">
        <h2 class="modal-title">Seleccionar imagen</h2>
        <button class="close-button" @click="close">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>

      <div class="modal-content">
        <div class="image-preview-container">
          <div class="image-preview card" :class="{ 'has-image': preview }">
            <img v-if="preview" :src="preview" alt="Vista previa" />
            <div v-else class="no-image">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                <circle cx="8.5" cy="8.5" r="1.5"></circle>
                <polyline points="21 15 16 10 5 21"></polyline>
              </svg>
              <p>No hay imagen seleccionada</p>
            </div>
          </div>
        </div>

        <div class="file-input-container">
          <label for="file-upload" class="file-input-label">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
              <polyline points="17 8 12 3 7 8"></polyline>
              <line x1="12" y1="3" x2="12" y2="15"></line>
            </svg>
            Seleccionar archivo
          </label>
          <input
            id="file-upload"
            type="file"
            accept="image/*"
            @change="onFileSelected"
            class="file-input"
          />
          <p class="file-name" v-if="selectedFile">{{ selectedFile.name }}</p>
          <p class="file-size-limit">Tamaño máximo: 5MB</p>
        </div>

        <button 
          class="upload-button" 
          @click="uploadImage" 
          :disabled="!selectedFile"
          :class="{ 'disabled': !selectedFile }"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
            <polyline points="17 8 12 3 7 8"></polyline>
            <line x1="12" y1="3" x2="12" y2="15"></line>
          </svg>
          Subir Imagen
        </button>
      </div>
    </div>
  </ion-modal>
</template>

<script>
import axios from 'axios'
import { ref, computed } from 'vue'
import {
  IonModal,
} from '@ionic/vue'

export default {
  components: {
    IonModal,
  },
  props: {
    isOpen: {
      type: Boolean,
      required: true
    },
    profileRole: {
      type: String,
      default: 'player'
    }
  },
  emits: ['close', 'uploaded'],
  setup(props, { emit }) {
    const selectedFile = ref(null)
    const preview = ref(null)

    const uploadEndpoint = computed(() => {
      return props.profileRole === 'organizer'
        ? "http://localhost:8081/api/organizers/me/profile-picture"
        : "http://localhost:8081/api/players/me/profile-picture"
    })

    function onFileSelected(event) {
      const file = event.target.files[0]
      if (file && file.size <= 5 * 1024 * 1024) {
        selectedFile.value = file
        preview.value = URL.createObjectURL(file)
      } else {
        alert("La imagen no puede superar los 5MB")
      }
    }

    function uploadImage() {
      if (!selectedFile.value) return

      const formData = new FormData()
      formData.append("file", selectedFile.value)

      axios.post(uploadEndpoint.value, formData, {
        headers: {
          "Authorization": `Bearer ${localStorage.getItem('token')}`,
        }
      })
        .then(response => {
          emit("uploaded", response.data.imageUrl)
          close()
        })
        .catch(error => {
          console.error(error)
          alert("Error al subir la imagen")
        })
    }

    function close() {
      preview.value = null
      selectedFile.value = null
      emit("close")
    }

    return {
      selectedFile,
      preview,
      onFileSelected,
      uploadImage,
      close
    }
  }
}
</script>

<style scoped>
.image-picker-modal {
  --background: rgba(26, 40, 65, 0.4);
  --backdrop-opacity: 0.6;
}

.modal-container {
  background-color: #f9f5f0;
  border-radius: 1rem;
  overflow: hidden;
  max-width: 500px;
  width: 90%;
  margin: 10vh auto;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.modal-header {
  background-color: #1a2841;
  color: #e0e1dd;
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.close-button {
  background: none;
  border: none;
  color: #e0e1dd;
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.close-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.modal-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.image-preview-container {
  display: flex;
  justify-content: center;
}

.image-preview {
  width: 100%;
  height: 200px;
  border-radius: 0.75rem;
  overflow: hidden;
  background-color: #e0e1dd;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.image-preview:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(26, 40, 65, 0.15);
}

.image-preview.has-image {
  background-color: transparent;
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 0.5rem;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  color: #415a77;
}

.no-image p {
  margin: 0;
  font-size: 0.875rem;
}

.file-input-container {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.file-input {
  display: none;
}

.file-input-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  background-color: #e0e1dd;
  color: #1a2841;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.2s ease;
}

.file-input-label:hover {
  background-color: #d1d5db;
}

.file-name {
  font-size: 0.875rem;
  color: #415a77;
  margin: 0;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size-limit {
  font-size: 0.75rem;
  color: #415a77;
  margin: 0;
  text-align: center;
}

.upload-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  background-color: #3d5a80;
  color: #fff;
  border: none;
  border-radius: 0.5rem;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 0.5rem;
}

.upload-button:hover:not(.disabled) {
  background-color: #1a2841;
}

.upload-button.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .modal-container {
    width: 95%;
    margin: 5vh auto;
  }
  
  .image-preview {
    height: 150px;
  }
}
</style>