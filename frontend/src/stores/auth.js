import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../api/axios.js'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const username = ref(localStorage.getItem('username') || null)
  const role = ref(localStorage.getItem('role') || null)
  const primeiroAcesso = ref(localStorage.getItem('primeiroAcesso') === 'true')

  async function login(username_, senha) {
    const { data } = await api.post('/api/auth/login', { username: username_, senha })
    token.value = data.token
    username.value = data.username
    role.value = data.role
    primeiroAcesso.value = data.primeiroAcesso
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
    localStorage.setItem('primeiroAcesso', data.primeiroAcesso)
  }

  function logout() {
    token.value = null
    username.value = null
    role.value = null
    primeiroAcesso.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('primeiroAcesso')
  }

  const isAdmin = () => role.value === 'ADMINISTRADOR'

  return { token, username, role, primeiroAcesso, login, logout, isAdmin }
})
