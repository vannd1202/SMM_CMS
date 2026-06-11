package com.example.smm_cms.controller.admin.provider;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.service.IProviderMonitorService;
import com.example.smm_cms.service.IProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.PUBLIC+"/provider-monitor")
@RequiredArgsConstructor
public class ProviderMonitorController {
    private final IProviderMonitorService providerMonitorService;
    private final IProviderService providerService;
    @GetMapping("/statistic")
    public ResponseData<?> statistic() {
        return providerMonitorService.statistic();
    }

    @PostMapping("/{id}/health-check")
    public ResponseData<?> healthCheck(@PathVariable Long id) {
        return providerMonitorService.healthCheck(id);
    }

    @GetMapping("/balance/{id}")
    public ResponseData<?> getBalance(@PathVariable Long id) {
        return providerService.getBalance(id);
    }
}
