package com.example.smm_cms.service;

import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.dto.request.order.CreateOrderRequest;
import com.example.smm_cms.dto.request.order.SearchOrderRequest;
import com.example.smm_cms.dto.request.order.admin.SearchOrderAdminRequest;

public interface IOrderService {
    ResponseData<?> create(CreateOrderRequest request);
    ResponseData<?> getById(Long id);
    ResponseData<?> search(SearchOrderRequest request);
    ResponseData<?> adminSearch(SearchOrderAdminRequest request);
    ResponseData<?> getDetail(Long orderId);
    ResponseData<?> syncOrder(Long orderId);
    ResponseData<?> getHistory(Long orderId);
    ResponseData<?> refund(Long orderId, String reason);
    ResponseData<?> statistic();


    void syncPendingOrders();

}
