package com.example.smm_cms.controller.provider;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.provider.SearchProviderRequest;
import com.example.smm_cms.dto.response.provider.CreateProviderRequest;
import com.example.smm_cms.dto.response.provider.UpdateProviderRequest;
import com.example.smm_cms.service.IProviderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.PUBLIC+"/provider")
@RequiredArgsConstructor
public class ProviderController {
    private final IProviderService providerService;

    @Operation(summary = "Tạo mới nhà cung câp")
    @PostMapping("/create")
    public ResponseData<?> create(
             @Valid @RequestBody CreateProviderRequest request) {
        return providerService.create(request);
    }

    @Operation(summary = "Cập nhật nhà cung câp")
    @PutMapping("/{id}")
    public ResponseData<?> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProviderRequest request) {
        return providerService.update(id, request);
    }

    @Operation(summary = "Chi tiết nhà cung câp")
    @GetMapping("/{id}")
    public ResponseData<?> getById(
            @PathVariable Long id) {
        return providerService.getById(id);
    }

    @GetMapping("/search")
    public ResponseData<?> getAll(@Valid @ParameterObject SearchProviderRequest request) {
        return providerService.getAll(request);
    }

}
