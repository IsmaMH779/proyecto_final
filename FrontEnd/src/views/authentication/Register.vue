<template>
  <ion-page>
    <div class="blue_container"></div>
    <div class="white_container">
      <img class="logo_image" src="@/assets/web_logo/Logo.svg" />
      
      <form @submit.prevent="registrarse">
        <div :class="[usernameError ? 'input_container_err' : 'input_container']">
          <img src="@/assets/input_assets/User.svg" />
          <input v-model="username" type="text" :placeholder="usernameErrorMessage" class="custom-input" />
        </div>
        <div :class="[emailError ? 'input_container_err' : 'input_container']">
          <img src="@/assets/input_assets/mail.svg" />
          <input v-model="email" type="text" :placeholder="emailErrorMessage" class="custom-input" />
        </div>

        <div :class="[locationError ? 'input_container_err' : 'input_container']">
          <img src="@/assets/input_assets/location.svg" style="height: 32px;" />
          <select v-model="location" class="custom-select">
            <option value="" disabled selected>Localidad</option>
            <option value="barcelona">Barcelona</option>
            <option value="girona">Girona</option>
            <option value="tarragona">Tarragona</option>
            <option value="lleida">Lleida</option>
          </select>
        </div>

        <div :class="[passwordError ? 'input_container_err' : 'input_container']">
          <img src="@/assets/input_assets/padlock.svg" />
          <input v-model="password" type="password" :placeholder="passwordError ? 'Contraseña requerida' : 'Contraseña'" class="custom-input" />
        </div>
        
        <div :class="[confirmPasswordError ? 'input_container_err' : 'input_container', 'button_separation']">
          <img src="@/assets/input_assets/padlock.svg" />
          <input v-model="confirmPassword" type="password" :placeholder="confirmPasswordError ? 'Las contraseñas no coinciden' : 'Repite la contraseña'" class="custom-input" />
        </div>     
        
        <button class="register_button" type="submit">Registrarse</button>
        <div class="login_redirect">
          ¿Ya tienes cuenta? <a href="/auth/login">Inicia sesión aquí</a>
        </div>
      </form>
    </div>

    <!-- MODAL DE CARGA -->
    <ion-modal :is-open="isLoading" class="transparent-modal">
      <div class="modal-content">
        <ion-spinner v-if="loading" name="crescent" style="color: #415A77"></ion-spinner>
        <div v-else class="success-message">
          <img src="@/assets/input_assets/success-check.svg" alt="Éxito" class="success-icon" />
          <p style="color: #415A77">¡Registro exitoso!</p>
        </div>
      </div>
    </ion-modal>
  </ion-page>
</template>

<script>
  import { IonPage, IonModal, IonSpinner } from '@ionic/vue';
  import { useRouter } from 'vue-router';
  import { ref, computed } from 'vue';
  import axios from 'axios';

  export default {
    name: 'RegisterPage',
    components: {
      IonPage,
      IonModal,
      IonSpinner
    },
    setup() {
      const router = useRouter();

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
      
      // Variables para mensajes de error
      const usernameExists = ref(false);
      const emailExists = ref(false);

      // Mensajes de error
      const usernameErrorMessage = computed(() => {
        if (usernameExists.value) return 'Usuario ya existente';
        if (usernameError.value) return 'Nombre de usuario requerido';
        return 'Nombre de usuario';
      });

      const emailErrorMessage = computed(() => {
        if (emailExists.value) return 'Correo electrónico ya existente';
        if (emailError.value) return 'Correo electrónico requerido';
        return 'Correo electrónico';
      });

      // Estado del modal - inicializado como falso para que no aparezca al cargar
      const isLoading = ref(false);
      const loading = ref(true);
      
      // Función para enviar el formulario
      const registrarse = async () => {
        // Resetear errores de existencia
        usernameExists.value = false;
        emailExists.value = false;
        
        // Validar campos
        if (validateFields() && verifyPass()) {
          // Mostrar modal de carga
          isLoading.value = true; 
          loading.value = true;

          const authServiceData = {
            username: username.value,
            email: email.value,
            password: password.value
          };

          const userServiceData = {
            username: username.value,
            mail: email.value,
            location: location.value
          };

          try {
            const authResponse = await axios.post('http://localhost:8080/api/auth/register', authServiceData);
            
            // Verificar la respuesta del servidor
            if (authResponse.status === 200) {
              const token = authResponse.data
              
              await axios.post(
                'http://localhost:8081/api/user-management/players/register', 
                userServiceData,
                {
                  headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                  }
                }
              );

              // mostrar check
              loading.value = false;
              setTimeout(() => {
                isLoading.value = false;
                // Redirigir al login después de 1.5 segundos
                router.push('/auth/login'); 
              }, 1500);
            } 
            else if (authResponse.status === 400 && authResponse.data === "USERNAME_FOUND") {
              // Manejar error de usuario existente
              usernameExists.value = true;
              usernameError.value = true;
              username.value = '';
              isLoading.value = false;
            } 
            else if (authResponse.status === 400 && authResponse.data === "EMAIL_FOUND") {
              // Manejar error de correo existente
              emailExists.value = true;
              emailError.value = true;
              email.value = '';
              isLoading.value = false;
            }
          } catch (error) {
            console.error('Error en el registro:', error.response || error.message);
            
            // Manejar errores específicos desde la respuesta del servidor
            if (error.response) {
              if (error.response.status === 400) {
                if (error.response.data === "USERNAME_FOUND") {
                  // Manejar error de usuario existente
                  usernameExists.value = true;
                  usernameError.value = true;
                  username.value = '';
                } else if (error.response.data === "EMAIL_FOUND") {
                  // Manejar error de correo existente
                  emailExists.value = true;
                  emailError.value = true;
                  email.value = '';
                }
              }
            }
            
            // Cerrar el modal en caso de error
            isLoading.value = false;
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
          return true;
        }

        // Vaciar los inputs y cambiar los placeholders
        password.value = '';
        confirmPassword.value = '';
        confirmPasswordError.value = true;
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
        usernameErrorMessage,
        emailErrorMessage,
        isLoading,
        loading
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
    margin-bottom: 70px;
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
    color: white;
    border: none;
    cursor: pointer;
  }

  .login_redirect {
    margin: auto;
    color: #415A77;
    margin-top: 25px;
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

  .success-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 18px;
    color: #0D1B2A;
  }

  .success-icon {
    width: 50px;
    height: 50px;
    margin-bottom: 10px;
  }
</style>