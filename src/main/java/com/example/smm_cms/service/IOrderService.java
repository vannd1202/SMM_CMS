package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.order.CreateOrderRequest;
import com.example.smm_cms.dto.request.order.SearchOrderRequest;

public interface IOrderService {
    ResponseData<?> create(
            CreateOrderRequest request);

    ResponseData<?> getById(
            Long id);

    ResponseData<?> search(
            SearchOrderRequest request);

    ResponseData<?> syncOrder(Long orderId);

    void syncPendingOrders();

}
