package com.example.smm_cms.controller;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.user.LoginRequest;
import com.example.smm_cms.dto.request.user.RegisterRequest;
import com.example.smm_cms.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.AUTH)
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService ;

    @PostMapping("/register")
    public ResponseData<?> register(
            @RequestBody RegisterRequest request) {

        return userService.register(request);
    }

    @PostMapping("/login")
    public ResponseData<?> login(
            @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}