<template>
  <ion-page>
    <div class="container">
      <Sidebar/>
      <ion-content :fullscreen="true">
        <ion-router-outlet></ion-router-outlet>
      </ion-content>
    </div>
  </ion-page>
</template>

<script setup lang="ts">
import Sidebar from '@/components/SidebarPlayer.vue';
import { IonRouterOutlet, IonPage, IonHeader, IonToolbar, IonTitle, IonContent, IonItem, IonCard, IonText} from '@ionic/vue';

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
