import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createMemoryHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import api from '../api/axios.js'

vi.mock('../api/axios.js', () => ({
  default: { post: vi.fn() },
}))

function buildRouter() {
  return createRouter({
    history: createMemoryHistory(),
    routes: [
      { path: '/login',       component: LoginView },
      { path: '/alunos',      component: { template: '<div>Alunos</div>' } },
      { path: '/trocar-senha', component: { template: '<div>Trocar</div>' } },
    ],
  })
}

describe('LoginView', () => {
  let router

  beforeEach(async () => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
    router = buildRouter()
    await router.push('/login')
  })

  function mountView() {
    return mount(LoginView, { global: { plugins: [router] } })
  }

  it('renderiza o formulário com campos de usuário, senha e botão Entrar', () => {
    const wrapper = mountView()
    const inputs = wrapper.findAll('input')
    expect(inputs).toHaveLength(2)
    expect(inputs[1].attributes('type')).toBe('password')
    expect(wrapper.find('button[type="submit"]').text()).toBe('Entrar')
  })

  it('botão não está desabilitado inicialmente', () => {
    const wrapper = mountView()
    expect(wrapper.find('button[type="submit"]').attributes('disabled')).toBeUndefined()
  })

  it('exibe "Entrando..." no botão enquanto a requisição está em andamento', async () => {
    let resolve
    api.post.mockReturnValueOnce(new Promise(r => { resolve = r }))
    const wrapper = mountView()

    await wrapper.findAll('input')[0].setValue('admin')
    await wrapper.findAll('input')[1].setValue('senha')
    wrapper.find('form').trigger('submit')
    await wrapper.vm.$nextTick()

    expect(wrapper.find('button[type="submit"]').text()).toBe('Entrando...')
    resolve({ data: { token: 'x', username: 'admin', role: 'ADMINISTRADOR', primeiroAcesso: false } })
  })

  it('exibe mensagem de erro quando o login falha', async () => {
    api.post.mockRejectedValueOnce(new Error('401'))
    const wrapper = mountView()

    await wrapper.findAll('input')[0].setValue('errado')
    await wrapper.findAll('input')[1].setValue('errada')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.text()).toContain('Usuário ou senha inválidos')
  })

  it('redireciona para /alunos após login bem-sucedido sem primeiro acesso', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt', username: 'admin', role: 'ADMINISTRADOR', primeiroAcesso: false },
    })
    const wrapper = mountView()

    await wrapper.findAll('input')[0].setValue('admin')
    await wrapper.findAll('input')[1].setValue('senha')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/alunos')
  })

  it('redireciona para /trocar-senha no primeiro acesso', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt', username: 'novo', role: 'TUTOR', primeiroAcesso: true },
    })
    const wrapper = mountView()

    await wrapper.findAll('input')[0].setValue('novo')
    await wrapper.findAll('input')[1].setValue('senha')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/trocar-senha')
  })
})
