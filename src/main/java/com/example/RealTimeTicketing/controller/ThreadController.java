package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.model.Configuration;
import com.example.RealTimeTicketing.model.Customer;
import com.example.RealTimeTicketing.model.Vendor;
import com.example.RealTimeTicketing.service.ConfigurationService;
import com.example.RealTimeTicketing.service.CustomerService;
import com.example.RealTimeTicketing.service.ThreadConfigurationService;
import com.example.RealTimeTicketing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ThreadConfigurationService threadConfigurationService;

    @Autowired
    private ConfigurationService configurationService;

    // Map to keep track of customer threads
    private final Map<String, Thread> customerThreads = new HashMap<>();
    private final Map<String, Thread> vendorThreads = new HashMap<>();


    @Autowired
    private ApplicationContext applicationContext;

    // Start simulation based on saved thread counts
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startSimulation() {
        threadConfigurationService.setStopSimulation(false);

        List<Vendor> vendors = vendorService.getAllVendors();
        List<Customer> customers = customerService.getAllCustomers();

        Configuration configuration = configurationService.getConfiguration();

        // Retrieve values from the Configuration object
        int ticketReleaseRate = configuration.getTicketReleaseRate();
        int retrievalRate = configuration.getCustomerRetrievalRate();


        for (int i = 0; i < vendors.size(); i++) {
            Vendor vendor = vendors.get(i);
            VendorService vendorService = applicationContext.getBean(VendorService.class);
            vendorService.setVendorDetails(vendor.getId(), retrievalRate); // Set the details
            Thread vendorThread = new Thread(vendorService, "VendorThread-" + (i+1));
            vendorThread.start();
            customerThreads.put(vendor.getId(), vendorThread);
        }

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            CustomerService customerService = applicationContext.getBean(CustomerService.class);
            customerService.setCustomerDetails(customer.getId(), retrievalRate); // Set the details
            Thread customerThread = new Thread(customerService, "CustomerThread-" + (i+1));
            customerThread.start();
            customerThreads.put(customer.getId(), customerThread);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Simulation started successfully.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String,String>> stopSimulation() {
        threadConfigurationService.setStopSimulation(true);

        for (Thread thread : customerThreads.values()) {
            thread.interrupt();
            System.out.println(thread.getName()+" is stopped.");
        }

        for (Thread thread : vendorThreads.values()) {
            thread.interrupt();
            System.out.println(thread.getName()+" is stopped.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Simulation stopped.");

        return ResponseEntity.ok(response);

    }
}

