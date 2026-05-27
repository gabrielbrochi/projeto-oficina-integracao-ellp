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

    <h2>Gerenciamento de Alunos</h2>

    <p v-if="erroApi" class="erro-api">{{ erroApi }}</p>

    <form @submit.prevent="editando ? atualizar() : criar()">
      <div class="form-grid">
        <div>
          <label>Nome *</label>
          <input v-model="form.nome" required />
        </div>
        <div>
          <label>RA</label>
          <input v-model="form.ra" />
        </div>
        <div>
          <label>Email Institucional</label>
          <input v-model="form.emailInstitucional" type="email" />
        </div>
        <div>
          <label>Telefone</label>
          <input
            v-model="form.telefone"
            pattern=".{10,}"
            title="Mínimo 10 dígitos (ex: (41) 99999-9999)"
            :class="{ 'input-erro': telefoneInvalido }"
          />
          <span v-if="telefoneInvalido" class="erro-campo">
            Mínimo 10 dígitos (ex: (41) 99999-9999)
          </span>
        </div>
        <div>
          <label>Data de Nascimento</label>
          <input v-model="form.dataNascimento" type="date" :max="hoje" />
        </div>
        <div>
          <label>Curso</label>
          <select v-model="form.curso">
            <option value="">Selecione</option>
            <option>Engenharia de Computação</option>
            <option>Engenharia de Controle e Automação</option>
            <option>Engenharia Elétrica</option>
            <option>Engenharia Eletrônica</option>
            <option>Engenharia Mecânica</option>
            <option>Engenharia de Software</option>
            <option>Matemática</option>
            <option>Tecnologia em Análise e Desenvolvimento de Sistemas</option>
          </select>
        </div>
      </div>
      <div class="form-actions">
        <button type="submit" :disabled="telefoneInvalido">{{ editando ? 'Atualizar' : 'Adicionar Aluno' }}</button>
        <button v-if="editando" type="button" class="btn-cancelar" @click="cancelarEdicao">Cancelar</button>
      </div>
    </form>

    <table v-if="alunos.length">
      <thead>
        <tr>
          <th>Nome</th>
          <th>RA</th>
          <th>Email Institucional</th>
          <th>Curso</th>
          <th>Telefone</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="aluno in alunos" :key="aluno.id">
          <td>{{ aluno.nome }}</td>
          <td>{{ aluno.ra || '-' }}</td>
          <td>{{ aluno.emailInstitucional || '-' }}</td>
          <td>{{ aluno.curso || '-' }}</td>
          <td>{{ aluno.telefone || '-' }}</td>
          <td>
            <button class="btn-editar" @click="iniciarEdicao(aluno)">Editar</button>
            <button class="btn-excluir" @click="deletar(aluno.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-else class="vazio">Nenhum aluno cadastrado.</p>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import api from '../api/axios.js'

const router = useRouter()
const auth = useAuthStore()

const hoje = new Date().toISOString().split('T')[0]

const telefoneInvalido = computed(() =>
  form.telefone.length > 0 && form.telefone.length < 10
)

const alunos = ref([])
const editando = ref(null)
const erroApi = ref('')
const form = reactive({
  nome: '', ra: '', emailInstitucional: '', telefone: '',
  dataNascimento: '', curso: ''
})

async function carregar() {
  const { data } = await api.get('/api/alunos')
  alunos.value = data
}

async function criar() {
  erroApi.value = ''
  try {
    await api.post('/api/alunos', form)
    resetForm()
    await carregar()
  } catch (e) {
    erroApi.value = e.response?.data?.message || 'Erro ao cadastrar aluno.'
  }
}

async function atualizar() {
  erroApi.value = ''
  try {
    await api.put(`/api/alunos/${editando.value}`, form)
    cancelarEdicao()
    await carregar()
  } catch (e) {
    erroApi.value = e.response?.data?.message || 'Erro ao atualizar aluno.'
  }
}

async function deletar(id) {
  if (confirm('Deseja excluir este aluno?')) {
    erroApi.value = ''
    try {
      await api.delete(`/api/alunos/${id}`)
      await carregar()
    } catch (e) {
      erroApi.value = e.response?.data?.message || 'Erro ao excluir aluno.'
    }
  }
}

function iniciarEdicao(aluno) {
  editando.value = aluno.id
  Object.assign(form, {
    nome: aluno.nome || '',
    ra: aluno.ra || '',
    emailInstitucional: aluno.emailInstitucional || '',
    telefone: aluno.telefone || '',
    dataNascimento: aluno.dataNascimento || '',
    curso: aluno.curso || '',
  })
}

function cancelarEdicao() {
  editando.value = null
  resetForm()
}

function resetForm() {
  Object.assign(form, {
    nome: '', ra: '', emailInstitucional: '', telefone: '',
    dataNascimento: '', curso: '', periodo: null
  })
}

function sair() {
  auth.logout()
  router.push('/login')
}

onMounted(carregar)
</script>

<style scoped>
.container { max-width: 1100px; margin: 2rem auto; padding: 1rem; }
nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #ddd; }
nav a { text-decoration: none; color: #3b82f6; }
nav span { margin-left: auto; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem; }
.full-width { grid-column: 1 / -1; }
label { display: block; margin-bottom: 0.25rem; font-size: 0.9rem; }
input, select { width: 100%; padding: 0.5rem; box-sizing: border-box; border: 1px solid #ccc; border-radius: 4px; }
input.input-erro { border-color: #dc2626; }
.erro-campo { display: block; margin-top: 0.2rem; font-size: 0.8rem; color: #dc2626; }
.erro-api { color: #dc2626; background: #fef2f2; border: 1px solid #fca5a5; border-radius: 4px; padding: 0.5rem 0.75rem; margin-bottom: 0.75rem; font-size: 0.9rem; }
.form-actions { display: flex; gap: 0.5rem; margin-bottom: 2rem; }
button { padding: 0.5rem 1rem; border: none; border-radius: 4px; cursor: pointer; background: #3b82f6; color: white; }
.btn-cancelar { background: #6b7280; }
.btn-editar { background: #f59e0b; font-size: 0.85rem; padding: 0.25rem 0.5rem; }
.btn-excluir { background: #dc2626; font-size: 0.85rem; padding: 0.25rem 0.5rem; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 0.6rem 0.5rem; border: 1px solid #ddd; text-align: left; font-size: 0.9rem; }
th { background: #f5f5f5; font-weight: 600; }
.vazio { color: #6b7280; }
</style>
