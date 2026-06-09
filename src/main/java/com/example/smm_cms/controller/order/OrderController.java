package com.example.smm_cms.controller.order;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.order.CreateOrderRequest;
import com.example.smm_cms.dto.request.order.SearchOrderRequest;
import com.example.smm_cms.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.PUBLIC + "/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @Operation(summary = "Tạo đơn hàng")
    @PostMapping
    public ResponseData<?> create(
            @RequestBody
            @Valid
            CreateOrderRequest request) {

        return orderService.create(request);
    }

    @Operation(summary = "Chi tiết đơn hàng")
    @GetMapping("/{id}")
    public ResponseData<?> getById(
            @PathVariable Long id) {

        return orderService.getById(id);
    }

    @Operation(summary = "Danh sách đơn hàng")
    @GetMapping("/search")
    public ResponseData<?> search(
            @ParameterObject
            @Valid
            SearchOrderRequest request) {

        return orderService.search(request);
    }

    @Operation(summary = "Sync trạng thái đơn")
    @PostMapping("/{id}/sync")
    public ResponseData<?> syncOrder(
            @PathVariable Long id) {
        return orderService.syncOrder(id);
    }
}
