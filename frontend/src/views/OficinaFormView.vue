<template>
  <div class="container">
    <nav>
      <router-link to="/alunos">Alunos</router-link>
      <router-link to="/oficinas">Oficinas</router-link>
      <router-link to="/inscricoes">Inscrições</router-link>
      <router-link v-if="auth.isAdmin()" to="/usuarios">Gerenciar Usuários</router-link>
      <span>Olá, {{ auth.username }}</span>
      <button @click="sair">Sair</button>
    </nav>

    <h2>{{ editando ? 'Editar Oficina' : 'Nova Oficina' }}</h2>

    <form @submit.prevent="submeter">
      <div class="form-grid">
        <div class="full-width">
          <label>Nome *</label>
          <input v-model="form.nome" required />
        </div>
        <div class="full-width">
          <label>Descrição</label>
          <textarea v-model="form.descricao" rows="3"></textarea>
        </div>
        <div>
          <label>Data *</label>
          <input v-model="form.data" type="date" required />
        </div>
        <div>
          <label>Carga Horária (horas) *</label>
          <input v-model.number="form.cargaHoraria" type="number" min="1" required />
        </div>
      </div>

      <p v-if="erro" class="erro">{{ erro }}</p>

      <div class="form-actions">
        <button type="submit">{{ editando ? 'Atualizar' : 'Cadastrar' }}</button>
        <router-link to="/oficinas">
          <button type="button" class="btn-cancelar">Cancelar</button>
        </router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { useOficinaStore } from '../stores/oficinaStore.js'
import { buscarOficina } from '../api/oficina.js'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const store = useOficinaStore()

const editando = computed(() => !!route.params.id)
const erro = ref(null)

const form = reactive({
  nome: '',
  descricao: '',
  data: '',
  cargaHoraria: 1,
})

async function submeter() {
  erro.value = null
  try {
    if (editando.value) {
      await store.atualizar(route.params.id, form)
    } else {
      await store.criar(form)
    }
    router.push('/oficinas')
  } catch {
    erro.value = editando.value
      ? 'Erro ao atualizar oficina.'
      : 'Erro ao cadastrar oficina.'
  }
}

function sair() {
  auth.logout()
  router.push('/login')
}

onMounted(async () => {
  if (editando.value) {
    try {
      const { data } = await buscarOficina(route.params.id)
      Object.assign(form, {
        nome: data.nome || '',
        descricao: data.descricao || '',
        data: data.data || '',
        cargaHoraria: data.cargaHoraria || 1,
      })
    } catch {
      erro.value = 'Erro ao carregar dados da oficina.'
    }
  }
})
</script>

<style scoped>
.container { max-width: 700px; margin: 2rem auto; padding: 1rem; }
nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #ddd; }
nav a { text-decoration: none; color: #3b82f6; }
nav span { margin-left: auto; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem; }
.full-width { grid-column: 1 / -1; }
label { display: block; margin-bottom: 0.25rem; font-size: 0.9rem; }
input, textarea, select { width: 100%; padding: 0.5rem; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; font-family: inherit; font-size: 1rem; }
textarea { resize: vertical; }
.form-actions { display: flex; gap: 0.5rem; margin-top: 0.5rem; }
button { padding: 0.5rem 1rem; border: none; border-radius: 4px; cursor: pointer; background: #3b82f6; color: white; }
.btn-cancelar { background: #6b7280; }
.erro { color: #dc2626; font-size: 0.9rem; margin-bottom: 0.5rem; }
</style>
