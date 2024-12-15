package com.murat.ing.credit_module.repository;

import com.murat.ing.credit_module.entity.LoanInstallment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    // Pagination support for loan installments by loan ID
    Page<LoanInstallment> findAllByLoan_Id(Long loanId, Pageable pageable);

    // Default sorting by due date for loan installments
    //List<LoanInstallment> findAllByLoan_IdOrderByDueDateAsc(Long loanId);

    //Only unpaid installments
    List<LoanInstallment> findAllByLoan_IdAndIsPaidFalseOrderByDueDateAsc(Long loanId);

    Page<LoanInstallment> findAllByLoan_IdAndIsPaid(Long loanId, Boolean isPaid, Pageable pageable);

}
