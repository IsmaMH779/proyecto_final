<template>
    <ion-page>
      <div class="blue_container"></div>
      <div class="white_container">
        <img class="logo_image" src="@/assets/web_logo/Logo.svg">
        
        <form @submit.prevent="iniciarSesion">
          <div :class="[emailError ? 'input_container_err' : 'input_container']">
            <img src="@/assets/input_assets/mail.svg">
            <input 
              v-model="email" 
              type="text" 
              :placeholder="emailErrorMessage" 
              class="custom-input"
            />
          </div>
          <div :class="[passwordError ? 'input_container_err' : 'input_container']">
            <img src="@/assets/input_assets/padlock.svg">
            <input 
              v-model="password" 
              type="password" 
              :placeholder="passwordErrorMessage" 
              class="custom-input"
            />
          </div> 
          <button class="login_button" type="submit">Iniciar sesión</button>
          <div class="register_redirect">
            ¿Aún no tienes cuenta? <a href="/auth/register">Registrate aquí</a>
          </div>
        </form>
      </div>
  
      <!-- MODAL DE CARGA -->
      <ion-modal :is-open="isLoading" class="transparent-modal">
        <div class="modal-content">
          <ion-spinner name="crescent" style="color: #415A77"></ion-spinner>
        </div>
      </ion-modal>
    </ion-page>
  </template>
    
  <script>
    import { IonPage, IonModal, IonSpinner } from '@ionic/vue';
    import { ref, computed } from 'vue';
    import { useRouter } from 'vue-router';
    import axios from 'axios';
  
    export default {
      name: 'LoginPage',
      components: {
        IonPage,
        IonModal,
        IonSpinner
      },
      setup() {
        const router = useRouter();
  
        // Variables de los inputs
        const email = ref('');
        const password = ref('');
  
        // Variables de error
        const emailError = ref(false);
        const passwordError = ref(false);
        const emailNotFound = ref(false);
        const incorrectPassword = ref(false);
  
        // Estado del modal de carga
        const isLoading = ref(false);
  
        // Mensajes de error
        const emailErrorMessage = computed(() => {
          if (emailNotFound.value) return 'Correo electrónico no encontrado';
          if (emailError.value) return 'Correo electrónico requerido';
          return 'Correo electrónico';
        });
  
        const passwordErrorMessage = computed(() => {
          if (incorrectPassword.value) return 'Contraseña incorrecta';
          if (passwordError.value) return 'Contraseña requerida';
          return 'Contraseña';
        });
  
        // Función para iniciar sesión
        const iniciarSesion = async () => {
          // Resetear errores
          emailError.value = false;
          passwordError.value = false;
          emailNotFound.value = false;
          incorrectPassword.value = false;
  
          // Validar campos
          let isValid = true;
  
          if (!email.value.trim()) {
            emailError.value = true;
            isValid = false;
          }
  
          if (!password.value.trim()) {
            passwordError.value = true;
            isValid = false;
          }
  
          if (!isValid) {
            return;
          }
  
          // Mostrar modal de carga
          isLoading.value = true;
  
          try {
            const response = await axios.post('http://localhost:8080/api/auth/login', {
              email: email.value,
              password: password.value
            });
  
            // Si la respuesta es exitosa
            if (response.status === 200) {
              // Guardar el token si es necesario
              const token = response.data;
              localStorage.setItem('token', token);
              
              // Redirigir al usuario a la página principal
              //todo
              router.push('/');
            }
          } catch (error) {
            console.error('Error en el inicio de sesión:', error.response || error.message);
            
            // Manejar errores
            if (error.response) {
              if (error.response.status === 400) {
                if (error.response.data === "EMAIL_NOT_FOUND") {
                  emailNotFound.value = true;
                  emailError.value = true;
                  email.value = '';
                } else if (error.response.data === "INCORRECT_PASSWORD") {
                  incorrectPassword.value = true;
                  passwordError.value = true;
                  password.value = '';
                }
              }
            }
          } finally {
            // Ocultar modal de carga
            isLoading.value = false;
          }
        };
  
        return {
          email,
          password,
          emailError,
          passwordError,
          emailErrorMessage,
          passwordErrorMessage,
          iniciarSesion,
          isLoading
        };
      }
    }
  </script>
    
  <style scoped>
    ion-page {
      display: flex !important;
      flex-direction: row !important;
      --background: transparent !important;
      background: transparent !important;
    }
  
    ::v-deep(.ion-page) {
      display: flex !important;
      flex-direction: row !important;
      background: transparent !important;
    }
  
    .logo_image {
      margin-bottom: 150px;
    }
  
    .blue_container {
      height: 100vh;
      width: 30%;
      background-color: #0D1B2A;
      position: fixed;
      left: 0;
      top: 0;
      z-index: 0;
    }
    
    .white_container {
      height: 100vh;
      width: 70%;
      background-color: #E0E1DD;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      position: fixed;
      right: 0;
      top: 0;
      z-index: 0;
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
      margin-bottom: 80px;
    }
  
    .input_container_err {
      width: 382px;
      display: flex;
      flex-direction: row;
      border-bottom: 2px solid #D94B4B;
      padding-bottom: 10px;
      margin-bottom: 80px;
    }
  
    .input_container_err .custom-input::placeholder {
      color: #D94B4B;
    }
  
    .login_button {
      background-color: #0D1B2A;
      width: 212px;
      height: 65px;
      border-radius: 50px;
      margin: auto;
      margin-top: 80px;
      margin-bottom: 10px;
      font-size: 20px;
      color: white;
      border: none;
      cursor: pointer;
    }
  
    .register_redirect {
      margin: auto;        
      color: #415A77;
      margin-top: 25px;
      margin-bottom: 40px;
    }
  
    /* Modal style */
    .transparent-modal {
      --background: black !important;
    }
  
    .transparent-modal::part(content) {
      background: transparent !important;
      box-shadow: none !important;
    }
  
    .modal-content {
      width: 300px;
      height: 200px;
      background: white;
      border-radius: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      padding: 20px;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
      margin: auto;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      z-index: 1000;
    }
  </style>
  
  