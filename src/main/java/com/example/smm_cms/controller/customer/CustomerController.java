package com.example.smm_cms.controller.customer;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.user.CustomerRequest;
import com.example.smm_cms.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.PRIVATE)
@RequiredArgsConstructor

public class CustomerController {
    private final IUserService userService;
    @RequestMapping("/customer/list")
    public ResponseData<?> list(@Valid @ParameterObject CustomerRequest request) {
        return userService.list(request);
    }


}
