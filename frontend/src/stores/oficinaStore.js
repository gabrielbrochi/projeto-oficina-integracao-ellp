import { defineStore } from 'pinia'
import { ref } from 'vue'
import { listarOficinas, criarOficina, atualizarOficina, deletarOficina } from '../api/oficina.js'

export const useOficinaStore = defineStore('oficina', () => {
  const oficinas = ref([])
  const carregando = ref(false)
  const erro = ref(null)

  async function carregar() {
    carregando.value = true
    erro.value = null
    try {
      const { data } = await listarOficinas()
      oficinas.value = data
    } catch {
      erro.value = 'Erro ao carregar oficinas.'
    } finally {
      carregando.value = false
    }
  }

  async function criar(payload) {
    await criarOficina(payload)
    await carregar()
  }

  async function atualizar(id, payload) {
    await atualizarOficina(id, payload)
    await carregar()
  }

  async function deletar(id) {
    await deletarOficina(id)
    await carregar()
  }

  return { oficinas, carregando, erro, carregar, criar, atualizar, deletar }
})
