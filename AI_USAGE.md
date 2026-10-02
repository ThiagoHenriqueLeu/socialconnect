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

- **35 testes distintos validados**: 6 unitários, 9 cenários HTTP em H2, 9 em PostgreSQL local, 9 via Testcontainers e 2 testes de contexto.
- Resultados e comandos: `docs/VALIDACAO_A1.md`.
- Guia de estudo e demonstração: `docs/GUIA_A1.md`.
- A revisão e a compreensão pessoal do código pelo aluno ainda devem ser confirmadas pelo próprio aluno antes da entrega.
