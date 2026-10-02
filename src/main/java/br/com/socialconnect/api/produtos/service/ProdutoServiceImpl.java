package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.model.*;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import jakarta.validation.Validator;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {
    private static final Set<String> CAMPOS_ORDENACAO = Set.of("idProduto", "nome", "categoria",
        "estoqueAtual", "estoqueMinimo", "unidadeMedida", "dataCadastro");
    private final ProdutoRepository repository;
    private final Validator validator;

    public ProdutoServiceImpl(ProdutoRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @Override
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        if (pageable.getSort().stream().anyMatch(order -> !CAMPOS_ORDENACAO.contains(order.getProperty()))) {
            throw new ProdutoException(HttpStatus.BAD_REQUEST, "produto.ordenacao.invalida");
        }
        Specification<Produto> filtro = (root, query, cb) -> cb.conjunction();
        if (nome != null && !nome.isBlank()) {
            String termo = nome.strip().toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            filtro = filtro.and((root, query, cb) -> cb.like(cb.lower(root.get("nome")), "%" + termo + "%", '\\'));
        }
        if (categoria != null) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("categoria"), categoria));
        }
        return repository.findAll(filtro, pageable).map(this::toResponse);
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) { return toResponse(encontrar(id)); }

    @Override
    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validar(dto);
        if (repository.existsByNome(dto.nome().strip())) {
            throw new ProdutoException(HttpStatus.CONFLICT, "produto.nome.duplicado");
        }
        Produto produto = new Produto();
        preencher(produto, dto);
        produto.setDataCadastro(LocalDate.now());
        return toResponse(repository.saveAndFlush(produto));
    }

    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = encontrar(id);
        validar(dto);
        if (repository.existsByNomeAndIdProdutoNot(dto.nome().strip(), id)) {
            throw new ProdutoException(HttpStatus.CONFLICT, "produto.nome.duplicado");
        }
        preencher(produto, dto);
        return toResponse(repository.saveAndFlush(produto));
    }

    @Override
    @Transactional
    public void deletar(Long id) { repository.delete(encontrar(id)); }

    private Produto encontrar(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ProdutoException(HttpStatus.NOT_FOUND, "produto.naoEncontrado"));
    }

    private void validar(ProdutoRequestDTO dto) {
        // Também protege chamadas ao service que não passam pelo controller.
        if (dto != null && dto.estoqueAtual() != null && dto.estoqueAtual() < 0) {
            throw new ProdutoException(HttpStatus.UNPROCESSABLE_ENTITY, "produto.estoque.negativo");
        }
        if (dto == null || !validator.validate(dto).isEmpty()) {
            throw new ProdutoException(HttpStatus.BAD_REQUEST, "produto.dados.invalidos");
        }
    }

    private void preencher(Produto produto, ProdutoRequestDTO dto) {
        produto.setNome(dto.nome().strip());
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(dto.unidadeMedida().strip());
    }

    private ProdutoResponseDTO toResponse(Produto produto) {
        return new ProdutoResponseDTO(produto.getIdProduto(), produto.getNome(), produto.getCategoria(),
            produto.getEstoqueAtual(), produto.getEstoqueMinimo(), produto.getUnidadeMedida(),
            produto.getDataCadastro(), produto.getEstoqueAtual() < produto.getEstoqueMinimo());
    }
}
