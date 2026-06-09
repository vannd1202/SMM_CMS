package com.example.smm_cms.sync;

import com.example.smm_cms.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSyncJob {
    private final IOrderService orderService;

//    @Scheduled(
//            fixedDelay = 600)
    public void syncOrders() {


        orderService.syncPendingOrders();
    }
}
