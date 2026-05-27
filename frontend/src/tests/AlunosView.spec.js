import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'
import AlunosView from '../views/AlunosView.vue'
import api from '../api/axios.js'

vi.mock('../api/axios.js', () => ({
  default: {
    get:    vi.fn(),
    post:   vi.fn(),
    put:    vi.fn(),
    delete: vi.fn(),
  },
}))

const alunosMock = [
  { id: 1, nome: 'Maria Silva',    ra: '2267845', emailInstitucional: 'maria@utfpr.edu.br', telefone: '(41) 99999-1111', dataNascimento: '2000-01-15', curso: 'Engenharia de Software' },
  { id: 2, nome: 'João Rodrigues', ra: '2350580', emailInstitucional: '',                  telefone: '',                dataNascimento: '',           curso: '' },
]

function buildRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/alunos',  component: AlunosView },
      { path: '/login',   component: { template: '<div/>' } },
      { path: '/oficinas', component: { template: '<div/>' } },
      { path: '/inscricoes', component: { template: '<div/>' } },
    ],
  })
}

describe('AlunosView', () => {
  let router

  beforeEach(async () => {
    setActivePinia(createPinia())
    localStorage.setItem('token',    'test-token')
    localStorage.setItem('username', 'admin')
    localStorage.setItem('role',     'ADMINISTRADOR')
    vi.clearAllMocks()
    api.get.mockResolvedValue({ data: [] })
    router = buildRouter()
    await router.push('/alunos')
  })

  afterEach(() => localStorage.clear())

  function mountView() {
    return mount(AlunosView, { global: { plugins: [router] } })
  }

  // ── Renderização básica ──────────────────────────────────────────────────────

  it('exibe "Nenhum aluno cadastrado" quando a lista está vazia', async () => {
    const wrapper = mountView()
    await flushPromises()
    expect(wrapper.text()).toContain('Nenhum aluno cadastrado')
  })

  it('renderiza a tabela quando há alunos', async () => {
    api.get.mockResolvedValueOnce({ data: alunosMock })
    const wrapper = mountView()
    await flushPromises()

    expect(wrapper.text()).toContain('Maria Silva')
    expect(wrapper.text()).toContain('João Rodrigues')
    expect(wrapper.find('table').exists()).toBe(true)
  })

  it('exibe "-" para campos opcionais não preenchidos', async () => {
    api.get.mockResolvedValueOnce({ data: alunosMock })
    const wrapper = mountView()
    await flushPromises()

    const linhas = wrapper.findAll('tbody tr')
    // João não tem email, curso nem telefone
    expect(linhas[1].text()).toContain('-')
  })

  // ── Validação do campo Telefone ───────────────────────────────────────────────

  it('botão submit está habilitado quando telefone está vazio', async () => {
    const wrapper = mountView()
    await flushPromises()

    const submitBtn = wrapper.find('button[type="submit"]')
    expect(submitBtn.attributes('disabled')).toBeUndefined()
  })

  it('botão submit está desabilitado quando telefone tem entre 1 e 9 caracteres', async () => {
    const wrapper = mountView()
    await flushPromises()

    const telInput = wrapper.find('input[pattern=".{10,}"]')
    await telInput.setValue('12345')

    const submitBtn = wrapper.find('button[type="submit"]')
    expect(submitBtn.attributes('disabled')).toBeDefined()
  })

  it('exibe mensagem de erro quando telefone tem entre 1 e 9 caracteres', async () => {
    const wrapper = mountView()
    await flushPromises()

    const telInput = wrapper.find('input[pattern=".{10,}"]')
    await telInput.setValue('9999')

    expect(wrapper.text()).toContain('Mínimo 10 dígitos')
  })

  it('botão submit está habilitado quando telefone tem exatamente 10 caracteres', async () => {
    const wrapper = mountView()
    await flushPromises()

    const telInput = wrapper.find('input[pattern=".{10,}"]')
    await telInput.setValue('1234567890')

    const submitBtn = wrapper.find('button[type="submit"]')
    expect(submitBtn.attributes('disabled')).toBeUndefined()
  })

  it('mensagem de erro some quando telefone atinge 10 caracteres', async () => {
    const wrapper = mountView()
    await flushPromises()

    const telInput = wrapper.find('input[pattern=".{10,}"]')
    await telInput.setValue('12345')        // inválido
    expect(wrapper.text()).toContain('Mínimo 10 dígitos')

    await telInput.setValue('1234567890')   // válido
    expect(wrapper.text()).not.toContain('Mínimo 10 dígitos')
  })

  // ── Navegação ────────────────────────────────────────────────────────────────

  it('exibe link de navegação para Oficinas', () => {
    const wrapper = mountView()
    expect(wrapper.html()).toContain('href="/oficinas"')
  })

  it('exibe link de navegação para Inscrições', () => {
    const wrapper = mountView()
    expect(wrapper.html()).toContain('href="/inscricoes"')
  })

  // ── Modo de edição ────────────────────────────────────────────────────────────

  it('botão "Cancelar" não é visível fora do modo de edição', async () => {
    const wrapper = mountView()
    await flushPromises()
    expect(wrapper.find('.btn-cancelar').exists()).toBe(false)
  })

  it('botão "Cancelar" aparece ao entrar no modo de edição', async () => {
    api.get.mockResolvedValueOnce({ data: alunosMock })
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('.btn-editar').trigger('click')
    expect(wrapper.find('.btn-cancelar').exists()).toBe(true)
  })

  it('texto do botão submit muda para "Atualizar" no modo de edição', async () => {
    api.get.mockResolvedValueOnce({ data: alunosMock })
    const wrapper = mountView()
    await flushPromises()

    await wrapper.find('.btn-editar').trigger('click')
    expect(wrapper.find('button[type="submit"]').text()).toBe('Atualizar')
  })
})
