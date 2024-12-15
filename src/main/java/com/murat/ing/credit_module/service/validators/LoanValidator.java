package com.murat.ing.credit_module.service.validators;

import com.murat.ing.credit_module.entity.Customer;
import com.murat.ing.credit_module.entity.LoanInstallment;
import com.murat.ing.credit_module.enums.Installment;
import com.murat.ing.credit_module.exception.InsufficientCreditException;
import com.murat.ing.credit_module.exception.InvalidLoanRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class LoanValidator {

    @Value("${loan.interest.min}")
    private Double minInterestRate;

    @Value("${loan.interest.max}")
    private Double maxInterestRate;

    @Value("${loan.max.installments.per.payment}")
    private int maxInstallmentsPerPayment;

    public void validateInterestRate(Double interestRate) {
        if (interestRate < minInterestRate || interestRate > maxInterestRate) {
            throw new InvalidLoanRequestException(
                    String.format("Invalid interest rate. Allowed range is %.2f to %.2f.",
                            minInterestRate, maxInterestRate));
        }
    }

    public void validateInstallments(Integer numberOfInstallments) {
        if (!Installment.isValid(numberOfInstallments)) {
            throw new InvalidLoanRequestException("Invalid number of installments. Allowed values are 6, 9, 12, or 24.");
        }
    }

    public void validateCustomerCredit(Customer customer, Double amount) {
        if (customer.getCreditLimit() - customer.getUsedCreditLimit() < amount) {
            throw new InsufficientCreditException("Customer does not have enough credit limit.");
        }
    }

    public double calculatePenaltyOrReward(LoanInstallment installment, LocalDate now) {
        double adjustedAmount = installment.getAmount();
        if (now.isBefore(installment.getDueDate())) {
            long daysBeforeDue = ChronoUnit.DAYS.between(now, installment.getDueDate());
            return adjustedAmount - (adjustedAmount * 0.001 * daysBeforeDue); // Reward hesaplama
        } else if (now.isAfter(installment.getDueDate())) {
            long daysAfterDue = ChronoUnit.DAYS.between(installment.getDueDate(), now);
            return adjustedAmount + (adjustedAmount * 0.001 * daysAfterDue); // Penalty hesaplama
        }
        return adjustedAmount;
    }

    public boolean isInstallmentEligibleForPayment(LoanInstallment installment, LocalDate now) {
        return !installment.getDueDate().isAfter(now.plusMonths(maxInstallmentsPerPayment));
    }


}

