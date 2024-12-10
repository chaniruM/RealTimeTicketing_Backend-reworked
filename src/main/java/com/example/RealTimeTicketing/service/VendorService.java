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
    private static final ReentrantLock lock = new ReentrantLock();

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private String vendorId;
    private int releaseRate;

    public void setVendorDetails(String vendorId, int releaseRate){
        this.vendorId = vendorId;
        this.releaseRate = releaseRate;
    }

   @Override
   public void run() {
       try {
           Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
           String vendorName = (vendor != null) ? vendor.getName() : "Unknown Vendor";


           while (true) {
               lock.lock();
               try {
                   int currentCount = (int) ticketRepository.count();

                   // Stop if the total ticket count reaches or exceeds the limit
                   if (currentCount >= ticketPoolService.getTotalTickets()) {
                       logger.info("Vendor " + vendorName + " finished releasing tickets. Total ticket limit reached.");
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
       }
   }


    public Vendor addVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
}
