# IronFit — Sistema Web Inteligente para Gestão de Dietas, Treinos e Bem-Estar

TCC (ADS) full-stack: backend Spring Boot + frontend React. Projeto construído **fase por fase**, um módulo completo (backend + frontend) por vez, um commit por fase na branch `master`.

## Resumo geral do progresso (para retomar rapidamente)

**10 fases concluídas (0 a 9)**, todas com backend + frontend integrados, testados e commitados — cada uma virou um módulo completo de ponta a ponta antes de passar pra próxima. A jornada até aqui, em ordem:

0. Scaffolding do projeto (Spring Boot 4 + Vite/React/TS).
1. **Auth** — cadastro, login com JWT+refresh token, logout, recuperação de senha.
2. **Perfil** — dados pessoais e Objetivo (tipo + peso meta).
3. **Treinos** — CRUD, templates, execução com cronômetro, histórico, volume semanal.
4. **Dietas** — catálogo de alimentos, CRUD com **cálculo automático de metas nutricionais** (TMB + Strategy por objetivo), diário alimentar.
5. **Hidratação** — meta diária, registro rápido de consumo, histórico com gráfico, sequência de dias.
6. **Progresso** — peso/IMC/% gordura/circunferências/fotos de evolução; **introduziu IMC no sistema** (não existia antes).
7. **IA** — assistente conversacional via OpenAI; gera *rascunhos* de treino/dieta que o usuário revisa e confirma antes de qualquer gravação real (nunca grava sozinha).
8. **Meditação** — catálogo de sessões guiadas, execução com cronômetro e animação de respiração, histórico.
9. **Notificações** — central in-app (água/treino/refeição/metas), gerada sob demanda, sem push nem job agendado.

**O que falta** (só isso): (1) **Dashboard real** substituindo a tela placeholder — agora que todos os módulos que ele agrega já existem, não há mais motivo pra adiar; (2) fase final de **Testes & Documentação** (testes de integração, README, DER, UML, casos de uso); (3) **testar a IA de ponta a ponta** quando o usuário configurar sua própria chave da OpenAI (adiado por escolha dele — ver seção "Como rodar").

Detalhes de cada fase (arquivos, decisões técnicas, hash do commit) estão na tabela abaixo e em "Decisões arquiteturais notáveis". Antes de continuar em uma nova sessão: leia este resumo, confira `git log --oneline -15` pra confirmar que bate com a tabela, e comece pela próxima fase da lista "O que falta fazer".

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
| 6 | **Progresso**: `RegistroProgresso` (peso, % gordura, 5 circunferências, foto de evolução, observações) único por dia (upsert), **introduz IMC e massa magra no sistema** (calculados a partir da altura do Perfil, nenhum campo existia antes), histórico com gráfico de peso (SVG puro, com linha de meta do Objetivo quando existir), comparação de fotos antes/agora, sugestão (soft, sem FK) do último peso registrado ao criar uma Dieta nova, 6 testes unitários novos | `2ddd085` |
| 7 | **IA**: assistente conversacional via OpenAI (chat completions, `response_format: json_object`), histórico de conversas/mensagens persistido (`ConversaIa`/`MensagemIa`), prompt inclui o catálogo real de exercícios/alimentos (o modelo só pode sugerir ids existentes), **fluxo de rascunho para revisão**: sugestão de treino/dieta vira `RascunhoIa` (`PENDENTE`), usuário revisa em `/treinos/novo` ou `/dietas/novo` já pré-preenchido e só confirmar cria o Treino/Dieta de verdade com `origem=IA` (`TreinoService`/`DietaService` ganharam overload `criar(usuarioId, request, origem)`); sem chave de API configurada nesta sessão (ver `OPENAI_API_KEY` em "Como rodar"), integração construída e testada via mock do client, nunca chamada de verdade ainda; 10 testes unitários novos | `09c7d8f` |
| 8 | **Meditação**: catálogo de sessões guiadas do sistema (`Meditacao`, 9 sessões seed em 5 tipos — sem versão privada por usuário, escopo não pedia), execução com cronômetro (`SessaoMeditacao`, mesmo padrão iniciar/concluir de `SessaoTreino`), animação de respiração guiada (inspirar/segurar/expirar em CSS puro, sem lib) para sessões do tipo `RESPIRACAO`, histórico e sequência de dias consecutivos, 6 testes unitários novos | `da9ebdb` |
| 9 | **Notificações**: central in-app (`Notificacao`, sem push do navegador — sem service worker/infra pra isso no projeto), geradas sob demanda (sem job agendado) sempre que `GET /api/notificacoes` é chamado: água (nenhum registro hoje / meta batida), treino (nenhuma sessão concluída hoje, só se o usuário tiver algum treino cadastrado), refeição (dieta ativa com refeições planejadas mas nenhuma registrada hoje) — dedupe por tipo+dia via `existsByUsuarioIdAndTipoAndCriadoEmAfter`; sininho com contador de não lidas no dashboard placeholder; 9 testes unitários novos | `bd9cd24` |

Pacotes/pastas **já criadas mas ainda vazias** (placeholders arquiteturais para as próximas fases): backend `dashboard/`; frontend `features/dashboard` (só tem `DashboardPlaceholderPage`).

