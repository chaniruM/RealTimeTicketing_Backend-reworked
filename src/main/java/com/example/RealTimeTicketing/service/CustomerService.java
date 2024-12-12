package com.example.RealTimeTicketing.service;

import com.example.RealTimeTicketing.model.Customer;
import com.example.RealTimeTicketing.model.Ticket;
import com.example.RealTimeTicketing.repository.CustomerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class simulating customer behavior in the real-time ticketing system.
 * Creates a separate thread to continuously attempt purchasing tickets for a specific customer
 * at a defined retrieval rate.
 */
@Service
public class CustomerService implements Runnable {

    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    //for interacting with the TicketPool to purchase tickets.
    @Autowired
    private TicketPoolService ticketPoolService;

    //for interacting with the Customer database table.
    @Autowired
    private CustomerRepository customerRepository;

    //Unique identifier of the customer this service represents.
    private String customerId;

    //Retrieval rate (in seconds) at which the customer attempts to purchase tickets.
    private int retrievalRate;

    /**
     * Sets the customer details (ID and retrieval rate) for this service instance.
     *
     * @param customerId Unique identifier of the customer.
     * @param retrievalRate Retrieval rate (in seconds) for the customer.
     */
    public void setCustomerDetails(String customerId, int retrievalRate) {
        this.customerId = customerId;
        this.retrievalRate = retrievalRate;
    }

    /**
     * Sets the dependencies (injected services) for this service instance.
     *
     * @param ticketPoolService Service instance for interacting with the TicketPool.
     * @param customerRepository Repository instance for interacting with the Customer table.
     */
    public void setDependencies(TicketPoolService ticketPoolService, CustomerRepository customerRepository) {
        this.ticketPoolService = ticketPoolService;
        this.customerRepository = customerRepository;
    }

    /**
     * The main thread run method that simulates customer behavior.
     * It continuously attempts to purchase tickets for the assigned customer at the retrieval rate
     * until all tickets are sold out.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (ticketPoolService.allTicketsSold()) {
                    logger.info("Customer " + Thread.currentThread().getName() + " finished purchasing tickets.");
                    break;
                }

                Ticket ticket = ticketPoolService.removeTicket(customerId);
                if (ticket != null) {
                    logger.info("Customer " + Thread.currentThread().getName() + " purchased Ticket-" + ticket.getTicketId());
                }else {
                    logger.warn("Customer " + Thread.currentThread().getName() + " failed to purchase ticket-" + ticket.getTicketId());
                }
                Thread.sleep(retrievalRate * 1000); // Simulate retrieval delay
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn(Thread.currentThread().getName() + " was interrupted.");
        }finally {
            logger.info(Thread.currentThread().getName() + " is stopping gracefully.");
        }
    }

    /**
     * Saves a new customer to the database.
     *
     * @param customer The Customer object to be saved.
     * @return The saved Customer object.
     */
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Retrieves a list of all customers from the database.
     *
     * @return A list of all Customer objects.
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Deletes a customer from the database by their ID.
     *
     * @param id The unique identifier of the customer to be deleted.
     */
    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }
}
