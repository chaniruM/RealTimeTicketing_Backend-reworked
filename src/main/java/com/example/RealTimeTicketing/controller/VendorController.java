package com.example.RealTimeTicketing.controller;

import com.example.RealTimeTicketing.model.Vendor;
import com.example.RealTimeTicketing.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing vendor operations.
 * Provides endpoints for creating and retrieving vendors.
 */
@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    /**
     * Creates a new vendor.
     *
     * @param vendor The vendor object to be created.
     * @return The created vendor object.
     */
    @PostMapping
    public ResponseEntity<Vendor> addVendor(@RequestBody Vendor vendor) {
        Vendor savedVendor = vendorService.addVendor(vendor);
        return ResponseEntity.ok(savedVendor);
    }

    /**
     * Retrieves a list of all vendors.
     *
     * @return A list of all vendors.
     */
    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        List<Vendor> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

}
