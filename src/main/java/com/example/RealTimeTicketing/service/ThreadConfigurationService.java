package com.example.RealTimeTicketing.service;

import org.springframework.stereotype.Service;

@Service
public class ThreadConfigurationService {
    private int numberOfVendors;
    private int numberOfCustomers;
    private volatile boolean stopSimulation = false; // Shared flag to stop threads

    public void setNumberOfVendors(int numberOfVendors) {
        this.numberOfVendors = numberOfVendors;
    }

    public int getNumberOfVendors() {
        return numberOfVendors;
    }

    public void setNumberOfCustomers(int numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }

    public int getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public boolean isStopSimulation() {
        return stopSimulation;
    }

    public void setStopSimulation(boolean stopSimulation) {
        this.stopSimulation = stopSimulation;
    }
}
