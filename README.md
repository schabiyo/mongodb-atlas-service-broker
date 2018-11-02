# A Cloud Foundry Service broker for MongoDB Atlas
Base application to demonstrate MongoDB Atlas Service Broker


## Introduction
This base application is intended to demonstrate a service broker implemenation for MongoDB Atlas. Itr allows to easily provision clusters in MongoDB Atlas from PCF marketplace. 

![](imgs/broker.png)


Please note that the service broker is currently in public beta. Please do not use it in production yet. Your feedback is welcome! 


## Getting Started

**Prerequisites**
- Cloud Foundry CLI
- Git Client
- Java SE Development Kit
- Maven

**Configuring the catalog**

One of the main thing you migh want to configure here is the MongoDB Atlas tiers you want the service broker to expose. Two file need to be updated for that.


![](imgs/ressource.png)


1. Define the service catalog

This is done within the application.yml, using the spring.openservicebroker.catalog property. In the example below, only one service with 4 differents plans will be advertised. In this context each plan will translate to a Tier in MongoDB Atlas.

```

catalog:
        services:
        - id: Atlas_AWS
          name: mongodb-atlas-aws
          description: MongoDB Atlas Service on AWS
          bindable: true
          tags:
          - MongoDB
          - Atlas
          - AWS
          plans:
          - id: aws-dev
            name: aws-dev
            bindable: true
            free: true
            description: Please use this for Dev (This is a multinenant environment)
          - id: aws-qa
            name: aws-qa
            bindable: true
            description: Please use this for Qa
          - id: aws-prod
            name: aws-prod
            bindable: true
            description: Please use this for any Production deploiement
          - id: aws-global_cluster
            name: aws-global_cluster
            bindable: true
            description: Please use this for any Production deploiement that requires global cluster. It includes 2 zones in US_EAST and US_CENTRAL


```

2. Provide provisioning details for each plan

In this section, you will have to provide the REST API JSON message that will be used to provision the environment on Atlas. Each plan will be associated with a JSON file that is named after the plan ID, this is very important, that how the service broker knows with JSON message to send over.



**Building**
```
$ git clone [REPO]
$ cd [REPO]
$ mvn clean install
``` 

### Running on Cloud Foundry
The application is set to use an embedded H2 database and to take advantage of Pivotal CF's auto-configuration for services. To use a MySQL Dev service in PCF, simply create and bind a service to the app and restart the app. No additional configuration is necessary.

Before deploying thge application, take a look at the manifest file for the recommended setting. Adjust them as per your environment.

After connection to your PCF environment.
```
$ cd [REPO]
$ cf push
```



