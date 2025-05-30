# DECKLY

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Demo](https://img.shields.io/badge/estado-demo-lightgrey)

> **Deckly** es una plataforma web orientada a la gestión y participación en torneos de TCG. Esta destinada a dos tipos de usuarios, organizadores y jugadores. Los organizadores podran crear y gestionar torneos y los jugadores inscribirse y participar en estos.
---

## Tabla de Contenidos

- [Características](#-características)
- [Instalación](#-instalación)
- [Tecnologías](#-tecnologías)
- [Autores](#-autores)

---

## Características

-  Registro y autenticación de usuarios
-  Gestión de perfiles de usuario
-  Sistema de emparejamiento
-  Creación y administración de torneos
-  Dashboard con estadísticas
-  Interfaz adaptada para escritorio y móvil

---

## Instalación

### Requisitos

- Node.js
- Ionic CLI (`npm install -g @ionic/cli`)
- Java (versión recomendada: 17 o superior)
- MySQL server
- MongoDB server
- IDE recomendado: IntelliJ IDEA (Community o Ultimate)

### Clonar el repositorio

```bash
git clone https://github.com/IsmaMH779/proyecto_final.git
cd proyecto_final
```

### Instalación del Frontend

```bash
cd FrontEnd
npm install
```

### Ejecución de los microservicios

El proyecto consta de varios microservicios backend en Java. Cada uno debe ser ejecutado por separado desde un IDE compatible con proyectos Maven:

- `authservice`: Gestiona la autenticación
- `matchmakingService`: Lógica de emparejamientos y resultados en tiempo real
- `tournamentService`: Administración de torneos
- `userManagementService`: Gestion del Perfil y datos de usuarios

Asegúrate de que todos los microservicios estén corriendo antes de iniciar el frontend.

### Ejecución del Frontend

Desde la carpeta `FrontEnd`, inicia el servidor de desarrollo con:

```bash
ionic serve
```

Esto abrirá la aplicación en tu navegador predeterminado.

---

## Tecnologías

### Frontend

- [Vue 3](https://vuejs.org/)
- [Ionic Framework](https://ionicframework.com/)

### Backend

- Java 17
- Spring Boot (en cada microservicio)
- MySQL y MongoDB (persistencia de datos)

### Otros

- JWT para manejo de sesiones
- WebSockets para actualizaciones del bracket en tiempo real.
---

## Autores

- Ismael M.H. – [GitHub](https://github.com/IsmaMH779) – Desarrollador
- Eloy D.A. – [GitHub](https://github.com/eloydlca) – Desarrollador
---
