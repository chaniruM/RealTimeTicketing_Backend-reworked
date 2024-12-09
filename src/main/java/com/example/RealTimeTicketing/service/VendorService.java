package com.example.RealTimeTicketing.service;

import com.example.RealTimeTicketing.model.Vendor;
import com.example.RealTimeTicketing.repository.TicketRepository;
import com.example.RealTimeTicketing.repository.VendorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Scope("prototype")
public class VendorService implements Runnable{
    private static final Logger logger = LogManager.getLogger(VendorService.class);
    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VendorRepository vendorRepository;

    private String vendorId;
    private int releaseRate;

    public void setVendorDetails(String vendorId, int releaseRate){
        this.vendorId = vendorId;
        this.releaseRate = releaseRate;
    }

   @Override
   public void run() {
        lock.lock();
       try {
           Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
           String vendorName = (vendor != null) ? vendor.getName() : "Unknown Vendor";

           while (true) {
               if (!ticketPoolService.canAddMoreTickets()) {
                   System.out.println("Vendor " + vendorName + " finished releasing tickets.");
                   break;
               }

               int ticketID = ticketPoolService.incrementAndGetTicketsAdded();

               ticketPoolService.addTicket(ticketID, "SL vs Eng", 1000, vendorId);
               Thread.sleep(releaseRate * 1000); // Simulate delay
           }
       } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
           System.out.println(Thread.currentThread().getName() + " was interrupted.");
           logger.warn(Thread.currentThread().getName() + " was interrupted.");
       } finally {
           lock.unlock();
       }
   }


    public Vendor addVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
}
