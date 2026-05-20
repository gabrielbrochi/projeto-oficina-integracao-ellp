<template>
  <div class="container">
    <h1>Trocar Senha</h1>
    <p class="aviso">Este é seu primeiro acesso. Por favor, defina uma nova senha.</p>
    <form @submit.prevent="trocar">
      <div>
        <label>Nova Senha</label>
        <input v-model="novaSenha" type="password" minlength="6" required />
      </div>
      <div>
        <label>Confirmar Senha</label>
        <input v-model="confirmar" type="password" required />
      </div>
      <p v-if="erro" class="erro">{{ erro }}</p>
      <button type="submit" :disabled="carregando">
        {{ carregando ? 'Salvando...' : 'Salvar nova senha' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import api from '../api/axios.js'

const router = useRouter()
const auth = useAuthStore()

const novaSenha = ref('')
const confirmar = ref('')
const erro = ref('')
const carregando = ref(false)

async function trocar() {
  if (novaSenha.value !== confirmar.value) {
    erro.value = 'As senhas não coincidem'
    return
  }
  erro.value = ''
  carregando.value = true
  try {
    await api.put('/api/usuarios/senha', { novaSenha: novaSenha.value })
    auth.primeiroAcesso = false
    localStorage.setItem('primeiroAcesso', 'false')
    router.push('/alunos')
  } catch {
    erro.value = 'Erro ao trocar senha'
  } finally {
    carregando.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 400px;
  margin: 100px auto;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}
h1 { text-align: center; margin-bottom: 0.5rem; }
.aviso { text-align: center; color: #6b7280; margin-bottom: 1.5rem; font-size: 0.9rem; }
div { margin-bottom: 1rem; }
label { display: block; margin-bottom: 0.25rem; }
input { width: 100%; padding: 0.5rem; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
button { width: 100%; padding: 0.75rem; background: #3b82f6; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; }
button:disabled { opacity: 0.6; cursor: not-allowed; }
.erro { color: #dc2626; }
</style>
