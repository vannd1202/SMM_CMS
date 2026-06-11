package com.example.smm_cms.controller.admin.customer;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.user.SearchUserRequest;
import com.example.smm_cms.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.PRIVATE + "/customer")
public class CustomerController {
    private final IUserService userService;

    @GetMapping("/search")
    public ResponseData<?> search(
            @Valid
            @ParameterObject
            SearchUserRequest request) {

        return userService.search(request);
    }

    @GetMapping("/{id}")
    public ResponseData<?> getById(
            @PathVariable Long id) {

        return userService.getById(id);
    }

    @GetMapping("/{id}/lock")
    public ResponseData<?> lock(
            @PathVariable Long id) {
        return userService.lock(id);
    }

    @GetMapping("/{id}/unlock")
    public ResponseData<?> unlock(
            @PathVariable Long id) {
        return userService.unlock(id);
    }
}
