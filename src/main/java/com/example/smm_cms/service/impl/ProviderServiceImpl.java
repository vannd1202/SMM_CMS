package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.base.ResponsePage;
import com.example.smm_cms.common.ProviderStatus;
import com.example.smm_cms.dto.request.provider.ProviderResponse;
import com.example.smm_cms.dto.request.provider.SearchProviderRequest;
import com.example.smm_cms.dto.response.provider.CreateProviderRequest;
import com.example.smm_cms.dto.response.provider.UpdateProviderRequest;
import com.example.smm_cms.entity.ProviderEntity;
import com.example.smm_cms.repository.ProviderRepository;
import com.example.smm_cms.service.IProviderService;
import com.example.smm_cms.service.ProviderClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderServiceImpl extends BaseService implements IProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderClient providerClient;

    @Override
    public ResponseData<?> create(
            CreateProviderRequest request) {
        String logPrefix = "Tạo mới nhà cung cấp";
        ResponseData<?> responseData = new ResponseData<>();
        try {
            ProviderEntity provider = ProviderEntity.builder()
                    .name(request.getName())
                    .apiUrl(request.getApiUrl())
                    .apiKey(request.getApiKey())
                    .status(ProviderStatus.ACTIVE)
                    .build();
            providerRepository.save(provider);
        } catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Tạo mới thất bai");
        }

        return responseData;
    }

    @Override
    public ResponseData<?> update(Long id, UpdateProviderRequest request) {
            String logPrefix = "Cập nhật nhà cung cấp";
            ResponseData<?> responseData = new ResponseData<>();
            try {
                ProviderEntity provider = providerRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Nhà cung cấp không tồn tại"));

                provider.setName(request.getName());
                provider.setApiUrl(request.getApiUrl());
                provider.setApiKey(request.getApiKey());
                provider.setStatus(request.getStatus());
                providerRepository.save(provider);
            } catch (Exception e) {
                LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
                responseData.setCode(-1);
                responseData.setMessage("Cập nhật thất bai");
            }
        return responseData;
    }

    @Override
    public ResponseData<?> getAll(SearchProviderRequest request) {
        String logPrefix = "Lấy danh sách nhà cung cấp";
        ResponseData<ResponsePage<ProviderResponse>> responseData = new ResponseData<>();
        try {
            Page<ProviderEntity> page;
            if (request.getName() != null) {
                page = providerRepository.findByNameContainingIgnoreCase(
                        request.getName(),
                        request.pageable());
            }else {
                page = providerRepository.findAll(request.pageable());
            }
            List<ProviderResponse> content = page.getContent()
                    .stream()
                    .map(this::toResponse)
                    .toList();
            responseData.success(new ResponsePage<>(page, content));
        } catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Lấy danh sách thất bai");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> getById(Long id) {
            String logPrefix = "Lấy thông tin nhà cung cấp";
            ResponseData<ProviderResponse> responseData = new ResponseData<>();
            try {
                ProviderEntity provider = providerRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Nhà cung cấp không tồn tại"));
                responseData.success(toResponse(provider));
            } catch (Exception e) {
                LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
                responseData.setCode(-1);
                responseData.setMessage("Lấy thông tin thất bai");
            }
            return responseData;
    }

    @Override
    public ResponseData<?> testConnection(Long id) {
        ResponseData<?> responseData = new ResponseData<>();
        ProviderEntity provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        String result = providerClient.testConnection(
                provider.getApiUrl(),
                provider.getApiKey()
        );
        responseData.setMessage(result);
        return responseData;
    }

    private ProviderResponse toResponse(
            ProviderEntity provider) {

        return ProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .apiUrl(provider.getApiUrl())
                .status(provider.getStatus())
                .createdAt(provider.getCreatedDate())
                .build();
    }
}
