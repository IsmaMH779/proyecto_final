<template>
    <ion-page>
        <div class="profilepag-container">
            <div class="profile-header">
                <div 
                    class="profile-img-container"
                    :class="{ 'edit-highlight': editMode }"
                    @click="handleProfileImageClicker"
                >
                <img v-if="userData.imageUrl" :src="'http://localhost:8081/images/profile/' + userData.imageUrl" />
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
                    <p class="profile-content-title-text with-margin">{{ userData.location }}</p>

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
        <ImagePickerModal :isOpen="showImageModal" @close="showImageModal = false" @uploaded="onImageUploaded" />
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
    components: { IonPage, ImagePickerModal},
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

        const inputWidth = ref(100);
        const locationWidth = ref(100);
        const phoneWidth = ref(100);
        const addressWidth = ref(100);
        const descriptionWidth = ref(100);

        const handleProfileImageClicker = () => {
            if (editMode.value) {
                openImagePicker();
            }
        };

        const openImagePicker = async () => {
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
                // Update organizer information
                const organizerResponse = await axios.put('http://localhost:8081/api/organizers/me', userData.value, {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`
                    }
                });

                if (organizerResponse.status === 200) {
                    originalData.value = { ...userData.value }; // save updated state
                    editMode.value = false;
                }
            } catch (error) {
                console.error('Error al actualizar perfil', error);
            }
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
                const token = localStorage.getItem('token');
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
                }
            } catch (error) {
                console.error('Error al obtener datos del usuario', error);
            }
        };

        const onPhoneInput = (event) => {
            const phoneNumber = event.target.value;
            const regex = /^[6-9]\d{8}$/;
            isPhoneValid.value = regex.test(phoneNumber);
        };

        const onPhoneInput2 = (event) => {
            const phoneNumber2 = event.target.value;
            const regex = /^[6-9]\d{8}$/;
            isPhoneValid2.value = regex.test(phoneNumber2);
        };

        onMounted(() => {
            getUserData();
        });

        return {
            editMode,
            userData,
            editIcon,
            saveIcon,
            toggleEditMode,
            showImageModal,
            handleProfileImageClicker,
            onImageUploaded,
            usernameError,
            adjustWidth,
            originalData,
            inputWidth,
            locationWidth,
            phoneWidth,
            addressWidth,
            descriptionWidth,
            getUserData,
            onPhoneInput,
            onPhoneInput2,
            defaultProfileImage
        };
    }
};
</script>
<style scoped>
.profilepag-container {
    display: flex;
    flex-direction: column;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.profile-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.profile-img-container {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #e0e0e0;
}

.profile-img-container img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.username-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding-left: 15px;
}

.username-text {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 5px;
}

.custom-input, .custom-select {
    width: 100%;
    padding: 8px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 5px;
    background-color: #fff;
}

.custom-input:focus, .custom-select:focus {
    border-color: #007aff;
    outline: none;
}

.field-wrapper {
    margin-bottom: 20px;
}

.profile-content-title {
    font-size: 16px;
    font-weight: 700;
    color: #555;
    margin-bottom: 5px;
}

.profile-content-title-text {
    font-size: 14px;
    color: #777;
}

.with-margin {
    margin-bottom: 15px;
}

.hidden-span {
    display: none;
}

.edit-profile-button {
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #007aff;
    color: #fff;
    padding: 8px 15px;
    border-radius: 5px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.edit-profile-button:hover {
    background-color: #005bb5;
}

.edit-profile-button-icon {
    width: 16px;
    height: 16px;
    margin-right: 10px;
}

.error-message {
    font-size: 12px;
    color: red;
    margin-top: 5px;
}

/* Responsive Styles */
@media (max-width: 768px) {
    .profile-header {
        flex-direction: column;
        align-items: center;
    }

    .profile-img-container {
        width: 80px;
        height: 80px;
    }

    .username-wrapper {
        align-items: center;
        padding-left: 0;
        padding-top: 10px;
    }

    .edit-profile-button {
        width: 100%;
        justify-content: center;
    }
}
</style>
