package com.example.RealTimeTicketing.repository;

import com.example.RealTimeTicketing.model.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for managing Vendor entities.
 *
 * Inherits from `MongoRepository` to provide basic CRUD operations for Vendor entities.
 */
public interface VendorRepository extends MongoRepository<Vendor, String> {
}
