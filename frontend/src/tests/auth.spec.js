import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '../stores/auth.js'
import api from '../api/axios.js'

vi.mock('../api/axios.js', () => ({
  default: { post: vi.fn() },
}))

describe('useAuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('inicia sem usuário autenticado quando localStorage está vazio', () => {
    const auth = useAuthStore()
    expect(auth.token).toBeNull()
    expect(auth.username).toBeNull()
    expect(auth.role).toBeNull()
  })

  it('login armazena token, username e role no estado e no localStorage', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt-abc', username: 'admin', role: 'ADMINISTRADOR', primeiroAcesso: false },
    })
    const auth = useAuthStore()
    await auth.login('admin', 'senha123')

    expect(auth.token).toBe('jwt-abc')
    expect(auth.username).toBe('admin')
    expect(auth.role).toBe('ADMINISTRADOR')
    expect(localStorage.getItem('token')).toBe('jwt-abc')
    expect(localStorage.getItem('username')).toBe('admin')
  })

  it('login registra primeiroAcesso = true quando indicado pelo servidor', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt-abc', username: 'novo', role: 'TUTOR', primeiroAcesso: true },
    })
    const auth = useAuthStore()
    await auth.login('novo', 'senha123')

    expect(auth.primeiroAcesso).toBe(true)
    expect(localStorage.getItem('primeiroAcesso')).toBe('true')
  })

  it('logout limpa estado e localStorage', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt-abc', username: 'admin', role: 'ADMINISTRADOR', primeiroAcesso: false },
    })
    const auth = useAuthStore()
    await auth.login('admin', 'senha123')
    auth.logout()

    expect(auth.token).toBeNull()
    expect(auth.username).toBeNull()
    expect(auth.role).toBeNull()
    expect(localStorage.getItem('token')).toBeNull()
    expect(localStorage.getItem('username')).toBeNull()
  })

  it('isAdmin retorna true para role ADMINISTRADOR', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt', username: 'admin', role: 'ADMINISTRADOR', primeiroAcesso: false },
    })
    const auth = useAuthStore()
    await auth.login('admin', 'senha')

    expect(auth.isAdmin()).toBe(true)
  })

  it('isAdmin retorna false para role TUTOR', async () => {
    api.post.mockResolvedValueOnce({
      data: { token: 'jwt', username: 'tutor', role: 'TUTOR', primeiroAcesso: false },
    })
    const auth = useAuthStore()
    await auth.login('tutor', 'senha')

    expect(auth.isAdmin()).toBe(false)
  })

  it('isAdmin retorna false quando não há usuário logado', () => {
    const auth = useAuthStore()
    expect(auth.isAdmin()).toBe(false)
  })

  it('recupera token do localStorage ao inicializar', () => {
    localStorage.setItem('token', 'token-salvo')
    localStorage.setItem('username', 'usuario-salvo')
    localStorage.setItem('role', 'TUTOR')
    // Recria o store para simular nova instância (nova aba/reload)
    setActivePinia(createPinia())
    const auth = useAuthStore()

    expect(auth.token).toBe('token-salvo')
    expect(auth.username).toBe('usuario-salvo')
  })
})
