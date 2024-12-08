package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.model.Configuration;
import com.example.RealTimeTicketing.service.ConfigurationService;
import com.example.RealTimeTicketing.service.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private TicketPoolService ticketPoolService;

    @PostMapping
    public ResponseEntity<Map<String, String>> updateConfiguration(@RequestBody Configuration configuration) {
        configurationService.updateConfiguration(configuration);
        configurationService.saveConfiguration(configuration);
        ticketPoolService.setMaxTicketCapacity(configuration.getMaxTicketCapacity());
        ticketPoolService.setTotalTickets(configuration.getTotalTickets());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Configuration saved successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Configuration> getConfiguration() {
        configurationService.loadConfiguration();

        return ResponseEntity.ok(configurationService.getConfiguration());
    }
}
