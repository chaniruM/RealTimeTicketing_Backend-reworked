package com.example.RealTimeTicketing.repository;

import com.example.RealTimeTicketing.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, Integer> {
}
