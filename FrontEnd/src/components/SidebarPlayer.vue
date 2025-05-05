<template>
  <div class="sidebar-layout">
    <div class="desktop-sidebar">
      <div class="logo_container">
        <h1 class="header-title">DECKLY</h1>
      </div>
      
      <ion-list id="player-list">
        <ion-item 
          v-for="(p, i) in appPages" 
          :key="i"
          @click="handleItemClick(p)" 
          :router-link="p.url" 
          lines="none" 
          :detail="false" 
          class="hydrated"
          :class="{'selected': selectedIndex === i}"
        >
          <ion-icon aria-hidden="true" slot="start" :ios="p.iosIcon" :md="p.mdIcon"></ion-icon>
          <ion-label>{{ p.title }}</ion-label>
        </ion-item>
      </ion-list>
    </div>
    
    <div class="desktop-sidebar-mobile" v-if="isMobile">
      <ion-menu content-id="main-content" type="overlay" :swipe-gesture="isMobile">
        <ion-content>
          <ion-list id="player-list-mobile">
            <div class="logo_container">
              <h1 class="header-title">DECKLY</h1>
            </div>
            
            <ion-menu-toggle :auto-hide="false" v-for="(p, i) in appPages" :key="i">
              <ion-item 
                @click="handleItemClick(p)" 
                :router-link="p.url" 
                lines="none" 
                :detail="false" 
                class="hydrated" 
                :class="{'selected': selectedIndex === i}"
              >
                <ion-icon aria-hidden="true" slot="start" :ios="p.iosIcon" :md="p.mdIcon"></ion-icon>
                <ion-label>{{ p.title }}</ion-label>
              </ion-item>
            </ion-menu-toggle>
          </ion-list>
        </ion-content>
      </ion-menu>
    </div>
    
    <div class="main-content" id="main-content">
      <ion-header class="header">
        <ion-toolbar>
          <ion-buttons slot="start">
            <ion-menu-button></ion-menu-button>
          </ion-buttons>
          
          <div class="header-container">
            <ion-title>{{ headerTitle }}</ion-title>
            
            <div class="user-info">
              <span class="username"> {{ userData.username }}</span>
              <div class="user-avatar" @click="toggleUserMenu" ref="userAvatarRef">
                <img v-if="userData.imageUrl" :src="'http://localhost:8081/images/profile/' + userData.imageUrl" />
                <img v-else :src="defaultProfileImage" alt="User" />
              </div>
            </div>
          </div>
        </ion-toolbar>
      </ion-header>
      
      <ion-content>
        <div class="router-container">
          <router-view />
        </div>
      </ion-content>
    </div>
    
    <Teleport to="body">
      <div 
        class="dropdown-menu" 
        v-show="isUserMenuOpen" 
        :style="dropdownPosition"
      >
        <div class="dropdown-item" @click="navigateToProfile">
          <ion-icon :icon="personOutline"></ion-icon>
          <span>Perfil</span>
        </div>
        <div class="dropdown-item" @click="logout">
          <ion-icon :icon="logOutOutline"></ion-icon>
          <span>Cerrar sesion</span>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import {
  IonContent,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonMenu,
  IonMenuToggle,
  IonHeader,
  IonToolbar,
  IonTitle,
  IonButtons,
  IonMenuButton
} from '@ionic/vue';
import { ref, onMounted, watch, onBeforeUnmount, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  homeOutline,
  homeSharp,
  trophyOutline,
  trophySharp,
  calendarOutline,
  calendarSharp,
  timeOutline,
  timeSharp,
  logOutOutline,
  logOutSharp,
  personOutline,
  searchOutline,
  searchSharp
} from 'ionicons/icons';
import defaultProfileImage from '@/assets/profile_assets/default-profile-image.svg';
import axios from 'axios';

const router = useRouter();
const route = useRoute();

const isMobile = ref(window.innerWidth <= 768);
const selectedIndex = ref(0);
const headerTitle = ref('');
const isUserMenuOpen = ref(false);
const userAvatarRef = ref(null);
const dropdownPosition = ref({
  top: '0px',
  right: '0px'
});

const userData = ref({
  username: "USERNAME",
  imageUrl: null
});

const handleResize = () => {
  isMobile.value = window.innerWidth <= 768;
};

// Paginas disponibles en el menu
const appPages = ref([
  { title: 'Home', url: '/web/player-home', iosIcon: homeOutline, mdIcon: homeSharp },
  { title: 'Torneos', url: '/web/player-tournament', iosIcon: trophyOutline, mdIcon: trophySharp },
  { title: 'Calendario', url: '/web/player-calendar', iosIcon: calendarOutline, mdIcon: calendarSharp },
  { title: 'Historial', url: '/web/player-history', iosIcon: timeOutline, mdIcon: timeSharp },
  { title: 'Buscador', url: '/web/player-search-tournament', iosIcon: searchOutline, mdIcon: searchSharp}
]);

const iosIcon = computed(() => appPages.value.map(p => p.iosIcon));
const mdIcon = computed(() => appPages.value.map(p => p.mdIcon));

