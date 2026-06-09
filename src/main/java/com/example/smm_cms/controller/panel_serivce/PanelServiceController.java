package com.example.smm_cms.controller.panel_serivce;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.panel_service.CreatePanelServiceRequest;
import com.example.smm_cms.dto.request.panel_service.SearchPanelServiceRequest;
import com.example.smm_cms.dto.request.panel_service.UpdatePanelServiceRequest;
import com.example.smm_cms.service.IPanelServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.PUBLIC + "/panel-service")
@RequiredArgsConstructor
public class PanelServiceController {
    private final IPanelServiceService panelServiceService;

    @PostMapping
    public ResponseData<?> create(
            @RequestBody @Valid CreatePanelServiceRequest request) {

        return panelServiceService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseData<?> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdatePanelServiceRequest request) {

        return panelServiceService.update(id, request);
    }

    @GetMapping("/{id}")
    public ResponseData<?> getById(
            @PathVariable Long id) {

        return panelServiceService.getById(id);
    }

    @GetMapping("/search")
    public ResponseData<?> search(
            @ParameterObject SearchPanelServiceRequest request) {

        return panelServiceService.search(request);
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> delete(
            @PathVariable Long id) {

        return panelServiceService.delete(id);
    }

    @PostMapping("/active/{id}")
    public ResponseData<?> active(
            @PathVariable Long id) {

        return panelServiceService.active(id);
    }

    @PostMapping("/inactive/{id}")
    public ResponseData<?> inactive(
            @PathVariable Long id) {

        return panelServiceService.inactive(id);
    }

}
