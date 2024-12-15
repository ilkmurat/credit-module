package com.murat.ing.credit_module.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Positive
    private Double loanAmount;

    @NotNull
    private Integer numberOfInstallment;

    private Boolean isPaid = false;

    private LocalDate createDate;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanInstallment> installments = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Integer getNumberOfInstallment() {
        return numberOfInstallment;
    }

    public void setNumberOfInstallment(Integer numberOfInstallment) {
        this.numberOfInstallment = numberOfInstallment;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public List<LoanInstallment> getInstallments() {
        return installments;
    }

    public void setInstallments(List<LoanInstallment> installments) {
        this.installments = installments;
    }
}
