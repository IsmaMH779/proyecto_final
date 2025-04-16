<template>
    <div @click="showProfile()" class="userProfile-Button-container">user</div>
  </template>
    
<script>
    import { useRouter } from 'vue-router';

    export default {
    setup() {
        const router = useRouter();

        const showProfile = () => {
        router.push('/web/organizer-profile'); 
        };

        return { showProfile };
    }
    };
</script>

<style scoped>
    .userProfile-Button-container {
        height: 100px;
        width: 200px;
        background-color: red;
    }
</style>
  