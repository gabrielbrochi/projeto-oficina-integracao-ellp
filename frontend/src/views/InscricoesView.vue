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

    <h2>Inscrições e Presença</h2>

    <div class="secao-oficina">
      <label for="sel-oficina">Selecione uma Oficina</label>
      <select
        id="sel-oficina"
        v-model="oficinaIdSelecionada"
        @change="carregarInscricoes"
      >
        <option value="">-- Escolha uma oficina --</option>
        <option v-for="o in oficinas" :key="o.id" :value="o.id">
          {{ o.nome }} — {{ formatarData(o.data) }}
        </option>
      </select>
    </div>

    <template v-if="oficinaIdSelecionada">

      <div class="secao-lista">
        <h3>Alunos inscritos</h3>
        <p v-if="carregando" class="info">Carregando...</p>
        <p v-else-if="inscricoes.length === 0" class="vazio">
          Nenhum aluno inscrito nesta oficina.
        </p>
        <table v-else>
          <thead>
            <tr>
              <th>Nome do Aluno</th>
              <th>Presença</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ins in inscricoes" :key="ins.id">
              <td>{{ ins.alunoNome }}</td>
              <td>
                <span :class="ins.presente ? 'badge-presente' : 'badge-ausente'">
                  {{ ins.presente ? 'Presente' : 'Ausente' }}
                </span>
              </td>
              <td class="td-acoes">
                <button
                  v-if="!ins.presente"
                  class="btn-presenca"
                  @click="marcarPresenca(ins.id)"
                >
                  Marcar Presença
                </button>
                <button
                  v-if="ins.presente"
                  class="btn-certificado"
                  @click="baixarCertificado(ins.id, ins.alunoNome)"
                >
                  ⬇ Certificado
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="secao-inscrever">
        <h3>Inscrever Aluno</h3>

        <p v-if="alunosDisponiveis.length === 0" class="vazio">
          Todos os alunos cadastrados já estão inscritos nesta oficina.
        </p>
        <template v-else>
          <div class="form-inscrever">
            <select v-model="alunoIdParaInscrever">
              <option value="">-- Selecione um aluno --</option>
              <option v-for="a in alunosDisponiveis" :key="a.id" :value="a.id">
                {{ a.nome }}{{ a.ra ? ` (${a.ra})` : '' }}
              </option>
            </select>
            <button
              class="btn-inscrever"
              :disabled="!alunoIdParaInscrever"
              @click="inscrever"
            >
              Inscrever
            </button>
          </div>
          <p v-if="erroInscricao" class="erro">{{ erroInscricao }}</p>
        </template>
      </div>

    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import api from '../api/axios.js'

const router = useRouter()
const auth = useAuthStore()

const oficinas = ref([])
const alunos = ref([])
const inscricoes = ref([])
const carregando = ref(false)
const oficinaIdSelecionada = ref('')
const alunoIdParaInscrever = ref('')
const erroInscricao = ref('')

const alunosDisponiveis = computed(() => {
  const inscritos = new Set(inscricoes.value.map(i => i.alunoId))
  return alunos.value.filter(a => !inscritos.has(a.id))
})

function formatarData(data) {
  if (!data) return '-'
  const [ano, mes, dia] = data.split('-')
  return `${dia}/${mes}/${ano}`
}

async function carregarInscricoes() {
  if (!oficinaIdSelecionada.value) {
    inscricoes.value = []
    return
  }
  carregando.value = true
  try {
    const { data } = await api.get(`/api/inscricoes/oficina/${oficinaIdSelecionada.value}`)
    inscricoes.value = data
  } finally {
    carregando.value = false
  }
  alunoIdParaInscrever.value = ''
  erroInscricao.value = ''
}

async function marcarPresenca(inscricaoId) {
  await api.patch(`/api/inscricoes/${inscricaoId}/presenca`)
  await carregarInscricoes()
}

async function baixarCertificado(inscricaoId, nomeAluno) {
  const response = await api.get(
    `/api/certificados/inscricao/${inscricaoId}`,
    { responseType: 'blob' }
  )
  const url = URL.createObjectURL(response.data)
  const link = document.createElement('a')
  link.href = url
  link.download = `certificado-${nomeAluno.replace(/\s+/g, '-')}.pdf`
  link.click()
  URL.revokeObjectURL(url)
}

async function inscrever() {
  erroInscricao.value = ''
  if (!alunoIdParaInscrever.value) return
  try {
    await api.post('/api/inscricoes', {
      alunoId: alunoIdParaInscrever.value,
      oficinaId: oficinaIdSelecionada.value,
    })
    alunoIdParaInscrever.value = ''
    await carregarInscricoes()
  } catch (e) {
    erroInscricao.value = e.response?.data?.message || 'Erro ao inscrever aluno.'
  }
}

function sair() {
  auth.logout()
  router.push('/login')
}

onMounted(async () => {
  const [resOf, resAl] = await Promise.all([
    api.get('/api/oficinas'),
    api.get('/api/alunos'),
  ])
  oficinas.value = resOf.data
  alunos.value = resAl.data
})
</script>

<style scoped>
.container { max-width: 900px; margin: 2rem auto; padding: 1rem; }
nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #ddd; }
nav a { text-decoration: none; color: #3b82f6; }
nav span { margin-left: auto; }
.secao-oficina { margin-bottom: 2rem; }
.secao-lista { margin-bottom: 2rem; }
label { display: block; margin-bottom: 0.4rem; font-size: 0.9rem; }
select { padding: 0.5rem; border: 1px solid #ccc; border-radius: 4px; min-width: 320px; font-size: 0.95rem; }
h3 { margin-bottom: 0.8rem; font-size: 1rem; color: #374151; }
table { width: 100%; border-collapse: collapse; margin-bottom: 0.5rem; }
th, td { padding: 0.6rem 0.5rem; border: 1px solid #ddd; text-align: left; font-size: 0.9rem; }
th { background: #f5f5f5; font-weight: 600; }
.td-acoes { display: flex; gap: 0.4rem; align-items: center; }
.badge-presente { background: #d1fae5; color: #065f46; padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.8rem; white-space: nowrap; }
.badge-ausente { background: #fee2e2; color: #991b1b; padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.8rem; white-space: nowrap; }
.form-inscrever { display: flex; gap: 0.5rem; align-items: center; margin-bottom: 0.5rem; }
button { padding: 0.4rem 0.8rem; border: none; border-radius: 4px; cursor: pointer; color: white; font-size: 0.85rem; }
button:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-presenca { background: #f59e0b; }
.btn-certificado { background: #7c3aed; }
.btn-inscrever { background: #3b82f6; font-size: 0.9rem; padding: 0.45rem 1rem; }
.vazio { color: #6b7280; font-style: italic; }
.info { color: #6b7280; }
.erro { color: #dc2626; font-size: 0.9rem; margin-top: 0.25rem; }
</style>
