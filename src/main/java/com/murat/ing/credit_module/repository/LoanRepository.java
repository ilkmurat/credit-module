package com.murat.ing.credit_module.repository;

import com.murat.ing.credit_module.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    @Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId AND " +
            "(CAST(:authUserId AS string) = 'admin' OR l.customer.id = :authUserId)")
    Page<Loan> findAllLoansForUser(@Param("customerId") Long customerId, @Param("authUserId") String authUserId, Pageable pageable);


    // Pagination support for loans by customer ID
    Page<Loan> findAllByCustomer_Id(Long customerId, Pageable pageable);

    // Repository
    Page<Loan> findAllByCustomer_IdAndNumberOfInstallment(Long customerId, Integer numberOfInstallments, Pageable pageable);

    Page<Loan> findAllByCustomer_IdAndIsPaid(Long customerId, Boolean isPaid, Pageable pageable);

    Page<Loan> findAllByCustomer_IdAndNumberOfInstallmentAndIsPaid(Long customerId, Integer numberOfInstallments, Boolean isPaid, Pageable pageable);
}
