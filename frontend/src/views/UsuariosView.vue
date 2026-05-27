<template>
  <div class="container">
    <nav>
      <router-link to="/alunos">Alunos</router-link>
      <router-link to="/oficinas">Oficinas</router-link>
      <router-link to="/inscricoes">Inscrições</router-link>
      <router-link to="/usuarios">Gerenciar Usuários</router-link>
      <span>Olá, {{ auth.username }}</span>
      <button @click="sair">Sair</button>
    </nav>

    <h2>Cadastro de Gerente de Oficina</h2>
    <form @submit.prevent="cadastrar">
      <div>
        <label>Username</label>
        <input v-model="form.username" required />
      </div>
      <div>
        <label>Senha inicial</label>
        <input v-model="form.senha" type="password" required />
      </div>
      <p v-if="mensagem" :class="{ erro: isErro }">{{ mensagem }}</p>
      <button type="submit">Cadastrar</button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import api from '../api/axios.js'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ username: '', senha: '' })
const mensagem = ref('')
const isErro = ref(false)

async function cadastrar() {
  try {
    await api.post('/api/usuarios', form)
    mensagem.value = 'Usuário cadastrado com sucesso!'
    isErro.value = false
    Object.assign(form, { username: '', senha: '' })
  } catch {
    mensagem.value = 'Erro ao cadastrar usuário.'
    isErro.value = true
  }
}

function sair() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.container { max-width: 500px; margin: 2rem auto; padding: 1rem; }
nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #ddd; }
nav a { text-decoration: none; color: #3b82f6; }
nav span { margin-left: auto; }
div { margin-bottom: 1rem; }
label { display: block; margin-bottom: 0.25rem; }
input { width: 100%; padding: 0.5rem; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
button { padding: 0.5rem 1.5rem; background: #3b82f6; color: white; border: none; border-radius: 4px; cursor: pointer; }
.erro { color: #dc2626; }
</style>
