# Sistema de Controle de Oficinas — ELLP

> Projeto desenvolvido para a disciplina **IF66K — Oficina de Integração 2**
> Projeto de extensão: [ELLP — Ensino Lúdico de Lógica e Programação](https://www.utfpr.edu.br/campus/cornelioprocopio/extensao/atividades-extensao/projeto-de-extensao-ellp-ensino-ludico-de-logica-e-programacao)

---

## 👥 Equipe

| Nome | RA |
|---|---|
| Davi Calheiros Quintella Souto | 2267845 |
| Gabriel Brochi Zani Dias | 2576210 |
| Gabriel Rodrigues Granjeia | 2350580 |

**Professor:** Antonio Carlos Fernandes da Silva
**Turma:** ES71 (2026_01)

---

## 📋 Descrição do Projeto

Este sistema atende às necessidades do projeto de extensão ELLP e tem como objetivo o controle e gerenciamento das oficinas oferecidas. O sistema permite o cadastro e autenticação de usuários (professores e tutores), gerenciamento de oficinas e alunos, controle de presenças e emissão de certificados de participação.

---

## ✅ Requisitos Funcionais

| ID | Descrição | Prioridade |
|---|---|---|
| RF01 | Cadastrar usuários (professores e tutores) | Essencial |
| RF02 | Autenticar usuários no sistema | Essencial |
| RF03 | Gerenciar Oficinas (criar, editar, listar, excluir) | Essencial |
| RF04 | Gerenciar Alunos (cadastrar, editar, listar, excluir) | Essencial |
| RF05 | Inscrever alunos em uma oficina | Essencial |
| RF06 | Registrar a presença de alunos em uma oficina | Essencial |
| RF07 | Gerar certificados de participação para alunos que concluíram uma oficina | Essencial |

---

## 🛠️ Tecnologias Utilizadas

| Camada | Tecnologia |
|---|---|
| **Front-end** | Vue.js 3 |
| **Back-end** | Java com Spring Boot |
| **Banco de Dados** | PostgreSQL |
| **Testes — Back-end** | JUnit 5 + Mockito |
| **Testes — Front-end** | Vitest + Vue Test Utils |
| **Gerenciamento de código** | GitHub |
| **Gestão de tarefas** | GitHub Projects (Kanban) |

---

## 🏗️ Arquitetura do Sistema

A arquitetura segue o modelo **cliente-servidor**, com separação clara de responsabilidades entre as camadas.

```
┌────────────────────┐        HTTP/REST        ┌──────────────────────┐        JDBC        ┌──────────────────┐
│                    │  ──── requisições ────▶  │                      │  ─── queries ────▶ │                  │
│  Front-end (SPA)   │                          │   Back-end (API)     │                    │   PostgreSQL     │
│     Vue.js 3       │  ◀─── respostas JSON ──  │   Spring Boot        │  ◀── resultados ── │                  │
│                    │                          │                      │                    │                  │
└────────────────────┘                          └──────────────────────┘                    └──────────────────┘
```

### Componentes

- **Front-end (Cliente):** Aplicação de página única (SPA) desenvolvida em Vue.js 3, responsável pela interface com o usuário e consumo da API REST.
- **Back-end (Servidor):** API RESTful desenvolvida em Java com Spring Boot, responsável pelas regras de negócio, autenticação e comunicação com o banco de dados.
- **Banco de Dados:** PostgreSQL para persistência relacional dos dados.

---

## 🧪 Estratégia de Testes Automatizados

A estratégia visa garantir qualidade e confiabilidade das funcionalidades em ambas as camadas da aplicação.

### Back-end (JUnit 5 + Mockito)

- **Testes unitários:** validação das regras de negócio nas classes de serviço, com uso de mocks para isolar dependências externas (Mockito).
- **Testes de integração:** validação das rotas da API e fluxo completo entre controller, service e repositório.

### Front-end (Vitest + Vue Test Utils)

- **Testes de componente:** verificação do comportamento de cada componente Vue de forma isolada.
- **Testes de integração:** validação dos fluxos de navegação e interação entre componentes.

### Métrica de Avaliação

Será utilizada a métrica de **cobertura de código** para avaliar a abrangência dos testes em ambas as camadas.

---

## 📅 Cronograma

O projeto utiliza a metodologia **Scrum**, dividido em três fases.

### Fase 1 — Planejamento (23/03/2026 a 12/04/2026)

Definição de requisitos, arquitetura, tecnologias e estratégia de testes. Entrega deste documento e dos artefatos iniciais do repositório.

### Fase 2 — Sprint 1 (13/04/2026 a 10/05/2026)

| Atividade | Requisito |
|---|---|
| Configuração do ambiente de desenvolvimento | — |
| Implementação do cadastro de usuários | RF01 |
| Implementação da autenticação | RF02 |
| Implementação do gerenciamento de Alunos | RF04 |

> ⚠️ **Intervalo (11/05/2026 a 24/05/2026):** período reservado para provas presenciais da instituição.

### Fase 3 — Sprint 2 (25/05/2026 a 14/06/2026)

| Atividade | Requisito |
|---|---|
| Implementação do gerenciamento de Oficinas | RF03 |
| Implementação da inscrição de alunos em oficinas | RF05 |
| Implementação do registro de presença | RF06 |
| Geração de certificados de participação | RF07 |

---

## 📊 Status do Projeto

| Fase | Status |
|---|---|
| Fase 1 — Planejamento | ✅ Concluída |
| Fase 2 — Sprint 1 | ✅ Concluída |
| Fase 3 — Sprint 2 | ✅ Concluída |

### Requisitos Implementados

| ID | Descrição | Status |
|---|---|---|
| RF01 | Cadastrar usuários (professores e tutores) | ✅ Implementado |
| RF02 | Autenticar usuários no sistema | ✅ Implementado |
| RF03 | Gerenciar Oficinas (criar, editar, listar, excluir) | ✅ Implementado |
| RF04 | Gerenciar Alunos (cadastrar, editar, listar, excluir) | ✅ Implementado |
| RF05 | Inscrever alunos em uma oficina | ✅ Implementado |
| RF06 | Registrar a presença de alunos em uma oficina | ✅ Implementado |
| RF07 | Gerar certificados de participação | ✅ Implementado |

---

## ⚙️ Configuração do Ambiente

### Pré-requisitos

- Java 17+
- Node.js 18+
- Docker e Docker Compose
- Maven 3.9+

### Banco de Dados

```bash
# Sobe o PostgreSQL via Docker (porta 5432)
sudo docker-compose up -d

```

> As credenciais padrão de desenvolvimento são `postgres/postgres`, banco `oficinas_ellp`. As tabelas são criadas automaticamente pelo Hibernate na inicialização do back-end.

### Back-end

```bash
cd backend

# Executar os testes
mvn test

# Executar a aplicação
mvn spring-boot:run

```

> As configurações de banco e JWT estão em `backend/src/main/resources/application.properties`. Em produção, sobrescreva via variáveis de ambiente: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET`.

### Conta padrão

Na primeira execução, o sistema cria automaticamente uma conta de administrador:

| Campo | Valor |
|---|---|
| Usuário | `admin` |
| Senha | `admin` |

> O sistema solicitará a troca de senha no primeiro acesso.

### Front-end

```bash
cd frontend

# Instalar dependências
npm install

# Executar os testes
npm test

# Executar em modo de desenvolvimento
npm run dev

```

> O Vite proxy redireciona chamadas para `/api` ao back-end em `localhost:8080`, evitando problemas de CORS em desenvolvimento.

---

## 🔁 Processo de Desenvolvimento

- Funcionalidades documentadas como **issues** no GitHub. (Não implementamos)
- Desenvolvimento realizado em **branches** por feature, com pull requests para a branch principal. (Não implementamos)
- Gestão de tarefas via **GitHub Projects** (Kanban com colunas: Backlog / In Progress / Review / Done). (Não implementamos)
- Testes automatizados obrigatórios para cada funcionalidade implementada.

---

## 📁 Estrutura do Repositório

```
projeto-oficina-integracao-ellp/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ellp/oficinas/   ← código fonte
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/java/com/ellp/oficinas/  ← testes
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── views/        ← páginas
│   │   ├── components/   ← componentes reutilizáveis
│   │   ├── stores/       ← estado global (Pinia)
│   │   ├── router/       ← rotas (Vue Router)
│   │   └── api/          ← chamadas HTTP (Axios)
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── docker-compose.yml
└── README.md
```