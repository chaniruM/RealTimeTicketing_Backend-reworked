package com.example.RealTimeTicketing.service;

import com.example.RealTimeTicketing.WebSocket.LogWebSocketHandler;
import com.example.RealTimeTicketing.model.Customer;
import com.example.RealTimeTicketing.model.Ticket;
import com.example.RealTimeTicketing.model.Vendor;
import com.example.RealTimeTicketing.repository.CustomerRepository;
import com.example.RealTimeTicketing.repository.TicketRepository;
import com.example.RealTimeTicketing.repository.VendorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class responsible for managing the ticket pool in the real-time ticketing system.
 * It uses a thread-safe approach to handle concurrent access from vendors and customers.
 */
@Service
public class TicketPoolService {

    //for interacting with the Ticket database table.
    @Autowired
    private TicketRepository ticketRepository;

    //for interacting with the Vendor database table.
    @Autowired
    private VendorRepository vendorRepository;

    //for interacting with the Customer database table.
    @Autowired
    private CustomerRepository customerRepository;

    //for sending real-time ticket movement logs to the frontend.
    @Autowired
    private LogWebSocketHandler logWebSocketHandler;

    private static final Logger logger = LogManager.getLogger(TicketPoolService.class);

    //Thread-safe structure to store tickets in the pool.
    private final List<Ticket> ticketpool = Collections.synchronizedList(new ArrayList<>());

    //Reentrant lock for thread-safe synchronization of access to the ticket pool and its methods.
    private final ReentrantLock lock = new ReentrantLock();

    //Condition object used to signal waiting customer threads when new tickets are added.
    private final Condition notEmpty = lock.newCondition();

    //Condition object used to signal waiting vendor threads when space becomes available in the pool.
    private final Condition notFull = lock.newCondition();

    //Total number of tickets to be released for the event.
    private int totalTickets;

    //Maximum capacity of the ticket pool (number of tickets it can hold at any given time).
    private int maxTicketCapacity;

    //Atomic integer to track the total number of tickets sold across all customers.
    private final AtomicInteger ticketsSold = new AtomicInteger(0);


    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public synchronized int getTicketsSold() {
        return ticketsSold.get();
    }

    public synchronized boolean allTicketsSold() {
        return ticketsSold.get() >= totalTickets;
    }

    /**
     * Adds a new ticket to the ticket pool for a specified event by a vendor.
     * This method is synchronized to ensure thread safety when multiple vendors try to add tickets concurrently.
     * It uses conditions to manage waiting threads:
     *  - `notFull`: Signals waiting vendors when space becomes available in the pool.
     *  - `notEmpty`: Signals waiting customers when new tickets are added to the pool.
     *
     * @param eventName  The name of the event for which the ticket is valid.
     * @param ticketPrice The price of the ticket.
     * @param vendorId   The unique identifier of the vendor adding the ticket.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    public void addTicket(String eventName, double ticketPrice, String vendorId) throws InterruptedException {
        lock.lock();
        try {
            // Wait until there's space in the pool
            while (ticketpool.size() >= maxTicketCapacity) {
                notFull.await();
                logger.info("TicketPool full. "+ Thread.currentThread().getName()+" waiting for tickets to be sold...");
            }

            // Generate a unique ticket ID using the database count + 1
            int ticketId;
            synchronized (this) {
                ticketId = (int) ticketRepository.count() + 1; // Ticket ID is count in DB + 1
            }

            // Create a new Ticket object with details
            Ticket ticket = new Ticket(ticketId, eventName, ticketPrice, vendorId);
            ticket.setVendorId(vendorId);
            ticket.setStatus("available");
            ticket.setCreatedAt(LocalDateTime.now());

            // Add the ticket to the pool and save it to the database
            ticketpool.add(ticket);
            ticketRepository.save(ticket);

            // Find the vendor name
            Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
            String vendorName = (vendor != null) ? vendor.getName() : "Unknown Vendor";

            logger.info("Vendor " + vendorName + " added Ticket-" + ticketId);
            logTicketMovement("Ticket-"+ ticketId + " added by "+ vendorName +" for: " + eventName);  // Log the addition

            notEmpty.signalAll(); // Notify customers waiting for tickets
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a ticket from the pool and assigns it to a customer.
     * This method is synchronized to ensure thread safety when multiple customers try to purchase tickets concurrently.
     * It uses conditions to manage waiting threads:
     *  - `notEmpty`: Signals waiting customers when new tickets are added to the pool.
     *  - `notFull`: Signals waiting vendors when space becomes available in the pool after a sale.
     *
     * @param customerId The unique identifier of the customer trying to purchase a ticket.
     * @return The purchased Ticket object, or null if no tickets are available or an error occurs.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    public Ticket removeTicket(String customerId) throws InterruptedException {
        lock.lock();
        try {
            // Wait until a ticket is available or all tickets are sold
            while (ticketpool.isEmpty() && ticketsSold.get() < totalTickets) {
                logger.info("TicketPool Empty. "+Thread.currentThread().getName()+" waiting for tickets to be added...");
                notEmpty.await();
            }

            // Check if all tickets are sold out
            if (ticketpool.isEmpty() && ticketsSold.get() >= totalTickets) {
                logger.info("Tickets sold out!");
                return null; // No more tickets to sell
            }

            // Remove the first ticket from the pool
            Ticket ticket = ticketpool.remove(0);
            ticketsSold.incrementAndGet(); // Increment tickets sold count

            // Update the ticket information in the database (sold status and customer ID)
            Ticket ticketInDb = ticketRepository.findById(ticket.getTicketId()).orElse(null);
            if (ticketInDb == null) {
                logger.warn("Ticket not found");
                return null;
            }
            ticketInDb.setStatus("sold");
            ticketInDb.setCustomerId(customerId);
            ticketRepository.save(ticketInDb);

            // Find the customer name
            Customer customer = customerRepository.findById(customerId).orElse(null);
            String customerName = (customer != null) ? customer.getName() : "Unknown Vendor";
            logTicketMovement("Ticket-"+ ticket.getTicketId() + " bought by "+ customerName +" for LKR." + ticket.getTicketPrice());

            notFull.signalAll(); // Notify vendors waiting to add tickets
            return ticket;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Logs ticket movement events to the WebSocket handler for real-time updates.
     *
     * @param logMessage The message to be logged and sent to the frontend.
     */
    public void logTicketMovement(String logMessage) {
        try {
            logWebSocketHandler.sendLog(logMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTicketsSize() {
        return ticketpool.size();
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getTotalTicketsAdded() {
        return (int) ticketRepository.count();
    }
}
