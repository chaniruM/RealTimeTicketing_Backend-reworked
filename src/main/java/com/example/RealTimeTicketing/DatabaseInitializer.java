package com.example.RealTimeTicketing;

import com.example.RealTimeTicketing.repository.CustomerRepository;
import com.example.RealTimeTicketing.repository.TicketRepository;
import com.example.RealTimeTicketing.repository.VendorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * This class initializes the database by clearing all existing data on application startup.
 * It's useful for testing and development purposes to ensure a clean database state.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VendorRepository vendorRepository;

    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);

    /**
     * Clears all data from the customer, ticket, and vendor repositories on application startup.
     *
     * @param args Command-line arguments (not used in this case).
     * @throws Exception If an error occurs during database operations.
     */
    @Override
    public void run(String... args) throws Exception {
        customerRepository.deleteAll();
        ticketRepository.deleteAll();
        vendorRepository.deleteAll();
        logger.info("Database cleared on startup.");
    }
}
