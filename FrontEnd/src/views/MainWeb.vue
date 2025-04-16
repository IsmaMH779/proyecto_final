<template>
  <ion-page>
    <div class="container">
      <div>
        <PlayerSidebar v-if="MyRole === 'player'"/>
        <OrganizerSidebar v-else-if="MyRole === 'organizer'"/>
      </div>
      
      <ion-content :fullscreen="true">
        <router-view />
      </ion-content>
    </div>
  </ion-page>
</template>

<script setup lang="ts">
import PlayerSidebar from '@/components/SidebarPlayer.vue';
import OrganizerSidebar from '@/components/SidebarOrganizer.vue';
import { ref } from 'vue';
import { IonRouterOutlet, IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonItem, IonCard, IonText} from '@ionic/vue';

const MyRole = ref(localStorage.getItem('role'));
</script>

<style scoped>
  .container {
    display: flex;
    flex-direction: row
  }

  ion-content{
    --background: #F5EFE7;
    height: 100vh;
  }
</style>
