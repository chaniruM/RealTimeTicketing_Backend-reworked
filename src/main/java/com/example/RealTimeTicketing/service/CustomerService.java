package com.example.RealTimeTicketing.service;

import com.example.RealTimeTicketing.model.Customer;
import com.example.RealTimeTicketing.model.Ticket;
import com.example.RealTimeTicketing.repository.CustomerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Scope("prototype")
public class CustomerService implements Runnable {

    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private CustomerRepository customerRepository;

    private String customerId;
    private int retrievalRate;

    public void setCustomerDetails(String customerId, int retrievalRate) {
        this.customerId = customerId;
        this.retrievalRate = retrievalRate;
    }

    @Override
    public void run() {
        try {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            String customerName = (customer != null) ? customer.getName() : "Unknown Customer";
            while (true) {
//                if (threadConfigurationService.isStopSimulation()) {
//                    System.out.println("Customer " + customerName + " stopping as simulation is stopped.");
//                    break;
//                }
                if (ticketPoolService.allTicketsSold()) {
                    System.out.println("Customer " + customerName + " finished purchasing tickets.");
                    break;
                }

                Ticket ticket = ticketPoolService.removeTicket(customerId);
                if (ticket != null) {
                    System.out.println("Customer " + customerName + " purchased Ticket-" + ticket.getTicketId());
//                    logger.info("Customer " + customerName + " purchased Ticket-" + ticket.getTicketId());
                }
                Thread.sleep(retrievalRate * 1000); // Simulate retrieval delay
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
//            System.err.println("Customer " + customerId + " was interrupted.");
            System.out.println(Thread.currentThread().getName() + " was interrupted.");
        }
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }
}
