package com.example.RealTimeTicketing.service;

import com.example.RealTimeTicketing.model.Ticket;
import com.example.RealTimeTicketing.model.TicketEntity;
import com.example.RealTimeTicketing.model.Vendor;
import com.example.RealTimeTicketing.repository.TicketRepository;
import com.example.RealTimeTicketing.repository.VendorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Scope("prototype")
public class VendorService implements Runnable{
    private static final Logger logger = LogManager.getLogger(VendorService.class);

    @Autowired
    private TicketPoolService ticketPoolService;

    @Autowired
    private ThreadConfigurationService threadConfigurationService;

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
       try {
           Vendor vendor = vendorRepository.findById(vendorId).orElse(null);
           String vendorName = (vendor != null) ? vendor.getName() : "Unknown Vendor";

           while (true) {
               if (threadConfigurationService.isStopSimulation()) {
                   System.out.println("Vendor " + vendorName + " stopping as simulation is stopped.");
                   break;
               }
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
           System.out.println("Vendor " + vendorId + " was interrupted.");
       }
   }


    public Vendor addVendor(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
}
