package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.report.RevenueReportRequest;

public interface IReportService {
    ResponseData<?> revenueReport(
            RevenueReportRequest request);

    ResponseData<?> serviceReport(
            RevenueReportRequest request);
}
