package com.example.RealTimeTicketing.service;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import com.example.RealTimeTicketing.model.Configuration;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Service class responsible for managing the configuration settings.
 * It provides methods to load, update, save, and retrieve the configuration object.
 */
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

    /**
     * Saves the current in-memory configuration to a JSON file named "configuration.json"
     * located in the "src/main/resources" directory.
     *
     * @param configuration The Configuration object to be saved.
     * @throws RuntimeException If an IOException occurs during file writing.
     */
    public void saveConfiguration(Configuration configuration) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("src/main/resources/configuration.json")) {
            gson.toJson(configuration, writer);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the application configuration from a JSON file named "configuration.json"
     * located in the "src/main/resources" directory. If the file doesn't exist or an error occurs,
     * it logs the error and continues with the default configuration.
     */
    public void loadConfiguration() {
        Gson gson = new Gson();
        try (FileReader fileReader = new FileReader("src/main/resources/configuration.json")){
            this.configuration = gson.fromJson(fileReader, Configuration.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("Configuration file not found. Change path to absolute path!");
        }
    }
}
