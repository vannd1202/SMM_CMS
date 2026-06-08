package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.provider.SearchProviderRequest;
import com.example.smm_cms.dto.response.provider.CreateProviderRequest;
import com.example.smm_cms.dto.response.provider.UpdateProviderRequest;

public interface IProviderService {
    ResponseData<?> create(CreateProviderRequest request);

    ResponseData<?> update(Long id, UpdateProviderRequest request);

    ResponseData<?> getAll(SearchProviderRequest request);

    ResponseData<?> getById(Long id);

    // start các hàm test
     ResponseData<?> testConnection(Long id);

     ResponseData<?> getServices(Long id);

    ResponseData<?> getBalance(Long id);

    //end các hàm test
}
