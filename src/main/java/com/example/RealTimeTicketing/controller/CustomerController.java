package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.model.Customer;
import com.example.RealTimeTicketing.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing customer operations.
 * Provides endpoints for creating, retrieving, and deleting customers.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Creates a new customer.
     *
     * @param customer The customer object to be created.
     * @return The created customer object.
     */
    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.addCustomer(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    /**
     * Retrieves a list of all customers.
     *
     * @return A list of all customers.
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
}
