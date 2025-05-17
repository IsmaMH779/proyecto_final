<template>
  <ion-page>
    <ion-content class="ion-padding-0" :scroll-y="true">
      <div class="landing-page">
        <!-- Navbar -->
        <nav class="navbar">
          <div class="container">
            <div class="logo">
              DECKLY
            </div>
            <div class="auth-buttons">
              <button class="btn-login" @click="showLoginModal = true">Iniciar Sesión</button>
              <button class="btn-register" @click="showRegisterModal = true">Registrarse</button>
            </div>
          </div>
        </nav>

        <!-- Hero Section -->
        <section class="hero">
          <div class="hero-bg"></div>
          <div class="container">
            <div class="hero-content">
              <h1 v-motion :initial="{ opacity: 0, y: 100 }" :enter="{ opacity: 1, y: 0 }">
                El futuro de los <span>torneos TCG</span> está aquí
              </h1>
              <p v-motion :initial="{ opacity: 0 }" :enter="{ opacity: 1, transition: { delay: 200 } }">
                Una plataforma completa para jugadores y organizadores
              </p>
            </div>
            <div class="hero-cards" v-motion :initial="{ opacity: 0 }" :enter="{ opacity: 1, transition: { delay: 600 } }">
              <div class="card card-1"></div>
              <div class="card card-2"></div>
              <div class="card card-3"></div>
            </div>
          </div>
          <div class="hero-wave">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320">
              <path fill="#f9f5f0" fill-opacity="1" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,149.3C960,160,1056,160,1152,138.7C1248,117,1344,75,1392,53.3L1440,32L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path>
            </svg>
          </div>
        </section>

        <!-- Roles Section -->
        <section class="roles">
          <div class="container">
            <div class="section-header" v-motion :initial="{ opacity: 0, y: 50 }" :enter="{ opacity: 1, y: 0 }">
              <h2>Dos experiencias, una plataforma</h2>
              <p>Elige tu rol y descubre todas las funcionalidades diseñadas para ti</p>
            </div>
            
            <div class="roles-tabs">
              <div class="tabs-header">
                <button 
                  class="tab-button" 
                  :class="{ active: activeTab === 'organizer' }" 
                  @click="activeTab = 'organizer'"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
                  </svg>
                  <span>Organizador</span>
                </button>
                <button 
                  class="tab-button" 
                  :class="{ active: activeTab === 'player' }" 
                  @click="activeTab = 'player'"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                  </svg>
                  <span>Jugador</span>
                </button>
              </div>
              
              <div class="tab-content">
                <!-- Organizer Tab -->
                <div v-if="activeTab === 'organizer'" class="tab-pane" v-motion :initial="{ opacity: 0 }" :enter="{ opacity: 1 }">
                  <div class="tab-grid">
                    <div class="tab-image">
                      <img src="@/assets/landing/landing_home.png" alt="Organizador TCG" />
                    </div>
                    <div class="tab-info">
                      <h3>Para Organizadores</h3>
                      <p>Crea y gestiona torneos de TCG con herramientas diseñadas para simplificar todo el proceso.</p>
                      
                      <div class="features-list">
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                              <circle cx="9" cy="7" r="4"></circle>
                              <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Gestión de Participantes</h4>
                            <p>Controla inscripciones, asistencia y datos de los jugadores en un solo lugar.</p>
                          </div>
                        </div>
                        
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                              <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Formatos Personalizables</h4>
                            <p>Crea torneos con diferentes formatos: eliminación directa, suizo, round robin y más.</p>
                          </div>
                        </div>
                        
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <path d="M12 20v-6M6 20V10M18 20V4"></path>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Estadísticas en Tiempo Real</h4>
                            <p>Visualiza datos importantes sobre tus torneos y participantes.</p>
                          </div>
                        </div>
                        
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                              <line x1="16" y1="2" x2="16" y2="6"></line>
                              <line x1="8" y1="2" x2="8" y2="6"></line>
                              <line x1="3" y1="10" x2="21" y2="10"></line>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Programación de Eventos</h4>
                            <p>Organiza tu calendario de torneos y notifica automáticamente a los participantes.</p>
                          </div>
                        </div>
                      </div>
                      
                      <button class="btn-role">Solicitud para ser organizador</button>
                    </div>
                  </div>
                </div>
                
                <!-- Player Tab -->
                <div v-if="activeTab === 'player'" class="tab-pane" v-motion :initial="{ opacity: 0 }" :enter="{ opacity: 1 }">
                  <div class="tab-grid">
                    <div class="tab-image">
                      <img src="@/assets/landing/landing_tournament.png" alt="Jugador TCG" />
                    </div>
                    <div class="tab-info">
                      <h3>Para Jugadores</h3>
                      <p>Encuentra torneos, compite y lleva un seguimiento de tu progreso en tus juegos favoritos.</p>
                      
                      <div class="features-list">
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <circle cx="12" cy="12" r="10"></circle>
                              <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"></polygon>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Descubre Torneos</h4>
                            <p>Encuentra torneos cercanos a tu ubicación o en línea para tus juegos favoritos.</p>
                          </div>
                        </div>
                        
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"></path>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Inscripción Sencilla</h4>
                            <p>Inscríbete en torneos con un solo clic y recibe notificaciones importantes.</p>
                          </div>
                        </div>
                        
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <path d="M12 20v-6M6 20V10M18 20V4"></path>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Seguimiento de Estadísticas</h4>
                            <p>Visualiza tu historial de partidas, victorias y evolución como jugador.</p>
                          </div>
                        </div>
                        
                        <div class="feature-item">
                          <div class="feature-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <circle cx="12" cy="8" r="7"></circle>
                              <polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"></polyline>
                            </svg>
                          </div>
                          <div class="feature-text">
                            <h4>Insignias y Reconocimientos</h4>
                            <p>Gana insignias por tus logros y construye tu reputación en la comunidad.</p>
                          </div>
                        </div>
                      </div>
                      
                      <button class="btn-role" @click="showRegisterModal = true">Comenzar como Jugador</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- How It Works -->
        <section class="how-it-works" v-motion :initial="{ opacity: 0, y: 50 }" :enter="{ opacity: 1, y: 0 }">
          <div class="container">
            <div class="section-header">
              <h2>Cómo funciona</h2>
              <p>Comienza a utilizar nuestra plataforma en tres sencillos pasos</p>
            </div>
            
            <div class="steps">
              <div class="step">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h3>Crea tu cuenta</h3>
                  <p>Regístrate en nuestra plataforma y elige tu rol: organizador o jugador.</p>
                </div>
              </div>
              
              <div class="step-connector"></div>
              
              <div class="step">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h3>Configura tu perfil</h3>
                  <p>Completa tu información y personaliza tu experiencia según tus preferencias.</p>
                </div>
              </div>
              
              <div class="step-connector"></div>
              
              <div class="step">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h3>¡Comienza a jugar!</h3>
                  <p>Crea o únete a torneos y disfruta de todas las funcionalidades de la plataforma.</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Get Started -->
        <section class="get-started" v-motion :initial="{ opacity: 0 }" :enter="{ opacity: 1 }">
          <div class="get-started-bg"></div>
          <div class="container">
            <div class="get-started-content">
              <h2>Comienza tu aventura TCG hoy mismo</h2>
              <p>Únete a nuestra comunidad y lleva tu experiencia de juego al siguiente nivel</p>
              <button class="btn-primary" @click="showRegisterModal = true">Comenzar Ahora</button>
            </div>
          </div>
        </section>

        <!-- Footer -->
        <footer class="footer">
          <div class="container">
            <div class="footer-top">
              <div class="footer-logo">
                <img src="https://placehold.co/40x120" alt="TCG Logo" />
                <p>La plataforma definitiva para torneos de TCG</p>
              </div>
              
              <div class="footer-nav">
                <div class="footer-nav-column">
                  <h4>Plataforma</h4>
                  <ul>
                    <li><a href="#">Características</a></li>
                    <li><a href="#">Torneos</a></li>
                    <li><a href="#">Comunidad</a></li>
                    <li><a href="#">Soporte</a></li>
                  </ul>
                </div>
                
                <div class="footer-nav-column">
                  <h4>Recursos</h4>
                  <ul>
                    <li><a href="#">Guías</a></li>
                    <li><a href="#">Blog</a></li>
                    <li><a href="#">FAQ</a></li>
                    <li><a href="#">Contacto</a></li>
                  </ul>
                </div>
                
                <div class="footer-nav-column">
                  <h4>Legal</h4>
                  <ul>
                    <li><a href="#">Términos</a></li>
                    <li><a href="#">Privacidad</a></li>
                    <li><a href="#">Cookies</a></li>
                  </ul>
                </div>
              </div>
            </div>
            
            <div class="footer-bottom">
              <p>&copy; 2025 TCG Platform. Todos los derechos reservados.</p>
              <div class="social-links">
                <a href="#" aria-label="Twitter">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z"></path>
                  </svg>
                </a>
                <a href="#" aria-label="Instagram">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
                    <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
                    <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line>
                  </svg>
                </a>
              </div>
            </div>
          </div>
        </footer>

        <!-- Login Modal -->
  <div class="modal" v-if="showLoginModal">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Iniciar Sesión</h3>
        <button class="close-button" @click="closeModal">×</button>
      </div>

      <form @submit.prevent="login">
        <div :class="['form-group', emailError ? 'has-error' : '']">
          <label for="email">Correo electrónico</label>
          <input
            type="email"
            id="email"
            v-model="loginForm.email"
            :placeholder="emailErrorMessage"
            class="form-control"
          />
        </div>

        <div :class="['form-group', passwordError ? 'has-error' : '']">
          <label for="password">Contraseña</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            :placeholder="passwordErrorMessage"
            class="form-control"
          />
        </div>

        <button type="submit" class="btn-primary" :disabled="isLoading">
          <span v-if="!isLoading">Iniciar Sesión</span>
          <span v-else>
            <i class="spinner-icon"></i> Cargando...
          </span>
        </button>
      </form>

      <div class="modal-footer">
        <p style="color: black;">¿Aún no tienes cuenta?
          <a href="#" @click.prevent="switchToRegister">Regístrate aquí</a>
        </p>
      </div>
    </div>
  </div>

        <!-- Register Modal -->
  <div class="modal" v-if="showRegisterModal">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Registrarse</h3>
        <button class="close-button" @click="closeModal">×</button>
      </div>

      <form @submit.prevent="register">
        <div :class="['form-group', usernameError ? 'has-error' : '']">
          <label for="reg-username">Nombre de usuario</label>
          <input
            type="text"
            id="reg-username"
            v-model="registerForm.username"
            :placeholder="usernameErrorMessage"
            class="form-control"
          />
        </div>

        <div :class="['form-group', emailError ? 'has-error' : '']">
          <label for="reg-email">Correo electrónico</label>
          <input
            type="email"
            id="reg-email"
            v-model="registerForm.email"
            :placeholder="emailErrorMessage"
            class="form-control"
          />
        </div>

        <!-- Campo de Localidad adaptado -->
      <div :class="['form-group', locationError ? 'has-error' : '']">
        <label for="reg-location">Localidad</label>
        <div class="location-select-container">
          <!-- Input de búsqueda -->
          <input
            id="reg-location"
            type="text"
            v-model="search"
            @input="showSuggestions = true"
            @blur="setTimeout(() => showSuggestions = false, 200)"
            class="form-control"
            autocomplete="off"
            placeholder="Buscar localidad"
          />
          
          <!-- Contenedor de sugerencias -->
          <div class="suggestions-container" v-if="showSuggestions && filteredProvinces.length">
            <ul class="suggestions-list">
              <li
                v-for="prov in filteredProvinces"
                :key="prov.code"
                @click="selectProvince(prov.name)"
                class="suggestion-item"
              >
                {{ prov.name }}
              </li>
            </ul>
          </div>
        </div>
      </div>

        <div :class="['form-group', passwordError ? 'has-error' : '']">
          <label for="reg-password">Contraseña</label>
          <input
            type="password"
            id="reg-password"
            v-model="registerForm.password"
            placeholder="Contraseña"
            class="form-control"
          />
        </div>

        <div :class="['form-group', confirmPasswordError ? 'has-error' : '']">
          <label for="reg-confirm-password">Confirmar contraseña</label>
          <input
            type="password"
            id="reg-confirm-password"
            v-model="registerForm.confirmPassword"
            placeholder="Confirmar contraseña"
            class="form-control"
          />
        </div>

        <button type="submit" class="btn-primary" :disabled="isLoading">
          <span v-if="!isLoading">Registrarse</span>
          <span v-else><i class="spinner-icon"></i> Cargando...</span>
        </button>
      </form>

      <div class="modal-footer">
        <p style="color: black;">¿Ya tienes cuenta?
          <a href="#" @click.prevent="switchToLogin">Inicia sesión aquí</a>
        </p>
      </div>
    </div>
  </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import provincias from '@/assets/data/location/locations.json';
