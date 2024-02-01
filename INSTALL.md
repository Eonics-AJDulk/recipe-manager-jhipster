# Installation Guide

## Prerequisites

Ensure that you have the following prerequisites installed on your system:

1. **Java Development Kit (JDK) 17:**

   - Install [Java 17](https://adoptium.net/releases.html?variant=openjdk17) on your machine.

2. **Node.js and npm:**

   - Install [Node.js and npm](https://nodejs.org/) for managing frontend dependencies.

3. **Docker:**
   - Install [Docker](https://www.docker.com/) if you plan to deploy the application using Docker containers.

## Project Setup

1. **Clone the Repository:**

   - Clone the project repository from the GitHub URL.

2. **Navigate to Project Directory:**

   - Open a terminal and move to the project directory:
     ```bash
     cd your-project-directory
     ```

3. **Generate and Configure the JHipster Application:**

   - Run the JHipster generator to initialize the application:
     ```bash
     ./mvnw
     ```

4. **Install Dependencies:**
   - Install backend and frontend dependencies:
     ```bash
     npm install
     ```

## Running the Application

1. **Create the Docker images:**

   - Run the following command to start the backend server:
     ```bash
     npm run java:docker
     ```

2. **Start the application:**

   - Open a new terminal window and run the following command to start the frontend:
     ```bash
     docker compose -f src/main/docker/app.yml up -d
     ```

   **Note:**

   - Older versions of Docker Compose use the `docker-compose` command instead of `docker compose`.
   - If you wish to see the logging output of the application, remove the `-d` flag from the command.

3. **Access the Application:**
   - Open your web browser and navigate to [http://localhost:8080](http://localhost:8080) to access the JHipster application.
   - You can log in to the application using the default credentials:
     - Username: `admin`
     - Password: `admin`
   - You can also access the Swagger UI at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) to view the API documentation.