Rotas atuais (`frontend/src/routes.tsx`): `/`, `/login`, `/cadastro`, `/esqueci-senha`, `/redefinir-senha` (públicas) + `/app` (dashboard placeholder), `/perfil`, `/treinos`, `/treinos/novo`, `/treinos/:id/editar`, `/treinos/execucao/:sessaoId`, `/treinos/historico`, `/dietas`, `/dietas/novo`, `/dietas/:id/editar`, `/dietas/diario`, `/hidratacao`, `/progresso`, `/ia`, `/meditacao`, `/meditacao/execucao/:sessaoId`, `/meditacao/historico`, `/notificacoes` (protegidas).

## O que falta fazer

Só resta um item antes da fase final de testes/documentação:

1. **Dashboard real**: substituir `DashboardPlaceholderPage` — saudação, IMC, peso, objetivo, água, treino do dia, refeições/calorias/macros, gráficos semanal/mensal, dias consecutivos, conquistas (todos os módulos que ele agrega já existem agora)
2. **Testes & Documentação** (fase dedicada, ao final): testes de integração cobrindo todos os módulos (hoje `dieta`, `hidratacao`, `progresso`, `ia`, `meditacao` e `notificacao` têm testes unitários; `auth`/`perfil`/`treino` não têm nenhum teste), README profissional, DER, diagrama de classes/UML, diagrama de casos de uso, fluxogramas
3. **Testar a integração de IA de ponta a ponta** assim que houver uma chave da OpenAI disponível (ver `OPENAI_API_KEY` em "Como rodar") — hoje só validada com o client mockado

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

Módulo de IA (opcional — sem a variável, o assistente responde com erro 409 amigável em vez de quebrar):
```bash
export OPENAI_API_KEY=sk-...        # obrigatória para o chat funcionar de verdade
export OPENAI_MODEL=gpt-4o-mini     # opcional, esse é o default
export OPENAI_BASE_URL=https://api.openai.com/v1  # opcional, útil pra apontar pra um proxy/gateway compatível
```

## Decisões arquiteturais notáveis (não óbvias pelo código)

- **Sem lib de estado/data-fetching no frontend** é intencional, não esquecimento — mantém o projeto simples de explicar na banca. Novas features devem seguir o mesmo padrão (`useState`+`useEffect`+`api/*.ts`), não introduzir React Query/Zustand no meio do caminho.
- **Dashboard fica por último** de propósito (ver seção "O que falta fazer").
- **Módulo Dieta não tem FK para Progresso**: guarda um snapshot (`pesoReferenciaKg`, `nivelAtividade`) na própria `Dieta` em vez de uma referência a `RegistroProgresso`. Agora que Progresso existe, `DietaFormPage` busca `GET /progresso/ultimo` só no frontend (best-effort, ignora 404) para pré-preencher `pesoReferenciaKg` de uma dieta nova — integração só de UX, sem acoplamento no backend.
- **Sem repositório remoto configurado** (`git remote -v` vazio) — todo o progresso está apenas no `.git` local desta máquina. Sugerir ao usuário configurar um remoto (GitHub) para backup fora da máquina é válido a qualquer momento.
- **Spring Boot 4 usa Jackson 3 por padrão** (`tools.jackson.databind.ObjectMapper`, não `com.fasterxml.jackson`), e não há bean de `RestClient.Builder` auto-configurado com os starters atuais. O módulo `ia` precisava especificamente de Jackson 2 clássico (`JsonNode`, `treeToValue`) e por isso declara seu próprio bean `ObjectMapper` em `IaJacksonConfig` (Jackson 2 já está no classpath via springdoc/jjwt) e constrói o `RestClient` direto via `RestClient.builder()` em vez de injetar `RestClient.Builder`. Ao integrar qualquer outra API externa, não presuma que esses beans do Spring Boot 3 "de sempre" existem — confira primeiro.
- **IA gera rascunho, nunca grava direto**: `TreinoService.criar`/`DietaService.criar` ganharam um overload com `origem` (`MANUAL` continua sendo o default do overload de 2 argumentos, usado pelos controllers normais); o módulo `ia` é o único chamador do overload de 3 argumentos com `origem=IA`, e só na confirmação explícita do rascunho pelo usuário — nunca a partir da resposta do modelo diretamente.
- **Notificações são in-app, não push**: decisão deliberada (não esquecimento) — o projeto não tem service worker nem infra de Web Push/FCM, e adicionar isso só para esta fase seria desproporcional. São geradas sob demanda (sem `@Scheduled`/job): toda vez que `GET /api/notificacoes` é chamado, `NotificacaoService` reavalia água/treino/refeição do dia e cria no máximo uma notificação nova por tipo por dia (a dedupe é por `criadoEm` do próprio dia, não por um campo "data" dedicado). Isso significa que uma notificação só aparece depois que o usuário abre a tela pelo menos uma vez naquele dia — comportamento aceitável para o escopo do TCC, mas vale mencionar na defesa se perguntarem sobre notificação "em tempo real".
- **Ambiente local instável durante o desenvolvimento**: backend (`spring-boot:run`) e frontend (`npm run dev`) não ficam permanentemente no ar — precisam ser iniciados manualmente e caem se o terminal fechar. Um incidente real desta sessão: dois processos de frontend concorrentes (um em background, outro do usuário) fizeram o `npm run dev` do usuário cair na porta 5174 em vez da 5173 configurada em `ironfit.cors.allowed-origins`, causando 403 de CORS só no navegador (requisições diretas via curl/proxy funcionavam normalmente). Se aparecer "não foi possível entrar"/erro genérico de rede no login, confira antes: (1) se os dois servidores estão de pé, (2) se o frontend está mesmo na porta 5173, (3) o rate limiter de login (Bucket4j, 5 tentativas/15min por IP, em memória) não travou por testes repetidos.
