package com.murat.ing.credit_module.repository;

import com.murat.ing.credit_module.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Custom Query Method: Search for customers containing a specific name
    List<Customer> findByNameContaining(String name);

    // Custom Query Method: Filter customers with a credit limit greater than specified
    List<Customer> findByCreditLimitGreaterThan(Double creditLimit);
}