import { useRouter } from 'vue-router'
import axios from 'axios'
import { 
  IonPage, 
  IonContent 
} from '@ionic/vue';

// Tab state
const activeTab = ref('organizer');

// Router para redirección post-login
const router = useRouter()

// Modal state
const showLoginModal = ref(false);
const showRegisterModal = ref(false);


// Form data
const loginForm = ref({
  email: '',
  password: ''
});

const registerForm = ref({
  username: '',
  email: '',
  location: '',
  password: '',
  confirmPassword: ''
});

// Búsqueda de provincias y el modelo
const search = ref("");
const showSuggestions = ref(false);
const provinces = ref([]);
const filteredProvinces = computed(() => {
  if (!search.value) return [];
  return provinces.value.filter((prov) =>
    prov.name.toLowerCase().includes(search.value.toLowerCase())
  );
});

const selectProvince = (name) => {
  search.value = name;
  registerForm.location = name;  // Asigna el valor seleccionado
  showSuggestions.value = false; // Oculta las sugerencias
};



// Flags de error
const emailError = ref(false)
const passwordError = ref(false)
const emailNotFound = ref(false)
const incorrectPassword = ref(false)

const usernameError = ref(false)
const locationError = ref(false)
const confirmPasswordError = ref(false)
const usernameExists = ref(false)
const emailExists = ref(false)

