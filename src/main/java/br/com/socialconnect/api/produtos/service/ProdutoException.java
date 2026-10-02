package br.com.socialconnect.api.produtos.service;

import org.springframework.http.HttpStatus;

public class ProdutoException extends RuntimeException {
    private final HttpStatus status;
    private final String messageKey;

    public ProdutoException(HttpStatus status, String messageKey) {
        super(messageKey);
        this.status = status;
        this.messageKey = messageKey;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessageKey() { return messageKey; }
}
