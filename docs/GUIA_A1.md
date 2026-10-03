
## Caminho de uma requisição

1. `ProdutoController` recebe JSON e aplica `@Valid` no DTO.
2. Bean Validation verifica os campos. `@EstoqueNaoNegativo` é a validação customizada.
3. `ProdutoServiceImpl` verifica regras, consulta o repositório e converte entidades em DTOs.
4. `ProdutoRepository` executa consultas JPA. `Specification` combina os filtros opcionais.
5. O banco aplica constraints de unicidade, obrigatoriedade e estoques não negativos.
6. O controller responde com o status HTTP apropriado. O advice transforma erros em Problem Details.

## Por que separar os arquivos?

- Entity representa a tabela. DTO representa o contrato público da API.
- O request não permite escolher o ID nem sobrescrever a data de cadastro.
- O service centraliza regras e transações; o controller trata HTTP.
- O repository cuida da persistência sem misturar HTTP ou apresentação.
- `estoqueBaixo` é calculado ao montar a resposta, evitando um booleano desatualizado no banco.

## Pontos para explicar

- `@NotNull` rejeita ausência, e a validação customizada rejeita valor negativo.
- O advice reconhece `EstoqueNaoNegativo` e retorna 422; as demais violações retornam 400.
- O service repete a proteção de estoque para chamadas que não passam pelo controller.
- A consulta prévia evita duplicidade com mensagem clara; UNIQUE no banco protege concorrência.
- Na atualização, a consulta exclui o próprio ID para permitir manter o nome.
- `saveAndFlush` envia a gravação ao banco ainda dentro da operação.
- `@Transactional` permite desfazer operações que falham e agrupa a persistência.
- Flyway executa as migrations versionadas antes da validação do Hibernate.
- H2 é temporário; PostgreSQL mantém dados em disco.
- `Accept-Language` seleciona mensagens em `messages.properties` ou `messages_en.properties`.

## Roteiro no Swagger

1. Abra `/swagger-ui.html` e expanda **Produtos**.
2. Faça POST com o exemplo de arroz: espere 201, Location e `estoqueBaixo: true`.
3. Repita o POST: espere 409.
4. Troque estoqueAtual por -1: espere 422 com `application/problem+json`.
5. Envie nome vazio ou omita categoria: espere 400 e os campos inválidos.
6. Liste com nome parcial e categoria; experimente paginação e ordenação.
7. Consulte o ID criado e atualize com estoqueAtual = estoqueMinimo: o alerta deve ser falso.
8. Exclua esse ID: espere 204 sem corpo. Consulte novamente: espere 404.

## Antes da entrega

Execute os testes, revise cada classe e escreva com suas palavras o que compreendeu.
O registro de IA descreve assistência recebida; não comprova compreensão do aluno.
Os testes Testcontainers exigidos para o bônus só ficam validados quando executados
com Docker e PostgreSQL 17. Testes locais com PostgreSQL são complementares.
