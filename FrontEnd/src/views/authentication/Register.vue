<template>
    <ion-page>
        <div class="blue_container"></div>
        <div class="white_container">
            <img class="logo_image" src="@/assets/web_logo/Logo.svg">
            
            <form>
                <div class="input_container">
                    <img src="@/assets/input_assets/User.svg">
                    <input type="text" placeholder="Nombre de usuario" class="custom-input"/>
                </div>
                <div class="input_container">
                    <img src="@/assets/input_assets/mail.svg">
                    <input type="text" placeholder="Correo electronico" class="custom-input"/>
                </div>
                
                <ion-list>
                    <ion-item>
                        <img src="@/assets/input_assets/location.svg">
                        <ion-select label="Localización" placeholder="Localización">
                            <ion-select-option value="1">Barcelona</ion-select-option>
                            <ion-select-option value="2">Tarragona</ion-select-option>
                            <ion-select-option value="3">Girona</ion-select-option>
                            <ion-select-option value="4">Lleida</ion-select-option>
                        </ion-select>
                    </ion-item>
                </ion-list>
                <div class="input_container">
                    <img src="@/assets/input_assets/padlock.svg">
                    <input type="text" placeholder="Contraseña" class="custom-input"/>
                </div> 
                <div class="input_container button_separation">
                    <img src="@/assets/input_assets/padlock.svg">
                    <input type="text" placeholder="Repite la contraseña" class="custom-input"/>
                </div>     
                <button class="register_button">Registrarse</button>
                <div class="login_redirect">
                    ¿Ya tienes cuenta? <a href="/auth/login">Inicia sesión aquí</a>
                </div>
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

    ion-list {
    background-color: transparent;
    }

    ion-item {
        --background: transparent;
    }

    .logo_image {
        margin-bottom: 70px;
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
        margin-bottom: 60px;
    }

    .register_button {
        background-color: #0D1B2A;
        width: 212px;
        height: 65px;
        border-radius: 50px;
        margin: auto;
        margin-top: 0px;
        font-size: 20px;    
    }
    .login_redirect {
        margin: auto;        
        color: #415A77;
        margin-top: 25px;
    }
</style>