package com.example.smm_cms.service;

public interface ProviderClient {
    String testConnection(String apiUrl, String apiKey);

    String getServices(String apiUrl, String apiKey);

    String getBalance(String apiUrl, String apiKey);

}
