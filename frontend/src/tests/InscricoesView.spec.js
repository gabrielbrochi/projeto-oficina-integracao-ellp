import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'
import InscricoesView from '../views/InscricoesView.vue'
import api from '../api/axios.js'

vi.mock('../api/axios.js', () => ({
  default: {
    get:   vi.fn(),
    post:  vi.fn(),
    patch: vi.fn(),
  },
}))

// ── Dados de apoio ────────────────────────────────────────────────────────────

const oficinas = [
  { id: 1, nome: 'Java Básico',   data: '2026-07-10', cargaHoraria: 8 },
  { id: 2, nome: 'Python Avançado', data: '2026-08-20', cargaHoraria: 16 },
]

const alunos = [
  { id: 1, nome: 'Maria Silva',    ra: '2267845' },
  { id: 2, nome: 'João Rodrigues', ra: '2350580' },
  { id: 3, nome: 'Ana Costa',      ra: '2400001' },
]

const inscricoes = [
  { id: 10, alunoId: 1, alunoNome: 'Maria Silva',    oficinaId: 1, oficinaNome: 'Java Básico', presente: false },
]

const inscricoesComPresente = [
  { id: 10, alunoId: 1, alunoNome: 'Maria Silva',    oficinaId: 1, oficinaNome: 'Java Básico', presente: true  },
]

// ── Helpers ───────────────────────────────────────────────────────────────────

function buildRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/inscricoes', component: InscricoesView },
      { path: '/login',      component: { template: '<div/>' } },
      { path: '/alunos',     component: { template: '<div/>' } },
      { path: '/oficinas',   component: { template: '<div/>' } },
    ],
  })
}

function setupApiMock(inscricoesData = inscricoes) {
  api.get.mockImplementation(url => {
    if (url === '/api/oficinas') return Promise.resolve({ data: oficinas })
    if (url === '/api/alunos')   return Promise.resolve({ data: alunos })
    if (url.includes('/api/inscricoes/oficina/'))
      return Promise.resolve({ data: inscricoesData })
    return Promise.resolve({ data: [] })
  })
}

describe('InscricoesView', () => {
  let router

  beforeEach(async () => {
    setActivePinia(createPinia())
    localStorage.setItem('token',    'test-token')
    localStorage.setItem('username', 'admin')
    localStorage.setItem('role',     'ADMINISTRADOR')
    vi.clearAllMocks()
    setupApiMock()
    router = buildRouter()
    await router.push('/inscricoes')
  })

  afterEach(() => localStorage.clear())

  function mountView() {
    return mount(InscricoesView, { global: { plugins: [router] } })
  }

  // ── Renderização inicial ──────────────────────────────────────────────────

  it('renderiza o seletor de oficinas após carregar', async () => {
    const wrapper = mountView()
    await flushPromises()

    expect(wrapper.find('#sel-oficina').exists()).toBe(true)
  })

  it('exibe as oficinas como opções no seletor', async () => {
    const wrapper = mountView()
    await flushPromises()

    const opcoes = wrapper.findAll('#sel-oficina option')
    // +1 pela opção padrão "-- Escolha uma oficina --"
    expect(opcoes.length).toBe(oficinas.length + 1)
    expect(wrapper.text()).toContain('Java Básico')
    expect(wrapper.text()).toContain('Python Avançado')
  })

  it('não exibe tabela de inscrições antes de selecionar uma oficina', async () => {
    const wrapper = mountView()
    await flushPromises()

    expect(wrapper.find('table').exists()).toBe(false)
  })

  // ── Ao selecionar uma oficina ─────────────────────────────────────────────

  it('exibe a tabela de inscritos ao selecionar uma oficina', async () => {
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.find('table').exists()).toBe(true)
    expect(wrapper.text()).toContain('Maria Silva')
  })

  it('exibe badge "Ausente" para aluno sem presença confirmada', async () => {
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.find('.badge-ausente').exists()).toBe(true)
    expect(wrapper.find('.badge-ausente').text()).toBe('Ausente')
  })

  it('exibe botão "Marcar Presença" para aluno ausente', async () => {
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.text()).toContain('Marcar Presença')
  })

  it('não exibe botão de certificado para aluno ausente', async () => {
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.find('.btn-certificado').exists()).toBe(false)
  })

  // ── Com presença confirmada ───────────────────────────────────────────────

  it('exibe badge "Presente" quando aluno tem presença confirmada', async () => {
    setupApiMock(inscricoesComPresente)
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.find('.badge-presente').exists()).toBe(true)
    expect(wrapper.find('.badge-presente').text()).toBe('Presente')
  })

  it('exibe botão de certificado quando aluno está presente', async () => {
    setupApiMock(inscricoesComPresente)
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.find('.btn-certificado').exists()).toBe(true)
  })

  it('não exibe botão "Marcar Presença" quando aluno já está presente', async () => {
    setupApiMock(inscricoesComPresente)
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.find('.btn-presenca').exists()).toBe(false)
  })

  // ── Seção de inscrição ────────────────────────────────────────────────────

  it('exibe seletor de alunos disponíveis para inscrição', async () => {
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    // Maria (id=1) já está inscrita; João (id=2) e Ana (id=3) devem aparecer
    const selects = wrapper.findAll('select')
    // selects[0] = oficina, selects[1] = aluno para inscrever
    expect(selects.length).toBe(2)
    expect(wrapper.text()).toContain('João Rodrigues')
    expect(wrapper.text()).toContain('Ana Costa')
  })

  it('botão "Inscrever" está desabilitado quando nenhum aluno é selecionado', async () => {
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.find('.btn-inscrever').attributes('disabled')).toBeDefined()
  })

  it('chama api.post ao inscrever um aluno', async () => {
    api.post.mockResolvedValueOnce({ data: {} })
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    const selects = wrapper.findAll('select')
    await selects[1].setValue(2) // seleciona João
    await wrapper.find('.btn-inscrever').trigger('click')
    await flushPromises()

    expect(api.post).toHaveBeenCalledWith('/api/inscricoes', {
      alunoId:   2,
      oficinaId: 1,
    })
  })

  it('chama api.patch ao marcar presença', async () => {
    api.patch.mockResolvedValueOnce({ data: {} })
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    await wrapper.find('.btn-presenca').trigger('click')
    await flushPromises()

    expect(api.patch).toHaveBeenCalledWith('/api/inscricoes/10/presenca')
  })

  // ── Sem inscrições ────────────────────────────────────────────────────────

  it('exibe mensagem de vazio quando não há inscritos na oficina', async () => {
    setupApiMock([]) // nenhuma inscrição
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('#sel-oficina').setValue(1)
    await flushPromises()

    expect(wrapper.text()).toContain('Nenhum aluno inscrito nesta oficina')
  })
})
