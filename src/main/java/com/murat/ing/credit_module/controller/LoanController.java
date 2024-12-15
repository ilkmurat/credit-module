package com.murat.ing.credit_module.controller;

import com.murat.ing.credit_module.dto.*;
import com.murat.ing.credit_module.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest loanRequest) {
        LoanResponse loan = loanService.createLoan(
                loanRequest.getCustomerId(),
                loanRequest.getAmount(),
                loanRequest.getInterestRate(),
                loanRequest.getNumberOfInstallments()
        );

        return ResponseEntity.ok(loan);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<LoanResponse>> listLoans(
            @PathVariable Long customerId,
            @RequestParam(required = false) Integer numberOfInstallments,
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LoanResponse> loans = loanService.listLoans(customerId, numberOfInstallments, isPaid, pageable);
        return ResponseEntity.ok(loans);
    }


    @GetMapping("/{loanId}/installments")
    public ResponseEntity<Page<LoanInstallmentResponse>> listInstallments(
            @PathVariable Long loanId,
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<LoanInstallmentResponse> installments = loanService.listInstallments(loanId, isPaid, sortBy, sortDirection, page, size);
        return ResponseEntity.ok(installments);
    }


    @DeleteMapping("/{loanId}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long loanId) {
        loanService.deleteLoan(loanId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{loanId}/pay")
    public ResponseEntity<PaymentResponse> payLoan(
            @PathVariable Long loanId,
            @RequestBody @Valid PaymentRequest paymentRequest) {
        PaymentResponse summary = loanService.payLoan(loanId, paymentRequest.getAmount());
        return ResponseEntity.ok(summary);
    }

}
