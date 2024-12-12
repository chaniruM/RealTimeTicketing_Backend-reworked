package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.service.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for retrieving information about the ticket pool.
 * Provides an endpoint to get the current status of the ticket pool.
 */
@RestController
@RequestMapping("/api/ticket-pool")
public class TicketPoolController {

    @Autowired
    private TicketPoolService ticketPoolService;

    /**
     * Retrieves the current status of the ticket pool, including:
     *  - currentTicketCount: Number of tickets currently available in the pool.
     *  - ticketsToRelease: Number of tickets remaining to be released based on the configured total tickets.
     *  - ticketsSold: Number of tickets sold so far.
     *
     * @return A ResponseEntity containing a Map with the ticket pool status information.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getStatus() {
        Map<String, Integer> status = new HashMap<>();
        status.put("currentTicketCount", ticketPoolService.getTicketsSize());
        status.put("ticketsToRelease", ticketPoolService.getTotalTickets() - ticketPoolService.getTotalTicketsAdded());
        status.put("ticketsSold", ticketPoolService.getTicketsSold());
        return ResponseEntity.ok(status);
    }
}