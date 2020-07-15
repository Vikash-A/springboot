package com.example.fa.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
@Data
@ConfigurationProperties(prefix = "com.example.fa.supplier")
public class SupplierConfiguration {

    private Map<String, String> suppliersMap;

    private String baseUrl;



}