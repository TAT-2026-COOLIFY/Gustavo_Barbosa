# IronFit — Sistema Web Inteligente para Gestão de Dietas, Treinos e Bem-Estar

TCC (ADS) full-stack: backend Spring Boot + frontend React. Projeto construído **fase por fase**, um módulo completo (backend + frontend) por vez, um commit por fase na branch `master`.

## Stack

**Backend** — `backend/` (Maven, Java 21, Spring Boot 4)
- Spring Web MVC, Spring Data JPA/Hibernate, Spring Security
- PostgreSQL + Flyway (migrations versionadas, sem `ddl-auto: update` — schema é sempre gerado via migration)
- JWT (`io.jsonwebtoken`/jjwt) com access token + refresh token
- springdoc-openapi (Swagger UI em `/swagger-ui.html`, docs em `/v3/api-docs`)
- Bucket4j (rate limit de login)
- Lombok (`@Getter @Setter @SuperBuilder @NoArgsConstructor @AllArgsConstructor` em entidades)
- Testes: JUnit 5 + Mockito + AssertJ (via `spring-boot-starter-*-test`, Spring Boot 4)

**Frontend** — `frontend/` (Vite, React 19, TypeScript)
- TailwindCSS v4 + shadcn/radix (componentes em `src/components/ui/`)
- Framer Motion (transições), `next-themes` (dark/light mode via classe `.dark`)
- React Router v7, Axios, React Hook Form + Zod
- **Sem lib de estado global** (nem Redux nem React Query) — `useState`/`useEffect` + funções `async` por feature em `api/*.ts`. Único Context: `AuthProvider` (`features/auth/hooks/useAuth.tsx`)

## Arquitetura backend (Clean Architecture por módulo)

Cada módulo de domínio é um pacote em `com.ironfit.api.<modulo>` com subpacotes:
```
<modulo>/
  controller/   REST controllers (@RestController, thin — delega tudo pro service)
  dto/          records (Request/Response DTOs)
  entity/       @Entity (extends BaseEntity) + enums
  mapper/       @Component simples (entity <-> DTO), sem MapStruct
  repository/   JpaRepository (métodos derivados + @Query quando precisa)
  service/      regra de negócio, @Transactional
```
Módulos compartilhados: `common/` (BaseEntity, Favorito genérico), `security/` (JWT, UsuarioUserDetails), `config/` (SecurityConfig, CORS, OpenAPI, JPA auditing), `exception/` (GlobalExceptionHandler + 3 exceções), `storage/` (upload de arquivos), `util/`.

**Padrões aplicados:** Repository Pattern, DTO Pattern, Builder (Lombok `@SuperBuilder`), Strategy + Factory (cálculo nutricional em `dieta/service/estrategia/`), Dependency Injection via construtor (sem `@Autowired` em campo).

**Convenções recorrentes** (replicadas em todo módulo novo):
- `BaseEntity`: id + `criadoEm`/`atualizadoEm` (auditoria automática via `JpaAuditingConfig`)
- Padrão "recurso do sistema" (template): `usuario_id NULL` = visível a todos; `usuario_id` preenchido = privado do usuário (usado em `Exercicio`/`Treino`, `Alimento`/`Dieta`)
- Padrão "um ativo por vez": campo `ativo`/`ativa` + índice único parcial `WHERE ativo` no Postgres (usado em `Objetivo`, `Dieta`)
- Favoritos: tabela genérica `favoritos` (`FavoritoTipo` enum + `itemId`), reaproveitada por qualquer módulo via `FavoritoService`
- Exceções: `ResourceNotFoundException`(404), `BusinessException`(409), `UnauthorizedException`(401) — capturadas pelo `GlobalExceptionHandler`, nunca criar exceção nova sem necessidade real
- Todo endpoint autenticado por padrão (`SecurityConfig` só libera rotas públicas explícitas: auth, swagger, uploads); usuário logado chega no controller via `@AuthenticationPrincipal UsuarioUserDetails`
- Migrations Flyway sequenciais `V<n>__descricao.sql` em `backend/src/main/resources/db/migration/`, nunca editar uma já commitada

## Arquitetura frontend

```
features/<modulo>/
  api/        funções async finas sobre axiosClient (uma função por endpoint)
  schemas/    zod schemas (campos numéricos como string + .refine, validação cruzada via .superRefine)
  components/ componentes "burros" (recebem dados + callbacks via props)
  pages/      telas (fetch em useEffect, formulários com react-hook-form+zodResolver)
types/<modulo>.ts   interfaces espelhando os DTOs do backend
```
- `lib/axiosClient.ts`: baseURL `/api`, token de acesso em memória, refresh token em localStorage, interceptor renova token em 401 automaticamente
- Cada página autenticada renderiza seu próprio header (botão voltar + `ThemeToggle`) — não existe ainda um shell/nav compartilhado (fica para quando o Dashboard real for construído)
- Estilo visual: classe utilitária `.glass` (glassmorphism, definida em `index.css`) + `Button variant="glass"`, cards com `Card className="glass border-0"`, fundo com blur decorativo (`blur-[110px]`) atrás do conteúdo, `framer-motion` fade+slide de entrada em toda página

## O que já foi implementado

