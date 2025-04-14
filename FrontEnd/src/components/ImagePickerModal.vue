<template>
  <ion-modal :is-open="isOpen" @didDismiss="close">
    <ion-content class="ion-padding">
      <ion-header>
        <ion-toolbar>
          <ion-title>Seleccionar imagen</ion-title>
          <ion-buttons slot="end">
            <ion-button @click="close">Cerrar</ion-button>
          </ion-buttons>
        </ion-toolbar>
      </ion-header>

      <div class="image-preview">
        <img v-if="preview" :src="preview" style="max-width: 100%; border-radius: 10px;" />
      </div>

      <input
        type="file"
        accept="image/*"
        @change="onFileSelected"
        style="margin-top: 20px;"
      />

      <ion-button expand="block" color="primary" @click="uploadImage" :disabled="!selectedFile">
        Subir Imagen
      </ion-button>
    </ion-content>
  </ion-modal>
</template>

<script>
import axios from 'axios'
import { ref } from 'vue'
import {
  IonModal,
  IonContent,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonButtons,
  IonButton
} from '@ionic/vue'

export default {
  components: {
    IonModal,
    IonContent,
    IonHeader,
    IonToolbar,
    IonTitle,
    IonButtons,
    IonButton
  },
  props: {
    isOpen: Boolean
  },
  emits: ['close', 'uploaded'],
  setup(props, { emit }) {
    const selectedFile = ref(null)
    const preview = ref(null)

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

      axios.post("http://localhost:8081/api/players/me/profile-picture", formData, {
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
.image-preview {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>