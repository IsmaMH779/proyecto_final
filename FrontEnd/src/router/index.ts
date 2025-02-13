import { createRouter, createWebHistory } from '@ionic/vue-router';
import { RouteRecordRaw } from 'vue-router';
import MainWeb from '../views/MainWeb.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/web'
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
        component: () => import('@/views/player/Home/MainHome.vue')
      },
      {
        path: 'player-tournament',
        component: () => import('@/views/player/Tournament/MainTournamnet.vue')
      },
      {
        path: 'player-calendar',
        component: () => import('@/views/player/Calendar/MainCalendar.vue')
      },
      {
        path: 'player-history',
        component: () => import('@/views/player/History/MainHistory.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
