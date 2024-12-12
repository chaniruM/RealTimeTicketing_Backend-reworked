package com.example.RealTimeTicketing.service;

import com.example.RealTimeTicketing.model.Vendor;
import com.example.RealTimeTicketing.repository.TicketRepository;
import com.example.RealTimeTicketing.repository.VendorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class responsible for simulating vendor behavior in the real-time ticketing system.
 * It creates a separate thread to continuously attempt adding tickets to the pool at a defined release rate
 * on behalf of a specific vendor.
 */
@Service
public class VendorService implements Runnable{
    private static final Logger logger = LogManager.getLogger(VendorService.class);

    //Reentrant lock to ensure thread-safe access to the ticket adding logic.
    private static final ReentrantLock lock = new ReentrantLock();

    //for interacting with the TicketPool to add tickets.
    @Autowired
    private TicketPoolService ticketPoolService;

    //for interacting with the Vendor database table.
    @Autowired
    private VendorRepository vendorRepository;

    //for interacting with the Ticket database table.
    @Autowired
    private TicketRepository ticketRepository;

    //Unique identifier of the vendor this service represents.
    private String vendorId;

    //Release rate (in seconds) at which the vendor attempts to add tickets to the pool.
    private int releaseRate;

    /**
     * Sets the vendor details (ID and release rate) for this service instance.
     *
     * @param vendorId Unique identifier of the vendor.
     * @param releaseRate Release rate (in seconds) at which the vendor releases tickets.
     */
    public void setVendorDetails(String vendorId, int releaseRate){
        this.vendorId = vendorId;
        this.releaseRate = releaseRate;
    }

    /**
     * Sets the dependencies (injected services) for this service instance.
     *
     * @param ticketPoolService Service instance for interacting with the TicketPool.
     * @param vendorRepository Repository instance for interacting with the Vendor table.
     * @param ticketRepository Repository instance for interacting with the Ticket table.
     */
    public void setDependencies(TicketPoolService ticketPoolService, VendorRepository vendorRepository, TicketRepository ticketRepository) {
        this.ticketPoolService = ticketPoolService;
        this.vendorRepository = vendorRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * The main thread run method that simulates vendor behavior.
     * It continuously attempts to add tickets to the pool for the assigned vendor at the release rate
     * until the total ticket limit is reached.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                lock.lock();
                try {
                    int currentCount = (int) ticketRepository.count();

                    // Stop if the total ticket count reaches or exceeds the limit
                    if (currentCount >= ticketPoolService.getTotalTickets()) {
                        logger.info("Vendor " + Thread.currentThread().getName() + " finished releasing tickets. Total ticket limit reached.");
                        break;
                    }

                    // Add ticket
                    ticketPoolService.addTicket("SL vs Eng", 1000, vendorId);
                }finally {
                    lock.unlock();
                }
                // Simulate delay before adding the next ticket
                Thread.sleep(releaseRate * 1000);

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn(Thread.currentThread().getName() + " was interrupted.");
        } catch (UncategorizedMongoDbException e){
            System.out.println(Thread.currentThread().getName() + " was terminated while accessing database.");
        } finally {
            logger.info(Thread.currentThread().getName() + " is stopping gracefully.");
        }
    }

    /**
     * Saves a new vendor to the database.
     *
     * @param vendor The Vendor object to be saved.
     * @return The saved Vendor object.
     */
    public Vendor addVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    /**
     * Retrieves a list of all vendors from the database.
     *
     * @return A list of all Vendor objects.
     */
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
}
