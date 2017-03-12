ATM Service
===============

##Summary

This application represents a cash machine which can return the balance or withdraw from a predefined set of accounts. Also, it offers the feature to replenish the device.

####The frameworks and libraries that were used are:
* **Spring Boot** - for fast bootstrapping of the web app
* **JUnit, Mockito** - unit testing
* **Hazelcast** - in-memory caching
* **Apache Commons Collections** - utility libraries
* **Jackson** - serializing
* **Slf4j** - logging


##Approach
The application exposes an API to interact with the ATM device. It allows checking the balance and withdrawing from an account. Also, the replenishment of the device is possible with denominations of £5, £10, £20 and £50. The amount to withdraw must be between £20 and £250 and must be a multiple of 5.

The accounts are loaded automatically at startup and stored in a distributed in-memory Hazelcast cache. When the application starts, the ATM is loaded with an amount of £600 with the following denominations (8 x£5, 7x£10, 7x£20, 7x£50). At withdrawal, the strategy is the following: it disburses the minimum number of notes and gives at least one £5 note if possible.

##Assumptions

* when the application starts the accounts mentioned in the requirement are loaded and stored in the distributed in-memory cache. They are hardcoded in the Hazelcast configuration for simplicity (it could be assumed that this information would be loaded from an external source in a real scenario).
* when the application starts, the notes will be loaded - these are stored separately per each instance of the application instead of the distributed cache because this information represents the state of a single machine and it shouldn’t be shared across all ATMs in the network.
* there is no authentication required. The account information are sent in the request. 
* no funding feature implemented


##Build and start the app
For bootstrapping the app you need Java 8 JDK and Maven 3.
Using Maven, build the application with the following command:
```
mvn clean install
```
and then run the application by typing
```
java -jar target/accountservice-0.0.1-SNAPSHOT.jar
```
Or checking out the code in your IDE and run it selecting
```
RunAs - Spring Boot App
```
##Using the application
APIs:
```
/GET http://localhost:8080/atm/{accountNumber}/balance
```
```
/GET http://localhost:8080/atm/{accountNumber}/withdraw?amount=35
```
```
/PUT http://localhost:8080/atm/replenish

Payload example:
{
  "notes": [
    {
      "denomination": 5,
      "count": 10
    },
    {
      "denomination": 20,
      "count": 10
    }
  ]
}
```