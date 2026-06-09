package com.example.smm_cms.controller.panel_serivce;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.panel_service.CreateServiceMappingRequest;
import com.example.smm_cms.service.IServiceMappingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.PUBLIC + "/service-mapping")
@RequiredArgsConstructor
public class ServiceMappingController {

    private final IServiceMappingService serviceMappingService;


    @PostMapping
    public ResponseData<?> create(
            @RequestBody
            @Valid
            CreateServiceMappingRequest request) {

        return serviceMappingService.createMapping(request);
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(
            @PathVariable Long id) {

        return serviceMappingService.deleteMapping(id);
    }

    @PutMapping("/{id}/priority")
    public ResponseData<?> updatePriority(
            @PathVariable Long id,
            @RequestParam Integer priority) {

        return serviceMappingService
                .updatePriority(id, priority);
    }

    @GetMapping("/panel-service/{id}")
    public ResponseData<?> getMappings(
            @PathVariable Long id) {

        return serviceMappingService
                .getMappings(id);
    }

    @GetMapping("/panel-service/{id}/best-provider")
    public ResponseData<?> getBestProvider(
            @PathVariable Long id) {

        return serviceMappingService
                .getBestProvider(id);
    }

    @GetMapping("/panel-service/{id}/lowest-cost")
    public ResponseData<?> getLowestCost(
            @PathVariable Long id) {

        return serviceMappingService
                .getLowestCost(id);
    }


}
