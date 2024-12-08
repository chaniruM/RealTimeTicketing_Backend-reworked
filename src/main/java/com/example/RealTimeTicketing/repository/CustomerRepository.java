package com.example.RealTimeTicketing.repository;

import com.example.RealTimeTicketing.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {
}
