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

    <div class="cabecalho">
      <h2>Gerenciamento de Oficinas</h2>
      <router-link to="/oficinas/nova">
        <button class="btn-novo">+ Nova Oficina</button>
      </router-link>
    </div>

    <p v-if="store.erro" class="erro">{{ store.erro }}</p>
    <p v-if="store.carregando" class="info">Carregando...</p>

    <table v-if="store.oficinas.length">
      <thead>
        <tr>
          <th>Nome</th>
          <th>Descrição</th>
          <th>Data</th>
          <th>Carga Horária</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="oficina in store.oficinas" :key="oficina.id">
          <td>{{ oficina.nome }}</td>
          <td>{{ oficina.descricao || '-' }}</td>
          <td>{{ formatarData(oficina.data) }}</td>
          <td>{{ oficina.cargaHoraria }}h</td>
          <td>
            <router-link :to="`/oficinas/${oficina.id}/editar`">
              <button class="btn-editar">Editar</button>
            </router-link>
            <button class="btn-excluir" @click="deletar(oficina.id)">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-else-if="!store.carregando" class="vazio">Nenhuma oficina cadastrada.</p>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { useOficinaStore } from '../stores/oficinaStore.js'

const router = useRouter()
const auth = useAuthStore()
const store = useOficinaStore()

function formatarData(data) {
  if (!data) return '-'
  const [ano, mes, dia] = data.split('-')
  return `${dia}/${mes}/${ano}`
}

async function deletar(id) {
  if (confirm('Deseja excluir esta oficina?')) {
    await store.deletar(id)
  }
}

function sair() {
  auth.logout()
  router.push('/login')
}

onMounted(store.carregar)
</script>

<style scoped>
.container { max-width: 1100px; margin: 2rem auto; padding: 1rem; }
nav { display: flex; gap: 1rem; align-items: center; margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #ddd; }
nav a { text-decoration: none; color: #3b82f6; }
nav span { margin-left: auto; }
.cabecalho { display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem; }
.cabecalho h2 { margin: 0; }
.btn-novo { background: #3b82f6; color: white; border: none; border-radius: 4px; padding: 0.5rem 1rem; cursor: pointer; }
.btn-editar { background: #f59e0b; color: white; border: none; border-radius: 4px; font-size: 0.85rem; padding: 0.25rem 0.5rem; cursor: pointer; margin-right: 0.25rem; }
.btn-excluir { background: #dc2626; color: white; border: none; border-radius: 4px; font-size: 0.85rem; padding: 0.25rem 0.5rem; cursor: pointer; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 0.6rem 0.5rem; border: 1px solid #ddd; text-align: left; font-size: 0.9rem; }
th { background: #f5f5f5; font-weight: 600; }
.vazio { color: #6b7280; }
.erro { color: #dc2626; }
.info { color: #6b7280; }
</style>
