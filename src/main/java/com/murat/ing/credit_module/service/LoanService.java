package com.murat.ing.credit_module.service;

import com.murat.ing.credit_module.dto.LoanInstallmentResponse;
import com.murat.ing.credit_module.dto.LoanResponse;
import com.murat.ing.credit_module.dto.PaymentResponse;
import com.murat.ing.credit_module.entity.Customer;
import com.murat.ing.credit_module.entity.Loan;
import com.murat.ing.credit_module.entity.LoanInstallment;
import com.murat.ing.credit_module.exception.CustomerNotFoundException;
import com.murat.ing.credit_module.mapper.LoanMapper;
import com.murat.ing.credit_module.repository.CustomerRepository;
import com.murat.ing.credit_module.repository.LoanInstallmentRepository;
import com.murat.ing.credit_module.repository.LoanRepository;
import com.murat.ing.credit_module.service.validators.LoanValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional // Ensures that all database changes are rolled back if any exception occurs
public class LoanService {

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;
    private final LoanValidator loanValidator;

    @Value("${loan.max.installments.per.payment}")
    private int maxInstallmentsPerPayment;

    private final LoanMapper loanConverter;


    public LoanService(CustomerRepository customerRepository, LoanRepository loanRepository, LoanInstallmentRepository installmentRepository, LoanValidator loanValidator, LoanMapper loanConverter) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.installmentRepository = installmentRepository;
        this.loanValidator = loanValidator;
        this.loanConverter = loanConverter;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #customerId == principal.username)")
    public LoanResponse createLoan(Long customerId, Double amount, Double interestRate, Integer numberOfInstallments) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        loanValidator.validateCustomerCredit(customer, amount);
        loanValidator.validateInterestRate(interestRate);
        loanValidator.validateInstallments(numberOfInstallments);

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(amount * (1 + interestRate));
        loan.setNumberOfInstallment(numberOfInstallments);
        loan.setCreateDate(LocalDate.now());
        loan = loanRepository.save(loan);

        createInstallments(loan, loan.getLoanAmount() / numberOfInstallments, numberOfInstallments);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + amount);
        customerRepository.save(customer);

        return loanConverter.toLoanResponse(loan);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #customerId == principal.id)")
    public Page<LoanResponse> listLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid, Pageable pageable) {
        Specification<Loan> spec = Specification.where(hasCustomerId(customerId))
                .and(hasNumberOfInstallments(numberOfInstallments))
                .and(hasPaymentStatus(isPaid));

        Page<Loan> loans = loanRepository.findAll(spec, pageable);

        // Map Loan entities to LoanResponse DTOs
        return loans.map( loanConverter::toLoanResponse);
    }

    // Specification for filtering by customer ID
    private Specification<Loan> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("customer").get("id"), customerId);
    }

    // Specification for filtering by number of installments
    private Specification<Loan> hasNumberOfInstallments(Integer numberOfInstallments) {
        if (numberOfInstallments == null) {
            return null; // No filter for number of installments
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("numberOfInstallment"), numberOfInstallments);
    }

    // Specification for filtering by payment status
    private Specification<Loan> hasPaymentStatus(Boolean isPaid) {
        if (isPaid == null) {
            return null; // No filter for payment status
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isPaid"), isPaid);
    }


    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #loanId == principal.loanId)")
    public Page<LoanInstallmentResponse> listInstallments(Long loanId, Boolean isPaid, String sortBy, String sortDirection, int page, int size) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found"));

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LoanInstallment> installments;

        // Apply filter criteria
        if (isPaid != null) {
            installments = installmentRepository.findAllByLoan_IdAndIsPaid(loanId, isPaid, pageable);
        } else {
            installments = installmentRepository.findAllByLoan_Id(loanId, pageable);
        }

        // Map LoanInstallment entities to LoanInstallmentResponse DTOs
        return installments.map(loanConverter::toLoanInstallmentResponse);
    }


    private void createInstallments(Loan loan, Double installmentAmount, Integer numberOfInstallments) {
        for (int i = 1; i <= numberOfInstallments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setDueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1)); // Ensures the first day of the month
            installmentRepository.save(installment);
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found"));
        loanRepository.delete(loan);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public PaymentResponse payLoan(Long loanId, Double paymentAmount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan not found"));

        LocalDate now = LocalDate.now();
        List<LoanInstallment> unpaidInstallments = installmentRepository.findAllByLoan_IdAndIsPaidFalseOrderByDueDateAsc(loanId);

        double remainingAmount = paymentAmount;
        int installmentsPaid = 0;
        double totalPaid = 0.0;
        double totalPenaltyOrReward = 0.0;

        for (LoanInstallment installment : unpaidInstallments) {
            if (!loanValidator.isInstallmentEligibleForPayment(installment, now)) {
                continue;
            }

            double adjustedAmount = loanValidator.calculatePenaltyOrReward(installment, now);

            if (remainingAmount >= adjustedAmount && installmentsPaid < maxInstallmentsPerPayment && !installment.getPaid()) {
                remainingAmount -= adjustedAmount;
                totalPaid += adjustedAmount;
                installmentsPaid++;

                installment.setPaid(true);
                installment.setPaymentDate(now);
                installment.setPaidAmount(adjustedAmount);
                installmentRepository.save(installment);
            } else {
                break;
            }
        }

        // Update loan status
        boolean loanFullyPaid = unpaidInstallments.stream().allMatch(LoanInstallment::getPaid);
        if (loanFullyPaid) {
            loan.setPaid(true);
            loanRepository.save(loan);
        }

        // Update customer credit limit
        Customer customer = loan.getCustomer();
        customer.setUsedCreditLimit(customer.getUsedCreditLimit() - totalPaid);
        customerRepository.save(customer);


        return new PaymentResponse(installmentsPaid, totalPaid, totalPenaltyOrReward, loanFullyPaid);
    }

}
