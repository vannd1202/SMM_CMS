package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.panel_service.CreatePanelServiceRequest;
import com.example.smm_cms.dto.request.panel_service.SearchPanelServiceRequest;
import com.example.smm_cms.dto.request.panel_service.UpdatePanelServiceRequest;

public interface IPanelServiceService {
    ResponseData<?> create(CreatePanelServiceRequest request);

    ResponseData<?> update(
            Long id,
            UpdatePanelServiceRequest request);

    ResponseData<?> getById(Long id);

    ResponseData<?> search(SearchPanelServiceRequest request);

    ResponseData<?> delete(Long id);

    ResponseData<?> active(Long id);

    ResponseData<?> inactive(Long id);
}
