
# Credit Module API

## Project Overview
This project is a backend Loan API for managing loans in a bank. Employees can create, list, and manage loan payments for customers.

## Requirements
- **Java 17**
- **Spring Boot 3.4.0**
- **H2 Database**
- **Lombok(Optional)**

## Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/ilkmurat/credit-module
   ```
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Create data ;(Initial data was created from data.sql file.)
   - curl -u admin:admin -X POST http://localhost:8085/api/loans \      
     -H "Content-Type: application/json" \
     -d '{
     "customerId": 1,
     "amount": 10000.0,
     "interestRate": 0.2,
     "numberOfInstallments": 12
     }'
   -  curl -u admin:admin -X POST http://localhost:8085/api/loans/1/pay \
      -H "Content-Type: application/json" \
      -d '{
      "amount": 1000.0
      }'

5. Access the H2 Console:
   - URL: `http://localhost:8085/h2-console`
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (leave blank)


## API Documentation
### Endpoints
1. **Create Loan**:
    - `POST /api/loans`
    - Request:
      ```json
      {
        "customerId": 1,
        "amount": 10000,
        "interestRate": 0.1,
        "numberOfInstallments": 12
      }
      ```
    - Response:
      ```json
      {
        "id": 1,
        "loanAmount": 11000,
        "numberOfInstallments": 12,
        "isPaid": false
      }
      ```

2. **List Loans**:
    - `GET /api/loans/customer/{customerId}`

3. **Pay Loan**:
    - `POST /api/loans/{loanId}/pay`
    - Request:
      ```json
      {
        "amount": 2000
      }
      ```

4. **List Installments**:
    - `GET /api/installments/{loanId}`

## You can use this postman collection ;  credit-module/LoanAPICollection.json


## Known Issues
- Lombok runtime issues resolved by switching to manual encapsulation. You can resolve this issue like this;
  Set annotation processing on: IntelliJ Settings(Preferences) -> Compiler -> Annotation
  Processors
  Change from ajc to javac for the project: Compiler -> Java Compiler
  you of course also need the Lombok plugin.
- I used it manually to make it work in different ide or java versions.
- Database visibility is restricted to the H2 Console for in-memory mode for 3rd party db editor. You should use h2 console.

## Future Improvements
- I managed the authorization from the SecurityFilter file. But As a best practice, it is better to manage dynamically.
- Benefits of this Approach
  Centralized Role Management: All roles are stored and managed in a central database.
  Dynamic Updates: Changes in roles do not require code changes; updating the database is enough.
  Scalability: Roles can be assigned and updated at runtime without restarting the application.
  This approach ensures dynamic and flexible role management, ideal for applications where roles are frequently updated or vary per user.

## Author
- Name: Murat ilk
- Contact: ilkmuratt@gmail.com