// Computed para mensajes de placeholder
const emailErrorMessage = computed(() => {
  if (emailNotFound.value)    return 'Correo electrónico no encontrado'
  if (emailError.value)       return 'Correo electrónico requerido'
  return 'Correo electrónico'
})
const passwordErrorMessage = computed(() => {
  if (incorrectPassword.value) return 'Contraseña incorrecta'
  if (passwordError.value)     return 'Contraseña requerida'
  return 'Contraseña'
})
const usernameErrorMessage = computed(() => {
  if (usernameExists.value) return 'Usuario ya existente'
  if (usernameError.value)  return 'Nombre de usuario requerido'
  return 'Nombre de usuario'
})

// Loading
const isLoading = ref(false)
const loadingSuccess = ref(false)

// Validaciones
function validateFields() {
  usernameError.value        = !registerForm.value.username.trim()
  emailError.value           = !registerForm.value.email.trim()
  locationError.value        = !registerForm.value.location
  passwordError.value        = !registerForm.value.password
  confirmPasswordError.value = !registerForm.value.confirmPassword
  return !(
    usernameError.value ||
    emailError.value ||
    locationError.value ||
    passwordError.value ||
    confirmPasswordError.value
  )
}

function verifyPasswordsMatch() {
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    confirmPasswordError.value = true
    registerForm.value.password = ''
    registerForm.value.confirmPassword = ''
    return false
  }
  return true
}


