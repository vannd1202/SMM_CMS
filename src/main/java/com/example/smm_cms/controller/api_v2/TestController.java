package com.example.smm_cms.controller.api_v2;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.service.IProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.PUBLIC)
@RequiredArgsConstructor
public class TestController {
    private final IProviderService providerService;

    @PostMapping("/test/{id}")
    public ResponseData<?> test(@PathVariable Long id) {
        return providerService.testConnection(id);
    }

}
