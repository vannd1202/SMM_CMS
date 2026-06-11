package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.base.ResponsePage;
import com.example.smm_cms.common.ProviderStatus;
import com.example.smm_cms.dto.request.provider.ProviderResponse;
import com.example.smm_cms.dto.request.provider.SearchProviderRequest;
import com.example.smm_cms.dto.response.order.ProviderStatusResponse;
import com.example.smm_cms.dto.response.provider.CreateProviderRequest;
import com.example.smm_cms.dto.response.provider.ProviderBalanceResponse;
import com.example.smm_cms.dto.response.provider.ProviderServiceResponse;
import com.example.smm_cms.dto.response.provider.UpdateProviderRequest;
import com.example.smm_cms.dto.response.service.ServiceResponse;
import com.example.smm_cms.entity.OrderEntity;
import com.example.smm_cms.entity.ProviderEntity;
import com.example.smm_cms.entity.ServiceEntity;
import com.example.smm_cms.repository.OrderRepository;
import com.example.smm_cms.repository.ProviderRepository;
import com.example.smm_cms.repository.ServiceRepository;
import com.example.smm_cms.service.IProviderService;
import com.example.smm_cms.service.ProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderServiceImpl extends BaseService implements IProviderService {
    private final ProviderRepository providerRepository;
    private final ProviderClient providerClient;
    private final ServiceRepository serviceRepository;
    private final OrderRepository orderRepository;

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
        }catch (BaseException e) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
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
            } else {
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
        }catch (BaseException be){
            LOGGER.error("Lỗi----{}-----{}", logPrefix, be.getMessage());
            responseData.setCode(be.getCode());
            responseData.setMessage(be.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Lấy thông tin thất bai");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> testConnection(Long id) {
        ResponseData<?> responseData = new ResponseData<>();
        try {
            ProviderEntity provider = providerRepository.findById(id)
                    .orElseThrow(() -> new BaseException(400,"Provider not found"));

            String result = providerClient.testConnection(
                    provider.getApiUrl(),
                    provider.getApiKey()
            );
            responseData.setMessage(result);
        }catch (BaseException e) {
            LOGGER.error("Lỗi----{}-----{}", "Kiểm tra kết nối nhà cung cấp", e.getMessage());
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", "Kiểm tra kết nối nhà cung cấp", e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Kết nối thất bai");
        }

        return responseData;
    }

    @Override
    public ResponseData<List<ProviderServiceResponse>> getServices(Long id) {
        ResponseData<List<ProviderServiceResponse>> responseData = new ResponseData<>();
        try {
            ProviderEntity provider = providerRepository.findById(id)
                    .orElseThrow(() ->
                            new BaseException(1001, "Provider không tồn tại"));
            List<ProviderServiceResponse> data = providerClient.getServices(
                    provider.getApiUrl(),
                    provider.getApiKey()
            );
            responseData.setData(data);

        }catch (BaseException e) {
            LOGGER.error("Lỗi----{}-----{}", "Lấy dịch vụ từ nhà cung cấp", e.getMessage());
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", "Lấy dịch vụ từ nhà cung cấp", e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Lấy dịch vụ thất bai");
        }

        return responseData;
    }

    @Override
    public ResponseData<?> getBalance(Long id) {
        ResponseData<ProviderBalanceResponse> responseData = new ResponseData<>();
        try {
            ProviderEntity provider = providerRepository.findById(id)
                    .orElseThrow(() ->
                            new BaseException(1001, "Provider không tồn tại"));

            ProviderBalanceResponse data = providerClient.getBalance(
                    provider.getApiUrl(),
                    provider.getApiKey()
            );
            data.setBalance(data.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP));
            responseData.setData(data);

        }catch (BaseException e) {
            LOGGER.error("Lỗi----{}-----{}", "Lấy số dư từ nhà cung cấp", e.getMessage());
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", "Lấy số dư từ nhà cung cấp", e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Lấy số dư thất bai");
        }

        return responseData;
    }

    @Transactional
    @Override
    public ResponseData<?> syncServices(Long providerId) {

        String logPrefix = "Đồng bộ dịch vụ từ provider";
        ResponseData<String> responseData = new ResponseData<>();
        try {
        ProviderEntity provider = providerRepository
                .findById(providerId)
                .orElseThrow(() ->
                        new BaseException(
                                1001,
                                "Provider không tồn tại"));



            List<ProviderServiceResponse> services =
                    providerClient.getServices(
                            provider.getApiUrl(),
                            provider.getApiKey());


            int created = 0;
            int updated = 0;

            for (ProviderServiceResponse item : services) {

                Optional<ServiceEntity> optional =
                        serviceRepository
                                .findByProviderIdAndProviderServiceId(
                                        providerId,
                                        item.getService());

                if (optional.isPresent()) {

                    ServiceEntity entity =
                            optional.get();

                    updateService(entity, item);

                    updated++;

                } else {

                    ServiceEntity entity =
                            buildService(provider, item);

                    serviceRepository.save(entity);

                    created++;
                }
            }

            Map<String, Object> result =
                    new HashMap<>();

            result.put("created", created);
            result.put("updated", updated);
            result.put("total", services.size());
            responseData.setMessage("Đông bộ thành công: ");
        } catch (BaseException be) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, be.getMessage());
            responseData.setMessage("Xảy ra lỗi đồng bộ dịch vụ");
        } catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
            responseData.setMessage("Xảy ra lỗi đồng bộ dịch vụ");
        }
        return responseData;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseData<?> getProviderServices(Long providerId) {
        ResponseData<List<ServiceResponse>> responseData = new ResponseData<>();
        try {
            ProviderEntity provider = providerRepository
                    .findById(providerId)
                    .orElseThrow(() ->
                            new BaseException(
                                    1001,
                                    "Provider không tồn tại"));

            List<ServiceResponse> data =
                    serviceRepository.findByProviderId(providerId)
                            .stream()
                            .map(this::toResponse)
                            .toList();
            responseData.setData(data);

        }catch (BaseException e) {
            LOGGER.error("Lỗi----{}-----{}", "Lấy dịch vụ của nhà cung cấp", e.getMessage());
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", "Lấy dịch vụ của nhà cung cấp", e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Lấy dịch vụ thất bai");
        }


        return responseData;
    }

    @Override
    public ResponseData<?> getProviderStatus(Long orderId) {
        String logPrefix = "Lấy trạng thái đơn hàng từ provider";
        ResponseData<ProviderStatusResponse> responseData = new ResponseData<>();
        try {
            OrderEntity order =
                    orderRepository.findById(orderId)
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Order không tồn tại"));

            ProviderEntity provider =
                    order.getMapping()
                            .getProviderService()
                            .getProvider();

            ProviderStatusResponse response =
                    providerClient.getStatus(
                            provider.getApiUrl(),
                            provider.getApiKey(),
                            order.getProviderOrderId());
            responseData.setData(response);
        } catch (BaseException be) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, be.getMessage());
            responseData.setCode(be.getCode());
            responseData.setMessage(be.getMessage());
        } catch (Exception e) {
            LOGGER.error("Lỗi----{}-----{}", logPrefix, e.getMessage());
        }
        return responseData;
    }

    private ServiceEntity buildService(
            ProviderEntity provider,
            ProviderServiceResponse item) {

        return ServiceEntity.builder()
                .provider(provider)
                .providerServiceId(item.getService())
                .name(item.getName())
                .category(item.getCategory())
                .type(item.getType())
                .rate(item.getRate())
                .minQuantity(item.getMin())
                .maxQuantity(item.getMax())
                .refill(item.getRefill())
                .cancelable(item.getCancel())
                .active(true)
                .build();
    }


    private void updateService(
            ServiceEntity entity,
            ProviderServiceResponse item) {

        entity.setName(item.getName());
        entity.setCategory(item.getCategory());
        entity.setType(item.getType());
        entity.setRate(item.getRate());
        entity.setMinQuantity(item.getMin());
        entity.setMaxQuantity(item.getMax());
        entity.setRefill(item.getRefill());
        entity.setCancelable(item.getCancel());

        serviceRepository.save(entity);
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

    private ServiceResponse toResponse(
            ServiceEntity entity) {

        return ServiceResponse.builder()
                .id(entity.getId())
                .providerServiceId(
                        entity.getProviderServiceId())
                .name(entity.getName())
                .category(entity.getCategory())
                .type(entity.getType())
                .rate(entity.getRate())
                .minQuantity(entity.getMinQuantity())
                .maxQuantity(entity.getMaxQuantity())
                .refill(entity.getRefill())
                .cancelable(entity.getCancelable())
                .active(entity.getActive())
                .build();
    }
}
