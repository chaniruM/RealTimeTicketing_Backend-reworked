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

/**
 * Service class responsible for simulating customer behavior in the real-time ticketing system.
 * It creates a separate thread to continuously attempt purchasing tickets for a specific customer
 * at a defined retrieval rate.
 *
 * **Thread Safety Considerations:**
 * This class uses a prototype scope (`@Scope("prototype")`) which creates a new bean instance
 * for each injection. However, the `run` method doesn't explicitly handle thread safety concerns.
 * If multiple customer threads try to access the `ticketPoolService` methods concurrently,
 * it might lead to unexpected behavior. Consider using synchronized blocks or thread-safe collections
 * within the `run` method for better control.
 */
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
                if (ticketPoolService.allTicketsSold()) {
                    logger.info("Customer " + customerName + " finished purchasing tickets.");
                    break;
                }

                Ticket ticket = ticketPoolService.removeTicket(customerId);
                if (ticket != null) {
                    logger.info("Customer " + customerName + " purchased Ticket-" + ticket.getTicketId());
                }else {
                    logger.warn("Customer " + customerName + " failed to purchase ticket-" + ticket.getTicketId());
                }
                Thread.sleep(retrievalRate * 1000); // Simulate retrieval delay
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " was interrupted.");
            logger.warn(Thread.currentThread().getName() + " was interrupted.");
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
