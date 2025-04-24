<template>
  <ion-app>
    <PlayerSidebar v-if="MyRole === 'player'"/>
    <OrganizerSidebar v-else-if="MyRole === 'organizer'"/>
  </ion-app>
</template>

<script setup lang="ts">
import PlayerSidebar from '@/components/SidebarPlayer.vue';
import OrganizerSidebar from '@/components/SidebarOrganizer.vue';
import { ref } from 'vue';
import { IonApp } from '@ionic/vue';

const MyRole = ref(localStorage.getItem('role'));
</script>