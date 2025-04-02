<template>
    <ion-page>
        <div class="profilepag-container">
        
            <div class="profile-header">
                <div class="profile-img-container">

                </div>
                <p class="username-text">{{ userData.username }}</p>
                <ion-button class="edit-profile-button">
                    <img src="@/assets/settings/profile_settings.svg">
                    <p>Editar perfil</p>
                </ion-button>
            </div>

            <div class="profile-content">
                <div>
                    <p class="profile-content-title">Informacion general</p>
                    <p class="profile-content-title-text">{{ userData.mail }}</p>
                    <p class="profile-content-title-text">{{ userData.location}}</p>
                    <p class="profile-content-title-text">Telefono</p>
                </div>

                <div>
                    <p class="profile-content-title">Informacion de torneo</p>
                    <p class="profile-content-title-text">torneos jugados</p>
                    <p class="profile-content-title-text">torneos ganados</p>
                </div >

                <div >
                    <p class="profile-content-title">Insignias</p>
                </div>
            </div>
        </div>
    </ion-page>
</template>
  
<script>
    import { ref, onMounted } from 'vue';
    import { IonPage, IonButton} from '@ionic/vue';
    import axios from 'axios';

    export default {
        components: { IonPage },
        setup() {
            const userData = ref({
                username: "Cargando...",
                mail: "Cargando...",
                location: "Cargando...",
                phoneNumber1: "Cargando...",
                birthdate: "Cargando..."
            });

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

                    console.log(userData.value);

                } catch (error) {
                    console.error("Error al obtener datos del usuario", error);
                }
            };

            onMounted(() => {
                getUserData();
            });

            return { userData };
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
        flex-direction: row;
        align-items: center;
        padding: 0px 0px 20px 10px;
     }

     .profile-img-container {
        height: 142.63px;
        width: 142.63px;
        border-radius: 132px;
        border: 3px solid #1B263B;
     }

     .username-text {
        color: #1B263B;
        font-size: 32px;
        font-style: normal;
        font-weight: 400;
        line-height: normal;
        margin-left: 50px;
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

     }

     .edit-profile-button p{
        color: white;
        margin: 0;
    }
</style> 