const login = async () => {
  // reset errores
  emailError.value = false
  passwordError.value = false
  emailNotFound.value = false
  incorrectPassword.value = false

  // validación simple
  let valid = true
  if (!loginForm.value.email.trim()) {
    emailError.value = true
    valid = false
  }
  if (!loginForm.value.password.trim()) {
    passwordError.value = true
    valid = false
  }
  if (!valid) return

  isLoading.value = true
  try {
    // petición de autenticación
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      email: loginForm.value.email,
      password: loginForm.value.password
    })
    localStorage.setItem('token', res.data)

    // petición rol
    const roleRes = await axios.get('http://localhost:8080/api/auth/me/role', {
      headers: { Authorization: `Bearer ${res.data}` }
    })
    if (roleRes.status === 200) {
      localStorage.setItem('role', roleRes.data)
    }

    // cerrar modal y redirigir
    closeModal()
    router.push('/web')
  } catch (err) {
    const status = err.response?.status
    const data = err.response?.data
    if (status === 400) {
      if (data === 'EMAIL_NOT_FOUND') {
        emailNotFound.value = true
        emailError.value = true
        loginForm.value.email = ''
      }
      else if (data === 'INCORRECT_PASSWORD') {
        incorrectPassword.value = true
        passwordError.value = true
        loginForm.value.password = ''
      }
    } else {
      console.error('Error en el login:', err)
    }
  } finally {
    isLoading.value = false
  }
}

