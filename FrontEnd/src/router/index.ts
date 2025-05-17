import { createRouter, createWebHistory } from "@ionic/vue-router";
import { RouteRecordRaw } from "vue-router";
import axios from "axios";
import MainWeb from "../views/MainWeb.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/landing",
  },
  {
    path: "/landing",
    redirect: "/landing/home",
    meta: { requiresGuest: true },
    children: [
      {
        path: "home",
        component: () => import("@/views/authentication/LandingPage.vue"),
      },
    ],
  },
  {
    path: "/web",
    component: MainWeb,
    meta: { requiresAuth: true },
    children: [
      //rutas del jugador
      {
        path: "player-home",
        component: () => import("@/views/player/home/MainHome.vue"),
        meta: { role: "player" },
      },
      {
        path: "player-tournament",
        component: () =>
          import("@/views/player/tournament/MainTournaments.vue"),
        meta: { role: "player" },
      },
      {
        path: "player-calendar",
        component: () => import("@/views/player/calendar/MainCalendar.vue"),
        meta: { role: "player" },
      },
      {
        path: "player-history",
        component: () => import("@/views/player/history/MainHistory.vue"),
        meta: { role: "player" },
      },
      {
        path: "player-search-tournament",
        component: () => import("@/views/common/SearchTournaments.vue"),
        meta: { role: "player" },
      },
      {
        path: "player-profile",
        component: () => import("@/views/player/profile/Profile.vue"),
        meta: { role: "player" },
      },
      {
        path: "player-tournament-profile/:id",
        component: () => import("@/views/common/TournamentProfile.vue"),
        meta: { role: "player" },
      },
      {
        path: "player-liveTournament/:id",
        component: () => import("@/views/player/tournament/LiveTournament.vue"),
        meta: { role: "player" },
      },

      // rutas del organizador
      {
        path: "organizer-home",
        component: () => import("@/views/organizer/home/MainHome.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-tournaments-created",
        component: () =>
          import("@/views/organizer/Tournament/MainTournaments.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-search-tournament",
        component: () => import("@/views/common/SearchTournaments.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-calendar",
        component: () => import("@/views/organizer/calendar/MainCalendar.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-history",
        component: () => import("@/views/organizer/history/MainHistory.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-profile",
        component: () => import("@/views/organizer/profile/Profile.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-create-tournament",
        component: () =>
          import("@/views/organizer/createTournament/MainCreateTournament.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-tournament-profile/:id",
        component: () => import("@/views/common/TournamentProfile.vue"),
        meta: { role: "organizer" },
      },
      {
        path: "organizer-liveTournament/:id",
        component: () =>
          import("@/views/organizer/Tournament/LiveTournament.vue"),
        meta: { role: "organizer" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// Middleware para proteger rutas
router.beforeEach(async (to, form, next) => {
  const token = localStorage.getItem("token");

  // Si estás logueado y tratas de entrar en /landing/*, te mando a tu home según rol
  if (token && to.path.startsWith("/landing")) {
    try {
      const { data: userRole } = await axios.get(
        "http://localhost:8080/api/auth/me/role",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (userRole === "player") return next("/web/player-home");
      if (userRole === "organizer") return next("/web/organizer-home");
      // rol inesperado: limpio token y dejo volver a landing
      localStorage.removeItem("token");
      return next("/landing/home");
    } catch {
      localStorage.removeItem("token");
      return next("/landing/home");
    }
  }

  try {
    // si requiere auth y no hay ningun token redirige al login
    if (to.meta.requiresAuth && !token) {
      return next("/");
    }

    if (token) {
      const response = await axios.get(
        "http://localhost:8080/api/auth/me/role",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const userRole = response.data;

      // redirigir al home en funcion del rol del usuario
      if (to.path == "/web") {
        if (userRole == "player") {
          return next("/web/player-home");
        } else if (userRole == "organizer") {
          return next("/web/organizer-home");
        }
      }

      // si la ruta tiene un rol definidio y este no coincide con el del usuario, redirigir
      if (to.meta.role && to.meta.role != userRole) {
        return next("/web");
      }
    }
  } catch (error) {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    return next("/landing/home");
  }

  next();
});

export default router;
