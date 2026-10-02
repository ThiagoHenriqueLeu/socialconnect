package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "Cadastro e controle do estoque de doações")
public class ProdutoController {
    private final ProdutoService service;

    public ProdutoController(ProdutoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Lista paginada com filtros opcionais combináveis. Nome parcial ignora maiúsculas. Exemplo: ?page=0&size=10&sort=nome,asc&categoria=ALIMENTO")
    @ApiResponse(responseCode = "200", description = "Página de produtos")
    @ApiResponse(responseCode = "400", description = "Filtro ou ordenação inválida", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public Page<ProdutoResponseDTO> listar(
        @Parameter(description = "Parte do nome", example = "Arroz") @RequestParam(required = false) String nome,
        @RequestParam(required = false) CategoriaProduto categoria,
        @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return service.listar(nome, categoria, pageable);
    }

    @GetMapping("/{id_produto}")
    @Operation(summary = "Buscar produto por ID")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ProdutoResponseDTO buscarPorId(@PathVariable("id_produto") Long idProduto) {
        return service.buscarPorId(idProduto);
    }

    @PostMapping
    @Operation(summary = "Cadastrar produto", description = "Gera ID e data de cadastro automaticamente. Nome é único e tem espaços externos removidos.")
    @ApiResponse(responseCode = "201", description = "Produto criado; Location contém a URL do recurso", headers = @io.swagger.v3.oas.annotations.headers.Header(name = "Location", schema = @Schema(type = "string", example = "http://localhost:8080/api/v1/produtos/1")))
    @ApiResponse(responseCode = "400", description = "Campos ausentes ou inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome já cadastrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque atual negativo", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO criado = service.criar(dto);
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(criado.idProduto()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id_produto}")
    @Operation(summary = "Atualizar produto", description = "Substitui todos os campos editáveis. Preserva ID e data de cadastro.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado")
    @ApiResponse(responseCode = "400", description = "Campos ausentes ou inválidos", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome já cadastrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque atual negativo", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ProdutoResponseDTO atualizar(@PathVariable("id_produto") Long idProduto,
                                        @Valid @RequestBody ProdutoRequestDTO dto) {
        return service.atualizar(idProduto, dto);
    }

    @DeleteMapping("/{id_produto}")
    @Operation(summary = "Excluir produto")
    @ApiResponse(responseCode = "204", description = "Produto excluído", content = @Content)
    @ApiResponse(responseCode = "400", description = "ID inválido", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(@PathVariable("id_produto") Long idProduto) {
        service.deletar(idProduto);
        return ResponseEntity.noContent().build();
    }
}
