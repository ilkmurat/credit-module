package com.murat.ing.credit_module.dto;

import java.time.LocalDate;
import java.util.List;

public class LoanResponse {
    private Long loanId;
    private Long customerId;
    private Double loanAmount;
    private Integer numberOfInstallments;
    private LocalDate createDate;
    private Boolean isPaid;

    // Constructor
    public LoanResponse(Long loanId, Long customerId, Double loanAmount, Integer numberOfInstallments, LocalDate createDate, Boolean isPaid, List<LoanInstallmentResponse> loanInstallmentResponses) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.loanAmount = loanAmount;
        this.numberOfInstallments = numberOfInstallments;
        this.createDate = createDate;
        this.isPaid = isPaid;
    }

    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }
}
