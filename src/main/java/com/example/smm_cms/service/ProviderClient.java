package com.example.smm_cms.service;

import com.example.smm_cms.dto.response.provider.ProviderServiceResponse;

import java.util.List;

public interface ProviderClient {
    String testConnection(String apiUrl, String apiKey);

    List<ProviderServiceResponse> getServices(String apiUrl, String apiKey);

    String getBalance(String apiUrl, String apiKey);

}
