package com.murat.ing.credit_module.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return new ErrorResponse("CUSTOMER_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InsufficientCreditException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientCreditException(InsufficientCreditException ex) {
        return new ErrorResponse("INSUFFICIENT_CREDIT", ex.getMessage());
    }

    @ExceptionHandler(InvalidLoanRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidLoanRequestException(InvalidLoanRequestException ex) {
        return new ErrorResponse("INVALID_LOAN_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(Exception ex) {
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again later.");
    }

    public static class ErrorResponse {
        private String errorCode;
        private String errorMessage;

        public ErrorResponse(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
