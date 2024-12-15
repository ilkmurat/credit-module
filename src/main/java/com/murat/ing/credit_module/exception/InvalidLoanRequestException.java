package com.murat.ing.credit_module.exception;

public class InvalidLoanRequestException extends RuntimeException {
    public InvalidLoanRequestException(String message) {
        super(message);
    }
}
