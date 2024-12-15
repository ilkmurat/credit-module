package com.murat.ing.credit_module.mapper;

import com.murat.ing.credit_module.dto.LoanResponse;
import com.murat.ing.credit_module.dto.LoanInstallmentResponse;
import com.murat.ing.credit_module.entity.Loan;
import com.murat.ing.credit_module.entity.LoanInstallment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LoanMapper {

    public LoanResponse toLoanResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getCustomer().getId(),
                loan.getLoanAmount(),
                loan.getNumberOfInstallment(),
                loan.getCreateDate(),
                loan.getPaid(),
                toLoanInstallmentResponses(loan.getInstallments())
        );
    }

    public List<LoanInstallmentResponse> toLoanInstallmentResponses(List<LoanInstallment> installments) {
        return installments.stream()
                .map(this::toLoanInstallmentResponse)
                .collect(Collectors.toList());
    }

    public LoanInstallmentResponse toLoanInstallmentResponse(LoanInstallment installment) {
        return new LoanInstallmentResponse(
                installment.getId(),
                installment.getAmount(),
                installment.getPaid(),
                installment.getDueDate()
        );
    }
}
