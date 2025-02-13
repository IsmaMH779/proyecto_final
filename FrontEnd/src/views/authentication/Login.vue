<template>
    <ion-page>
        <div class="blue_container"></div>
        <div class="white_container">
            <img class="logo_image" src="@/assets/web_logo/Logo.svg">
            
            <form>
                <div class="input_container">
                    <img src="@/assets/input_assets/mail.svg">
                    <input type="text" placeholder="Correo electronico" class="custom-input"/>
                </div>
                <div class="input_container">
                    <img src="@/assets/input_assets/padlock.svg">
                    <input type="text" placeholder="Contraseña" class="custom-input"/>
                </div> 
                <div class="input_container button_separation">
                    <img src="@/assets/input_assets/padlock.svg">
                    <input type="text" placeholder="Repite la contraseña" class="custom-input"/>
                </div>     
                <button class="login_button">Iniciar sesión</button>
            </form>
        </div>
    </ion-page>
</template>
  
<script>
    import { IonPage } from '@ionic/vue';
</script>
  
<style scoped>
    ion-page {
        display: flex;
        flex-direction: row;
    }

    .logo_image {
        margin-bottom: 150px;
    }

    .blue_container {
        height: 100vh;
        width: 30%;
        background-color: #0D1B2A;
    }
    .white_container {
        height: 100vh;
        width: 70%;
        background-color: #E0E1DD;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
    }

    form {
        display: flex;
        flex-direction: column;
    }

    .custom-input {
        width: 100%;
        border: none;
        outline: none;
        font-size: 16px;
        color: #415A77; /* Color del texto */
        background-color: transparent;
        margin-left: 19px;
        font-size: 20px;
    }

    .custom-input::placeholder {
        color: #415A77;
        opacity: 1;
    }

    .input_container {
        width: 382px;
        display: flex;
        flex-direction: row;
        border-bottom:2px solid #415A77;
        padding-bottom: 10px;
        margin-bottom: 50px;
    }

    .button_separation {
        margin-bottom: 106px;
    }

    .login_button {
        background-color: #0D1B2A;
        width: 212px;
        height: 65px;
        border-radius: 50px;
        margin: auto;
        font-size: 20px;    
        }
</style>