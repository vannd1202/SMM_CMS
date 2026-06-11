package com.example.smm_cms.controller.admin.order;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.common.ApiPath;
import com.example.smm_cms.dto.request.order.admin.SearchOrderAdminRequest;
import com.example.smm_cms.service.IOrderService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.PUBLIC+"/admin-order")
@RequiredArgsConstructor
public class OrderAdminController {
    private final IOrderService orderService;

    @GetMapping("/search")
    public ResponseData<?> search(
            @ParameterObject
            SearchOrderAdminRequest request){
            return orderService.adminSearch(request);
    }

    @GetMapping("/{id}")
    public ResponseData<?> detail(
            @PathVariable Long id){
        return orderService.getDetail(id);
    }

    @GetMapping("/{id}/history")
    public ResponseData<?> history(
            @PathVariable Long id){
        return orderService.getHistory(id);
    }

    @PostMapping("/{id}/sync")
    public ResponseData<?> sync(
            @PathVariable Long id){
        return orderService.syncOrder(id);
    }

    @PostMapping("/{id}/refund")
    public ResponseData<?> refund(
            @PathVariable Long id,
            @Parameter String reason){
        return orderService.refund(id, reason);
    }

    @GetMapping("/statistic")
    public ResponseData<?> statistic(){
        return orderService.statistic();
    }
}
