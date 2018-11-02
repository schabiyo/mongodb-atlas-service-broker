# A Cloud Foundry Service broker for MongoDB Atlas
Base application to demonstrate MongoDB Atlas Service Broker


## Introduction
This base application is intended to demonstrate a service broker implemenation for MongoDB Atlas. Itr allows to easily provision clusters in MongoDB Atlas from PCF marketplace. 

Please note that the service broker is currently in public beta. Please do not use it in production yet. Your feedback is welcome! 


## Getting Started

**Prerequisites**
- Cloud Foundry CLI
- Git Client
- Java SE Development Kit
- Maven

**Configuring the catalog**


**Building**
```
$ git clone [REPO]
$ cd [REPO]
$ mvn clean install
``` 

### Running on Cloud Foundry
The application is set to use an embedded H2 database and to take advantage of Pivotal CF's auto-configuration for services. To use a MySQL Dev service in PCF, simply create and bind a service to the app and restart the app. No additional configuration is necessary when running locally or in Pivotal CF.

Before deploying thge application, take a look at the manifest file for the recommended setting. Adjust them as per your environment.

After connection to your PCF environment.
```
$ cd [REPO]
$ cf push
```



