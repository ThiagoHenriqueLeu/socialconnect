package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.service.ProdutoException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.List;

@RestControllerAdvice(assignableTypes = ProdutoController.class)
public class ProdutoExceptionHandler extends ResponseEntityExceptionHandler {
    private final MessageSource messages;

    public ProdutoExceptionHandler(MessageSource messages) { this.messages = messages; }

    @ExceptionHandler(ProdutoException.class)
    public ResponseEntity<Object> handleProduto(ProdutoException exception, WebRequest request) {
        return responder(exception, exception.getStatus(), exception.getMessageKey(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleIntegrity(DataIntegrityViolationException exception, WebRequest request) {
        // A constraint UNIQUE protege também duas gravações concorrentes com o mesmo nome.
        return responder(exception, HttpStatus.CONFLICT, "produto.conflito", request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        boolean estoqueNegativo = exception.getBindingResult().getFieldErrors().stream()
            .anyMatch(error -> "EstoqueNaoNegativo".equals(error.getCode()));
        HttpStatus resposta = estoqueNegativo ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.BAD_REQUEST;
        ProblemDetail problem = problem(resposta, estoqueNegativo ? "produto.estoque.negativo" : "produto.dados.invalidos");
        List<ErroCampo> erros = exception.getBindingResult().getFieldErrors().stream()
            .map(error -> new ErroCampo(error.getField(), error.getDefaultMessage())).toList();
        problem.setProperty("errors", erros);
        return handleExceptionInternal(exception, problem, headers, resposta, request);
    }

    private ResponseEntity<Object> responder(Exception exception, HttpStatus status, String key, WebRequest request) {
        return handleExceptionInternal(exception, problem(status, key), new HttpHeaders(), status, request);
    }

    private ProblemDetail problem(HttpStatus status, String key) {
        return ProblemDetail.forStatusAndDetail(status, messages.getMessage(key, null, LocaleContextHolder.getLocale()));
    }

    public record ErroCampo(String field, String message) {}
}
