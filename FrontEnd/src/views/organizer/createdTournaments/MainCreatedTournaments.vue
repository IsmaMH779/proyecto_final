<template>
  <ion-page>
    <div class="header"><p class="header-title">Torneos</p></div>

    <div class="content-container">
      <ion-grid>
        <ion-row>
          <ion-col
            size="12" 
            size-sm="12" 
            size-md="12" 
            size-lg="4"
            v-for="tournament in filteredTournaments" 
            :key="tournament.id"
          >
            <div class="box">
              <div class="tournament-name-container">
                <p>[{{ tournament.game }}]   {{ " " +tournament.name }}</p>
              </div>
              <span><p>{{ tournament.format }}</p></span>
              <span>
                <p>{{ tournament.startDate.split('T')[0] }}</p>
                <p>{{ tournament.startDate.split('T')[1] }}</p>
              </span>
            </div>
          </ion-col>
        </ion-row>
      </ion-grid>
    </div>
  </ion-page>
</template>

<script>
import { IonPage } from "@ionic/vue";
import axios from "axios";

export default {
  components: {
    IonPage,
  },
  data() {
    return {
      tournaments: [],
    };
  },
  computed: {
  // Filtrar por torneo activo false y torneo cerrado true
  filteredTournaments() {
    return this.tournaments.filter(tournament => {
      // Filtrar torneos con isActive: false e isClosed: false
      if (!tournament.isActive && !tournament.isClosed) {
        // Modificar el formato si es "Direct_elimination"
        if (tournament.format === "Direct_elimination") {
          tournament.format = "Eliminacion directa";
        }
        return true;  // Solo devolver torneos que cumplen con la condición
      }
      return false;
    });
  },
},

  mounted() {
    axios.get("http://localhost:8082/api/tournaments/organizer", {
      headers: {
              "Content-Type": "application/json",
              "Authorization": `Bearer ${localStorage.getItem('token')}`
            }
    }).then((response) => {
      this.tournaments = response.data
    })
    .catch((error) => {
        console.error("Error al obtener datos:", error);
    });
  }
};

</script>


<style scoped>
p {
  margin: 0;
}
.header {
  background-color: #e0e1dd;
  height: 123px;
  box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
  color: black;
  display: flex;
  align-items: center;
  padding-left: 30px;
}

.header-title {
  color: #1B263B;
  font-size: 30px;
  width: max-content;
}

.content-container {
  height: 100%;
  padding: 20px 30px;

}

.box {
  color: black;
  background-color: #E0E1DD;
  border: 2px solid #415A77;
  border-radius: 16px;
  height: 100%;
  padding: 15px 30px 15px 30px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}
  

.tournament-name-container {
  border-bottom: 2px solid #415A77;
  padding-bottom: 10px;
  font-weight: bold;
}

span {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: 10px;

}

span p{
  margin: 15px 0px 0px 0px;
  color: #000;
  font-size: 16px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}
</style>