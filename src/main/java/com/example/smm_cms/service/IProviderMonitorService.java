package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;

public interface IProviderMonitorService {
    ResponseData<?> statistic();

    ResponseData<?> healthCheck(Long providerId);
}
