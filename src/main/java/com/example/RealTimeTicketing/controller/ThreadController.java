package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.model.Configuration;
import com.example.RealTimeTicketing.model.Customer;
import com.example.RealTimeTicketing.model.Vendor;
import com.example.RealTimeTicketing.repository.CustomerRepository;
import com.example.RealTimeTicketing.repository.TicketRepository;
import com.example.RealTimeTicketing.repository.VendorRepository;
import com.example.RealTimeTicketing.service.ConfigurationService;
import com.example.RealTimeTicketing.service.CustomerService;
import com.example.RealTimeTicketing.service.TicketPoolService;
import com.example.RealTimeTicketing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing the simulation threads.
 * Provides endpoints for starting and stopping the simulation.
 */
@RestController
@RequestMapping("/api/threads")
public class ThreadController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Map to keep track of customer threads
    private final Map<String, Thread> customerThreads = new HashMap<>();
    private final Map<String, Thread> vendorThreads = new HashMap<>();

    /**
     * Starts the simulation based on the saved configuration and customer/vendor data.
     * Creates and starts threads for each customer and vendor, managing them in the respective maps.
     *
     * @return A ResponseEntity indicating successful simulation start.
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startSimulation() {

        List<Vendor> vendors = vendorService.getAllVendors();
        List<Customer> customers = customerService.getAllCustomers();

        Configuration configuration = configurationService.getConfiguration();

        int ticketReleaseRate = configuration.getTicketReleaseRate();
        int customerRetrievalRate = configuration.getCustomerRetrievalRate();


        for (int i = 0; i < vendors.size(); i++) {
            Vendor vendor = vendors.get(i);
            VendorService newVendorService = new VendorService();
            newVendorService.setDependencies(ticketPoolService, vendorRepository, ticketRepository);
            newVendorService.setVendorDetails(vendor.getId(), ticketReleaseRate); // Set the details
            Thread vendorThread = new Thread(newVendorService, vendor.getName());
            vendorThread.start();
            vendorThreads.put(vendor.getId(), vendorThread);
        }

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            CustomerService newCustomerService = new CustomerService();
            newCustomerService.setDependencies(ticketPoolService, customerRepository);
            newCustomerService.setCustomerDetails(customer.getId(), customerRetrievalRate); // Set the details
            Thread customerThread = new Thread(newCustomerService, customer.getName());
            customerThread.start();
            customerThreads.put(customer.getId(), customerThread);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Simulation started successfully.");

        return ResponseEntity.ok(response);
    }

    /**
     * Stops the ongoing simulation by interrupting all active threads.
     *
     * @return A ResponseEntity indicating successful simulation stop.
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String,String>> stopSimulation() {

        for (Thread thread : customerThreads.values()) {
            thread.interrupt();
            System.out.println(thread.getName()+" is stopping as simulation is stopped.");
        }

        for (Thread thread : vendorThreads.values()) {
            thread.interrupt();
            System.out.println(thread.getName()+" is stopping as simulation is stopped.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Simulation stopped.");

        return ResponseEntity.ok(response);

    }
}

