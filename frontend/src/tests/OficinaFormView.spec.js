import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'
import OficinaFormView from '../views/OficinaFormView.vue'

// Mock do store de oficinas para isolar o componente dos efeitos colaterais
const mockCriar    = vi.fn()
const mockAtualizar = vi.fn()

vi.mock('../stores/oficinaStore.js', () => ({
  useOficinaStore: () => ({
    criar:    mockCriar,
    atualizar: mockAtualizar,
  }),
}))

// Mock da função de busca de oficina individual (usada no modo edição)
vi.mock('../api/oficina.js', () => ({
  buscarOficina: vi.fn().mockResolvedValue({
    data: { nome: 'Java Básico', descricao: 'Curso introdutório', data: '2026-07-01', cargaHoraria: 8 },
  }),
}))

vi.mock('../api/axios.js', () => ({
  default: { get: vi.fn() },
}))

function buildRouter(path) {
  const routes = [
    { path: '/oficinas/nova',          component: OficinaFormView },
    { path: '/oficinas/:id/editar',    component: OficinaFormView },
    { path: '/oficinas',               component: { template: '<div/>' } },
    { path: '/alunos',                 component: { template: '<div/>' } },
    { path: '/inscricoes',             component: { template: '<div/>' } },
  ]
  const router = createRouter({ history: createMemoryHistory(), routes })
  return router
}

describe('OficinaFormView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.setItem('token',    'test-token')
    localStorage.setItem('username', 'admin')
    localStorage.setItem('role',     'ADMINISTRADOR')
    mockCriar.mockReset()
    mockAtualizar.mockReset()
  })

  afterEach(() => localStorage.clear())

  // ── Modo "Nova Oficina" ────────────────────────────────────────────────────

  it('exibe título "Nova Oficina" quando a rota não tem ID', async () => {
    const router = buildRouter()
    await router.push('/oficinas/nova')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })

    expect(wrapper.text()).toContain('Nova Oficina')
  })

  it('exibe texto "Cadastrar" no botão de submit para nova oficina', async () => {
    const router = buildRouter()
    await router.push('/oficinas/nova')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })

    expect(wrapper.find('button[type="submit"]').text()).toBe('Cadastrar')
  })

  it('renderiza os três campos obrigatórios (nome, data, carga horária)', async () => {
    const router = buildRouter()
    await router.push('/oficinas/nova')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })

    const inputs = wrapper.findAll('input')
    // nome (text), data (date), cargaHoraria (number) = pelo menos 3
    expect(inputs.length).toBeGreaterThanOrEqual(3)
    const tipos = inputs.map(i => i.attributes('type') || 'text')
    expect(tipos).toContain('date')
    expect(tipos).toContain('number')
  })

  it('renderiza o campo de descrição (textarea)', async () => {
    const router = buildRouter()
    await router.push('/oficinas/nova')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })

    expect(wrapper.find('textarea').exists()).toBe(true)
  })

  it('chama store.criar ao submeter o formulário no modo nova', async () => {
    mockCriar.mockResolvedValueOnce({})
    const router = buildRouter()
    await router.push('/oficinas/nova')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })

    // Preenche campos obrigatórios
    const inputs = wrapper.findAll('input')
    await inputs[0].setValue('Oficina de Testes')                          // nome
    await inputs.find(i => i.attributes('type') === 'date').setValue('2026-08-01') // data
    await inputs.find(i => i.attributes('type') === 'number').setValue(4) // carga

    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(mockCriar).toHaveBeenCalledOnce()
  })

  // ── Modo "Editar Oficina" ──────────────────────────────────────────────────

  it('exibe título "Editar Oficina" quando a rota tem ID', async () => {
    const router = buildRouter()
    await router.push('/oficinas/1/editar')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })
    await flushPromises()

    expect(wrapper.text()).toContain('Editar Oficina')
  })

  it('exibe texto "Atualizar" no botão de submit para edição', async () => {
    const router = buildRouter()
    await router.push('/oficinas/1/editar')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })
    await flushPromises()

    expect(wrapper.find('button[type="submit"]').text()).toBe('Atualizar')
  })

  it('carrega os dados da oficina ao entrar em modo edição', async () => {
    const router = buildRouter()
    await router.push('/oficinas/1/editar')
    const wrapper = mount(OficinaFormView, { global: { plugins: [router] } })
    await flushPromises()

    // O primeiro input (nome) deve ter o valor carregado pelo mock
    expect(wrapper.findAll('input')[0].element.value).toBe('Java Básico')
  })
})
