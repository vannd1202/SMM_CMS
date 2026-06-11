package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.OrderStatus;
import com.example.smm_cms.dto.response.provider.ProviderBalanceResponse;
import com.example.smm_cms.dto.response.provider.ProviderHealthCheckResponse;
import com.example.smm_cms.dto.response.provider.ProviderStatisticResponse;
import com.example.smm_cms.entity.ProviderEntity;
import com.example.smm_cms.repository.OrderRepository;
import com.example.smm_cms.repository.ProviderRepository;
import com.example.smm_cms.repository.ServiceRepository;
import com.example.smm_cms.service.IProviderMonitorService;
import com.example.smm_cms.service.ProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderMonitorServiceImpl extends BaseService implements IProviderMonitorService {
    private final ProviderRepository providerRepository;
    private final ProviderClient providerClient;
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public ResponseData<?> statistic() {

        ResponseData<List<ProviderStatisticResponse>> responseData = new ResponseData<>();

        try {

            List<ProviderStatisticResponse> result = new ArrayList<>();

            List<ProviderEntity> providers =
                    providerRepository.findAll();

            for (ProviderEntity provider
                    : providers) {

                BigDecimal balance =
                        BigDecimal.ZERO;

                try {

                    ProviderBalanceResponse balanceResponse =
                            providerClient.getBalance(
                                    provider.getApiUrl(),
                                    provider.getApiKey());

                    if (balanceResponse == null) {

                        throw new BaseException(
                                500,
                                "Provider không phản hồi");
                    }

                    if (balanceResponse.getError() != null) {

                        throw new BaseException(
                                400,
                                balanceResponse.getError());
                    }

                    if (balanceResponse.getBalance() != null) {

                        balance =
                                balanceResponse.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP);
                    }

                } catch (BaseException be) {
                    responseData.setCode(be.getCode());
                    responseData.setMessage(be.getMessage());
                    return responseData;
                }

                result.add(
                        ProviderStatisticResponse.builder()
                                .providerId(
                                        provider.getId())
                                .providerName(
                                        provider.getName())
                                .apiUrl(
                                        provider.getApiUrl())
                                .balance(
                                        balance)
                                .totalServices(
                                        serviceRepository
                                                .countByProviderId(
                                                        provider.getId()))
                                .totalOrders(
                                        orderRepository
                                                .countByMappingProviderServiceProviderId(
                                                        provider.getId()))
                                .failedOrders(
                                        orderRepository
                                                .countByMappingProviderServiceProviderIdAndStatus(
                                                        provider.getId(),
                                                        OrderStatus.FAILED))
                                .lastSyncTime(
                                        provider.getUpdatedDate())
                                .active(
                                        provider.getStatus())
                                .build());
            }
            responseData.setData(result);

        } catch (Exception e) {

            responseData.setCode(500);
            responseData.setMessage(
                    "Internal Server Error");
        }

        return responseData;
    }

    @Override
    public ResponseData<?> healthCheck(Long providerId) {

        ResponseData<ProviderHealthCheckResponse>
                responseData = new ResponseData<>();

        try {

            ProviderEntity provider =
                    providerRepository.findById(
                                    providerId)
                            .orElseThrow(() ->
                                    new BaseException(
                                            404,
                                            "Provider không tồn tại"));

            long start =
                    System.currentTimeMillis();

            ProviderBalanceResponse balance =
                    providerClient.getBalance(
                            provider.getApiUrl(),
                            provider.getApiKey());

            long end =
                    System.currentTimeMillis();

            responseData.setData(
                    ProviderHealthCheckResponse.builder()
                            .providerId(
                                    provider.getId())
                            .providerName(
                                    provider.getName())
                            .status("UP")
                            .balance(
                                    balance.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP))
                            .responseTimeMs(
                                    end - start)
                            .build());

        } catch (Exception e) {

            responseData.setData(
                    ProviderHealthCheckResponse.builder()
                            .providerId(
                                    providerId)
                            .status("DOWN")
                            .build());
        }

        return responseData;
    }
}
