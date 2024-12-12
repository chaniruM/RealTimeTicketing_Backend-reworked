# Real-Time Ticketing System - Backend

This backend is built using **Spring Boot** to manage the ticketing simulation, showcasing concepts like multithreading, concurrency, and real-time event handling. It provides APIs for managing tickets, customers, vendors, and configurations.

## Features
- **Multithreading & Concurrency:**
    - Simulates vendor and customer interactions using threads.
    - Ensures thread-safe operations with locks and conditions.
- **Real-Time Data Updates:**
    - Implements WebSocket for real-time ticket status logs.
- **RESTful API:**
    - CRUD operations for vendors, customers, and configurations.
- **Data Persistence:**
    - Uses MongoDB to store tickets, customers, and vendors.
    - Uses JSON to store configuration setting for reuse.
- **Logging:**
    - Employs Log4j2 for detailed log tracking.

## File Structure

| File/Directory                        | Description                                                                                        |
|---------------------------------------|----------------------------------------------------------------------------------------------------|
| **model/**                            | Contains data models for Ticket, Vendor, Customer, and Configuration.                              |
| **service/**                          | Business logic for ticket pool, configuration, and simulation management.                          |
| **controller/**                       | REST API controllers for configurations, threads, and CRUD operations.                             |
| **repository/**                       | Interfaces for MongoDB interaction.                                                                |
| **WebSocket/**                        | Real-time WebSocket handler for log broadcasting.                                                  |
| **resources/application.properties**  | Configuration for MongoDB and application setup.                                                   |
| **resources/log4j2.xml**              | Log4j2 configuration for logging setup.                                                            |
| **pom.xml**                           | Maven dependencies and build configuration.                                                        |
| **DatabaseInitializer.java**          | Implements commanlinerunner interface to clear the database once the while booting up the backend. |
| **WebConfig.java**                    | Enables Cross-Origin Resource Sharing (CORS) for all endpoints in the application.                 |
| **RealTimeTicketingApplication.java** | The entry point for the application                                                                |

## Prerequisites

    1. Java (JDK 21 or later)
    2. Maven for dependency management.
    3. MongoDB installed and running locally.

## Setup Instructions
### Running the Backend (Spring Boot) in IntelliJ IDEA
1.	**Import the Project:**
- Open IntelliJ IDEA.
- Click on File > Open.
- Select the backend project folder (e.g., RealTimeTicketing-Backend).
2.	**Maven Configuration:**
- If Maven is not automatically configured:
- Go to File > Project Structure > Modules > Dependencies.
- Ensure the Maven settings are applied.
- Check the pom.xml file for dependencies. IntelliJ should automatically download them.
3.	**Configure MongoDB:**
- Ensure MongoDB is running on your system (e.g., localhost:27017).
- Update the database configuration in application.properties if necessary.
4.	**Run the Backend:**
- Locate the RealTimeTicketingApplication.java file in the src/main/java directory.
- Right-click on the file and select Run ‘RealTimeTicketingApplication.main()’.
- IntelliJ will compile the project and start the Spring Boot server.
5.	**Verify:**
- Open a browser and navigate to http://localhost:8083/api. You should see the API endpoints.


## Usage
1.	Access APIs at http://localhost:8083/api.
2.	Endpoints include:
- /api/configuration - Manage configurations.
- /api/customers - Manage customers.
- /api/vendors - Manage vendors.
- /api/threads/start - Start the simulation.
- /api/threads/stop - Stop the simulation.
- /api/ticket-pool/status - Get ticket pool status.


## Troubleshooting
- **MongoDB not connecting:** Ensure MongoDB is running and configured correctly in application.properties.
- **Port conflicts:** Modify server.port in application.properties if another application uses port 8083.
