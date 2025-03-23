<template>
    <ion-page>
      <div class="blue_container"></div>
      <div class="white_container">
        <img class="logo_image" src="@/assets/web_logo/Logo.svg" />
        
        <form @submit.prevent="registrarse">
          <div :class="[usernameError ? 'input_container_err' : 'input_container']">
            <img src="@/assets/input_assets/User.svg" />
            <input v-model="username" type="text" :placeholder="usernameError ? 'Nombre de usuario requerido' : 'Nombre de usuario'" class="custom-input" />
          </div>
          <div :class="[emailError ? 'input_container_err' : 'input_container']">
            <img src="@/assets/input_assets/mail.svg" />
            <input v-model="email" type="text" :placeholder="emailError ? 'Correo electrónico requerido' : 'Correo electrónico'" class="custom-input" />
          </div>
  
          <div :class="[locationError ? 'input_container_err' : 'input_container']">
            <img src="@/assets/input_assets/location1.svg" style="height: 32px;" />
            <select v-model="location" class="custom-select">
              <option value="" disabled selected>Localidad</option>
              <option value="Barcelona">Barcelona</option>
              <option value="Girona">Girona</option>
              <option value="Tarragona">Tarragona</option>
              <option value="Lleida">Lleida</option>
            </select>
          </div>
  
          <div :class="[passwordError ? 'input_container_err' : 'input_container']">
            <img src="@/assets/input_assets/padlock.svg" />
            <input v-model="password" type="password" :placeholder="passwordError ? 'Contraseña requerida' : 'Contraseña'" class="custom-input" />
          </div>
          
          <div :class="[passwordError ? 'input_container_err' : 'input_container', 'button_separation']">
            <img src="@/assets/input_assets/padlock.svg" />
            <input v-model="confirmPassword" type="password" :placeholder="confirmPasswordError ? 'Repite la contraseña' : 'Las contraseñas no coinciden'" class="custom-input" />
          </div>     
          
          <button class="register_button" type="submit">Registrarse</button>
          <div class="login_redirect">
            ¿Ya tienes cuenta? <a href="/auth/login">Inicia sesión aquí</a>
          </div>
        </form>
      </div>
    </ion-page>
  </template>
  
  <script>
    import { IonPage } from '@ionic/vue';
    import { ref } from 'vue';
    import axios from 'axios';
  
    export default {
      name: 'RegisterPage',
      setup() {
        // Variables de los inputs
        const username = ref('');
        const email = ref('');
        const location = ref('');
        const password = ref('');
        const confirmPassword = ref('');
  
        // Variables de error
        const usernameError = ref(false);
        const emailError = ref(false);
        const locationError = ref(false);
        const passwordError = ref(false);
        const confirmPasswordError = ref(false);
  
        const passwordContainer = ref("input_container");
        const confirmPasswordPlaceholder = ref("Repite la contraseña");
  
        // Función para enviar el formulario
        const registrarse = async () => {
          // Validar campos
          if (validateFields() && verifyPass()) {
            const data = {
              email: email.value,
              password: password.value,
            };
  
            console.log(data);
            try {
              const response = await axios.post('http://localhost:8080/auth/register', data);
  
              // Verificar la respuesta del servidor
              if (response.status === 200) {
                console.log('Registro exitoso:', response.data);
                // Redirigir o mostrar mensaje de éxito
              }
            } catch (error) {
              console.error('Error en el registro:', error.response || error.message);
              // Manejar error (mostrar mensaje al usuario)
            }
          }
        };
  
        // Validar los campos requeridos
        const validateFields = () => {
          usernameError.value = !username.value;
          emailError.value = !email.value;
          locationError.value = !location.value;
          passwordError.value = !password.value;
          confirmPasswordError.value = !confirmPassword.value;
  
          return !usernameError.value && !emailError.value && !locationError.value && !passwordError.value && !confirmPasswordError.value;
        };
  
        // Verificar que las contraseñas sean iguales
        const verifyPass = () => {
          if (password.value === confirmPassword.value) {
              passwordContainer.value = "input_container"; 
              confirmPasswordPlaceholder.value = "Repite la contraseña";  // Si las contraseñas coinciden
              return true;
          }
  
          // Vaciar los inputs y cambiar los placeholders
          password.value = '';
          confirmPassword.value = '';
          passwordContainer.value = "input_container_err";  
          confirmPasswordPlaceholder.value = "Las contraseñas no coinciden"; // Si no coinciden
          return false;
        };
  
        return {
          username,
          email,
          location,
          password,
          confirmPassword,
          registrarse,
          usernameError,
          emailError,
          locationError,
          passwordError,
          confirmPasswordError,
          passwordContainer,
          confirmPasswordPlaceholder
        };
      }
    };
  </script>
  
  <style scoped>
    .custom-select {
      width: 100%;
      border: none;
      outline: none;
      font-size: 16px;
      color: #415A77;
      background-color: transparent;
      font-size: 20px;
      margin-left: 19px;
    }
  
    ion-page {
      display: flex;
      flex-direction: row;
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
      color: #415A77;
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
      border-bottom: 2px solid #415A77;
      padding-bottom: 10px;
      margin-bottom: 50px;
    }
  
    .input_container_err {
      width: 382px;
      display: flex;
      flex-direction: row;
      border-bottom: 2px solid #D94B4B;
      padding-bottom: 10px;
      margin-bottom: 50px;
    }
  
    .input_container_err .custom-input::placeholder {
      color: #D94B4B;
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
  