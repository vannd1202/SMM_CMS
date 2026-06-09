package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.panel_service.CreateServiceMappingRequest;

public interface IServiceMappingService {
    ResponseData<?> createMapping(
            CreateServiceMappingRequest request);

    ResponseData<?> updatePriority(
            Long mappingId,
            Integer priority);

    ResponseData<?> deleteMapping(
            Long mappingId);

    ResponseData<?> getMappings(
            Long panelServiceId);

    ResponseData<?> getBestProvider(
            Long panelServiceId);

    ResponseData<?> getLowestCost(
            Long panelServiceId);
}
