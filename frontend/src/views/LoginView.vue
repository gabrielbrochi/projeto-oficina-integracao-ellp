<template>
  <div class="login-container">
    <h1>Oficinas ELLP</h1>
    <form @submit.prevent="handleLogin">
      <div>
        <label>Usuário</label>
        <input v-model="username" required />
      </div>
      <div>
        <label>Senha</label>
        <input v-model="senha" type="password" required />
      </div>
      <p v-if="erro" class="erro">{{ erro }}</p>
      <button type="submit" :disabled="carregando">
        {{ carregando ? 'Entrando...' : 'Entrar' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const router = useRouter()
const auth = useAuthStore()

const username = ref('')
const senha = ref('')
const erro = ref('')
const carregando = ref(false)

async function handleLogin() {
  erro.value = ''
  carregando.value = true
  try {
    await auth.login(username.value, senha.value)
    if (auth.primeiroAcesso) {
      router.push('/trocar-senha')
    } else {
      router.push('/alunos')
    }
  } catch {
    erro.value = 'Usuário ou senha inválidos'
  } finally {
    carregando.value = false
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}
h1 { text-align: center; margin-bottom: 1.5rem; }
div { margin-bottom: 1rem; }
label { display: block; margin-bottom: 0.25rem; }
input { width: 100%; padding: 0.5rem; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
button { width: 100%; padding: 0.75rem; background: #3b82f6; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; }
button:disabled { opacity: 0.6; cursor: not-allowed; }
.erro { color: #dc2626; }
</style>
