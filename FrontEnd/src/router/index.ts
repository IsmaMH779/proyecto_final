import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import axios from 'axios';
import MainWeb from '../views/MainWeb.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/auth'
  },{
    path: '/auth',
    redirect: '/auth/login',
    meta: {requiresGuest: true},
    children: [
      {
        path:'login',
        component: () => import('@/views/authentication/Login.vue')
      },
      {
        path:'register',
        component: () => import('@/views/authentication/Register.vue')
      }
    ]
  },
  {
    path: '/web',
    component: MainWeb,
    meta: {requiresAuth: true},
    children: [
      //rutas del jugador
      {
        path: 'player-home',
        component: () => import('@/views/player/home/MainHome.vue'),
        meta: {role: 'player'}
      },
      {
        path: 'player-tournament',
        component: () => import('@/views/player/tournament/MainTournamnet.vue'),
        meta: {role: 'player'}
      },
      {
        path: 'player-calendar',
        component: () => import('@/views/player/calendar/MainCalendar.vue'),
        meta: {role: 'player'}
      },
      {
        path: 'player-history',
        component: () => import('@/views/player/history/MainHistory.vue'),
        meta: {role: 'player'}
      },
      {
        path: 'player-profile',
        component: () => import('@/views/player/profile/Profile.vue'),
        meta: {role: 'player'}
      },

      // rutas del organizador
      {
        path: 'organizer-home',
        component: () => import('@/views/organizer/home/MainHome.vue'),
        meta: {role: 'organizer'}
      },
      {
        path: 'organizer-tournaments-created',
        component: () => import('@/views/organizer/createdTournaments/MainCreatedTournaments.vue'),
        meta: {role: 'organizer'}
      },
      {
        path: 'organizer-tournaments-active',
        component: () => import('@/views/organizer/activeTournaments/MainActiveTournaments.vue'),
        meta: {role: 'organizer'}
      },
      {
        path: 'organizer-search-tournament',
        component: () => import('@/views/organizer/searchTournament/MainSearchTournaments.vue'),
        meta: {role: 'organizer'}
      },
      {
        path: 'organizer-calendar',
        component: () => import('@/views/organizer/calendar/MainCalendar.vue'),
        meta: {role: 'organizer'}
      },
      {
        path: 'organizer-history',
        component: () => import('@/views/organizer/history/MainHistory.vue'),
        meta: {role: 'organizer'}
      },
      {
        path: 'organizer-profile',
        component: () => import('@/views/organizer/profile/Profile.vue'),
        meta: {role: 'organizer'}
      },

    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Middleware para proteger rutas
router.beforeEach(async (to, form, next) => {
  const token = localStorage.getItem('token');

  //si el usuario intenta acceder a auth estando logueado 
  if (to.meta.requieresGuest && token) {
    try {
      const response = await axios.get('http://localhost:8080/api/auth/me/role', {
        headers: { Authorization: `Bearer ${token}` }
      });
  
      const userRole = response.data;

      // Redirigir según su rol
      if (userRole === 'player') {
        return next('/web/player-home');
      } else if (userRole === 'organizer') {
        return next('/web/organizer-home');
      }

    } catch (error) {
      localStorage.removeItem('token');
      return next('/auth/login')
    }

  }


  try {
    // si requiere auth y no hay ningun token redirige al login
    if (to.meta.requiresAuth && !token) {
      return next ('/');
    }

    if (token) {
      
      const response = await axios.get('http://localhost:8080/api/auth/me/role', {
        headers: { Authorization: `Bearer ${token}` }
      });

      const userRole = response.data;

      // redirigir al home en funcion del rol del usuario
      if (to.path == '/web') {
        if (userRole == 'player') {
          return next('/web/player-home');
        } else if (userRole == 'organizer') {
          return next('/web/organizer-home');
        }
      }
      
      // si la ruta tiene un rol definidio y este no coincide con el del usuario, redirigir
      if (to.meta.role && to.meta.role != userRole) {
        return next('/web');
      }

    }
  } catch (error) {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    return next('/auth/login')
  }

  next();
})

export default router
