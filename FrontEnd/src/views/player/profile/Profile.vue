<template>
    <ion-page>
        <div class="profilepag-container">
        
            <div class="profile-header">
                <div class="profile-img-container">

                </div>
                <p class="username-text">USERNAME</p>
            </div>

            <div class="profile-content">

            </div>
        </div>
        
    </ion-page>
</template>
  
<script>
import { IonPage } from '@ionic/vue';


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
        
     }
</style> 