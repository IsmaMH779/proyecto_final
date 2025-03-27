<template>
    <ion-page>
        <div class="header"> PLAYER HOME 
            <UserProfileButton/>
        </div>
        
    </ion-page>
</template>
  
<script>
import { IonPage } from '@ionic/vue';
import UserProfileButton from '@/components/UserProfileButton.vue';

export default {
  components: {
    IonPage,
    UserProfileButton,
  },
};
</script>
  
<style scoped>
    .header {
        background-color: #E0E1DD;
        height: 123px;
        box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
        color: black;
    }
</style>