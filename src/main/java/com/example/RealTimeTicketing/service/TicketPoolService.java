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

@Service
public class TicketPoolService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LogWebSocketHandler logWebSocketHandler;

    private static final Logger logger = LogManager.getLogger(TicketPoolService.class);

    private final List<Ticket> ticketpool = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    private int totalTickets; // Total tickets to be released
    private int maxTicketCapacity; // Max capacity of the ticket pool


    public AtomicInteger getTicketsAdded() {
        return ticketsAdded;
    }

    private final AtomicInteger ticketsAdded = new AtomicInteger(0); // Shared counter for total tickets added
    private final AtomicInteger ticketsSold = new AtomicInteger(0); // Shared counter for total tickets sold

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

    public synchronized boolean canAddMoreTickets() {
        return ticketsAdded.get() < totalTickets; // Check if more tickets can be added
    }

    public void addTicket(int ticketId, String eventName, double ticketPrice, String vendorId) throws InterruptedException {
        lock.lock();
        try {
            while (ticketpool.size() >= maxTicketCapacity) {
                notFull.await(); // Wait until there's space
                logger.info("TicketPool full. "+ Thread.currentThread().getName()+" waiting for tickets to be sold...");
            }

            Ticket ticket = new Ticket(ticketId, eventName, ticketPrice, vendorId);
            ticket.setVendorId(vendorId);
            ticket.setStatus("available");
            ticket.setCreatedAt(LocalDateTime.now());

            ticketpool.add(ticket);
            ticketRepository.save(ticket);

            Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
            String vendorName = (vendor != null) ? vendor.getName() : "Unknown Vendor";
            logger.info("Vendor " + vendorName + " added Ticket-" + ticketId);
//            System.out.println("Vendor " + vendorName + " added Ticket-" + ticketId);

            logTicketMovement("Ticket-"+ ticketId + " added by "+ vendorName +" for: " + eventName);  // Log the addition

            notEmpty.signalAll(); // Notify customers waiting for tickets
        } finally {
            lock.unlock();
        }
    }

    public Ticket removeTicket(String customerId) throws InterruptedException {
        lock.lock();
        try {
            while (ticketpool.isEmpty() && ticketsSold.get() < totalTickets) {
                logger.info("TicketPool Empty. "+Thread.currentThread().getName()+" waiting for tickets to be added...");
//                System.out.println(Thread.currentThread().getName()+" waiting for tickets to be added...");
                notEmpty.await();// Wait for tickets to be added
            }
            if (ticketpool.isEmpty() && ticketsSold.get() >= totalTickets) {
                logger.info("Tickets sold out!");
                return null; // No more tickets to sell
            }
            Ticket ticket = ticketpool.remove(0);
            ticketsSold.incrementAndGet(); // Increment tickets sold count

            Ticket ticketInDb = ticketRepository.findById(ticket.getTicketId()).orElse(null);
            if (ticketInDb == null) {
//                System.out.println("Ticket not found");
                logger.warn("Ticket not found");
                return null;
            }

            ticketInDb.setStatus("sold");
            ticketInDb.setCustomerId(customerId);
            ticketRepository.save(ticketInDb);

            Customer customer = customerRepository.findById(customerId).orElse(null);
            String customerName = (customer != null) ? customer.getName() : "Unknown Vendor";
            logTicketMovement("Ticket-"+ ticket.getTicketId() + " bought by "+ customerName +" for LKR." + ticket.getTicketPrice());

            notFull.signalAll(); // Notify vendors waiting to add tickets
            return ticket;
        } finally {
            lock.unlock();
        }
    }

    public void logTicketMovement(String logMessage) {
        try {
            logWebSocketHandler.sendLog(logMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int incrementAndGetTicketsAdded() {
        return ticketsAdded.incrementAndGet(); // Increment and return the next ticket ID
    }

    public int getTotalTicketsAdded() {
        return ticketsAdded.get();
    }

    public int getTicketsSize() {
        return ticketpool.size();
    }

    public int getTotalTickets() {
        return totalTickets;
    }
}
