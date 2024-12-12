package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.model.Configuration;
import com.example.RealTimeTicketing.service.ConfigurationService;
import com.example.RealTimeTicketing.service.TicketPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring REST controller for managing the real-time ticketing system configuration.
 * Provides endpoints for updating and retrieving the system configuration.
 */
@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private TicketPoolService ticketPoolService;

    /**
     * Updates the system configuration with the provided configuration object.
     *
     * @param configuration The Configuration object containing the updated settings.
     * @return A ResponseEntity containing a success message upon successful update.
     */
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

    /**
     * Retrieves the current system configuration.
     *
     * @return A ResponseEntity containing the retrieved Configuration object.
     */
    @GetMapping
    public ResponseEntity<Configuration> getConfiguration() {
        configurationService.loadConfiguration();

        return ResponseEntity.ok(configurationService.getConfiguration());
    }
}