async function register() {
  // resetear errores de existencia
  usernameExists.value = emailExists.value = false

  if (!validateFields() || !verifyPasswordsMatch()) {
    return
  }

  isLoading.value = true

  try {
    // Registro en auth service
    const authRes = await axios.post(
      'http://localhost:8080/api/auth/register',
      {
        username: registerForm.value.username,
        email: registerForm.value.email,
        password: registerForm.value.password
      }
    )

    const token = authRes.data
    // Registro en user-management
    await axios.post(
      'http://localhost:8081/api/user-management/players/register',
      {
        username: registerForm.value.username,
        mail: registerForm.value.email,
        location: registerForm.value.location
      },
      { headers: { Authorization: `Bearer ${token}` } }
    )

    // Mostrar éxito y redirigir al login
    loadingSuccess.value = true
    setTimeout(() => {
      closeModal()
      router.push('/landing/home')
    }, 1500)

  } catch (err) {
    const status = err.response?.status
    const data = err.response?.data

    if (status === 400) {
      if (data === 'USERNAME_FOUND') {
        usernameExists.value = usernameError.value = true
        registerForm.value.username = ''
      } else if (data === 'EMAIL_FOUND') {
        emailExists.value = emailError.value = true
        registerForm.value.email = ''
      }
    } else {
      console.error('Error en el registro:', err)
    }
  } finally {
    isLoading.value = false
  }
}

// Funciones de control
function closeModal() {
  showRegisterModal.value = false
  showLoginModal.value = false
  resetForm()
}

function resetForm() {
  loginForm.value.email = ''
  loginForm.value.password = ''
  emailError.value = false
  passwordError.value = false
  emailNotFound.value = false
  incorrectPassword.value = false

  registerForm.value = {
    username: '',
    email: '',
    location: '',
    password: '',
    confirmPassword: ''
  }

  usernameError.value = emailError.value = locationError.value = false
  passwordError.value = confirmPasswordError.value = false
  usernameExists.value = emailExists.value = false
  isLoading.value = loadingSuccess.value = false
}

function switchToRegister() {
  showLoginModal.value = false
  resetForm()
  showRegisterModal.value = true
}

const switchToLogin = () => {
  showRegisterModal.value = false;
  resetForm()
  showLoginModal.value = true;
};

onMounted(async () => {
  provinces.value = await provincias;
});
</script>

