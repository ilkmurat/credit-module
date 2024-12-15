package com.murat.ing.credit_module.dto;

public class PaymentResponse {
    private final int installmentsPaid;
    private final double totalPaid;
    private final double totalPenaltyOrReward;
    private final boolean loanFullyPaid;


    public PaymentResponse(int installmentsPaid, double totalPaid, double totalPenaltyOrReward, boolean loanFullyPaid) {
        this.installmentsPaid = installmentsPaid;
        this.totalPaid = totalPaid;
        this.totalPenaltyOrReward = totalPenaltyOrReward;
        this.loanFullyPaid = loanFullyPaid;

    }

    // Getters
    public int getInstallmentsPaid() {
        return installmentsPaid;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public double getTotalPenaltyOrReward() {
        return totalPenaltyOrReward;
    }

    public boolean isLoanFullyPaid() {
        return loanFullyPaid;
    }
}
