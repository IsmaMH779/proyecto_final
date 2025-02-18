import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import MainWeb from '../views/MainWeb.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/auth'
  },{
    path: '/auth',
    redirect: '/auth/login',
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
    children: [
      {
        path: '',
        redirect: '/web/player-home'
      },
      {
        path: 'player-home',
        component: () => import('@/views/player/home/MainHome.vue')
      },
      {
        path: 'player-tournament',
        component: () => import('@/views/player/tournament/MainTournamnet.vue')
      },
      {
        path: 'player-calendar',
        component: () => import('@/views/player/calendar/MainCalendar.vue')
      },
      {
        path: 'player-history',
        component: () => import('@/views/player/history/MainHistory.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
