package com.example.smm_cms.controller.admin.report;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.report.RevenueReportRequest;
import com.example.smm_cms.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.PUBLIC + "/report")
@RequiredArgsConstructor
public class ReportController {
    private final IReportService reportService;

    @PostMapping("/revenue")
    public ResponseData<?> revenue(
            @RequestBody RevenueReportRequest request) {

        return reportService.revenueReport(
                request);
    }

    @PostMapping("/services")
    public ResponseData<?> services(
            @RequestBody RevenueReportRequest request) {

        return reportService.serviceReport(
                request);
    }
}
