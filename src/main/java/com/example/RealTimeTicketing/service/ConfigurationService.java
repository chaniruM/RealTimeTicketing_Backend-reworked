package com.example.RealTimeTicketing.service;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import com.example.RealTimeTicketing.model.Configuration;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class ConfigurationService {
    private static final Logger logger = LogManager.getLogger(CustomerService.class);

    private Configuration configuration;

    public ConfigurationService() {
        this.configuration = new Configuration(0, 0, 0, 0);
    }

    public void updateConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void saveConfiguration(Configuration configuration) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("src/main/resources/configuration.json")) {
            gson.toJson(configuration, writer);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void loadConfiguration() {
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader("src/main/resources/configuration.json")){
            this.configuration = gson.fromJson(fileReader, Configuration.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
