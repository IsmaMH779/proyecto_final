<template>
    <bracket :rounds="rounds">
      <template #player="{ player }">
        {{ player.name }}
      </template>
    </bracket>
  </template>
 

<script setup>
import Bracket from "vue-tournament-bracket";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const route = useRoute();
const yeah = ref("");

const rounds = [
        //Semi finals
        {
            games: [
                {

                    player1: { id: "1", name: "Competitor 1", winner: false },
                    player2: { id: "4", name: "Competitor 4", winner: true },
                },
                {

                    player1: { id: "5", name: "Competitor 5", winner: false },
                    player2: { id: "8", name: "Competitor 8", winner: true },
                }
            ]
        },
        //Final
        {
            games: [
                {

                    player1: { id: "4", name: "Competitor 4", winner: false },
                    player2: { id: "8", name: "Competitor 8", winner: true },
                }
            ]
        }
    ];


const fetchBracket = async () => {
    
}


onMounted(() => {
 fetchBracket()
});
</script>

<style></style>
