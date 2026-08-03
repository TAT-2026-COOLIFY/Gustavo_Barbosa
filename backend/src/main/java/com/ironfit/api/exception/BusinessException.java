package com.ironfit.api.exception;

/** Regra de negócio violada (ex.: e-mail já cadastrado, objetivo duplicado ativo). */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
