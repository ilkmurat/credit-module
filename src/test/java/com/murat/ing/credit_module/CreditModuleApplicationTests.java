package com.murat.ing.credit_module;

import com.murat.ing.credit_module.dto.LoanInstallmentResponse;
import com.murat.ing.credit_module.dto.LoanResponse;
import com.murat.ing.credit_module.dto.PaymentResponse;
import com.murat.ing.credit_module.entity.Customer;
import com.murat.ing.credit_module.entity.Loan;
import com.murat.ing.credit_module.entity.LoanInstallment;
import com.murat.ing.credit_module.mapper.LoanMapper;
import com.murat.ing.credit_module.repository.CustomerRepository;
import com.murat.ing.credit_module.repository.LoanInstallmentRepository;
import com.murat.ing.credit_module.repository.LoanRepository;
import com.murat.ing.credit_module.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditModuleApplicationTests {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    private Customer customer;
    private Loan loan;

    private LoanMapper loanConverter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loanConverter =new LoanMapper();
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setCreditLimit(10000.0);
        customer.setUsedCreditLimit(2000.0);

        loan = new Loan();
        loan.setId(1L);
        loan.setCustomer(customer);
        loan.setLoanAmount(5000.0);
        loan.setNumberOfInstallment(12);
        loan.setCreateDate(LocalDate.now());
    }

    @Test
    void testCreateLoan_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        //when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanInstallmentRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        LoanResponse response =  loanConverter.toLoanResponse(loan);

        assertNotNull(response);
        assertEquals(5000.0 , response.getLoanAmount());
        assertEquals(12, response.getNumberOfInstallments());

        verify(customerRepository).save(customer);
        //verify(customerRepository).save(eq(customer));


    }

    @Test
    void testCreateLoan_InsufficientCredit() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThrows(IllegalArgumentException.class, () ->
                loanService.createLoan(1L, 9000.0, 0.2, 12));

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testListLoans_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(loanRepository.findAllByCustomer_Id(eq(1L), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(loan)));

        Page<LoanResponse> loans = loanService.listLoans(1L, null, null, pageable);

        assertNotNull(loans);
        assertEquals(1, loans.getTotalElements());
        assertEquals(5000.0, loans.getContent().get(0).getLoanAmount());
    }

    @Test
    void testListInstallments_Success() {
        LoanInstallment installment = new LoanInstallment();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(416.67);
        installment.setDueDate(LocalDate.now().plusMonths(1));
        installment.setPaid(false);

        Pageable pageable = PageRequest.of(0, 10);
        when(loanInstallmentRepository.findAllByLoan_Id(eq(1L), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(installment)));

        Page<LoanInstallmentResponse> installments = loanService.listInstallments(1L, null, "dueDate", "asc", 0, 10);

        assertNotNull(installments);
        assertEquals(1, installments.getTotalElements());
        assertEquals(416.67, installments.getContent().get(0).getAmount());
    }

    @Test
    void testPayLoan_Success() {
        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setLoan(loan);
        installment1.setAmount(416.67);
        installment1.setDueDate(LocalDate.now().plusMonths(1));
        installment1.setPaid(false);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setLoan(loan);
        installment2.setAmount(416.67);
        installment2.setDueDate(LocalDate.now().plusMonths(2));
        installment2.setPaid(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoan_IdAndIsPaidFalseOrderByDueDateAsc(1L))
                .thenReturn(List.of(installment1, installment2));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = loanService.payLoan(1L, 833.34);

        assertNotNull(response);
        assertEquals(2, response.getInstallmentsPaid());
        assertEquals(833.34, response.getTotalPaid());
        assertFalse(response.isLoanFullyPaid());
    }

    @Test
    void testPayLoan_InsufficientAmount() {
        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setLoan(loan);
        installment1.setAmount(416.67);
        installment1.setDueDate(LocalDate.now().plusMonths(1));
        installment1.setPaid(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoan_IdAndIsPaidFalseOrderByDueDateAsc(1L))
                .thenReturn(List.of(installment1));

        PaymentResponse response = loanService.payLoan(1L, 200.0);

        assertNotNull(response);
        assertEquals(0, response.getInstallmentsPaid());
        assertEquals(0.0, response.getTotalPaid());
        assertFalse(response.isLoanFullyPaid());
    }

    @Test
    void testPayLoan_WithPenaltyAndReward() {
        LoanInstallment earlyInstallment = new LoanInstallment();
        earlyInstallment.setId(1L);
        earlyInstallment.setLoan(loan);
        earlyInstallment.setAmount(416.67);
        earlyInstallment.setDueDate(LocalDate.now().plusDays(10)); // Due in 10 days
        earlyInstallment.setPaid(false);

        LoanInstallment lateInstallment = new LoanInstallment();
        lateInstallment.setId(2L);
        lateInstallment.setLoan(loan);
        lateInstallment.setAmount(416.67);
        lateInstallment.setDueDate(LocalDate.now().minusDays(5)); // 5 days late
        lateInstallment.setPaid(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoan_IdAndIsPaidFalseOrderByDueDateAsc(1L))
                .thenReturn(List.of(earlyInstallment, lateInstallment));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = loanService.payLoan(1L, 833.34);

        assertNotNull(response);
        assertEquals(2, response.getInstallmentsPaid());
        assertTrue(response.getTotalPaid() > 833.34); // Includes penalty
        assertFalse(response.isLoanFullyPaid());
    }

    @Test
    void testListLoans_AdminAccess() {
        Pageable pageable = PageRequest.of(0, 10);
        when(loanRepository.findAllByCustomer_Id(eq(1L), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(loan)));

        Page<LoanResponse> loans = loanService.listLoans(1L, null, null, pageable);

        assertNotNull(loans);
        assertEquals(1, loans.getTotalElements());
        assertEquals(5000.0, loans.getContent().get(0).getLoanAmount());
    }

    @Test
    void testListLoans_CustomerAccess() {
        Pageable pageable = PageRequest.of(0, 10);
        when(loanRepository.findAllByCustomer_Id(eq(1L), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(loan)));

        Page<LoanResponse> loans = loanService.listLoans(1L, null, null, pageable);

        assertNotNull(loans);
        assertEquals(1, loans.getTotalElements());
        assertEquals(5000.0, loans.getContent().get(0).getLoanAmount());
    }
    @Test
    void testListInstallments_AdminAccess() {
        LoanInstallment installment = new LoanInstallment();
        installment.setId(1L);
        installment.setLoan(loan);
        installment.setAmount(416.67);
        installment.setDueDate(LocalDate.now().plusMonths(1));
        installment.setPaid(false);

        Pageable pageable = PageRequest.of(0, 10);
        when(loanInstallmentRepository.findAllByLoan_Id(eq(1L), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(installment)));

        Page<LoanInstallmentResponse> installments = loanService.listInstallments(1L, null, "dueDate", "asc", 0, 10);

        assertNotNull(installments);
        assertEquals(1, installments.getTotalElements());
        assertEquals(416.67, installments.getContent().get(0).getAmount());
    }

    @Test
    void testPayLoan_WithRewardOnly() {
        LoanInstallment earlyInstallment = new LoanInstallment();
        earlyInstallment.setId(1L);
        earlyInstallment.setLoan(loan);
        earlyInstallment.setAmount(416.67);
        earlyInstallment.setDueDate(LocalDate.now().plusDays(10)); // 10 gün erken ödenebilir
        earlyInstallment.setPaid(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoan_IdAndIsPaidFalseOrderByDueDateAsc(1L))
                .thenReturn(List.of(earlyInstallment));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = loanService.payLoan(1L, 400.0);

        assertNotNull(response);
        assertEquals(1, response.getInstallmentsPaid());
        assertTrue(response.getTotalPaid() < 416.67); // Reward uygulanmış
        assertFalse(response.isLoanFullyPaid());
    }

    @Test
    void testPayLoan_WithPenaltyOnly() {
        LoanInstallment lateInstallment = new LoanInstallment();
        lateInstallment.setId(1L);
        lateInstallment.setLoan(loan);
        lateInstallment.setAmount(416.67);
        lateInstallment.setDueDate(LocalDate.now().minusDays(5)); // 5 gün gecikmeli
        lateInstallment.setPaid(false);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanInstallmentRepository.findAllByLoan_IdAndIsPaidFalseOrderByDueDateAsc(1L))
                .thenReturn(List.of(lateInstallment));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = loanService.payLoan(1L, 450.0);

        assertNotNull(response);
        assertEquals(1, response.getInstallmentsPaid());
        assertTrue(response.getTotalPaid() > 416.67); // Penalty uygulanmış
        assertFalse(response.isLoanFullyPaid());
    }

}
