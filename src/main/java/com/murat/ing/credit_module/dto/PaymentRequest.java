package com.murat.ing.credit_module.dto;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    private Double amount;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
