package com.example.RealTimeTicketing.repository;

import com.example.RealTimeTicketing.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for managing Ticket entities.
 *
 * Inherits from `MongoRepository` to provide basic CRUD operations for Ticket entities.
 */
public interface TicketRepository extends MongoRepository<Ticket, Integer> {
}
