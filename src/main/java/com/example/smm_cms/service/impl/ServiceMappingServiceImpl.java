package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.panel_service.CreateServiceMappingRequest;
import com.example.smm_cms.dto.response.service.ServiceMappingResponse;
import com.example.smm_cms.entity.PanelServiceEntity;
import com.example.smm_cms.entity.ProviderEntity;
import com.example.smm_cms.entity.ServiceEntity;
import com.example.smm_cms.entity.ServiceMappingEntity;
import com.example.smm_cms.repository.PanelRepository;
import com.example.smm_cms.repository.ServiceMappingRepository;
import com.example.smm_cms.repository.ServiceRepository;
import com.example.smm_cms.service.IServiceMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceMappingServiceImpl extends BaseService implements IServiceMappingService {
    private final ServiceMappingRepository serviceMappingRepository;
    private final PanelRepository panelRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public ResponseData<?> createMapping(CreateServiceMappingRequest request) {
        String logPrefix = "[ServiceMappingServiceImpl][createMapping] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            PanelServiceEntity panelService =
                    panelRepository.findById(
                                    request.getPanelServiceId())
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Panel service không tồn tại"));

            ServiceEntity providerService =
                    serviceRepository.findById(
                                    request.getProviderServiceId())
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Provider service không tồn tại"));

            ServiceMappingEntity entity =
                    new ServiceMappingEntity();

            entity.setPanelService(panelService);
            entity.setProviderService(providerService);
            entity.setPriority(request.getPriority());
            serviceMappingRepository.save(entity);
            responseData.setMessage("Tạo mapping thành công");
        }catch (BaseException e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage("Tạo mapping thất bại");
        }
       return responseData;
    }

    @Override
    public ResponseData<?> updatePriority(Long mappingId, Integer priority) {
        String logPrefix = "[ServiceMappingServiceImpl][updatePriority] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            ServiceMappingEntity entity =
                    serviceMappingRepository.findById(mappingId)
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Mapping không tồn tại"));
            entity.setPriority(priority);
            serviceMappingRepository.save(entity);
            responseData.setMessage("Cập nhật priority thành công");
        }catch (BaseException e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage("Cập nhật priority thất bại");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> deleteMapping(Long mappingId) {
            String logPrefix = "[ServiceMappingServiceImpl][deleteMapping] - ";
            ResponseData<String> responseData = new ResponseData<>();
            try {
                ServiceMappingEntity entity =
                        serviceMappingRepository.findById(mappingId)
                                .orElseThrow(() ->
                                        new BaseException(
                                                400,
                                                "Mapping không tồn tại"));
                serviceMappingRepository.delete(entity);
                responseData.setMessage("Xóa mapping thành công");

            }catch (BaseException e) {
                LOGGER.error(logPrefix + e.getMessage());
                responseData.setMessage(e.getMessage());
            }
            catch (Exception e) {
                LOGGER.error(logPrefix + e.getMessage());
                responseData.setMessage("Xóa mapping thất bại");
            }
        return responseData;
    }

    @Override
    public ResponseData<?> getMappings(Long panelServiceId) {
        String logPrefix = "[ServiceMappingServiceImpl][getMappings] - ";
        ResponseData<List<ServiceMappingResponse>> responseData = new ResponseData<>();

        List<ServiceMappingResponse> data =
                serviceMappingRepository
                        .findByPanelServiceIdOrderByPriorityAsc(
                                panelServiceId)
                        .stream()
                        .map(this::toResponse)
                        .toList();
        responseData.setData(data);
        return responseData;
    }

    @Override
    public ResponseData<?> getBestProvider(Long panelServiceId) {
        String logPrefix = "[ServiceMappingServiceImpl][getBestProvider] - ";
        ResponseData<ServiceMappingEntity> responseData = new ResponseData<>();
        try {
            ServiceMappingEntity mapping =
                    serviceMappingRepository
                            .findFirstByPanelServiceIdOrderByPriorityAsc(
                                    panelServiceId)
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Không tìm thấy provider"));
            responseData.setData(mapping);
        }catch (BaseException e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage(e.getMessage());
        }
        catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage("Lấy provider thất bại");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> getLowestCost(Long panelServiceId) {
            String logPrefix = "[ServiceMappingServiceImpl][getLowestCost] - ";
            ResponseData<List<ServiceMappingEntity>> responseData = new ResponseData<>();
            try {
                List<ServiceMappingEntity> mappings =
                        serviceMappingRepository
                                .findByPanelServiceIdOrderByPriorityAsc(
                                        panelServiceId);

                if (mappings.isEmpty()) {

                    throw new BaseException(
                            400,
                            "Không có mapping");
                }

                BigDecimal lowestCost =
                        mappings.stream()
                                .map(m ->
                                        m.getProviderService()
                                                .getRate())
                                .min(BigDecimal::compareTo)
                                .orElse(BigDecimal.ZERO);

                Map<String, Object> result =
                        new HashMap<>();

                result.put(
                        "panelServiceId",
                        panelServiceId);

                result.put(
                        "lowestCost",
                        lowestCost);
                responseData.setData(mappings.stream()
                        .filter(m ->
                                m.getProviderService()
                                        .getRate()
                                        .compareTo(lowestCost) == 0)
                        .toList());

            }catch (BaseException e) {
                LOGGER.error(logPrefix + e.getMessage());
                responseData.setMessage(e.getMessage());
            }
            catch (Exception e) {
                LOGGER.error(logPrefix + e.getMessage());
                responseData.setMessage("Lấy provider thất bại");
            }
        return responseData;
    }

    private ServiceMappingResponse toResponse(
            ServiceMappingEntity entity) {

        ServiceEntity service =
                entity.getProviderService();

        ProviderEntity provider =
                service.getProvider();

        return ServiceMappingResponse.builder()
                .mappingId(entity.getId())
                .priority(entity.getPriority())
                .providerId(provider.getId())
                .providerName(provider.getName())
                .providerServiceId(service.getId())
                .providerServiceName(service.getName())
                .rate(service.getRate())
                .build();
    }
}
