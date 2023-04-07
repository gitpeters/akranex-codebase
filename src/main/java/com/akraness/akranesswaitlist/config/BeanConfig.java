package com.akraness.akranesswaitlist.config;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Value("${azure.blob.connection-string}")
    String connectionString;
    @Value("${azure.blob.container-name}")
    String containerName;
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BlobClientBuilder getClient() {
        BlobClientBuilder client = new BlobClientBuilder();
        client.connectionString(connectionString);
        client.containerName(containerName);
        return client;
    }

    @Bean
    public BlobContainerClient containerClient() {
        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
        containerClient.createIfNotExists();
        return containerClient;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}