// Titulo del header
const updateHeader = () => {
  const currentPath = route.path;
  const index = appPages.value.findIndex((page) => page.url === currentPath && page.url !== 'logout');
  selectedIndex.value = index;
  headerTitle.value = index !== -1 ? appPages.value[index].title : '';
};

// Elemento activo segun la URL
const setActiveMenuItem = () => {
  const path = window.location.pathname;
  if (path !== undefined) {
    selectedIndex.value = appPages.value.findIndex((page) => page.url === path);
  }
};

// Accion al hacer clic en un item del menu
const handleItemClick = (page) => {
  if (page.url === 'logout') {
    logout();
  }
};

// Posicionar el dropdown debajo del avatar
const updateDropdownPosition = () => {
  if (userAvatarRef.value) {
    const rect = userAvatarRef.value.getBoundingClientRect();
    dropdownPosition.value = {
      top: `${rect.bottom + 5}px`,
      right: `${window.innerWidth - rect.right}px`
    };
  }
};

const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value;
  if (isUserMenuOpen.value) {
    updateDropdownPosition();
  }
};

// Cierra el menu de usuario si se hace clic fuera
const closeUserMenu = (event) => {
  if (isUserMenuOpen.value && 
      userAvatarRef.value && 
      !userAvatarRef.value.contains(event.target) && 
      !event.target.closest('.dropdown-menu')) {
    isUserMenuOpen.value = false;
  }
};

const navigateToProfile = () => {
  isUserMenuOpen.value = false;
  router.push('/web/player-profile');
};

// Cerrar sesion
const logout = () => {
  isUserMenuOpen.value = false;
  localStorage.removeItem('token');
  router.push('/auth/login');
};

// Obtener datos del usuario
const getUserData = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      router.push('/auth/login');
      return;
    }

    const response = await axios.get('http://localhost:8081/api/players/me', {
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      }
    });

    if (response.status === 200) {
      userData.value = response.data;
    }
  } catch (error) {
    console.error('Error al obtener datos del usuario', error);
  }
};

onMounted(() => {
  setActiveMenuItem();
  updateHeader();
  document.addEventListener('click', closeUserMenu);
  window.addEventListener('resize', updateDropdownPosition);
  window.addEventListener('resize', handleResize);
  getUserData();
});

onBeforeUnmount(() => {
  document.removeEventListener('click', closeUserMenu);
  window.removeEventListener('resize', updateDropdownPosition);
  window.removeEventListener('resize', handleResize);
});

watch(() => route.path, () => {
  updateHeader();
});
</script>


<style scoped>
.sidebar-layout {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
}

/* Sidebar desktop */
.desktop-sidebar {
  width: 320px;
  background: #0D1B2A;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  padding: 0px 15px;
}

.header-title {
  margin-bottom: 0;
  font-weight: bold;
}

/* mediaqueries movil */
@media (max-width: 768px) {
  .desktop-sidebar {
    display: none !important;
  }
  .header-title {
    margin: 0 0 15px 0;
  }

  .username {
    display: none;
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: calc(100% - 320px);
  overflow: hidden;
}

@media (max-width: 768px) {
  .main-content {
    width: 100%;
  }
}

.router-container {
  flex: 1;
  overflow: auto;
}

/* Sidebar */
ion-menu {
  --width: 280px;
  max-width: 280px;
}

ion-menu ion-content {
  --background: #0D1B2A;
}

/* estilos en comun */
.logo_container {
  display: flex;
  color: white;
  padding-left: 20px;
}

ion-list {
  padding: 20px 0;
  background: #0D1B2A;
}

ion-item {
  --padding-start: 10px;
  --padding-end: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
  --background: transparent;
  --color: white;
}

ion-item.selected {
  --background: #1B263B;
  --color: white;
}

ion-item ion-icon {
  color: white;
}

ion-item ion-label {
  font-weight: 400;
  font-size: 20px;
}

/* estilos del header */
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 0 16px;
}

ion-title {
  color: #0D1B2A;
  padding: 0;
  text-align: left;
}

ion-menu-button {
  color: #0D1B2A;
}

/* estilos del contenido */
ion-content {
  --background: #F5EFE7;
}

ion-toolbar {
  --background: #E0E1DD;
  --color: white;
  display: flex;
}

/* ajustes responsive */
@media (min-width: 769px) {
  /* Hide the menu button on desktop */
  ion-buttons {
    display: none;
  }
}

/* boton del usuario */
.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-left: auto;
}

.username {
  color: #1b263b;
}

.user-avatar {
  cursor: pointer;
  position: relative;
}

.user-avatar img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid #1a2841;
}

.header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.header ion-toolbar {
  height: 84.5px;
}

/* dropdown */
.dropdown-menu {
  margin-top: 5px;
  position: fixed;
  width: 180px;
  background: #E0E1DD;
  border-left: 1px solid #0D1B2A;
  border-bottom: 1px solid #0D1B2A;
  border-right: 1px solid #0D1B2A;
  z-index: 9999;
  overflow: hidden;
  animation: fadeIn 0.2s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  color: #0D1B2A;
  cursor: pointer;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.dropdown-item ion-icon {
  font-size: 18px;
  margin-right: 10px;
  color: #0D1B2A;
}
</style>