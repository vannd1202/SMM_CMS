package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.user.CustomerRequest;
import com.example.smm_cms.dto.request.user.LoginRequest;
import com.example.smm_cms.dto.request.user.RegisterRequest;

public interface IUserService {
    ResponseData<?> register(RegisterRequest request);
    ResponseData<?> login(LoginRequest request);
    ResponseData<?> list(CustomerRequest request);
}