| Fase | Módulo | Commit |
|---|---|---|
| 0 | Scaffolding (Spring Boot 4 + Java 21, Vite + React + TS + Tailwind v4) | `ccaabf2` |
| 1 | **Auth**: cadastro, login, JWT + refresh token, logout, esqueci/alterar senha | `9e7255e` |
| 2 | **Perfil**: dados pessoais (altura, sexo, data nasc., nível experiência, bio), foto de perfil, Objetivo (tipo + peso meta + data alvo, um ativo por vez) | `79cf027` |
| 3 | **Treinos**: CRUD, templates do sistema, duplicar, favoritar, grupos musculares, execução com cronômetro + registro de séries (`SessaoTreino`/`SerieExecutada`), histórico, volume semanal | `371ca45` |
| 4 | **Dietas**: catálogo de alimentos (~50 seed TACO + personalizados), CRUD de dieta com refeições/itens, **cálculo automático de metas** (TMB Mifflin-St Jeor + fator atividade + Strategy por objetivo via `EstrategiaMetaNutricionalFactory`), diário alimentar (`RegistroRefeicao`, separado do plano), 22 testes unitários novos | `6a00e3c` |
| 5 | **Hidratação**: meta diária de água (`MetaHidratacao`, padrão "um ativo por vez", default 2000ml auto-criado), registro de consumo (`RegistroHidratacao`, um por copo/clique, com desfazer), resumo do dia (total/meta/percentual), histórico dos últimos N dias com gráfico de barras (sem lib externa, SVG/CSS puro), sequência de dias consecutivos batendo a meta, 7 testes unitários novos | `9244cc9` |

Pacotes/pastas **já criadas mas ainda vazias** (placeholders arquiteturais para as próximas fases): backend `ai/`, `dashboard/`, `meditacao/`, `notificacao/`, `progresso/`; frontend `features/dashboard` (só tem `DashboardPlaceholderPage`), `features/ia`, `features/meditacao`, `features/notificacoes`, `features/progresso`.

Rotas atuais (`frontend/src/routes.tsx`): `/`, `/login`, `/cadastro`, `/esqueci-senha`, `/redefinir-senha` (públicas) + `/app` (dashboard placeholder), `/perfil`, `/treinos`, `/treinos/novo`, `/treinos/:id/editar`, `/treinos/execucao/:sessaoId`, `/treinos/historico`, `/dietas`, `/dietas/novo`, `/dietas/:id/editar`, `/dietas/diario`, `/hidratacao` (protegidas).

## O que falta fazer

Ordem sugerida (deixando o Dashboard real por último de propósito — ele agrega dados de todos os outros módulos, então implementá-lo cedo geraria retrabalho):

1. **Progresso**: peso, fotos de evolução, IMC, % gordura, massa magra, circunferências, gráficos comparativos — **nenhum campo de peso atual/IMC existe ainda em lugar nenhum do sistema**, este módulo é quem deve introduzir isso (histórico de peso), possivelmente alimentando `pesoReferenciaKg` da Dieta como sugestão
2. **IA**: assistente conversacional (integração com API OpenAI ou Claude) que gera treino/dieta/dicas a partir de uma conversa com o usuário — nenhuma integração de IA existe ainda no projeto (sem SDK, sem chave de API configurada); `OrigemTreino`/`OrigemDieta` já têm o valor `IA` no enum, preparado para quando esse módulo existir
3. **Meditação**: sessões guiadas, tempo, respiração, histórico
4. **Notificações**: água, treino, refeição, metas
5. **Dashboard real**: substituir `DashboardPlaceholderPage` — saudação, IMC, peso, objetivo, água, treino do dia, refeições/calorias/macros, gráficos semanal/mensal, dias consecutivos, conquistas (depende de 1-4 já existirem)
6. **Testes & Documentação** (fase dedicada, ao final): testes de integração cobrindo todos os módulos (hoje `dieta` e `hidratacao` têm testes unitários; `auth`/`perfil`/`treino` não têm nenhum teste), README profissional, DER, diagrama de classes/UML, diagrama de casos de uso, fluxogramas

## Como rodar

```bash
# Backend (precisa de Postgres local — ver backend/src/main/resources/application-dev.yml)
cd backend && ./mvnw spring-boot:run

# Testes backend
cd backend && ./mvnw test

# Frontend
cd frontend && npm install && npm run dev

# Build + lint frontend
cd frontend && npm run build && npm run lint
```
Swagger: `http://localhost:8080/swagger-ui.html`. Frontend dev: `http://localhost:5173`.

## Decisões arquiteturais notáveis (não óbvias pelo código)

- **Sem lib de estado/data-fetching no frontend** é intencional, não esquecimento — mantém o projeto simples de explicar na banca. Novas features devem seguir o mesmo padrão (`useState`+`useEffect`+`api/*.ts`), não introduzir React Query/Zustand no meio do caminho.
- **Dashboard fica por último** de propósito (ver seção "O que falta fazer").
- **Módulo Dieta não depende de Progresso** (que ainda não existe): guarda um snapshot (`pesoReferenciaKg`, `nivelAtividade`) na própria `Dieta` em vez de uma FK para histórico de peso. Quando Progresso existir, pode sugerir o último peso registrado para pré-preencher o formulário, sem criar acoplamento rígido.
- **Sem repositório remoto configurado** (`git remote -v` vazio) — todo o progresso está apenas no `.git` local desta máquina. Sugerir ao usuário configurar um remoto (GitHub) para backup fora da máquina é válido a qualquer momento.
