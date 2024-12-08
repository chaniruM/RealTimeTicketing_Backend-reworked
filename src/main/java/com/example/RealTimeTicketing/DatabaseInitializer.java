package com.example.RealTimeTicketing;

import com.example.RealTimeTicketing.repository.CustomerRepository;
import com.example.RealTimeTicketing.repository.TicketRepository;
import com.example.RealTimeTicketing.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public void run(String... args) throws Exception {
        customerRepository.deleteAll();
        ticketRepository.deleteAll();
        vendorRepository.deleteAll();
        System.out.println("Database cleared on startup.");
    }
}
