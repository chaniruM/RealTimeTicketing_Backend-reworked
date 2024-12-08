package com.example.RealTimeTicketing.repository;

import com.example.RealTimeTicketing.model.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VendorRepository extends MongoRepository<Vendor, String> {
}
