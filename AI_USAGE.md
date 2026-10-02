# Registro de Uso de IA Generativa

> **Política da disciplina:** O uso de IA generativa é permitido como
> assistente. O discente é **integralmente responsável** por testar, auditar e
> defender todo o código entregue, independentemente de como foi gerado.

## Registro

Os prompts abaixo são resumos técnicos da solicitação de implementação da A1,
organizados por assunto. Não são transcrições de mensagens individuais.
A assistência da IA abrangeu a implementação do módulo de Produtos, seus testes
e sua documentação.

| Data | Aula | Ferramenta | Prompt (resumo) | Uso da saída |
|------|------|------------|-----------------|--------------|
| 02/10/2026 | A1 | Codex (OpenAI) | Implemente o CRUD de Produtos com entidade, DTOs em records, repository, service e controller. Inclua paginação e filtros por nome e categoria. | Implementação direta do módulo e dos endpoints REST. |
| 02/10/2026 | A1 | Codex (OpenAI) | Valide os campos obrigatórios, impeça estoque negativo e nomes duplicados e calcule o alerta de estoque baixo. Retorne erros no formato Problem Details. | Implementação das validações, regras de negócio e respostas HTTP 400, 404, 409 e 422. |
| 02/10/2026 | A1 | Codex (OpenAI) | Corrija as migrations do Flyway e configure a aplicação para H2 e PostgreSQL local, mantendo as credenciais fora do código. | Correção das migrations e configuração dos perfis de banco de dados. |
| 02/10/2026 | A1 | Codex (OpenAI) | Documente os endpoints no Swagger com exemplos de entrada, respostas e códigos HTTP. Atualize o README com instruções de execução. | Implementação das anotações OpenAPI e atualização da documentação. |
| 02/10/2026 | A1 | Codex (OpenAI) | Crie testes unitários com Mockito e testes de integração com PostgreSQL 17 via Testcontainers. Valide o CRUD, nomes duplicados e estoque negativo. | Criação e execução dos testes automatizados, incluindo os cenários de sucesso e erro. |

## Verificação e revisão

## Exemplos de prompts para o simulado

O projeto é utilizado como simulado, conforme informado pelo aluno. Os prompts
abaixo são exemplos fictícios para praticar diagnóstico e revisão de código;
não substituem o registro de assistência acima nem descrevem erros necessariamente
presentes na versão atual. A coluna final indica o objetivo esperado do exercício.

| Data | Aula | Ferramenta | Prompt (exemplo fictício) | Uso esperado da saída |
|------|------|------------|--------------------------|-----------------------|
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | O Flyway não encontra minhas migrations. Revise o caminho e os nomes dos arquivos SQL. | Identificar problemas na localização e na nomenclatura das migrations. |
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | A validação de estoque negativo retorna 400. Como ajustar para retornar 422 com Problem Details? | Conferir o tratamento da validação customizada no handler. |
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | Ao atualizar um produto mantendo o nome, recebo conflito. O que devo corrigir na consulta de duplicidade? | Verificar se a consulta desconsidera o ID do próprio produto. |
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | Revise o método atualizar de ProdutoServiceImpl. Confira a validação de estoque e a preservação da data de cadastro. | Analisar as regras do método e possíveis falhas na atualização. |
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | Revise o cálculo de estoqueBaixo. Quando o estoque atual é igual ao mínimo, o alerta deve ser falso? | Conferir o uso da comparação estrita entre os estoques. |
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | Confira se os filtros por nome e categoria funcionam juntos na listagem paginada. | Revisar a combinação dos filtros e a aplicação da paginação. |
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | Meus DTOs aparecem sem exemplos no Swagger. Quais anotações preciso revisar? | Identificar ajustes nas anotações de documentação dos campos. |
| 02/10/2026 | Simulado A1 | Codex (OpenAI) | Revise ProdutoControllerIntegrationTest e confira a configuração do PostgreSQL 17 no Testcontainers. | Verificar o container, a conexão e a execução dos testes de integração. |

## Resultados da implementação

- **35 testes distintos validados**: 6 unitários, 9 cenários HTTP em H2, 9 em PostgreSQL local, 9 via Testcontainers e 2 testes de contexto.
- Resultados e comandos: `docs/VALIDACAO_A1.md`.
- Guia de estudo e demonstração: `docs/GUIA_A1.md`.
- A revisão e a compreensão pessoal do código pelo aluno ainda devem ser confirmadas pelo próprio aluno antes da entrega.