<style scoped>
/* Base styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

input {
    background-color: white;
}

.roles {
    background-color: #F9F5F0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen,
    Ubuntu, Cantarell, "Open Sans", "Helvetica Neue", sans-serif;
  color: #1a2841;
  background-color: #f9f5f0;
  line-height: 1.6;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
  width: 100%;
  position: relative;
  z-index: 2;
}

/* Navbar */
.navbar {
  background-color: rgba(13, 27, 42, 0.95);
  padding: 1rem 0;
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo img {
  height: 40px;
}

.auth-buttons {
  display: flex;
  gap: 1rem;
}

.btn-login {
  background: transparent;
  color: #e0e1dd;
  border: 1px solid #e0e1dd;
  padding: 0.5rem 1.25rem;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn-login:hover {
  background-color: rgba(224, 225, 221, 0.1);
  transform: translateY(-2px);
}

.btn-register {
  background-color: #3d5a80;
  color: #e0e1dd;
  border: none;
  padding: 0.5rem 1.25rem;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn-register:hover {
  background-color: #415a77;
  transform: translateY(-2px);
}

/* Hero Section */
.hero {
  background-color: #0d1b2a;
  color: #e0e1dd;
  padding: 10rem 0 6rem;
  position: relative;
  overflow: hidden;
height: 100vh;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at 30% 50%, rgba(61, 90, 128, 0.4) 0%, rgba(13, 27, 42, 0) 70%);
  z-index: 1;
}

.hero .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;
}

.hero-content {
  flex: 1;
  max-width: 600px;
}

.hero h1 {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 1.5rem;
  line-height: 1.2;
}

.hero h1 span {
  background: linear-gradient(90deg, #e0e1dd, #8eacc5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.hero p {
  font-size: 1.25rem;
  margin-bottom: 2.5rem;
  opacity: 0.9;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
}

.btn-primary {
  background-color: #e0e1dd;
  color: #1a2841;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 50px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: default;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: default;
}

.btn-primary:hover {
  background-color: #fff;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.btn-secondary {
  background-color: transparent;
  color: #e0e1dd;
  border: 1px solid #e0e1dd;
  padding: 0.75rem 2rem;
  border-radius: 50px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background-color: rgba(224, 225, 221, 0.1);
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.hero-cards {
  flex: 1;
  position: relative;
  height: 400px;
}

.card {
  position: absolute;
  width: 200px;
  height: 280px;
  background-color: #3d5a80;
  border-radius: 10px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
  border-radius: 10px;
}

.card-1 {
  background: linear-gradient(135deg, #3d5a80 0%, #1a2841 100%);
  transform: rotate(-15deg) translateY(20px);
  z-index: 1;
  left: 0;
}

.card-2 {
  background: linear-gradient(135deg, #415a77 0%, #1a2841 100%);
  transform: rotate(5deg) translateY(-20px);
  z-index: 2;
  left: 100px;
}

.card-3 {
  background: linear-gradient(135deg, #778da9 0%, #1a2841 100%);
  transform: rotate(25deg) translateY(0);
  z-index: 3;
  left: 200px;
}

.hero-cards:hover .card-1 {
  transform: rotate(-20deg) translateY(30px) translateX(-20px);
}

.hero-cards:hover .card-2 {
  transform: rotate(0) translateY(-30px);
}

.hero-cards:hover .card-3 {
  transform: rotate(30deg) translateY(10px) translateX(20px);
}

.hero-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 3;
  line-height: 0;
}

.hero-wave svg {
  display: block;
  width: 100%;
  height: auto;
}

/* Roles Section */
.roles {
  padding: 6rem 0;
}

.section-header {
  text-align: center;
  margin-bottom: 4rem;
}

.section-header h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #1a2841;
}

.section-header p {
  font-size: 1.25rem;
  color: #415a77;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.roles-tabs {
  max-width: 1000px;
  margin: 0 auto;
}

.tabs-header {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-bottom: 3rem;
}

.tab-button {
  background: none;
  border: none;
  padding: 1rem 2rem;
  border-radius: 50px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #415a77;
  font-size: 1.125rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.tab-button svg {
  transition: all 0.3s ease;
}

.tab-button:hover {
  color: #1a2841;
}

.tab-button:hover svg {
  transform: scale(1.1);
}

.tab-button.active {
  background-color: #e0e1dd;
  color: #1a2841;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.1);
}

.tab-content {
  background-color: #e0e1dd;
  border-radius: 20px;
  padding: 3rem;
  box-shadow: 0 10px 30px rgba(26, 40, 65, 0.1);
}

.tab-grid {
  display: flex;
  gap: 3rem;
  align-items: center;
}

.tab-image {
  flex: 1;
}

.tab-image img {
  width: 100%;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(26, 40, 65, 0.1);
}

.tab-info {
  flex: 1;
}

.tab-info h3 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #1a2841;
}

.tab-info > p {
  font-size: 1.125rem;
  color: #415a77;
  margin-bottom: 2rem;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 2.5rem;
}

.feature-item {
  display: flex;
  gap: 1rem;
}

.feature-icon {
  width: 50px;
  height: 50px;
  background-color: rgba(61, 90, 128, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3d5a80;
  flex-shrink: 0;
}

.feature-text h4 {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #1a2841;
}

.feature-text p {
  font-size: 0.875rem;
  color: #415a77;
}

.btn-role {
  background-color: #3d5a80;
  color: #e0e1dd;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 50px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-role:hover {
  background-color: #1a2841;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(26, 40, 65, 0.1);
}

/* How It Works */
.how-it-works {
  padding: 6rem 0;
  background-color: #f0f0f0;
}

.steps {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 900px;
  margin: 0 auto;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  width: 250px;
}

.step-number {
  width: 60px;
  height: 60px;
  background-color: #3d5a80;
  color: #e0e1dd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  box-shadow: 0 10px 20px rgba(26, 40, 65, 0.1);
}

.step-content h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #1a2841;
}

.step-content p {
  font-size: 0.875rem;
  color: #415a77;
}

.step-connector {
  flex-grow: 1;
  height: 2px;
  background: linear-gradient(90deg, #3d5a80, #415a77);
  position: relative;
  margin: 0 1rem;
  max-width: 100px;
}

.step-connector::before,
.step-connector::after {
  content: '';
  position: absolute;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #3d5a80;
  top: 50%;
  transform: translateY(-50%);
}

.step-connector::before {
  left: 0;
}

.step-connector::after {
  right: 0;
}

/* Get Started */
.get-started {
  padding: 6rem 0;
  position: relative;
  overflow: hidden;
}

.get-started-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1a2841 0%, #3d5a80 100%);
  z-index: 1;
}

.get-started-content {
  text-align: center;
  color: #e0e1dd;
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.get-started h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.get-started p {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

/* Footer */
.footer {
  background-color: #0d1b2a;
  color: #e0e1dd;
  padding: 4rem 0 2rem;
}

.footer-top {
  display: flex;
  flex-wrap: wrap;
  gap: 4rem;
  margin-bottom: 3rem;
}

.footer-logo {
  flex: 1;
  min-width: 250px;
}

.footer-logo img {
  margin-bottom: 1rem;
}

.footer-logo p {
  opacity: 0.8;
}

.footer-nav {
  flex: 2;
  display: flex;
  flex-wrap: wrap;
  gap: 3rem;
}

.footer-nav-column {
  flex: 1;
  min-width: 150px;
}

.footer-nav-column h4 {
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.footer-nav-column ul {
  list-style: none;
}

.footer-nav-column li {
  margin-bottom: 0.75rem;
}

.footer-nav-column a {
  color: #e0e1dd;
  opacity: 0.8;
  text-decoration: none;
  transition: opacity 0.3s ease;
}

.footer-nav-column a:hover {
  opacity: 1;
}

.footer-bottom {
  padding-top: 2rem;
  border-top: 1px solid rgba(224, 225, 221, 0.2);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-bottom p {
  opacity: 0.8;
}

.social-links {
  display: flex;
  gap: 1rem;
}

.social-links a {
  color: #e0e1dd;
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.social-links a:hover {
  opacity: 1;
}

/* Modal */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background-color: #f9f5f0;
  border-radius: 20px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.modal-header {
  background-color: #3d5a80;
  color: #e0e1dd;
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.5rem;
}

.close-button {
  background: none;
  border: none;
  color: #e0e1dd;
  font-size: 1.5rem;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.close-button:hover {
  opacity: 0.8;
}

.modal form {
  padding: 1.5rem;
}

.btn-primary, .btn-secondary {
  margin: 16px;
  padding: 10px 24px;
  border: none;
  border-radius: 50px;
  font-size: 1em;
  cursor: pointer;
}



.form-group.has-error input,
.form-group.has-error select {
  border-color: #D94B4B;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group.has-error input {
  border-color: #D94B4B;
}

.form-group.has-error input::placeholder {
  color: #D94B4B;
}

.form-control {
  padding: 8px;
  font-size: 1em;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-top: 4px;
}

@keyframes spin { to { transform: rotate(360deg); } }
.modal-footer {
  padding: 16px;
  text-align: center;
  background: #E0E1DD;
}

.spinner-icon {
  display: inline-block;
  width: 16px; height: 16px;
  border: 2px solid #fff;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #1a2841;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 1rem;
  color: #1a2841;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #3d5a80;
  outline: none;
  box-shadow: 0 0 0 2px rgba(61, 90, 128, 0.2);
}

.modal-footer {
  padding: 1rem 1.5rem;
  background-color: #f0f0f0;
  text-align: center;
}

.modal-footer a {
  color: #3d5a80;
  text-decoration: none;
  font-weight: 600;
}

.modal-footer a:hover {
  text-decoration: underline;
}

/* Responsive */
@media (max-width: 992px) {
  .hero .container {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-buttons {
    justify-content: center;
  }
  
  .hero-cards {
    margin-top: 3rem;
  }
  
  .tab-grid {
    flex-direction: column;
  }
  
  .steps {
    flex-direction: column;
    gap: 2rem;
  }
  
  .step-connector {
    width: 2px;
    height: 50px;
    margin: 0;
  }
  
  .footer-top {
    flex-direction: column;
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 2.5rem;
  }
  
  .section-header h2 {
    font-size: 2rem;
  }
  
  .tab-info h3 {
    font-size: 1.75rem;
  }
  
  .get-started h2 {
    font-size: 2rem;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}

@media (max-width: 576px) {
  .hero h1 {
    font-size: 2rem;
  }
  
  .hero p {
    font-size: 1rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    width: 100%;
  }
  
  .btn-primary,
  .btn-secondary,
  .btn-role {
    width: 100%;
  }
  
  .tabs-header {
    flex-direction: column;
    gap: 1rem;
  }
  
  .tab-button {
    width: 100%;
    justify-content: center;
  }
  
  .tab-content {
    padding: 1.5rem;
  }
  
  .auth-buttons {
    gap: 0.5rem;
  }
  
  .btn-login,
  .btn-register {
    padding: 0.5rem 0.75rem;
    font-size: 0.875rem;
  }
}

.location-select-container {
  position: relative;
}

.suggestions-container {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  z-index: 10;
  margin-top: 0.25rem;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(26, 40, 65, 0.15);
}

.suggestions-list {
  max-height: 200px;
  overflow-y: auto;
  margin: 0;
  padding: 0;
  list-style: none;
  background-color: #fff;
  border: 2px solid #3d5a80;
  border-radius: 0.5rem;
}

.suggestion-item {
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
  border-bottom: 1px solid rgba(61, 90, 128, 0.1);
  color:black;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-item:hover {
  background-color: rgba(61, 90, 128, 0.1);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-control {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #3d5a80;
  border-radius: 0.5rem;
  background-color: #fff;
  color: #1a2841;
  font-size: 1rem;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-control:focus {
  outline: none;
  border-color: #1a2841;
  box-shadow: 0 0 0 3px rgba(61, 90, 128, 0.2);
}

.has-error .form-control {
  border-color: red;
}

</style>
