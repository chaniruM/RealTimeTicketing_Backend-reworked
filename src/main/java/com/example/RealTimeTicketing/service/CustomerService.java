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


//public class CustomerService {
//
//    @Autowired
//    private TicketPoolService ticketPoolService;
//
//    @Autowired
//    private ThreadConfigurationService threadConfigurationService;
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    public void startCustomer(String customerId, int retrievalRate) {
//        Runnable customerTask = new CustomerTask(customerId, retrievalRate);
//        new Thread(customerTask, "CustomerThread-" + customerId).start();
//    }
//
//    public class CustomerTask implements Runnable {
//        private final String customerId;
//        private final int retrievalRate;
//
//        public CustomerTask(String customerId, int retrievalRate) {
//            this.customerId = customerId;
//            this.retrievalRate = retrievalRate;
//        }
//
//        @Override
//        public void run() {
//            try {
//                Customer customer = customerRepository.findById(customerId).orElse(null);
//                String customerName = (customer != null) ? customer.getName() : "Unknown Customer";
//                while (true) {
//                    if (threadConfigurationService.isStopSimulation()) {
////                        System.out.println("Customer " + customerId + " stopping as simulation is stopped.");
//                        System.out.println("Customer " + customerName + " stopping as simulation is stopped.");
//                        break;
//                    }
//                    if (ticketPoolService.allTicketsSold()) {
////                        System.out.println("Customer " + customerId + " finished purchasing tickets.");
//                        System.out.println("Customer " + customerName + " finished purchasing tickets.");
//                        break;
//                    }
//
//                    Ticket ticket = ticketPoolService.purchaseTicket(customerId);
//                    if (ticket != null) {
////                        System.out.println("Customer " + customerId + " purchased Ticket-" + ticket.getTicketId());
//                        System.out.println("Customer " + customerName + " purchased Ticket-" + ticket.getTicketId());
//                    }
//                    Thread.sleep(retrievalRate * 1000); // Simulate retrieval delay
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                System.err.println("Customer " + customerId + " was interrupted.");
//            }
//
//        }
//    }

@Service
@Scope("prototype")
public class CustomerService implements Runnable {

    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private ThreadConfigurationService threadConfigurationService;

    @Autowired
    private CustomerRepository customerRepository;

    private String customerId;
    private int retrievalRate;

//    public void startCustomer(String customerId, int retrievalRate) {
//        this.customerId = customerId;
//        this.retrievalRate = retrievalRate;
//        new Thread(this, "CustomerThread-" + customerId).start();
//    }
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
                if (threadConfigurationService.isStopSimulation()) {
                    System.out.println("Customer " + customerName + " stopping as simulation is stopped.");
                    break;
                }
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
            System.err.println("Customer " + customerId + " was interrupted.");
        }
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
