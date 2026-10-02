# Validação local — 02/10/2026

- `mvnw.cmd clean compile`: sucesso.
- `mvnw.cmd test` com `RUN_LOCAL_POSTGRES_TESTS=true` e credenciais na sessão:
  **26 testes, zero falhas, zero erros, zero ignorados**.
- 6 testes unitários de service, 9 de contrato HTTP em H2, 9 do mesmo contrato
  em PostgreSQL 17 local e 2 testes de inicialização do contexto.
- `mvnw.cmd -Ptestcontainers test`: sucesso com Docker 28.1.1 e imagem
  `postgres:17-alpine` (PostgreSQL 17.11). Os 9 cenários de
  `ProdutoControllerIntegrationTest` passaram, incluindo 201, 409 e 422.
  Essa execução teve 26 testes executados sem falhas e 9 testes de PostgreSQL
  local ignorados, pois já haviam sido validados na execução anterior.
  Total de cenários distintos validados nas duas execuções: 35.
- API executada na porta 8080, perfil `postgres`, banco `socialconnect_a1`.
- Testes PostgreSQL usam exclusivamente `socialconnect_a1_test`.
- Flyway aplicou V1 e V2; Hibernate validou as entidades contra o esquema.
- Swagger e listagem de produtos respondem HTTP 200.
- OpenAPI contém as cinco operações, exemplos, paginação e respostas de erro.

As credenciais ficam somente nas variáveis de ambiente do processo.
Repositório público: https://github.com/ThiagoHenriqueLeu/socialconnect
Branch da entrega: `avalicao1`. Publicado após autorização do aluno.
