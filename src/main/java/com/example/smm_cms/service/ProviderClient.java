package com.example.smm_cms.service;

import com.example.smm_cms.dto.response.order.ProviderCreateOrderResponse;
import com.example.smm_cms.dto.response.order.ProviderStatusResponse;
import com.example.smm_cms.dto.response.provider.ProviderServiceResponse;

import java.util.List;

public interface ProviderClient {
    String testConnection(String apiUrl, String apiKey);

    List<ProviderServiceResponse> getServices(String apiUrl, String apiKey);

    String getBalance(String apiUrl, String apiKey);

    ProviderCreateOrderResponse createOrder(String apiUrl, String apiKey, Long serviceId, String target, Integer quantity);

    ProviderStatusResponse getStatus(String apiUrl, String apiKey, String orderId);
}
