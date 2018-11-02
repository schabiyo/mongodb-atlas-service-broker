package com.syolab.broker.atlasbroker.config;

import org.springframework.cloud.servicebroker.model.catalog.Catalog;
import org.springframework.cloud.servicebroker.model.catalog.Plan;
import org.springframework.cloud.servicebroker.model.catalog.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class AtlasCatalogConfiguration {

    //@Bean
    public Catalog catalog() {
        Plan plan = Plan.builder()
                .id("simple-plan")
                .name("standard")
                .description("A simple plan")
                .free(true)
                .build();

        ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                .id("example-service")
                .name("example")
                .description("A simple example")
                .bindable(true)
                .tags("example", "tags")
                .plans(plan)
                .build();

        return Catalog.builder()
                .serviceDefinitions(serviceDefinition)
                .build();
    }
}
