import api from './axios.js'

const BASE = '/api/oficinas'

export function listarOficinas() {
  return api.get(BASE)
}

export function buscarOficina(id) {
  return api.get(`${BASE}/${id}`)
}

export function criarOficina(payload) {
  return api.post(BASE, payload)
}

export function atualizarOficina(id, payload) {
  return api.put(`${BASE}/${id}`, payload)
}

export function deletarOficina(id) {
  return api.delete(`${BASE}/${id}`)
}
