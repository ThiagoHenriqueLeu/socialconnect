# Registro de Uso de IA Generativa

> **Política da disciplina:** O uso de IA generativa é permitido como
> assistente. O discente é **integralmente responsável** por testar, auditar e
> defender todo o código entregue, independentemente de como foi gerado.

## Instruções

Para cada aula ou entrega, registre abaixo:
- **Data**
- **Ferramenta** (ChatGPT, Copilot, Claude, etc.)
- **Prompt(s) utilizado(s)** (resumo ou cópia)
- **O que foi feito com a saída** (copiado integralmente, adaptado, usado como referência, descartado)

---

## Registro

Os prompts abaixo resumem os pedidos feitos na conversa, considerando o contexto de cada mensagem. Não são transcrições literais.

| Data | Aula | Ferramenta | Prompt (resumo) | Uso da saída |
|------|------|------------|-----------------|--------------|
| 04/09/2026 | Preparação do projeto | Codex (OpenAI) | Rode o projeto socialconnect-api-main que está na Área de Trabalho. | A IA verificou o projeto e iniciou a API com uma opção temporária de criação das tabelas H2 após identificar uma falha de inicialização. |
| 02/10/2026 | A1 | Codex (OpenAI) | Rode novamente o projeto SocialConnect. | A IA iniciou a API e verificou as respostas HTTP do Swagger e da listagem de beneficiários. |
| 02/10/2026 | A1 | Codex (OpenAI) | Leia as instruções do arquivo avaliacao1.html e explique o que entendeu da atividade. | Usei a explicação como referência para identificar os requisitos do módulo de Produtos, os testes de bônus e os passos de entrega. |
| 02/10/2026 | A1 | Codex (OpenAI) | Pode implementar os requisitos apresentados. Vamos fazer o projeto primeiro e deixar o GitHub para depois. | A IA implementou diretamente os arquivos de entidade, DTOs, repository, service, controller, validação customizada, Problem Details, mensagens traduzidas e documentação Swagger. Também corrigiu a configuração e as migrations do Flyway e criou testes automatizados. |
| 02/10/2026 | A1 | Codex (OpenAI) | O Docker não abriu. Para o banco de dados, use o PostgreSQL da máquina, que acesso pelo pgAdmin. | A IA configurou o perfil PostgreSQL, criou bancos separados para aplicação e testes e executou testes unitários, de contexto e de integração HTTP em H2 e PostgreSQL local. As credenciais não foram incluídas no código. |
| 02/10/2026 | A1 | Codex (OpenAI) | Fiz o Docker funcionar; pode continuar com essa parte. | A IA executou o perfil Testcontainers com PostgreSQL 17, verificou os nove testes de integração e atualizou o registro dos resultados. |
| 02/10/2026 | A1 | Codex (OpenAI) | Confira o que falta da tarefa e prepare o restante da entrega, incluindo o registro de prompts no formato do AI_USAGE.md. | A IA organizou este registro no formato original, criou o repositório público socialconnect e publicou a branch avalicao1 com commits separados para implementação, testes e documentação. O envio no Teams é uma etapa separada. |

---

## Verificação e revisão

- Foram validados **35 testes distintos** em duas execuções: 6 unitários com Mockito, 9 cenários HTTP com H2, 9 com PostgreSQL local, 9 com PostgreSQL 17 via Testcontainers e 2 testes de contexto.
- O código foi implementado com assistência direta de IA, e não somente consultado como exemplo.
- Os resultados e comandos estão em `docs/VALIDACAO_A1.md`.
- O roteiro para estudar e demonstrar o projeto está em `docs/GUIA_A1.md`.
- **A revisão e a compreensão pessoal do aluno ainda devem ser confirmadas pelo próprio aluno antes da entrega.** O uso da IA e a aprovação dos testes não substituem essa etapa.
