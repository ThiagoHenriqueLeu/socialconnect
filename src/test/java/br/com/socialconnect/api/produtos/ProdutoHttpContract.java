package br.com.socialconnect.api.produtos;

import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.net.URI;
import java.net.http.*;
import static org.assertj.core.api.Assertions.*;

/** Executa o mesmo contrato HTTP sobre H2 e PostgreSQL real. */
abstract class ProdutoHttpContract {
    @LocalServerPort private int port;
    @Autowired private ProdutoRepository repository;
    @Autowired private JdbcTemplate jdbc;
    private final HttpClient client = HttpClient.newHttpClient();
    private final JsonMapper json = new JsonMapper();

    @BeforeEach
    void limparProdutos() { repository.deleteAll(); }

    private String dados(String nome, String categoria, int estoque, int minimo) {
        return """
            {"nome":"%s","categoria":"%s","estoqueAtual":%d,"estoqueMinimo":%d,"unidadeMedida":"unidade"}
            """.formatted(nome, categoria, estoque, minimo);
    }

    private HttpResponse<String> request(String metodo, String caminho, String body, String idioma) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + caminho))
            .header("Content-Type", "application/json").header("Accept-Language", idioma)
            .method(metodo, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> request(String metodo, String caminho, String body) throws Exception {
        return request(metodo, caminho, body, "pt-BR");
    }

    private JsonNode body(HttpResponse<String> response) { return json.readTree(response.body()); }

    private void problem(HttpResponse<String> response, int status) {
        assertThat(response.statusCode()).isEqualTo(status);
        assertThat(response.headers().firstValue("content-type").orElse("")).contains("application/problem+json");
        assertThat(body(response).path("status").asInt()).isEqualTo(status);
        assertThat(body(response).path("title").asString()).isNotBlank();
        assertThat(body(response).path("detail").asString()).isNotBlank();
        assertThat(body(response).path("instance").asString()).startsWith("/api/v1/produtos");
    }

    @Test
    void deveCriarProdutoQuandoDadosValidos() throws Exception {
        // Arrange
        String dto = dados("Arroz 5kg", "ALIMENTO", 3, 10);
        // Act
        var resposta = request("POST", "/api/v1/produtos", dto);
        // Assert
        assertThat(resposta.statusCode()).isEqualTo(201);
        long id = body(resposta).path("idProduto").asLong();
        assertThat(id).isPositive();
        assertThat(resposta.headers().firstValue("Location").orElseThrow()).endsWith("/api/v1/produtos/" + id);
        assertThat(body(resposta).path("estoqueBaixo").asBoolean()).isTrue();
        assertThat(body(resposta).path("dataCadastro").asString()).matches("\\d{4}-\\d{2}-\\d{2}");
        assertThat(request("GET", "/api/v1/produtos/" + id, null).statusCode()).isEqualTo(200);
    }

    @Test
    void deveRetornar409QuandoNomeDuplicado() throws Exception {
        // Arrange
        String dto = dados("Arroz", "ALIMENTO", 3, 10);
        request("POST", "/api/v1/produtos", dto);
        // Act
        var resposta = request("POST", "/api/v1/produtos", dto);
        // Assert
        problem(resposta, 409);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void deveRetornar422QuandoEstoqueNegativo() throws Exception {
        // Arrange
        String dto = dados("Arroz", "ALIMENTO", -1, 10);
        // Act
        var resposta = request("POST", "/api/v1/produtos", dto);
        // Assert
        problem(resposta, 422);
        assertThat(body(resposta).path("errors").get(0).path("field").asString()).isEqualTo("estoqueAtual");
        assertThat(repository.count()).isZero();
    }

    @Test
    void deveAtualizarPreservandoDataEExcluirProduto() throws Exception {
        var criado = request("POST", "/api/v1/produtos", dados("Arroz", "ALIMENTO", 3, 10));
        String path = "/api/v1/produtos/" + body(criado).path("idProduto").asLong();
        var atualizado = request("PUT", path, dados("Arroz", "ALIMENTO", 10, 10));
        assertThat(atualizado.statusCode()).isEqualTo(200);
        assertThat(body(atualizado).path("estoqueBaixo").asBoolean()).isFalse();
        assertThat(body(atualizado).path("dataCadastro")).isEqualTo(body(criado).path("dataCadastro"));
        var removido = request("DELETE", path, null);
        assertThat(removido.statusCode()).isEqualTo(204);
        assertThat(removido.body()).isEmpty();
        problem(request("GET", path, null), 404);
        problem(request("DELETE", path, null), 404);
        problem(request("PUT", path, dados("Arroz", "ALIMENTO", 0, 0)), 404);
    }

    @Test
    void deveRejeitarAtualizacaoDuplicadaOuNegativaSemAlterarProduto() throws Exception {
        request("POST", "/api/v1/produtos", dados("Arroz", "ALIMENTO", 3, 10));
        var criado = request("POST", "/api/v1/produtos", dados("Feijao", "ALIMENTO", 5, 10));
        String path = "/api/v1/produtos/" + body(criado).path("idProduto").asLong();
        problem(request("PUT", path, dados("Arroz", "ALIMENTO", 5, 10)), 409);
        problem(request("PUT", path, dados("Feijao", "ALIMENTO", -1, 10)), 422);
        var preservado = body(request("GET", path, null));
        assertThat(preservado.path("nome").asString()).isEqualTo("Feijao");
        assertThat(preservado.path("estoqueAtual").asInt()).isEqualTo(5);
    }

    @Test
    void deveCombinarFiltrosPaginacaoEOrdenacao() throws Exception {
        request("POST", "/api/v1/produtos", dados("Arroz branco", "ALIMENTO", 0, 0));
        request("POST", "/api/v1/produtos", dados("Arroz integral", "ALIMENTO", 1, 0));
        request("POST", "/api/v1/produtos", dados("Arroz decorativo", "OUTROS", 1, 0));
        var resposta = request("GET", "/api/v1/produtos?nome=ARROZ&categoria=ALIMENTO&page=1&size=1&sort=nome,asc", null);
        assertThat(resposta.statusCode()).isEqualTo(200);
        var pagina = body(resposta);
        assertThat(pagina.path("content").size()).isEqualTo(1);
        assertThat(pagina.path("content").get(0).path("nome").asString()).isEqualTo("Arroz integral");
        assertThat(pagina.path("page").path("totalElements").asInt()).isEqualTo(2);
        assertThat(pagina.path("page").path("number").asInt()).isEqualTo(1);
    }

    @Test
    void deveRetornar400ParaCamposInvalidosEJsonMalformado() throws Exception {
        problem(request("POST", "/api/v1/produtos", "{}"), 400);
        problem(request("POST", "/api/v1/produtos", "{"), 400);
        problem(request("POST", "/api/v1/produtos", dados(" ", "ALIMENTO", 1, 1)), 400);
        problem(request("POST", "/api/v1/produtos", dados("Arroz", "INVALIDA", 1, 1)), 400);
        problem(request("POST", "/api/v1/produtos", dados("Arroz", "ALIMENTO", 1, -1)), 400);
        problem(request("POST", "/api/v1/produtos", dados("A".repeat(151), "ALIMENTO", 1, 0)), 400);
        problem(request("GET", "/api/v1/produtos?categoria=INVALIDA", null), 400);
        problem(request("GET", "/api/v1/produtos?sort=inexistente,asc", null), 400);
        problem(request("GET", "/api/v1/produtos/abc", null), 400);
    }

    @Test
    void deveTraduzirMensagensPeloAcceptLanguage() throws Exception {
        var resposta = request("POST", "/api/v1/produtos", dados("Arroz", "ALIMENTO", -1, 0), "en");
        problem(resposta, 422);
        assertThat(body(resposta).path("detail").asString()).isEqualTo("Current stock must not be negative.");
        assertThat(body(resposta).path("errors").get(0).path("message").asString()).isEqualTo("Current stock must not be negative.");
        var portugues = request("POST", "/api/v1/produtos", dados("Arroz", "ALIMENTO", -1, 0));
        assertThat(body(portugues).path("detail").asString()).isEqualTo("O estoque atual não pode ser negativo.");
    }

    @Test
    void deveExecutarMigrationsEProtegerBancoContraEstoqueNegativo() {
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"success\" = true AND \"type\" = 'SQL'", Integer.class)).isEqualTo(2);
        assertThatThrownBy(() -> jdbc.update("""
            INSERT INTO produtos (nome, categoria, estoque_atual, estoque_minimo, unidade_medida, data_cadastro)
            VALUES ('Invalido', 'ALIMENTO', -1, 0, 'kg', CURRENT_DATE)
            """)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
