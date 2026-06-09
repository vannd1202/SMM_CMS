package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.base.ResponsePage;
import com.example.smm_cms.dto.request.panel_service.CreatePanelServiceRequest;
import com.example.smm_cms.dto.request.panel_service.SearchPanelServiceRequest;
import com.example.smm_cms.dto.request.panel_service.UpdatePanelServiceRequest;
import com.example.smm_cms.dto.response.panel_services.PanelServiceResponse;
import com.example.smm_cms.entity.PanelServiceEntity;
import com.example.smm_cms.repository.PanelRepository;
import com.example.smm_cms.service.IPanelServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor

public class PanelServiceServiceImpl extends BaseService implements IPanelServiceService {
    private final PanelRepository panelServiceRepository;
    @Override
    @Transactional
    public ResponseData<?> create(CreatePanelServiceRequest request) {
        String logPrefix = "[PanelServiceServiceImpl][create] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            PanelServiceEntity entity = new PanelServiceEntity();
            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setSellPrice(request.getSellPrice());
            entity.setActive(
                    request.getActive() == null
                            ? true
                            : request.getActive());

            panelServiceRepository.save(entity);
            responseData.setMessage("Tạo dịch vụ thành công");


        }catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage("Tạo dịch vụ thất bại");
        }
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<?> update(Long id, UpdatePanelServiceRequest request) {
        String logPrefix = "[PanelServiceServiceImpl][update] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            PanelServiceEntity entity =
                    panelServiceRepository.findById(id)
                            .orElseThrow(() ->
                                    new BaseException(
                                            400,
                                            "Panel service không tồn tại"));

            entity.setName(request.getName());
            entity.setDescription(request.getDescription());
            entity.setSellPrice(request.getSellPrice());
            entity.setActive(request.getActive());
            panelServiceRepository.save(entity);
            responseData.setMessage("Cật nhật thành công");
        } catch (Exception e) {
            LOGGER.error("Cật nhật thất bại: " + e.getMessage());
            responseData.setMessage("Cật nhật thất bại");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> getById(Long id) {
            String logPrefix = "[PanelServiceServiceImpl][getById] - ";
            ResponseData<PanelServiceResponse> responseData = new ResponseData<>();
            try {
                PanelServiceEntity entity =
                        panelServiceRepository.findById(id)
                                .orElseThrow(() ->
                                        new BaseException(
                                                400,
                                                "Panel service không tồn tại"));
                responseData.setData(toResponse(entity));
            } catch (Exception e) {
                LOGGER.error(logPrefix + e.getMessage());
                responseData.setMessage("Lấy thông tin thất bại");
            }
            return responseData;
    }

    @Override
    public ResponseData<?> search(SearchPanelServiceRequest request) {
        String logPrefix = "[PanelServiceServiceImpl][search] - ";
        ResponseData<ResponsePage<PanelServiceResponse>> responseData = new ResponseData<>();
        Page<PanelServiceEntity> page;

        if (request.getName() == null
                || request.getName().isBlank()) {

            page = panelServiceRepository.findAll(
                    request.pageable());

        } else {

            page = panelServiceRepository
                    .findByNameContainingIgnoreCase(
                            request.getName(),
                            request.pageable());
        }
        List<PanelServiceResponse> data =
                page.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();
        responseData.setData(new ResponsePage<>(page, data));
        return responseData;
    }

    @Override
    @Transactional
    public ResponseData<?> delete(Long id) {
        String logPrefix = "[PanelServiceServiceImpl][delete] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            PanelServiceEntity entity =
                    panelServiceRepository.findById(id)
                            .orElseThrow(() ->
                                    new BaseException(
                                            404,
                                            "Panel service không tồn tại"));
            entity.setActive(false);
            panelServiceRepository.save(entity);
            responseData.setMessage("Xóa thành công");
        }catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage("Xóa thất bại");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> active(Long id) {
        String logPrefix = "[PanelServiceServiceImpl][active] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            PanelServiceEntity entity =
                    panelServiceRepository.findById(id)
                            .orElseThrow(() ->
                                    new BaseException(
                                            404,
                                            "Panel service không tồn tại"));
            entity.setActive(true);
            panelServiceRepository.save(entity);
            responseData.setMessage("Kích hoạt thành công");
        } catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage("Kích hoạt thất bại");
        }
        return responseData;
    }

    @Override
    public ResponseData<?> inactive(Long id) {
        String logPrefix = "[PanelServiceServiceImpl][inactive] - ";
        ResponseData<String> responseData = new ResponseData<>();
        try {
            PanelServiceEntity entity =
                    panelServiceRepository.findById(id)
                            .orElseThrow(() ->
                                    new BaseException(
                                            404,
                                            "Panel service không tồn tại"));
            entity.setActive(false);
            panelServiceRepository.save(entity);
            responseData.setMessage("Vô hiệu hóa thành công");
        } catch (Exception e) {
            LOGGER.error(logPrefix + e.getMessage());
            responseData.setMessage("Vô hiệu hóa thất bại");
        }
        return responseData;
    }

    private PanelServiceResponse toResponse(
            PanelServiceEntity entity) {

        return PanelServiceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sellPrice(entity.getSellPrice())
                .active(entity.getActive())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .build();
    }
}
