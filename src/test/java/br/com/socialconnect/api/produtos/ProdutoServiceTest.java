package br.com.socialconnect.api.produtos;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.model.*;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import br.com.socialconnect.api.produtos.service.*;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
    @Mock private ProdutoRepository repository;
    private ProdutoService service;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    void preparar() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        service = new ProdutoServiceImpl(repository, validatorFactory.getValidator());
    }

    @AfterEach
    void fechar() { validatorFactory.close(); }

    private ProdutoRequestDTO dados(int estoque) {
        return new ProdutoRequestDTO("Arroz 5kg", CategoriaProduto.ALIMENTO, estoque, 10, "unidade");
    }

    @Test
    void deveCriarProdutoQuandoDadosValidos() {
        // Arrange
        when(repository.saveAndFlush(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            produto.setIdProduto(1L);
            return produto;
        });
        // Act
        var resposta = service.criar(dados(3));
        // Assert
        assertThat(resposta.idProduto()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Arroz 5kg");
        assertThat(resposta.dataCadastro()).isEqualTo(LocalDate.now());
        assertThat(resposta.estoqueBaixo()).isTrue();
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // Arrange
        var dto = dados(-1);
        // Act
        var erro = catchThrowableOfType(ProdutoException.class, () -> service.criar(dto));
        // Assert
        assertThat(erro.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        verifyNoInteractions(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // Arrange
        when(repository.existsByNome("Arroz 5kg")).thenReturn(true);
        // Act
        var erro = catchThrowableOfType(ProdutoException.class, () -> service.criar(dados(3)));
        // Assert
        assertThat(erro.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void devePreservarDataCadastroEPermitirMesmoNomeNaAtualizacao() {
        // Arrange
        Produto produto = new Produto();
        produto.setIdProduto(1L);
        produto.setNome("Arroz 5kg");
        produto.setDataCadastro(LocalDate.of(2026, 1, 1));
        when(repository.findById(1L)).thenReturn(Optional.of(produto));
        when(repository.saveAndFlush(produto)).thenReturn(produto);
        // Act
        var resposta = service.atualizar(1L, dados(10));
        // Assert
        assertThat(resposta.dataCadastro()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(resposta.estoqueBaixo()).isFalse();
    }

    @Test
    void deveRejeitarEstoqueNegativoNaAtualizacaoSemSalvar() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(new Produto()));
        // Act
        var erro = catchThrowableOfType(ProdutoException.class, () -> service.atualizar(1L, dados(-1)));
        // Assert
        assertThat(erro.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void deveRetornar404AoExcluirProdutoInexistente() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());
        // Act
        var erro = catchThrowableOfType(ProdutoException.class, () -> service.deletar(99L));
        // Assert
        assertThat(erro.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(repository, never()).delete(any(Produto.class));
    }
}
