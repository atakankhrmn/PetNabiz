# PET NABIZ – Database Management Systems Project

## Project Description
PET NABIZ is a web-based pet healthcare management system developed for the
CME 3401 – Database Management Systems course.

The system allows pet owners to manage their pets, book veterinary appointments,
and track medical and vaccination records. Veterinarians can manage appointments
and update medical records, while administrators oversee the system.

## Tech Stack

This project uses a monolith architecture with a React frontend serving as a Single Page Application (SPA).

### Backend (Server-Side)
* **Language:** Java 17
* **Framework:** Spring Boot 4.x
* **Security:** Spring Security (Session-based Auth & CORS Configuration)
* **Database:** MySQL
* **ORM:** Spring Data JPA / Hibernate
* **Build Tool:** Maven
* **Utilities:** Lombok, Spring Web (REST API)

### Frontend (Client-Side)
* **Library:** React.js
* **Build Tool:** Vite
* **HTTP Client:** Axios (Custom interceptors for session handling)
* **Routing:** React Router DOM
* **Language:** JavaScript (ES6+)
* **Styling:** CSS3 (Inline & Modules)

### Other Tools
* **Version Control:** Git
* **API Testing:** Postman
* **IDE:** IntelliJ IDEA

## Project Structure
- src/ : Backend source code
- frontend/ : Frontend application
- pom.xml : Maven configuration
- .mvn/, mvnw, mvnw.cmd : Maven Wrapper files

## How to Run the Project

### Backend
1. Make sure Java and Maven are installed.
2. Configure database credentials in application.properties.
3. Run the backend using:
   mvn spring-boot:run

Backend runs on: http://localhost:8080

### Frontend
1. Navigate to the frontend directory:
   cd frontend

2. Install dependencies:
   npm install

3. Start the development server:
   npm run dev

Frontend runs on: http://localhost:5173 (or the port defined by the framework).

Note: Frontend and backend are run separately during development.

## GitHub Repository
https://github.com/serhaydin/PetNabiz

## Authors
- Atakan Kahraman – 2022510066
- Emre Altundal – 2022510092
- Serhat Aydın – 2023510130

## Course Information
CME 3401 – Database Management Systems
Dokuz Eylül University, Computer Engineering Department
