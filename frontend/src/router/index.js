import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import LoginView from '../views/LoginView.vue'
import TrocarSenhaView from '../views/TrocarSenhaView.vue'
import UsuariosView from '../views/UsuariosView.vue'
import AlunosView from '../views/AlunosView.vue'

const routes = [
  { path: '/', redirect: '/alunos' },
  { path: '/login', component: LoginView },
  { path: '/trocar-senha', component: TrocarSenhaView, meta: { requiresAuth: true } },
  { path: '/usuarios', component: UsuariosView, meta: { requiresAuth: true, requiresAdmin: true } },
  { path: '/alunos', component: AlunosView, meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.token) return '/login'
  if (to.meta.requiresAdmin && !auth.isAdmin()) return '/alunos'
})

export default router
