package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.service.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ticket-pool")
public class TicketPoolController {

    @Autowired
    private TicketPoolService ticketPoolService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getStatus() {
        Map<String, Integer> status = new HashMap<>();
        status.put("currentTicketCount", ticketPoolService.getTicketsSize());
        status.put("ticketsToRelease", ticketPoolService.getTotalTickets() - ticketPoolService.getTotalTicketsAdded());
        status.put("ticketsSold", ticketPoolService.getTicketsSold());
        return ResponseEntity.ok(status);
    }
}