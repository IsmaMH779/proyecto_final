<template>
    <ion-page>
        <div class="profilepag-container">
            <div class="profile-header">
                <div class="profile-img-container"></div>

                <div class="username-wrapper">
                    <span ref="usernameSpan" class="hidden-span">{{ userData.username }}</span>
                    <input 
                        v-if="editMode" 
                        v-model="userData.username" 
                        class="custom-input username-text" 
                        :style="{ width: inputWidth + 'px' }"
                        @input="adjustWidth"
                    />
                    <p v-else class="username-text">{{ userData.username }}</p>
                </div>

                <button class="edit-profile-button" @click="toggleEditMode">
                    <img class="edit-profile-button-icon" :src="editMode ? saveIcon : editIcon">
                    <p class="edit-profile-button-text">{{ editMode ? 'Guardar cambios' : 'Editar perfil' }}</p>
                </button>
            </div>

            <div class="profile-content">
                <div>
                    <p class="profile-content-title">Informacion general</p>
                    <p class="profile-content-title-text">{{ userData.mail }}</p>
                    <p class="profile-content-title-text">{{ userData.location }}</p>
                    <p class="profile-content-title-text">Telefono</p>
                </div>

                <div>
                    <p class="profile-content-title">Informacion de torneo</p>
                    <p class="profile-content-title-text">torneos jugados</p>
                    <p class="profile-content-title-text">torneos ganados</p>
                </div>

                <div>
                    <p class="profile-content-title">Insignias</p>
                </div>
            </div>
        </div>
    </ion-page>
</template>
  
<script>
import { ref, onMounted, nextTick } from 'vue';
import { IonPage } from '@ionic/vue';
import axios from 'axios';

import editIconPath from '@/assets/profile_assets/profile_settings.svg';
import saveIconPath from '@/assets/profile_assets/saveCheck_icon.svg';

export default {
    components: { IonPage },
    setup() {
        const editMode = ref(false);
        const editIcon = ref(editIconPath);
        const saveIcon = ref(saveIconPath);
        const userData = ref({
            username: "Cargando...",
            mail: "Cargando...",
            location: "Cargando...",
            phoneNumber1: "Cargando...",
            birthdate: "Cargando..."
        });

        const usernameSpan = ref(null);
        const inputWidth = ref(100);

        const toggleEditMode = () => {
            editMode.value = !editMode.value;
            nextTick(() => adjustWidth()); // Ajustar el ancho al entrar en modo edición
        };

        const adjustWidth = () => {
            nextTick(() => {
                if (usernameSpan.value) {
                    inputWidth.value = usernameSpan.value.offsetWidth + 10; // 10px extra de margen
                }
            });
        };

        const getUserData = async () => {
            try {
                const token = localStorage.getItem("token");
                const response = await axios.get('http://localhost:8081/api/players/me', {
                    headers: { Authorization: `Bearer ${token}` }
                });

                userData.value = {
                    username: response.data.username,
                    mail: response.data.mail,
                    location: response.data.location,
                    phoneNumber1: response.data.phoneNumber1,
                    birthdate: response.data.birthdate,
                };

                adjustWidth();

            } catch (error) {
                console.error("Error al obtener datos del usuario", error);
            }
        };

        onMounted(() => {
            getUserData();
        });

        return { userData, editMode, toggleEditMode, editIcon, saveIcon, usernameSpan, inputWidth, adjustWidth };
    }
};
</script>
  
<style scoped>
.profilepag-container {
    background-color: #F5EFE7;
    height: 100vh;
    padding: 30px 30px;
}

/* Header del perfil */
.profile-header {
    border-bottom: 4px solid #1B263B;
    display: flex;
    align-items: center; /* Centra verticalmente el nombre de usuario */
    justify-content: space-between;
    padding: 0px 0px 20px 10px;
    position: relative; /* Necesario para posicionar el botón */
    height: 150px; 
}

.profile-img-container {
    height: 142.63px;
    width: 142.63px;
    border-radius: 132px;
    border: 3px solid #1B263B;
}

/* Contenedor del nombre de usuario */
.username-wrapper {
    display: flex;
    flex-grow: 1; /* Esto hace que ocupe el espacio disponible */
    align-items: center; /* Centra verticalmente el texto */
}

/* Estilo del texto del usuario */
.username-text {
    color: #1B263B;
    font-size: 32px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    margin-left: 50px;
}

/* Input editable */
.custom-input {
    border: none;
    border-bottom: 2px solid #1B263B;
    outline: none;
    background-color: transparent;
    font-size: 32px;
    font-weight: 400;
    color: #1B263B;
    min-width: 50px;
}

/* Span oculto para calcular el ancho del input */
.hidden-span {
    visibility: hidden;
    white-space: nowrap;
    font-size: 32px;
    font-weight: 400;
    position: absolute;
}

/* contenido del perfil */
.profile-content {
    display: flex;
    justify-content: space-between;
    width: 100%;
}

.profile-content-title {
    color: #1B263B;
    font-size: 32px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    border-bottom: 2px solid #1B263B;
    padding: 0px 10px;
    text-align: center;
}

.profile-content-title-text {
    color: #415A77;
    font-size: 25px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    padding: 0px 10px;
}

/* Edicion del perfil */
.edit-profile-button {
    margin-left: auto;
    display: flex;
    flex-direction: row;
    background-color: #415A77;
    border-radius: 50px;
    width: max-content;
    align-items: center;
    padding: 5px 20px;
    align-self: flex-end;
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
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    margin-left: 20px;
}

.edit-profile-button p {
    color: white;
    margin: 0;
}
</style>
