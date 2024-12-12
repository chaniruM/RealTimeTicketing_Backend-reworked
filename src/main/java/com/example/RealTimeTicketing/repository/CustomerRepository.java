package com.example.RealTimeTicketing.repository;

import com.example.RealTimeTicketing.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for managing Customer entities.
 *
 * Inherits from `MongoRepository` to provide basic CRUD operations for Customer entities.
 */
public interface CustomerRepository extends MongoRepository<Customer, String> {
}
