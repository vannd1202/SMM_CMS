package com.example.smm_cms.service.impl;

import com.example.smm_cms.dto.response.order.ProviderCreateOrderResponse;
import com.example.smm_cms.dto.response.order.ProviderStatusResponse;
import com.example.smm_cms.dto.response.provider.ProviderBalanceResponse;
import com.example.smm_cms.dto.response.provider.ProviderServiceResponse;
import com.example.smm_cms.service.ProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SmmProviderClient implements ProviderClient {
    private final WebClient webClient;

    @Override
    public String testConnection(String apiUrl, String apiKey) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("action", "balance");

        return webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public List<ProviderServiceResponse> getServices(String apiUrl, String apiKey) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("action", "services");

        return webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(ProviderServiceResponse.class)
                .collectList()
                .block();
    }

    @Override
    public ProviderBalanceResponse getBalance(String apiUrl, String apiKey) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("action", "balance");
        ProviderBalanceResponse data = webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(ProviderBalanceResponse.class)
                .block();
        return data;
    }

    @Override
    public ProviderCreateOrderResponse createOrder(String apiUrl, String apiKey, Long serviceId, String target, Integer quantity) {
        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("key", apiKey);
        body.add("action", "add");
        body.add("service", String.valueOf(serviceId));
        body.add("link", target);
        body.add("quantity", String.valueOf(quantity));

        return webClient.post()
                .uri(apiUrl)
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(
                        ProviderCreateOrderResponse.class)
                .block();
    }

    @Override
    public ProviderStatusResponse getStatus(String apiUrl, String apiKey, String orderId) {
        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add("key", apiKey);
        body.add("action", "status");
        body.add("order", orderId);

        return webClient.post()
                .uri(apiUrl)
                .contentType(
                        MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(
                        ProviderStatusResponse.class)
                .block();
    }


}
