<template>
    <div class="sidebar_container">
      <div class="logo_container">
        <h1>LOGO</h1>
      </div>
        <ul>
            <li><a href="/web/player-home"><div class="button_nav">Home</div></a></li>
            <li><a href="/web/player-tournament"><div class="button_nav">Torneos</div></a></li>
            <li><a href="/web/player-calendar"><div class="button_nav">Calendario</div></a></li>
            <li><a href="/web/player-history"><div class="button_nav">Historial</div></a></li>
        </ul>
    </div>
</template>
  
<script>
    export default {
    data() {
        return {
        isActive: false, // Estado para saber si el botón está activo
        };
    },
    };
</script>
  
  <style scoped>
  .sidebar_container {
    /* Estilos del componente */
    color: white;
    background: #0D1B2A;
    height: 100vh;
    width: 320px;
    padding-left: 31px;
  }

  .logo_container{
    display: flex;
    justify-content: center;
    height: 123px;
    align-items: center;
    
  }
  
  ul {
    list-style: none;
    padding: 0px;
    margin: 50px 0px 0px 0px;
  }

  li {

  }
  a {
    text-decoration: none;
  }

  .button_nav{
    color: #FFF;
    font-size: 32px;
    font-style: normal;
    font-weight: 400;
    line-height: normal;
    width: 100%;
    background-color: transparent;
    padding-left: 22px ;
    text-align: left;
    margin-bottom:20px;
    height: 58px;
    vertical-align: center;
    display: flex;
    align-items: center;
  }

  .button_nav:hover {
    background-color: #1B263B;
  }


  </style>