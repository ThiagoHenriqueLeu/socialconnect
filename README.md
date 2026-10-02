# SocialConnect API — Avaliação A1

API de beneficiários e estoque de doações. Java 21, Spring Boot 4.1.1,
Spring Data JPA, Bean Validation, Flyway e OpenAPI.

## Executar

Pré-requisito: JDK 21. O Maven Wrapper está incluído.

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

No Linux/macOS, use `./mvnw`. O padrão é H2 em memória: dados são perdidos ao
encerrar. Flyway cria as tabelas e Hibernate valida o esquema.
Não é necessário passar `ddl-auto=update`.

- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs
- H2 Console: http://localhost:8080/h2-console
- Conexão H2: `jdbc:h2:mem:socialconnectdb`, usuário `sa`, senha vazia.

### PostgreSQL local (sem Docker)

O pgAdmin administra o servidor; a API conecta diretamente ao PostgreSQL.
Crie o banco `socialconnect_a1` no pgAdmin e configure a sessão PowerShell:

```powershell
$env:DB_USERNAME = 'postgres' # ou seu usuário do servidor
$senha = Read-Host 'Senha do PostgreSQL' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $senha).Password
$env:DB_URL = 'jdbc:postgresql://localhost:5432/socialconnect_a1'
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=postgres'
```

Após encerrar, `Remove-Item Env:DB_PASSWORD` remove a senha da sessão.
Credenciais não são gravadas no projeto. Nesse modo os dados persistem.

## Produtos

| Método | Caminho | Sucesso | Erros principais |
|---|---|---|---|
| GET | `/api/v1/produtos` | 200, página | 400 |
| GET | `/api/v1/produtos/{id_produto}` | 200 | 400, 404 |
| POST | `/api/v1/produtos` | 201 + Location | 400, 409, 422 |
| PUT | `/api/v1/produtos/{id_produto}` | 200 | 400, 404, 409, 422 |
| DELETE | `/api/v1/produtos/{id_produto}` | 204, sem corpo | 400, 404 |

Corpo de POST e PUT:

```json
{
  "nome": "Arroz 5kg",
  "categoria": "ALIMENTO",
  "estoqueAtual": 3,
  "estoqueMinimo": 10,
  "unidadeMedida": "unidade"
}
```

Todos os cinco campos são obrigatórios. Nome tem até 150 caracteres; unidade,
até 20. Categorias: `ALIMENTO`, `ROUPA`, `HIGIENE`, `OUTROS`.
ID e data são gerados pelo servidor. PUT exige corpo completo e preserva ID e
data. A resposta acrescenta `idProduto`, `dataCadastro` e `estoqueBaixo`.
O alerta é verdadeiro somente se estoqueAtual < estoqueMinimo.

- Estoque atual negativo: **422**, inclusive no PUT.
- Estoque mínimo negativo ou outros campos inválidos: **400**.
- Nome repetido: **409**; manter o próprio nome no PUT é permitido.
- Unicidade considera o nome exato após remover espaços externos,
  diferenciando maiúsculas de minúsculas.
- Validações na entrada, no service e nas constraints do banco.
- Erros usam `application/problem+json`: `type`, `title`, `status`, `detail`,
  `instance`; validação acrescenta `errors` com campo e mensagem.
- `Accept-Language: en` seleciona mensagens de validação/negócio em inglês.
  O padrão é português. Títulos HTTP e mensagens técnicas podem ser em inglês.

Listagem com filtros combinados:

```text
/api/v1/produtos?nome=arroz&categoria=ALIMENTO&page=0&size=10&sort=nome,asc
```

Nome é busca parcial sem distinção de maiúsculas. Filtros são opcionais.
Página começa em zero; tamanho padrão 10, máximo 100. Ordenação aceita os
campos persistidos, como `nome`, `idProduto`, `estoqueAtual` e `dataCadastro`.
Resposta contém `content` e `page` (`size`, `number`, `totalElements`, `totalPages`).
Beneficiários permanecem em `/api/v1/beneficiarios`.

## Testes

```powershell
.\mvnw.cmd test
```

Executa testes unitários com Mockito, testes de contexto e nove cenários HTTP
com H2: CRUD, unicidade, estoque negativo, alerta, filtros, paginação,
ordenação, validação, tradução e Flyway.

### PostgreSQL local

Crie um banco **exclusivo de testes** `socialconnect_a1_test` no pgAdmin.
Não use o banco da aplicação: os testes removem produtos a cada cenário.
Com `DB_USERNAME` e `DB_PASSWORD` configurados:

```powershell
$env:RUN_LOCAL_POSTGRES_TESTS = 'true'
$env:TEST_DB_URL = 'jdbc:postgresql://localhost:5432/socialconnect_a1_test'
.\mvnw.cmd test '-Dtest=ProdutoControllerPostgresLocalTest'
Remove-Item Env:RUN_LOCAL_POSTGRES_TESTS
```

### Bônus: Testcontainers com PostgreSQL 17

`ProdutoControllerIntegrationTest.java` usa `@SpringBootTest` com porta
aleatória, `@Testcontainers` e PostgreSQL 17. Requer Docker disponível:

```powershell
.\mvnw.cmd -Ptestcontainers test
```

O perfil é opcional e não roda no comando padrão. Testes PostgreSQL local
ficam ignorados sem a variável acima. Testes compilados/ignorados não são
evidência de execução bem-sucedida.

## Organização e migrations

`produtos/{controller,service,repository,dto,model,validation}` contém o módulo.
`config` contém OpenAPI, idioma e serialização de páginas.
Injeção por construtores e campos finais; DTOs são records.

Migrations em `src/main/resources/db/migration`:

- `V1__create_beneficiarios_table.sql`: tabela original alinhada com a entidade.
- `V2__create_produtos_table.sql`: próximo número disponível, PK `id_produto`,
  nome único e CHECKs de estoque e categoria.

A antiga pasta `db.migration` e o nome com só um sublinhado impediam o
reconhecimento da migration original. Foram adicionados o starter Flyway e
seu módulo PostgreSQL. A correção foi feita para bancos novos; não altere
migrations já aplicadas em outros ambientes existentes.

## Entrega

Repositório público: https://github.com/ThiagoHenriqueLeu/socialconnect

Branch da atividade: [avalicao1](https://github.com/ThiagoHenriqueLeu/socialconnect/tree/avalicao1)
(grafia indicada no enunciado). Implementação, testes e documentação foram
registrados em commits Conventional Commits. Envie o link da branch no Teams.
Consulte `AI_USAGE.md` e `docs/GUIA_A1.md` antes da apresentação.
