package com.murat.ing.credit_module.dto;

import java.time.LocalDate;

public class LoanInstallmentResponse {
    private Long installmentId;
    private Double amount;
    private Boolean isPaid;
    private LocalDate dueDate;

    public LoanInstallmentResponse(Long installmentId, Double amount, Boolean isPaid, LocalDate dueDate) {
        this.installmentId = installmentId;
        this.amount = amount;
        this.isPaid = isPaid;
        this.dueDate = dueDate;
    }

    public Long getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Long installmentId) {
        this.installmentId = installmentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
